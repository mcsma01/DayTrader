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

package org.apache.geronimo.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;


/**
 * @version $Rev$ $Date$
 */
public class ResolvingObjectOutputStream extends ObjectOutputStream {

    private TransportContext transportContext;

    public ResolvingObjectOutputStream(OutputStream out) throws IOException {
        this(out, null);
    }

    public ResolvingObjectOutputStream(OutputStream out, TransportContext transportContext) throws IOException {
        super(out);
        this.transportContext = transportContext;
        this.enableReplaceObject(transportContext != null);
    }

    /**
     * @see java.io.ObjectOutputStream#replaceObject(java.lang.Object)
     */
    protected Object replaceObject(Object obj) throws IOException {
        return transportContext.writeReplace(obj);
    }
}
