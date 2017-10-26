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
package org.onosproject.drivers.mynetconfdriver.yang.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.drivers.mynetconfdriver.yang.OpenconfigBgpNetconfService;
import org.onosproject.netconf.DatastoreId;
import org.onosproject.netconf.NetconfException;
import org.onosproject.netconf.NetconfSession;
import org.onosproject.yang.gen.v1.openconfigbgp.rev20170730.OpenconfigBgp;
import org.onosproject.yang.gen.v1.openconfigbgp.rev20170730.OpenconfigBgpOpParam;
import org.onosproject.yang.gen.v1.openconfigbgp.rev20170730.openconfigbgp.bgptop.DefaultBgp;
import org.onosproject.yang.gen.v1.openconfigbgp.rev20170730.openconfigbgppeergroup.bgppeergrouplist.PeerGroup;
import org.onosproject.yang.model.*;
import org.onosproject.yang.runtime.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/**
 * Implementation of the interface OpenconfigBgpNetconfService.
 */
@Component(immediate = true, inherit = true)
@Service
public class OpenconfigBgpManager extends AbstractYangServiceImpl implements OpenconfigBgpNetconfService {

    public static final String OPENCONFIG_BGP = "org.onosproject.drivers.mynetconfdriver.yang.openconfigbgp";
    public static final String OPENCONFIG_BGP_NS = "http://www.mynetconfdriver.com/openconfigbgp";

    @Activate
    public void activate() {
        super.activate();
        appId = coreService.registerApplication(OPENCONFIG_BGP);
        log.info("OpenconfigBgpManager Started");
    }

    @Deactivate
    public void deactivate() {
        super.deactivate();
        log.info("OpenconfigBgpManager Stopped");
    }

    /**
     * Get a filtered subset of the model.
     * This is meant to filter the current live model
     * against the attribute(s) given in the argument
     * and return the filtered model.
     * @throws NetconfException if the session has any error
     */
    @Override
    public OpenconfigBgp getOpenconfigBgp(OpenconfigBgpOpParam openconfigBgpFilter, NetconfSession session) throws NetconfException {

        ModelObjectData moQuery = DefaultModelObjectData.builder()
                .addModelObject((ModelObject) openconfigBgpFilter.bgp())
                .build();

        ModelObjectData moReply = getNetconfObject(moQuery, session);

        OpenconfigBgpOpParam openconfigBgp = new OpenconfigBgpOpParam();
        for (ModelObject mo:moReply.modelObjects()) {
            if (mo instanceof DefaultBgp) {
                openconfigBgp.bgp((DefaultBgp) mo);
            }
        }

        return openconfigBgp;
    }

    /**
     * Get a specific subset of the model
     * using a pre-build rpc
     * and return the filtered model.
     * @throws NetconfException if the session has any error
     */
    @Override
    public OpenconfigBgp getOpenconfigBgpInit(NetconfSession session) throws NetconfException {
        if (session == null) {
            throw new NetconfException("Session is null when calling getOpenconfigBgp()");
        }

        String xmlResult = session.get(getBgpRequestBuilder(), null); //RequestBuilder to be defined

        xmlResult = removeRpcReplyData(xmlResult);
        DefaultCompositeStream resultDcs = new DefaultCompositeStream(
                null, new ByteArrayInputStream(xmlResult.getBytes()));
        CompositeData compositeData = xSer.decode(resultDcs, yCtx);

        ModelObjectData mod = ((ModelConverter) yangModelRegistry).createModel(compositeData.resourceData());

        OpenconfigBgpOpParam openconfigBgp = new OpenconfigBgpOpParam();
        for (ModelObject mo:mod.modelObjects()) {
            if (mo instanceof DefaultBgp) {
                openconfigBgp.bgp((DefaultBgp) mo);
            }
        }

        return openconfigBgp;
    }

    /**
     * Call NETCONF edit-config with a configuration.
     */

    @Override
    public boolean setOpenconfigBgp(OpenconfigBgpOpParam openconfigBgp, NetconfSession session, DatastoreId targetDs) throws NetconfException {
        ModelObjectData mo = DefaultModelObjectData.builder()
                .addModelObject(openconfigBgp).build();
        return setNetconfObject(mo, session, targetDs, null);
    }

    /**
     * Delete the configuration.
     * In this case it deletes the peerGroups taken from the configuration.
     */

    @Override
    public boolean deleteOpenconfigBgp(OpenconfigBgpOpParam openconfigBgp, NetconfSession session, DatastoreId ncDs) throws NetconfException {

        ModelObjectData moQuery =  DefaultModelObjectData.builder()
                .addModelObject(openconfigBgp).build();

        ArrayList anis = new ArrayList<AnnotatedNodeInfo>();
        for(PeerGroup peerGroup: openconfigBgp.bgp().peerGroups().peerGroup()){
             String peerGroupName = peerGroup.config().peerGroupName();
        //Delete the specified peer groups
            ResourceId.Builder ridBuilder = ResourceId.builder()
                    .addBranchPointSchema("/", null)
                    .addBranchPointSchema("bgp", OPENCONFIG_BGP_NS)
                    .addBranchPointSchema("peer-groups", OPENCONFIG_BGP_NS)
                    .addBranchPointSchema("bgp-peer-group-list", OPENCONFIG_BGP_NS)
                    .addBranchPointSchema("peer-group", OPENCONFIG_BGP_NS)
                    .addKeyLeaf("peer-group-name", OPENCONFIG_BGP_NS, peerGroupName);
            AnnotatedNodeInfo ani = DefaultAnnotatedNodeInfo.builder()
                    .resourceId(ridBuilder.build())
                    .addAnnotation(new DefaultAnnotation(NC_OPERATION, OP_DELETE))
                    .build();
            anis.add(ani);

}



        return setNetconfObject(moQuery, session, ncDs, anis);
    }

    /**
     * Builds a request crafted to get the BGP configuration of the device.
     *
     * @return The request string.
     */

    private static String getBgpRequestBuilder() { //TODO: Write the right BGP rpc for the device
        StringBuilder rpc = new StringBuilder();
        /*
        rpc.append("<system-state xmlns=\"urn:ietf:params:xml:ns:yang:ietf-system\" ");
        rpc.append("xmlns:sysms=\"http://www.microsemi.com/microsemi-edge-assure/msea-system\">");
        rpc.append("<platform>");
        rpc.append("<os-release/>");
        rpc.append("<sysms:device-identification>");
        rpc.append("<sysms:serial-number/>");
        rpc.append("</sysms:device-identification>");
        rpc.append("</platform>");
        rpc.append("<clock>");
        rpc.append("<current-datetime/>");
        rpc.append("</clock>");
        rpc.append("</system-state>");
        rpc.append("<system xmlns=\"urn:ietf:params:xml:ns:yang:ietf-system\" ");
        rpc.append("xmlns:sysms=\"http://www.microsemi.com/microsemi-edge-assure/msea-system\">");
        rpc.append("<sysms:longitude/>");
        rpc.append("<sysms:latitude/>");
        rpc.append("</system>");
        */
        return rpc.toString();

    }
}
