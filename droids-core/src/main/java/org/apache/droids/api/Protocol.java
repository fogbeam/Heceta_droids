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
package org.apache.droids.api;

import java.io.IOException;
import java.net.URI;

/**
 * The protocol interface is a wrapper to hide the underlying implementation of
 * the communication at protocol level.
 * 
 * @version 1.0
 * 
 */
public interface Protocol {
  /**
   * Some protocols (like http) offer a mechanism to evaluate whether the client
   * can request a given url (in http this is the robots.txt configuration)
   * 
   * @param url
   *                the url to evaluate
   * @return true if we can request the url. false if we are forbidden.
   * @throws MalformedURLException
   */
  boolean isAllowed(URI url) throws IOException;

  /**
   * Return the content entity represent of the url
   * 
   * @param url
   *                url of the stream we want to open
   * @return the content of the given url
   * @throws IOException
   */
  ManagedContentEntity load(URI uri) throws IOException;

}