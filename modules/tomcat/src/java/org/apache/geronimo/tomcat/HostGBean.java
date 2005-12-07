/**
 *
 * Copyright 2003-2005 The Apache Software Foundation
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
package org.apache.geronimo.tomcat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.apache.catalina.Cluster;
import org.apache.catalina.Host;
import org.apache.catalina.Manager;
import org.apache.catalina.Realm;
import org.apache.catalina.Valve;
import org.apache.catalina.core.StandardHost;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.geronimo.gbean.GBeanInfo;
import org.apache.geronimo.gbean.GBeanInfoBuilder;
import org.apache.geronimo.gbean.GBeanLifecycle;
import org.apache.geronimo.j2ee.j2eeobjectnames.NameFactory;
import org.apache.geronimo.tomcat.cluster.CatalinaClusterGBean;

/**
 * @version $Rev$ $Date$
 */
public class HostGBean extends BaseGBean implements GBeanLifecycle, ObjectRetriever {

    private static final Log log = LogFactory.getLog(HostGBean.class);
    
    public static final String J2EE_TYPE = "Host";
    private static final String WORKDIR = "workDir";
    private static final String NAME = "name";
    
    private final Host host;

    public HostGBean(String className, 
            Map initParams, 
            ArrayList aliases,
            ObjectRetriever realmGBean,            
            ValveGBean tomcatValveChain,
            CatalinaClusterGBean clusterGBean,
            ManagerGBean manager) throws Exception {
        super(); // TODO: make it an attribute
        
        //Validate
        if (className == null){
            className = "org.apache.catalina.core.StandardHost";
        }
        
        if (initParams == null){
            throw new IllegalArgumentException("Must have a 'name' value in initParams.");
        }
        
        //Be sure the name has been declared.
        if (!initParams.containsKey(NAME)){
            throw new IllegalArgumentException("Must have a 'name' value initParams.");
        }
        
        //Be sure we have a default working directory
        if (!initParams.containsKey(WORKDIR)){
            initParams.put(WORKDIR, "work");
        }

        //Create the Host object
        host = (Host)Class.forName(className).newInstance();
        
        //Set the parameters
        setParameters(host, initParams);
        
        //Add aliases, if any
        if (aliases != null){
            for (Iterator iter = aliases.iterator(); iter.hasNext();) {
                String alias = (String) iter.next();
                host.addAlias(alias);
            }
        }
        
        if (realmGBean != null)
            host.setRealm((Realm)realmGBean.getInternalObject());

        //Add the valve list
        if (host instanceof StandardHost){
            if (tomcatValveChain != null){
                ValveGBean valveGBean = tomcatValveChain;
                while(valveGBean != null){
                    ((StandardHost)host).addValve((Valve)valveGBean.getInternalObject());
                    valveGBean = valveGBean.getNextValve();
                }
            }
        }

        //Add clustering
        if (clusterGBean != null){
            host.setCluster((Cluster)clusterGBean.getInternalObject());
        }
        
        //Add manager
        if (manager != null)
            host.setManager((Manager)manager.getInternalObject());
    }

    public Object getInternalObject() {
        return host;
    }

    public void doFail() {
        log.warn("Failed");
    }

    public void doStart() throws Exception {
        log.debug("Started host name '" + host.getName() + "'");
    }

    public void doStop() throws Exception {
        log.debug("Stopped host '" + host.getName() + "'");
    }

    public static final GBeanInfo GBEAN_INFO;

    static {
        GBeanInfoBuilder infoFactory = GBeanInfoBuilder.createStatic("TomcatHost", HostGBean.class, J2EE_TYPE);
        infoFactory.addAttribute("className", String.class, true);
        infoFactory.addAttribute("initParams", Map.class, true);
        infoFactory.addAttribute("aliases", ArrayList.class, true);
        infoFactory.addReference("RealmGBean", ObjectRetriever.class, NameFactory.GERONIMO_SERVICE);
        infoFactory.addReference("TomcatValveChain", ValveGBean.class, ValveGBean.J2EE_TYPE);
        infoFactory.addReference("CatalinaCluster", CatalinaClusterGBean.class, CatalinaClusterGBean.J2EE_TYPE);
        infoFactory.addReference("Manager", ManagerGBean.class, ManagerGBean.J2EE_TYPE);
        infoFactory.addOperation("getInternalObject");
        infoFactory.setConstructor(new String[] { 
                "className", 
                "initParams", 
                "aliases", 
                "RealmGBean", 
                "TomcatValveChain",
                "CatalinaCluster",
                "Manager"});
        GBEAN_INFO = infoFactory.getBeanInfo();
    }

    public static GBeanInfo getGBeanInfo() {
        return GBEAN_INFO;
    }
}
