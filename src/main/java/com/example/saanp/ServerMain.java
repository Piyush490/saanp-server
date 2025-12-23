package com.example.saanp;

public class ServerMain {

    public static void main(String[] args) throws Exception {
        NettyServer server = new NettyServer(8080);
        server.start();
    }
}
