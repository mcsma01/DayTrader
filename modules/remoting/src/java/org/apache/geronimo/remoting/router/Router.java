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

package org.apache.geronimo.remoting.router;

import java.net.URI;

import org.apache.geronimo.remoting.transport.Msg;
import org.apache.geronimo.remoting.transport.TransportException;

/**
 * @version $Rev$ $Date$
 */
public interface Router {

    /**
     * Sends a request message to the other end.
     * 
     * @param request
     * @return
     */
    Msg sendRequest(URI to, Msg request) throws TransportException;

    /**
     * Sends a datagram message.  No response is expected.   
     * 
     * @param request
     * @return
     */
    void sendDatagram(URI to, Msg request) throws TransportException;

}
