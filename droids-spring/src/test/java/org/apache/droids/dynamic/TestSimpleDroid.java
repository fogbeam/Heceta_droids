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

import junit.framework.Assert;

import org.apache.droids.api.Droid;
import org.apache.droids.api.Link;
import org.apache.droids.handle.ReportHandler;
import org.apache.droids.localserver.LocalHttpServer;
import org.apache.droids.localserver.ResourceHandler;
import org.apache.droids.robot.crawler.CrawlingDroid;
import org.apache.droids.robot.crawler.ReportCrawlingDroid;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSimpleDroid {

	protected LocalHttpServer testserver;

	private final static ApplicationContext context = new ClassPathXmlApplicationContext(
			"classpath:/droids-core-test-context.xml");

	private DroidsConfig droidsConfig = null;

	@Before
	public void setUp() throws Exception {
		this.droidsConfig = (DroidsConfig) TestSimpleDroid.context
				.getBean("org.apache.droids.dynamic.DroidsConfig");
		this.testserver = new LocalHttpServer();
	}

	@Test
	public void testReportCrawlingDroid() throws Exception {
		this.testserver.register("*", new ResourceHandler());
		this.testserver.start();

		String baseURI = "http:/" + this.testserver.getServiceAddress();
		String targetURI = baseURI + "/start_html";

		Droid<Link> droid = createSimpleReportCrawlingDroid(targetURI);

		droid.init();
		droid.start();
		droid.getTaskMaster().awaitTermination(30, TimeUnit.SECONDS);

		Assert.assertFalse(ReportHandler.getReport().isEmpty());
		Assert.assertEquals(5, ReportHandler.getReport().size());
		Assert.assertTrue(ReportHandler.getReport().contains(
				baseURI + "/start_html"));
		Assert.assertTrue(ReportHandler.getReport().contains(
				baseURI + "/page1_html"));
		Assert.assertTrue(ReportHandler.getReport().contains(
				baseURI + "/page2_html"));
		Assert.assertTrue(ReportHandler.getReport().contains(
				baseURI + "/page3_html"));
		Assert.assertTrue(ReportHandler.getReport().contains(
				baseURI + "/page4_html"));

		ReportHandler.recycle();
	}

	private Droid<Link> createSimpleReportCrawlingDroid(final String targetURI) {
		Droid<Link> droid = this.droidsConfig.getDroid("report");

		Assert.assertFalse("Droid is null.", droid == null);
		Assert.assertTrue(
				"The test droid must be an instance of ReportCrawlingDroid",
				droid instanceof ReportCrawlingDroid);

		final List<String> locations = new ArrayList<String>();
		locations.add(targetURI);
		((CrawlingDroid) droid).setInitialLocations(locations);

		return droid;
	}

}
