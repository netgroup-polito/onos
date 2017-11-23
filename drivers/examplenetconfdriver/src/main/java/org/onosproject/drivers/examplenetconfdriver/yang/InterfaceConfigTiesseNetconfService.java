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
package org.onosproject.drivers.examplenetconfdriver.yang;

import org.onosproject.netconf.DatastoreId;
import org.onosproject.netconf.NetconfException;
import org.onosproject.netconf.NetconfSession;


import org.onosproject.yang.gen.v1.tiessebridge.rev20170225.TiesseBridgeOpParam;
import org.onosproject.yang.gen.v1.tiesseethernet.rev20170523.TiesseEthernetOpParam;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.TiesseSwitch;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.TiesseSwitchOpParam;
import org.onosproject.yang.gen.v1.tiessevlan.rev20170225.TiesseVlanOpParam;

/**
 * Interface to include Netconf sessions to configure Tiesse devices.
 */

public interface InterfaceConfigTiesseNetconfService {

    /**
     * Sets the value to attribute tiesseSwitch.
     *
     * @param tiesseSwitch value of tiesseSwitch
     * @param session An active NETCONF session
     * @param targetDs one of running, candidate or startup
     * @return Boolean to indicate success or failure
     * @throws NetconfException if the session has any error
     */

    boolean setTiesseSwitch(TiesseSwitchOpParam tiesseSwitch, NetconfSession session,
                            DatastoreId targetDs) throws NetconfException;

    /**
     * Sets the value to attribute tiesseEthernet.
     *
     * @param tiesseEthernet value of tiesseEthernet
     * @param session An active NETCONF session
     * @param targetDs one of running, candidate or startup
     * @return Boolean to indicate success or failure
     * @throws NetconfException if the session has any error
     */

    boolean setTiesseEthernet(TiesseEthernetOpParam tiesseEthernet, NetconfSession session,
                          DatastoreId targetDs, String intf) throws NetconfException;



    /**
     * Sets the value to attribute tiesseVlan.
     *
     * @param tiesseVlan value of tiesseVlan
     * @param session An active NETCONF session
     * @param targetDs one of running, candidate or startup
     * @return Boolean to indicate success or failure
     * @throws NetconfException if the session has any error
     */

    boolean setTiesseVlan(TiesseVlanOpParam tiesseVlan, NetconfSession session,
                          DatastoreId targetDs) throws NetconfException;

    /**
     * Sets the value to attribute tiesseBridge.
     *
     * @param tiesseBridge value of tiesseBridge
     * @param session An active NETCONF session
     * @param targetDs one of running, candidate or startup
     * @return Boolean to indicate success or failure
     * @throws NetconfException if the session has any error
     */

    boolean setTiesseBridge(TiesseBridgeOpParam tiesseBridge, NetconfSession session,
                          DatastoreId targetDs) throws NetconfException;


    /**
     * Deletes the value to attribute tiesseVlan.
     * Not implemented.
     *
     * @param tiesseVlan value of tiesseVlan
     * @param session An active NETCONF session
     * @param targetDs The NETCONF datastore to edit
     * @return Boolean to indicate success or failure
     * @throws NetconfException if the session has any error
     */
    boolean deleteTiesseVlan(TiesseVlanOpParam tiesseVlan, NetconfSession session,
                             DatastoreId targetDs) throws NetconfException;




}
