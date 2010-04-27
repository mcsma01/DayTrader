/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.geronimo.shell.deploy;

import java.io.PrintWriter;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.geronimo.cli.deployer.BaseCommandArgs;
import org.apache.geronimo.deployment.cli.AbstractCommand;
import org.apache.geronimo.deployment.cli.CommandEncrypt;
import org.apache.geronimo.deployment.cli.ConsoleReader;
import org.apache.geronimo.deployment.cli.ServerConnection;
import org.apache.geronimo.deployment.cli.StreamConsoleReader;

@Command(scope = "deploy", name = "encrypt", description = "Encrypt strings")
public class EncryptCommand extends ConnectCommand{
    
    @Argument(required = true, description = "Encrypted Message")
    String message;
    
    @Override
    protected Object doExecute() throws Exception {
        ServerConnection connection = connect();

        AbstractCommand command = new CommandEncrypt();

        ConsoleReader consoleReader = new StreamConsoleReader(session.getKeyboard(),new PrintWriter(session.getConsole(),true));

        BaseCommandArgs args = new BaseCommandArgs(message.split(" "));

        command.execute(consoleReader, connection, args);

        return null;
    }

}