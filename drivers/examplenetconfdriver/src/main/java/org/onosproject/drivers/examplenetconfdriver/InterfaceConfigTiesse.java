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

    /**
     * Adds a bridge that connects two interfaces.
     * (e.g.: set bridge br0
     *        set bridge br0 interface eth1.77
     *        set bridge br0 interface eth2.88
     *        set bridge br0 ipaddr 1.2.3.4 netmask 255.255.255.0
     *        set bridge br0 on)
     * @param bridgeName the name of the bridge to create
     * @param intf1 the name of the first interface to add to the bridge
     * @param intf2 the name of the second interface to add to the bridge
     * @param ipAddress ip address to assign to the bridge
     * @param netmask netmask to assign to the bridge
     * @return the result of operation
     */
    boolean addBridge(String bridgeName, String intf1, String intf2, String ipAddress, String netmask);

}
