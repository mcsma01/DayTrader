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

package org.apache.geronimo.remoting.transport;

import java.net.URI;

import junit.framework.TestCase;

import org.apache.geronimo.remoting.MarshalledObject;
import org.apache.geronimo.remoting.router.Router;

import EDU.oswego.cs.dl.util.concurrent.CyclicBarrier;
import EDU.oswego.cs.dl.util.concurrent.Semaphore;

/**
 * Unit test for the Async Remoting Transport
 *
 * @version $Rev$ $Date$
 */

public class AsyncTransportStress extends TestCase {

    TransportServer server;
    TransportClient client;
    URI connectURI;
    volatile MockRouter mockRouter;

    class MockRouter implements Router {
        
        Semaphore requestCounter=new Semaphore(0);
        Semaphore datagramCounter=new Semaphore(0);

        public Msg sendRequest(URI to, Msg request) throws TransportException {
            try {
                requestCounter.release();

                Msg response = request.createMsg();
                response.pushMarshaledObject(request.popMarshaledObject());
                return response;

            } catch (Exception e) {
                throw new TransportException(e.getMessage());
            }
        }

        /**
         * @see org.apache.geronimo.remoting.transport.Router#sendDatagram(java.net.URI, org.apache.geronimo.remoting.transport.Msg)
         */
        public void sendDatagram(URI to, Msg request) throws TransportException {
            datagramCounter.release();
        }
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        System.out.println("================================================");
        URI bindURI = new URI("async://0.0.0.0:0");
        TransportFactory tf = TransportFactory.getTransportFactory(bindURI);
        server = tf.createSever();
        mockRouter = new MockRouter();
        server.bind(bindURI,mockRouter);
        connectURI = server.getClientConnectURI();
        server.start();
        client = tf.createClient();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        server.dispose();
    }

    public void testConcurrentRequests() throws Exception {
        
        final int WORKERS = 100;
        final int MESSAGE_COUNT=10;
        final CyclicBarrier barrier = new CyclicBarrier(WORKERS); 

        for( int i=0; i < WORKERS; i++ ) {                
        
            new Thread() {
                /**
                 * @see java.lang.Thread#run()
                 */
                public void run() {
                    try {
                        String text = "Hello World";
                        MarshalledObject object = client.createMarshalledObject();
                        object.set(text);
                        Msg msg = client.createMsg();
                        msg.pushMarshaledObject(object);
                                
                        barrier.barrier();
                        
                        for(int i=0; i < MESSAGE_COUNT; i++ )         
                            client.sendRequest(connectURI,msg);
                        
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        
        for( int j=0; j < WORKERS; j++ ) {                
            for(int i=0; i < MESSAGE_COUNT; i++ ) {
                boolean b = mockRouter.requestCounter.attempt(30000000);         
                if( !b )
                    fail("test timed out");
            }
        }
    }
}
