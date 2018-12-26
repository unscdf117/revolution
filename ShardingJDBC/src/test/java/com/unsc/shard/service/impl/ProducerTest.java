package com.unsc.shard.service.impl;

import com.unsc.shard.BaseTestCase;
import com.unsc.shard.common.amqplistener.Producer;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by DELL on 2018/12/24.
 */
public class ProducerTest extends BaseTestCase {

    @Resource
    private Producer producer;

    /*@Test
    public void testMsg() {
        producer.send();
    }*/
}