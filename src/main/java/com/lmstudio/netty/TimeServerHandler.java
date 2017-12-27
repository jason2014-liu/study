package com.lmstudio.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;


public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        //String body = new String(req, "UTF-8");
//        String body = new String(req, "UTF-8").substring(0,req.length-System.getProperty("line.separator").length());
//        //System.out.println("The time server receive order:"+body);
        String body = (String)msg;
        System.out.println("The time server receive order:"+body+"; the count is :"+ ++counter);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)? new Date().toString():"BAD ORDER";
        currentTime = currentTime + System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(resp);
        //super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}

