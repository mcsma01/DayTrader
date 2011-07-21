/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.geronimo.openwebbeans.deployment;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.ejb.EJB;
import javax.enterprise.inject.Produces;

import org.apache.geronimo.common.DeploymentException;
import org.apache.geronimo.deployment.ModuleIDBuilder;
import org.apache.geronimo.deployment.service.EnvironmentBuilder;
import org.apache.geronimo.gbean.AbstractName;
import org.apache.geronimo.gbean.GBeanData;
import org.apache.geronimo.gbean.annotation.GBean;
import org.apache.geronimo.gbean.annotation.ParamAttribute;
import org.apache.geronimo.gbean.annotation.ParamReference;
import org.apache.geronimo.j2ee.annotation.Holder;
import org.apache.geronimo.j2ee.deployment.EARContext;
import org.apache.geronimo.j2ee.deployment.Module;
import org.apache.geronimo.j2ee.deployment.ModuleBuilderExtension;
import org.apache.geronimo.j2ee.deployment.NamingBuilder;
import org.apache.geronimo.j2ee.deployment.WebModule;
import org.apache.geronimo.j2ee.j2eeobjectnames.NameFactory;
import org.apache.geronimo.kernel.Naming;
import org.apache.geronimo.kernel.config.ConfigurationStore;
import org.apache.geronimo.kernel.repository.Environment;
import org.apache.geronimo.openejb.cdi.WebBeansConfigurationListener;
import org.apache.geronimo.web.info.WebAppInfo;
import org.apache.openejb.jee.WebApp;
import org.apache.xbean.finder.AbstractFinder;
import org.apache.xbean.finder.BundleAnnotationFinder;
import org.apache.xbean.finder.ClassFinder;
import org.apache.xbean.osgi.bundle.util.BundleResourceFinder;
import org.apache.xbean.osgi.bundle.util.BundleResourceFinder.ResourceFinderCallback;
import org.apache.xbean.osgi.bundle.util.DiscoveryRange;
import org.apache.xbean.osgi.bundle.util.ResourceDiscoveryFilter;
import org.apache.xmlbeans.XmlObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Rev $Date
 */
@GBean(j2eeType = NameFactory.MODULE_BUILDER)
public class OpenWebBeansModuleBuilderExtension implements ModuleBuilderExtension {

    private static final Logger log = LoggerFactory.getLogger(OpenWebBeansModuleBuilderExtension.class);

    private final Environment defaultEnvironment;
    private final Environment jsfPluginEnvironment;
    //only plausible naming builder is ours that adds BeanManager entry
    private final NamingBuilder namingBuilders;

    //this is the geronimo copy
    private static final String CONTEXT_LISTENER_NAME = WebBeansConfigurationListener.class.getName();

    public OpenWebBeansModuleBuilderExtension(
            @ParamAttribute(name = "defaultEnvironment") Environment defaultEnvironment,
            @ParamAttribute(name = "jsfPluginEnvironment") Environment jsfPluginEnvironment,
            @ParamReference(name = "NamingBuilders", namingType = NameFactory.MODULE_BUILDER) NamingBuilder namingBuilders) {
        this.defaultEnvironment = defaultEnvironment;
        this.jsfPluginEnvironment = jsfPluginEnvironment;
        this.namingBuilders = namingBuilders;
    }

    public void createModule(Module module, Bundle bundle, Naming naming, ModuleIDBuilder idBuilder)
            throws DeploymentException {
        if (!(module instanceof WebModule) /*|| !hasBeanXml(module)*/) {
            // not a web module, nothing to do
            return;
        }

        EnvironmentBuilder.mergeEnvironments(module.getEnvironment(), defaultEnvironment);
        if(hasBeanXml(module)){
            EnvironmentBuilder.mergeEnvironments(module.getEnvironment(), jsfPluginEnvironment);
        }
    }

    public void createModule(Module module, Object plan, JarFile moduleFile, String targetPath, URL specDDUrl,
            Environment environment, Object moduleContextInfo, AbstractName earName, Naming naming,
            ModuleIDBuilder idBuilder) throws DeploymentException {
        if (!(module instanceof WebModule) /*|| !hasBeanXml(module)*/) {
            // not a web module, nothing to do
            return;
        }       
        
        EnvironmentBuilder.mergeEnvironments(module.getEnvironment(), defaultEnvironment);
        if(hasBeanXml(module)){
            EnvironmentBuilder.mergeEnvironments(module.getEnvironment(), jsfPluginEnvironment);
        }
    }

    public void installModule(JarFile earFile, EARContext earContext, Module module, Collection configurationStores,
            ConfigurationStore targetConfigurationStore, Collection repository) throws DeploymentException {
    }

    public void initContext(EARContext earContext, Module module, Bundle bundle) throws DeploymentException {
    }

    public void addGBeans(EARContext earContext, Module module, Bundle bundle, Collection repository)
            throws DeploymentException {
        if (!(module instanceof WebModule)) {
            // not a web module, nothing to do
            return;
        }

        WebModule webModule = (WebModule) module;

//        if (!hasBeansXml(bundle)) {
//            return;
//        }

        EARContext moduleContext = module.getEarContext();
        Map sharedContext = module.getSharedContext();
        //add the ServletContextListener to the web app context
        GBeanData webAppData = (GBeanData) sharedContext.get(WebModule.WEB_APP_DATA);
        // add myfaces listener
        WebAppInfo webAppInfo = (WebAppInfo) webAppData.getAttribute("webAppInfo");
        if (webAppInfo != null && !webAppInfo.listeners.contains(CONTEXT_LISTENER_NAME)) {
            webAppInfo.listeners.add(0, CONTEXT_LISTENER_NAME);
        }
        AbstractName moduleName = moduleContext.getModuleName();
        Map<EARContext.Key, Object> buildingContext = new HashMap<EARContext.Key, Object>();
        buildingContext.put(NamingBuilder.GBEAN_NAME_KEY, moduleName);

        //use the same holder object as the web app.
        Holder holder = NamingBuilder.INJECTION_KEY.get(sharedContext);
        buildingContext.put(NamingBuilder.INJECTION_KEY, holder);

        WebApp webApp = webModule.getSpecDD();
        XmlObject geronimoWebApp = webModule.getVendorDD();

        AbstractFinder originalFinder = webModule.getClassFinder();
        ClassFinder classFinder = createOpenWebBeansClassFinder(webApp, webModule);
        webModule.setClassFinder(classFinder);
        namingBuilders.buildNaming(webApp, geronimoWebApp, webModule, buildingContext);
        webModule.setClassFinder(originalFinder);
        

//        AbstractName webBeansGBeanName = moduleContext.getNaming().createChildName(moduleName, "webbeans-lifecycle", "webbeans");
//        GBeanData providerData = new GBeanData(webBeansGBeanName, OpenWebBeansGBean.class);
//        try {
//            moduleContext.addGBean(providerData);
//        } catch (GBeanAlreadyExistsException e) {
//            throw new DeploymentException("Duplicate webbean config gbean in web module", e);
//        }

        //make the web app start second after the webbeans machinery
//        webAppData.addDependency(webBeansGBeanName);
    }

    private boolean hasBeansXml(Bundle bundle) {
        return bundle.getEntry("WEB-INF/beans.xml") != null || bundle.getResource("META-INF/beans.xml") != null;
    }

    private boolean hasBeanXml(Module webModule) {
        return webModule.getDeployable().getResource("WEB-INF/beans.xml") != null || webModule.getDeployable().getResource("META-INF/beans.xml") != null;
    }

    protected ClassFinder createOpenWebBeansClassFinder(WebApp webApp, WebModule webModule)
        throws DeploymentException {
        List<Class<?>> classes = getManagedClasses(webApp, webModule);
        return new ClassFinder(classes);
    }

    private List<Class<?>> getManagedClasses(WebApp webApp, WebModule webModule) throws DeploymentException {

        // Technically a CDI jar can never be meta-data complete
        // But without this check we fail some EE TCK tests

        // It would seem we are scanning too much of the classpath in this method
        // we should only be scanning the jars that contain beans.xml files
        // Obviously, this conflicts with the Bundle concept somewhat

       /* if (webApp.isMetadataComplete()) return Collections.EMPTY_LIST;*/


        Bundle bundle = webModule.getEarContext().getDeploymentBundle();
        ServiceReference reference = bundle.getBundleContext().getServiceReference(PackageAdmin.class.getName());
        try {
            PackageAdmin packageAdmin = (PackageAdmin) bundle.getBundleContext().getService(reference);
            
            //1. Generated the jar file list, which contains META-INF/beans.xml file
            final Set<String> annotationScanRequiredJarFiles = new HashSet<String>();
            String moduleNamePrefix = webModule.isStandAlone() ? "" : webModule.getTargetPath() + "/";
            final String libDirectory = moduleNamePrefix + "WEB-INF/lib";
            BundleResourceFinder resourceFinder = new BundleResourceFinder(packageAdmin, bundle, "META-INF/", "beans.xml", new ResourceDiscoveryFilter() {

                @Override
                public boolean directoryDiscoveryRequired(String directoryName) {
                    return false;
                }

                @Override
                public boolean rangeDiscoveryRequired(DiscoveryRange discoveryRange) {
                    return discoveryRange == DiscoveryRange.BUNDLE_CLASSPATH;
                }

                @Override
                public boolean zipFileDiscoveryRequired(String zipFileName) {
                    return zipFileName.startsWith(libDirectory) && zipFileName.endsWith(".jar");
                }

            });
            resourceFinder.find(new ResourceFinderCallback() {

                @Override
                public boolean foundInDirectory(Bundle arg0, String arg1, URL arg2) throws Exception {
                    return false;
                }

                @Override
                public boolean foundInJar(Bundle bundle, String zipFileName, ZipEntry zipEntry, InputStream in) throws Exception {
                    String zipEntryName = zipEntry.getName();
                    if (zipEntryName.equals("META-INF/beans.xml")) {
                        annotationScanRequiredJarFiles.add(zipFileName);
                    }
                    return true;
                }
            });
            
            final String webInfClassesFolder = moduleNamePrefix + "WEB-INF/classes";
            final boolean webInfClassesScanRequired = bundle.getResource(moduleNamePrefix + "WEB-INF/beans.xml") != null;

            //2. Scan annotations
            Map<Class<? extends Annotation>, Set<Class>> annotationClassSetMap = new HashMap<Class<? extends Annotation>, Set<Class>>();
            BundleAnnotationFinder bundleAnnotationFinder = new BundleAnnotationFinder(packageAdmin, bundle, new ResourceDiscoveryFilter() {

                @Override
                public boolean directoryDiscoveryRequired(String directory) {                    
                    return directory.equals(webInfClassesFolder) && webInfClassesScanRequired;
                }

                @Override
                public boolean rangeDiscoveryRequired(DiscoveryRange discoveryRange) {
                    return discoveryRange.equals(DiscoveryRange.BUNDLE_CLASSPATH);
                }

                @Override
                public boolean zipFileDiscoveryRequired(String jarFile) {
                    return annotationScanRequiredJarFiles.contains(jarFile);
                }
            });

            final List<Member> members = new ArrayList<Member>();

            members.addAll(bundleAnnotationFinder.findAnnotatedMethods(Produces.class));
            members.addAll(bundleAnnotationFinder.findAnnotatedFields(Produces.class));
            members.addAll(bundleAnnotationFinder.findAnnotatedFields(EJB.class));
            members.addAll(bundleAnnotationFinder.findAnnotatedMethods(EJB.class));

            final Set<Class<?>> classes = new HashSet<Class<?>>();
            for (Member member : members) {
                classes.add(member.getDeclaringClass());
            }

            return new ArrayList<Class<?>>(classes);
        } catch (Exception e) {
            throw new DeploymentException("Fail to scan jsr299 annotations", e);
        } finally {
            bundle.getBundleContext().ungetService(reference);
        }
    }
    
}
