/*
 * Copyright (C) 2017 Alex Stockinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fi.testee.jms;

import fi.testee.spi.ResourceProvider;
import fi.testee.spi.scope.TestInstanceScope;

import javax.annotation.Resource;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.jms.Queue;

@TestInstanceScope
public class QueueResourceProvider implements ResourceProvider {
    @Override
    public Object resolve(final InjectionPoint injectionPoint) {
        final Resource annotation = injectionPoint.getAnnotated().getAnnotation(Resource.class);
        if (null == annotation) {
            return null;
        }
        if (Queue.class != injectionPoint.getType()) {
            return null;
        }
        return new TestEEfiQueue(annotation.mappedName());
    }

    @Override
    public Object resolve(final String jndiName, final String mappedName) {
        return null;
    }

}
