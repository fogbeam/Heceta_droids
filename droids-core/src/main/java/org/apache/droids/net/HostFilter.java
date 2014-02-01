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
package org.apache.droids.net;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.apache.droids.api.URLFilter;

/**
 * Simple hostname based implementation of {@link URLFilter).
 * 
 * @version 1.0
 */
public class HostFilter implements URLFilter {

  private Set<String> allowedHosts;

  public HostFilter(final Set<String> allowedHosts) {
    super();
    this.allowedHosts = new HashSet<String>();
    if (allowedHosts != null) {
      this.allowedHosts.addAll(allowedHosts);
    }
  }
  
  public HostFilter(final String allowedHost) {
    super();
    this.allowedHosts = new HashSet<String>();
    if (allowedHost != null) {
      this.allowedHosts.add(allowedHost);
    }
  }

  @Override
  public String filter(final String url) {
    try {
      URI uri = new URI(url);
      if (this.allowedHosts.contains(uri.getHost())) {
        return url;
      } else {
        return null;
      }
    } catch (URISyntaxException ex) {
      return null;
    }
  }

}
