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
package org.apache.droids.impl;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.apache.droids.api.DelayTimer;
import org.apache.droids.api.Droid;
import org.apache.droids.api.Task;
import org.apache.droids.api.TaskExceptionHandler;
import org.apache.droids.api.TaskExceptionResult;
import org.apache.droids.api.TaskMaster;
import org.apache.droids.api.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequentialTaskMaster<T extends Task> implements TaskMaster<T>
{

  private static final Logger LOG = LoggerFactory.getLogger(SequentialTaskMaster.class);
  private final Object mutex;
  private volatile boolean completed;
  private volatile Date startedWorking = null;
  private volatile Date finishedWorking = null;
  private volatile int completedTask = 0;
  private volatile T lastCompletedTask = null;
  private volatile ExecutionState state = ExecutionState.INITIALIZED;
  private DelayTimer delayTimer = null;
  private TaskExceptionHandler exHandler = null;

  public SequentialTaskMaster() {
    super();
    this.mutex = new Object();
  }

  /**
   * The queue has been initialized
   */
  @Override
  public synchronized void start(final Queue<T> queue, final Droid<T> droid) {
    this.completed = false;
    this.startedWorking = new Date();
    this.finishedWorking = null;
    this.completedTask = 0;
    this.state = ExecutionState.RUNNING;

    boolean terminated = false;
    while (!terminated) {
      T task = queue.poll();
      if (task == null) {
        break;
      }
      if (delayTimer != null) {
        long delay = delayTimer.getDelayMillis();
        if (delay > 0) {
          try {
            Thread.sleep(delay);
          } catch (InterruptedException e) {
          }
        }
      }
      Worker<T> worker = droid.getNewWorker();
      try {
        if (!task.isAborted()) {
          worker.execute(task);
        }
        completedTask++;
        lastCompletedTask = task;
      } catch (Exception ex) {
        TaskExceptionResult result = TaskExceptionResult.WARN;
        if (exHandler != null) {
          result = exHandler.handleException(ex);
        }
        switch (result) {
          case WARN:
        	LOG.warn(ex.toString() + " " + task.getId());
            if (LOG.isDebugEnabled()) {
            	LOG.debug(ex.toString(), ex);
            }
            break;
          case FATAL:
            LOG.error(ex.getMessage(), ex);
            terminated = true;
            break;
        }
      }
    }
    finishedWorking = new Date();
    this.state = ExecutionState.STOPPED;
    droid.finished();
    synchronized (mutex) {
      completed = true;
      mutex.notifyAll();
    }
  }

  @Override
  public final void setExceptionHandler(TaskExceptionHandler exHandler) {
    this.exHandler = exHandler;
  }

  @Override
  public final void setDelayTimer(DelayTimer delayTimer) {
    this.delayTimer = delayTimer;
  }

  public boolean isWorking() {
    return startedWorking != null && finishedWorking == null;
  }

  @Override
  public Date getStartTime() {
    return startedWorking;
  }

  @Override
  public Date getFinishedWorking() {
    return finishedWorking;
  }

  @Override
  public long getCompletedTasks() {
    return completedTask;
  }

  @Override
  public T getLastCompletedTask() {
    return lastCompletedTask;
  }

  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    if (timeout < 0) {
      timeout = 0;
    }
    synchronized (this.mutex) {
      long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
      long remaining = timeout;
      while (!completed) {
        this.mutex.wait(remaining);
        if (timeout >= 0) {
          remaining = deadline - System.currentTimeMillis();
          if (remaining <= 0) {
            return false; // Reach if timeout is over and no finish.
          }
        }
      }
    }
    return true;
  }

  @Override
  public ExecutionState getExecutionState() {
    return state;
  }
}
