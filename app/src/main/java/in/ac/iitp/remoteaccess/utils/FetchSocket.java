package in.ac.iitp.remoteaccess.utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import in.ac.iitp.remoteaccess.model.ClientSocket;

/**
 * Created by scopeinfinity on 7/4/16.
 */
public class FetchSocket extends AsyncTask<String,Void,String> {

    private ClientSocket socket;

    @Override
    protected String doInBackground(String... params) {
        try {
            socket = ClientSocket.getInstance();
            Log.e("*************","SOCKET Connected : "+socket.isConnected());
            socket.out.write((params[0] + "\r\n").getBytes());
            socket.out.flush();
            String l = null;
            StringBuilder sb = new StringBuilder();
            int ch;
            Log.e("*******************", "WAITING");

            while((ch = socket.in.read())!=-1) {
                if((char)ch!='\n' && (char)ch!='\r') {
                    sb.append((char)ch);
                    continue;
                }

                final String finalL = sb.toString().trim();
                sb = new StringBuilder();
                Log.e("*************>","MSG : "+finalL);

               /* context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,">"+ finalL+"<",Toast.LENGTH_SHORT).show();
                    }
                });*/
                if(finalL.startsWith("[[") && finalL.endsWith("]]"))
                    return finalL.substring(2,finalL.length()-2);
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("*******************",e.toString());

        } finally {
            Log.e("*******************", "FINAL : " + socket.isConnected());


        }

        return null;
    }
}
