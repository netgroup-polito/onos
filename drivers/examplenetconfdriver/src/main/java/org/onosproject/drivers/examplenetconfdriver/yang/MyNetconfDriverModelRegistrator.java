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

import com.google.common.collect.ImmutableMap;
import org.apache.felix.scr.annotations.Component;
import org.onosproject.yang.AbstractYangModelRegistrator;
import org.onosproject.yang.gen.v1.entitystatetcmib.rev20051122.EntityStateTcMib;
import org.onosproject.yang.gen.v1.fpgainternal.rev20151130.FpgaInternal;
import org.onosproject.yang.gen.v1.ianacrypthash.rev20140806.IanaCryptHash;
import org.onosproject.yang.gen.v1.ianaiftype.rev20140508.IanaIfType;
import org.onosproject.yang.gen.v1.ieeetypes.rev20080522.IeeeTypes;
import org.onosproject.yang.gen.v1.ietfinettypes.rev20130715.IetfInetTypes;
import org.onosproject.yang.gen.v1.ietfinterfaces.rev20140508.IetfInterfaces;
import org.onosproject.yang.gen.v1.ietfnetconf.rev20110601.IetfNetconf;
import org.onosproject.yang.gen.v1.ietfnetconfacm.rev20120222.IetfNetconfAcm;
import org.onosproject.yang.gen.v1.ietfnetconfmonitoring.rev20101004.IetfNetconfMonitoring;
import org.onosproject.yang.gen.v1.ietfnetconfnotifications.rev20120206.IetfNetconfNotifications;
import org.onosproject.yang.gen.v1.ietfnetconfwithdefaults.rev20100609.IetfNetconfWithDefaults;
import org.onosproject.yang.gen.v1.ietfsystem.rev20140806.IetfSystem;
import org.onosproject.yang.gen.v1.ietfsystemmicrosemi.rev20160505.IetfSystemMicrosemi;
import org.onosproject.yang.gen.v1.ietfsystemtlsauth.rev20140524.IetfSystemTlsAuth;
import org.onosproject.yang.gen.v1.ietfx509certtoname.rev20130326.IetfX509CertToName;
import org.onosproject.yang.gen.v1.ietfyangtypes.rev20130715.IetfYangTypes;
import org.onosproject.yang.gen.v1.ncnotifications.rev20080714.NcNotifications;
import org.onosproject.yang.gen.v1.netopeercfgnetopeer.rev20130214.NetopeerCfgnetopeer;
import org.onosproject.yang.gen.v1.notifications.rev20080714.Notifications;
import org.onosproject.yang.gen.v1.openconfigbgp.rev20170730.*;
import org.onosproject.yang.gen.v1.openconfigbgppolicy.rev20170730.OpenconfigBgpPolicy;
import org.onosproject.yang.gen.v1.openconfigbgptypes.rev20170730.OpenconfigBgpErrors;
import org.onosproject.yang.gen.v1.openconfigbgptypes.rev20170730.OpenconfigBgpTypes;
import org.onosproject.yang.gen.v1.openconfiginettypes.rev20170824.OpenconfigInetTypes;
import org.onosproject.yang.gen.v1.openconfiginterfaces.rev20170714.OpenconfigInterfaces;
import org.onosproject.yang.gen.v1.openconfigpolicytypes.rev20170714.OpenconfigPolicyTypes;
import org.onosproject.yang.gen.v1.openconfigroutingpolicy.rev20170714.OpenconfigRoutingPolicy;
import org.onosproject.yang.gen.v1.openconfigtypes.rev20170816.OpenconfigTypes;
import org.onosproject.yang.gen.v1.openconfigyangtypes.rev20170730.OpenconfigYangTypes;
import org.onosproject.yang.gen.v1.rfc2544.rev20151020.Rfc2544;
import org.onosproject.yang.gen.v1.svcactivationtypes.rev20151027.SvcActivationTypes;

//import org.onosproject.yang.gen.v1.y1564.rev20151029.Y1564;
import org.onosproject.yang.gen.v1.tiesseaccesslist.rev20170707.TiesseAccessList;
import org.onosproject.yang.gen.v1.tiessearp.rev20170529.TiesseArp;
import org.onosproject.yang.gen.v1.tiessebgp.rev20170225.TiesseBgp;
import org.onosproject.yang.gen.v1.tiessebridge.rev20170225.TiesseBridge;
import org.onosproject.yang.gen.v1.tiessecli.rev20170703.TiesseCli;
import org.onosproject.yang.gen.v1.tiesseip.rev20170521.TiesseIp;
import org.onosproject.yang.gen.v1.tiesseospf.rev20170225.TiesseOspf;
import org.onosproject.yang.gen.v1.tiesseprefixlist.rev20170707.TiessePrefixList;
import org.onosproject.yang.gen.v1.tiesseswitch.rev20170522.TiesseSwitch;
import org.onosproject.yang.gen.v1.tiessesystem.rev20170225.TiesseSystem;
import org.onosproject.yang.gen.v1.tiessevlan.rev20170225.TiesseVlan;
import org.onosproject.yang.gen.v1.tiessebridge.rev20170225.TiesseBridge;
import org.onosproject.yang.model.DefaultYangModuleId;
import org.onosproject.yang.model.YangModuleId;
import org.onosproject.yang.runtime.AppModuleInfo;
import org.onosproject.yang.runtime.DefaultAppModuleInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Representation of examplenetconfdriver model registrator which registers My Device
 * models.
 */
@Component(immediate = true)
public class MyNetconfDriverModelRegistrator extends AbstractYangModelRegistrator {

    public MyNetconfDriverModelRegistrator() {
        super(IetfSystem.class, getAppInfo());
    }

    private static Map<YangModuleId, AppModuleInfo> getAppInfo() {
        Map<YangModuleId, AppModuleInfo> appInfo = new HashMap<>();

        appInfo.put(new DefaultYangModuleId("fpga-internal", "2015-11-30"),
                new DefaultAppModuleInfo(FpgaInternal.class, null));
        appInfo.put(new DefaultYangModuleId("iana-if-type", "2014-05-08"),
                new DefaultAppModuleInfo(IanaIfType.class, null));
        appInfo.put(new DefaultYangModuleId("ietf-yang-types", "2013-07-15"),
                new DefaultAppModuleInfo(IetfYangTypes.class, null));
        appInfo.put(new DefaultYangModuleId("ietf-x509-cert-to-name", "2013-03-26"),
                new DefaultAppModuleInfo(IetfX509CertToName.class, null));
        appInfo.put(new DefaultYangModuleId("ietf-system", "2014-08-06"),
                new DefaultAppModuleInfo(IetfSystem.class, null));
        appInfo.put(new DefaultYangModuleId("ietf-inet-types", "2013-07-15"),
                new DefaultAppModuleInfo(IetfInetTypes.class, null));
        appInfo.put(new DefaultYangModuleId("ietf-netconf-with-defaults", "2010-06-09"),
                new DefaultAppModuleInfo(IetfNetconfWithDefaults.class, null));
        appInfo.put(new DefaultYangModuleId("ietf-netconf-monitoring", "2010-10-04"),
                new DefaultAppModuleInfo(IetfNetconfMonitoring.class, null));
        appInfo.put(new DefaultYangModuleId("ietf-netconf-acm", "2012-02-22"),
                new DefaultAppModuleInfo(IetfNetconfAcm.class, null));
        appInfo.put(new DefaultYangModuleId("ietf-system-tls-auth", "2014-05-24"),
                new DefaultAppModuleInfo(IetfSystemTlsAuth.class, null));
        appInfo.put(new DefaultYangModuleId("rfc-2544", "2015-10-20"),
                new DefaultAppModuleInfo(Rfc2544.class, null));
        appInfo.put(new DefaultYangModuleId("netopeer-cfgnetopeer", "2013-02-14"),
                new DefaultAppModuleInfo(NetopeerCfgnetopeer.class, null));
        appInfo.put(new DefaultYangModuleId("ENTITY-STATE-TC-MIB", "2005-11-22"),
                new DefaultAppModuleInfo(EntityStateTcMib.class, null));
        appInfo.put(new DefaultYangModuleId("ietf-netconf-notifications", "2012-02-06"),
                new DefaultAppModuleInfo(IetfNetconfNotifications.class, null));
        appInfo.put(new DefaultYangModuleId("nc-notifications", "2008-07-14"),
                new DefaultAppModuleInfo(NcNotifications.class, null));
        appInfo.put(new DefaultYangModuleId("iana-crypt-hash", "2014-08-06"),
                new DefaultAppModuleInfo(IanaCryptHash.class, null));
        appInfo.put(new DefaultYangModuleId("ieee-types", "2008-05-22"),
                new DefaultAppModuleInfo(IeeeTypes.class, null));
        appInfo.put(new DefaultYangModuleId("svc-activation-types", "2015-10-27"),
                new DefaultAppModuleInfo(SvcActivationTypes.class, null));
        appInfo.put(new DefaultYangModuleId("ietf-netconf", "2011-06-01"),
                new DefaultAppModuleInfo(IetfNetconf.class, null));
        appInfo.put(new DefaultYangModuleId("ietf-system-microsemi", "2016-05-05"),
                new DefaultAppModuleInfo(IetfSystemMicrosemi.class, null));
        appInfo.put(new DefaultYangModuleId("notifications", "2008-07-14"),
                new DefaultAppModuleInfo(Notifications.class, null));
        appInfo.put(new DefaultYangModuleId("ietf-interfaces", "2014-05-08"),
                new DefaultAppModuleInfo(IetfInterfaces.class, null));
        appInfo.put(new DefaultYangModuleId("openconfig-bgp", "2017-07-30"),
                new DefaultAppModuleInfo(OpenconfigBgp.class, null));
        appInfo.put(new DefaultYangModuleId("openconfig-bgp-common", "2017-07-30"),
                new DefaultAppModuleInfo(OpenconfigBgpCommon.class, null));
        appInfo.put(new DefaultYangModuleId("openconfig-bgp-common-multiprotocol", "2017-07-30"),
                new DefaultAppModuleInfo(OpenconfigBgpCommonMultiprotocol.class, null));
        appInfo.put(new DefaultYangModuleId("openconfig-bgp-common-structure", "2017-07-30"),
                new DefaultAppModuleInfo(OpenconfigBgpCommonStructure.class, null));
        appInfo.put(new DefaultYangModuleId("openconfig-bgp-errors", "2017-07-30"),
                new DefaultAppModuleInfo(OpenconfigBgpErrors.class, null));
        appInfo.put(new DefaultYangModuleId("openconfig-bgp-global", "2017-07-30"),
                new DefaultAppModuleInfo(OpenconfigBgpGlobal.class, null));
        appInfo.put(new DefaultYangModuleId("openconfig-bgp-neighbor", "2017-07-30"),
                new DefaultAppModuleInfo(OpenconfigBgpNeighbor.class, null));
        appInfo.put(new DefaultYangModuleId("openconfig-bgp-policy", "2017-07-30"),
                new DefaultAppModuleInfo(OpenconfigBgpPolicy.class, null));
        appInfo.put(new DefaultYangModuleId("openconfig-bgp-types", "2017-07-30"),
                new DefaultAppModuleInfo(OpenconfigBgpTypes.class, null));
        appInfo.put(new DefaultYangModuleId("openconfig-inet-types", "2017-04-11"),
                new DefaultAppModuleInfo(OpenconfigInetTypes.class, null));
        appInfo.put(new DefaultYangModuleId("openconfig-inet-types", "2017-08-24"),
                new DefaultAppModuleInfo(OpenconfigInetTypes.class, null));
        appInfo.put(new DefaultYangModuleId("openconfig-interfaces", "2017-07-14"),
                new DefaultAppModuleInfo(OpenconfigInterfaces.class, null));
        appInfo.put(new DefaultYangModuleId("openconfig-policy-types", "2017-07-14"),
                new DefaultAppModuleInfo(OpenconfigPolicyTypes.class, null));
        appInfo.put(new DefaultYangModuleId("openconfig-routing-policy", "2017-07-14"),
                new DefaultAppModuleInfo(OpenconfigRoutingPolicy.class, null));
        appInfo.put(new DefaultYangModuleId("openconfig-types", "2017-08-16"),
                new DefaultAppModuleInfo(OpenconfigTypes.class, null));
        appInfo.put(new DefaultYangModuleId("openconfig-yang-types", "2017-07-30"),
                new DefaultAppModuleInfo(OpenconfigYangTypes.class, null));
        appInfo.put(new DefaultYangModuleId("tiesse-access-list", "2017-07-07"),
                new DefaultAppModuleInfo(TiesseAccessList.class, null));
        appInfo.put(new DefaultYangModuleId("tiesse-arp", "2017-05-29"),
                new DefaultAppModuleInfo(TiesseArp.class, null));
        appInfo.put(new DefaultYangModuleId("tiesse-bgp", "2017-02-25"),
                new DefaultAppModuleInfo(TiesseBgp.class, null));
        appInfo.put(new DefaultYangModuleId("tiesse-cli", "2017-07-03"),
                new DefaultAppModuleInfo(TiesseCli.class, null));
        appInfo.put(new DefaultYangModuleId("tiesse-ip", "2017-05-21"),
                new DefaultAppModuleInfo(TiesseIp.class, null));
        appInfo.put(new DefaultYangModuleId("tiesse-ospf", "2017-02-25"),
                new DefaultAppModuleInfo(TiesseOspf.class, null));
        appInfo.put(new DefaultYangModuleId("tiesse-prefix-list", "2017-07-07"),
                new DefaultAppModuleInfo(TiessePrefixList.class, null));
        appInfo.put(new DefaultYangModuleId("tiesse-vlan", "2017-02-25"),
                new DefaultAppModuleInfo(TiesseVlan.class, null));
        appInfo.put(new DefaultYangModuleId("tiesse-switch", "2017-05-22"),
                new DefaultAppModuleInfo(TiesseSwitch.class, null));
        appInfo.put(new DefaultYangModuleId("tiesse-system", "2017-02-25"),
                new DefaultAppModuleInfo(TiesseSystem.class, null));
        appInfo.put(new DefaultYangModuleId("tiesse-bridge", "2017-02-25"),
                new DefaultAppModuleInfo(TiesseBridge.class, null));
        return ImmutableMap.copyOf(appInfo);
    }
}
