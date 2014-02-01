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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.droids.api.DelayTimer;
import org.apache.droids.api.Droid;
import org.apache.droids.api.Task;
import org.apache.droids.api.TaskExceptionHandler;
import org.apache.droids.api.TaskExceptionResult;
import org.apache.droids.api.TaskMaster;
import org.apache.droids.api.WorkMonitor;
import org.apache.droids.api.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for running all the tasks
 */
public class MultiThreadedTaskMaster<T extends Task> implements TaskMaster<T>
{

  protected static final Logger LOG = LoggerFactory.getLogger(MultiThreadedTaskMaster.class);
  private static final long TICKLE_TIME = 1000L;
  
  /**
   * The execution state
   */
  protected volatile ExecutionState state = ExecutionState.STOPPED;
  /**
   * The delay timer
   */
  protected DelayTimer delayTimer;
  /**
   * The start time
   */
  protected Date startTime;
  /**
   * The end time
   */
  protected Date endTime;
  /**
   * The last completed task
   */
  protected T lastCompletedTask;
  /**
   * The completed task counter
   */
  protected AtomicLong completedTasks = new AtomicLong();
  /**
   * The monitor that that records the processing of tasks
   */
  protected WorkMonitor<T> monitor;
  /**
   * The task exception handler
   */
  protected TaskExceptionHandler exceptionHandler;

  /*
   * The pool size
   */
  private int poolSize = 1;
  /**
   * The pool
   */
  private TaskExecutorPool pool;

  @Override
  public void start(Queue<T> queue, Droid<T> droid)
  {
    if (LOG.isInfoEnabled()) {
      LOG.info("Start the executor service.");
    }

    state = ExecutionState.RUNNING;

    if (pool == null) {
      this.pool = new TaskExecutorPool();
      this.pool.setCorePoolSize(this.poolSize);
    }

    // Stagger startup
    for (int i = 0; i < poolSize; i++) {
      try {
        Thread.sleep(TICKLE_TIME);
      } catch(InterruptedException ignored) {
        LOG.error("", ignored);
      }
      pool.execute(new TaskExecutor(droid));
    }
  }

  /**
   * Stops the TaskMaster
   */
  public void stop()
  {
    // debug
    if (LOG.isInfoEnabled()) {
      LOG.info("Stop the executor service.");
    }

    state = ExecutionState.STOPPED;

    // Disable new tasks from being submitted
    pool.shutdown();

    // Wait a while for existing tasks to terminate
    try {
      if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {

        // Cancel currently executing tasks
        pool.shutdownNow();

        // Wait a while for to respond to being canceled
        if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
          if (LOG.isInfoEnabled()) {
            LOG.info("Scheduler did not stop.");
          }
        }
      }
    } catch (InterruptedException ex) {

      if (LOG.isInfoEnabled()) {
        LOG.info("Force scheduler to stop.");
      }

      // (Re-)Cancel if current thread also interrupted
      pool.shutdownNow();

      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }

    // debug
    if (LOG.isInfoEnabled()) {
      LOG.info("Scheduler stopped.");
    }

  }

  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
  {
    return pool.awaitTermination(timeout, unit);
  }

  /**
   * @inheritDoc
   */
  @Override
  public ExecutionState getExecutionState()
  {
    return state;
  }

  /**
   * @return
   * @inheritDoc
   */
  public WorkMonitor<T> getMonitor()
  {
    return monitor;
  }

  /**
   * @param monitor
   * @inheritDoc
   */
  public void setMonitor(WorkMonitor<T> monitor)
  {
    if (state == ExecutionState.RUNNING) {
      throw new IllegalStateException("The TaskMaster must be stopped to set a Monitor.");
    }
    this.monitor = monitor;
  }

  @Override
  public void setExceptionHandler(TaskExceptionHandler exceptionHandler)
  {
    this.exceptionHandler = exceptionHandler;
  }

  @Override
  public void setDelayTimer(DelayTimer delayTimer)
  {
    this.delayTimer = delayTimer;
  }

  @Override
  public Date getFinishedWorking()
  {
    return endTime;
  }

  @Override
  public T getLastCompletedTask()
  {
    return lastCompletedTask;
  }

  @Override
  public long getCompletedTasks()
  {
    return completedTasks.get();
  }

  @Override
  public Date getStartTime()
  {
    return startTime;
  }

  /**
   * Sets the pool size
   *
   * @return
   */
  public int getPoolSize()
  {
    return poolSize;
  }

  /**
   * Returns the size of the pool
   *
   * @param poolSize
   */
  public void setPoolSize(int poolSize)
  {
    this.poolSize = poolSize;
    if(pool != null) 
      pool.setCorePoolSize(this.poolSize);
  }

  private class TaskExecutorPool extends ThreadPoolExecutor
  {

    private static final long KEEP_ALIVE = 50000L;

    public TaskExecutorPool()
    {
      super(poolSize, poolSize, KEEP_ALIVE, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
      this.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable thrwbl)
    {
      super.afterExecute(r, thrwbl);

      
      
      // try to reexecute the task runner while
      // the task queue is not empty and while the pool
      // is still completing the execution of tasks.
      @SuppressWarnings("unchecked")
      TaskExecutor taskExecutor = (TaskExecutor) r;
      
      while (taskExecutor.getQueue().size() > 0 || getQueue().size() > 0) {
        if (taskExecutor.getQueue().size() > 0) {
          execute(r);
          return;
        }
        try {
          Thread.sleep(TICKLE_TIME);
        } catch (InterruptedException e) {
          LOG.error("", e);
        }
      }
      
      state = ExecutionState.COMPLETED;
      // If this point is reached, a count of one means this completed thread
      if(this.getActiveCount() == 1) {
    	  
    	// finish droid just once  
        taskExecutor.getDroid().finished();
        shutdown();
      }

    }
  }

  private class TaskExecutor implements Runnable
  {

    private final Droid<T> droid;
    private final Queue<T> queue;
    private final Worker<T> worker;

    public TaskExecutor(Droid<T> droid)
    {
      this.droid = droid;
      this.queue = droid.getQueue();
      this.worker = droid.getNewWorker();
    }

    public Droid<T> getDroid()
    {
      return droid;
    }

    public Queue<T> getQueue()
    {
      return queue;
    }

    @SuppressWarnings("unused")
    public Worker<? extends Task> getWorker()
    {
      return worker;
    }

    @Override
    public void run()
    {
      // poll the last task
      T task = queue.poll();
      
      if(task == null) {
        try {
          Thread.sleep(TICKLE_TIME);
        } catch (InterruptedException e) {
          LOG.error("", e);
        }
        task = queue.poll();
      }

      // execute the task
      if (task != null) {
        try {
          // monitor the execution of the task
          if (monitor != null) {
            monitor.beforeExecute(task, worker);
          }

          // debug
          if (LOG.isDebugEnabled()) {
            LOG.debug("Worker [" + worker + "] execute task [" + task + "].");
          }

          // execute the task
          if(!task.isAborted()) {
            worker.execute(task);
          }

          // debug
          if (LOG.isDebugEnabled()) {
            LOG.debug("Worker [" + worker + "] executed task [" + task + "] with success.");
          }

          // monitor the execution of the task
          if (monitor != null) {
            monitor.afterExecute(task, worker, null);
          }

          // set the monitored variables
          completedTasks.incrementAndGet();
          lastCompletedTask = task;

        } catch (Exception ex) {
          // debug
          if (LOG.isDebugEnabled()) {
            LOG.debug("Worker [" + worker + "] executed task [" + task + "] without success.");
          }

          // debug
          if (LOG.isErrorEnabled()) {
            LOG.error("", ex);
          }

          // monitor the exception
          if (monitor != null) {
            monitor.afterExecute(task, worker, ex);
          }

          // handler the exception
          if (ex != null) {
            TaskExceptionResult result = exceptionHandler.handleException(ex);

            // stop the execution in case of a fatal exception
            if (TaskExceptionResult.FATAL.equals(result)) {
              state = ExecutionState.STOPPED;
              droid.finished();
              pool.shutdownNow();
            }
          }
        }
      }
    }
  }
}
