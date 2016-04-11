package in.ac.iitp.remoteaccess.model;

/**
 * Created by scopeinfinity on 6/4/16.
 */
public class DeviceModel {
    private String deviceName;
    private String IP;

    public DeviceModel(String name, String IP) {
        this.deviceName = name;
        this.IP = IP;
    }

    public String getIP() {
        return IP;
    }

    public String getDeviceName() {
        return deviceName;
    }
}

