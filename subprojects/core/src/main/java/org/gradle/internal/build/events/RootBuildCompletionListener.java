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

package org.gradle.internal.build.events;

import org.gradle.internal.scan.UsedByScanPlugin;

/**
 * A listener that is notified on completion of the root build. The listener is notified after all user code has completed and the build outcome written to the logging system. The listener is notified immediately before the services for the build tree are torn down, so these services are available for use.
 *
 * <p>This is a Gradle scoped listener. The listener is not expected to be thread-safe, as these events are never delivered concurrently.
 */
@UsedByScanPlugin
public interface RootBuildCompletionListener {
    void rootBuildComplete();
}
