package in.ac.iitp.remoteaccess.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.Pair;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import in.ac.iitp.remoteaccess.activity.LogApplication;
import in.ac.iitp.remoteaccess.model.ClientSocket;
import in.ac.iitp.remoteaccess.model.LogModel;

import in.ac.iitp.remoteaccess.R;
import in.ac.iitp.remoteaccess.utils.FetchSocket;

/**
 * Created by scopeinfinity on 13/3/16.
 */
public class LogAdapter extends ArrayAdapter<LogModel> implements Comparator<LogModel> {

    private LogApplication mActivity;
    private ClientSocket socket;
    private HashMap<String, Integer> blockMap;

    public LogAdapter(LogApplication activity, int resource) {
        super(activity, resource);
        mActivity = activity;
        blockMap = new HashMap<>();
        loadFile();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.item_log_element, null);

        TextView tvAppName = (TextView) convertView.findViewById(R.id.tv_log_app_name);
        TextView tvPID = (TextView) convertView.findViewById(R.id.tv_log_pid);
        ImageButton close = (ImageButton) convertView.findViewById(R.id.b_log_close);
        final ImageButton block = (ImageButton) convertView.findViewById(R.id.b_log_block);

        final LogModel data = getItem(position);
        tvAppName.setText(data.getAppName());
        tvPID.setText("PID : "+data.getPID());
        tvAppName.setText(data.getAppName());
        if(blockMap.containsKey(getBlockName(data))) {
            block.setImageResource(R.drawable.ic_thumb_up_black_36dp);
            tvPID.setText("BLOCKED");

        } else {
            block.setImageResource(R.drawable.ic_block_black_36dp);
            tvPID.setText("PID : "+data.getPID());
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FetchSocket fss = new FetchSocket() {
                    @Override
                    protected void onPostExecute(String s) {
                        if (s == null)
                            Snackbar.make(mActivity.findViewById(android.R.id.content),
                                    "Error!", Snackbar.LENGTH_SHORT).show();
                        else {
                            if (s.startsWith("Status :")) {
                                s = s.substring(8).trim();
                                if (s.equals("0")) {
                                    Snackbar.make(mActivity.findViewById(android.R.id.content),
                                            "Terminated Successfully", Snackbar.LENGTH_SHORT).show();
                                    fetchList();
                                } else {
                                    Snackbar.make(mActivity.findViewById(android.R.id.content),
                                            "Terminatation Failed", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(mActivity.findViewById(android.R.id.content),
                                        "Some Error Occured!", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                };
                fss.execute("PIDKILL_" + data.getPID() + "\r\n");
            }
        });

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(blockMap.containsKey(getBlockName(data))) {

                        FetchSocket fss = new FetchSocket() {
                            @Override
                            protected void onPostExecute(String s) {
                                if (s == null)
                                    Snackbar.make(mActivity.findViewById(android.R.id.content),
                                            "Error!", Snackbar.LENGTH_SHORT).show();
                                else {
                                    if (s.startsWith("Status :")) {
                                        s = s.substring(8).trim();
                                        if (s.equals("0")) {
                                            Snackbar.make(mActivity.findViewById(android.R.id.content),
                                                    "Unblocked!", Snackbar.LENGTH_SHORT).show();
                                            fetchList();
                                        } else {
                                            Snackbar.make(mActivity.findViewById(android.R.id.content),
                                                    "Failed!", Snackbar.LENGTH_SHORT).show();
                                        }
                                        blockMap.remove(getBlockName(data));
                                        saveFile();

                                    } else {
                                        Snackbar.make(mActivity.findViewById(android.R.id.content),
                                                "Some Error Occured!", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        };
                        fss.execute("PIDKILL_" +blockMap.get(getBlockName(data)) + "\r\n");

                } else {


                    FetchSocket fss = new FetchSocket() {
                        @Override
                        protected void onPostExecute(String s) {
                            if (s == null)
                                Snackbar.make(mActivity.findViewById(android.R.id.content),
                                        "Error!", Snackbar.LENGTH_SHORT).show();
                            else {
                                if (s.startsWith("PID :")) {
                                    s = s.substring(5).trim();
                                    Log.e("*******", "PID >>> " + s);
                                    int pid = Integer.parseInt(s);
                                    blockMap.put(getBlockName(data), pid);
                                    saveFile();
                                    fetchList();
                                    Snackbar.make(mActivity.findViewById(android.R.id.content),
                                            "Block Called!", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Snackbar.make(mActivity.findViewById(android.R.id.content),
                                            "Some Error Occured!", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }
                    };
                    fss.execute("BLOCK_" + getBlockName(data)  + "\r\n");



                }
            }
        });


        YoYo.with(Techniques.FadeIn).duration(50).playOn(convertView);


        return convertView;

    }
    public  void fetchList(){
        fetchList(false);
    }
    public void fetchList(final boolean closeAcitivity) {


        FetchSocket fetchSocket= new FetchSocket(){
            @Override
            protected void onPostExecute(String s) {
                if(s==null) {
                    Toast.makeText(mActivity.getApplicationContext(),"Error in Connection!", Toast.LENGTH_SHORT).show();
                    if(closeAcitivity)
                      ;//  mActivity.finish();
                    return;
                }
                String[] words = s.split("!");
                clear();
                for (int i=1;i<words.length;i+=2) {
                    String cmd = words[i];
                    if(filter(cmd))
                    add(new LogModel(cmd, Integer.parseInt(words[i - 1])));
                    //Toast.makeText(getApplicationContext(),"Added! : "+words[i,Toast.LENGTH_SHORT).show();

                }
                mActivity.checkBlankMSG();

                for (Map.Entry<String, Integer> data: blockMap.entrySet()) {
                    add(new LogModel(data.getKey(),-1));

                }
                sort(LogAdapter.this);
            }
        };
        fetchSocket.execute("PIDS\r\n");
    }
    private boolean  filter(String cmd ){
        if(cmd.startsWith("[") && cmd.endsWith("]"))
            return false;
        return true;
    }

    public int compare(LogModel lhs, LogModel rhs) {
        if(lhs.getPID()<0)
            return Integer.MIN_VALUE;
        if(rhs.getPID()<0)
            return Integer.MAX_VALUE;

        int t = lhs.getAppName().compareTo(rhs.getAppName());
        if(t==0)
            return lhs.getPID()-rhs.getPID();
        return t;
    }

    private String getBlockName(LogModel data) {
        String name = data.getAppName();
        if(name.lastIndexOf('/')>=0)
            name = name.substring(name.lastIndexOf('/')+1);
        return name;

    }


    private void saveFile() {
        File data = new File(Environment.getDataDirectory(),"blockList");
            try {
                BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(data)));
                for (Map.Entry<String, Integer> d: blockMap.entrySet()) {
                   br.write(d.getKey()+"\n"+ d.getValue()+"\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    private void loadFile() {
        blockMap.clear();
        File data = new File(Environment.getDataDirectory(),"blockList");
        if(data.exists()) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(data)));
                String line = null;
                while ((line = br.readLine())!=null) {
                    String line2 = br.readLine();
                    if(line2!=null) {
                        blockMap.put(line,Integer.parseInt(line2));
                    }
                }
                br.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
