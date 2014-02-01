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
package org.apache.droids.robot.crawler;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.droids.api.ContentEntity;
import org.apache.droids.api.Link;
import org.apache.droids.api.ManagedContentEntity;
import org.apache.droids.api.Parse;
import org.apache.droids.api.Parser;
import org.apache.droids.api.Protocol;
import org.apache.droids.api.Task;
import org.apache.droids.api.TaskValidator;
import org.apache.droids.api.Worker;
import org.apache.droids.exception.DroidsException;
import org.apache.droids.helper.factories.HandlerFactory;
import org.apache.droids.helper.factories.URLFiltersFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlingWorker implements Worker<Link> 
{

  private static final Logger LOG = LoggerFactory.getLogger(CrawlingWorker.class);

  private final CrawlingDroid droid;
  HandlerFactory handlerFactory;
  
  public CrawlingWorker( CrawlingDroid droid )
  {
    this.droid = droid;
  }

  @Override
  public void execute(Link link) throws DroidsException, IOException
  {
    final String userAgent = this.getClass().getCanonicalName();
    if (LOG.isDebugEnabled()) {
      LOG.debug("Starting " + userAgent);
    }
    URI uri = link.getURI();
    final Protocol protocol = droid.getProtocolFactory().getProtocol(uri);
    if (protocol == null) {
      if (LOG.isWarnEnabled()) {
        LOG.warn("Unsupported protocol scheme '" + uri.getScheme() + "'");
      }
      return;
    }
    
    if (protocol.isAllowed(uri)) {
      if (LOG.isInfoEnabled()) {
        LOG.info("Loading " + uri);
      }
      ManagedContentEntity entity = null;
      try {
        entity = protocol.load(uri);
      } catch(OutOfMemoryError e) {
        LOG.error("Out of memory processing: " + uri + " skipping", e);
        throw new DroidsException(e);
      }
      try {
        String contentType = entity.getMimeType();
        if (LOG.isDebugEnabled()) {
          LOG.debug("Content type " + contentType);
        }
        if (contentType == null){
          LOG.info("Missing content type... can't parse...");
        }
        else {
          Parser parser = droid.getParserFactory().getParser(contentType);
          if( parser == null ) {
            if (LOG.isDebugEnabled()) {
              LOG.debug("Could not find parser for " + contentType);
            }
          }
          else {
            Parse parse = parser.parse(entity, link);
            if( parse.getNewTasks() != null && parse.isFollowed() ) {
              Collection<Link> outlinks = getFilteredOutlinks( parse );
              droid.getQueue().addAll( outlinks );
            }
            entity.setParse(parse);
            handle(entity, link);
          }
        }
      } finally {
        entity.finish();
      }
    } 
    else {
      if (LOG.isInfoEnabled()) {
        LOG.info("Stopping processing since"
            + " bots are not allowed for " + uri );
      }
    }
  }
  
  protected void handle(ContentEntity entity, Link link) 
      throws DroidsException, IOException
  {
    getHandlerFactory().handle(link.getURI(), entity);
  }
  
  protected Collection<Link> getFilteredOutlinks( Parse parse )
  {
    URLFiltersFactory filters = droid.getFiltersFactory();
    TaskValidator< Link > linkValidator = droid.getLinkValidator(); 
   
    // TODO -- make the hashvalue for Outlink...
    Map<String,Link> filtered = new LinkedHashMap<String,Link>();
    for( Task outTask : parse.getNewTasks() ) {
      // only use Links, so if for some reason it isn't a Link, skip
      if( !(outTask instanceof Link)) {
        continue;
      }
      Link outlink = (Link)outTask;
      String id = outlink.getId();
      if (filters.accept(id) && !filtered.containsKey(id)) {
    	if( linkValidator == null ){
    	  filtered.put(id,outlink);
    	}
    	else if( linkValidator.validate( outlink ) ){
    	  filtered.put(id,outlink);
    	}
      }
    }
    return filtered.values();
  }
  
  public HandlerFactory getHandlerFactory() {
    return handlerFactory;
  }

  public void setHandlerFactory(HandlerFactory handlerFactory) {
    this.handlerFactory = handlerFactory;
  }
}

