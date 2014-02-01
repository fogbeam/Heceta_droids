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

import java.util.Queue;
import org.apache.droids.exception.DroidsException;

/**
 * Interface for a droid. Droid can be seen as a "project manger" that delegates
 * the work to {@link Worker} units.
 * <p>
 * Droids aims to be an intelligent standalone robot framework that allows to
 * create and extend existing droids (robots). In the future it will offer an
 * administration application to manage and controll the different droids.
 * 
 * @version 1.0
 */
public interface Droid<T extends Task>
{

  /**
   * Initialize the queue. Can have different implementation but the main groups
   * normally are
   * <ol>
   * <li>add only one url, from which we then start crawling</li>
   * <li>add an array of start urls and then crawl them</li>
   * <li>add an array of urls as fixed subset (no further crawling done)</li>
   * </ol>
   * @throws DroidsException
   */
  void init() throws DroidsException;

  /**
   * Invoke an instance of the worker used in the droid
   */
  void start();

  /**
   * Invoke when the droid has completed
   */
  void finished();

  /**
   * Return the tasks queue
   *
   * @return
   */
  public Queue<T> getQueue();

  /**
   * Ask the droid for a new worker
   */
  Worker<T> getNewWorker();

  /**
   * Get the task master
   */
  TaskMaster<T> getTaskMaster();
}
