package com.lmstudio.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeServer {

    public void bind(int port) throws InterruptedException {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChildChannelHandler());

            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    private  class  ChildChannelHandler extends ChannelInitializer<SocketChannel>{

        protected void initChannel(SocketChannel socketChannel) throws Exception {

            //解决TCP粘包和拆包
            //LineBasedFrameDecoder 的工作原理是它依次遍历ByteBuf中的可读字节，判断看是否有“\n”或者“\r\n”，如果有，就以此位置为结束位置，从可读索引到结束位置区间的字节就组成了一行。
            //它是以换行符作结束标志的解码器，支持携带结束符或不携带结束符两种解码方式，同时支持配置单行的最大长度。
            //如果连续读取到最大长度后任然没有发现换行符，就会抛出异常，同时忽略掉之前读到的异常码流。
            //DelimiterBasedFrameDecoder 用于对使用分隔符结尾的消息进行自动解码；FixedLengthFrameDecoder 用于对固定长度的消息进行自动解码
            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
            //StringDecoder的功能非常简单，就是将接收到的对象转换成字符串，然后继续调用后面的Handler。
            //LineBasedFrameDecoder和StringDecoder的组合就是按行切换的文本解码器，它被设计用来支持TCP的粘包和拆包。
            socketChannel.pipeline().addLast(new StringDecoder());

            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        new TimeServer().bind(port);
    }
}
