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
import org.onosproject.yang.gen.v1.tiessebridge.rev20170225.TiesseBridgeOpParam;
import org.onosproject.yang.gen.v1.tiessebridge.rev20170225.tiessebridge.Bridge;
import org.onosproject.yang.gen.v1.tiessebridge.rev20170225.tiessebridge.DefaultBridge;
import org.onosproject.yang.gen.v1.tiessebridge.rev20170225.tiessebridge.bridge.Br;
import org.onosproject.yang.gen.v1.tiessebridge.rev20170225.tiessebridge.bridge.DefaultBr;
import org.onosproject.yang.gen.v1.tiessebridge.rev20170225.tiessebridge.bridge.br.DefaultYangAutoPrefixInterface;
import org.onosproject.yang.gen.v1.tiessebridge.rev20170225.tiessebridge.bridge.br.YangAutoPrefixInterface;
import org.onosproject.yang.gen.v1.tiessecli.rev20170703.tiessecli.Onoff;
import org.onosproject.yang.gen.v1.tiessecli.rev20170703.tiessecli.TsInterfaces;
import org.onosproject.yang.gen.v1.tiessecli.rev20170703.tiessecli.tsinterfaces.TsInterfacesUnion;
import org.onosproject.yang.gen.v1.tiessecli.rev20170703.tiessecli.tsinterfaces.tsinterfacesunion.TsInterfacesUnionEnum1;
import org.onosproject.yang.gen.v1.tiesseethernet.rev20170523.TiesseEthernetOpParam;
import org.onosproject.yang.gen.v1.tiesseethernet.rev20170523.tiesseethernet.*;
import org.onosproject.yang.gen.v1.tiesseethernet.rev20170523.tiesseethernet.ethparams.AccessVlan;
import org.onosproject.yang.gen.v1.tiesseethernet.rev20170523.tiesseethernet.ethparams.DefaultAccessVlan;
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
 * Configures interfaces on Tiesse devices.
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

        //log.info("Inside addAccessMode() method");

         /*
        Example of commands to configure the port in access mode (lan splitting off):

        set switch port 1 mode access
        set switch port 1 vid 10
        set switch on

         */

        //Configuration for LAN splitting off (NOT USED since is not supported yet from Tiesse Imola)

        /*
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
        */


        //Configuration for LAN splitting on (USED)

        /*
        Example of commands to configure the port in access mode (lan splitting on):

        set eth1 access-vlan vid 50
        set eth1 on
        */

        TiesseEthernetOpParam tiesseEthernet = new TiesseEthernetOpParam();

        if(intf.equals("eth0")) {
            Eth0 eth0 = new DefaultEth0();
            AccessVlan accessVlan = new DefaultAccessVlan();
            String vlanIdString = vlanId.toString(); //parse from short to string
            Long vidLong = Long.parseLong(vlanIdString); //parse from string to long
            accessVlan.vid(vidLong);

            eth0.accessVlan(accessVlan);
            eth0.active(Onoff.fromString("on"));
            tiesseEthernet.eth0(eth0);
        }

        if(intf.equals("eth1")) {
            Eth1 eth1 = new DefaultEth1();
            AccessVlan accessVlan = new DefaultAccessVlan();
            String vlanIdString = vlanId.toString(); //parse from short to string
            Long vidLong = Long.parseLong(vlanIdString); //parse from string to long
            accessVlan.vid(vidLong);

            eth1.accessVlan(accessVlan);
            eth1.active(Onoff.fromString("on"));
            tiesseEthernet.eth1(eth1);
        }

        if(intf.equals("eth2")) {
            Eth2 eth2 = new DefaultEth2();
            AccessVlan accessVlan = new DefaultAccessVlan();
            String vlanIdString = vlanId.toString(); //parse from short to string
            Long vidLong = Long.parseLong(vlanIdString); //parse from string to long
            accessVlan.vid(vidLong);

            eth2.accessVlan(accessVlan);
            eth2.active(Onoff.fromString("on"));
            tiesseEthernet.eth2(eth2);
        }

        if(intf.equals("eth3")) {
            Eth3 eth3 = new DefaultEth3();
            AccessVlan accessVlan = new DefaultAccessVlan();
            String vlanIdString = vlanId.toString(); //parse from short to string
            Long vidLong = Long.parseLong(vlanIdString); //parse from string to long
            accessVlan.vid(vidLong);

            eth3.accessVlan(accessVlan);
            eth3.active(Onoff.fromString("on"));
            tiesseEthernet.eth3(eth3);
        }

        if(intf.equals("eth4")) {
            Eth4 eth4 = new DefaultEth4();
            AccessVlan accessVlan = new DefaultAccessVlan();
            String vlanIdString = vlanId.toString(); //parse from short to string
            Long vidLong = Long.parseLong(vlanIdString); //parse from string to long
            accessVlan.vid(vidLong);

            eth4.accessVlan(accessVlan);
            eth4.active(Onoff.fromString("on"));
            tiesseEthernet.eth4(eth4);
        }

        if(intf.equals("eth5")) {
            Eth5 eth5 = new DefaultEth5();
            AccessVlan accessVlan = new DefaultAccessVlan();
            String vlanIdString = vlanId.toString(); //parse from short to string
            Long vidLong = Long.parseLong(vlanIdString); //parse from string to long
            accessVlan.vid(vidLong);

            eth5.accessVlan(accessVlan);
            eth5.active(Onoff.fromString("on"));
            tiesseEthernet.eth5(eth5);
        }


        //log.info("tiesseEthernet object configured");
        boolean reply;
        try {
            //log.info("Calling interfaceConfigTiesseNetconfService.setTiesseEthernet() ");
            //reply = interfaceConfigTiesseNetconfService.setTiesseSwitch(tiesseSwitch, session, DatastoreId.RUNNING);
            reply = interfaceConfigTiesseNetconfService.setTiesseEthernet(tiesseEthernet, session, DatastoreId.RUNNING, intf);
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

        //IMPORTANT: With LAN splitting ON, this method is not to be called.
        //           Trunk Mode is implicit with LAN splitting ON.
        //           Just need to give the command: set vlan add vid x interface y
        //           This makes that interface accept packets with vlan id x, as if it was a trunk port.


        /*
        Example of commands to configure the port in trunk mode(lan splitting off):

        set switch port 1 mode trunk
        set switch port 1 allow vid 2
        set switch port 1 allow vid 3
        set switch port 1 allow vid 4
        set switch on

        */

        //This method is NOT USED since is not supported yet from Tiesse Imola

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
     * Adds a VLAN to an interface.
     * Assign IP address, netmask and broadcast to that VLAN sub-interface.
     *
     * @param intf the name of the interface
     * @param vlanId the VLAN ID
     * @param ipAddress the ip Address to assign
     * @param netmask the netmask to assign
     * @return the result of operation
     */


    @Override
    public boolean addVlanAndIpAddrAndNetmaskToInterface(String intf, VlanId vlanId, String ipAddress, String netmask, String broadcast) {

        NetconfController controller = checkNotNull(handler()
                .get(NetconfController.class));

        NetconfSession session = controller.getDevicesMap().get(handler()
                .data().deviceId()).getSession();

        InterfaceConfigTiesseNetconfService interfaceConfigTiesseNetconfService =
                (InterfaceConfigTiesseNetconfService) checkNotNull(handler().get(InterfaceConfigTiesseNetconfService.class));

        /*
        Example of commands to configure vlan and associate ip address and netmask to the sub-interface:

        set vlan add vid 10 interface eth1

        set vlan eth1.10 ipaddr 192.168.10.1 netmask 255.255.255.0 broadcast 192.168.10.255

        */

        //Configuration of interface with VLAN
        //log.info("Inside IpAddrAndNetmaskToInterface() method");
        TiesseVlanOpParam tiesseVlan = new TiesseVlanOpParam();
        Vlan vlan = new DefaultVlan();
        Vlans vlans = new DefaultVlans();



        String vlanIdString = vlanId.toString(); //parse from short to string
        Long vidLong = Long.parseLong(vlanIdString); //parse from string to long
        vlans.vid(vidLong); // set vlan id
        vlans.protocol("802.1q");
        vlans.yangAutoPrefixInterface(TsInterfaces.fromString(intf)); //set interface = intf


        Ipv4Address ipAddr = Ipv4Address.fromString(ipAddress);
        Netmask netmaskVar = Netmask.fromString(netmask);
        Ipv4Address ipBroadcast = Ipv4Address.fromString(broadcast);
        vlans.ipaddr(ipAddr);
        vlans.netmask(netmaskVar);
        vlans.broadcast(ipBroadcast);


        vlans.active(Onoff.fromString("on"));

        vlan.addToVlans(vlans); //add this vlan to the list of vlans
        tiesseVlan.vlan(vlan);

        boolean reply;
        try {
            //log.info("Calling setTiesseVlan()");
            reply = interfaceConfigTiesseNetconfService.setTiesseVlan(tiesseVlan, session, DatastoreId.RUNNING);
            //String reply =  setNetconfObject(mo, session, DatastoreId.RUNNING, null);
        } catch (NetconfException e) {
            log.error("Failed to configure VLAN ID {} on device {} interface {}.",
                    vlanId, handler().data().deviceId(), intf, e);
            return false;
        }

        return reply;
    }


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
    @Override
    public boolean addBridge(String bridgeName, String intf1, String intf2, String ipAddress, String netmask) {

        NetconfController controller = checkNotNull(handler()
                .get(NetconfController.class));

        NetconfSession session = controller.getDevicesMap().get(handler()
                .data().deviceId()).getSession();

        InterfaceConfigTiesseNetconfService interfaceConfigTiesseNetconfService =
                (InterfaceConfigTiesseNetconfService) checkNotNull(handler().get(InterfaceConfigTiesseNetconfService.class));

        TiesseBridgeOpParam tiesseBridge = new TiesseBridgeOpParam();
        Bridge bridge = new DefaultBridge();
        Br br = new DefaultBr();

        br.name(bridgeName); //set br name (e.g.: br0)

        Ipv4Address ipAddr = Ipv4Address.fromString(ipAddress);
        Netmask netmaskVar = Netmask.fromString(netmask);
        br.ipaddr(ipAddr); //add ip addr and netmask of the br
        br.netmask(netmaskVar);

        YangAutoPrefixInterface firstIntf = new DefaultYangAutoPrefixInterface();
        firstIntf.name(intf1);
        YangAutoPrefixInterface secondIntf = new DefaultYangAutoPrefixInterface();
        secondIntf.name(intf2);
        br.addToYangAutoPrefixInterface(firstIntf); //set first intf of the br
        br.addToYangAutoPrefixInterface(secondIntf); //set second intf of the br

        br.active(Onoff.fromString("on"));

        bridge.addToBr(br); //add this bridge to the list of br
        tiesseBridge.bridge(bridge);

        boolean reply;
        try {
            //reply = session.requestSync(addAccessModeBuilder(intf, vlanId));
            //log.info("Calling setTiesseBridge()");
            reply = interfaceConfigTiesseNetconfService.setTiesseBridge(tiesseBridge, session, DatastoreId.RUNNING);


            //String reply =  setNetconfObject(mo, session, DatastoreId.RUNNING, null);
        } catch (NetconfException e) {
            log.error("Failed to configure bridge {} on device {} with interface {} and interface {}.",
                    bridgeName, handler().data().deviceId(), intf1, intf2, e);
            return false;
        }

        return reply;
    }

    //
    //TODO: The methods below are not implemented yet, since they are not supported by Tiesse Imola.
    //

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

