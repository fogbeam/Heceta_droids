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
package org.apache.droids.examples;

import java.util.Queue;
import org.apache.droids.api.Handler;
import org.apache.droids.api.Link;
import org.apache.droids.api.TaskMaster;
import org.apache.droids.api.Worker;
import org.apache.droids.examples.handler.ExceptionReportHandler;
import org.apache.droids.robot.crawler.CrawlingDroid;
import org.apache.droids.robot.crawler.CrawlingWorker;

public class ExceptionCrawlingDroid extends CrawlingDroid {

	public ExceptionCrawlingDroid(Queue<Link> queue, TaskMaster<Link> taskMaster) {
		super(queue, taskMaster);
	}

	@Override
	public Worker<Link> getNewWorker() {
		final CrawlingWorker worker = new CrawlingWorker(this);
		Handler testHandler = new ExceptionReportHandler();
		worker.setHandlerFactory(DroidsFactory
				.createDefaultHandlerFactory(testHandler));
		return worker;
	}

}
