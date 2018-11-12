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

package org.gradle.tooling.internal.consumer.parameters;

import org.gradle.tooling.internal.protocol.InternalBuildProfileConsumer;
import org.gradle.tooling.internal.protocol.profile.InternalBuildProfile;
import org.gradle.tooling.internal.protocol.profile.InternalTimeInterval;
import org.gradle.tooling.model.internal.profile.DefaultBuildProfile;
import org.gradle.tooling.model.internal.profile.DefaultTimeInterval;
import org.gradle.tooling.model.profile.BuildProfile;
import org.gradle.tooling.model.profile.TimeInterval;

import java.util.List;
import java.util.function.Consumer;

class BuildProfileConsumerAdapter implements InternalBuildProfileConsumer {
    private final List<Consumer<BuildProfile>> consumers;

    public BuildProfileConsumerAdapter(List<Consumer<BuildProfile>> consumers) {
        this.consumers = consumers;
    }

    @Override
    public void accept(Object o) {
        if (o instanceof BuildProfile) {
            forward((BuildProfile) o);
        }
        else if (o instanceof InternalBuildProfile) {
            forward(toToolingModel((InternalBuildProfile) o));
        }
    }

    private BuildProfile toToolingModel(InternalBuildProfile internalBuildProfile) {
        return new DefaultBuildProfile(toToolingModel(internalBuildProfile.getTotalBuildTime()));
    }

    private TimeInterval toToolingModel(InternalTimeInterval internalTotalBuildTime) {
        return new DefaultTimeInterval(internalTotalBuildTime.getStart(), internalTotalBuildTime.getEnd());
    }

    private void forward(BuildProfile buildProfile) {
        for (Consumer<BuildProfile> consumer : consumers) {
            // TODO make fail-safe
            consumer.accept(buildProfile);
        }
    }
}
