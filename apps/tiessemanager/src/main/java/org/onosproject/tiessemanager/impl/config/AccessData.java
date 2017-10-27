package org.onosproject.tiessemanager.impl.config;

public class AccessData {

    private String port;
    private String vlan;
    private String ipaddress;
    private String netmask;


    public AccessData() {
    }

    public AccessData(String port, String vlan, String ipaddress, String netmask) {

        this.port = port;
        this.vlan = vlan;
        this.ipaddress = ipaddress;
        this.netmask = netmask;
    }


    public void setPort(String port) {
        this.port = port;
    }

    public void setVlan(String vlan) {
        this.vlan = vlan;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getPort() {

        return port;
    }

    public String getVlan() {
        return vlan;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public String getNetmask() {
        return netmask;
    }

    public boolean isEmpty() {
        return port == null && vlan == null && ipaddress == null && netmask == null;
    }

}
