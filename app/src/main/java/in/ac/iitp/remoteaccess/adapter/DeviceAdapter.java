package in.ac.iitp.remoteaccess.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import in.ac.iitp.remoteaccess.R;
import in.ac.iitp.remoteaccess.model.DeviceModel;
import in.ac.iitp.remoteaccess.model.LogModel;

/**
 * Created by scopeinfinity on 6/4/16.
 */
public class DeviceAdapter extends ArrayAdapter<DeviceModel> {
    private Context mContext;

    public DeviceAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.item_device, null);

        TextView deviceName = (TextView) convertView.findViewById(R.id.tv_device_Name);
        TextView deviceIP = (TextView) convertView.findViewById(R.id.tv_device_ip);
        ImageButton pick = (ImageButton) convertView.findViewById(R.id.b_device_pick);

        final DeviceModel data = getItem(position);
        deviceName.setText(data.getDeviceName());
        deviceIP.setText("PID : "+data.getIP());
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picked(data.getIP());
            }
        });


        YoYo.with(Techniques.Swing).duration(50).playOn(convertView);


        return convertView;

    }

    private void picked(String IP) {

    }
}
