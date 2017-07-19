package com.dajudge.testee.ejb;

import com.dajudge.testee.exceptions.TesteeException;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jboss.weld.ejb.spi.EjbDescriptor;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;

import javax.annotation.Resource;
import javax.ejb.EJB;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 * Bridge between CDI and EJB dependency injection.
 *
 * @author Alex Stockinger, IT-Stockinger
 */
public class EjbBridge {
    private final Map<Type, EjbDescriptor<?>> ejbDescriptors;
    private final Map<EjbDescriptor<?>, SessionBeanContainer> containers;

    public EjbBridge(
            final Set<EjbDescriptor<?>> ejbDescriptors,
            final Consumer<Object> cdiInjection,
            final Function<Resource, Object> resourceInjection
    ) {
        final Consumer<Object> injection = cdiInjection
                .andThen(ejbInjection(EJB.class, this::injectEjb))
                .andThen(ejbInjection(Resource.class, injectResources(resourceInjection)));
        this.ejbDescriptors = ejbDescriptors.stream().collect(toMap(it -> it.getBeanClass(), it -> it));
        this.containers = ejbDescriptors.stream().collect(toMap(it -> it, it -> toBeanContainer(it, injection)));
    }

    private BiConsumer<Object, Field> injectResources(final Function<Resource, Object> resourceInjection) {
        return (o,f) -> inject(o, f, resourceInjection.apply(f.getAnnotation(Resource.class)));
    }

    private Consumer<Object> ejbInjection(
            final Class<? extends Annotation> annotationClass,
            final BiConsumer<Object, Field> injector
    ) {
        return o -> stream(FieldUtils.getAllFields(o.getClass()))
                .filter(it -> it.getAnnotation(annotationClass) != null)
                .forEach(it -> injector.accept(o, it));
    }

    private void injectEjb(final Object o, final Field field) {
        inject(o, field, createInstance(lookupDescriptor(field.getType())).createResource().getInstance());
    }

    private void inject(Object o, Field field, Object instanceToInject) {
        try {
            field.setAccessible(true);
            field.set(o, instanceToInject);
        } catch (final IllegalAccessException e) {
            throw new TesteeException("Failed to inject into field", e);
        }
    }

    private SessionBeanContainer toBeanContainer(
            final EjbDescriptor<?> desc, Consumer<Object> injection) {
        return new SingletonBeanContainer(desc, injection);
    }

    public EjbDescriptor<?> lookupDescriptor(final Type type) {
        return ejbDescriptors.get(type);
    }

    public ResourceReferenceFactory<Object> createInstance(final EjbDescriptor<?> descriptor) {
        return containers.get(descriptor).get();
    }
}
