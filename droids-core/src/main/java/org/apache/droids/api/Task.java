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

import java.io.Serializable;
import java.util.Date;

/**
 * A task is a working instruction for a droid. One can limit the depth of the
 * task. That is based on the fact that a droid can extract more tasks from the
 * one that it is currently working on. However sometimes one want to limit the
 * number of nested task, this is what is determined by the depth.
 * 
 * @version 1.0
 * 
 */
public interface Task extends Serializable {
  /**
   * The id of the task. In a standard crawl that is most likely the url that
   * identifies the task
   * 
   * @return The id of the task
   */
  String getId();
  
  /**
   * 
   * @return The depth of the task
   */
  int getDepth();

  /**
   * When was the task created
   * 
   * @return the date when the task was created.
   */
  Date getTaskDate();
  
  public void abort();
  
  public boolean isAborted();
}
