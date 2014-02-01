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
package org.apache.droids.impl;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import junit.framework.Assert;
import org.apache.droids.LinkTask;
import org.apache.droids.api.Link;
import org.junit.Test;

public class TestSimpleTaskQueueWithHistory
{
  @Test
  public void testOffer() throws Exception
  {
    Queue<LinkTask> queue;
    URI	uri;
    LinkTask task;

    queue = new SimpleTaskQueueWithHistory<LinkTask>();
    Assert.assertEquals(0, queue.size());
    uri = new URI("http://www.example.com");
    Assert.assertNotNull(uri);
    task = new LinkTask(null, uri, 1);
    Assert.assertNotNull(task);
    queue.offer(task);
    Assert.assertEquals(1, queue.size());
    queue.offer(task);
    Assert.assertEquals(1, queue.size());
    queue.poll();
    Assert.assertEquals(0, queue.size());
    queue.offer(task);
    Assert.assertEquals(0, queue.size());
  }
  
  @Test
  public void testAddAll() throws URISyntaxException 
  {
	  Collection<Link> links = new LinkedList<Link>();
	  links.add(new LinkTask(null, new URI("http://www.example.com"), 0));
	  links.add(new LinkTask(null, new URI("http://www.example.com/1"), 1));
	  links.add(new LinkTask(null, new URI("http://www.example.com/2"), 1));
	  links.add(new LinkTask(null, new URI("http://www.example.com/3"), 1));
	  links.add(new LinkTask(null, new URI("http://www.example.com/4"), 1));
	  
	  Queue<Link> queue = new SimpleTaskQueueWithHistory<Link>();
	  assertEquals(0, queue.size());
	  queue.addAll(links);
	  assertEquals(5, queue.size());
	  
	  links.add(new LinkTask(null, new URI("http://www.example.com/1"), 1));
	  links.add(new LinkTask(null, new URI("http://www.example.com/5"), 1));
	  links.add(new LinkTask(null, new URI("http://www.example.com/2"), 1));

	  queue.addAll(links);
	  assertEquals(6, queue.size());
	  
	  queue.poll();

	  assertEquals(5, queue.size());

	  queue.addAll(links);
	  assertEquals(5, queue.size());

  }
}
