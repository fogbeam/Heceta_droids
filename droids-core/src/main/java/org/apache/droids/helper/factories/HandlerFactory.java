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

import java.io.IOException;
import java.net.URI;

import org.apache.droids.api.ContentEntity;
import org.apache.droids.api.Handler;
import org.apache.droids.exception.DroidsException;

/**
 * Factory that will traverse all registered handler and execute them.
 * 
 * @version 1.0
 * 
 */
public class HandlerFactory extends GenericFactory<Handler> {

  /**
   * Will traverse all registered handler and execute them. If we encounter a
   * problem we directly return false and leave.
   * 
   * @param stream
   *                the underlying stream
   * @param url
   *                the underlying url
   * @param parse
   *                the underlying parse object
   * @return false if we found a problem, true if all went well
   */
  public boolean handle(URI uri, ContentEntity entity) 
      throws DroidsException, IOException {
    for (Handler handler : getMap().values()) {
      handler.handle(uri, entity);
    }
    return true;
  }

}
