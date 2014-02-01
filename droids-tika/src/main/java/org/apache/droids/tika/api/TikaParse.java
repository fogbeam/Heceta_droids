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
package org.apache.droids.tika.api;

import org.apache.droids.api.Parse;
import org.apache.tika.metadata.Metadata;

public interface TikaParse extends Parse {

	/**
	 * Retrieves the main content of the parsed document.
	 * Uses Tika's plugin in for Boilerpipe.
	 * @return plain text result with boilerplate removed
	 */
  public String getMainContent();
  
  /**
   * Extracted meta data from the document. This can include
   * meta tags from within an HTML document
   * @return metadata object from the parse
   */
  public Metadata getMetadata();
  
  /**
   * The HTML representation of the document.
   * @return The HTML representation of the document.
   */
  public String getXml();
  
  /**
   * Plain text representation of the document.
   * @return plain text version without formatting
   */
  public String getPlainText();
  
  /**
   * If the document should be indexed or not.
   * This can be determined from metadata or other methods
   * @return false if the document shouldn't be indexed, true otherwise
   */
  public boolean isIndexed();
}
