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

import java.util.Collections;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.protocol.HttpContext;

class NoAuthHandler implements AuthenticationHandler
{

  @Override
  public Map<String, Header> getChallenges(HttpResponse response, HttpContext context)
      throws MalformedChallengeException
  {
    return Collections.emptyMap();
  }

  @Override
  public boolean isAuthenticationRequested(HttpResponse response, HttpContext context)
  {
    return false;
  }

  @Override
  public AuthScheme selectScheme(Map<String, Header> challenges, HttpResponse response, HttpContext context)
      throws AuthenticationException
  {
    throw new AuthenticationException(
        "Unable to respond to any of these challenges: " + challenges);
  }
  
}
