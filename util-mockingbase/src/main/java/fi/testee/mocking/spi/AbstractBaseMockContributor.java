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
package fi.testee.mocking.spi;

import fi.testee.exceptions.TestEEfiException;
import fi.testee.utils.ReflectionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public abstract class AbstractBaseMockContributor implements MockContributor {
    @Resource(mappedName = "testeefi/instance/instance")
    private Object testInstance;

        @Override
        public Map<Field, Object> contributeMocks() {
            injectMocks(testInstance);
            return stream(FieldUtils.getAllFields(testInstance.getClass()))
                    .filter(this::isMockField)
                    .collect(Collectors.toMap(
                            it -> it,
                            it -> {
                                try {
                                    it.setAccessible(true);
                                    return it.get(testInstance);
                                } catch (IllegalAccessException e) {
                                    throw new TestEEfiException("Failed to retrieve mock from test instance", e);
                                }
                            }
                    ));
    }

    protected abstract boolean isMockField(Field field);

    protected abstract void injectMocks(Object testInstance);
}
