package com.example.forrest_hunter.p2pmessaging;

import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SendReceive extends Thread {
    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;
    Handler handler;

    public SendReceive(Socket socket, Handler handler) {
        this.socket = socket;
        this.handler = handler;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        while(socket != null){
            try {
                bytes = inputStream.read(buffer);
                if(bytes > 0){
                    handler.obtainMessage(Strings.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] bytes){

    }
}
