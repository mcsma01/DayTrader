/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.geronimo.axis.testUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.management.ObjectName;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.geronimo.axis.AxisGeronimoUtils;
import org.apache.geronimo.connector.outbound.connectiontracking.ConnectionTrackingCoordinator;
import org.apache.geronimo.gbean.GBeanData;
import org.apache.geronimo.gbean.GBeanInfo;
import org.apache.geronimo.j2ee.management.impl.J2EEServerImpl;
import org.apache.geronimo.kernel.Kernel;
import org.apache.geronimo.pool.ThreadPool;
import org.apache.geronimo.system.serverinfo.ServerInfo;
import org.apache.geronimo.timer.vm.VMStoreThreadPooledNonTransactionalTimer;
import org.apache.geronimo.timer.vm.VMStoreThreadPooledTransactionalTimer;
import org.apache.geronimo.transaction.manager.TransactionManagerImpl;
import org.apache.geronimo.transaction.context.TransactionContextManager;

/**
 * @version $Rev: $ $Date: $
 */
public class J2EEManager {
    public static final Log log = LogFactory.getLog(J2EEManager.class);

    public void init() throws AxisFault {
    }
    
    public void startJ2EEContainer(Kernel kernel) throws AxisFault {
        try {
            String str =
                    System.getProperty(javax.naming.Context.URL_PKG_PREFIXES);
            if (str == null) {
                str = ":org.apache.geronimo.naming";
            } else {
                str = str + ":org.apache.geronimo.naming";
            }
            System.setProperty(javax.naming.Context.URL_PKG_PREFIXES, str);
            setUpTransactionManager(kernel);
            setUpTimer(kernel);
            
            GBeanData serverInfoGBean = new GBeanData(AxisGeronimoConstants.J2EE_SERVER_INFO,ServerInfo.GBEAN_INFO);
            serverInfoGBean.setAttribute("baseDirectory", ".");
            AxisGeronimoUtils.startGBeanOnlyIfNotStarted(AxisGeronimoConstants.J2EE_SERVER_INFO, 
                            serverInfoGBean, kernel,Thread.currentThread().getContextClassLoader());
            
            GBeanData j2eeServerGBean = new GBeanData(AxisGeronimoConstants.J2EE_SERVER_NAME,J2EEServerImpl.GBEAN_INFO);
            j2eeServerGBean.setReferencePatterns("ServerInfo", Collections.singleton(AxisGeronimoConstants.J2EE_SERVER_INFO));
            AxisGeronimoUtils.startGBeanOnlyIfNotStarted(AxisGeronimoConstants.J2EE_SERVER_NAME, j2eeServerGBean, kernel,Thread.currentThread().getContextClassLoader());
                    

            // //load mock resource adapter for mdb
            // setUpResourceAdapter(kernel);
            startEJBContainer(kernel);
            startWebContainer(kernel);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    public void stopJ2EEContainer(Kernel kernel) throws AxisFault {
        try {
            stopWebContainer(kernel);
            stopEJBContainer(kernel);
            stopTransactionManager(kernel);
            stopTimer(kernel);
            AxisGeronimoUtils.stopGBean(AxisGeronimoConstants.J2EE_SERVER_INFO, kernel);
            AxisGeronimoUtils.stopGBean(AxisGeronimoConstants.J2EE_SERVER_NAME, kernel);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    private void setUpTransactionManager(Kernel kernel) throws AxisFault {
        try {
            GBeanData tmGBean = new GBeanData(AxisGeronimoConstants.TRANSACTION_MANAGER_NAME,TransactionManagerImpl.GBEAN_INFO);
            Set rmpatterns = new HashSet();
            rmpatterns.add(ObjectName.getInstance("geronimo.server:j2eeType=JCAManagedConnectionFactory,*"));
            tmGBean.setAttribute("defaultTransactionTimeoutSeconds", new Integer(10));
            tmGBean.setReferencePatterns("ResourceManagers", rmpatterns);
            AxisGeronimoUtils.startGBeanOnlyIfNotStarted(AxisGeronimoConstants.TRANSACTION_MANAGER_NAME, tmGBean,
                 kernel,Thread.currentThread().getContextClassLoader());
            
            GBeanData tcmGBean = new GBeanData(AxisGeronimoConstants.TRANSACTION_CONTEXT_MANAGER_NAME,TransactionContextManager.GBEAN_INFO);
            tcmGBean.setReferencePattern("TransactionManager", AxisGeronimoConstants.TRANSACTION_MANAGER_NAME);
            AxisGeronimoUtils.startGBeanOnlyIfNotStarted(AxisGeronimoConstants.TRANSACTION_CONTEXT_MANAGER_NAME, 
                tcmGBean, kernel,Thread.currentThread().getContextClassLoader());
            
            GBeanData trackedConnectionAssociator = new GBeanData(AxisGeronimoConstants.TRACKED_CONNECTION_ASSOCIATOR_NAME,ConnectionTrackingCoordinator.GBEAN_INFO);
            AxisGeronimoUtils.startGBeanOnlyIfNotStarted(AxisGeronimoConstants.TRACKED_CONNECTION_ASSOCIATOR_NAME,
                 trackedConnectionAssociator, kernel,Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    private void stopTransactionManager(Kernel kernel) throws AxisFault {
        try {
            AxisGeronimoUtils.stopGBean(AxisGeronimoConstants.TRANSACTION_MANAGER_NAME, kernel);
            AxisGeronimoUtils.stopGBean(AxisGeronimoConstants.TRANSACTION_CONTEXT_MANAGER_NAME, kernel);
            AxisGeronimoUtils.stopGBean(AxisGeronimoConstants.TRACKED_CONNECTION_ASSOCIATOR_NAME, kernel);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    public static void setUpTimer(Kernel kernel) throws Exception {
        GBeanData threadPoolGBean = new GBeanData(AxisGeronimoConstants.THREADPOOL_NAME,ThreadPool.GBEAN_INFO);
        threadPoolGBean.setAttribute("keepAliveTime", new Integer(5000));
        threadPoolGBean.setAttribute("poolSize", new Integer(5));
        threadPoolGBean.setAttribute("poolName", "DefaultThreadPool");
        AxisGeronimoUtils.startGBeanOnlyIfNotStarted(AxisGeronimoConstants.THREADPOOL_NAME, threadPoolGBean,
             kernel,Thread.currentThread().getContextClassLoader());
        
        GBeanData transactionalTimerGBean = new GBeanData(AxisGeronimoConstants.TRANSACTIONAL_TIMER_NAME,VMStoreThreadPooledTransactionalTimer.GBEAN_INFO);
        transactionalTimerGBean.setAttribute("repeatCount", new Integer(5));
        transactionalTimerGBean.setReferencePattern("TransactionContextManager", AxisGeronimoConstants.TRANSACTION_CONTEXT_MANAGER_NAME);
        transactionalTimerGBean.setReferencePattern("ThreadPool", AxisGeronimoConstants.THREADPOOL_NAME);
        AxisGeronimoUtils.startGBeanOnlyIfNotStarted(AxisGeronimoConstants.TRANSACTIONAL_TIMER_NAME, transactionalTimerGBean, 
            kernel,Thread.currentThread().getContextClassLoader());
        
        GBeanData nonTransactionalTimerGBean = new GBeanData(AxisGeronimoConstants.NONTRANSACTIONAL_TIMER_NAME,VMStoreThreadPooledNonTransactionalTimer.GBEAN_INFO);
        nonTransactionalTimerGBean.setReferencePattern("ThreadPool", AxisGeronimoConstants.THREADPOOL_NAME);
        AxisGeronimoUtils.startGBeanOnlyIfNotStarted(AxisGeronimoConstants.NONTRANSACTIONAL_TIMER_NAME, 
            nonTransactionalTimerGBean, kernel,Thread.currentThread().getContextClassLoader());
    }

    private void stopTimer(Kernel kernel) throws AxisFault {
        try {
            AxisGeronimoUtils.stopGBean(AxisGeronimoConstants.THREADPOOL_NAME, kernel);
            AxisGeronimoUtils.stopGBean(AxisGeronimoConstants.TRANSACTIONAL_TIMER_NAME, kernel);
            AxisGeronimoUtils.stopGBean(AxisGeronimoConstants.NONTRANSACTIONAL_TIMER_NAME, kernel);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    public void startWebContainer(Kernel kernel) throws Exception {
        Set containerPatterns = Collections.singleton(AxisGeronimoConstants.WEB_CONTAINER_NAME);
        
        Class jettyClass = Class.forName("org.apache.geronimo.jetty.JettyContainerImpl");
        GBeanInfo jettyinfo =  (GBeanInfo)jettyClass.getMethod("getGBeanInfo",null).invoke(null,null);
        GBeanData container = new GBeanData(AxisGeronimoConstants.WEB_CONTAINER_NAME,jettyinfo);
        
        Class jconnectorClass = Class.forName("org.apache.geronimo.jetty.connector.HTTPConnector");
        GBeanInfo connectorinfo =  (GBeanInfo)jconnectorClass.getMethod("getGBeanInfo",null).invoke(null,null);
        GBeanData connector = new GBeanData(AxisGeronimoConstants.WEB_CONNECTOR_NAME,connectorinfo);
        
        connector.setAttribute("port", new Integer(AxisGeronimoUtils.AXIS_SERVICE_PORT));
        connector.setReferencePatterns("JettyContainer", containerPatterns);
        
        AxisGeronimoUtils.startGBeanOnlyIfNotStarted(AxisGeronimoConstants.WEB_CONTAINER_NAME, container, 
            kernel,Thread.currentThread().getContextClassLoader());
        AxisGeronimoUtils.startGBeanOnlyIfNotStarted(AxisGeronimoConstants.WEB_CONNECTOR_NAME, connector,
             kernel,Thread.currentThread().getContextClassLoader());
    }

    private void stopWebContainer(Kernel kernel) throws AxisFault {
        try {
            AxisGeronimoUtils.stopGBean(AxisGeronimoConstants.WEB_CONNECTOR_NAME, kernel);
            AxisGeronimoUtils.stopGBean(AxisGeronimoConstants.WEB_CONTAINER_NAME, kernel);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    public void startEJBContainer(Kernel kernel) throws Exception {
        Class ciClass = Class.forName("org.openejb.ContainerIndex");
        GBeanInfo ciinfo =  (GBeanInfo)ciClass.getMethod("getGBeanInfo",null).invoke(null,null);
        GBeanData containerIndexGBean = new GBeanData(AxisGeronimoConstants.EJB_CONTAINER_NAME,ciinfo);
        

        Set ejbContainerNames = new HashSet();
        ejbContainerNames.add(ObjectName.getInstance(AxisGeronimoConstants.J2EE_DOMAIN_NAME
                + ":j2eeType=StatelessSessionBean,*"));
        ejbContainerNames.add(ObjectName.getInstance(AxisGeronimoConstants.J2EE_DOMAIN_NAME
                + ":j2eeType=StatefulSessionBean,*"));
        ejbContainerNames.add(ObjectName.getInstance(AxisGeronimoConstants.J2EE_DOMAIN_NAME
                + ":j2eeType=EntityBean,*"));
        containerIndexGBean.setReferencePatterns("EJBContainers",
                ejbContainerNames);
        AxisGeronimoUtils.startGBeanOnlyIfNotStarted(AxisGeronimoConstants.EJB_CONTAINER_NAME,
                containerIndexGBean, kernel,Thread.currentThread().getContextClassLoader());
    }

    private void stopEJBContainer(Kernel kernel) throws AxisFault {
        try {
            AxisGeronimoUtils.stopGBean(AxisGeronimoConstants.EJB_CONTAINER_NAME, kernel);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

}
