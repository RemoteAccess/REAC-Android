package in.ac.iitp.remoteaccess.model;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import in.ac.iitp.remoteaccess.utils.Client;

/**
 * Created by scopeinfinity on 7/4/16.
 */
public class ClientSocket {
    private static ClientSocket instance = null;
    private Socket mainSocket;
    private String password;

    public OutputStream out;
    public InputStream in;

    public ClientSocket(Socket socket, String password) {
        this.mainSocket = socket;

        try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
            out.write((password+"\r\n").getBytes());
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        instance = this;
    }

    public static ClientSocket getInstance() {
        return instance;
    }

    public Socket getSocket() {
        return mainSocket;
    }

    public boolean isConnected() {
        if(mainSocket==null)
            return false;
        return mainSocket.isConnected();
    }

}
