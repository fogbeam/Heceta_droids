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
package org.apache.droids.exception;

import java.net.URI;

/**
 * If we do not have any instance of a protocol registered for the given url.
 * 
 * @version 1.0
 * 
 */
public class ProtocolNotFoundException extends DroidsException {
  private static final long serialVersionUID = 6980937469875896426L;
  private URI uri = null;

  /**
   * Create an exception for the given url
   * 
   * @param url
   *                url where we do not have a suitable protocol
   */
  public ProtocolNotFoundException(URI uri) {
    this(uri, "protocol not found for uri=" + uri);
  }

  /**
   * Create an exception for the given url and detailed message
   * 
   * @param url
   *                url where we do not have a suitable protocol
   * @param message
   *                detailed message to explain the underlying cause
   */
  public ProtocolNotFoundException(URI uri, String message) {
    super(message);
    this.uri = uri;
  }

  /**
   * Will return the url which has caused the problem
   * 
   * @return url which has caused the problem
   */
  public URI getUri() {
    return uri;
  }
}
