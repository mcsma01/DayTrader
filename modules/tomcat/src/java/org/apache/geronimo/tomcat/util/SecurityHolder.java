/**
 *
 * Copyright 2003-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package org.apache.geronimo.tomcat.util;

import java.security.PermissionCollection;
import org.apache.geronimo.security.deploy.DefaultPrincipal;

public class SecurityHolder
{

    private String policyContextID;
    private DefaultPrincipal defaultPrincipal;
    private PermissionCollection checked;
    private PermissionCollection excluded;

    public SecurityHolder()
    {
        policyContextID = null;
        defaultPrincipal = null;
        checked = null;
        excluded = null;
    }

    public PermissionCollection getChecked()
    {
        return checked;
    }

    public void setChecked(PermissionCollection checked)
    {
        this.checked = checked;
    }

    public DefaultPrincipal getDefaultPrincipal()
    {
        return defaultPrincipal;
    }

    public void setDefaultPrincipal(DefaultPrincipal defaultPrincipal)
    {
        this.defaultPrincipal = defaultPrincipal;
    }

    public PermissionCollection getExcluded()
    {
        return excluded;
    }

    public void setExcluded(PermissionCollection excluded)
    {
        this.excluded = excluded;
    }

    public String getPolicyContextID()
    {
        return policyContextID;
    }

    public void setPolicyContextID(String policyContextID)
    {
        this.policyContextID = policyContextID;
    }
}
