package in.ac.iitp.remoteaccess.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import in.ac.iitp.remoteaccess.model.LogModel;

import in.ac.iitp.remoteaccess.R;

/**
 * Created by scopeinfinity on 13/3/16.
 */
public class LogAdapter extends ArrayAdapter<LogModel> {

    private Context mContext;

    public LogAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.item_log_element, null);

        TextView tvAppName = (TextView) convertView.findViewById(R.id.tv_log_app_name);
        TextView tvPID = (TextView) convertView.findViewById(R.id.tv_log_pid);
        ImageButton close = (ImageButton) convertView.findViewById(R.id.b_log_close);

        final LogModel data = getItem(position);
        tvAppName.setText(data.getAppName());
        tvPID.setText("PID : "+data.getPID());
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Killing PID:" + data.getPID(), Toast.LENGTH_SHORT).show();
            }
        });


        YoYo.with(Techniques.FadeIn).duration(100).playOn(convertView);


        return convertView;

    }
}
