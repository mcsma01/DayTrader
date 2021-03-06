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
package org.apache.geronimo.kernel.basic;

import java.util.Set;
import javax.management.ObjectName;

import org.apache.geronimo.kernel.lifecycle.LifecycleMonitor;
import org.apache.geronimo.kernel.lifecycle.LifecycleListener;

/**
 * @version $Rev$ $Date$
 */
public class LifecycleMonitorFlyweight implements LifecycleMonitor {
    private final LifecycleMonitor lifecycleMonitor;

    public LifecycleMonitorFlyweight(LifecycleMonitor lifecycleMonitor) {
        this.lifecycleMonitor = lifecycleMonitor;
    }

    public void addLifecycleListener(LifecycleListener listener, ObjectName pattern) {
        lifecycleMonitor.addLifecycleListener(listener, pattern);
    }

    public void addLifecycleListener(LifecycleListener listener, Set patterns) {
        lifecycleMonitor.addLifecycleListener(listener, patterns);
    }

    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycleMonitor.removeLifecycleListener(listener);
    }
}
