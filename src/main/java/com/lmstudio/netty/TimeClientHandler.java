package com.lmstudio.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

   // private final ByteBuf firstMessage;

    private int count;
    private byte[] req;

    public TimeClientHandler() {
//        byte[] req = "QUERY TIME ORDER".getBytes();
//        firstMessage = Unpooled.buffer(req.length);
//        firstMessage.writeBytes(req);
        req = ("QUERY TIME ORDER"+System.getProperty("line.separator")).getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       // super.channelActive(ctx);
       // ctx.writeAndFlush(firstMessage);
        //TCP 粘包测试
        ByteBuf message = null;
        for (int i = 0; i < 100; i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       // super.channelRead(ctx, msg);
//        ByteBuf buf = (ByteBuf)msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        String body = new String(req,"UTF-8");
//        //System.out.println("Now is :"+body);
//        System.out.println("Now is:"+body+";the count is :"+ ++count);
        String body = (String)msg;
        System.out.println("Now is:"+body+";the count is :"+ ++count);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
