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
package org.apache.geronimo.axis.builder.mock;

import java.net.URL;
import java.rmi.Remote;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.HandlerChain;

import org.apache.geronimo.axis.client.OperationInfo;
import org.apache.geronimo.axis.client.SEIFactory;

/**
 * @version $Rev$ $Date$
 */
public class MockSEIFactory implements SEIFactory {
    public Remote createServiceEndpoint() {
        return new MockPortImpl();
    }

    public HandlerChain createHandlerChain() {
        return null;
    }

    public OperationInfo[] getOperationInfos() {
        return null;
    }

    public QName getPortQName() {
        return null;
    }

    public QName getServiceName() {
        return null;
    }

    public URL getWSDLDocumentLocation() {
        return null;
    }
}
