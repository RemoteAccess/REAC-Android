package in.ac.iitp.remoteaccess.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import in.ac.iitp.remoteaccess.R;
import in.ac.iitp.remoteaccess.adapter.LogAdapter;
import in.ac.iitp.remoteaccess.model.ClientSocket;
import in.ac.iitp.remoteaccess.model.LogModel;
import in.ac.iitp.remoteaccess.utils.FetchSocket;

public class LogApplication extends AppCompatActivity {

    private ListView list;
    private LogAdapter adapter;
    private ClientSocket socket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_application);

        list = (ListView) findViewById(R.id.list);
        adapter = new LogAdapter(this,R.layout.item_log_element);
        socket = ClientSocket.getInstance();


        list.setAdapter(adapter);

        adapter.fetchList();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_log_application,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh :
                adapter.fetchList();
            break;
        }
        return true;
    }
}
