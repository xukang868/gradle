/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.tooling.internal.provider.runner;

import org.gradle.initialization.BuildEventConsumer;
import org.gradle.profile.BuildProfile;
import org.gradle.profile.ProfileListener;
import org.gradle.tooling.internal.protocol.events.InternalOperationDescriptor;
import org.gradle.tooling.internal.protocol.events.InternalTimeInterval;
import org.gradle.tooling.internal.provider.events.DefaultBuildProfileEvent;
import org.gradle.tooling.internal.provider.events.DefaultOperationDescriptor;
import org.gradle.tooling.internal.provider.events.DefaultTimeInterval;

import java.time.Instant;

class ClientForwardingBuildProfileListener implements ProfileListener {
    private final BuildEventConsumer consumer;

    ClientForwardingBuildProfileListener(BuildEventConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void buildFinished(BuildProfile result) {
        InternalOperationDescriptor descriptor = new DefaultOperationDescriptor("BUILD_PROFILE", "Build profile", "Build profile", null);
        InternalTimeInterval totalBuildTime = new DefaultTimeInterval(Instant.ofEpochMilli(result.getBuildStarted()), Instant.ofEpochMilli(result.getBuildFinished()));
        consumer.dispatch(new DefaultBuildProfileEvent(result.getBuildFinished(), descriptor, totalBuildTime));
    }
}
