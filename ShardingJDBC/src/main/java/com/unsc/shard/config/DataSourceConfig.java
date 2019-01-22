package com.unsc.shard.config;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import javax.sql.DataSource;

import io.shardingsphere.api.config.MasterSlaveRuleConfiguration;
import io.shardingsphere.api.config.ShardingRuleConfiguration;
import io.shardingsphere.core.rule.ShardingRule;
import io.shardingsphere.core.yaml.masterslave.YamlMasterSlaveConfiguration;
import io.shardingsphere.core.yaml.masterslave.YamlMasterSlaveRuleConfiguration;
import io.shardingsphere.core.yaml.sharding.YamlShardingConfiguration;
import io.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import io.shardingsphere.shardingjdbc.jdbc.core.datasource.MasterSlaveDataSource;
import io.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Lists;

@Configuration
@Slf4j
public class DataSourceConfig {
	
	@javax.annotation.Resource
	private Filter statFilter;

	private static final String SHARDING_YML_PATH = "sharding/dataSource.yml";

    private static final String MASTER_SLAVE_YML_PATH = "sharding/masterSlave.yml";
	
	/**
	 * 构建dataSource
	 * 这里没有使用ShardingDataSourceFactory 
	 * 因为要为druid数据源配置监听Filter
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
    @Bean
    public DataSource getDataSource() throws IOException, SQLException {
        Map<String, Object> param = parse();
        YamlShardingConfiguration shardConfig = (YamlShardingConfiguration) param.get("simpleSharding");
        YamlMasterSlaveConfiguration msConfig = (YamlMasterSlaveConfiguration) param.get("simpleMasterSlave");
        Map<String, DataSource> shardDataSourceMap = shardConfig.getDataSources();
        Map<String, DataSource> msDataSourceMap = msConfig.getDataSources();
        //加入MasterSlaveDataSource

        ShardingRule rule = shardConfig.getShardingRule(Collections.emptyList());
        shardDataSourceMap.forEach((k,v)->{
            DruidDataSource d = (DruidDataSource) v;
            d.setProxyFilters(Lists.newArrayList(statFilter));
        });
        msDataSourceMap.forEach((k, v) -> {
            DruidDataSource d = (DruidDataSource) v;
            d.setProxyFilters(Lists.newArrayList(statFilter));
        });

        ShardingRuleConfiguration config = rule.getShardingRuleConfig();
        config.setMasterSlaveRuleConfigs(getMasterSlaveRuleConfigurations());

        var dataSourceMap = new HashMap<String, DataSource>(2);
        dataSourceMap.putAll(shardDataSourceMap);
        dataSourceMap.putAll(msDataSourceMap);
        return new ShardingDataSource(dataSourceMap, rule);
    }

    private List<MasterSlaveRuleConfiguration> getMasterSlaveRuleConfigurations() {
        MasterSlaveRuleConfiguration masterSlaveRuleConfig1 =
                new MasterSlaveRuleConfiguration("ds0", "ds0", Arrays.asList("ds1"));
        return Lists.newArrayList(masterSlaveRuleConfig1);
    }

    /**
	 * 解析yml
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	private HashMap<String, Object> parse() throws IOException, FileNotFoundException, UnsupportedEncodingException {
		Resource shardResource = new ClassPathResource(SHARDING_YML_PATH);
		Resource msResource = new ClassPathResource(MASTER_SLAVE_YML_PATH);
	    try (InputStreamReader shardInputStreamReader = new InputStreamReader(shardResource.getInputStream(), "UTF-8")) {
            try (InputStreamReader msInputStreamReader = new InputStreamReader(msResource.getInputStream(), "UTF-8")) {
                YamlShardingConfiguration shardingConfiguration = new Yaml(new Constructor(YamlShardingConfiguration.class)).loadAs(shardInputStreamReader, YamlShardingConfiguration.class);
                YamlMasterSlaveConfiguration masterSlaveConfiguration = new Yaml(new Constructor(YamlMasterSlaveConfiguration.class)).loadAs(msInputStreamReader, YamlMasterSlaveConfiguration.class);
                var param = new HashMap<String, Object>(4);
                param.put("simpleMasterSlave", masterSlaveConfiguration);
                param.put("simpleSharding", shardingConfiguration);
                return param;
            }
	    }
	}
}
