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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.droids.LinkTask;
import org.apache.droids.api.ContentEntity;
import org.apache.droids.api.Link;
import org.apache.droids.examples.DroidsFactory;
import org.apache.droids.examples.SysoutCrawlingDroid;
import org.apache.droids.exception.DroidsException;
import org.apache.droids.helper.factories.ParserFactory;
import org.apache.droids.helper.factories.ProtocolFactory;
import org.apache.droids.robot.crawler.CrawlingDroid;
import org.apache.droids.robot.crawler.CrawlingWorker;
import org.apache.droids.tika.TikaDocumentParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.defaultanswers.ReturnsMocks;

public class TestCrawlingWorker {
	CrawlingWorker instance;
	private TikaDocumentParser htmlParser;

	@Before
	public void initialize() {
		final Queue<Link> queue = new LinkedList<Link>();
		final CrawlingDroid droid = createDroid(queue);
		instance = (CrawlingWorker) droid.getNewWorker();
	}

	private final CrawlingDroid createDroid(final Queue<Link> queue) {
		final CrawlingDroid droid = new SysoutCrawlingDroid(queue, null);

		final ProtocolFactory protocolFactory = DroidsFactory
				.createDefaultProtocolFactory();
		droid.setProtocolFactory(protocolFactory);

		final ParserFactory parserFactory = parserSetup();
		droid.setParserFactory(parserFactory);
		return droid;
	}

	private final ParserFactory parserSetup() {
		final ParserFactory parserFactory = new ParserFactory();

		htmlParser = Mockito.mock(TikaDocumentParser.class, new ReturnsMocks());

		parserFactory.getMap().put("text/html", htmlParser);
		return parserFactory;
	}

	//
	@After
	public void cleanup() {
		instance = null;
		htmlParser = null;
	}

	//
	@Test
	public void nothingHappens() {
		// Arrange

		// Act

		// Assert
	}

	// execute
	@Test
	public void execute_linkIsParsed() throws DroidsException, IOException,
			URISyntaxException {
		// Arrange
		final Link link = new LinkTask(null, new URI("http://www.google.com"),
				1);

		// Act
		this.instance.execute(link);

		// Assert
		Mockito.verify(htmlParser).parse(Matchers.any(ContentEntity.class),
				Matchers.any(Link.class));
	}

}
