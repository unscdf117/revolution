package com.unsc.nettys.client;

import com.unsc.nettys.BaseTestCase;
import com.unsc.nettys.server.EchoServer;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by DELL on 2018/12/27.
 */
public class EchoClientTest {

    @Test
    public void test() throws InterruptedException {
        new EchoServer().init();
    }

    @Test
    public void test1() throws InterruptedException {
        EchoClient client = new EchoClient(8877, "127.0.0.1");
        client.build();
    }
}