package com.unsc.nettys.chat.client;

import io.netty.channel.*;

import static java.lang.System.out;

/**
 * 愚蠢之极 chatClient extends ChannelInboundHandler
 * @author DELL
 */
@ChannelHandler.Sharable
public class StupidChatClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String mes) throws Exception {
        //皮一把import static
        out.println(mes);
    }
}