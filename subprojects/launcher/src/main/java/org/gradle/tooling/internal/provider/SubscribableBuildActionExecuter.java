/*
 * Copyright 2017 the original author or authors.
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

package org.gradle.tooling.internal.provider;

import org.gradle.initialization.BuildEventConsumer;
import org.gradle.initialization.BuildRequestContext;
import org.gradle.internal.event.ListenerManager;
import org.gradle.internal.invocation.BuildAction;
import org.gradle.internal.operations.BuildOperationListener;
import org.gradle.internal.operations.BuildOperationListenerManager;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.launcher.exec.BuildActionExecuter;
import org.gradle.launcher.exec.BuildActionParameters;
import org.gradle.launcher.exec.BuildActionResult;
import org.gradle.profile.ProfileListener;

import java.util.ArrayList;
import java.util.List;

public class SubscribableBuildActionExecuter implements BuildActionExecuter<BuildActionParameters> {
    private final BuildActionExecuter<BuildActionParameters> delegate;
    private final ListenerRegistry<BuildOperationListener> buildOperationListenerRegistry;
    private final ListenerRegistry<ProfileListener> profileListenerRegistry;
    private final List<? extends SubscribableBuildActionRunnerRegistration> registrations;

    public SubscribableBuildActionExecuter(BuildActionExecuter<BuildActionParameters> delegate, final BuildOperationListenerManager buildOperationListenerManager, final ListenerManager listenerManager, List<? extends SubscribableBuildActionRunnerRegistration> registrations) {
        this.delegate = delegate;
        this.buildOperationListenerRegistry = new ListenerRegistry<BuildOperationListener>() {
            @Override
            protected void subscribe(BuildOperationListener listener) {
                buildOperationListenerManager.addListener(listener);
            }
            @Override
            protected void unsubscribe(BuildOperationListener listener) {
                buildOperationListenerManager.removeListener(listener);
            }
        };
        this.profileListenerRegistry = new ListenerRegistry<ProfileListener>() {
            @Override
            protected void subscribe(ProfileListener listener) {
                listenerManager.addListener(listener);
            }
            @Override
            protected void unsubscribe(ProfileListener listener) {
                listenerManager.removeListener(listener);
            }
        };
        this.registrations = registrations;
    }

    @Override
    public BuildActionResult execute(BuildAction action, BuildRequestContext requestContext, BuildActionParameters actionParameters, ServiceRegistry contextServices) {
        if (action instanceof SubscribableBuildAction) {
            SubscribableBuildAction subscribableBuildAction = (SubscribableBuildAction) action;
            BuildEventConsumer eventConsumer = requestContext.getEventConsumer();
            registerListeners(subscribableBuildAction.getClientSubscriptions(), eventConsumer);
        }
        try {
            return delegate.execute(action, requestContext, actionParameters, contextServices);
        } finally {
            unsubscribeListeners();
        }
    }

    private void registerListeners(BuildClientSubscriptions clientSubscriptions, BuildEventConsumer eventConsumer) {
        for (SubscribableBuildActionRunnerRegistration registration : registrations) {
            buildOperationListenerRegistry.subscribeAll(registration.createBuildOperationListeners(clientSubscriptions, eventConsumer));
            profileListenerRegistry.subscribeAll(registration.createProfileListeners(clientSubscriptions, eventConsumer));
        }
    }

    private void unsubscribeListeners() {
        buildOperationListenerRegistry.unsubscribeAll();
        profileListenerRegistry.unsubscribeAll();
    }

    private abstract static class ListenerRegistry<T> {
        private List<T> listeners = new ArrayList<T>();

        void subscribeAll(Iterable<T> additionalListeners) {
            for (T listener : additionalListeners) {
                subscribe(listener);
                listeners.add(listener);
            }
        }

        void unsubscribeAll() {
            for (T listener : listeners) {
                unsubscribe(listener);
            }
            listeners.clear();
        }

        protected abstract void subscribe(T listener);

        protected abstract void unsubscribe(T listener);
    }

}
