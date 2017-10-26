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

package org.onosproject.drivers.examplenetconfdriver;


import static org.slf4j.LoggerFactory.getLogger;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onosproject.net.DeviceId;
import org.onosproject.net.meter.Meter.Unit;
import org.onosproject.net.meter.MeterOperation;
import org.onosproject.net.meter.MeterOperations;
import org.onosproject.net.meter.MeterProvider;
import org.onosproject.net.meter.MeterProviderRegistry;
import org.onosproject.net.meter.MeterProviderService;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;
import org.slf4j.Logger;

/**
 * Provider which uses an NETCONF controller to handle meters.
 *
 * TODO: move this to an architecture similar to FlowRuleDriverProvider in order
 * to use a behavior to discover meters.
 */
@Component(immediate = true, enabled = true)
public class MyDeviceMeterProvider extends AbstractProvider implements MeterProvider {

    private final Logger log = getLogger(getClass());

    //@Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    //protected NetconfController controller;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected MeterProviderRegistry providerRegistry;

    //@Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    //protected CoreService coreService;

    private MeterProviderService providerService;

    //private static final int COS_INDEX_1 = 1;
    //private static final short DEFAULT_OUTGOING_PRIO = 3;

    /**
     * Creates a OpenFlow meter provider.
     */
    public MyDeviceMeterProvider() {
        super(new ProviderId("netconf", "org.onosproject.provider.meter.examplenetconfdriver"));
    }

    @Activate
    public void activate() {
        providerService = providerRegistry.register(this);

    }

    @Deactivate
    public void deactivate() {
        providerRegistry.unregister(this);

        providerService = null;
    }

    @Override
    public void performMeterOperation(DeviceId deviceId, MeterOperations meterOps) {
        log.debug("Adding meterOps to examplenetconfdriver Meter Store");
    }

    @Override
    public void performMeterOperation(DeviceId deviceId, MeterOperation meterOp) { //TODO: write the device specific meter operation

    }

    private static long toBitsPerSec(long rate, Unit unit) {
        if (unit == Unit.KB_PER_SEC) {
            return rate * 8;
        } else {
            return -1;
        }
    }
}
