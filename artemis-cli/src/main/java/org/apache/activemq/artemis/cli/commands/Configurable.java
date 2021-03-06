/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.activemq.artemis.cli.commands;

import javax.inject.Inject;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import io.airlift.airline.Arguments;
import io.airlift.airline.Help;
import io.airlift.airline.Option;
import io.airlift.airline.model.CommandGroupMetadata;
import io.airlift.airline.model.CommandMetadata;
import io.airlift.airline.model.GlobalMetadata;
import org.apache.activemq.artemis.cli.CLIException;
import org.apache.activemq.artemis.core.config.FileDeploymentManager;
import org.apache.activemq.artemis.core.config.impl.FileConfiguration;
import org.apache.activemq.artemis.dto.BrokerDTO;
import org.apache.activemq.artemis.factory.BrokerFactory;
import org.apache.activemq.artemis.integration.bootstrap.ActiveMQBootstrapLogger;
import org.apache.activemq.artemis.jms.server.config.impl.FileJMSConfiguration;

/**
 * Abstract class where we can replace the configuration in various places *
 */
public abstract class Configurable extends ActionAbstract {

   @Arguments(description = "Broker Configuration URI, default 'xml:${ARTEMIS_INSTANCE}/etc/bootstrap.xml'")
   String configuration;

   @Option(name = "--broker", description = "This would override the broker configuration from the bootstrap")
   String brokerConfig;

   @Inject
   public GlobalMetadata global;

   private BrokerDTO brokerDTO = null;

   private FileConfiguration fileConfiguration;

   protected void treatError(Exception e, String group, String command) {
      ActiveMQBootstrapLogger.LOGGER.debug(e.getMessage(), e);
      System.err.println();
      System.err.println("Error:" + e.getMessage());
      System.err.println();
      helpGroup(group, command);
   }

   protected void helpGroup(String groupName, String commandName) {
      for (CommandGroupMetadata group : global.getCommandGroups()) {
         if (group.getName().equals(groupName)) {
            for (CommandMetadata command : group.getCommands()) {
               if (command.getName().equals(commandName)) {
                  Help.help(command);
               }
            }
            break;
         }
      }
   }

   // There should be one lock per VM
   // These will be locked as long as the VM is running
   private static RandomAccessFile serverLockFile = null;
   private static FileLock serverLockLock = null;

   protected static void lockCLI(File lockPlace) throws Exception {
      if (lockPlace != null) {
         lockPlace.mkdirs();
         File fileLock = new File(lockPlace, "cli.lock");
         RandomAccessFile file = new RandomAccessFile(fileLock, "rw");
         serverLockLock = file.getChannel().tryLock();
         if (serverLockLock == null) {
            throw new CLIException("Error: There is another process using the server at " + lockPlace + ". Cannot start the process!");
         }
      }
   }

   protected File getLockPlace() throws Exception {
      String brokerInstance = getBrokerInstance();
      if (brokerInstance != null) {
         return new File(new File(brokerInstance),"lock");
      }
      else {
         return null;
      }
   }

   public static void unlock() {
      try {
         if (serverLockFile != null) {
            serverLockFile.close();
         }

         if (serverLockLock != null) {
            serverLockLock.close();
         }
      }
      catch (Exception ignored) {
      }
   }

   protected FileConfiguration getFileConfiguration() throws Exception {
      if (fileConfiguration == null) {
         if (getBrokerInstance() == null) {
            final String defaultLocation = "./data";
            fileConfiguration = new FileConfiguration();
            // These will be the default places in case the file can't be loaded
            fileConfiguration.setBindingsDirectory(defaultLocation + "/bindings");
            fileConfiguration.setJournalDirectory(defaultLocation + "/journal");
            fileConfiguration.setLargeMessagesDirectory(defaultLocation + "/largemessages");
            fileConfiguration.setPagingDirectory(defaultLocation + "/paging");
         }
         else {
            fileConfiguration = new FileConfiguration();
            FileJMSConfiguration jmsConfiguration = new FileJMSConfiguration();

            String serverConfiguration = getBrokerDTO().server.configuration;
            FileDeploymentManager fileDeploymentManager = new FileDeploymentManager(serverConfiguration);
            fileDeploymentManager.addDeployable(fileConfiguration).addDeployable(jmsConfiguration);
            fileDeploymentManager.readConfiguration();
         }
      }

      fileConfiguration.setBrokerInstance(new File(getBrokerInstance()));

      return fileConfiguration;
   }

   protected BrokerDTO getBrokerDTO() throws Exception {
      if (brokerDTO == null) {
         getConfiguration();

         brokerDTO = BrokerFactory.createBrokerConfiguration(configuration);

         if (brokerConfig != null) {
            if (!brokerConfig.startsWith("file:")) {
               brokerConfig = "file:" + brokerConfig;
            }

            brokerDTO.server.configuration = brokerConfig;
         }
      }

      return brokerDTO;
   }

   protected String getConfiguration() {
      if (configuration == null) {
         File xmlFile = new File(new File(new File(getBrokerInstance()), "etc"), "bootstrap.xml");
         configuration = "xml:" + xmlFile.toURI().toString().substring("file:".length());

         // To support Windows paths as explained above.
         configuration = configuration.replace("\\", "/");

         ActiveMQBootstrapLogger.LOGGER.usingBrokerConfig(configuration);
      }

      return configuration;
   }

}
