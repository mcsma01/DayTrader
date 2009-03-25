/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.geronimo.kernel.util;

// import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility functions related to Input validation.
 *
 * @version $Rev$ $Date$
 */
public class InputUtils {
    private static final Log log = LogFactory.getLog(InputUtils.class);

    private static final Pattern ILLEGAL_CHARS = Pattern.compile("[\\.]{2}|[<>:\\\\/\"\'\\|]");

    public final static void validateSafeInput(String input) {
        // look for illegal chars in input
        if (input != null) {
            Matcher inputMatcher = ILLEGAL_CHARS.matcher(input);
            if (inputMatcher.find()) 
            {
                log.warn("Illegal characters detected in input" + input);
                throw new IllegalArgumentException("input  "+input+" contains illegal characters: .. < > : / \\ \' \" | ");
            }
        }
    }

    public final static void validateSafeInput(ArrayList<String> inputs) {
        for (String input : inputs) {
            validateSafeInput(input);
        }
    }
}
