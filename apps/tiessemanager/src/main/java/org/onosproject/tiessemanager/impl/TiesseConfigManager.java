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
 */

package org.onosproject.tiessemanager.impl;

import com.google.common.collect.ImmutableMap;
import org.apache.felix.scr.annotations.*;
import org.onlab.packet.VlanId;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;

import org.onosproject.drivers.examplenetconfdriver.InterfaceConfigTiesse;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.driver.DriverHandler;
import org.onosproject.net.driver.DriverService;
import org.onosproject.tiessemanager.impl.config.AccessData;
import org.onosproject.tiessemanager.impl.config.TiesseConfig;
import org.onosproject.tiessemanager.TiesseConfigService;
import org.onosproject.net.config.*;
import org.onosproject.net.config.basics.SubjectFactories;
import org.onosproject.tiessemanager.impl.config.TrunkData;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import static org.slf4j.LoggerFactory.getLogger;

/**
 * Implementation class of {@link org.onosproject.tiessemanager.TiesseConfigService}.
 */
@Component(immediate = true)
@Service
public class TiesseConfigManager implements TiesseConfigService {

    private static final String APP_NAME = "org.onosproject.app.tiessemanager";

    private final Logger log = getLogger(getClass());
    private ApplicationId appId;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected NetworkConfigRegistry configRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected NetworkConfigService configService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DriverService driverService;

    private ExecutorService eventExecutor = Executors.newSingleThreadExecutor();

    private final NetworkConfigListener configListener = new InternalConfigListener();

    private final ConfigFactory configFactory =
            new ConfigFactory(SubjectFactories.APP_SUBJECT_FACTORY, TiesseConfig.class, "vlansconfig") {
                @Override
                public TiesseConfig createConfig() {
                    return new TiesseConfig();
                }
            };

    private List<AccessData> accessModeDataList;
    private List<TrunkData> trunkModeDataList;
    private Map<String, List<VlanId>> trunkModePortVlanMap;
    private DeviceId deviceId;

    @Activate
    public void activate() {
        appId = coreService.getAppId(APP_NAME);
        configRegistry.registerConfigFactory(configFactory);
        configService.addListener(configListener);
        log.info("Started");
    }

    @Deactivate
    public void deactivate() {
        configService.removeListener(configListener);
        configRegistry.unregisterConfigFactory(configFactory);
        log.info("Stopped");
    }


    @Override
    public List<AccessData> accessModeData() {
        return accessModeDataList;
    }

    @Override
    public List<TrunkData> trunkModeData() {
        return trunkModeDataList;
    }


    private void readConfig() {
        log.debug("Config received");
        TiesseConfig config = configRegistry.getConfig(appId, TiesseConfig.class);
        accessModeDataList = config.getAccessModeData();
        trunkModeDataList = config.getTrunkModeData();

        trunkModePortVlanMap = TrunkModePortVlanMapCreator(trunkModeDataList);
        ConfigTiesseDevice(trunkModePortVlanMap);

    }

    /**
     * Sets the Tiesse device with the received configuration.
     * In particular it sets the Tiesse device's ports in access/trunk mode
     * and assigns them an ip address and a netmask.
     */
    private void ConfigTiesseDevice(Map<String, List<VlanId>> trunkModePortVlanMap) { //TODO: add ip address and netmask configuration for the vlan
        Iterable<Device> devices = deviceService.getAvailableDevices();
        for (Device device : devices) {
            if (device.manufacturer().equals("Tiesse")) { //if manufacturer is equal to Tiesse
                if (device.is(InterfaceConfigTiesse.class)){ // if the interfaceConfig behavior is supported by the device.
                    DriverHandler handler = driverService.createHandler(device.id());
                    InterfaceConfigTiesse interfaceConfig = handler.behaviour(InterfaceConfigTiesse.class);
                    if (!accessModeDataList.isEmpty()) { //if accessModeData List is not empty
                        for (AccessData accessModeData: accessModeDataList){
                            String port = accessModeData.getPort();
                            String accessVlanString = accessModeData.getVlan();
                            String accessIpAddr = accessModeData.getIpaddress();
                            String accessNetmask = accessModeData.getNetmask();

                            VlanId accessVlanId = VlanId.vlanId(Short.parseShort(accessVlanString));//parse vlan id from String to VlanId type

                            interfaceConfig.addAccessMode(port, accessVlanId); //set switch in access mode with port and vlan
                            interfaceConfig.addIpAddrAndNetmaskToInterface(port,accessVlanId,accessIpAddr,accessNetmask); //set vlan with ip address and netmask
                            }
                    }
                    if (!trunkModeDataList.isEmpty()) { //if trunkModeData List is not empty
                        if(!trunkModePortVlanMap.isEmpty()) { //if the port-vlanlist map is not empty
                            for (Map.Entry<String, List<VlanId>> portVlanEntry : trunkModePortVlanMap.entrySet()) //for every port-vlanlist map element
                            {
                                String port = portVlanEntry.getKey();
                                List<VlanId> vlanIdList = portVlanEntry.getValue();

                                interfaceConfig.addTrunkMode(port, vlanIdList); //set switch in trunk mode with port and vlans allowed for that port
                            }
                        }

                        for (TrunkData trunkModeData: trunkModeDataList) { //second cycle to add vlan interface and ip addr

                            String port = trunkModeData.getPort();
                            String trunkVlanString = trunkModeData.getVlan();
                            String trunkIpAddr = trunkModeData.getIpaddress();
                            String trunkNetmask = trunkModeData.getNetmask();

                            VlanId trunkVlanId = VlanId.vlanId(Short.parseShort(trunkVlanString));//parse vlan id from String to VlanId type

                            interfaceConfig.addIpAddrAndNetmaskToInterface(port,trunkVlanId,trunkIpAddr,trunkNetmask); //set vlan with ip address and netmask

                        }
                    }
                }
            }
        }
    }


    /**
     * Creates a map with port as key and list of vlans for that port as value for trunk mode.
     */
    public Map<String, List<VlanId>> TrunkModePortVlanMapCreator(List<TrunkData> trunkModeDataList){
        Map<String, List<VlanId>> portVlansMap = new HashMap<>(); //map with port, vlan id list for that port

        if (!trunkModeDataList.isEmpty()) { //if trunkModeData List is not empty

            for (TrunkData trunkModeData: trunkModeDataList) {

                String port = trunkModeData.getPort();
                String trunkVlanString = trunkModeData.getVlan();

                VlanId trunkVlanId = VlanId.vlanId(Short.parseShort(trunkVlanString));//parse vlan id from String to VlanId type


                if(portVlansMap.isEmpty()) //if the map is empty, first time accessing it
                {
                    portVlansMap.put(port, new ArrayList<VlanId>()); //add a port with a new empty list
                    portVlansMap.get(port).add(trunkVlanId); //add vlan to the list for that port
                }
                else//from the second time accessing the map on
                {
                    if(portVlansMap.containsKey(port))// port already exists in the map
                    {
                        portVlansMap.get(port).add(trunkVlanId); //add vlan to the list for that port
                    }
                    else // port does not exist in map
                    {
                        portVlansMap.put(port, new ArrayList<VlanId>()); //add a port with a new empty list
                        portVlansMap.get(port).add(trunkVlanId); //add vlan to the list for that port
                    }
                }
            }
        }
        return portVlansMap;
    }

    private class InternalConfigListener implements NetworkConfigListener {
        @Override
        public void event(NetworkConfigEvent event) {
            if (!event.configClass().equals(TiesseConfig.class)) {
                return;
            }
            log.debug("Listener called: {}", event.type());
            switch (event.type()) {
                case CONFIG_ADDED:
                    log.info("Network configuration added");
                    eventExecutor.execute(TiesseConfigManager.this::readConfig);
                    break;
                case CONFIG_UPDATED:
                    log.info("Network configuration updated");
                    eventExecutor.execute(TiesseConfigManager.this::readConfig);
                    break;
                default:
                    break;
            }
        }
    }

}
