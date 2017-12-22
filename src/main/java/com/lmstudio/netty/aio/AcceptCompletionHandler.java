package com.lmstudio.netty.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * A handler for consuming the result of an asynchronous I/O operation.
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {

    /**
     * @param result The result of the I/O operation.
     * @param attachment  The object attached to the I/O operation when it was initiated.
     */
    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {

        /**
         * 调用AsynchronousServerSocketChannel的accept方法后，如果有新的客户端接入，系统将回调我们传入的ComletionHandler实例的completed方法，
         * 表示新的客户端已经接入成功。因为一个AsynchronousServerSocketChannel可以接收成千上万个客户端，所以需要继续调用它的accept方法，接收其他的客户端连接，形成一个循环。
         * 每当接收一个客户端连接成功之后，再异步接收新的客户端连接。
         */
        attachment.asynchronousServerSocketChannel.accept(attachment,this);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //Reads a sequence of bytes from this channel into the given buffer.
        result.read(buffer,buffer,new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {

        exc.printStackTrace();
        attachment.latch.countDown();
    }
}
