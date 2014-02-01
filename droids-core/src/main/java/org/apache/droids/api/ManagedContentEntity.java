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

/**
 * Abstract interface representing a body of content managed by a {@link Droid}.
 * <p>
 * Droid MUST call {@link #finish()} when the entity is no longer 
 * needed in order to release underlying resources held by the entity. 
 * 
 * @version 1.0
 */
public interface ManagedContentEntity extends ContentEntity {

  /**
   * Sets the parse object.
   * 
   * @param
   */
  void setParse(Parse parse);
  
  /**
   * Releases all underlying resources held by the entity.
   */
  void finish();
  
}