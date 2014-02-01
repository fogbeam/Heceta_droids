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
package org.apache.droids.examples;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.apache.droids.api.Droid;
import org.apache.droids.api.Link;
import org.apache.droids.api.TaskExceptionHandler;
import org.apache.droids.api.TaskExceptionResult;
import org.apache.droids.api.TaskMaster;
import org.apache.droids.examples.DroidsFactory;
import org.apache.droids.handle.ReportHandler;
import org.apache.droids.localserver.LocalHttpServer;
import org.apache.droids.localserver.ResourceHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSimpleDroid {

	protected LocalHttpServer testserver;

	@Before
	public void initializeLocalTestServer() {
		this.testserver = new LocalHttpServer();
	}

	@After
	public void shutdownLocalTestServer() throws IOException {
		this.testserver.stop();
	}

	@Test
	public void testBasicCrawling() throws Exception {
		this.testserver.register("*", new ResourceHandler());
		this.testserver.start();

		String baseURI = "http:/" + this.testserver.getServiceAddress();
		String targetURI = baseURI + "/start_html";

		Droid<Link> droid = DroidsFactory.createSimpleReportCrawlingDroid(targetURI);

		droid.init();
		droid.start();

		while (!droid.getTaskMaster().awaitTermination(250L, TimeUnit.MILLISECONDS))
			;

		Assert.assertFalse(ReportHandler.getReport().isEmpty());
		Assert.assertEquals(5, ReportHandler.getReport().size());
		Assert.assertTrue(ReportHandler.getReport().contains(baseURI + "/start_html"));
		Assert.assertTrue(ReportHandler.getReport().contains(baseURI + "/page1_html"));
		Assert.assertTrue(ReportHandler.getReport().contains(baseURI + "/page2_html"));
		Assert.assertTrue(ReportHandler.getReport().contains(baseURI + "/page3_html"));
		Assert.assertTrue(ReportHandler.getReport().contains(baseURI + "/page4_html"));

		ReportHandler.recycle();
	}

	@Test
	public void testTerminateCrawlingOnException() throws Exception {
		this.testserver.register("*", new ResourceHandler());
		this.testserver.start();

		String baseURI = "http:/" + this.testserver.getServiceAddress();
		String targetURI = baseURI + "/start_html";

		Droid<Link> droid = DroidsFactory.createSimpleExceptionCrawlingDroid(targetURI);

		TaskMaster<Link> taskMaster = (TaskMaster<Link>) droid.getTaskMaster();
		taskMaster.setExceptionHandler(new TaskExceptionHandler() {

			public TaskExceptionResult handleException(Exception ex) {
				if (ex instanceof RuntimeException) {
					return TaskExceptionResult.FATAL;
				}
				return TaskExceptionResult.WARN;
			}

		});

		droid.init();
		droid.start();
		while (!droid.getTaskMaster().awaitTermination(250L, TimeUnit.MILLISECONDS))
			;

		Assert.assertFalse(ReportHandler.getReport().isEmpty());
		Assert.assertEquals(5, ReportHandler.getReport().size());
		Assert.assertTrue(ReportHandler.getReport().contains(baseURI + "/start_html"));
		Assert.assertTrue(ReportHandler.getReport().contains(baseURI + "/page1_html"));
		Assert.assertTrue(ReportHandler.getReport().contains(baseURI + "/page2_html"));
		Assert.assertTrue(ReportHandler.getReport().contains(baseURI + "/page3_html"));
		Assert.assertTrue(ReportHandler.getReport().contains(baseURI + "/page4_html"));

		ReportHandler.recycle();
	}

}
