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

import org.apache.droids.api.Droid;
import org.apache.droids.api.Task;

/**
 * Factory that will lookup a droid by its name and returns it.
 * 
 * @version 1.0
 * 
 */
public class DroidFactory<T extends Task> extends GenericFactory<Droid<T>> {

  /**
   * Lookup a droid by its name and return it.
   * 
   * @param name
   *                the droid we want to use
   * @return the droid registered for the given name
   */
  public Droid<T> getDroid(String name) {
    return getMap().get(name);
  }

}
