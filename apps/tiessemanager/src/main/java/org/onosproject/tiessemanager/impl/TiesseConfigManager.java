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

import org.onosproject.drivers.examplenetconfdriver.InterfaceConfigExtended;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.behaviour.InterfaceConfig;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.driver.DriverHandler;
import org.onosproject.net.driver.DriverService;
import org.onosproject.tiessemanager.impl.config.TiesseConfig;
import org.onosproject.tiessemanager.TiesseConfigService;
import org.onosproject.net.config.*;
import org.onosproject.net.config.basics.SubjectFactories;
import org.slf4j.Logger;

import java.util.ArrayList;
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

    private Map<String, String> portVlanAccessModeMap;
    private Map<String, List<String>> portVlanTrunkModeMap;
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
    public Map<String, String>  portVlanAccessModeMap() {
        return ImmutableMap.copyOf(portVlanAccessModeMap);
    }

    @Override
    public Map<String, List<String>> portVlanTrunkModeMap() {
        return ImmutableMap.copyOf(portVlanTrunkModeMap);
    }


    private void readConfig() {
        log.debug("Config received");
        TiesseConfig config = configRegistry.getConfig(appId, TiesseConfig.class);
        portVlanAccessModeMap = config.getPortVlanAccessModeMap();
        portVlanTrunkModeMap = config.getPortVlanTrunkModeMap();

        ConfigTiesseDevice();

    }

    private void ConfigTiesseDevice() { //TODO: add ip address and netmask configuration for the vlan
        Iterable<Device> devices = deviceService.getAvailableDevices();
        for (Device device : devices) {
            if (device.manufacturer().equals("Tiesse")) { //if manufacturer is equal to Tiesse
                if (device.is(InterfaceConfigExtended.class)){ // if the interfaceConfig behavior is supported by the device.
                    DriverHandler handler = driverService.createHandler(device.id());
                    InterfaceConfigExtended interfaceConfig = handler.behaviour(InterfaceConfigExtended.class);
                    if (!portVlanAccessModeMap.isEmpty()) { //if portVlanAccessModeMap is not empty
                        for (Map.Entry<String, String> entry : portVlanAccessModeMap.entrySet()) {
                            String port = entry.getKey();
                            String accessVlanString = entry.getValue();

                            VlanId accessVlanId = VlanId.vlanId(Short.parseShort(accessVlanString));//parse vlan id from String to VlanId type
                            interfaceConfig.addAccessMode(port, accessVlanId);
                        }

                    }
                    if (!portVlanTrunkModeMap.isEmpty()) { //if portVlanTrunkModeMap is not empty
                        for (Map.Entry<String, List<String>> entry : portVlanTrunkModeMap.entrySet()) {
                            List<VlanId> vlanIdListForPort = new ArrayList<>();
                            String port = entry.getKey();
                            List<String> vlanListForPort = entry.getValue();
                            for(String trunkVlanString: vlanListForPort)
                            {
                                VlanId trunkVlanId = VlanId.vlanId(Short.parseShort(trunkVlanString));//parse vlan id from String to VlanId type
                                vlanIdListForPort.add(trunkVlanId);
                            }

                            interfaceConfig.addTrunkMode(port, vlanIdListForPort);
                        }

                    }

                }
            }
        }
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
