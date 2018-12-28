package com.unsc.shard.config;

import com.unsc.shard.BaseTestCase;
import org.junit.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by DELL on 2018/12/28.
 */
public class RedisConfigTest extends BaseTestCase {

    @Resource
    private RedissonClient client;

    @Test
    public void test() {
        //1. 从redis里面获取key unsc
        RBucket<Object> bucket = client.getBucket("unsc");
        if (null == bucket.get()) {
            //为空 设置value
            bucket.set("117");
        }else {
            bucket.getAndSet("052");
        }
        Object res = bucket.get();
        System.out.println(res);
    }
}