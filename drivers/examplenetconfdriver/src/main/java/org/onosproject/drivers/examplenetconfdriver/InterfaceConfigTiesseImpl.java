/*
 * Copyright 2016-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.onosproject.drivers.examplenetconfdriver;

import org.onlab.packet.VlanId;
import org.onosproject.drivers.examplenetconfdriver.yang.InterfaceConfigTiesseNetconfService;
import org.onosproject.drivers.utilities.XmlConfigParser;
import org.onosproject.net.behaviour.PatchDescription;
import org.onosproject.net.behaviour.TunnelDescription;
import org.onosproject.net.device.DeviceInterfaceDescription;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onosproject.netconf.DatastoreId;
import org.onosproject.netconf.NetconfController;
import org.onosproject.netconf.NetconfException;
import org.onosproject.netconf.NetconfSession;
import org.onosproject.yang.gen.v1.tiessecli.rev20170703.tiessecli.Onoff;
import org.onosproject.yang.gen.v1.tiessecli.rev20170703.tiessecli.TsInterfaces;
import org.onosproject.yang.gen.v1.tiessecli.rev20170703.tiessecli.tsinterfaces.TsInterfacesUnion;
import org.onosproject.yang.gen.v1.tiessecli.rev20170703.tiessecli.tsinterfaces.tsinterfacesunion.TsInterfacesUnionEnum1;
import org.onosproject.yang.gen.v1.tiesseip.rev20170521.tiesseip.Ipv4Address;
import org.onosproject.yang.gen.v1.tiesseip.rev20170521.tiesseip.Netmask;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.TiesseSwitchOpParam;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.tiesseswitch.DefaultYangAutoPrefixSwitch;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.tiesseswitch.YangAutoPrefixSwitch;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.tiesseswitch.switchportset.Allow;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.tiesseswitch.switchportset.DefaultAllow;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.tiesseswitch.switchportset.ModeEnum;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.tiesseswitch.yangautoprefixswitch.DefaultPort;
import org.onosproject.yang.gen.v1.tiessevlan.rev20170225.TiesseVlanOpParam;
import org.onosproject.yang.gen.v1.tiessevlan.rev20170225.tiessevlan.DefaultVlan;
import org.onosproject.yang.gen.v1.tiessevlan.rev20170225.tiessevlan.Vlan;
import org.onosproject.yang.gen.v1.tiessevlan.rev20170225.tiessevlan.vlan.DefaultVlans;
import org.onosproject.yang.gen.v1.tiessevlan.rev20170225.tiessevlan.vlan.Vlans;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.tiesseswitch.yangautoprefixswitch.Port;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Configures interfaces on Cisco IOS devices.
 */
public class InterfaceConfigTiesseImpl extends AbstractHandlerBehaviour
        implements InterfaceConfigTiesse {

    private final Logger log = getLogger(getClass());

    /**
     * Adds an access interface to a VLAN.
     *
     * @param intf the name of the interface
     * @param vlanId the VLAN ID
     * @return the result of operation
     */
    @Override
    public boolean addAccessMode(String intf, VlanId vlanId) {
        NetconfController controller = checkNotNull(handler()
                                       .get(NetconfController.class));

        NetconfSession session = controller.getDevicesMap().get(handler()
                                 .data().deviceId()).getSession();

        InterfaceConfigTiesseNetconfService interfaceConfigTiesseNetconfService =
                (InterfaceConfigTiesseNetconfService) checkNotNull(handler().get(InterfaceConfigTiesseNetconfService.class));

        /*

        Es di comandi per configurare la porta in access mode:

        set switch port 1 mode access
        set switch port 1 vid 10
        set switch on

        <---This method implements just the commands on top of this comment--->

        <---The commands below are implemented in addIpAddrAndNetmaskToInterface() method--->

        set vlan add vid 10 interface eth1

        set vlan eth1.10 ipaddr 192.168.10.1 netmask 255.255.255.0

         */

        //Configuration of switch port, vid and access mode
        TiesseSwitchOpParam tiesseSwitch = new TiesseSwitchOpParam();

        Port tiessePort = new DefaultPort();
        //tiessePort.name("1");
        tiessePort.name(intf); //set port name
        tiessePort.mode(ModeEnum.ACCESS); //set port mode to Access mode

        String vlanIdString = vlanId.toString(); //parse from short to string
        Long vidLong = Long.parseLong(vlanIdString); //parse from string to long
        tiessePort.vid(vidLong); //set port vid
        YangAutoPrefixSwitch yangAutoPrefixSwitch = new DefaultYangAutoPrefixSwitch();
        yangAutoPrefixSwitch.addToPort(tiessePort); //add port to switch
        yangAutoPrefixSwitch.active(Onoff.fromString("on")); // set switch on

        tiesseSwitch.yangAutoPrefixSwitch(yangAutoPrefixSwitch);

        boolean reply;
        try {
            //reply = session.requestSync(addAccessModeBuilder(intf, vlanId));
            reply = interfaceConfigTiesseNetconfService.setTiesseSwitch(tiesseSwitch, session, DatastoreId.RUNNING);
            //String reply =  setNetconfObject(mo, session, DatastoreId.RUNNING, null);
        } catch (NetconfException e) {
            log.error("Failed to configure VLAN ID {} on device {} interface {}.",
                      vlanId, handler().data().deviceId(), intf, e);
            return false;
        }

        return reply;
    }


    /**
     *  Adds a trunk interface for VLANs.
     *
     * @param intf the name of the interface
     * @param vlanIds the VLAN IDs
     * @return the result of operation
     */
    @Override
    public boolean addTrunkMode(String intf, List<VlanId> vlanIds) {
        /*
        Es di comandi per configurare la porta in trunk mode:

        set switch port 1 mode trunk
        set switch port 1 allow vid 2
        set switch port 1 allow vid 3
        set switch port 1 allow vid 4
        set switch on

        <---This method implements just the commands on top of this comment--->

        <---The commands below are implemented in addIpAddrAndNetmaskToInterface() method--->

        set vlan add vid 2 interface eth1
        set vlan add vid 3 interface eth1
        set vlan add vid 4 interface eth1

        set vlan eth1.2 ipaddr 192.168.2.1 netmask 255.255.255.0
        set vlan eth1.3 ipaddr 192.168.3.1 netmask 255.255.255.0
        set vlan eth1.4 ipaddr 192.168.4.1 netmask 255.255.255.0


        */

        NetconfController controller = checkNotNull(handler()
                .get(NetconfController.class));

        NetconfSession session = controller.getDevicesMap().get(handler()
                .data().deviceId()).getSession();

        InterfaceConfigTiesseNetconfService interfaceConfigTiesseNetconfService =
                (InterfaceConfigTiesseNetconfService) checkNotNull(handler().get(InterfaceConfigTiesseNetconfService.class));


        //Configuration of switch port, vid and trunk mode
        TiesseSwitchOpParam tiesseSwitch = new TiesseSwitchOpParam();

        Port tiessePort = new DefaultPort();


        //tiessePort.name("1");
        tiessePort.name(intf); //set port name
        tiessePort.mode(ModeEnum.TRUNK); //set port mode to Trunk mode


        Allow allow = new DefaultAllow();
        for(VlanId vlanId: vlanIds) {
            String vlanIdString = vlanId.toString(); //parse from short to string
            Long vidLong = Long.parseLong(vlanIdString); //parse from string to long
            allow.addToVid(vidLong); //add list of vid to allow in the trunk mode port
        }
        tiessePort.allow(allow); //set list of vid to allow

        YangAutoPrefixSwitch yangAutoPrefixSwitch = new DefaultYangAutoPrefixSwitch();
        yangAutoPrefixSwitch.addToPort(tiessePort); //add port to switch
        yangAutoPrefixSwitch.active(Onoff.fromString("on")); // set switch on

        tiesseSwitch.yangAutoPrefixSwitch(yangAutoPrefixSwitch);

        boolean reply;
        try {
            //reply = session.requestSync(addAccessModeBuilder(intf, vlanId));
            reply = interfaceConfigTiesseNetconfService.setTiesseSwitch(tiesseSwitch, session, DatastoreId.RUNNING);
            //String reply =  setNetconfObject(mo, session, DatastoreId.RUNNING, null);
        } catch (NetconfException e) {
            log.error("Failed to configure VLAN ID list for trunk mode on device {} interface {}.",
                    handler().data().deviceId(), intf, e);
            return false;
        }

        return reply;
    }


    /**
     * Adds IP address and netmask to a VLAN sub-interface.
     *
     * @param intf the name of the interface
     * @param vlanId the VLAN ID
     * @param ipAddress the ip Address to assign
     * @param netmask the netmask to assign
     * @return the result of operation
     */


    @Override
    public boolean addIpAddrAndNetmaskToInterface(String intf, VlanId vlanId, String ipAddress, String netmask) {

        NetconfController controller = checkNotNull(handler()
                .get(NetconfController.class));

        NetconfSession session = controller.getDevicesMap().get(handler()
                .data().deviceId()).getSession();

        InterfaceConfigTiesseNetconfService interfaceConfigTiesseNetconfService =
                (InterfaceConfigTiesseNetconfService) checkNotNull(handler().get(InterfaceConfigTiesseNetconfService.class));

        /*

        Es di comandi per configurare la vlan e associare ip e netmask alla sottinterfaccia:

        set vlan add vid 10 interface eth1

        set vlan eth1.10 ipaddr 192.168.10.1 netmask 255.255.255.0

        <---This method implements the commands on top of this comment--->
        */

        //Configuration of interface with VLAN

        TiesseVlanOpParam tiesseVlan = new TiesseVlanOpParam();
        Vlan vlan = new DefaultVlan();
        Vlans vlans = new DefaultVlans();

        tiesseVlan.vlan(vlan); //add

        String vlanIdString = vlanId.toString(); //parse from short to string
        Long vidLong = Long.parseLong(vlanIdString); //parse from string to long
        vlans.vid(vidLong); // set vlan id
        vlans.protocol("802.1q");
        TsInterfacesUnionEnum1 tsInterfacesUnionEnum1 = TsInterfacesUnionEnum1.of(intf); //es: intf = ETH0 or intf = ETH1
        TsInterfacesUnion tsInterfacesUnion = new TsInterfacesUnion(tsInterfacesUnionEnum1);
        TsInterfaces tsInterface = new TsInterfaces(tsInterfacesUnion);
        vlans.yangAutoPrefixInterface(tsInterface); //set interface intf


        //Ipv4Address ipAddr = Ipv4Address.fromString("192.168.100.1");
        //Netmask netmask = Netmask.fromString("255.255.255.0");
        Ipv4Address ipAddr = Ipv4Address.fromString(ipAddress);
        Netmask netmaskVar = Netmask.fromString(netmask);


        vlans.ipaddr(ipAddr);
        vlans.netmask(netmaskVar);


        tiesseVlan.vlan().addToVlans(vlans); //add this vlan to the list of vlans


        boolean reply;
        try {
            //reply = session.requestSync(addAccessModeBuilder(intf, vlanId));
            reply = interfaceConfigTiesseNetconfService.setTiesseVlan(tiesseVlan, session, DatastoreId.RUNNING);
            //String reply =  setNetconfObject(mo, session, DatastoreId.RUNNING, null);
        } catch (NetconfException e) {
            log.error("Failed to configure VLAN ID {} on device {} interface {}.",
                    vlanId, handler().data().deviceId(), intf, e);
            return false;
        }

        return reply;
    }

    //TODO: Implement all the methods under this todo for the Tiesse


    /**
     * Builds a request to add an access interface to a VLAN.
     *
     * @param intf the name of the interface
     * @param vlanId the VLAN ID
     * @return the request string.
     */
    private String addAccessModeBuilder(String intf, VlanId vlanId) {
        StringBuilder rpc = new StringBuilder(getOpeningString(intf));
        rpc.append("<switchport><access><vlan><VLANIDVLANPortAccessMode>");
        rpc.append(vlanId);
        rpc.append("</VLANIDVLANPortAccessMode></vlan></access></switchport>");
        rpc.append("<switchport><mode><access/></mode></switchport>");
        rpc.append(getClosingString());

        return rpc.toString();
    }

    /**
     * Removes an access interface to a VLAN.
     *
     * @param intf the name of the interface
     * @return the result of operation
     */

    @Override
    public boolean removeAccessMode(String intf) {
        NetconfController controller = checkNotNull(handler()
                                                            .get(NetconfController.class));

        NetconfSession session = controller.getDevicesMap().get(handler()
                                 .data().deviceId()).getSession();
        String reply;
        try {
            reply = session.requestSync(removeAccessModeBuilder(intf));
        } catch (NetconfException e) {
            log.error("Failed to remove access mode from device {} interface {}.",
                      handler().data().deviceId(), intf, e);
            return false;
        }

        return XmlConfigParser.configSuccess(XmlConfigParser.loadXml(
                new ByteArrayInputStream(reply.getBytes(StandardCharsets.UTF_8))));
    }

    /**
     * Builds a request to remove an access interface from a VLAN.
     *
     * @param intf the name of the interface
     * @return the request string.
     */
    private String removeAccessModeBuilder(String intf) {
        StringBuilder rpc = new StringBuilder(getOpeningString(intf));
        rpc.append("<switchport operation=\"delete\"><access><vlan><VLANIDVLANPortAccessMode>");
        rpc.append("</VLANIDVLANPortAccessMode></vlan></access></switchport>");
        rpc.append("<switchport operation=\"delete\"><mode><access/></mode></switchport>");
        rpc.append(getClosingString());

        return rpc.toString();
    }

    /**
     * Builds a request to configure an interface as trunk for VLANs.
     *
     * @param intf the name of the interface
     * @param vlanIds the VLAN IDs
     * @return the request string.
     */
    private String addTrunkModeBuilder(String intf, List<VlanId> vlanIds) {
        StringBuilder rpc = new StringBuilder(getOpeningString(intf));
        rpc.append("<switchport><trunk><encapsulation><dot1q/></encapsulation>");
        rpc.append("</trunk></switchport><switchport><trunk><allowed><vlan>");
        rpc.append("<VLANIDsAllowedVLANsPortTrunkingMode>");
        rpc.append(getVlansString(vlanIds));
        rpc.append("</VLANIDsAllowedVLANsPortTrunkingMode></vlan></allowed></trunk>");
        rpc.append("</switchport><switchport><mode><trunk/></mode></switchport>");
        rpc.append(getClosingString());

        return rpc.toString();
    }

    /**
     * Removes trunk mode configuration from an interface.
     *
     * @param intf the name of the interface
     * @return the result of operation
     */
    @Override
    public boolean removeTrunkMode(String intf) {
        NetconfController controller = checkNotNull(handler()
                                       .get(NetconfController.class));

    NetconfSession session = controller.getDevicesMap().get(handler()
                             .data().deviceId()).getSession();
    String reply;
    try {
        reply = session.requestSync(removeTrunkModeBuilder(intf));
    } catch (NetconfException e) {
        log.error("Failed to remove trunk mode from device {} interface {}.",
                  handler().data().deviceId(), intf, e);
        return false;
    }

    return XmlConfigParser.configSuccess(XmlConfigParser.loadXml(
            new ByteArrayInputStream(reply.getBytes(StandardCharsets.UTF_8))));
}

    /**
     * Builds a request to remove trunk mode configuration from an interface.
     *
     * @param intf the name of the interface
     * @return the request string.
     */
    private String removeTrunkModeBuilder(String intf) {
        StringBuilder rpc = new StringBuilder(getOpeningString(intf));
        rpc.append("<switchport><mode operation=\"delete\"><trunk/></mode></switchport>");
        rpc.append("<switchport><trunk operation=\"delete\"><encapsulation>");
        rpc.append("<dot1q/></encapsulation></trunk></switchport>");
        rpc.append("<switchport><trunk operation=\"delete\"><allowed><vlan>");
        rpc.append("<VLANIDsAllowedVLANsPortTrunkingMode>");
        rpc.append("</VLANIDsAllowedVLANsPortTrunkingMode></vlan></allowed>");
        rpc.append("</trunk></switchport>");
        rpc.append(getClosingString());

        return rpc.toString();
    }

    /**
     * Adds a rate limit on an interface.
     *
     * @param intf the name of the interface
     * @param limit the limit as a percentage
     * @return the result of operation
     */
    @Override
    public boolean addRateLimit(String intf, short limit) {
        NetconfController controller = checkNotNull(handler()
                                       .get(NetconfController.class));

        NetconfSession session = controller.getDevicesMap().get(handler()
                                 .data().deviceId()).getSession();
        String reply;
        try {
            reply = session.requestSync(addRateLimitBuilder(intf, limit));
        } catch (NetconfException e) {
            log.error("Failed to configure rate limit {}%% on device {} interface {}.",
                      limit, handler().data().deviceId(), intf, e);
            return false;
        }

        return XmlConfigParser.configSuccess(XmlConfigParser.loadXml(
                new ByteArrayInputStream(reply.getBytes(StandardCharsets.UTF_8))));
    }

    /**
     * Builds a request to configure an interface with rate limit.
     *
     * @param intf the name of the interface
     * @param limit the limit as a percentage
     * @return the request string.
     */
    private String addRateLimitBuilder(String intf, short limit) {
        StringBuilder rpc = new StringBuilder(getOpeningString(intf));
        rpc.append("<srr-queue><bandwidth><limit>");
        rpc.append("<EnterBandwidthLimitInterfaceAsPercentage>");
        rpc.append(limit);
        rpc.append("</EnterBandwidthLimitInterfaceAsPercentage>");
        rpc.append("</limit></bandwidth></srr-queue>");
        rpc.append(getClosingString());

        return rpc.toString();
    }

    /**
     * Removes rate limit from an interface.
     *
     * @param intf the name of the interface
     * @return the result of operation
     */
    @Override
    public boolean removeRateLimit(String intf) {
        NetconfController controller = checkNotNull(handler()
                                       .get(NetconfController.class));

        NetconfSession session = controller.getDevicesMap().get(handler()
                                 .data().deviceId()).getSession();
        String reply;
        try {
            reply = session.requestSync(removeRateLimitBuilder(intf));
        } catch (NetconfException e) {
            log.error("Failed to remove rate limit from device {} interface {}.",
                      handler().data().deviceId(), intf, e);
            return false;
        }

        return XmlConfigParser.configSuccess(XmlConfigParser.loadXml(
                new ByteArrayInputStream(reply.getBytes(StandardCharsets.UTF_8))));
    }

    /**
     * Builds a request to remove a rate limit from an interface.
     *
     * @param intf the name of the interface
     * @return the request string.
     */
    private String removeRateLimitBuilder(String intf) {
        StringBuilder rpc = new StringBuilder(getOpeningString(intf));
        rpc.append("<srr-queue operation=\"delete\"><bandwidth><limit>");
        rpc.append("</limit></bandwidth></srr-queue>");
        rpc.append(getClosingString());

        return rpc.toString();
    }

    /**
     * Builds the opening of a request for the configuration of an interface.
     *
     * @param intf the interface to be configured
     * @return the opening string
     */
    private String getOpeningString(String intf) {
        StringBuilder rpc =
                new StringBuilder("<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" ");
        rpc.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        rpc.append("<edit-config>");
        rpc.append("<target>");
        rpc.append("<running/>");
        rpc.append("</target>");
        rpc.append("<config>");
        rpc.append("<xml-config-data>");
        rpc.append("<Device-Configuration><interface><Param>");
        rpc.append(intf);
        rpc.append("</Param>");
        rpc.append("<ConfigIf-Configuration>");

        return rpc.toString();
    }

    /**
     * Builds the closing of a request for the configuration of an interface.
     *
     * @return the closing string
     */
    private String getClosingString() {
        StringBuilder rpc = new StringBuilder("</ConfigIf-Configuration>");
        rpc.append("</interface>");
        rpc.append("</Device-Configuration>");
        rpc.append("</xml-config-data>");
        rpc.append("</config>");
        rpc.append("</edit-config>");
        rpc.append("</rpc>");

        return rpc.toString();
    }

    /**
     * Builds a string with comma separated VLAN-IDs.
     *
     * @param vlanIds the VLAN IDs
     * @return the string including the VLAN-IDs
     */
    private String getVlansString(List<VlanId> vlanIds) {
        StringBuilder vlansStringBuilder = new StringBuilder();

        for (int i = 0; i < vlanIds.size(); i++) {
            vlansStringBuilder.append(vlanIds.get(i));

            if (i != vlanIds.size() - 1) {
                vlansStringBuilder.append(",");
            }
        }
        return  vlansStringBuilder.toString();
    }

    /**
     * Provides the interfaces configured on a device.
     *
     * @return the list of the configured interfaces
     */
    @Override
    public List<DeviceInterfaceDescription> getInterfaces() {
        NetconfController controller =
                checkNotNull(handler().get(NetconfController.class));

        NetconfSession session = controller.getDevicesMap().get(handler()
                                 .data().deviceId()).getSession();
        String reply;
        try {
            reply = session.requestSync(getConfigBuilder());
        } catch (NetconfException e) {
            log.error("Failed to retrieve configuration from device {}.",
                      handler().data().deviceId(), e);
            return null;
        }

        //return XmlParserCisco.getInterfacesFromConfig(XmlConfigParser.loadXml(
        //        new ByteArrayInputStream(reply.getBytes(StandardCharsets.UTF_8))));

        return null; //TODO:return the right interfaces list
    }

    /**
     * Builds a request for getting configuration from device.
     *
     * @return the request string.
     */
    private String getConfigBuilder() {
        StringBuilder rpc =
                new StringBuilder("<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" ");
        rpc.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        rpc.append("<get-config>");
        rpc.append("<source>");
        rpc.append("<running/>");
        rpc.append("</source>");
        rpc.append("<filter>");
        rpc.append("<config-format-xml>");
        rpc.append("</config-format-xml>");
        rpc.append("</filter>");
        rpc.append("</get-config>");
        rpc.append("</rpc>");

        return rpc.toString();
    }

    @Override
    public boolean addTunnelMode(String ifaceName, TunnelDescription tunnelDesc) {
        throw new UnsupportedOperationException("Add tunnel mode is not supported");
    }

    @Override
    public boolean removeTunnelMode(String ifaceName) {
        throw new UnsupportedOperationException("Remove tunnel mode is not supported");
    }

    @Override
    public boolean addPatchMode(String ifaceName, PatchDescription patchDesc) {
        throw new UnsupportedOperationException("Add patch interface is not supported");
    }

    @Override
    public boolean removePatchMode(String ifaceName) {
        throw new UnsupportedOperationException("Remove patch interface is not supported");
    }


}

