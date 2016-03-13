package in.ac.iitp.remoteaccess.model;

/**
 * Created by scopeinfinity on 13/3/16.
 */
public class LogModel {
    private String appName;
    private int PID;

    public LogModel(String name, int PID) {
        this.appName = name;
        this.PID = PID;
    }

    public int getPID() {
        return PID;
    }

    public String getAppName() {
        return appName;
    }
}
