package in.ac.iitp.remoteaccess.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Map;

import in.ac.iitp.remoteaccess.R;
import in.ac.iitp.remoteaccess.adapter.DownloadAdapter;
import in.ac.iitp.remoteaccess.model.LogModel;
import in.ac.iitp.remoteaccess.utils.FetchSocket;

public class Devices extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


}
