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

package org.onosproject.tiessemanager;


import java.util.List;
import java.util.Map;

/**
 *  Tiesse configuration interface.
 *  Currently, the whole configuration is passed to Tiesse through
 *  the network configuration subsystem.
 */

public interface TiesseConfigService {

    /**
     * Returns the map between the port and vlan ID with Access Mode.
     *
     * @return a map between the port and vlan ID
     */

    Map<String, String> portVlanAccessModeMap();

    /**
     * Returns the map between the port and vlan ID with Trunk Mode.
     *
     * @return a map between the port and vlan ID
     */

    Map<String, List<String>> portVlanTrunkModeMap();
}

