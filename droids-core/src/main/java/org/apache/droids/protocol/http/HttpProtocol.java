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
package org.apache.droids.protocol.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.droids.api.ManagedContentEntity;
import org.apache.droids.api.Protocol;
import org.apache.droids.norobots.ContentLoader;
import org.apache.droids.norobots.NoRobotClient;
import org.apache.droids.norobots.NoRobotException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.CoreProtocolPNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Protocol handler based on HttpClient 4.0.
 */
public class HttpProtocol implements Protocol {

  private static final Logger LOG = LoggerFactory.getLogger(HttpProtocol.class);

  private final HttpClient httpclient;
  private final ContentLoader contentLoader;
  
  private boolean forceAllow = false;
  private String userAgent = "Apache-Droids/1.1 (java 1.5)";

  public HttpProtocol(final HttpClient httpclient) {
    super();
    this.httpclient = httpclient;
    this.httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
    this.contentLoader = new HttpClientContentLoader(httpclient);
  }
  
  public HttpProtocol() {
    this(new DroidsHttpClient());
  }

  @Override
  public ManagedContentEntity load(URI uri) throws IOException {
    HttpGet httpget = new HttpGet(uri);
    HttpResponse response = httpclient.execute(httpget);
    StatusLine statusline = response.getStatusLine();
    if (statusline.getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {
      httpget.abort();
      throw new HttpResponseException(
          statusline.getStatusCode(), statusline.getReasonPhrase());
    }
    HttpEntity entity = response.getEntity();
    if (entity == null) {
      // Should _almost_ never happen with HTTP GET requests.
      throw new ClientProtocolException("Empty entity");
    }
    long maxlen = httpclient.getParams().getLongParameter(DroidsHttpClient.MAX_BODY_LENGTH, 0);
    return new HttpContentEntity(entity, maxlen);
  }

  @Override
  public boolean isAllowed(URI uri) throws IOException {
    if (forceAllow) {
      return forceAllow;
    }

    URI baseURI;
    try {
      baseURI = new URI(
          uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), 
          "/", null, null);
    } catch (URISyntaxException ex) {
      LOG.error("Unable to determine base URI for " + uri);
      return false;
    }
    
    NoRobotClient nrc = new NoRobotClient(contentLoader, userAgent);
    try {
      nrc.parse(baseURI);
    } catch (NoRobotException ex) {
      LOG.error("Failure parsing robots.txt: " + ex.getMessage());
      return false;
    }
    boolean test = nrc.isUrlAllowed(uri);
    if (LOG.isInfoEnabled()) {
      LOG.info(uri + " is " + (test ? "allowed" : "denied"));
    }
    return test;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
    this.httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
  }

  /**
   * You can force that a site is allowed (ignoring the robots.txt). This should
   * only be used on server that you control and where you have the permission
   * to ignore the robots.txt.
   * 
   * @return <code>true</code> if you are rude and ignore robots.txt.
   *         <code>false</code> if you are playing nice.
   */
  public boolean isForceAllow() {
    return forceAllow;
  }

  /**
   * You can force that a site is allowed (ignoring the robot.txt). This should
   * only be used on server that you control and where you have the permission
   * to ignore the robots.txt.
   * 
   * @param forceAllow
   *                if you want to force an allow and ignore the robot.txt set
   *                to <code>true</code>. If you want to obey the rules and
   *                be polite set to <code>false</code>.
   */
  public void setForceAllow(boolean forceAllow) {
    this.forceAllow = forceAllow;
  }
  
  protected HttpClient getHttpClient() {
    return this.httpclient;
  }

}
