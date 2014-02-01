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

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * 
 * A TaskMaster is responsible for running all the tasks.
 * 
 */
public interface TaskMaster<T extends Task>
{
  /**
   * Possible execution states for the TaskMaster.
   */
  public enum ExecutionState
  {
    INITIALIZED, RUNNING, STOPPED, COMPLETED
  };

  /**
   * Start the process of running tasks.
   * 
   * @param droid
   */
  void start(final Queue<T> queue, final Droid<T> droid);

  /**
   * Blocks until all tasks have completed execution.
   *
   * @param timeout
   * @param unit
   * @return
   * @throws InterruptedException
   */
  boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

  /**
   * Set the exception handler
   * 
   * @param taskExceptionHandler
   */
  void setExceptionHandler(TaskExceptionHandler taskExceptionHandler);

  /**
   * Set a delay timer.
   * 
   * @param simpleDelayTimer
   */
  void setDelayTimer(DelayTimer simpleDelayTimer);

  /**
   * Returns the current state.
   *
   * @return
   */
  ExecutionState getExecutionState();

  /**
   * Get the Date the TaskMaster started to work.
   * 
   * @return the start date
   */
  Date getStartTime();

  /**
   * Get the Date the TaskMaster finished.
   * 
   * @return the work-finished date
   */
  Date getFinishedWorking();

  /**
   * Get the number of completed tasks.
   * 
   * @return number of completed tasks
   */
  long getCompletedTasks();

  /**
   * Get the last completed task.
   * 
   * @return the last task
   */
  T getLastCompletedTask();
}
