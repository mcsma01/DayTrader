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

package org.apache.geronimo.network.protocol.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.geronimo.network.protocol.DownPacket;


/**
 * @version $Rev$ $Date$
 */
public abstract class ShortKeyDownPacket implements DownPacket {

    final short key;

    public ShortKeyDownPacket(short key) {
        this.key = key;
    }

    public final Collection getBuffers() {
        ArrayList buffers = new ArrayList();

        buffers.add(ByteBuffer.allocate(2).putShort(key).flip());

        return buffers;
    }

    protected abstract Collection getChildBuffers();
}
