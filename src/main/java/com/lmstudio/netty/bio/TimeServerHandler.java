package com.lmstudio.netty.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 * 学习过TCP/IP相关知识的人都知道，当消息的接收方处理缓慢的时候，将不能及时地从TCP缓冲区读取数据，这将会导致发送方的TCP window size不断减小，
 * 直到为0，双方处于Keep-Alive状态，消息发送方将不能再向TCP缓冲区写入消息，这时如果采用的是同步阻塞IO，write操作将会被无限期阻塞，直到
 * window size 大于0 或者发生IO异常
 */
public class TimeServerHandler implements Runnable {

    private Socket socket;

    public TimeServerHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        BufferedReader in = null;
        PrintWriter out = null;
        try{
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(),true);
            String currentTime = null;
            String body = null;
            while(true){
                body = in.readLine();
                if(body == null){
                    break;
                }
                System.out.println("The time server receive order:"+body);
                currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)? new Date().toString():"BAD ORDER";
                out.println(currentTime);
            }
        }catch (Exception e){
            if(in != null){
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if(out != null) out.close();
            if(this.socket != null){
                try {
                    this.socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                this.socket = null;
            }
        }
    }
}
