package com.lmstudio.netty.codec.serialize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class UserInfo implements Serializable {

    //默认的序列号
    private static final long serialVersionUID = 1L;

    private String userName;
    private int userID;

    public UserInfo(String userName, int userID) {
        this.userName = userName;
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * 基于ByteBuffer的通用二进制编解码
     * @return
     */
    public byte[] codec(){
        long startTime = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] value = this.userName.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(this.userID);
        buffer.flip();
        value = null;
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        long endTime = System.currentTimeMillis();
        System.out.println("The byte array serializable cost time is "+(endTime-startTime)+"ms");
        return result;
    }

    /**
     * jdk 序列化方式
     * @return
     * @throws IOException
     */
    public byte[] jdkSerialize() throws IOException {
        long startTime = System.currentTimeMillis();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        oos.flush();
        oos.close();
        byte[] result = bos.toByteArray();
        bos.close();
        long endTime = System.currentTimeMillis();
        System.out.println("The jdk serializable cost time is "+(endTime-startTime)+"ms");
        return result;
    }

    /**
     * 从测试结果来看，无论序列化后码流的大小，还是序列化的性能（可以序列化大量对象，测试结果更明显），JDK默认的序列化机制表现得都很差；
     * 而且无法跨语言，当我们需要和异构语言进行RPC交互时，java序列化就难以胜任。
     * 目前几乎所有流行的java rpc通信框架，都没有使用java序列化作为编解码框架，原因就在于它无法跨语言，而这些rpc框架需要支持跨语言调用。
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        UserInfo userInfo = new UserInfo("小明", 911);
        System.out.println("The jdk serializable length is:"+userInfo.jdkSerialize().length);
        System.out.println("The byte array serializable length is:"+userInfo.codec().length);

        /**
         * output:
         * The jdk serializable cost time is 7ms
         * The jdk serializable length is:112
         * The byte array serializable cost time is 0ms
         * The byte array serializable length is:14
         */
    }
}
