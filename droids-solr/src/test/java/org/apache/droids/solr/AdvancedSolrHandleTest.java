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
package org.apache.droids.solr;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import junit.framework.TestCase;

import org.apache.droids.exception.DroidsException;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;

public class AdvancedSolrHandleTest extends TestCase {

	SolrServer solr;
	
	String simpleHtmlPage = "" +
		"<html>" +
			"<body>" +
				"<div>" +
					"<p>p0</p>" +
					"<p>p1</p>" +
					"<p>p2</p>" +
				"</div>" +
				"<div>" +
					"<p>p3</p>" +
					"<p>p4</p>" +
					"<p>p5</p>" +
				"</div>" +
			"</body>" +
		"</html>";

	protected String getSolrHome() {
		return "example";
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		SolrResourceLoader loader = new SolrResourceLoader(getSolrHome());
		CoreContainer container = new CoreContainer(loader);
		CoreDescriptor descriptor = new CoreDescriptor(container, "cname", ".");
		SolrCore core = container.create(descriptor);
		container.register(core.getName(), core, false);

		solr = new EmbeddedSolrServer(container, core.getName());
	}

	public void tearDown() throws Exception {
		// remove everything....
		solr.deleteByQuery("*:*");
		solr.commit();
	}
	
	public void performSelection(String html, String field, String selector, String expectedValue) throws IOException, DroidsException, URISyntaxException, SolrServerException {
		AdvancedSolrHandler handler = new AdvancedSolrHandler();
		handler.setServer(solr);
		
		HashMap<String, String> selectors = new HashMap<String, String>();
		selectors.put(field, selector);
		handler.setSelectors(selectors);
		
		MockContentEntity contentEntity = new MockContentEntity();
		contentEntity.setCharset("UTF-8");
		contentEntity.setMimeType("text/html");
		contentEntity.setText(html);
		
		handler.handle(new URI("http://localhost/"), contentEntity);
		solr.commit();
		
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		query.setFields(field);
		QueryResponse response = solr.query(query);
		
		SolrDocument doc = response.getResults().iterator().next();
		String value = (String)doc.getFieldValue(field);
		
		assertEquals(expectedValue, value);
	}
	
	public void testSelectorA() throws Exception {
		performSelection(simpleHtmlPage, "selector", "/html[0]/body[0]/div[0]/p[0]", "p0");
	}
	
	public void testSelectorB() throws Exception {
		performSelection(simpleHtmlPage, "selector", "/html[0]/body[0]/div[1]/p[1]", "p4");
	}
	
	public void testSelectorC() throws Exception {
		performSelection(simpleHtmlPage, "selector", "/html[0]/body[0]/div[1]", "p3p4p5");
	}

}