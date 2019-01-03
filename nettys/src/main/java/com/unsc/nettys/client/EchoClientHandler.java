package com.unsc.nettys.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;

import java.nio.charset.Charset;

/**
 * Echo Client var 117
 * @author DELL
 * @date 2018/12/27
 */
@ChannelHandler.Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**read 每个传入的消息都会调用该方法*/
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        System.out.println("EchoClient Received: {}" + byteBuf.toString(Charset.forName("UTF-8")));
    }
    /**见名知义 channel启动时候必然触发*/
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        String string = "ofo";
        ctx.writeAndFlush(Unpooled.copiedBuffer(string.getBytes()));
    }
    /**ex*/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
