package de.hebk.multiplayer;

import com.google.gson.Gson;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;

public class ClientConnection {
    private SSLSocket socket;
    private String username;
    private Gson gson;
    private BufferedReader reader;
    private BufferedWriter writer;

    public ClientConnection(SSLSocket socket) {
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            gson = new Gson();

            Packet p = gson.fromJson(read(), Packet.class);
            if (p.getPacketType().equals(PacketType.JOIN)) {
                username = p.getContent();
            }
            else {
                closeConnection();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String msg) {
        try {
            writer.write(msg);
            writer.flush();
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String read() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
