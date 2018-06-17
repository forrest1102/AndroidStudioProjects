package com.example.forrest_hunter.p2pmessaging;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClass extends Thread {
    Socket socket;
    ServerSocket serverSocket;
    SendReceive sendReceive;

    public ServerClass(Socket socket, ServerSocket serverSocket, SendReceive sendReceive) {
        this.socket = socket;
        this.serverSocket = serverSocket;
        this.sendReceive = sendReceive;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(8888);
            socket = serverSocket.accept();
            sendReceive = new SendReceive(socket);
            sendReceive.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
