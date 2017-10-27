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

package org.onosproject.tiessemanager.impl.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.onosproject.core.ApplicationId;

import org.onosproject.net.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration class for vlans.
 */
public class TiesseConfig extends Config<ApplicationId> {

    private static final String VLANS = "vlans";
    private static final String PORT = "port";
    private static final String VLAN = "vlan";
    private static final String MODE = "mode";
    private static final String VLAN_LIST = "vlanlist";
    private static final String IP_ADDRESS = "ipaddress";
    private static final String NETMASK = "netmask";
    private static final String IP_ADDRESS_LIST = "ipaddresslist";
    private static final String NETMASK_LIST = "netmasklist";

    /**
     * Gets port,vlan,ip address and netmask for access mode from configuration.
     * @return AccessData object
     */

    public List<AccessData> getAccessModeData() {
        List<AccessData> accessDataList = new ArrayList<>();

        JsonNode vlans = object.path(VLANS);
        //for each element of the vlans list
        vlans.forEach(vlansElem -> {

            String mode = vlansElem.path(MODE).asText();
            if (mode.equals("ACCESS")) {

                String port = vlansElem.path(PORT).asText();
                String vlan = vlansElem.path(VLAN).asText();
                String ipaddress = vlansElem.path(IP_ADDRESS).asText();
                String netmask = vlansElem.path(NETMASK).asText();

                AccessData accessData = new AccessData(port, vlan, ipaddress, netmask);
                accessDataList.add(accessData);
            }
        });

        return accessDataList;
    }

    /**
     * Gets port,vlan,ip address and netmask for trunk mode from configuration.
     * @return TrunkData object
     */

    public List<TrunkData> getTrunkModeData() {
        List<TrunkData> trunkDataList = new ArrayList<>();


        JsonNode vlans = object.path(VLANS);
        //for each element of the vlans node
        vlans.forEach(vlansElem -> {

            String mode = vlansElem.path(MODE).asText();
            if (mode.equals("TRUNK")) {
                String port = vlansElem.path(PORT).asText();
                String vlan = vlansElem.path(VLAN).asText();
                String ipaddress = vlansElem.path(IP_ADDRESS).asText();
                String netmask = vlansElem.path(NETMASK).asText();

                TrunkData trunkData = new TrunkData(port, vlan, ipaddress, netmask);
                trunkDataList.add(trunkData);
            }
        });

        return trunkDataList;
    }
}

