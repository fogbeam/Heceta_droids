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
package org.apache.droids.parse.html;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.droids.LinkTask;
import org.apache.droids.api.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @deprecated Use Tika's LinkContentHandler instead.
 *             You will need to convert Tika's Links into Droids Links.
 *
 */
@Deprecated
public class LinkExtractor extends DefaultHandler 
{

  /**
   * Name of element that may contain base URI
   */
  private static final String BASE_ELEMENT = "base";

  /**
   * Name of attribute for base URI
   */
  private static final String BASE_ATTRIBUTE = "href";

  /**
   * Logger
   */
  private static final Logger LOG = LoggerFactory.getLogger(LinkExtractor.class);

  /**
   * Base url for host reference
   */
  private final Link base;

  /**
   * Map with the pair label-attribute for the accepted items
   */
  private final Map<String, String> elements;

  /**
   * List of links
   */
  private ArrayList<Link> links = new ArrayList<Link>();

  /**
   * Set of URIs visited yet
   */
  private Set<String> history = null;
  
  /**
   * Base URI for resolving
   */
  private URI baseUri = null;
  
  /**
   * Check for base elements
   */
  private boolean checkBase = true;
  
  /**
   * The parsed link
   */
  private URI link = null;

  /**
   * Anchor text
   */
  private StringBuilder anchorText = new StringBuilder();

  public LinkExtractor(Link base, Map<String, String> elements) {
    super();
    this.base = base;
    this.elements = elements;
    this.baseUri = base.getURI();
  }
  
  @Override
  public void startDocument() throws SAXException {
    history = new HashSet<String>();
    history.add(base.getURI().toString());
  }

  @Override
  public void startElement(String uri, String loc, String raw, Attributes att) throws SAXException 
  {
    if(checkBase && BASE_ELEMENT.equalsIgnoreCase(loc) && att.getValue(BASE_ATTRIBUTE) != null) {
      try {
        baseUri = new URI(att.getValue(BASE_ATTRIBUTE));
        LOG.debug("Found base URI: " + baseUri);
        checkBase = false;
      } 
      catch ( URISyntaxException e) {
        LOG.error("Base URI not valid: " + att.getValue(BASE_ATTRIBUTE));
      }
    }
    
    Iterator<String> it = elements.keySet().iterator();
    String elem, linkAtt;
    while (it.hasNext()) {
      elem = it.next();
      linkAtt = elements.get(elem);
      if (elem.equalsIgnoreCase(loc) && att.getValue(linkAtt) != null) {
        link = getURI(att.getValue(linkAtt));
        LOG.debug("Found element: " + elem + " with link: " + link);
        if (link != null) {
        	addOutlinkURI(link.toString());
        	link = null;
                anchorText = new StringBuilder();
        }
      }
    }
  }

  @Override 
  public void characters(char[] ch, int start, int length) {
    anchorText.append(ch, start, length);
  }
  
  @Override
  public void endElement(String uri, String loc, String raw) {
    Iterator<String> it = elements.keySet().iterator();
    String elem;
    while (it.hasNext()) {
      elem = it.next();
      if (elem.equalsIgnoreCase(loc)) {
        addAnchorText(anchorText.toString());
      }
    }
  }


  @Override
  public void endDocument() throws SAXException 
  {
    history = null;
    LOG.debug("Found " + links.size() + " outliks");
  }

  /**
   * Setting Anchor text of last added anchor
   * @param anchorText Text to be added
   */
  private void addAnchorText(String anchorText) {
    if (!links.isEmpty()) {
      LinkTask l = (LinkTask) links.get(links.size() - 1);
      l.setAnchorText(anchorText.replaceAll("\\s+", " ").trim());
      LOG.debug("Adding anchor: " + l.getAnchorText() + " on link: " + l);
    } 
  }


  /**
   * Add the outlink to the {@code links} list if the value is a valid URI.
   * @param value the outlink.
   */
  public void addOutlinkURI(String value) {
    if (history == null)
      history = new HashSet<String>();
    if (links == null)
      links = new ArrayList<Link>();
    if (history.add(link.toString())) {
      links.add(new LinkTask(base, link, base.getDepth() + 1));
      LOG.debug("Added outlink: " + link + " with depth: " + base.getDepth() + 1);
    }
  }

  public Collection<Link> getLinks() {
    return links;
  }

  public Map<String, String> getElements() {
    return elements;
  }

  /**
   * Transform a String into an URI.
   * @param target the URI in String format.
   * @return the URI or null if the URI is not valid.
   */
  private URI getURI(String target) {
    target = target.replaceAll("\\s", "%20");
    try {
      if (!target.toLowerCase().startsWith("javascript")
          && !target.contains(":/")) {
        return baseUri.resolve(target.split("#")[0]);
      } 
      else if (!target.toLowerCase().startsWith("javascript")) {
        return new URI(target.split("#")[0]);
      }
    } 
    catch (Exception e) {
      LOG.error("URI not valid: " + target);
    }
    return null;
  }
}
