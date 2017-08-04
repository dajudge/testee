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
package fi.testee.spi;

import org.jboss.weld.context.CreationalContextImpl;

import java.util.Set;

/**
 * Access to dependency injection.
 *
 * @author Alex Stockinger, IT-Stockinger
 */
public interface DependencyInjection {
    <T> Set<T> getInstancesOf(Class<T> clazz, ReleaseCallbackHandler handler);

    <T> T getInstanceOf(Class<T> clazz, ReleaseCallbackHandler handler);

    <T> void inject(T o, ReleaseCallbackHandler handler);
}
