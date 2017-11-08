package org.onosproject.drivers.examplenetconfdriver;

import org.onlab.packet.VlanId;
import org.onosproject.net.behaviour.InterfaceConfig;

/**
 * Interface that extends InterfaceConfig for interfaces' configuration on Tiesse devices.
 */

public interface InterfaceConfigTiesse extends InterfaceConfig{


    /**
     * Adds a VLAN to an interface.
     * Adds IP address, netmask and broadcast to that vlan sub-interface
     * (e.g.: set vlan eth1.10 ipaddr 192.168.10.1 netmask 255.255.255.0  broadcast 192.168.10.255)
     *
     * @param intf the name of the interface
     * @param vlanId the VLAN ID
     * @return the result of operation
     */
    boolean addVlanAndIpAddrAndNetmaskToInterface(String intf, VlanId vlanId, String ipAddress, String netmask, String broadcast);

}
