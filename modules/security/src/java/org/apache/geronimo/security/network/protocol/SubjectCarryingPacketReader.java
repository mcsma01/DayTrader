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

package org.apache.geronimo.security.network.protocol;

import org.apache.geronimo.network.protocol.util.ByteKeyUpPacketReader;


/**
 * @version $Rev$ $Date$
 */
class SubjectCarryingPacketReader extends ByteKeyUpPacketReader implements SubjectCarryingPackets {

    private static SubjectCarryingPacketReader ourInstance = new SubjectCarryingPacketReader();

    public static SubjectCarryingPacketReader getInstance() {
        return ourInstance;
    }

    private SubjectCarryingPacketReader() {
        register(PASSTHROUGH, new PassthroughUpPacket());
        register(SUBJECT, new SubjectCarryingUpPacket());
    }
}