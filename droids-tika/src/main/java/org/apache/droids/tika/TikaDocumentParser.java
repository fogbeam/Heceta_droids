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
package org.apache.droids.tika;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.droids.LinkTask;
import org.apache.droids.api.ContentEntity;
import org.apache.droids.api.Link;
import org.apache.droids.api.Task;
import org.apache.droids.exception.DroidsException;
import org.apache.droids.tika.api.TikaParse;
import org.apache.droids.tika.api.TikaParser;
import org.apache.droids.tika.parse.TikaParseImpl;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.BoilerpipeContentHandler;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Parses documents using Tika.
 * Any document type that Tika can handle, can be handled by this class,
 * including HTML. 
 *
 */
public class TikaDocumentParser implements TikaParser {

  protected static final Logger LOG = LoggerFactory.getLogger(TikaDocumentParser.class);
  
  @Override
  public TikaParse parse(ContentEntity entity, Task task) throws DroidsException,
      IOException {
    // Init Tika objects
    org.apache.tika.parser.Parser parser = new AutoDetectParser();
    Metadata metadata = new Metadata();
    
    String charset = entity.getCharset();
    if (charset == null) {
      charset = "UTF-8";
    }
    
    StringWriter dataBuffer = new StringWriter();
    StringWriter bodyBuffer = new StringWriter();
    StringWriter mainContentBuffer = new StringWriter();
     
    SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
    TransformerHandler xmlHandler;
    try {
      xmlHandler = factory.newTransformerHandler();
    } catch (TransformerConfigurationException e) {
      throw new DroidsException(e);
    }
    xmlHandler.getTransformer().setOutputProperty(OutputKeys.METHOD, "xml");
    xmlHandler.setResult(new StreamResult(dataBuffer));
    
    BoilerpipeContentHandler mainContentHandler = new BoilerpipeContentHandler(mainContentBuffer);
    BodyContentHandler bodyHandler = new BodyContentHandler(bodyBuffer);
    LinkContentHandler linkHandler = new LinkContentHandler();
    
    TeeContentHandler parallelHandler = new TeeContentHandler(xmlHandler, mainContentHandler, bodyHandler, linkHandler );

    InputStream instream = entity.obtainContent();
    try {
      parser.parse(instream, parallelHandler, metadata, new ParseContext());
      
      ArrayList<Link> extractedTasks = new ArrayList<Link>();
      int depth = task.getDepth() + 1;
      if (task instanceof LinkTask) {
	      for(org.apache.tika.sax.Link tikaLink : linkHandler.getLinks()) {
	        try {
	          URI uri = new URI(tikaLink.getUri());
            // Test to see if the scheme is empty
            // This would indicate a relative URL, so resolve it against the task URI
            if(uri.getScheme() == null) {
              uri = ((Link) task).getURI().resolve(uri);
            }
            extractedTasks.add(new LinkTask((Link)task, uri, depth, tikaLink.getText()));
	        } catch (URISyntaxException e) {
	          if(LOG.isWarnEnabled()) {
	            LOG.warn("URI not valid: "+ tikaLink.getUri());
	          }
	        }
	      }
      }
      return new TikaParseImpl(dataBuffer.toString(), extractedTasks, bodyBuffer.toString(), mainContentBuffer.toString(), metadata);
    } catch (SAXException ex) {
      throw new DroidsException("Failure parsing document " + task.getId(), ex);
    } catch (TikaException ex) {
      throw new DroidsException("Failure parsing document " + task.getId(), ex);
    } finally {
      instream.close();
    } 
  }

}
