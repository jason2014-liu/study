package com.lmstudio.netty.codec.protobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SubReqClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //super.channelActive(ctx);
        for (int i = 0; i < 10; i++) {
            ctx.write(subReq(i));
        }

        ctx.flush();
    }

    private SubscribeReqProto.SubscribeReq subReq(int i){
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(i);
        builder.setUserName("jason");
        builder.setProductName("Netty Book");
        List<String> address = new ArrayList<String>();
        address.add("huawei");
        address.add("alibaba");
        address.add("jingdong");
        builder.addAllAddress(address);
        return builder.build();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       // super.channelRead(ctx, msg);
        System.out.println("Receive server response:["+msg+"]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
       // super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       // super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }
}
