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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Provides access to things like JARs via a standard API.  Generally
 * these may be local (file: URLs) or remote (http: URLs).  This is
 * a fairly limited read-only type repository.  There are additional
 * interfaces that a Repository may implement to indicate additional
 * capabilities.
 *
 * @version $Rev$ $Date$
 */
public interface Repository {
    boolean hasURI(URI uri);

    URL getURL(URI uri) throws MalformedURLException;
}
