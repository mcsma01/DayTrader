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

package org.apache.geronimo.network.protocol.control.commands;

import java.util.Collection;

import org.apache.geronimo.network.protocol.control.ControlContext;
import org.apache.geronimo.network.protocol.control.ControlException;


/**
 * @version $Rev$ $Date$
 */
public interface MenuItem {

    public final byte CREATE = (byte) 0x00;
    public final byte ATTRIBUTE = (byte) 0x01;
    public final byte REFERENCE = (byte) 0x02;

    public final byte RESERVED = (byte) 0xff;

    Collection getBuffers();

    Object execute(ControlContext context) throws ControlException;
}
