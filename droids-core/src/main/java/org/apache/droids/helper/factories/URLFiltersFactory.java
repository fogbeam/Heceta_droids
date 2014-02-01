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
package org.apache.droids.helper.factories;

import org.apache.droids.api.URLFilter;

/**
 * Factory that will traverse all registered filter and execute them.
 * 
 * @version 1.0
 * 
 */
public class URLFiltersFactory extends GenericFactory<URLFilter> {

  /**
   * Run all defined filters. Assume logical AND.
   * 
   * @param urlString -
   *                url to test
   * @return true if filter plugin accept the url, false if excluded.
   */
  public boolean accept(String urlString) {
  	if (urlString == null) {
  		return false;
  	}
  		
    for (String key : getMap().keySet()) {
      if(!accept(urlString, key)) {
      	return false;
      }
    }
    return true;
  }

  /**
   * Run a specific filter class.
   * 
   * @param urlString -
   *                url to test
   * @param filterName -
   *                name of the specific filter class.
   * @return true if filter plugin accept the url, false if excluded.
   */
  public boolean accept(String urlString, String filterName) {
    if (urlString == null ||
    		doFilter(urlString, filterName) == null) {
      return false;
    }
    return true;
  }

  /**
   * Check string against filters list
   * 
   * @param urlString -
   *                url to test
   * @param filterName -
   *                name of the specific filter class.
   * @return the URL if it's allowed, NULL otherwise
   */
	protected String doFilter(String urlString, String filterName) {
	  return getMap().get(filterName).filter(urlString);
  }
}
