package com.example;

import com.example.IProtocol.ICommunicationHandler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketCommunication implements ICommunicationHandler {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private static int PORT = 8080;
    private static String HOST;

    public SocketCommunication() {
        initializeSocketCommunication();
    }

    public void initializeSocketCommunication() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            byte[] ip = localhost.getAddress();
            HOST = (ip[0] & 0xFF) + "." + (ip[1] & 0xFF) + "." + (ip[2] & 0xFF) + "." + (ip[3] & 0xFF);
        } catch (UnknownHostException e) {
            System.out.println("Error in SocketCommunication -> getHost: " + e.getMessage());
        }
    }

    public SocketCommunication(Socket socket) {
        initializeSocketCommunication();

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
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
        } catch (Exception e) {
            System.out.println("Error in SocketCommunication -> close: " + e.getMessage());
        }
    }

    public static String getHost() {
        new SocketCommunication();
        return HOST;
    }

    public static int getPort() {
        return PORT;
    }
}
