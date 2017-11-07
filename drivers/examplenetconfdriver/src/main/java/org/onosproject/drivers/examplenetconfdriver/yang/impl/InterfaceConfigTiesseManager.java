/*
 * Copyright 2017-present Open Networking Laboratory
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

package org.onosproject.drivers.examplenetconfdriver.yang.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;

import org.onosproject.netconf.DatastoreId;
import org.onosproject.netconf.NetconfException;
import org.onosproject.netconf.NetconfSession;
import org.onosproject.yang.gen.v1.tiesseethernet.rev20170523.TiesseEthernetOpParam;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.TiesseSwitch;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.TiesseSwitchOpParam;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.tiesseswitch.DefaultYangAutoPrefixSwitch;
import org.onosproject.yang.model.*;
import org.onosproject.yang.runtime.*;

import org.onosproject.drivers.examplenetconfdriver.yang.InterfaceConfigTiesseNetconfService;
import org.onosproject.netconf.DatastoreId;
import org.onosproject.netconf.NetconfException;
import org.onosproject.netconf.NetconfSession;
import org.onosproject.yang.gen.v1.tiessevlan.rev20170225.TiesseVlanOpParam;



import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the interface InterfaceConfigTiesseNetconfService.
 */
@Component(immediate = true, inherit = true)
@Service
public class InterfaceConfigTiesseManager extends AbstractYangServiceImpl implements InterfaceConfigTiesseNetconfService{
    public static final String INTERFACE_CONFIG_TIESSE = "org.onosproject.drivers.examplenetconfdriver.yang.InterfaceConfigTiesse";
    public static final String INTERFACE_CONFIG_TIESSE_NS = "http://www.examplenetconfdriver.com/InterfaceConfigTiesse";

    @Activate
    public void activate() {
        super.activate();
        appId = coreService.registerApplication(INTERFACE_CONFIG_TIESSE);
        log.info("InterfaceConfigTiesseManager Started");
    }

    @Deactivate
    public void deactivate() {
        super.deactivate();
        log.info("InterfaceConfigTiesseManager Stopped");
    }

    @Override
    public boolean setTiesseEthernet(TiesseEthernetOpParam tiesseEthernet, NetconfSession session, DatastoreId targetDs, String intf) throws NetconfException {
        log.info("Inside setTiesseEthernet() ");
        ModelObjectData mo = null;
        if (intf.equals("eth0")) {
            mo = DefaultModelObjectData.builder()
                    .addModelObject((ModelObject) tiesseEthernet.eth0()).build();
        }
        if (intf.equals("eth1")) {
            mo = DefaultModelObjectData.builder()
                    .addModelObject((ModelObject) tiesseEthernet.eth1()).build();
        }
        if (intf.equals("eth2")) {
            mo = DefaultModelObjectData.builder()
                    .addModelObject((ModelObject) tiesseEthernet.eth2()).build();
        }
        if (intf.equals("eth3")) {
            mo = DefaultModelObjectData.builder()
                    .addModelObject((ModelObject) tiesseEthernet.eth3()).build();
        }
        if (intf.equals("eth4")) {
            mo = DefaultModelObjectData.builder()
                    .addModelObject((ModelObject) tiesseEthernet.eth4()).build();
        }
        if (intf.equals("eth5")) {
            mo = DefaultModelObjectData.builder()
                    .addModelObject((ModelObject) tiesseEthernet.eth5()).build();
        }
        log.info("Inside setTiesseEthernet(). Builded model. ");
        return setNetconfObjectTiesseEthernet(mo, session, targetDs, null);
    }

    /**
     * Call NETCONF edit-config with a configuration.
     */

    @Override
    public boolean setTiesseSwitch(TiesseSwitchOpParam tiesseSwitch, NetconfSession session, DatastoreId targetDs) throws NetconfException {
        log.info("Inside setTiesseSwitch() ");
        ModelObjectData mo = DefaultModelObjectData.builder()
                .addModelObject((ModelObject) tiesseSwitch.yangAutoPrefixSwitch()).build();
        log.info("Inside setTiesseSwitch(). Builded model. ");
        return setNetconfObjectTiesseSwitch(mo, session, targetDs, null);
    }

    /**
     * Call NETCONF edit-config with a configuration.
     */

    @Override
    public boolean setTiesseVlan(TiesseVlanOpParam tiesseVlan, NetconfSession session, DatastoreId targetDs) throws NetconfException {
        log.info("Inside setTiesseVlan() ");
        ModelObjectData mo = DefaultModelObjectData.builder()
                .addModelObject((ModelObject) tiesseVlan.vlan()).build();
        log.info("Inside setTiesseVlan(). Builded model. ");
        return setNetconfObjectTiesseVlan(mo, session, targetDs, null);
    }

    /**
     * Delete the configuration.
     *
     */

    @Override
    public boolean deleteTiesseVlan(TiesseVlanOpParam tiesseVlan, NetconfSession session, DatastoreId targetDs) throws NetconfException {
        return false;
    }

    /**
     * Get a filtered subset of the model.
     * This is meant to filter the current live model
     * against the attribute(s) given in the argument
     * and return the filtered model.
     * @throws NetconfException if the session has any error
     */
    @Override
    public TiesseSwitch getTiesseSwitch(TiesseSwitchOpParam tiesseSwitch, NetconfSession session)
            throws NetconfException {

        ModelObjectData moQuery = DefaultModelObjectData.builder()
                .addModelObject((ModelObject) tiesseSwitch.yangAutoPrefixSwitch())
                .build();

        ModelObjectData moReply = getNetconfObject(moQuery, session);

        TiesseSwitchOpParam tiesseSwitchOpParam = new TiesseSwitchOpParam();
        for (ModelObject mo:moReply.modelObjects()) {
            if (mo instanceof DefaultYangAutoPrefixSwitch) {
                tiesseSwitchOpParam.yangAutoPrefixSwitch((DefaultYangAutoPrefixSwitch) mo);
            }
        }

        return tiesseSwitchOpParam;
    }
}
