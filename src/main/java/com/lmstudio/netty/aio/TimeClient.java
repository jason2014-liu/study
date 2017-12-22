package com.lmstudio.netty.aio;

/**
 * 可jps -l 查看到进程ID；再jstack -l processID 查看线程快照
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8080;
        new Thread(new AsyncTimeClientHandler("127.0.0.1",port),"AIO-AsyncTimeClientHandler-001").start();
    }
}
