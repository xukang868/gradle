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

package org.gradle.tooling.internal.provider.events;

import org.gradle.tooling.internal.protocol.events.InternalBuildProfileEvent;
import org.gradle.tooling.internal.protocol.events.InternalOperationDescriptor;
import org.gradle.tooling.internal.protocol.events.InternalTimeInterval;

public class DefaultBuildProfileEvent extends AbstractProgressEvent<InternalOperationDescriptor> implements InternalBuildProfileEvent {

    private final InternalTimeInterval totalBuildTime;

    public DefaultBuildProfileEvent(long eventTime, InternalOperationDescriptor descriptor, InternalTimeInterval totalBuildTime) {
        super(eventTime, descriptor);
        this.totalBuildTime = totalBuildTime;
    }

    @Override
    public String getDisplayName() {
        return "Build profiled";
    }

    @Override
    public InternalTimeInterval getTotalBuildTime() {
        return totalBuildTime;
    }
}
