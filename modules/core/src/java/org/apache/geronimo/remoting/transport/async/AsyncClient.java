/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.geronimo.remoting.transport.async;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.geronimo.remoting.MarshalledObject;
import org.apache.geronimo.remoting.transport.BytesMarshalledObject;
import org.apache.geronimo.remoting.transport.Msg;
import org.apache.geronimo.remoting.transport.TransportClient;
import org.apache.geronimo.remoting.transport.TransportException;

/**
 * AsyncClientInvoker uses sockets to remotely connect to the 
 * a remote AsyncServerInvoker.  Requests are sent asynchronously 
 * to allow more concurrent requests to be sent to the server
 * while using fewer sockets.  This is also known as the 'async'
 * protocol.
 * 
 * TODO:
 * If you are running on Java 1.4, this transport
 * transport will take advantage of the NIO 
 * classes to further reduce the resources used on the server.
 *
 * @version $Revision: 1.1 $ $Date: 2003/08/22 02:23:26 $
 */
public class AsyncClient implements TransportClient {

    static final Log log = LogFactory.getLog(AsyncClient.class);

    /**
     * @see org.apache.j2ee.remoting.transport.TransportClient#sendRequest(org.apache.j2ee.remoting.URI, byte[])
     */
    public Msg sendRequest(URI to, Msg request) throws TransportException {
        AbstractServer server = Registry.instance.getServerForClientRequest();
        ChannelPool pool = server.getChannelPool(to);
        return pool.sendRequest(to, request);
    }

    /**
     * @see org.apache.j2ee.remoting.transport.TransportClient#sendDatagram(org.apache.j2ee.remoting.URI, byte[])
     */
    public void sendDatagram(URI to, Msg request) throws TransportException {
        AbstractServer server = Registry.instance.getServerForClientRequest();
        ChannelPool pool = server.getChannelPool(to);
        pool.sendDatagram(to, request);
    }

    /**
     * @see org.apache.geronimo.remoting.transport.TransportClient#createMsg()
     */
    public Msg createMsg() {
        return new AsyncMsg();
    }

    /**
     * @see org.apache.geronimo.remoting.transport.TransportClient#createMarshalledObject()
     */
    public MarshalledObject createMarshalledObject() {
        return new BytesMarshalledObject();
    }

}
