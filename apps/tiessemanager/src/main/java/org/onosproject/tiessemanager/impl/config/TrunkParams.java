package org.onosproject.tiessemanager.impl.config;

public class TrunkParams {

    private String vlan;
    private String ipaddress;
    private String netmask;

    public TrunkParams(String vlan, String ipaddress, String netmask) {
        this.vlan = vlan;
        this.ipaddress = ipaddress;
        this.netmask = netmask;
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
}
