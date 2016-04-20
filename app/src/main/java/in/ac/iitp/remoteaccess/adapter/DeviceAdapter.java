package in.ac.iitp.remoteaccess.adapter;

import android.content.Context;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.ac.iitp.remoteaccess.R;
import in.ac.iitp.remoteaccess.activity.Devices;
import in.ac.iitp.remoteaccess.activity.LoginActivity;
import in.ac.iitp.remoteaccess.model.DeviceModel;
import in.ac.iitp.remoteaccess.model.LogModel;
import in.ac.iitp.remoteaccess.utils.Constants;
import in.ac.iitp.remoteaccess.utils.MyHttpClient;

/**
 * Created by scopeinfinity on 6/4/16.
 */

public class DeviceAdapter extends ArrayAdapter<DeviceModel> {
    private LoginActivity mContext;

    public DeviceAdapter(LoginActivity context) {
        super(context, R.layout.item_device);
        mContext = context;
        fetchIPs();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.item_device, null);

        TextView deviceName = (TextView) convertView.findViewById(R.id.tv_device_Name);
        final TextView deviceIP = (TextView) convertView.findViewById(R.id.tv_device_ip);
        final ImageButton pick = (ImageButton) convertView.findViewById(R.id.b_device_pick);
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picked(deviceIP.getText().toString());
            }
        });

        final DeviceModel data = getItem(position);
        deviceName.setText(data.getDeviceName());
        deviceIP.setText("PID : "+data.getIP());
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picked(data.getIP());
            }
        });


        //YoYo.with(Techniques.Swing).duration(50).playOn(convertView);


        return convertView;

    }

    private void picked(String IP) {
     mContext.fillIP(IP);
    }

    private void fetchIPs() {
        MyHttpClient client = new MyHttpClient(Constants.BASE_URL + "/api/allip", null, false, new  MyHttpClient.MyHttpClientListener() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onFailed(Exception e) {
               Toast.makeText(mContext, "Coun't fetch IP list!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onSuccess(Object output) {
                if(output==null)
                    Toast.makeText(mContext, "Coun't fetch IP list", Toast.LENGTH_SHORT).show();
                else {
                    try {
                        JSONObject data = new JSONObject((String)output);
                        JSONArray array = data.getJSONArray("data");
                        clear();
                        for (int i=0;i<array.length();i++)
                        {
                            JSONObject row = array.getJSONObject(i);
                            add(new DeviceModel(row.getString("name"),row.getString("ip")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Coun't fetch IP list!!", Toast.LENGTH_SHORT).show();
                    }
                    checkBlankMSG();
                }
            }

            @Override
            public void onBackgroundSuccess(String result) {
                Log.e("******************", "AA" + result);
            }
        });
    }


    private void checkBlankMSG() {
        mContext.checkBlankMSG();
    }
}
