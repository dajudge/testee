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
import org.jboss.weld.bean.SessionBean;
import org.jboss.weld.ejb.spi.EjbDescriptor;
import org.jboss.weld.manager.BeanManagerImpl;

import java.util.function.Supplier;

public class EjbDescriptorHolder<T> {
    private final EjbDescriptor<T> descriptor;
    private final InterceptorChain chain;
    private final SessionBean<T> bean;
    private final BeanManagerImpl beanManager;

    public EjbDescriptorHolder(
            final EjbDescriptor<T> descriptor,
            final InterceptorChain chain,
            final SessionBean<T> bean,
            final BeanManagerImpl beanManager
    ) {
        this.descriptor = descriptor;
        this.chain = chain;
        this.bean = bean;
        this.beanManager = beanManager;
    }

    public InterceptorChain getInterceptorChain() {
        return chain;
    }

    public EjbDescriptor<T> getDescriptor() {
        return descriptor;
    }

    public SessionBean<T> getBean() {
        return bean;
    }

    public BeanManagerImpl getBeanManager() {
        return beanManager;
    }
}
