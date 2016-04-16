package in.ac.iitp.remoteaccess.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;

import in.ac.iitp.remoteaccess.R;
import in.ac.iitp.remoteaccess.adapter.DownloadAdapter;
import in.ac.iitp.remoteaccess.model.ClientSocket;
import in.ac.iitp.remoteaccess.utils.FetchSocket;

public class Downloads extends AppCompatActivity {

    private ListView listView;
    private DownloadAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);


        listView = (ListView) findViewById(R.id.list);
        adapter = new DownloadAdapter(this);
        listView.setAdapter(adapter);
        fetchList();

    }


    public void fetchList() {


        FetchSocket fetchSocket= new FetchSocket(){
            @Override
            protected void onPostExecute(String s) {
                if(s==null) {
                    Toast.makeText(getApplicationContext(), "Error in Connection!!", Toast.LENGTH_SHORT).show();
                   // finish();
                    return;
                }
                Log.e("*****************","Downlaods : "+s);
                String[] words = s.split("/");
                adapter.clear();
                for (int i=0;i<words.length;i++) {
                    adapter.add(words[i]);
                    //Toast.makeText(getApplicationContext(),"Added! : "+words[i,Toast.LENGTH_SHORT).show();

                }

            }
        };
        fetchSocket.execute("SHARED\r\n");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_log_application, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh :
                fetchList();
                break;
            case R.id.action_close :
                ClientSocket.close();
                System.exit(0);
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
                LoginActivity.autoLogin(null, this);
                break;

        }
        return true;
    }
}
