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
package org.apache.geronimo.j2ee.deployment;

import java.io.File;
import java.net.URL;
import java.net.URI;
import java.util.jar.JarFile;

import org.apache.geronimo.common.DeploymentException;

/**
 * @version $Rev$ $Date$
 */
public interface ModuleBuilder {
    Module createModule(File plan, JarFile moduleFile) throws DeploymentException;

    Module createModule(Object plan, JarFile moduleFile, String targetPath, URL specDDUrl, URI earConfigId, Object moduleContextInfo) throws DeploymentException;

    void installModule(JarFile earFile, EARContext earContext, Module module) throws DeploymentException;

    void initContext(EARContext earContext, Module module, ClassLoader cl) throws DeploymentException;

    void addGBeans(EARContext earContext, Module module, ClassLoader cl) throws DeploymentException;

    String getSchemaNamespace();
}
