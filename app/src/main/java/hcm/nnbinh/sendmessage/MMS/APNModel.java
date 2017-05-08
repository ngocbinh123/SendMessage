package hcm.nnbinh.sendmessage.MMS;

/**
 * Created by nguyenngocbinh on 5/7/17.
 */

public class APNModel {
    private String type;
    private String mmsc;
    private String proxy;
    private String port;

    public APNModel(String type, String mmsc, String proxy, String port) {
        this.type = type;
        this.mmsc = mmsc;
        this.proxy = proxy;
        this.port = port;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMmsc() {
        return mmsc;
    }

    public void setMmsc(String mmsc) {
        this.mmsc = mmsc;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
