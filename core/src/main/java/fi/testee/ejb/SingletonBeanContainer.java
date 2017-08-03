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
package fi.testee.ejb;

import fi.testee.deployment.InterceptorChain;
import fi.testee.exceptions.TestEEfiException;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.jboss.weld.injection.spi.ResourceReference;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

public class SingletonBeanContainer<T> implements ResourceReferenceFactory<T> {
    private final Provider<T> factory;
    private final T proxyInstance;
    private T instance;

    public SingletonBeanContainer(
            final Class<T> clazz,
            final Provider<T> factory,
            final InterceptorChain chain
    ) {
        this.factory = factory;
        proxyInstance = createProxy(clazz, this::instance, chain);
    }

    @SuppressWarnings("unchecked")
    private static <T> T createProxy(
            final Class<T> clazz,
            final Supplier<T> producer,
            final InterceptorChain chain
    ) {
        try {
            final ProxyFactory proxyFactory = new ProxyFactory();
            proxyFactory.setSuperclass(clazz);
            proxyFactory.setFilter(m -> m.getDeclaringClass() != Object.class);
            final Class<T> proxyClass = proxyFactory.createClass();
            final Object instance = proxyClass.newInstance();
            ((ProxyObject) instance).setHandler(methodHandler(producer, chain));
            return (T) instance;
        } catch (final IllegalAccessException | InstantiationException e) {
            throw new TestEEfiException("Failed to create proxy instance of " + clazz, e);
        }
    }

    private static <T> MethodHandler methodHandler(
            final Supplier<T> producer,
            final InterceptorChain interceptorChain
    ) {
        return (self, thisMethod, proceed, args) -> {
            final T target = producer.get();
            return interceptorChain.invoke(target, thisMethod, args,
                    () -> {
                        try {
                            return thisMethod.invoke(target, args);
                        } catch (final InvocationTargetException e) {
                            throw e.getTargetException();
                        }
                    });
        };
    }

    @Override
    public ResourceReference<T> createResource() {
        return new ResourceReference<T>() {
            @Override
            public T getInstance() {
                return proxyInstance;
            }

            @Override
            public void release() {
                if (instance != null) {
                    invoke(instance, PreDestroy.class);
                }
            }
        };
    }

    private synchronized T instance() {
        if (null == instance) {
            instance = factory.get();
            invoke(instance, PostConstruct.class);
        }
        return instance;
    }

    private void invoke(final T t, final Class<? extends Annotation> annotation) {
        Class<?> c = t.getClass();
        while (c != null && c != Object.class) {
            invoke(t, c, annotation);
            c = c.getSuperclass();
        }
    }

    private void invoke(final T t, final Class<?> c, final Class<? extends Annotation> annotation) {
        final Set<Method> candidates = stream(c.getDeclaredMethods())
                .filter(it -> it.getAnnotation(annotation) != null)
                .collect(toSet());
        if (candidates.isEmpty()) {
            return;
        }
        if (candidates.size() > 1) {
            throw new TestEEfiException("Only one @" + annotation.getSimpleName() + " method is allowed per class");
        }
        // TODO check for correct modifiers etc.
        final Method method = candidates.iterator().next();
        method.setAccessible(true);
        try {
            method.invoke(t);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw new TestEEfiException("Failed to invoke @" + annotation.getSimpleName() + " method " + method, e);
        }
    }
}
