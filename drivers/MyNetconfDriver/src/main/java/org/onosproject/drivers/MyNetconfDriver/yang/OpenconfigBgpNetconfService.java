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
package org.onosproject.drivers.MyNetconfDriver.yang;

import org.onosproject.netconf.DatastoreId;
import org.onosproject.netconf.NetconfException;
import org.onosproject.netconf.NetconfSession;

import org.onosproject.yang.gen.v1.openconfigbgp.rev20170730.OpenconfigBgpOpParam;
import org.onosproject.yang.gen.v1.openconfigbgp.rev20170730.OpenconfigBgp;

public interface OpenconfigBgpNetconfService {

    /**
     * Get a specific subset of the model
     * using a pre-build rpc.
     *
     * @param session An active NETCONF session
     * @return OpenconfigBgp
     * @throws NetconfException if the session has any error
     */
    OpenconfigBgp getOpenconfigBgpInit (NetconfSession session) throws NetconfException;

    /**
     * Returns the filtered model.
     *
     * @param openconfigBgpFilter value of openconfigBgp to use as filter
     * @param session An active NETCONF session
     * @return OpenconfigBgp
     * @throws NetconfException if the session has any error
     */
    OpenconfigBgp getOpenconfigBgp(OpenconfigBgpOpParam openconfigBgpFilter, NetconfSession session) throws NetconfException;


    /**
     * Sets the value to attribute openconfigBgp.
     *
     * @param openconfigBgp value of openconfigBgp
     * @param session An active NETCONF session
     * @param targetDs one of running, candidate or startup
     * @return Boolean to indicate success or failure
     * @throws NetconfException if the session has any error
     */

    boolean setOpenconfigBgp(OpenconfigBgpOpParam openconfigBgp, NetconfSession session,
                       DatastoreId targetDs) throws NetconfException;

    /**
     * Deletes the value to attribute openconfigBgp.
     *
     * @param openconfigBgp value of openconfigBgp
     * @param session An active NETCONF session
     * @param targetDs The NETCONF datastore to edit
     * @return Boolean to indicate success or failure
     * @throws NetconfException if the session has any error
     */
    boolean deleteOpenconfigBgp(OpenconfigBgpOpParam openconfigBgp, NetconfSession session,
                                DatastoreId targetDs) throws NetconfException;


}
