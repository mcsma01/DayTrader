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
 *        Apache Software Foundation (http:www.apache.org/)."
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
 * <http:www.apache.org/>.
 *
 * ====================================================================
 */
package org.apache.geronimo.security.providers;

import org.apache.geronimo.security.AbstractSecurityRealm;
import org.apache.geronimo.security.GeronimoSecurityException;
import org.apache.geronimo.kernel.management.State;
import org.apache.regexp.RE;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 *
 * @version $Revision: 1.1 $ $Date: 2003/11/18 05:17:18 $
 */

public class SQLSecurityRealm extends AbstractSecurityRealm {
    private String connectionURL;
    private String user = "";
    private String password = "";
    private String userSelect = "SELECT UserName, Password FROM Users";
    private String groupSelect = "SELECT GroupName, UserName FROM Groups";
    HashMap users = new HashMap();
    HashMap groups = new HashMap();

    final static String REALM = "org.apache.geronimo.security.providers.SQLSecurityRealm";

    protected void doStart() throws Exception {
        if (connectionURL == null) throw  new IllegalStateException("Connection URI not set");

        refresh();
    }

    protected void doStop() throws Exception {
        connectionURL = null;

        users.clear();
        groups.clear();
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        if (getStateInstance() != State.STOPPED)
            throw new IllegalStateException("Cannot change the Connection URI after the realm is started");

        this.connectionURL = connectionURL;
    }

    public String getUser() {
        return user;
    }

    public void setPassword(String password) {
        if (getStateInstance() != State.STOPPED)
            throw new IllegalStateException("Cannot change the connection password after the realm is started");

        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setUser(String user) {
        if (getStateInstance() != State.STOPPED)
            throw new IllegalStateException("Cannot change the connection user after the realm is started");

        this.user = user;
    }

    public String getUserSelect() {
        return userSelect;
    }

    public void setUserSelect(String userSelect) {
        if (getStateInstance() != State.STOPPED)
            throw new IllegalStateException("Cannot change the user SQL select statement after the realm is started");

        this.userSelect = userSelect;
    }

    public String getGroupSelect() {
        return groupSelect;
    }

    public void setGroupSelect(String groupSelect) {
        if (getStateInstance() != State.STOPPED)
            throw new IllegalStateException("Cannot change the group SQL select statement after the realm is started");

        this.groupSelect = groupSelect;
    }


    public Set getGroupPrincipals() throws GeronimoSecurityException {
        if (getStateInstance() != State.RUNNING)
            throw new IllegalStateException("Cannot obtain Groups until the realm is started");

        return Collections.unmodifiableSet(groups.keySet());
    }

    public Set getGroupPrincipals(RE regexExpression) throws GeronimoSecurityException {
        if (getStateInstance() != State.RUNNING)
            throw new IllegalStateException("Cannot obtain Groups until the realm is started");

        HashSet result = new HashSet();
        Iterator iter = groups.keySet().iterator();
        String group;
        while (iter.hasNext()) {
            group = (String) iter.next();

            if (regexExpression.match(group)) {
                result.add(group);
            }
        }

        return result;
    }

    public Set getUserPrincipals() throws GeronimoSecurityException {
        if (getStateInstance() != State.RUNNING)
            throw new IllegalStateException("Cannot obtain Users until the realm is started");

        return Collections.unmodifiableSet(users.keySet());
    }

    public Set getUserPrincipals(RE regexExpression) throws GeronimoSecurityException {
        if (getStateInstance() != State.RUNNING)
            throw new IllegalStateException("Cannot obtain Users until the realm is started");

        HashSet result = new HashSet();
        Iterator iter = users.keySet().iterator();
        String user;
        while (iter.hasNext()) {
            user = (String) iter.next();

            if (regexExpression.match(user)) {
                result.add(user);
            }
        }

        return result;
    }

    public void refresh() throws GeronimoSecurityException {
        try {
            Connection conn = DriverManager.getConnection(connectionURL, user, password);

            PreparedStatement statement = conn.prepareStatement(userSelect);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String userName = result.getString(1);
                String userPassword = result.getString(2);

                users.put(userName, userPassword);
            }

            statement = conn.prepareStatement(groupSelect);
            result = statement.executeQuery();

            while (result.next()) {
                String groupName = result.getString(1);
                String userName = result.getString(2);

                Set userset = (Set) groups.get(groupName);
                if (userset == null) {
                    userset = new HashSet();
                    groups.put(groupName, userset);
                }
                userset.add(userName);
            }

            conn.close();
        } catch (SQLException sqle) {
            throw new GeronimoSecurityException(sqle);
        }
    }

    String obfuscate(String password) {
        return password;
    }

    public AppConfigurationEntry[] getAppConfigurationEntry() {
        HashMap options = new HashMap();

        options.put(REALM, this);
        AppConfigurationEntry entry = new AppConfigurationEntry("org.apache.geronimo.security.providers.SQLLoginModule",
                                                                AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT,
                                                                options);
        AppConfigurationEntry[] configuration = {entry};

        return configuration;
    }
}
