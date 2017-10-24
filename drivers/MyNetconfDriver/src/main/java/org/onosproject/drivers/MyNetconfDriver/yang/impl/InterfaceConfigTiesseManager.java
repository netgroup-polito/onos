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

package org.onosproject.drivers.MyNetconfDriver.yang.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;

import org.onosproject.netconf.DatastoreId;
import org.onosproject.netconf.NetconfException;
import org.onosproject.netconf.NetconfSession;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.TiesseSwitchOpParam;
import org.onosproject.yang.model.*;
import org.onosproject.yang.runtime.*;

import org.onosproject.drivers.MyNetconfDriver.yang.InterfaceConfigTiesseNetconfService;
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
    public static final String INTERFACE_CONFIG_TIESSE = "org.onosproject.drivers.MyNetconfDriver.yang.InterfaceConfigTiesse";
    public static final String INTERFACE_CONFIG_TIESSE_NS = "http://www.mynetconfdriver.com/InterfaceConfigTiesse";

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

    /**
     * Call NETCONF edit-config with a configuration.
     */

    @Override
    public boolean setTiesseSwitch(TiesseSwitchOpParam tiesseSwitch, NetconfSession session, DatastoreId targetDs) throws NetconfException {

        ModelObjectData mo = DefaultModelObjectData.builder()
                .addModelObject(tiesseSwitch).build();

        return setNetconfObject(mo, session, targetDs, null);
    }

    /**
     * Call NETCONF edit-config with a configuration.
     */

    @Override
    public boolean setTiesseVlan(TiesseVlanOpParam tiesseVlan, NetconfSession session, DatastoreId targetDs) throws NetconfException {

        ModelObjectData mo = DefaultModelObjectData.builder()
                .addModelObject(tiesseVlan).build();

        return setNetconfObject(mo, session, targetDs, null);
    }

    /**
     * Delete the configuration.
     *
     */

    @Override
    public boolean deleteTiesseVlan(TiesseVlanOpParam tiesseVlan, NetconfSession session, DatastoreId targetDs) throws NetconfException {
        return false;
    }
}
