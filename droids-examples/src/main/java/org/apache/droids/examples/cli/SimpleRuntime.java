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
package org.apache.droids.examples.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.apache.droids.robot.crawler.CrawlingDroid;
import org.apache.droids.tika.TikaDocumentParser;
import org.apache.droids.api.Link;
import org.apache.droids.delay.SimpleDelayTimer;
import org.apache.droids.examples.SysoutCrawlingDroid;
import org.apache.droids.handle.SysoutHandler;
import org.apache.droids.helper.factories.DroidFactory;
import org.apache.droids.helper.factories.HandlerFactory;
import org.apache.droids.helper.factories.ParserFactory;
import org.apache.droids.helper.factories.ProtocolFactory;
import org.apache.droids.helper.factories.URLFiltersFactory;
import org.apache.droids.impl.DefaultTaskExceptionHandler;
import org.apache.droids.impl.SequentialTaskMaster;
import org.apache.droids.net.RegexURLFilter;
import org.apache.droids.protocol.http.DroidsHttpClient;
import org.apache.droids.protocol.http.HttpProtocol;
import org.apache.http.HttpVersion;
import org.apache.http.conn.params.ConnManagerParamBean;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParamBean;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParamBean;
import org.apache.http.protocol.HTTP;

/**
 * Simple Droids runtime that wires various components together in Java code
 * without using a DI framework.
 * 
 */
public class SimpleRuntime {

	private SimpleRuntime() {
	}

	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			System.out.println("Please specify a URL to crawl");
			System.exit(-1);
		}
		String targetURL = args[0];

		// Create parser factory. Support basic HTML markup only
		ParserFactory parserFactory = new ParserFactory();
		TikaDocumentParser tikaParser = new TikaDocumentParser();
		parserFactory.getMap().put("text/html", tikaParser);

		// Create protocol factory. Support HTTP/S only.
		ProtocolFactory protocolFactory = new ProtocolFactory();

		// Create and configure HTTP client
		HttpParams params = new BasicHttpParams();
		HttpProtocolParamBean hppb = new HttpProtocolParamBean(params);
		HttpConnectionParamBean hcpb = new HttpConnectionParamBean(params);
		ConnManagerParamBean cmpb = new ConnManagerParamBean(params);

		// Set protocol parametes
		hppb.setVersion(HttpVersion.HTTP_1_1);
		hppb.setContentCharset(HTTP.ISO_8859_1);
		hppb.setUseExpectContinue(true);
		// Set connection parameters
		hcpb.setStaleCheckingEnabled(false);
		// Set connection manager parameters
		ConnPerRouteBean connPerRouteBean = new ConnPerRouteBean();
		connPerRouteBean.setDefaultMaxPerRoute(2);
		cmpb.setConnectionsPerRoute(connPerRouteBean);

		DroidsHttpClient httpclient = new DroidsHttpClient(params);

		HttpProtocol httpProtocol = new HttpProtocol(httpclient);
		protocolFactory.getMap().put("http", httpProtocol);
		protocolFactory.getMap().put("https", httpProtocol);

		// Create URL filter factory.
		URLFiltersFactory filtersFactory = new URLFiltersFactory();
		RegexURLFilter defaultURLFilter = new RegexURLFilter();
		defaultURLFilter.setFile("classpath:/regex-urlfilter.txt");
		filtersFactory.getMap().put("default", defaultURLFilter);

		// Create handler factory. Provide sysout handler only.
		HandlerFactory handlerFactory = new HandlerFactory();
		SysoutHandler defaultHandler = new SysoutHandler();
		handlerFactory.getMap().put("default", defaultHandler);

		// Create droid factory. Leave it empty for now.
		DroidFactory<Link> droidFactory = new DroidFactory<Link>();

		// Create default droid
		SimpleDelayTimer simpleDelayTimer = new SimpleDelayTimer();
		simpleDelayTimer.setDelayMillis(100);

		Queue<Link> simpleQueue = new LinkedList<Link>();

		SequentialTaskMaster<Link> taskMaster = new SequentialTaskMaster<Link>();
		taskMaster.setDelayTimer(simpleDelayTimer);
		taskMaster.setExceptionHandler(new DefaultTaskExceptionHandler());

		CrawlingDroid helloCrawler = new SysoutCrawlingDroid(simpleQueue,
				taskMaster);
		helloCrawler.setFiltersFactory(filtersFactory);
		helloCrawler.setParserFactory(parserFactory);
		helloCrawler.setProtocolFactory(protocolFactory);

		Collection<String> initialLocations = new ArrayList<String>();
		initialLocations.add(targetURL);
		helloCrawler.setInitialLocations(initialLocations);

		// Initialize and start the crawler
		helloCrawler.init();
		helloCrawler.start();

		// Await termination
		helloCrawler.getTaskMaster().awaitTermination(0, TimeUnit.MILLISECONDS);
		// Shut down the HTTP connection manager
		httpclient.getConnectionManager().shutdown();
	}

}
