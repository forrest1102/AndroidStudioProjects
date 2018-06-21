package com.example.forrest_hunter.p2pmessaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Handler;

public class SendReceive extends Thread {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Handler handler;

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
                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] bytes){

    }
}
