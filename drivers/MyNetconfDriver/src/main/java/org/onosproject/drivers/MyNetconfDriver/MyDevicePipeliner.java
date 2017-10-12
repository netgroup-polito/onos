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
package org.onosproject.drivers.MyNetconfDriver;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.onlab.osgi.ServiceDirectory;
import org.onosproject.net.DeviceId;
import org.onosproject.net.behaviour.NextGroup;
import org.onosproject.net.behaviour.Pipeliner;
import org.onosproject.net.behaviour.PipelinerContext;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onosproject.net.flow.FlowRule;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.flowobjective.FilteringObjective;
import org.onosproject.net.flowobjective.ForwardingObjective;
import org.onosproject.net.flowobjective.NextObjective;
import org.onosproject.net.flowobjective.Objective;
import org.onosproject.net.flowobjective.ObjectiveError;
import org.slf4j.Logger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalNotification;

/**
 * Support for FlowObjectives in MyDevice.
 *
 */
public class MyDevicePipeliner extends AbstractHandlerBehaviour implements Pipeliner {

    protected final Logger log = getLogger(getClass());
    protected ServiceDirectory serviceDirectory;
    protected FlowRuleService flowRuleService;
    protected DeviceId deviceId;
    protected Cache<Integer, NextObjective> pendingNext;


    @Override
    public void init(DeviceId deviceId, PipelinerContext context) {
        this.serviceDirectory = context.directory();
        this.deviceId = deviceId;

        flowRuleService = serviceDirectory.get(FlowRuleService.class);

        pendingNext = CacheBuilder.newBuilder()
                .expireAfterWrite(20, TimeUnit.SECONDS)
                .removalListener((RemovalNotification<Integer, NextObjective> notification) -> {
                    if (notification.getCause() == RemovalCause.EXPIRED) {
                        notification.getValue().context()
                                .ifPresent(c -> c.onError(notification.getValue(),
                                        ObjectiveError.FLOWINSTALLATIONFAILED));
                    }
                }).build();

        log.debug("Loaded handler behaviour MyDevice Pipeliner for " + handler().data().deviceId().uri());
    }

    @Override
    public void filter(FilteringObjective filterObjective) { //TODO

    }

    @Override
    public void forward(ForwardingObjective forwardObjective) { //TODO

    }

    @Override
    public void next(NextObjective nextObjective) {

        pendingNext.put(nextObjective.id(), nextObjective);
        nextObjective.context().ifPresent(context -> context.onSuccess(nextObjective));

        log.debug("next() of MyDevice Pipeliner called for "
                + handler().data().deviceId().uri()
                + ". Objective: " + nextObjective);

    }

    @Override
    public List<String> getNextMappings(NextGroup nextGroup) {

        log.debug("getNextMappings() of MyDevice Pipeliner called for "
                + handler().data().deviceId().uri()
                + ". Objective: " + nextGroup);

        return new ArrayList<String>();
    }

    protected void installObjective(FlowRule.Builder ruleBuilder, Objective objective) { //TODO

    }
}
