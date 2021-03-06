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
package org.apache.geronimo.jetty;

import org.apache.geronimo.management.geronimo.WebConnector;

/**
 * A Jetty-specific extension to the standard Geronimo web connector interface.
 *
 * @version $Rev$ $Date$
 */
public interface JettyWebConnector extends WebConnector {

    /**
     * Gets the minimum number of threads used to service connections from
     * this connector.
     */
    public int getMinThreads();

    /**
     * Sets the minimum number of threads used to service connections from
     * this connector.
     */
    public void setMinThreads(int threads);

    public int getThreads();

    public int getIdlethreads();

    public String getDefaultScheme();

    public void setMaxIdleTimeMs(int max);

    public int getMaxIdleTimeMs();

    public void setLowThreadsMaxIdleTimeMs(int max);

    public int getLowThreadsMaxIdleTimeMs();

    public void setLowThreads(int lowThreads);

    public int getLowThreads();
}
