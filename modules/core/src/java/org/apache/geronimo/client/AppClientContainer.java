/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Geronimo" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Geronimo", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * ====================================================================
 */
package org.apache.geronimo.client;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.geronimo.core.service.InvocationResult;
import org.apache.geronimo.core.service.Invocation;
import org.apache.geronimo.core.service.Interceptor;
import org.apache.geronimo.kernel.deployment.DeploymentException;
import org.apache.geronimo.kernel.management.State;
import org.apache.geronimo.naming.java.ComponentContextInterceptor;
import org.apache.geronimo.naming.java.ReadOnlyContext;

/**
 * @jmx.mbean
 *
 * @version $Revision: 1.7 $ $Date: 2003/11/26 20:54:27 $
 */
public class AppClientContainer implements AppClientContainerMBean {
    private static final Class[] MAIN_ARGS = {String[].class};

    private String mainClassName;
    private URL clientURL;
    private ReadOnlyContext compContext;

    private Interceptor firstInterceptor;

    public AppClientContainer() {
    }

    public AppClientContainer(URL clientURL, String mainClassName, ReadOnlyContext compContext) {
        this.clientURL = clientURL;
        this.mainClassName = mainClassName;
        this.compContext = compContext;
    }

    /**
     * @jmx.managed-attribute
     */
    public void setMainClassName(String className) {
        mainClassName = className;
    }

    /**
     * @jmx.managed-attribute
     */
    public String getMainClassName() {
        return mainClassName;
    }

    /**
     * @jmx.managed-attribute
     */
    public URL getClientURL() {
        return clientURL;
    }

    /**
     * @jmx.managed-attribute
     */
    public void setClientURL(URL clientURL) {
        this.clientURL = clientURL;
    }

    /**
     * @jmx.managed-attribute
     */
    public ReadOnlyContext getComponentContext() {
        return compContext;
    }

    /**
     * @jmx.managed-attribute
     */
    public void setComponentContext(ReadOnlyContext compContext) {
        this.compContext = compContext;
    }

    public void doStart() throws Exception {
        ClassLoader clientCL = new URLClassLoader(new URL[] { clientURL }, Thread.currentThread().getContextClassLoader());
        Method mainMethod;
        try {
            Class mainClass = clientCL.loadClass(mainClassName);
            mainMethod = mainClass.getMethod("main", MAIN_ARGS);
        } catch (ClassNotFoundException e) {
            throw new DeploymentException("Unable to load Main-Class " + mainClassName, e);
        } catch (NoSuchMethodException e) {
            throw new DeploymentException("Main-Class " + mainClassName + " does not have a main method", e);
        }

        firstInterceptor = new MainInvokerInterceptor(mainMethod);
        firstInterceptor = new ComponentContextInterceptor(firstInterceptor, compContext);
    }

    public void doStop() throws Exception {
    }

    public final InvocationResult invoke(Invocation invocation) throws Throwable {
//        if (getStateInstance() != State.RUNNING) {
//            throw new IllegalStateException("invoke can only be called after the Container has started");
//        }
        return firstInterceptor.invoke(invocation);
    }

}
