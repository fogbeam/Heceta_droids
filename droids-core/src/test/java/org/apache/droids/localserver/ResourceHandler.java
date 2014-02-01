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
package org.apache.droids.localserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

/**
 * A handler that serves out a resource
 */
public class ResourceHandler implements HttpRequestHandler
{

  public void handle(final HttpRequest request, final HttpResponse response,
      final HttpContext context) throws HttpException, IOException {

    String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
    if (!"GET".equals(method) && !"HEAD".equals(method)) {
      throw new MethodNotSupportedException(method + " not supported by " + getClass().getName());
    }
    String requestURI = request.getRequestLine().getUri();
    String s = requestURI; 
    if (!s.startsWith("/")) {
      s = "/" + s;
    }
    s = "resources" + s;
    
    ClassLoader cl = ResourceHandler.class.getClassLoader();
    URL resource = cl.getResource(s);

    if (resource != null) { 
      InputStream instream = resource.openStream();
      InputStreamEntity entity = new InputStreamEntity(instream, -1);
      if (requestURI.endsWith("_html")) {
        entity.setContentType("text/html");
        entity.setChunked(true);
      }
      response.setEntity(entity);
      
    } else {
      response.setStatusCode(HttpStatus.SC_NOT_FOUND);
      StringEntity entity = new StringEntity(requestURI + " not found", "US-ASCII");
      entity.setContentType("text/html");
      response.setEntity(entity);
    }
  }

}
