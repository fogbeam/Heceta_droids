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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.droids.api.ContentEntity;
import org.apache.droids.api.Handler;
import org.apache.droids.exception.DroidsException;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.cyberneko.html.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * A Droids Handler which allows to specify selectors to store 
 * documents' parts in a Solr index.
 * 
 * A selector is an Entry made of a key which matches the solr fiel and
 * of a value which correspond to a path selector.
 * 
 * Path selectors are always absolute and supports indexes. 
 * 
 * Here are some examples:
 * 	- /html[0]/div[0]
 * 	- /html[0]/div[0]/p[0]
 * 	- /html[0]/div[1]/p[2]
 */
public class AdvancedSolrHandler implements Handler {

	/**
	 * A solr server
	 */
	private SolrServer server;
	
	/**
	 * The selectors allow to save specific parts of the document in the index.
	 * The HashMap's key matches the Solr field.
	 * The HashMap's value is an absolute path corresponding to an element.
	 */
	private Map<String, String> selectors;
	
	/**
	 * A content handler
	 */
	private SolrContentHandler contentHandler = new SolrContentHandler(selectors);

	/**
	 * An HTML parser
	 */
	private SAXParser parser;
	
	/**
	 * @return the current solr server
	 */
	public SolrServer getServer() {
		return server;
	}

	/**
	 * @param solr a solr server 
	 */
	public void setServer(SolrServer solr) {
		this.server = solr;
	}

	/**
	 * @return the current path selectors
	 */
	public Map<String, String> getSelectors() {
		return selectors;
	}

	/**
	 * @param selectors an hash map containing path selectors
	 */
	public void setSelectors(HashMap<String, String> selectors) {
		contentHandler.initPatterns(selectors);
		this.selectors = selectors;
	}

	/* 
	 * @see org.apache.droids.api.Handler#handle(java.net.URI, org.apache.droids.api.ContentEntity)
	 */
    @Override
	public void handle(URI uri, ContentEntity entity) throws IOException, DroidsException {
		SolrInputDocument doc = createSolrInputDocument(uri, entity);
		try {
			server.add(doc);
		} catch (SolrServerException e) {
			throw new DroidsException(e);
		}
	}

	/**
	 * Generates a SolrInputDocument from an URI and a ContentEntity 
	 * which correspond to the document which need to be saved in the index
	 * 
	 * @param uri an uri
	 * @param entity an entity
	 * @return
	 */
	private SolrInputDocument createSolrInputDocument(URI uri, ContentEntity entity) {
		SolrInputDocument doc = new SolrInputDocument();

		doc.setField("id", uri.getPath());
		doc.setField("name", uri.toASCIIString());
		doc.setField("host", uri.getHost());
		doc.setField("mime", entity.getMimeType());
		doc.setField("content", entity.getParse().getText());
		
		if (parser == null) initParser();
		
		if (!selectors.isEmpty()) {
			contentHandler.initDocument(doc);
			try {
				parser.setContentHandler(contentHandler);
				parser.parse(new InputSource(entity.obtainContent()));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
		}
		
		return doc;
	}

	/**
	 * Initialize a Cyber Necko parser configured to return lower case element's names
	 * 
	 * @return
	 */
	private SAXParser initParser() {
		parser = new SAXParser();
		try {
			parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
			parser.setFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content", false);
			parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);
			parser.setFeature("http://cyberneko.org/html/features/report-errors", false);
		} catch (SAXNotRecognizedException ex) {
			throw new IllegalStateException(ex);
		} catch (SAXNotSupportedException ex) {
			throw new IllegalStateException(ex);
		}
		return parser;
	}

	/**
	 * A class that implements a SAX ContentHandler and uses patterns to record documents 
	 * elements in a SolrInputDocuement.
	 */
	private class SolrContentHandler implements ContentHandler {

		private SolrInputDocument doc;
		
		/**
		 * the patterns which match element's path
		 */
		private Map<String, Pattern> patterns = new HashMap<String, Pattern>();

		/**
		 * stores the values which match the patterns
		 */
		private Map<String, String> valueRecorders = new HashMap<String, String>();

		/**
		 * A two dimensional stack used to store the current path
		 */
		private Stack<Stack<String>> path = new Stack<Stack<String>>();

		private int level = 0;

		private int lastLevel = 0;
		
		/**
		 * Constructor
		 * 
		 * @param selectors an Map which contains selectors
		 */
		public SolrContentHandler(Map<String, String> selectors) {
			initPatterns(selectors);
		}
		
		/**
		 * Initialize patterns
		 * @param selectors
		 */
		public void initPatterns(Map<String, String> selectors) {
			if (selectors != null) {
				
				// clear the current patterns
				patterns.clear();
				
				// pattern for the element and its index
				final Pattern p = Pattern.compile("^([a-zA-Z:-_\\.]+)(\\[([0-9]*)\\]){0,1}$");
	
				// for each selector
				Set<String> keys = selectors.keySet();
				for (String key : keys) {
					// creating a pattern
					String regex = "^";
					String selector = selectors.get(key);
					String[] elements = selector.split("/");
					// which match all the elements and their respective indices
					for (String element : elements) {
						Matcher m = p.matcher(element);
						if (m.find()) {
							String elementName = m.group(1);
							String elementIndex = m.group(3);
							regex += "/" + elementName;
							if (elementIndex == null) {
								regex += "\\[[0-9]*\\]";
							} else {
								regex += "\\[" + elementIndex + "\\]";
							}
						}
					}
					regex += "$";
	
					// storing the new Pattern
					Pattern pattern = Pattern.compile(regex);
					patterns.put(key, pattern);
				}
			}
		}
		
		/**
		 * Initialization of the document used for indexation
		 * 
		 * @param doc a solr document
		 */
		public void initDocument(SolrInputDocument doc) {
			this.doc = doc;
		}

		/* 
		 * @see org.xml.sax.ContentHandler#startDocument()
		 */
		@Override
		public void startDocument() throws SAXException {
			level = 0;
		}

		/* 
		 * @see org.xml.sax.ContentHandler#endDocument()
		 */
		@Override
		public void endDocument() throws SAXException {
			level = 0;
		}

		/* 
		 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {			
			// set the level properties.
			level++;

			// go down in the hierarchy of elements.
			if (level == lastLevel && path.size() > 0) {
				path.get(path.size() - 1).add(localName);
			} else if (level > lastLevel) {
				Stack<String> s = new Stack<String>();
				s.add(localName);
				path.add(s);
			}

			// if the path matches a pattern, starts recording the matching content.
			String path = getCurrentPath();
			Iterator<Entry<String, Pattern>> entries = patterns.entrySet().iterator();
			while (entries.hasNext()) {
				Entry<String, Pattern> entry = entries.next();
				String patternName = entry.getKey();
				Pattern patternValue = entry.getValue();
				Matcher matcher = patternValue.matcher(path);
				if (matcher.find()) {
					valueRecorders.put(patternName, "");
				}
			}

		}

		/* 
		 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
		 */
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			// check if we climb in the hierarchy.
			if (level < lastLevel && path.size() > 0) { 
				path.pop();
			}
			
			// set the level properties.
			lastLevel = level;
			level--;
			
			// if the path matches a selector, stores the matching content.
			String path = getCurrentPath();
			Iterator<Entry<String, Pattern>> entries = patterns.entrySet().iterator();
			while (entries.hasNext()) {
				Entry<String, Pattern> entry = entries.next();
				String patternName = entry.getKey();
				Pattern patternValue = entry.getValue();
				Matcher matcher = patternValue.matcher(path);
				if (matcher.find()) {
					// add the matching content to the solr document.
					String value = valueRecorders.remove(patternName);
					doc.addField(patternName, value);
				}
			}
		}

		/* 
		 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
		 */
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			// store the content in each recorder
			Set<String> keys = valueRecorders.keySet();
			for (String key : keys) {
				String recorder = valueRecorders.get(key);
				recorder += new String(Arrays.copyOfRange(ch, start, start + length));
				valueRecorders.put(key, recorder);
			}
		}
		
		/* 
		 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
		 */
		@Override
		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
			characters(ch, start, length);
		}

		/* 
		 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
		 */
		@Override
		public void startPrefixMapping(String prefix, String uri) throws SAXException {

		}

		/* 
		 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
		 */
		@Override
		public void endPrefixMapping(String prefix) throws SAXException {

		}

		/* 
		 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
		 */
		@Override
		public void processingInstruction(String target, String data) throws SAXException {

		}

		/* 
		 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
		 */
		@Override
		public void setDocumentLocator(Locator locator) {

		}

		/* 
		 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
		 */
		@Override
		public void skippedEntity(String name) throws SAXException {

		}

		/**
		 * Computes the current path by crossing the path stack.
		 * 
		 * @return a path
		 */
		private String getCurrentPath() {
			String p = "";

			// find the element at each level
			for (Stack<String> h : path) {
				String element = h.get(h.size() - 1);
				Integer index = -1;
				// find the element's index 
				for (String e : h) {
					if (e.equals(element)) {
						index++;
					}
				}
				// path with the index at each level
				p += "/" + element + "[" + index + "]";
			}
			return p;
		}

	}
}
