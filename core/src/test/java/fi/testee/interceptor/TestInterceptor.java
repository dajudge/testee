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
package fi.testee.interceptor;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@UseInterceptor
@Interceptor
public class TestInterceptor {
    @Resource
    private EJBContext ejbContext;

    public static class Invocation {
        public final Object target;
        public final Method method;

        public Invocation(final Object target, final Method method) {
            this.target = target;
            this.method = method;
        }
    }

    public static final List<Invocation> INVOCATIONS = new ArrayList<>();

    @AroundInvoke
    public Object logMethodEntry(final InvocationContext invocationContext) throws Exception {
        assertNotNull(ejbContext);
        INVOCATIONS.add(new Invocation(invocationContext.getTarget(), invocationContext.getMethod()));
        return invocationContext.proceed();
    }
}
