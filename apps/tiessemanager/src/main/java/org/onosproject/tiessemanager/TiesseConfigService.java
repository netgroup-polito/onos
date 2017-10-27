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


import org.onosproject.tiessemanager.impl.config.AccessData;
import org.onosproject.tiessemanager.impl.config.TrunkData;

import java.util.List;
import java.util.Map;

/**
 *  Tiesse configuration interface.
 *  Currently, the whole configuration is passed to Tiesse through
 *  the network configuration subsystem.
 */

public interface TiesseConfigService {

    /**
     * Returns the AccessData object list containing port,vlan id, ip address and netmask for Access Mode.
     *
     * @return AccessData object
     */

    List<AccessData> accessModeData();

    /**
     * Returns the TrunkData object list containing port and a list of:vlan id, ip address and netmask for Trunk Mode.
     *
     * @return TrunkData object
     */

    List<TrunkData> trunkModeData();
}

