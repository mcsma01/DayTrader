/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.geronimo.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.management.ObjectName;

import org.apache.geronimo.gbean.GBeanInfo;
import org.apache.geronimo.gbean.GBeanInfoFactory;
import org.apache.geronimo.transaction.context.TransactionContext;
import org.apache.geronimo.transaction.context.TransactionContextManager;

/**
 * @version $Rev: 46019 $ $Date: 2004-09-14 02:56:06 -0700 (Tue, 14 Sep 2004) $
 */
public final class AppClientContainer {
    private static final Class[] MAIN_ARGS = {String[].class};

    private final String mainClassName;
    private final AppClientPlugin jndiContext;
    private final ObjectName appClientModuleName;
    private final Method mainMethod;
    private final ClassLoader classLoader;
    private final TransactionContextManager transactionContextManager;

    public AppClientContainer(String mainClassName, ObjectName appClientModuleName, ClassLoader classLoader, AppClientPlugin jndiContext, TransactionContextManager transactionContextManager) throws Exception {
        this.mainClassName = mainClassName;
        this.appClientModuleName = appClientModuleName;
        this.classLoader = classLoader;
        this.jndiContext = jndiContext;
        this.transactionContextManager = transactionContextManager;

        try {
            Class mainClass = classLoader.loadClass(mainClassName);
            mainMethod = mainClass.getMethod("main", MAIN_ARGS);
        } catch (ClassNotFoundException e) {
            throw new AppClientInitializationException("Unable to load Main-Class " + mainClassName, e);
        } catch (NoSuchMethodException e) {
            throw new AppClientInitializationException("Main-Class " + mainClassName + " does not have a main method", e);
        }
    }

    public ObjectName getAppClientModuleName() {
        return appClientModuleName;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public void main(String[] args) throws Exception {
        Thread thread = Thread.currentThread();

        ClassLoader contextClassLoader = thread.getContextClassLoader();
        thread.setContextClassLoader(classLoader);
        TransactionContext oldTransactionContext = transactionContextManager.getContext();
        TransactionContext currentTransactionContext = null;
        try {
            jndiContext.startClient(appClientModuleName);
            currentTransactionContext = transactionContextManager.newUnspecifiedTransactionContext();
            mainMethod.invoke(null, new Object[]{args});

        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception) {
                throw (Exception) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new Error(e);
        } finally {
            jndiContext.stopClient(appClientModuleName);

            thread.setContextClassLoader(contextClassLoader);
            transactionContextManager.setContext(oldTransactionContext);
            currentTransactionContext.commit();
        }
    }

    public static final GBeanInfo GBEAN_INFO;

    static {
        GBeanInfoFactory infoFactory = new GBeanInfoFactory(AppClientContainer.class);

        infoFactory.addOperation("main", new Class[]{String[].class});
        infoFactory.addAttribute("mainClassName", String.class, true);
        infoFactory.addAttribute("appClientModuleName", ObjectName.class, true);
        infoFactory.addAttribute("classLoader", ClassLoader.class, false);
        infoFactory.addReference("JNDIContext", AppClientPlugin.class);
        infoFactory.addReference("TransactionContextManager", TransactionContextManager.class);

        infoFactory.setConstructor(new String[]{"mainClassName", "appClientModuleName", "classLoader", "JNDIContext", "TransactionContextManager"});

        GBEAN_INFO = infoFactory.getBeanInfo();
    }

    public static GBeanInfo getGBeanInfo() {
        return GBEAN_INFO;
    }
}
