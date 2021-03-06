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
package fi.testee.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public final class AnnotationUtils {
    private AnnotationUtils() {
    }

    public static <T extends Annotation> List<T> collectAnnotations(
            final Class<?> clazz,
            final Class<T> annotation
    ) {
        final List<T> ret = new ArrayList<>();
        final T[] annotations = clazz.getAnnotationsByType(annotation);
        if (annotations != null) {
            ret.addAll(asList(annotations));
        }
        if (clazz.getSuperclass() != null) {
            ret.addAll(collectAnnotations(clazz.getSuperclass(), annotation));
        }
        return ret;
    }

    public static <T extends Annotation> T firstByTypeHierarchy(
            final Class<?> clazz,
            final Class<T> annotation
    ) {
        final List<T> ret = collectAnnotations(clazz, annotation);
        return ret.isEmpty() ? null : ret.get(0);
    }

    public static boolean hasAtLeastOneOf(final Field field, final Class<? extends Annotation>... annotations) {
        for (final Class<? extends Annotation> annotation : annotations) {
            if (field.getAnnotation(annotation) != null) {
                return true;
            }
        }
        return true;
    }

    public static Map<Class<? extends Annotation>, Collection<Field>> groupByAnnotation(
            final Collection<Field> fields,
            final Class<? extends Annotation>... annotations
    ) {
        final Map<Class<? extends Annotation>, Collection<Field>> ret = new HashMap<>();
        for (final Class<? extends Annotation> annotation : annotations) {
            ret.put(annotation, new HashSet<>());
            for (final Field field : fields) {
                if (field.getAnnotation(annotation) != null) {
                    ret.get(annotation).add(field);
                }
            }
        }
        return ret;
    }
}
