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
 * Monitor the execution of Tasks.
 *
 * @param <T>
 */
public interface WorkMonitor<T extends Task> {
  
  /**
   * Monitor the task before the execution.
   * 
   * @param task
   * @param worker
   */
  void beforeExecute( final T task, final Worker<T> worker );
  
  /**
   * Monitor the task after the execution.
   * 
   * @param task
   * @param worker
   * @param ex
   */
  void afterExecute( final T task, final Worker<T> worker, Exception ex );
}
