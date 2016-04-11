package in.ac.iitp.remoteaccess.adapter;

import android.content.Context;
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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import in.ac.iitp.remoteaccess.model.ClientSocket;
import in.ac.iitp.remoteaccess.model.LogModel;

import in.ac.iitp.remoteaccess.R;
import in.ac.iitp.remoteaccess.utils.FetchSocket;

/**
 * Created by scopeinfinity on 13/3/16.
 */
public class LogAdapter extends ArrayAdapter<LogModel> implements Comparator<LogModel> {

    private Context mContext;
    private ClientSocket socket;
    private HashMap<LogModel, Integer> blockMap;

    public LogAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
        blockMap = new HashMap<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.item_log_element, null);

        TextView tvAppName = (TextView) convertView.findViewById(R.id.tv_log_app_name);
        TextView tvPID = (TextView) convertView.findViewById(R.id.tv_log_pid);
        ImageButton close = (ImageButton) convertView.findViewById(R.id.b_log_close);
        final ImageButton block = (ImageButton) convertView.findViewById(R.id.b_log_block);

        final LogModel data = getItem(position);
        tvAppName.setText(data.getAppName());
        tvPID.setText("PID : "+data.getPID());
        tvAppName.setText(data.getAppName());
        if(blockMap.containsKey(data)) {
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
                            Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show();
                        else {
                            if (s.startsWith("Status :")) {
                                s = s.substring(8).trim();
                                if (s.equals("0")) {
                                    Toast.makeText(mContext, "Terminated Successfully", Toast.LENGTH_SHORT).show();
                                    fetchList();
                                } else {
                                    Toast.makeText(mContext, "Terminatation Failed", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(mContext, "Some Error Occured!", Toast.LENGTH_SHORT).show();
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

                if(blockMap.containsKey(data)) {

                        FetchSocket fss = new FetchSocket() {
                            @Override
                            protected void onPostExecute(String s) {
                                if (s == null)
                                    Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show();
                                else {
                                    if (s.startsWith("Status :")) {
                                        s = s.substring(8).trim();
                                        if (s.equals("0")) {
                                            Toast.makeText(mContext, "Unblocked", Toast.LENGTH_SHORT).show();
                                            blockMap.remove(block);
                                            fetchList();
                                        } else {
                                            Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(mContext, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        };
                        fss.execute("PIDKILL_" + blockMap.get(block) + "\r\n");

                } else {


                    FetchSocket fss = new FetchSocket() {
                        @Override
                        protected void onPostExecute(String s) {
                            if (s == null)
                                Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show();
                            else {
                                if (s.startsWith("PID :")) {
                                    s = s.substring(5).trim();
                                    int pid = Integer.parseInt(s);
                                    blockMap.put(data,pid);
                                    fetchList();
                                    Toast.makeText(mContext, "Block Called!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    };
                    fss.execute("BLOCK_" + data.getPID() + "\r\n");



                }
            }
        });


        YoYo.with(Techniques.FadeIn).duration(50).playOn(convertView);


        return convertView;

    }

    public void fetchList() {


        FetchSocket fetchSocket= new FetchSocket(){
            @Override
            protected void onPostExecute(String s) {
                if(s==null) {
                    Toast.makeText(mContext,"Error in Connection!!",Toast.LENGTH_SHORT).show();
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
                for (Map.Entry<LogModel, Integer> data: blockMap.entrySet()) {
                    add(data.getKey());

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
        int t = lhs.getAppName().compareTo(rhs.getAppName());
        if(t==0)
            return lhs.getPID()-rhs.getPID();
        return t;
    }


}
