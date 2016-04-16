package in.ac.iitp.remoteaccess.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import in.ac.iitp.remoteaccess.R;
import in.ac.iitp.remoteaccess.model.ClientSocket;
import in.ac.iitp.remoteaccess.utils.Client;
import in.ac.iitp.remoteaccess.utils.Constants;
import in.ac.iitp.remoteaccess.utils.FetchSocket;

public class Home extends AppCompatActivity {

    private ClientSocket clientSocket;
    public static final int PICKFILE_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getIntent().hasExtra("AfterLogin"))
        {
            ClientSocket.askSavePassword(this);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean("autoLogin",true);
            edit.commit();


        }

        clientSocket = ClientSocket.getInstance();
        /* Intent in = getIntent();
        if(!in.hasExtra(LoginActivity.INTENT_EMAIL))
        {
            Toast.makeText(this, "No Data",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        TextView tv_user = (TextView) findViewById(R.id.user);
        tv_user.setText(in.getStringExtra(LoginActivity.INTENT_EMAIL));
        */


        findViewById(R.id.b_logapp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), LogApplication.class);
                startActivity(in);
            }
        });

        findViewById(R.id.b_lock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchSocket fss = new FetchSocket() {
                    @Override
                    protected void onPostExecute(String s) {
                        if (s == null)
                            Toast.makeText(Home.this, "Error!", Toast.LENGTH_SHORT).show();
                        else {
                            if (s.startsWith("Status :")) {
                                s = s.substring(8).trim();
                                if (s.equals("0")) {
                                    Snackbar.make(findViewById(android.R.id.content),
                                            "Logging Out", Snackbar.LENGTH_LONG).show();
                                } else {
                                    Snackbar.make(findViewById(android.R.id.content),
                                            "Failed!", Snackbar.LENGTH_LONG).show();
                                }


                            } else {
                                Snackbar.make(findViewById(android.R.id.content),
                                        "Some Error Occurred!", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                };
                fss.execute("LOCK\r\n");
            }
        });
    findViewById(R.id.b_upload).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICKFILE_REQUEST_CODE);

        }
    });

        findViewById(R.id.b_shared).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), Downloads.class);
                startActivity(in);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==  PICKFILE_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                try {
                    String uriString = data.getData().toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();
                    Log.e("*****************", ">>PATH : " + path);
                    String displayName = null;

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = Home.this.getContentResolver().query(data.getData(), null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                                File temp = new File(displayName);
                                displayName = temp.getName();
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();
                    }

                    upload(displayName, getContentResolver().openInputStream(data.getData()));
                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(findViewById(android.R.id.content),
                            "Error in Opening File", Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(findViewById(android.R.id.content)
                        ,"File Pick Failed!", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void upload(String lastPathSegment, final InputStream inputStream) {

        FetchSocket fss = new FetchSocket() {
            @Override
            protected void onPostExecute(String s) {
                if (s == null)
                    Toast.makeText(Home.this, "Error!", Toast.LENGTH_SHORT).show();
                else {
                    if (s.startsWith("Status :")) {
                        s = s.substring(8).trim();
                        if (s.equals("0")) {
                            Snackbar.make(findViewById(android.R.id.content),
                                    "Uploading File", Snackbar.LENGTH_LONG).show();

                            new UploadStream(inputStream);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content),
                                    "Failed!", Snackbar.LENGTH_LONG).show();
                        }


                    } else {
                        Snackbar.make(findViewById(android.R.id.content),
                                "Some Error Occurred!", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        };
        fss.execute("UPLOAD" + lastPathSegment + "\r\n");
        Toast.makeText(this, lastPathSegment, Toast.LENGTH_SHORT).show();
    }
    class UploadStream extends AsyncTask<Void,Void,Boolean> {

        InputStream is;
        UploadStream(InputStream is) {
            this.is = is;
            execute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            InetAddress address = null;
            try {
                address = InetAddress.getByName(Client.getServer());
                Thread.sleep(1000);
                Socket connection = new Socket(address, 8083);
                OutputStream out = connection.getOutputStream();
                IOUtils.copy(is, out);
                is.close();
                out.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
           return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Send Successful!", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(findViewById(android.R.id.content),
                        "Transmission Failed!!", Snackbar.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_close :
                ClientSocket.close();
                System.exit(0);

                break;
            case R.id.action_anotherdevice :
                closeSession();
                break;
            case R.id.action_refreshconnection:
                ClientSocket cs = ClientSocket.getInstance();
                if(cs!=null) {
                    Socket s = cs.getSocket();
                    if(s!=null)
                        try {
                            s.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
                LoginActivity.autoLogin(null,this);
                break;
        }
        return true;
    }

    private void closeSession() {
        ClientSocket.close();
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putBoolean("autoLogin",false);
        edit.commit();

        Intent in = new Intent(this,LoginActivity.class);
        startActivity(in);
        finish();
    }
}
