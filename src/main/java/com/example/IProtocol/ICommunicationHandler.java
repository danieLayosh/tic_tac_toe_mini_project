package com.example.IProtocol;

public interface ICommunicationHandler {
    String readMessage();
    void send(String message);
    void close();
}
