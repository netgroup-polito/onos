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


import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeoutException;


import org.onlab.packet.IpPrefix;

import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;

import org.onosproject.drivers.examplenetconfdriver.yang.OpenconfigBgpNetconfService;

import org.onosproject.net.PortNumber;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onosproject.net.flow.DefaultFlowEntry;
import org.onosproject.net.flow.DefaultFlowRule;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.FlowEntry;
import org.onosproject.net.flow.FlowEntry.FlowEntryState;
import org.onosproject.net.flow.FlowRule;
import org.onosproject.net.flow.FlowRuleProgrammable;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flow.criteria.Criteria;
import org.onosproject.net.flow.criteria.Criterion;
import org.onosproject.net.flow.criteria.Criterion.Type;
import org.onosproject.net.flow.criteria.PortCriterion;


import org.onosproject.netconf.DatastoreId;
import org.onosproject.netconf.NetconfController;
import org.onosproject.netconf.NetconfDevice;
import org.onosproject.netconf.NetconfException;
import org.onosproject.netconf.NetconfSession;

import org.onosproject.yang.gen.v1.openconfigbgp.rev20170730.OpenconfigBgp;
import org.onosproject.yang.gen.v1.openconfigbgp.rev20170730.OpenconfigBgpOpParam;
import org.onosproject.yang.gen.v1.openconfigbgp.rev20170730.openconfigbgp.bgptop.Bgp;
import org.onosproject.yang.gen.v1.openconfigbgp.rev20170730.openconfigbgp.bgptop.DefaultBgp;
import org.onosproject.yang.gen.v1.openconfigbgp.rev20170730.openconfigbgpcommon.bgpcommonneighborgrouptransportconfig.LocalAddressUnion;
import org.onosproject.yang.gen.v1.openconfigbgp.rev20170730.openconfigbgppeergroup.bgppeergrouplist.DefaultPeerGroup;
import org.onosproject.yang.gen.v1.openconfigbgp.rev20170730.openconfigbgppeergroup.bgppeergrouplist.PeerGroup;

import org.slf4j.Logger;

/**
 * An implementation of the FlowRuleProgrammable behaviour for My Device.
 *
 * This device is not a native Open Flow device. It has a NETCONF interface for configuration.<br>
 *
 */
public class MyDeviceFlowRuleProgrammable extends AbstractHandlerBehaviour implements FlowRuleProgrammable {

    protected final Logger log = getLogger(getClass());
    public static final String EXAMPLENETCONFDRIVER_DRIVERS = "com.examplenetconfdriver.drivers";
    public static final int PRIORITY_DEFAULT = 50000;
    //To protect the NETCONF session from concurrent access across flow addition and removal
    //static Semaphore sessionMutex = new Semaphore(1);

    /**
     * Draft of getFlowEntries for OpenconfigBGP.
     * @return A collection of Flow Entries
     */

    @Override
    public Collection<FlowEntry> getFlowEntries() {
        Collection<FlowEntry> flowEntryCollection = new HashSet<FlowEntry>();

        NetconfController controller = checkNotNull(handler().get(NetconfController.class));
        NetconfDevice ncDevice = controller.getDevicesMap().get(handler().data().deviceId());
        if (ncDevice == null) {
            log.error("Internal ONOS Error. Device has been marked as reachable, " +
                            "but deviceID {} is not in Devices Map. Continuing with empty description",
                    handler().data().deviceId());
            return flowEntryCollection;
        }
        NetconfSession session = ncDevice.getSession();
        CoreService coreService = checkNotNull(handler().get(CoreService.class));
        ApplicationId appId = coreService.getAppId(EXAMPLENETCONFDRIVER_DRIVERS);

        /*
        OpenconfigBgpNetconfService openconfigBgpNetconfService =
                (OpenconfigBgpNetconfService) checkNotNull(handler().get(OpenconfigBgpNetconfService.class));

        log.debug("getFlowEntries() called on MyDeviceFlowRuleProgrammable");

        //First get the OpenconfigBgp rules


        OpenconfigBgpOpParam op = new OpenconfigBgpOpParam();

        try {
            OpenconfigBgp openconfigBgpCurrent =
                    openconfigBgpNetconfService.getOpenconfigBgp(op, session);
            if (openconfigBgpCurrent != null) {
                flowEntryCollection.addAll(
                        convertBgpToFlowRules(openconfigBgpCurrent, appId));
            }
        } catch (NetconfException e) {
            if (e.getCause() instanceof TimeoutException) {
                log.warn("Timeout exception getting OpenconfigBGP Flow Entries from {}",
                        handler().data().deviceId());
                return flowEntryCollection;
            } else {
                log.error("Unexpected error on OpenconfigBGP getFlowEntries on {}",
                        handler().data().deviceId(), e);
            }
        }*/

        return flowEntryCollection;
    }


    /**
     * Apply the flow entries to the device.
     * Translate the flow rule to Bgp policies and send them to the device through NETCONF
     *
     * @param rules A collection of Flow Rules to be applied to the device
     * @return A collection of the Flow Rules that have been added.
     */
    @Override
    public Collection<FlowRule> applyFlowRules(Collection<FlowRule> rules) {
        Collection<FlowRule> frAdded = new HashSet<FlowRule>();
        if (rules == null || rules.size() == 0) {
            return rules;
        }
        NetconfController controller = checkNotNull(handler().get(NetconfController.class));
        NetconfSession session = controller.getDevicesMap().get(handler().data().deviceId()).getSession();

        /*
        OpenconfigBgpNetconfService openconfigBgpNetconfService =
                (OpenconfigBgpNetconfService) checkNotNull(handler().get(OpenconfigBgpNetconfService.class));
        log.debug("applyFlowRules() called on MyDeviceFlowRuleProgrammable with {} rules.", rules.size());
        // FIXME: Change this so it's dynamically driven


        Bgp bgp = new DefaultBgp();
        int counter = 0; //counter for creating peergroup name

        for (FlowRule fr : rules) { //TODO: This is just a draft, needs to be modified
            /*
            // Example to filter the flowrules by the Port 0
            if (fr.selector().getCriterion(Type.IPV4_SRC) != null &&
                    fr.selector().getCriterion(Type.IN_PORT) != null &&
                    ((PortCriterion) fr.selector().getCriterion(Type.IN_PORT)).port().toLong() == 0) {
                parseFrForBgp(frAdded, fr);
            } else {
                log.info("Unexpected Flow Rule type applied: " + fr);
            }
            *//*

            parseFrForBgp(frAdded, fr, bgp, counter);
            counter++;

        }
        OpenconfigBgpOpParam opBgpParam = new OpenconfigBgpOpParam();
        opBgpParam.bgp(bgp);


        try {
            //sessionMutex.acquire();

            openconfigBgpNetconfService.setOpenconfigBgp(opBgpParam, session, DatastoreId.RUNNING);

        } catch (NetconfException e) {
            log.error("Error applying Flow Rules to BGP - will try again: " + e.getMessage());
            //sessionMutex.release();
            return frAdded;

        }

        */
        //sessionMutex.release();
        return frAdded;
    }

    /**
     * Remove flow rules from the device.
     * In this particular case flow rules with port 0 are filtered and then are parsed to create peerGroup elements
     * to be removed from the device through NETCONF.
     *
     * @param rulesToRemove A collection of Flow Rules to be removed to the device
     * @return A collection of the Flow Rules that have been removed.
     */
    @Override
    public Collection<FlowRule> removeFlowRules(Collection<FlowRule> rulesToRemove) {
        NetconfController controller = checkNotNull(handler().get(NetconfController.class));
        NetconfSession session = controller.getDevicesMap().get(handler().data().deviceId()).getSession();


        OpenconfigBgpNetconfService openconfigBgpNetconfService =
                (OpenconfigBgpNetconfService) checkNotNull(handler().get(OpenconfigBgpNetconfService.class));

        log.debug("removeFlowRules() called on MyDeviceFlowRuleProgrammable with {} rules.", rulesToRemove.size());

        if (rulesToRemove.size() == 0) {
            return rulesToRemove;
        }


        Collection<FlowRule> rulesRemoved = new HashSet<FlowRule>();
        /*

        Bgp bgp = new DefaultBgp();
        int counter = 0; //counter for creating peergroup name

        for (FlowRule ruleToRemove : rulesToRemove) {
            // Example to filter the flowrules by the Port 0
            if (ruleToRemove.selector().getCriterion(Type.IPV4_SRC) != null &&
                    ruleToRemove.selector().getCriterion(Type.IN_PORT) != null &&
                    ((PortCriterion) ruleToRemove.selector().getCriterion(Type.IN_PORT)).port().toLong() == 0) {

                //create a peer group element from fr and add the rule to rulesRemoved
                parseFrForBgp(rulesRemoved, ruleToRemove, bgp, counter);
                counter++;



            } else {
                log.info("Unexpected Flow Rule type removal: " + ruleToRemove);
            }
        }

        //If there is at least 1 rule to remove, delete it from datastore
        if (counter > 0) {
            try {
                OpenconfigBgpOpParam opBgpParam = new OpenconfigBgpOpParam();
                opBgpParam.bgp(bgp);

                openconfigBgpNetconfService.deleteOpenconfigBgp(opBgpParam, session, DatastoreId.RUNNING);

            } catch (NetconfException e) {
                log.warn("Remove FlowRule on openconfigBgpNetconfService could not delete the rule - "
                        + "it may already have been deleted: " + e.getMessage());
            }
        }*/

        return rulesRemoved;
    }


    private Collection<FlowEntry> convertBgpToFlowRules( //TODO: Write the correct flowrule, this is just a draft/example
            OpenconfigBgp openconfigBgpCurrent, ApplicationId appId) {
        Collection<FlowEntry> flowEntryCollection = new HashSet<FlowEntry>();

        List<PeerGroup> peerGroupList = openconfigBgpCurrent.bgp().peerGroups().peerGroup();
        Criterion matchInPort = Criteria.matchInPort(PortNumber.portNumber(0));
        TrafficSelector.Builder tsBuilder = DefaultTrafficSelector.builder();

        if(peerGroupList != null){
            for (PeerGroup peer: peerGroupList){
                String peerIpAddress = peer.transport().state().localAddress().ipAddress().toString();
                Criterion matchIpSrc = Criteria.matchIPSrc(IpPrefix.valueOf(peerIpAddress));
                TrafficSelector selector = tsBuilder.add(matchIpSrc).add(matchInPort).build();

                TrafficTreatment.Builder trBuilder = DefaultTrafficTreatment.builder();
                TrafficTreatment treatment = trBuilder.setOutput(PortNumber.portNumber(2)).build();

                FlowRule.Builder feBuilder = new DefaultFlowRule.Builder();

                FlowRule fr = feBuilder
                        .forDevice(handler().data().deviceId())
                        .withSelector(selector)
                        .withTreatment(treatment)
                        .forTable(1)
                        .makePermanent()
                        .withPriority(PRIORITY_DEFAULT)
                        .build();

                flowEntryCollection.add(
                        new DefaultFlowEntry(fr, FlowEntryState.ADDED, 0, 0, 0));
            }
        }
        return flowEntryCollection;
    }

    private void parseFrForBgp(Collection<FlowRule> frList, FlowRule fr, Bgp bgp, int counter) { //TODO: Write the correct bgp object, this is just an example
        String ipAddrStr = fr.selector().getCriterion(Type.IPV4_SRC).toString().substring(9); //filter by ip
        log.debug("Applying IP address to " + ipAddrStr + "on EA1000 through NETCONF");


        LocalAddressUnion lau =  LocalAddressUnion.fromString(ipAddrStr);

        PeerGroup peerGroup = new DefaultPeerGroup();
        peerGroup.config().peerGroupName("PeerGroup_" + counter);
        peerGroup.transport().config().localAddress(lau); //applying the ip filter from the flow rule to a new peergroup

        bgp.peerGroups().peerGroup().add(peerGroup);  //adding the peerGroup to peerGroups of bgp



        frList.add(fr);
    }

}
