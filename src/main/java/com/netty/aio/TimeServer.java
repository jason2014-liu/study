package com.netty.aio;

public class TimeServer {
    public static void main(String[] args) {
        int port = 8080;
        new Thread(new AsyncTimeServerHandler(port),"AIO-AsyncTimeServerHandler-001").start();
    }
}
