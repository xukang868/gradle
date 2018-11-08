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

package org.gradle.integtests.tooling.r51

import org.gradle.integtests.tooling.fixture.TargetGradleVersion
import org.gradle.integtests.tooling.fixture.ToolingApiSpecification
import org.gradle.integtests.tooling.fixture.ToolingApiVersion
import org.gradle.tooling.events.OperationType
import org.gradle.tooling.events.profile.BuildProfileEvent

@ToolingApiVersion('>=5.1')
@TargetGradleVersion('>=5.1')
class BuildProfileCrossVersionSpec extends ToolingApiSpecification {

    def "receive progress events when launching a build"() {
        given:
        buildFile << """
            apply plugin: 'base'
        """

        when: "launching a build"
        def events = []
        def listener = { events << it }
        withConnection {
            newBuild()
                .forTasks('check')
                .withArguments("--profile")
                .addProgressListener(listener, OperationType.PROFILE)
                .run()
        }

        then:
        events.size() == 1
        events[0] instanceof BuildProfileEvent
        events[0].totalBuildTime.start < events[0].totalBuildTime.end
    }
}
