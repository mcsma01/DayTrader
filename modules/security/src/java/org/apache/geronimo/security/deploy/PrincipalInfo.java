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
package org.apache.geronimo.security.deploy;

import java.beans.PropertyEditorManager;
import java.io.Serializable;

import org.apache.geronimo.common.propertyeditor.PropertyEditorException;
import org.apache.geronimo.common.propertyeditor.TextPropertyEditorSupport;


/**
 * @version $Rev$ $Date$
 */
public class PrincipalInfo implements Serializable {

    static {
        PropertyEditorManager.registerEditor(PrincipalInfo.class, PrincipalEditor.class);
    }

    private final String className;
    private final String principalName;
    private final boolean designatedRunAs;

    public PrincipalInfo(String className, String principalName, boolean designatedRunAs) {
        this.className = className;
        this.principalName = principalName;
        this.designatedRunAs = designatedRunAs;
    }

    public String getClassName() {
        return className;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public boolean isDesignatedRunAs() {
        return designatedRunAs;
    }

    public static class PrincipalEditor extends TextPropertyEditorSupport {

        public void setAsText(String text) {
            if (text != null) {
                String[] parts = text.split(",");
                if (parts.length != 3) {
                    throw new PropertyEditorException("Principal should have the form 'name,class,run-as'");
                }
                PrincipalInfo principalInfo = new PrincipalInfo(parts[0], parts[1], Boolean.valueOf(parts[2]).booleanValue());
                setValue(principalInfo);
            } else {
                setValue(null);
            }
        }

        public String getAsText() {
            PrincipalInfo principalInfo = (PrincipalInfo) getValue();
            if (principalInfo == null) {
                return null;
            }
            return principalInfo.getPrincipalName() + "," + principalInfo.getClassName() + "," + principalInfo.isDesignatedRunAs();
        }
    }
}
