/**
 *
 * Copyright 2003-2004 The Apache Software Foundation
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

package org.apache.geronimo.deployment.plugin;

import javax.enterprise.deploy.shared.CommandType;

import org.apache.geronimo.deployment.plugin.local.CommandSupport;

/**
 *
 *
 * @version $Rev$ $Date$
 */
public class FailedProgressObject extends CommandSupport {
    public FailedProgressObject(CommandType command, String message) {
        super(command);
        fail(message);
    }

    public void run() {
    }
}
