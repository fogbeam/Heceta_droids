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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.droids.api.AdvancedManagedContentEntity;
import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class AdvancedHttpContentEntity extends HttpContentEntity implements
    AdvancedManagedContentEntity {

  private Map<String,String> metadata = new HashMap<String,String>();
  private long contentLength;
  
  public AdvancedHttpContentEntity(HttpEntity entity, long maxlen) throws IOException {
    super(entity, maxlen);
  }

  public AdvancedHttpContentEntity(HttpEntity entity, Header[] allHeaders, long maxlen) throws IOException {
    super(entity, maxlen);
    for(Header h : allHeaders) {
      metadata.put(h.getName(), h.getValue());
    }
    contentLength = entity.getContentLength();
  }

  @Override
  public boolean containsMetadataKey(String key) {
    return metadata.containsKey(key);
  }

  @Override
  public String getValue(String key) {
    return metadata.get(key);
  }

  @Override
  public Set<String> metadataKeySet() {
    return metadata.keySet();
  }

  @Override
  public long getContentLength() {
    return this.contentLength;
  }

}
