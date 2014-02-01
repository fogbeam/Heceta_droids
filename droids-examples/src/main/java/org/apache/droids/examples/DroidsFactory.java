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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.droids.api.Droid;
import org.apache.droids.api.Handler;
import org.apache.droids.api.Link;
import org.apache.droids.api.URLFilter;
import org.apache.droids.delay.SimpleDelayTimer;
import org.apache.droids.handle.SaveHandler;
import org.apache.droids.helper.factories.HandlerFactory;
import org.apache.droids.helper.factories.ParserFactory;
import org.apache.droids.helper.factories.ProtocolFactory;
import org.apache.droids.helper.factories.URLFiltersFactory;
import org.apache.droids.impl.DefaultTaskExceptionHandler;
import org.apache.droids.impl.SequentialTaskMaster;
import org.apache.droids.impl.SimpleTaskQueueWithHistory;
import org.apache.droids.protocol.http.HttpProtocol;
import org.apache.droids.robot.crawler.CrawlingDroid;
import org.apache.droids.robot.crawler.ReportCrawlingDroid;
import org.apache.droids.tika.TikaDocumentParser;

/**
 * 
 * Helper class for creating defaults.
 * 
 */
public class DroidsFactory {

	public static ParserFactory createDefaultParserFactory() {
		ParserFactory parserFactory = new ParserFactory();
		TikaDocumentParser tikaParser = new TikaDocumentParser();
		parserFactory.getMap().put("text/html", tikaParser);
		return parserFactory;
	}

	public static ProtocolFactory createDefaultProtocolFactory() {
		ProtocolFactory protocolFactory = new ProtocolFactory();
		HttpProtocol httpProtocol = new HttpProtocol();
		httpProtocol.setForceAllow(true);

		protocolFactory.getMap().put("http", httpProtocol);
		return protocolFactory;
	}

	public static URLFiltersFactory createDefaultURLFiltersFactory() {
		URLFiltersFactory filtersFactory = new URLFiltersFactory();
		URLFilter defaultURLFilter = new URLFilter() {

			public String filter(String urlString) {
				return urlString;
			}

		};
		filtersFactory.getMap().put("default", defaultURLFilter);
		return filtersFactory;
	}

	public static HandlerFactory createDefaultHandlerFactory(
			Handler defaultHandler) {
		HandlerFactory handlerFactory = new HandlerFactory();
		handlerFactory.getMap().put("default", defaultHandler);
		return handlerFactory;
	}

	public static Droid<Link> createSimpleSaveCrawlingDroid(String targetURI) {
		ParserFactory parserFactory = createDefaultParserFactory();
		ProtocolFactory protocolFactory = createDefaultProtocolFactory();
		URLFiltersFactory filtersFactory = createDefaultURLFiltersFactory();

		SimpleDelayTimer simpleDelayTimer = new SimpleDelayTimer();
		simpleDelayTimer.setDelayMillis(100);

		SimpleTaskQueueWithHistory<Link> simpleQueue = new SimpleTaskQueueWithHistory<Link>();

		SequentialTaskMaster<Link> taskMaster = new SequentialTaskMaster<Link>();
		taskMaster.setDelayTimer(simpleDelayTimer);
		taskMaster.setExceptionHandler(new DefaultTaskExceptionHandler());

		CrawlingDroid crawler = new SaveCrawlingDroid(simpleQueue, taskMaster,
				new SaveHandler());
		crawler.setFiltersFactory(filtersFactory);
		crawler.setParserFactory(parserFactory);
		crawler.setProtocolFactory(protocolFactory);

		Collection<String> initialLocations = new ArrayList<String>();
		initialLocations.add(targetURI);
		crawler.setInitialLocations(initialLocations);
		return crawler;
	}

	public static Droid<Link> createSimpleReportCrawlingDroid(String targetURI) {
		ParserFactory parserFactory = createDefaultParserFactory();
		ProtocolFactory protocolFactory = createDefaultProtocolFactory();
		URLFiltersFactory filtersFactory = createDefaultURLFiltersFactory();

		SimpleDelayTimer simpleDelayTimer = new SimpleDelayTimer();
		simpleDelayTimer.setDelayMillis(100);

		SequentialTaskMaster<Link> taskMaster = new SequentialTaskMaster<Link>();
		// MultiThreadedTaskMaster<Link> taskMaster = new
		// MultiThreadedTaskMaster<Link>();
		taskMaster.setDelayTimer(simpleDelayTimer);
		taskMaster.setExceptionHandler(new DefaultTaskExceptionHandler());

		Queue<Link> queue = new LinkedList<Link>();

		CrawlingDroid crawler = new ReportCrawlingDroid(queue, taskMaster);
		crawler.setFiltersFactory(filtersFactory);
		crawler.setParserFactory(parserFactory);
		crawler.setProtocolFactory(protocolFactory);

		Collection<String> initialLocations = new ArrayList<String>();
		initialLocations.add(targetURI);
		crawler.setInitialLocations(initialLocations);
		return crawler;
	}

	public static Droid<Link> createSimpleExceptionCrawlingDroid(
			String targetURI) {
		ParserFactory parserFactory = createDefaultParserFactory();
		ProtocolFactory protocolFactory = createDefaultProtocolFactory();
		URLFiltersFactory filtersFactory = createDefaultURLFiltersFactory();

		SimpleDelayTimer simpleDelayTimer = new SimpleDelayTimer();
		simpleDelayTimer.setDelayMillis(100);

		Queue<Link> queue = new LinkedList<Link>();

		SequentialTaskMaster<Link> taskMaster = new SequentialTaskMaster<Link>();
		// MultiThreadedTaskMaster<Link> taskMaster = new
		// MultiThreadedTaskMaster<Link>();
		taskMaster.setDelayTimer(simpleDelayTimer);
		taskMaster.setExceptionHandler(new DefaultTaskExceptionHandler());

		CrawlingDroid crawler = new ExceptionCrawlingDroid(queue, taskMaster);
		crawler.setFiltersFactory(filtersFactory);
		crawler.setParserFactory(parserFactory);
		crawler.setProtocolFactory(protocolFactory);

		Collection<String> initialLocations = new ArrayList<String>();
		initialLocations.add(targetURI);
		crawler.setInitialLocations(initialLocations);
		return crawler;
	}

}
