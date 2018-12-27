package com.unsc.nettys.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * Echo Client var 117
 * @author DELL
 * @date 2018/12/27
 */
//@Component
public class EchoClient {

  /*  @Value("${netty.client.port}")
    private Integer port;

    @Value("${netty.client.host}")
    private String host;*/

     private Integer port;

    private String host;

    private EchoClientHandler handler = new EchoClientHandler();

    private void init() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new EchoClientHandler());
                        }
                    });
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public void build() throws InterruptedException {
        init();
    }

    public EchoClient(Integer port, String host) {
        this.port = port;
        this.host = host;
    }
}
