/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.geronimo.system.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import org.apache.geronimo.gbean.AbstractName;
import org.apache.geronimo.kernel.config.Configuration;
import org.apache.geronimo.kernel.config.ConfigurationData;
import org.apache.geronimo.kernel.config.ConfigurationManager;
import org.apache.geronimo.kernel.config.ConfigurationStore;
import org.apache.geronimo.kernel.config.NoSuchConfigException;
import org.apache.geronimo.kernel.config.NoSuchStoreException;
import org.apache.geronimo.kernel.config.NullConfigurationStore;
import org.apache.geronimo.kernel.config.LifecycleResults;
import org.apache.geronimo.kernel.config.LifecycleException;
import org.apache.geronimo.kernel.config.LifecycleMonitor;
import org.apache.geronimo.kernel.repository.Artifact;
import org.apache.geronimo.kernel.repository.FileWriteMonitor;
import org.apache.geronimo.kernel.repository.WritableListableRepository;
import org.apache.geronimo.kernel.repository.ArtifactResolver;
import org.apache.geronimo.kernel.repository.Version;
import org.apache.geronimo.system.serverinfo.BasicServerInfo;
import org.apache.geronimo.system.threads.ThreadPool;

/**
 * Tests the plugin installer GBean
 *
 * @version $Rev$ $Date$
 */
public class PluginInstallerTest extends TestCase {
    private URL fakeRepo;
    private URL testRepo;
    private PluginInstaller installer;

    protected void setUp() throws Exception {
        super.setUp();
        fakeRepo = new URL("http://nowhere.com/");
        String url = getClass().getResource("/geronimo-plugins.xml").toString();
        int pos = url.lastIndexOf("/");
        testRepo = new URL(url.substring(0, pos));
        installer = new PluginInstallerGBean(new MockConfigManager(), new MockRepository(), new MockConfigStore(),
                new BasicServerInfo("."), new ThreadPool() {
            public int getPoolSize() {
                return 0;
            }

            public int getMaximumPoolSize() {
                return 0;
            }

            public int getActiveCount() {
                return 0;
            }

            public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
                return false;
            }

            public void execute(String consumerName, Runnable runnable) {
                new Thread(runnable).start();
            }
        }, null);
    }

    public void testParsing() throws Exception {
        PluginList list = installer.listPlugins(testRepo, null, null);
        assertNotNull(list);
        assertEquals(1, list.getRepositories().length);
        assertEquals(fakeRepo, list.getRepositories()[0]);
        assertTrue(list.getPlugins().length > 0);
        int prereqCount = 0;
        for (int i = 0; i < list.getPlugins().length; i++) {
            PluginMetadata metadata = list.getPlugins()[i];
            prereqCount += metadata.getPrerequisites().length;
            for (int j = 0; j < metadata.getPrerequisites().length; j++) {
                PluginMetadata.Prerequisite prerequisite = metadata.getPrerequisites()[j];
                assertFalse(prerequisite.isPresent());
            }
        }
        assertTrue(prereqCount > 0);
    }

    private static class MockConfigStore extends NullConfigurationStore {

    }

    private static class MockRepository implements WritableListableRepository {
        public void copyToRepository(File source, Artifact destination, FileWriteMonitor monitor) throws IOException {
        }

        public void copyToRepository(InputStream source, int size, Artifact destination, FileWriteMonitor monitor) throws IOException {
        }

        public boolean contains(Artifact artifact) {
            return false;
        }

        public File getLocation(Artifact artifact) {
            return null;
        }

        public LinkedHashSet getDependencies(Artifact artifact) {
            return new LinkedHashSet();
        }

        public SortedSet list() {
            return new TreeSet();
        }

        public SortedSet list(Artifact query) {
            return new TreeSet();
        }
    }

    private static class MockConfigManager implements ConfigurationManager {

        public boolean isInstalled(Artifact configurationId) {
            return false;
        }

        public Artifact[] getInstalled(Artifact query) {
            return new Artifact[0];
        }

        public Artifact[] getLoaded(Artifact query) {
            return new Artifact[0];
        }

        public Artifact[] getRunning(Artifact query) {
            return new Artifact[0];
        }

        public boolean isLoaded(Artifact configID) {
            return false;
        }

        public List listStores() {
            return Collections.EMPTY_LIST;
        }

        public ConfigurationStore[] getStores() {
            return new ConfigurationStore[0];
        }

        public ConfigurationStore getStoreForConfiguration(Artifact configuration) {
            return null;
        }

        public List listConfigurations(AbstractName store) throws NoSuchStoreException {
            return Collections.EMPTY_LIST;
        }

        public boolean isRunning(Artifact configurationId) {
            return false;
        }

        public List listConfigurations() {
            return null;
        }

        public boolean isConfiguration(Artifact artifact) {
            return false;
        }

        public Configuration getConfiguration(Artifact configurationId) {
            return null;
        }

        public LifecycleResults loadConfiguration(Artifact configurationId) throws NoSuchConfigException, LifecycleException {
            return null;
        }

        public LifecycleResults loadConfiguration(ConfigurationData configurationData) throws NoSuchConfigException, LifecycleException {
            return null;
        }

        public LifecycleResults loadConfiguration(Artifact configurationId, LifecycleMonitor monitor) throws NoSuchConfigException, LifecycleException {
            return null;
        }

        public LifecycleResults loadConfiguration(ConfigurationData configurationData, LifecycleMonitor monitor) throws NoSuchConfigException, LifecycleException {
            return null;
        }

        public LifecycleResults unloadConfiguration(Artifact configurationId) throws NoSuchConfigException {
            return null;
        }

        public LifecycleResults unloadConfiguration(Artifact configurationId, LifecycleMonitor monitor) throws NoSuchConfigException {
            return null;
        }

        public LifecycleResults startConfiguration(Artifact configurationId) throws NoSuchConfigException, LifecycleException {
            return null;
        }

        public LifecycleResults startConfiguration(Artifact configurationId, LifecycleMonitor monitor) throws NoSuchConfigException, LifecycleException {
            return null;
        }

        public LifecycleResults stopConfiguration(Artifact configurationId) throws NoSuchConfigException {
            return null;
        }

        public LifecycleResults stopConfiguration(Artifact configurationId, LifecycleMonitor monitor) throws NoSuchConfigException {
            return null;
        }

        public LifecycleResults restartConfiguration(Artifact configurationId) throws NoSuchConfigException, LifecycleException {
            return null;
        }

        public LifecycleResults restartConfiguration(Artifact configurationId, LifecycleMonitor monitor) throws NoSuchConfigException, LifecycleException {
            return null;
        }

        public LifecycleResults reloadConfiguration(Artifact configurationId) throws NoSuchConfigException, LifecycleException {
            return null;
        }

        public LifecycleResults reloadConfiguration(Artifact configurationId, LifecycleMonitor monitor) throws NoSuchConfigException, LifecycleException {
            return null;
        }

        public LifecycleResults reloadConfiguration(Artifact configurationId, Version version) throws NoSuchConfigException, LifecycleException {
            return null;
        }

        public LifecycleResults reloadConfiguration(Artifact configurationId, Version version, LifecycleMonitor monitor) throws NoSuchConfigException, LifecycleException {
            return null;
        }

        public LifecycleResults reloadConfiguration(ConfigurationData configurationData) throws NoSuchConfigException, LifecycleException {
            return null;
        }

        public LifecycleResults reloadConfiguration(ConfigurationData configurationData, LifecycleMonitor monitor) throws NoSuchConfigException, LifecycleException {
            return null;
        }

        public void uninstallConfiguration(Artifact configurationId) throws IOException, NoSuchConfigException {

        }

        public ArtifactResolver getArtifactResolver() {
            return null;
        }

        public boolean isOnline() {
            return true;
        }

        public void setOnline(boolean online) {
        }
    }
}