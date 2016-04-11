package in.ac.iitp.remoteaccess.utils;

import android.content.Context;
import android.os.AsyncTask;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import in.ac.iitp.remoteaccess.model.ClientSocket;

/**
 * Created by scopeinfinity on 7/4/16.
 */
public class Client extends AsyncTask<String,Void,Boolean> {

    private Context context;
    private String server;

    private ClientSocket client;


    public Client(Context context, String server) {
        this.context = context;
        this.server = server;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            InetAddress address = InetAddress.getByName(server);
            Socket connection = new Socket(address,Constants.MAIN_PORT);
            client = new ClientSocket(connection,params[0]);
            Thread.sleep(1000);
            return client.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
