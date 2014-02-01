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
package org.apache.droids.api;

import java.util.Set;

/**
 * Handler that provides metadata information.
 *
 */
public interface AdvancedManagedContentEntity extends ManagedContentEntity {

  /**
   * Get metadata value
   * @param key value to get
   * @return value, null if not found
   */
  public String getValue(String key);
  
  /**
   * Retrieve set of valid keys.
   * @return set of valid metadata keys
   */
  public Set<String> metadataKeySet();
  
  /**
   * Checks for existence of metadata key
   * @param key key to test
   * @return true if key exists
   */
  public boolean containsMetadataKey(String key);
  
  /**
   * Returns the content length if known
   * @return number of bytes, negative if unknown
   */
  public long getContentLength();
}
