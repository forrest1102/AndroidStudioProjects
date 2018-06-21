package com.example.forrest_hunter.p2pmessaging;

import android.os.Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClass extends Thread {
    Socket socket;
    ServerSocket serverSocket;
    SendReceive sendReceive;
    Handler handler;

    public ServerClass(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(8888);
            socket = serverSocket.accept();
            sendReceive = new SendReceive(socket, handler);
            sendReceive.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
