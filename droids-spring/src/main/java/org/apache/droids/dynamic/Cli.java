/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.droids.dynamic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.droids.api.Droid;
import org.apache.droids.robot.crawler.CrawlingDroid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The principal class to start droids. The commandline prepares the context 
 * and starts the droid that is specified in the arguments. 
 *
 * @version 1.0
 *
 */
public class Cli {
  private Cli(){
  }
  private static final Logger LOG = LoggerFactory.getLogger(Cli.class);

  /**
   * Invoke the processing with droids.
   * @param args You need to provide the droid name (e.g. crawler-x-m02y07) and optional the config file.
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      LOG.error("To invoke a droid:\n"
          + "You need to provide a droid name (e.g. crawler-x-m02y07)" +
              " and optionally an initial location to crawl");
      return;
    }
    String name = args[0];
    String location = null;
    if (args.length > 1) {
      location = args[1];
    }
    ApplicationContext context = new ClassPathXmlApplicationContext( 
        "classpath:/org/apache/droids/dynamic/droids-core-context.xml");
    
    DroidsConfig config = (DroidsConfig) context.getBean("org.apache.droids.dynamic.DroidsConfig");
    Droid droid = config.getDroid(name);
    
    if (droid == null) {
      LOG.error("Droid " + name + " is not defined");
      LOG.error(getUsage());
      return;
    }
    
    LOG.info("A p a c h e    D r o i d s - an intelligent robot framework");
    if (droid instanceof CrawlingDroid) {
      List<String> locations = new ArrayList<String>();
      if (location == null) {
        LOG.error("Droid " + name + " is a crawler, however you have not defined a starting location.");
        LOG.error(getUsage());
        return;
      }
      locations.add(location);
      ((CrawlingDroid) droid).setInitialLocations(locations);
    }
    droid.init();
    droid.start();
    droid.getTaskMaster().awaitTermination(0, TimeUnit.MILLISECONDS);
  }

  private static String getUsage() {
    StringBuffer message = new StringBuffer();
    message.append("Please start Droids like follows. ");
    message.append("\n");
    message.append("The first argument should define the name of " +
    		"the droid you want to start (e.g. \"hello\"). ");
    message.append("\n");
    message.append("The second argument may be a starting location. " +
    		"This initial location is mandatory for crawler and walker, so you need to define it.");
    return message.toString();
  }

}
