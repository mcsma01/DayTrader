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
package org.apache.geronimo.system.configuration;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.geronimo.kernel.InvalidGBeanException;
import org.apache.geronimo.kernel.repository.Artifact;
import org.apache.geronimo.system.configuration.condition.JexlExpressionParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

/**
 * @version $Rev$ $Date$
 */
class ServerOverride {
    private final Map<Artifact, ConfigurationOverride> configurations = new LinkedHashMap<Artifact, ConfigurationOverride>();

    public ServerOverride() {
    }

    public ServerOverride(Element element, JexlExpressionParser expressionParser) throws InvalidGBeanException {
        NodeList configs = element.getElementsByTagName("module");
        for (int i = 0; i < configs.getLength(); i++) {
            Element configurationElement = (Element) configs.item(i);
            ConfigurationOverride configuration = new ConfigurationOverride(configurationElement, expressionParser);
            addConfiguration(configuration);
        }

        // The config.xml file in 1.0 use configuration instead of module
        configs = element.getElementsByTagName("configuration");
        for (int i = 0; i < configs.getLength(); i++) {
            Element configurationElement = (Element) configs.item(i);
            ConfigurationOverride configuration = new ConfigurationOverride(configurationElement, expressionParser);
            addConfiguration(configuration);
        }
    }

    public ConfigurationOverride getConfiguration(Artifact configurationName) {
        return getConfiguration(configurationName, false);
    }

    public ConfigurationOverride getConfiguration(Artifact configurationName, boolean create) {
        ConfigurationOverride configuration = (ConfigurationOverride) configurations.get(configurationName);
        if (create && configuration == null) {
            configuration = new ConfigurationOverride(configurationName, true);
            configurations.put(configurationName, configuration);
        }
        return configuration;
    }

    public void addConfiguration(ConfigurationOverride configuration) {
        configurations.put(configuration.getName(), configuration);
    }

    public void removeConfiguration(Artifact configurationName) {
        configurations.remove(configurationName);
    }

    public Map<Artifact, ConfigurationOverride> getConfigurations() {
        return configurations;
    }

    public Artifact[] queryConfigurations(Artifact query) {
        List list = new ArrayList();
        for (Iterator it = configurations.keySet().iterator(); it.hasNext();) {
            Artifact test = (Artifact) it.next();
            if(query.matches(test)) {
                list.add(test);
            }
        }
        return (Artifact[]) list.toArray(new Artifact[list.size()]);
    }

    public Element writeXml(Document doc) {
        Element root = doc.createElement("attributes");
        root.setAttribute("xmlns", "http://geronimo.apache.org/xml/ns/attributes-1.1");
        doc.appendChild(doc.createComment(" ======================================================== "));
        doc.appendChild(doc.createComment(" Warning - This XML file is re-generated by Geronimo when "));
        doc.appendChild(doc.createComment(" changes are made to Geronimo's configuration, therefore  "));
        doc.appendChild(doc.createComment(" any comments added to this file will be lost.            "));
        doc.appendChild(doc.createComment(" Do not edit this file while Geronimo is running.         "));
        doc.appendChild(doc.createComment(" ======================================================== "));
        doc.appendChild(root);
        for (Iterator it = configurations.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            ConfigurationOverride configurationOverride = (ConfigurationOverride) entry.getValue();
            configurationOverride.writeXml(doc, root);
        }
        return root;
    }
}
