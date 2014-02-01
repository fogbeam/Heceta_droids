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
import java.io.InputStream;

/**
 * Abstract interface representing a body of content with a particular 
 * MIME type and an optional charset.
 * <p>
 * IMPORTANT: implementations of this interface MUST ensure that the content
 * is repeatable, that is, the content can be consumed more than once. 
 * <p>
 * IMPORTANT: The consumer of the entity content MUST close the input stream 
 * returned by {@link #obtainContent()} when finished reading the content. 
 * 
 * @version 1.0
 */
public interface ContentEntity {

  /**
   * Returns content of the entity as an input stream. This input stream
   * MUST be closed by the consumer when finished reading content.
   * <p/>
   * IMPORTANT: This method MUST return a new instance of {@link InputStream}
   * to ensure the content can be consumed more than once.
   *  
   * @return input stream
   * @throws IOException
   */
  InputStream obtainContent() throws IOException;
  
  /**
   * Returns MIME type of the entity.
   * 
   * @return MIME type
   */
  String getMimeType();
  
  /**
   * Returns charset of the entity if known. Otherwise returns 
   * <code>null</code>.
   * 
   * @return charset
   */
  String getCharset();

  /**
   * Returns the parse object from a former processing step
   * May be <code>null</code> if not available.
   * 
   * @return parse object
   */
  Parse getParse();
  
}