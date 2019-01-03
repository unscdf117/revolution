package com.unsc.nettys.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;

/**
 * xxHandler 需要针对不同类型的事件来调用ChannelHandler
 * 需要通过自己实现/扩展ChannelHandler来注册到事件lifecycle当中
 * 需要自己去实现具体业务逻辑来响应需求
 * 这样可以做到框架和业务的剥离 更加灵活
 * @author DELL
 */
@ChannelHandler.Sharable
@Component
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**channelRead 每次消息传入时触发*/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));
        ctx.write(msg);
    }
    /**通知ChannelInboundHandler 调用channelRead是当前批量读取中的最后一条消息*/
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }
    /**ex*/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}