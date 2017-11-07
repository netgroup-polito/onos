package org.onosproject.tiessemanager.impl.config;


/**
 * Access mode data representation for vlans.
 */

public class AccessData {

    private String intf;
    private String port;
    private String vlan;
    private String ipaddress;
    private String netmask;


    public AccessData() {
    }

    public AccessData(String intf, String port, String vlan, String ipaddress, String netmask) {

        this.intf = intf;
        this.port = port;
        this.vlan = vlan;
        this.ipaddress = ipaddress;
        this.netmask = netmask;
    }

    public String getIntf() {
        return intf;
    }

    public void setIntf(String intf) {
        this.intf = intf;
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
        return intf == null && port == null && vlan == null && ipaddress == null && netmask == null;
    }

}
