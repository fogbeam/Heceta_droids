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
package org.apache.droids.protocol.http;

import java.io.IOException;
import java.net.URI;

import org.apache.droids.api.AdvancedManagedContentEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;

public class AdvancedHttpProtocol extends HttpProtocol {

  public AdvancedHttpProtocol(HttpClient httpclient) {
    super(httpclient);
  }
  
  public AdvancedHttpProtocol() {
    super();
  }

  @Override
  public AdvancedManagedContentEntity load(URI uri) throws IOException {
    HttpGet httpget = new HttpGet(uri);
    HttpResponse response = getHttpClient().execute(httpget);
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
    long maxlen = getHttpClient().getParams().getLongParameter(DroidsHttpClient.MAX_BODY_LENGTH, 0);
    return new AdvancedHttpContentEntity(entity,response.getAllHeaders(),maxlen);
  }
}

  

