/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.geronimo.common;

import java.net.URL;
import java.net.URLConnection;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class GeronimoEnvironmentTest extends TestCase {
    
    public void testCaching() throws Exception {
        GeronimoEnvironment.init();
        URL url = new URL("jar:file:testFile.jar!/test");
        URLConnection conn = url.openConnection();
        assertFalse(conn.getUseCaches());
        assertFalse(conn.getDefaultUseCaches());
    }
}
