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

/**
 * ParserNotFoundException gives a detailed exception for problems that can
 * occur while parsing a task.
 * 
 * @version 1.0
 * 
 */
public class ParserNotFoundException extends DroidsException {
  private static final long serialVersionUID = -7058147461702832283L;
  private String url = null;
  private String contentType = null;

  /**
   * Create an exception for the given url and content type
   * 
   * @param url
   *                url which produced the error
   * @param contentType
   *                content type of the url
   */
  public ParserNotFoundException(String url, String contentType) {
    this(url, contentType, "parser not found for contentType=" + contentType
        + " url=" + url);
  }

  /**
   * Create an exception for the given url and content type
   * 
   * @param url
   *                url which produced the error
   * @param contentType
   *                content type of the url
   * @param message
   *                a detailed message to state what has gone wrong
   */
  public ParserNotFoundException(String url, String contentType, String message) {
    super(message);
    this.url = url;
    this.contentType = contentType;
  }

  /**
   * Constructs a new exception with the specified detail message. The cause is
   * not initialized, and may subsequently be initialized by a call to
   * {@link #initCause}.
   * 
   * @param message
   *                the detail message. The detail message is saved for later
   *                retrieval by the {@link #getMessage()} method.
   */
  public ParserNotFoundException(String message) {
    super(message);
  }

  /**
   * If not constructed via message only it will return the url which has caused
   * the problem
   * 
   * @return url which has caused the problem
   */
  public String getUrl() {
    return url;
  }

  /**
   * If not constructed via message only it will return the content typee which
   * has caused the problem
   * 
   * @return content typee which has caused the problem
   */
  public String getContentType() {
    return contentType;
  }
}
