/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.apache.droids.robot.crawler;

import java.util.Queue;
import org.apache.droids.api.Link;
import org.apache.droids.api.TaskMaster;
import org.apache.droids.api.Worker;
import org.apache.droids.handle.ReportHandler;
import org.apache.droids.helper.factories.HandlerFactory;


/**
 * 
 * This simple CrawlingDroid uses the ReportHandler to handle all retrieved files.
 * 
 */
public class ReportCrawlingDroid extends CrawlingDroid {

  public ReportCrawlingDroid(Queue<Link> queue, TaskMaster<Link> taskMaster) {
    super(queue, taskMaster);
  }

  @Override
  public Worker<Link> getNewWorker() {
    final CrawlingWorker worker = new CrawlingWorker( this );
    HandlerFactory handlerFactory = new HandlerFactory();
    handlerFactory.getMap().put("default", new ReportHandler());
    worker.setHandlerFactory(handlerFactory);
    return worker;
  }

}
