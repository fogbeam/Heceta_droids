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
package org.apache.droids;

import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.io.Serializable;

import org.apache.droids.api.Link;

/**
 * 
 * Basic implementation for @Link.
 * LinkTasks are working instructions for URI based droids.
 * 
 */
public class LinkTask implements Link, Serializable {
  private static final long serialVersionUID = -44808094386453088L;

  private Date started;
  private final int depth;
  private final URI uri;
  private final Link from;

  private Date lastModifedDate;
  private Collection<URI> linksTo;
  private String anchorText;
  private int weight;
  private boolean aborted = false;

  /**
   * Creates a new LinkTask.
   * 
   * @param from
   * @param uri
   * @param depth
   */
  public LinkTask(Link from, URI uri, int depth) {
    this.from = from;
    this.uri = uri;
    this.depth = depth;
    this.started = new Date();
  }

  /**
   * Creates a new LinkTask.
   * 
   * @param from
   * @param uri
   * @param depth
   * @param weight
   */
  public LinkTask(Link from, URI uri, int depth, int weight) {
    this.from = from;
    this.uri = uri;
    this.depth = depth;
    this.started = new Date();
    this.weight = weight;
  }
  
  /**
   * Creates a new LinkTask
   * 
   * @param from
   * @param uri
   * @param depth
   * @param anchorText
   */
  public LinkTask(Link from, URI uri, int depth, String anchorText) {
    this(from, uri, depth);
    this.anchorText = anchorText;
  }

  @Override
  public String getId() {
    return uri.toString();
  }

  @Override
  public Date getTaskDate() {
    return started;
  }

  /**
   * Set the Date the task started.
   * 
   * @param started
   */
  public void setTaskDate(Date started) {
    this.started = started;
  }

  @Override
  public int getDepth() {
    return depth;
  }

  @Override
  public Link getFrom() {
    return from;
  }

  @Override
  public Collection<URI> getTo() {
    return linksTo;
  }

  @Override
  public Date getLastModifiedDate() {
    return lastModifedDate;
  }

  /**
   * Set the Date the Task object was last modified.
   * 
   * @param lastModifedDate
   */
  public void setLastModifedDate(Date lastModifedDate) {
    this.lastModifedDate = lastModifedDate;
  }

  /**
   * Set Outgoing links.
   * 
   * @param linksTo
   */
  public void setLinksTo(Collection<URI> linksTo) {
    this.linksTo = linksTo;
  }

  @Override
  public URI getURI() {
    return uri;
  }

  @Override
  public String getAnchorText() {
    return anchorText;
  }

  /**
   * Set the anchor text for this link.
   * 
   * @param anchorText
   */
  public void setAnchorText(String anchorText) {
    this.anchorText = anchorText;
  }

  /**
   * Get the weight of the link
   * 
   * @return the links weight
   */
  public int getWeight() {
    return weight;
  }

  /**
   * Set the weight of the link.
   * 
   * @param weight
   */
  public void setWeight(int weight) {
    this.weight = weight;
  }

  @Override
  public void abort() {
    aborted = true;
  }

  @Override
  public boolean isAborted() {
    return aborted;
  }

}