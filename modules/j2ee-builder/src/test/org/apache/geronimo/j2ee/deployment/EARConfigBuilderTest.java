/**
 *
 * Copyright 2003-2004 The Apache Software Foundation
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

package org.apache.geronimo.j2ee.deployment;

import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.List;
import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.geronimo.common.DeploymentException;
import org.apache.geronimo.deployment.util.DeploymentUtil;
import org.apache.geronimo.kernel.jmx.JMXUtil;
import org.apache.geronimo.kernel.Kernel;
import org.apache.geronimo.kernel.config.ConfigurationStore;
import org.apache.geronimo.kernel.config.InvalidConfigException;
import org.apache.geronimo.kernel.config.NoSuchConfigException;
import org.apache.geronimo.kernel.config.Configuration;
import org.apache.geronimo.kernel.registry.GBeanRegistry;
import org.apache.geronimo.kernel.registry.BasicGBeanRegistry;
import org.apache.geronimo.gbean.GBeanData;
import org.apache.geronimo.gbean.GBeanInfoBuilder;
import org.apache.geronimo.gbean.GBeanInfo;

/**
 * @version $Rev$ $Date$
 */
public class EARConfigBuilderTest extends TestCase {
    private static final File basedir = new File(System.getProperty("basedir", System.getProperty("user.dir")));

    private static JarFile earFile;
    private static MockEJBConfigBuilder ejbConfigBuilder = new MockEJBConfigBuilder();
    private static MockWARConfigBuilder webConfigBuilder = new MockWARConfigBuilder();
    private static MockConnectorConfigBuilder connectorConfigBuilder = new MockConnectorConfigBuilder();
    private static ResourceReferenceBuilder resourceReferenceBuilder = null;
    private static ModuleBuilder appClientConfigBuilder = null;
    private static ServiceReferenceBuilder serviceReferenceBuilder = null;

    private static final String j2eeServerName = "someDomain";
    private static final ObjectName j2eeServer = JMXUtil.getObjectName(j2eeServerName + ":j2eeType=J2EEServer,name=J2EEServerName");
    private static final ObjectName transactionManagerObjectName = JMXUtil.getObjectName(j2eeServerName + ":type=TransactionManager");
    private static final ObjectName connectionTrackerObjectName = JMXUtil.getObjectName(j2eeServerName + ":type=ConnectionTracker");
    private static final ObjectName transactionalTimerObjectName = JMXUtil.getObjectName(j2eeServerName + ":type=ThreadPooledTimer,name=TransactionalThreaPooledTimer");
    private static final ObjectName nonTransactionalTimerObjectName = JMXUtil.getObjectName(j2eeServerName + ":type=ThreadPooledTimer,name=NonTransactionalThreaPooledTimer");
    private URI defaultParentId;

    protected void setUp() throws Exception {
        defaultParentId = new URI("org/apache/geronimo/Server");
    }

    public static Test suite() throws Exception {
        TestSuite inner = new TestSuite(EARConfigBuilderTest.class);
        TestSetup setup14 = new TestSetup(inner) {
            protected void setUp() throws Exception {
                earFile = DeploymentUtil.createJarFile(new File(basedir, "target/test-ear14/test-ear.ear"));
                ejbConfigBuilder.ejbModule = new EJBModule(false, null, null, null, "test-ejb-jar.jar", null, null, null);
                webConfigBuilder.contextRoot = "test";
                webConfigBuilder.webModule = new WebModule(false, null, null, null, "test-war.war", null, null, null);
                webConfigBuilder.webModule.setContextRoot("test");
                connectorConfigBuilder.connectorModule = new ConnectorModule(false, null, null, null, "test-rar.rar", null, null, null);
            }

            protected void tearDown() {
                DeploymentUtil.close(earFile);
                close(ejbConfigBuilder.ejbModule);
                close(webConfigBuilder.webModule);
                close(connectorConfigBuilder.connectorModule);
            }
        };
        TestSetup setupNaked14 = new TestSetup(inner) {
            protected void setUp() throws Exception {
                earFile = DeploymentUtil.createJarFile(new File(basedir, "target/test-ear14/test-naked-ear.ear"));
                ejbConfigBuilder.ejbModule = new EJBModule(false, null, null, null, "test-ejb-jar.jar", null, null, null);
                webConfigBuilder.contextRoot = "test";
                webConfigBuilder.webModule = new WebModule(false, null, null, null, "test-war.war", null, null, null);
                webConfigBuilder.webModule.setContextRoot("test");
                connectorConfigBuilder.connectorModule = new ConnectorModule(false, null, null, null, "test-rar.rar", null, null, null);
            }

            protected void tearDown() {
                DeploymentUtil.close(earFile);
                close(ejbConfigBuilder.ejbModule);
                close(webConfigBuilder.webModule);
                close(connectorConfigBuilder.connectorModule);
            }
        };
        TestSetup setup13 = new TestSetup(inner) {
            protected void setUp() throws Exception {
                earFile = DeploymentUtil.createJarFile(new File(basedir, "target/test-ear13/test-ear.ear"));
                ejbConfigBuilder.ejbModule = new EJBModule(false, null, null, null, "test-ejb-jar.jar", null, null, null);
                webConfigBuilder.contextRoot = "test";
                webConfigBuilder.webModule = new WebModule(false, null, null, null, "test-war.war", null, null, null);
                webConfigBuilder.webModule.setContextRoot("test");
                connectorConfigBuilder.connectorModule = new ConnectorModule(false, null, null, null, "test-rar.rar", null, null, null);
            }

            protected void tearDown() {
                DeploymentUtil.close(earFile);
                close(ejbConfigBuilder.ejbModule);
                close(webConfigBuilder.webModule);
                close(connectorConfigBuilder.connectorModule);
            }
        };
        TestSetup setupNaked13 = new TestSetup(inner) {
            protected void setUp() throws Exception {
                earFile = DeploymentUtil.createJarFile(new File(basedir, "target/test-ear13/test-naked-ear.ear"));
                ejbConfigBuilder.ejbModule = new EJBModule(false, null, null, null, "test-ejb-jar.jar", null, null, null);
                webConfigBuilder.contextRoot = "test";
                webConfigBuilder.webModule = new WebModule(false, null, null, null, "test-war.war", null, null, null);
                webConfigBuilder.webModule.setContextRoot("test");
                connectorConfigBuilder.connectorModule = new ConnectorModule(false, null, null, null, "test-rar.rar", null, null, null);
            }

            protected void tearDown() {
                DeploymentUtil.close(earFile);
                close(ejbConfigBuilder.ejbModule);
                close(webConfigBuilder.webModule);
                close(connectorConfigBuilder.connectorModule);
            }
        };
        TestSetup setupUnpacked = new TestSetup(inner) {
            protected void setUp() throws Exception {
                earFile = DeploymentUtil.createJarFile(new File(basedir, "target/test-unpacked-ear/full/"));
                ejbConfigBuilder.ejbModule = new EJBModule(false, null, null, null, "test-ejb-jar.jar/", null, null, null);
                webConfigBuilder.contextRoot = "test";
                webConfigBuilder.webModule = new WebModule(false, null, null, null, "test-war.war/", null, null, null);
                webConfigBuilder.webModule.setContextRoot("test");
                connectorConfigBuilder.connectorModule = new ConnectorModule(false, null, null, null, "test-rar.rar", null, null, null);
            }

            protected void tearDown() {
                DeploymentUtil.close(earFile);
                close(ejbConfigBuilder.ejbModule);
                close(webConfigBuilder.webModule);
                close(connectorConfigBuilder.connectorModule);
            }
        };
        TestSetup setupUnpackedNaked = new TestSetup(inner) {
            protected void setUp() throws Exception {
                earFile = DeploymentUtil.createJarFile(new File(basedir, "target/test-unpacked-ear/naked/"));
                ejbConfigBuilder.ejbModule = new EJBModule(false, null, null, null, "test-ejb-jar.jar/", null, null, null);
                webConfigBuilder.contextRoot = "test";
                webConfigBuilder.webModule = new WebModule(false, null, null, null, "test-war.war", null, null, null);
                webConfigBuilder.webModule.setContextRoot("test");
                connectorConfigBuilder.connectorModule = new ConnectorModule(false, null, null, null, "test-rar.rar", null, null, null);
            }

            protected void tearDown() {
                DeploymentUtil.close(earFile);
                close(ejbConfigBuilder.ejbModule);
                close(webConfigBuilder.webModule);
                close(connectorConfigBuilder.connectorModule);
            }
        };
        TestSetup setupUnpackedAltDD = new TestSetup(inner) {
            protected void setUp() throws Exception {
                earFile = DeploymentUtil.createJarFile(new File(basedir, "target/test-unpacked-ear/alt-dd/"));
                ejbConfigBuilder.ejbModule = new EJBModule(false, null, null, null, "test-ejb-jar.jar/", null, null, null);
                webConfigBuilder.contextRoot = "test";
                webConfigBuilder.webModule = new WebModule(false, null, null, null, "test-war.war/", null, null, null);
                webConfigBuilder.webModule.setContextRoot("test");
                connectorConfigBuilder.connectorModule = new ConnectorModule(false, null, null, null, "test-rar.rar", null, null, null);
            }

            protected void tearDown() {
                DeploymentUtil.close(earFile);
                close(ejbConfigBuilder.ejbModule);
                close(webConfigBuilder.webModule);
                close(connectorConfigBuilder.connectorModule);
            }
        };
        TestSetup setupPackedAltDD = new TestSetup(inner) {
            protected void setUp() throws Exception {
                earFile = DeploymentUtil.createJarFile(new File(basedir, "target/test-unpacked-ear/alt-dd.ear"));
                ejbConfigBuilder.ejbModule = new EJBModule(false, null, null, null, "test-ejb-jar.jar/", null, null, null);
                webConfigBuilder.contextRoot = "test";
                webConfigBuilder.webModule = new WebModule(false, null, null, null, "test-war.war/", null, null, null);
                webConfigBuilder.webModule.setContextRoot("test");
                connectorConfigBuilder.connectorModule = new ConnectorModule(false, null, null, null, "test-rar.rar", null, null, null);
            }

            protected void tearDown() {
                DeploymentUtil.close(earFile);
                close(ejbConfigBuilder.ejbModule);
                close(webConfigBuilder.webModule);
                close(connectorConfigBuilder.connectorModule);
            }
        };

        TestSuite suite = new TestSuite();
        suite.addTest(setup14);
        suite.addTest(setupNaked14);
        suite.addTest(setup13);
        suite.addTest(setupNaked13);
        suite.addTest(setupUnpacked);
        suite.addTest(setupUnpackedNaked);
        suite.addTest(setupUnpackedAltDD);
        suite.addTest(setupPackedAltDD);
        return suite;
    }

    public void testBuildConfiguration() throws Exception {
        Kernel kernel = new Kernel("foo", new BasicGBeanRegistry());
        kernel.boot();

        GBeanData store = new GBeanData(JMXUtil.getObjectName("foo:j2eeType=ConfigurationStore,name=mock"), MockConfigStore.GBEAN_INFO);
        kernel.loadGBean(store, this.getClass().getClassLoader());
        kernel.startGBean(store.getName());

        EARConfigBuilder configBuilder = new EARConfigBuilder(defaultParentId, transactionManagerObjectName, connectionTrackerObjectName, transactionalTimerObjectName, nonTransactionalTimerObjectName, null, ejbConfigBuilder, null, webConfigBuilder, connectorConfigBuilder, resourceReferenceBuilder, appClientConfigBuilder, serviceReferenceBuilder, kernel);


        File tempDir = null;
        try {
            tempDir = DeploymentUtil.createTempDir();
            Object plan = configBuilder.getDeploymentPlan(null, earFile);
            configBuilder.buildConfiguration(plan, earFile, tempDir);
        } finally {
            DeploymentUtil.recursiveDelete(tempDir);
            kernel.shutdown();
        }
    }

    public void testNoEJBDeployer() throws Exception {
        EARConfigBuilder configBuilder = new EARConfigBuilder(defaultParentId, transactionManagerObjectName, connectionTrackerObjectName, transactionalTimerObjectName, nonTransactionalTimerObjectName, null, null, null, webConfigBuilder, connectorConfigBuilder, resourceReferenceBuilder, appClientConfigBuilder, serviceReferenceBuilder, null);

        File tempDir = null;
        try {
            tempDir = DeploymentUtil.createTempDir();
            Object plan = configBuilder.getDeploymentPlan(null, earFile);
            configBuilder.buildConfiguration(plan, earFile, tempDir);
            fail("Should have thrown a DeploymentException");
        } catch (DeploymentException e) {
            // expected
        } finally {
            DeploymentUtil.recursiveDelete(tempDir);
        }
    }

    public void testNoWARDeployer() throws Exception {
        EARConfigBuilder configBuilder = new EARConfigBuilder(defaultParentId, transactionManagerObjectName, connectionTrackerObjectName, transactionalTimerObjectName, nonTransactionalTimerObjectName, null, ejbConfigBuilder, null, null, connectorConfigBuilder, resourceReferenceBuilder, appClientConfigBuilder, serviceReferenceBuilder, null);

        File tempDir = null;
        try {
            tempDir = DeploymentUtil.createTempDir();
            Object plan = configBuilder.getDeploymentPlan(null, earFile);
            configBuilder.buildConfiguration(plan, earFile, tempDir);
            fail("Should have thrown a DeploymentException");
        } catch (DeploymentException e) {
            // expected
        } finally {
            DeploymentUtil.recursiveDelete(tempDir);
        }
    }

    public void testNoConnectorDeployer() throws Exception {
        EARConfigBuilder configBuilder = new EARConfigBuilder(defaultParentId, transactionManagerObjectName, connectionTrackerObjectName, transactionalTimerObjectName, nonTransactionalTimerObjectName, null, ejbConfigBuilder, null, webConfigBuilder, null, resourceReferenceBuilder, appClientConfigBuilder, serviceReferenceBuilder, null);

        File tempDir = null;
        try {
            tempDir = DeploymentUtil.createTempDir();
            Object plan = configBuilder.getDeploymentPlan(null, earFile);
            configBuilder.buildConfiguration(plan, earFile, tempDir);
            fail("Should have thrown a DeploymentException");
        } catch (DeploymentException e) {
            // expected
        } finally {
            DeploymentUtil.recursiveDelete(tempDir);
        }
    }

    private static void close(Module module) {
        if (module != null) {
            module.close();
        }
    }

    public static class MockConfigStore implements ConfigurationStore {
        public URI install(URL source) throws IOException, InvalidConfigException {
            return null;
        }

        public URI install(File source) throws IOException, InvalidConfigException {
            return null;
        }

        public void uninstall(URI configID) throws NoSuchConfigException, IOException {

        }

        public boolean containsConfiguration(URI configID) {
            return true;
        }

        public GBeanData getConfiguration(URI id) throws NoSuchConfigException, IOException, InvalidConfigException {
            GBeanData configData = null;
            try {
                configData = new GBeanData(Configuration.getConfigurationObjectName(id), Configuration.GBEAN_INFO);
            } catch (MalformedObjectNameException e) {
                throw new InvalidConfigException(e);
            }
            configData.setAttribute("ID", id);
            configData.setAttribute("domain", "test");
            configData.setAttribute("server", "bar");
            configData.setAttribute("gBeanState", NO_OBJECTS_OS);
            return configData;
        }

        public void updateConfiguration(Configuration configuration) throws NoSuchConfigException, Exception {

        }

        public URL getBaseURL(URI id) throws NoSuchConfigException {
            return null;
        }

        public String getObjectName() {
            return null;
        }

        public List listConfiguations() {
            return null;
        }

        public File createNewConfigurationDir() {
            return null;
        }

        public final static GBeanInfo GBEAN_INFO;

        private static final byte[] NO_OBJECTS_OS;

        static {
            GBeanInfoBuilder infoBuilder = new GBeanInfoBuilder(MockConfigStore.class);
            infoBuilder.addInterface(ConfigurationStore.class);
            GBEAN_INFO = infoBuilder.getBeanInfo();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.flush();
                NO_OBJECTS_OS = baos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };


}
