package com.unsc.nettys.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Netty Echo Server var John117
 * @author DELL
 * @date 2018/12/27
 */
@Component
public class EchoServer {
/*
    @Value("${netty.server.port}")
    public Integer port;*/

    public void init() throws InterruptedException {
        //1. 创建EventLoopGroup
        //2. 创建ServerBootStrap
        //3. 指定Nio传输的Channel
        //4. 使用指定端口设置Socket地址
        //5. 添加一个EchoServerHandler实例到子Channel的ChannelPipeline
        //6. 异步绑定服务器 调用sync() 方法阻塞 直到绑定完毕
        //7. 获取到Channel的CloseFuture 并阻塞当前线程 直到其完毕
        //8. 关闭EventLoopGroup 释放资源
        EchoServerHandler echoServerHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group).channel(NioServerSocketChannel.class).localAddress(8877).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) {
                    channel.pipeline().addLast(echoServerHandler);
                }
            });
            ChannelFuture future = serverBootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            //释放资源 优雅关闭
            group.shutdownGracefully().sync();
        }
    }
}
