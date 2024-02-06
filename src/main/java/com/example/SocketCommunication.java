package com.example;

import com.example.IProtocol.ICommunicationHandler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class SocketCommunication implements ICommunicationHandler {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private static int PORT = 8080;
    private static String HOST = "192.168.16.176";

    public SocketCommunication(Socket socket) {
        this.socket = socket;
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public String readMessage() {
        try {
            return in.readUTF();
        } catch (Exception e) {
            System.out.println("Error in SocketCommunication -> readMessage: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void send(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (Exception e) {
            System.out.println("Error in SocketCommunication -> send: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            System.out.println("Error in SocketCommunication -> close: " + e.getMessage());
        }
    }

    public static String getHost() {
        return HOST;
    }

    public static int getPort() {
        return PORT;
    }
}
