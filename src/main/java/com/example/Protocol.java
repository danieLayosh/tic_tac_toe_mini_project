package com.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Protocol {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private static int PORT = 1234;
    private static String HOST = "192.168.137.1";

    public Protocol(Socket socket) {
        this.socket = socket;
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public String res(){
        try {
            return this.in.readUTF();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return "";
    }

    public void send(String msg){
        try {
            this.out.writeUTF(msg);
            this.out.flush();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void close(){
        try {
            this.socket.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static String getHost() {
        return HOST;
    }

    public static int getProt(){
        return PORT;
    }

}
