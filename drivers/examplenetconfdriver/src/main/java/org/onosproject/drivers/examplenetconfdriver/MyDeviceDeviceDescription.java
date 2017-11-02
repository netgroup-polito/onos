/*
 * Copyright 2016-present Open Networking Laboratory
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
package org.onosproject.drivers.examplenetconfdriver;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.onlab.packet.ChassisId;
import org.onosproject.drivers.examplenetconfdriver.yang.IetfSystemNetconfService;
import org.onosproject.drivers.examplenetconfdriver.yang.InterfaceConfigTiesseNetconfService;
import org.onosproject.drivers.examplenetconfdriver.yang.impl.InterfaceConfigTiesseManager;
import org.onosproject.net.AnnotationKeys;
import org.onosproject.net.DefaultAnnotations;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Port;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DefaultDeviceDescription;
import org.onosproject.net.device.DefaultPortDescription;
import org.onosproject.net.device.DeviceDescription;
import org.onosproject.net.device.DeviceDescriptionDiscovery;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.PortDescription;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onosproject.netconf.NetconfController;
import org.onosproject.netconf.NetconfDevice;
import org.onosproject.netconf.NetconfException;
import org.onosproject.netconf.NetconfSession;

import org.onosproject.yang.gen.v1.ietfsystemmicrosemi.rev20160505.ietfsystemmicrosemi.system.AugmentedSysSystem;
import org.onosproject.yang.gen.v1.ietfsystemmicrosemi.rev20160505.ietfsystemmicrosemi.system.DefaultAugmentedSysSystem;
import org.onosproject.yang.gen.v1.ietfsystemmicrosemi.rev20160505.ietfsystemmicrosemi.systemstate.platform.AugmentedSysPlatform;
import org.onosproject.yang.gen.v1.ietfsystem.rev20140806.IetfSystem;
import org.onosproject.yang.gen.v1.ietfsystemmicrosemi.rev20160505.ietfsystemmicrosemi.systemstate.platform.DefaultAugmentedSysPlatform;
import org.onosproject.yang.gen.v1.ietfyangtypes.rev20130715.ietfyangtypes.DateAndTime;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.TiesseSwitch;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.TiesseSwitchOpParam;
import org.slf4j.Logger;

/**
 * Implementation of DeviceDescriptionDiscovery methods.
 */

public class MyDeviceDeviceDescription extends AbstractHandlerBehaviour implements DeviceDescriptionDiscovery {

    private String serialNumber = "unavailable";
    private String swVersion = "unavailable";
    //private String longitudeStr = null;
    //private String latitudeStr = null;
    private final Logger log = getLogger(getClass());

    public MyDeviceDeviceDescription() {
        log.info("Loaded handler behaviour MyDeviceDeviceDescription.");
    }

    @Override
    public DeviceDescription discoverDeviceDetails() {
        log.info("Adding description for MyDevice device");

        NetconfController controller = checkNotNull(handler().get(NetconfController.class));
        NetconfDevice ncDevice = controller.getDevicesMap().get(handler().data().deviceId());
        if (ncDevice == null) {
            log.error("Internal ONOS Error. Device has been marked as reachable, " +
                    "but deviceID {} is not in Devices Map. Continuing with empty description",
                    handler().data().deviceId());
            return null;
        }
        NetconfSession session = ncDevice.getSession();
        IetfSystemNetconfService ietfSystemService =
                (IetfSystemNetconfService) checkNotNull(handler().get(IetfSystemNetconfService.class));

        try {
            IetfSystem system = ietfSystemService.getIetfSystemInit(session);
            if (system != null && system.systemState() != null) {

                swVersion = system.systemState().platform().osRelease();
                /*
                //example of augmentation of models
                AugmentedSysPlatform augmentedSysPlatform = //example taken from microsemi device
                        (AugmentedSysPlatform) system.systemState()
                        .platform().augmentation(DefaultAugmentedSysPlatform.class);
                serialNumber = augmentedSysPlatform.deviceIdentification().serialNumber();//example taken from microsemi device
                */
                serialNumber = "5262-IKF";
                DateAndTime deviceDateAndTime = system.systemState().clock().currentDatetime();
                OffsetDateTime odt =
                        OffsetDateTime.parse(deviceDateAndTime.string(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                if (odt.getYear() < OffsetDateTime.now(ZoneId.of("UTC")).getYear()) {
                    OffsetDateTime nowUtc = OffsetDateTime.now(ZoneId.of("UTC"));
                    log.warn("Date on device is in the past: {}. Setting it to {}", odt.toString(), nowUtc);
                    ietfSystemService.setCurrentDatetime(nowUtc, session);
                }
            }
            /*
            if (system != null && system.system() != null) {
                AugmentedSysSystem augmentedSystem = //example taken from microsemi device
                        (AugmentedSysSystem) system.system().augmentation(DefaultAugmentedSysSystem.class);
                longitudeStr = augmentedSystem.longitude().toPlainString(); //example taken from microsemi device
                latitudeStr = augmentedSystem.latitude().toPlainString(); //example taken from microsemi device
            }*/
        } catch (NetconfException e) {
            log.error("Unable to retrieve init data from device: " + handler().data().deviceId().toString()
                    + " Error: " + e.getMessage());
            e.printStackTrace();
        }

        DeviceService deviceService = checkNotNull(handler().get(DeviceService.class));
        DeviceId deviceId = handler().data().deviceId();
        Device device = deviceService.getDevice(deviceId);
        DefaultAnnotations.Builder annotationsBuilder = DefaultAnnotations.builder();
        /*
        if (longitudeStr != null && latitudeStr != null) {
            annotationsBuilder.set(AnnotationKeys.LONGITUDE, longitudeStr)
                    .set(AnnotationKeys.LATITUDE, latitudeStr).build();
        } else {
            log.warn("Longitude and latitude could not be retrieved from device " + deviceId);
        }*/

        return new DefaultDeviceDescription(device.id().uri(), Device.Type.OTHER, "Tiesse", "Imola", swVersion,
                serialNumber, new ChassisId(), annotationsBuilder.build());
    }

    /*
    *
    * Example of PortDetails. It defines 2 ports: 1 Fiber port 0 and 1 copper port 1.
    * It also can be defined through a netconf get with the device specific rpc to get the ports
    *
    */

    @Override
    public List<PortDescription> discoverPortDetails() { //TODO: implement port discovery based on the specific device

        List<PortDescription> ports = new ArrayList<PortDescription>();
        /*
        //Example of static ports assignment (taken from microsemi)
       */

        DefaultAnnotations annotationPort1 = DefaultAnnotations.builder().set(AnnotationKeys.PORT_NAME, "Port 1").build();
        PortDescription port1 = new DefaultPortDescription(PortNumber.portNumber(1), true, Port.Type.COPPER, 1000,
                annotationPort1);
        ports.add(port1);

        DefaultAnnotations annotationPort2 = DefaultAnnotations.builder().set(AnnotationKeys.PORT_NAME, "Port 2").build();
        PortDescription port2 = new DefaultPortDescription(PortNumber.portNumber(2), true, Port.Type.COPPER, 1000,
                annotationPort2);
        ports.add(port2);

        DefaultAnnotations annotationPort3 = DefaultAnnotations.builder().set(AnnotationKeys.PORT_NAME, "Port 3").build();
        PortDescription port3 = new DefaultPortDescription(PortNumber.portNumber(3), true, Port.Type.COPPER, 1000,
                annotationPort3);
        ports.add(port3);

        DefaultAnnotations annotationPort4 = DefaultAnnotations.builder().set(AnnotationKeys.PORT_NAME, "Port 4").build();
        PortDescription port4 = new DefaultPortDescription(PortNumber.portNumber(4), true, Port.Type.COPPER, 1000,
                annotationPort4);
        ports.add(port4);

        DefaultAnnotations annotationPort5 = DefaultAnnotations.builder().set(AnnotationKeys.PORT_NAME, "Port 5").build();
        PortDescription port5 = new DefaultPortDescription(PortNumber.portNumber(5), true, Port.Type.COPPER, 1000,
                annotationPort5);
        ports.add(port5);


        /*
        //Example of ports assignment with the info obtained through netconf request to the device
       */

        /*
        NetconfController controller = checkNotNull(handler().get(NetconfController.class));
        NetconfDevice ncDevice = controller.getDevicesMap().get(handler().data().deviceId());
        if (ncDevice == null) {
            log.error("Internal ONOS Error. Device has been marked as reachable, " +
                            "but deviceID {} is not in Devices Map. Continuing with empty description",
                    handler().data().deviceId());
            return ports;
        }
        NetconfSession session = ncDevice.getSession();

        InterfaceConfigTiesseNetconfService interfaceConfigTiesseNetconfService =
                (InterfaceConfigTiesseNetconfService) checkNotNull(handler().get(InterfaceConfigTiesseNetconfService.class));

        TiesseSwitchOpParam op = new TiesseSwitchOpParam();

        try {
            TiesseSwitch tiesseSwitchCurrent =
                    interfaceConfigTiesseNetconfService.getTiesseSwitch(op, session);
            if (tiesseSwitchCurrent != null) {

                List<org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.tiesseswitch.yangautoprefixswitch.Port> tiessePorts = tiesseSwitchCurrent.yangAutoPrefixSwitch().port();
                for (org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.tiesseswitch.yangautoprefixswitch.Port tiessePort: tiessePorts)
                {
                    String tiessePortName = tiessePort.name();
                    PortNumber portNumber = PortNumber.portNumber(1); //portNumber initialized to 1 but will be changed if different
                    DefaultAnnotations annotationPort = DefaultAnnotations.builder().set(AnnotationKeys.PORT_NAME, tiessePortName).build();
                    if (tiessePortName.equals("Port 1")) { portNumber = PortNumber.portNumber(1);
                    } else if (tiessePortName.equals("Port 2")) {portNumber = PortNumber.portNumber(2);
                    } else if (tiessePortName.equals("Port 3")) {portNumber = PortNumber.portNumber(3);
                    } else if (tiessePortName.equals("Port 4")) {portNumber = PortNumber.portNumber(4);
                    }
                        PortDescription port = new DefaultPortDescription(portNumber, true, Port.Type.COPPER, 1000,
                                annotationPort);
                        ports.add(port);
                    }
                }

            }
        catch (NetconfException e) {
            if (e.getCause() instanceof TimeoutException) {
                log.warn("Timeout exception getting TiesseSwitch ports from {}",
                        handler().data().deviceId());
                return ports;
            } else {
                log.error("Unexpected error on TiesseSwitch discoverPortDetails() on {}",
                        handler().data().deviceId(), e);
            }
        }
        */

        return ports;
    }
}
