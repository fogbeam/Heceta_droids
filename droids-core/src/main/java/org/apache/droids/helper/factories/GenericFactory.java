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

import java.util.HashMap;
import java.util.Map;

/**
 * Basically all factories till now extend this generic factory. The core is a
 * simple Map.
 * 
 * @version 1.0
 * 
 */
public class GenericFactory<T> {

  private Map<String,T> map = null;

  public GenericFactory() {
    map = new HashMap<String,T>();
  }
  
  /**
   * Get the register which contains all components.
   * 
   * @return the register which contains all components
   */
  public Map<String,T> getMap() {
    return map;
  }

  /**
   * Set the register which contains all components.
   * 
   * @param map
   *                the register which contains all components.
   */
  @SuppressWarnings("unchecked")
  @Deprecated
  public void setMap(Map map) {
    this.map = map;
  }

  /**
   * Will lookup which component is linked to the name and will return it.
   * 
   * @param name
   *                -the name of the component you need.
   * @return plugin to process the job.
   */
  public T resolve(String name) {
    return map.get(name);
  }

}
