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
package org.apache.droids.parse.html;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.droids.api.ContentEntity;
import org.apache.droids.api.Link;
import org.apache.droids.api.Parse;
import org.apache.droids.api.Parser;
import org.apache.droids.api.Task;
import org.apache.droids.exception.ContentFormatViolationException;
import org.apache.droids.exception.DroidsException;
import org.apache.droids.parse.ParseImpl;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.filters.ElementRemover;
import org.cyberneko.html.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * @version 1.0
 * @deprecated Use TikaDocumentParser in droids-tika as it uses Tika to parse documents, 
 *             which is better maintained than this class. It also has more functionality.
 * 
 */
@Deprecated
public class HtmlParser implements Parser {

  private Map<String, String> elements= null;

  public Map<String, String> getElements() {
    if (elements == null) {
      elements = new HashMap<String, String>();
    }
    return elements;
  }

  public void setElements(Map<String, String> elements) {
    this.elements = elements;
  }

  @Override
  public Parse parse(ContentEntity entity, Task newLink) throws DroidsException, IOException {
    // setup filter chain
    XMLDocumentFilter[] filters = { getRemover() };
    // create HTML parser
    SAXParser parser = getParser(filters);
    LinkExtractor linkExtractor = new LinkExtractor((Link)newLink, elements);
    parser.setContentHandler(linkExtractor);
    InputStream instream = entity.obtainContent();
    try {
      parser.parse(new InputSource(instream));
    } catch (SAXException ex) {
      throw new ContentFormatViolationException("Failure parsing HTML content", ex);
    } finally {
      instream.close();
    }
    return new ParseImpl(newLink.getId(),linkExtractor.getLinks());
  }

  private SAXParser getParser(XMLDocumentFilter[] filters) {
    SAXParser parser = new SAXParser();
    try {
      parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
      parser.setFeature(
        "http://cyberneko.org/html/features/balance-tags/ignore-outside-content",
        false);
      parser.setFeature(
        "http://cyberneko.org/html/features/balance-tags/document-fragment",
        true);
      parser.setFeature(
        "http://cyberneko.org/html/features/report-errors",
        false);
    } catch (SAXNotRecognizedException ex) {
      throw new IllegalStateException(ex);
    } catch (SAXNotSupportedException ex) {
      throw new IllegalStateException(ex);
    }
    return parser;
  }

  private ElementRemover getRemover() {
    // create element remover filter
    final ElementRemover remover = new ElementRemover();
    // set which elements to accept
    for (String key : elements.keySet()) {
      String value = elements.get(key);
      remover.acceptElement(key, new String[] { value });
    }
    // completely remove some elements
    //remover.removeElement("head");
    return remover;
  }

}
