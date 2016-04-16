package in.ac.iitp.remoteaccess.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import in.ac.iitp.remoteaccess.R;
import in.ac.iitp.remoteaccess.model.ClientSocket;
import in.ac.iitp.remoteaccess.model.LogModel;
import in.ac.iitp.remoteaccess.utils.Client;
import in.ac.iitp.remoteaccess.utils.FetchSocket;

/**
 * Created by scopeinfinity on 14/4/16.
 */
public class DownloadAdapter extends ArrayAdapter<String> {

    private Activity mActivity;

    public DownloadAdapter(Activity activity) {
        super(activity, R.layout.item_download_file);
        mActivity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.item_download_file, null);

        final TextView filename = (TextView) convertView.findViewById(R.id.filename);
        ImageButton download = (ImageButton) convertView.findViewById(R.id.b_download);

        final String file = getItem(position);
        filename.setText(file);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload(file);
            }
        });


        return convertView;

    }
    private void startDownload(String filename) {
        File dir = new File( Environment.getExternalStorageDirectory(),"RemoteAccess");
        dir.mkdirs();
        File file = new File(dir,filename);
        Log.e("******************", "Download : Created");
        DownloadStream ds= new DownloadStream(filename, file);
        ds.execute();
    }

    class DownloadStream extends AsyncTask<Void,Void,Boolean> {

        File file;
        String filename;
        DownloadStream(String filename, File file) {
            this.file = file;
            this.filename = filename;
            Log.e("******************","Download : Const");


        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.e("******************", "Download : DIB INIT");
            ServerSocket sss = null;
            try {

                Log.e("******************", "Download : DIB");
                    final ServerSocket ss = new ServerSocket(8083);
                    sss = ss;
                 mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final FetchSocket fetchSocket= new FetchSocket(){
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                Log.e("*****************", "SS PRE ");

                            }

                            @Override
                            protected String doInBackground(String... params) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                return super.doInBackground(params);
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                if(s==null) {
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mActivity.getApplicationContext(), "Error in Connection!!", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                    try {
                                        ss.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else if(!s.equals("Status : 0")) {
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mActivity.getApplicationContext(), "Invalid Filename!", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                    try {
                                        ss.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Log.e("*****************","Download : "+s);

                                //Toast.makeText(getApplicationContext(),"Added! : "+words[i,Toast.LENGTH_SHORT).show();

                            }


                        };
                        Log.e("******************", "FS : call download");
                        fetchSocket.executeOnExecutor(THREAD_POOL_EXECUTOR, "DOWNLOAD_" + filename + "\r\n");
                    }
                });


                if(ss.isClosed())
                    return false;

                Log.e("******************","Download : Before SOcket");

                Socket s = ss.accept();
                Log.e("******************","Download : After SOcket");

                if(s!=null) {
                    InputStream is = s.getInputStream();
                    file.createNewFile();

                    OutputStream os = new FileOutputStream(file);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(mActivity.findViewById(android.R.id.content),
                                    "Downloading Started!", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                    IOUtils.copy(is,os);
                    is.close();
                    os.close();
                    s.close();
                    ss.close();
                    return true;
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(sss!=null)
                    if(!sss.isClosed())
                        try {
                            sss.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean) {
                Toast.makeText(mActivity.getApplicationContext(), "File Saved at " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mActivity.getApplicationContext(),"File Download Failed!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
