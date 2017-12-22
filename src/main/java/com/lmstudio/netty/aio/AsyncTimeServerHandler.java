package com.lmstudio.netty.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeServerHandler implements Runnable
{
    private int port;
    CountDownLatch latch;
    AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public AsyncTimeServerHandler(int port) {
        this.port = port;
        try {
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("The time server is start in port:"+port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //new Thread().join() Waits for this thread to die.
        //CountDownLatch 允许一个或多个线程等待其它线程完成操作
        latch = new CountDownLatch(1);
        doAccept();
        try {
            latch.await();//让线程在此阻塞，防止服务器执行完成退出;阻塞当前线程，直到n变成0
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void doAccept(){
        /**
         * Accepts a connection.
         * This method initiates an asynchronous operation to accept a
         * connection made to this channel's socket. The {@code handler} parameter is
         * a completion handler that is invoked when a connection is accepted (or
         * the operation fails). The result passed to the completion handler is
         * the {@link AsynchronousSocketChannel} to the new connection.
         */
        asynchronousServerSocketChannel.accept(this,new AcceptCompletionHandler());

    }
}
