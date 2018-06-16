package com.example.forrest_hunter.p2pmessaging;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.Socket;
import java.util.logging.SocketHandler;

public class ClientClass extends Thread {

    Socket socket;
    String hostAdd;

    public ClientClass(InetAddress hostAddress){
        hostAdd = hostAddress.getHostAddress();
        socket = new Socket();
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(hostAdd, 8888), 500);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
