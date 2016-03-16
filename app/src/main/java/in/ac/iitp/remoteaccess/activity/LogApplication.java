package in.ac.iitp.remoteaccess.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import in.ac.iitp.remoteaccess.R;
import in.ac.iitp.remoteaccess.adapter.LogAdapter;
import in.ac.iitp.remoteaccess.model.LogModel;

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

        adapter.add(new LogModel("Firefox", 1234));
        adapter.add(new LogModel("Chrome",4563));
        adapter.add(new LogModel("Cinnamon",52));
        adapter.add(new LogModel("Skype", 9231));
        adapter.add(new LogModel("Firefox", 1234));
        adapter.add(new LogModel("Chrome",4563));
        adapter.add(new LogModel("Cinnamon",52));
        adapter.add(new LogModel("Skype",9231));
        adapter.add(new LogModel("Firefox", 1234));
        adapter.add(new LogModel("Chrome",4563));
        adapter.add(new LogModel("Cinnamon",52));
        adapter.add(new LogModel("Skype",9231));
        adapter.add(new LogModel("Firefox", 1234));
        adapter.add(new LogModel("Chrome",4563));
        adapter.add(new LogModel("Cinnamon",52));
        adapter.add(new LogModel("Skype",9231));
        adapter.add(new LogModel("Firefox", 1234));
        adapter.add(new LogModel("Chrome",4563));
        adapter.add(new LogModel("Cinnamon",52));
        adapter.add(new LogModel("Skype",9231));
        adapter.add(new LogModel("Firefox", 1234));
        adapter.add(new LogModel("Chrome",4563));
        adapter.add(new LogModel("Cinnamon",52));
        adapter.add(new LogModel("Skype",9231));
        adapter.add(new LogModel("Firefox", 1234));
        adapter.add(new LogModel("Chrome",4563));
        adapter.add(new LogModel("Cinnamon",52));
        adapter.add(new LogModel("Skype",9231));
        adapter.add(new LogModel("Firefox", 1234));
        adapter.add(new LogModel("Chrome",4563));
        adapter.add(new LogModel("Cinnamon",52));
        adapter.add(new LogModel("Skype",9231));
        adapter.add(new LogModel("Firefox", 1234));
        adapter.add(new LogModel("Chrome",4563));
        adapter.add(new LogModel("Cinnamon",52));
        adapter.add(new LogModel("Skype",9231));
        adapter.add(new LogModel("Firefox", 1234));
        adapter.add(new LogModel("Chrome",4563));
        adapter.add(new LogModel("Cinnamon",52));
        adapter.add(new LogModel("Skype",9231));




    }
}
