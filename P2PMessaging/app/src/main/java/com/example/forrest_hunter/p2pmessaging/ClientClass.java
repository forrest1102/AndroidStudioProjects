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
    SendReceive sendReceive;

    public ClientClass(InetAddress hostAddress, SendReceive sendReceive){
        hostAdd = hostAddress.getHostAddress();
        socket = new Socket();
        this.sendReceive = new SendReceive(socket);
        sendReceive.start();
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
