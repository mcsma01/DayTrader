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

package org.apache.geronimo.kernel.repository;

/**
 * @version $Rev$ $Date$
 */
public class MissingDependencyException extends Exception {
    public MissingDependencyException() {
    }

    public MissingDependencyException(Throwable cause) {
        super(cause);
    }

    public MissingDependencyException(String message) {
        super(message);
    }

    public MissingDependencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
