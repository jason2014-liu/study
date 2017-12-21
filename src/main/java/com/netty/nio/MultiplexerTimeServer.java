package com.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

    private Selector selector;

    private ServerSocketChannel servChannel;

    private volatile boolean stop;

    public MultiplexerTimeServer(int port) {
        try{
            selector = Selector.open();
            servChannel = ServerSocketChannel.open();
            servChannel.configureBlocking(false);
            servChannel.socket().bind(new InetSocketAddress(port),1024);
            servChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is start in port:"+port);

        }catch (IOException e){
            e.printStackTrace();
            System.exit(1);//Terminates the currently running Java Virtual Machine
        }
    }

    public void stop(){
        this.stop = true;
    }

    @Override
    public void run() {

        while (!stop){
            try{
                selector.select(1000);//每隔1s被唤醒一次
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()){
                    key = it.next();
                    it.remove();
                    try{
                        handleInput(key);
                    }catch (Exception e){
                        e.printStackTrace();
                        if(key != null){
                            key.cancel();
                            if(key.channel() != null){
                                key.channel().close();
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        //多路复用器关闭后，所有注册在上面的channel和Pipe资源都会被自动去注销并关闭，所以不需要重复释放资源
        if(selector != null){
            try{
                selector.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException{
        if(key.isValid()){


            if(key.isAcceptable()){
                //完成如下操作，相当于完成了TCP的三次握手，TCP物理链路正式建立
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                //Accepts a connection made to this channel's socket.
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);//同时也可以对其它TCP参数进行设置，例如TCP接收和发送缓冲区的大小等
                sc.register(selector, SelectionKey.OP_READ);
            }

            if(key.isReadable()){
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                //the number of bytes read, possibly zero, or <tt>-1</tt> if the hannel has reached end-of-stream
                int readBytes = sc.read(readBuffer);
                if(readBytes > 0){
                    readBuffer.flip();//设置limit = position; position = 0;用于后续对缓冲区的读取操作
                    byte[] bytes = new byte[readBuffer.remaining()];//Returns the number of elements between the current position and the limit.
                    readBuffer.get(bytes);//This method transfers bytes from this buffer into the given destination array
                    String body = new String(bytes, "UTF-8");
                    System.out.println("The time server receive order:"+body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)? new Date().toString():"BAD ORDER";

                    doWrite(sc,currentTime);
                }else if(readBytes < 0){
                    //链路已经关闭，需要关闭SocketChannel，释放资源
                    key.cancel();
                    sc.close();
                }else {
                    //读到0字节，忽略
                }
            }
        }
    }

    private void doWrite(SocketChannel channel, String response) throws IOException {
        if(response != null && response.trim().length()>0){
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();//limit = position; position = 0;

            //由于SocketChannel是异步非阻塞的，它并不能保证一次能够把需要发送的字节数组发送完，此时会出现“写半包”问题。
            //我们需要注册写操作，不断轮询Selector将没有发送完的ByteBuffer发送完毕，然后可以通过ByteBuffer的hasRemain()方法判断消息是否发送完成。
            //此处未处理“写半包”场景
            channel.write(writeBuffer);
        }
    }
}
