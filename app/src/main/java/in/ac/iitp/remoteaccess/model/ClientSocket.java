package in.ac.iitp.remoteaccess.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

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

    public OutputStream out;
    public InputStream in;


    private static String password;
    private static String server;

    private static boolean lastPasswordChanged = false;

    public ClientSocket(String server, Socket socket, String password) {
        this.mainSocket = socket;
        if(!ClientSocket.password.equals(password) || !ClientSocket.server.equals(server)) {
            lastPasswordChanged = true;
        } else lastPasswordChanged = false;

        ClientSocket.password = password;
        ClientSocket.server = server;
        Log.e("*************", "SERVER : " + server);
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

    public static void saveCredentials(Context context) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString("last_pass",password);
        edit.putString("last_server",server);
        edit.commit();
    }

    public static void loadCredential(Context context) {

        SharedPreferences sp =  PreferenceManager.getDefaultSharedPreferences(context);
        password = sp.getString("last_pass", "");
        server = sp.getString("last_server","");
    }

    public static String getPassword(){
        return password;
    }
    public static String getServer() {
        return server;
    }

    public static void askSavePassword(final Context context) {
        if(!lastPasswordChanged)
            return;
        new AlertDialog.Builder(context)
                .setTitle("Save Password")
                .setMessage("Do you want to update the Credentials!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveCredentials(context);
                    }
                })
                .setNegativeButton("No",null)
                .create().show();
    }

    public static void close() {
        ClientSocket cs = ClientSocket.getInstance();
        if(cs!=null)
            try {
                cs.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

}
