package com.unsc.nettys.chat.server;

import com.unsc.nettys.chat.client.StupidChatClientInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

/**
 * 愚蠢至极的聊天 Server
 * @author DELL
 * @date 2019/1/3
 */
//@Component
public class StupidChatServer {

//    private final StupidChatServerHandler chatServerHandler = new StupidChatServerHandler();
    /**
     * 手动端口号 ; )
     */
    private Integer port = 7788;

    public void start() throws InterruptedException {
        //1. 创建EventLoopGroup
        //2. 创建ServerBootStrap
        //3. 指定Nio传输的Channel
        //4. 使用指定端口设置Socket地址
        //5. 添加一个EchoServerHandler实例到子Channel的ChannelPipeline
        //6. 异步绑定服务器 调用sync() 方法阻塞 直到绑定完毕
        //7. 获取到Channel的CloseFuture 并阻塞当前线程 直到其完毕
        //8. 关闭EventLoopGroup 释放资源
//        StupidChatServerHandler chatServerHandler = new StupidChatServerHandler();
        NioEventLoopGroup inGroup = new NioEventLoopGroup();
        NioEventLoopGroup outGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bs = new ServerBootstrap();

            bs.group(inGroup, outGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new StupidChatServerInit())
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = bs.bind(port).sync();
            f.channel().closeFuture().sync();
        }finally {
            //优雅释放资源 嘿嘿嘿 : )
            inGroup.shutdownGracefully().sync();
            outGroup.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        StupidChatServer server = new StupidChatServer();
//        server.port = 7799;
        server.start();
    }
}
