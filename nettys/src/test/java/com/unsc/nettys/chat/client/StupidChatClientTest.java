package com.unsc.nettys.chat.client;

import com.unsc.nettys.chat.server.StupidChatServer;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by DELL on 2019/1/3.
 */
public class StupidChatClientTest {

    @Test
    public void runServer() throws InterruptedException {
        StupidChatServer server = new StupidChatServer();
        server.start();
    }

    @Test
    public void runClient() throws Exception {
        StupidChatClient client = new StupidChatClient(7788, "127.0.0.1");
        client.init();
    }
}