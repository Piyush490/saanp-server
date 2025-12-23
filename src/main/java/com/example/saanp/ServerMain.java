package com.example.saanp;

public class ServerMain {

    public static void main(String[] args) throws Exception {

        int port = Integer.parseInt(
                System.getenv().getOrDefault("PORT", "8080")
        );

        NettyServer server = new NettyServer(port);
        server.start();
    }
}
