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

//
// This source code implements specifications defined by the Java
// Community Process. In order to remain compliant with the specification
// DO NOT add / change / or delete method signatures!
//

package javax.enterprise.deploy.spi.exceptions;

/**
 * This exception reports errors in setting up an application client for
 * execution.
 *
 * @version $Rev$ $Date$
 */
public class ClientExecuteException extends Exception {
    /**
     * Creates new <tt>ClientExecuteException</tt> without a detail message.
     */
    public ClientExecuteException() {
        super();
    }

    /**
     * Constructs a <tt>ClientExecuteException</tt> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public ClientExecuteException(String msg) {
        super(msg);
    }
}