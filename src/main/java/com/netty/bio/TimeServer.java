package com.netty.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServer{

   public static void main(String[] args) throws IOException {

       int port = 8080;
       ServerSocket server = null;
       try
       {
           server = new ServerSocket(port);
           System.out.println("The time server is start in port:"+port);
           Socket socket = null;
           //创建IO任务线程池
           TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(10,10000);
           while(true){
               socket = server.accept();
               //new Thread(new TimeServerHandler(socket)).start();
               singleExecutor.execute(new TimeServerHandler(socket));
           }
       }finally {
            if(server != null){
                System.out.println("The time server close");
                server.close();
                server = null;
            }
       }
   }



}
