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
import org.gradle.tooling.model.profile.BuildProfile

@ToolingApiVersion('>=5.1')
@TargetGradleVersion('>=5.1')
class BuildProfileCrossVersionSpec extends ToolingApiSpecification {

    def "builds profile"() {
        given:
        buildFile << """
            task helloWorld() {
                doLast {
                    println "Hello World!"
                }
            }
            
            task unused() {}
        """
        settingsFile << """
            rootProject.name = 'some project'
        """

        when:
        BuildProfile profile
        withConnection {
            newBuild()
                .forTasks('helloWorld')
                .requestProfile { profile = it }
                .run()
        }

        then:
        profile != null
        profile.totalBuildTime.start < profile.totalBuildTime.end
    }

}
