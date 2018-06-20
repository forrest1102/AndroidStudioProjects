package com.example.forrest_hunter.p2pmessaging;

import android.os.Handler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.Socket;
import java.util.logging.SocketHandler;

public class ClientClass extends Thread {

    Socket socket;
    String hostAdd;
    SendReceive sendReceive;
    Handler handler;

    public ClientClass(InetAddress hostAddress, Handler handler){
        hostAdd = hostAddress.getHostAddress();
        socket = new Socket();
        this.handler = handler;
        sendReceive = new SendReceive(socket, this.handler);
        sendReceive.start();
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(hostAdd, 8888), 500);
            sendReceive = new SendReceive(socket, handler);
            sendReceive.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
