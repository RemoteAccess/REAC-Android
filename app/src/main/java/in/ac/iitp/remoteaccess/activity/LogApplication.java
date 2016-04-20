package in.ac.iitp.remoteaccess.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.Socket;

import in.ac.iitp.remoteaccess.R;
import in.ac.iitp.remoteaccess.adapter.LogAdapter;
import in.ac.iitp.remoteaccess.model.ClientSocket;
import in.ac.iitp.remoteaccess.model.LogModel;
import in.ac.iitp.remoteaccess.utils.FetchSocket;

public class LogApplication extends AppCompatActivity {

    private ListView list;
    private LogAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_application);

        list = (ListView) findViewById(R.id.list);
        adapter = new LogAdapter(this,R.layout.item_log_element);


        list.setAdapter(adapter);

        adapter.fetchList();


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
                adapter.fetchList();
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

    public void checkBlankMSG() {
        if(list.getCount()==0)
            ((TextView)(findViewById(R.id.tv_blank))).setText("No IP Found!");
        else
            ((TextView)findViewById(R.id.tv_blank)).setText("Available REAC-Server IP");
    }
}
