package org.onosproject.tiessemanager.impl.config;

/**
 * Trunk mode data representation for vlans.
 */

public class TrunkData {

    private String port;
    private String vlan;
    private String ipaddress;
    private String netmask;

    public TrunkData() {
    }

    public TrunkData(String port, String vlan, String ipaddress, String netmask) {
        this.port = port;
        this.vlan = vlan;
        this.ipaddress = ipaddress;
        this.netmask = netmask;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getVlan() {
        return vlan;
    }

    public void setVlan(String vlan) {
        this.vlan = vlan;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public boolean isEmpty() {
        return port == null && vlan == null && ipaddress == null && netmask == null;
    }
}
