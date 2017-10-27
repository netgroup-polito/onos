package org.onosproject.drivers.examplenetconfdriver;

import org.onlab.packet.VlanId;
import org.onosproject.net.behaviour.InterfaceConfig;

/**
 * Interface that extends InterfaceConfig for interfaces' configuration on Tiesse devices.
 */

public interface InterfaceConfigTiesse extends InterfaceConfig{


    /**
     * Adds IP address and netmask to the vlan sub-interface (e.g.: set vlan eth1.10 ipaddr 192.168.10.1 netmask 255.255.255.0)
     *
     * @param intf the name of the interface
     * @param vlanId the VLAN ID
     * @return the result of operation
     */
    boolean addIpAddrAndNetmaskToInterface(String intf, VlanId vlanId, String ipAddress, String netmask);

}
