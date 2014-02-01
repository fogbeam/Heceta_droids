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
package org.apache.droids;

import java.util.Queue;
import org.apache.droids.api.Droid;
import org.apache.droids.api.Task;
import org.apache.droids.api.TaskMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage common tasks in standard Droids
 */
public abstract class AbstractDroid<T extends Task> implements Droid<T>
{

  protected final Logger log = LoggerFactory.getLogger(AbstractDroid.class);
  protected final Queue<T> queue;
  protected final TaskMaster<T> taskMaster;

  public AbstractDroid(Queue<T> queue, TaskMaster<T> taskMaster)
  {
    this.queue = queue;
    this.taskMaster = taskMaster;
  }

  @Override
  public void start()
  {
    taskMaster.start(queue, this);
  }

  public Queue<T> getQueue()
  {
    return queue;
  }

  @Override
  public TaskMaster<T> getTaskMaster()
  {
    return taskMaster;
  }
}
