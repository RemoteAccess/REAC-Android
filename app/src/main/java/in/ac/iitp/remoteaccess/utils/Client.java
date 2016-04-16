package in.ac.iitp.remoteaccess.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import in.ac.iitp.remoteaccess.model.ClientSocket;

/**
 * Created by scopeinfinity on 7/4/16.
 */
public class Client extends AsyncTask<String,Void,Boolean> {

    private Context context;
    private static String server;

    private ClientSocket client;


    public Client(Context context, String server) {
        this.context = context;
        Client.server = server;

    }

    public static String getServer() {
        return server;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            Log.e("*********************", "Trying!");

            InetAddress address = InetAddress.getByName(server);
            if(!address.isReachable(5000)) {
                return null;
            }

            Socket connection = new Socket(address, Constants.MAIN_PORT);
            Log.e("*********************", "Trying!");
            //connection.setSoTimeout(10000);

            //connection.bind(new InetSocketAddress(address, Constants.MAIN_PORT));
            Log.e("*********************", "Trying!");
            //connection.setSoTimeout(1000);

            client = new ClientSocket(server, connection, params[0]);

            Thread.sleep(500);
            InputStream is = client.in;
            String PROTOCOL_INIT = "[[[[REAC:RemoteAccess]]]]";
            String inP = "";
            int ch;

                for (int i = 0; i < PROTOCOL_INIT.length() && (ch = is.read()) != -1; i++)
                    inP += (char) ch;

                if (!inP.equals(PROTOCOL_INIT)) {
                    Log.e("*********************", "INP : " + inP);
                    client.getSocket().close();
                    return null;

                }


                StringBuilder sb = new StringBuilder();
                if (!client.isConnected())
                    return null;

                while ((ch = is.read()) != -1) {
                    if ((char) ch != '\n' && (char) ch != '\r') {
                        sb.append((char) ch);
                        continue;
                    }

                    Log.e("*******************<", "WAITI|NG");
                    final String finalL = sb.toString().trim();
                    sb = new StringBuilder();
                    Log.e("*************>", "MSG : " + finalL);

                    if (finalL.startsWith("[[") && finalL.endsWith("]]")) {

                        String cmd = finalL.substring(2, finalL.length() - 2);
                        if (cmd.contains("Allowed"))
                            return true;
                        else return false;
                    }
                }
                return null;

        }catch (Exception e) {
            e.printStackTrace();
            try {
                if(client.getSocket().isConnected())
                    client.getSocket().close();
                } catch (Exception e1) {

                    e1.printStackTrace();
                }
        }

        return null;
    }

}
