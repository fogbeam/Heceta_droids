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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import com.google.common.base.Preconditions;
import java.util.Queue;

import org.apache.droids.AbstractDroid;
import org.apache.droids.LinkTask;
import org.apache.droids.api.Link;
import org.apache.droids.api.TaskMaster;
import org.apache.droids.api.TaskValidator;
import org.apache.droids.api.Worker;
import org.apache.droids.exception.InvalidTaskException;
import org.apache.droids.helper.factories.ParserFactory;
import org.apache.droids.helper.factories.ProtocolFactory;
import org.apache.droids.helper.factories.URLFiltersFactory;

public abstract class CrawlingDroid extends AbstractDroid<Link>
{

  private Collection<String> initialLocations;
  ProtocolFactory protocolFactory;
  ParserFactory parserFactory;
  URLFiltersFactory filtersFactory;
  private TaskValidator<Link> linkValidator; 

  public CrawlingDroid(Queue<Link> queue, TaskMaster<Link> taskMaster)
  {
    super(queue, taskMaster);
  }

  public void setInitialLocations(Collection<String> initialLocations)
  {
    this.initialLocations = initialLocations;
  }

  @Override
  public void init() throws InvalidTaskException
  {
    Preconditions.checkState(initialLocations != null, "WebCrawlerDroid requires at least one starting file");
    Preconditions.checkState(!initialLocations.isEmpty(), "WebCrawlerDroid requires at least one starting file");
    for (String location : initialLocations) {
      URI uri;
      try {
        uri = new URI(location);
      } catch (URISyntaxException ex) {
        throw new InvalidTaskException("Invalid lication: " + location);
      }
      queue.offer(new LinkTask(null, uri, 0));
    }
  }

  public void start()
  {
    taskMaster.start(queue, this);
  }

  @Override
  public void finished()
  {
    log.info("FINISHED!!!");
  }

  public abstract Worker<Link> getNewWorker();

  public ProtocolFactory getProtocolFactory()
  {
    return protocolFactory;
  }

  public void setProtocolFactory(ProtocolFactory protocolFactory)
  {
    this.protocolFactory = protocolFactory;
  }

  public ParserFactory getParserFactory()
  {
    return parserFactory;
  }

  public void setParserFactory(ParserFactory parserFactory)
  {
    this.parserFactory = parserFactory;
  }

  public URLFiltersFactory getFiltersFactory()
  {
    return filtersFactory;
  }

  public void setFiltersFactory(URLFiltersFactory filtersFactory)
  {
    this.filtersFactory = filtersFactory;
  }
  
  public void setLinkValidator(TaskValidator<Link> linkValidator)
  {
    this.linkValidator = linkValidator;
  }
  
  public TaskValidator<Link> getLinkValidator()
  {
	return linkValidator;
  }
  
}
