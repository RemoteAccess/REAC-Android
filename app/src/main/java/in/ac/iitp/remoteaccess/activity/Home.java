package in.ac.iitp.remoteaccess.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import in.ac.iitp.remoteaccess.R;

public class Home extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent in = getIntent();
        if(!in.hasExtra(LoginActivity.INTENT_EMAIL))
        {
            Toast.makeText(this, "No Data",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        TextView tv_user = (TextView) findViewById(R.id.user);
        tv_user.setText(in.getStringExtra(LoginActivity.INTENT_EMAIL));



        findViewById(R.id.b_logapp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), LogApplication.class);
                startActivity(in);
            }
        });


    }
}
