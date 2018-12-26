package com.unsc.shard;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@MapperScan("com.unsc.shard.mapper")
@EnableAspectJAutoProxy
@EnableRabbit
@ImportResource(value = {"classpath:dubbo/dubbo.xml"})
public class Sharding {

	public static void main(String[] args) {
		SpringApplication.run(Sharding.class, args);
	}

}
