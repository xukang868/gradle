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

package org.gradle.api.internal.artifacts.transform;

import com.google.common.collect.ImmutableSortedMap;
import org.gradle.api.Action;
import org.gradle.api.artifacts.transform.ArtifactTransformDependencies;
import org.gradle.api.internal.attributes.ImmutableAttributes;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.tasks.DefaultPropertySpecFactory;
import org.gradle.api.internal.tasks.execution.DefaultTaskProperties;
import org.gradle.api.internal.tasks.execution.TaskFingerprinter;
import org.gradle.api.internal.tasks.execution.TaskProperties;
import org.gradle.api.internal.tasks.properties.PropertyWalker;
import org.gradle.internal.Cast;
import org.gradle.internal.UncheckedException;
import org.gradle.internal.fingerprint.CurrentFileCollectionFingerprint;
import org.gradle.internal.hash.HashCode;
import org.gradle.internal.instantiation.InstantiatorFactory;
import org.gradle.internal.snapshot.impl.ArrayValueSnapshot;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

public class TransformerFromCallable extends AbstractTransformer<Callable<List<File>>> {

    private final InstantiatorFactory instantiatorFactory;
    private final Class<?> parameterType;
    private final Action<?> configurationAction;

    public TransformerFromCallable(ImmutableAttributes from, Class<? extends Callable<List<File>>> actionType, Class<?> parameterType, HashCode implementationHash, @Nullable Action<?> parameterConfigurationAction, InstantiatorFactory instantiatorFactory) {
        super(actionType, ArrayValueSnapshot.EMPTY, implementationHash, instantiatorFactory, from);
        this.instantiatorFactory = instantiatorFactory;
        this.parameterType = parameterType;
        this.configurationAction = parameterConfigurationAction;
    }

    @Override
    public List<File> transform(File primaryInput, File outputDir, ArtifactTransformDependencies dependencies) {
        Callable<List<File>> transformer = newTransformer(primaryInput, outputDir, dependencies);
        try {
            List<File> result = transformer.call();
            validateOutputs(primaryInput, outputDir, result);
            return result;
        } catch (Exception e) {
            throw UncheckedException.throwAsUncheckedException(e);
        }
    }

    @Override
    public ImmutableSortedMap<String, CurrentFileCollectionFingerprint> getInputFileFingerprints(TaskFingerprinter taskFingerprinter, File primaryInput, PropertyWalker propertyWalker, FileResolver pathToFileResolver, Object owner, ArtifactTransformDependencies artifactTransformDependencies) {
        Callable<List<File>> transformerInstance = newTransformer(primaryInput, null, artifactTransformDependencies);

        TaskProperties properties = DefaultTaskProperties.resolveInputs(propertyWalker, new DefaultPropertySpecFactory(owner, pathToFileResolver), owner.toString(), transformerInstance);
        return taskFingerprinter.fingerprintTaskFiles(owner, properties.getInputFileProperties());
    }

    @Override
    protected Object getParameters() {
        Object configuration = instantiatorFactory.inject().newInstance(parameterType);
        if (configurationAction != null) {
            Cast.<Action<Object>>uncheckedNonnullCast(configurationAction).execute(configuration);
        }
        return configuration;
    }
}
