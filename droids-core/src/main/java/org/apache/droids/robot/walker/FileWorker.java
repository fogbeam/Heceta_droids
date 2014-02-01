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
package org.apache.droids.robot.walker;

import java.io.File;

import java.util.Queue;
import org.apache.droids.api.Worker;
import org.apache.droids.exception.InvalidTaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileWorker implements Worker<FileTask>
{

  private final Logger log = LoggerFactory.getLogger(FileWorker.class);
  final Queue<FileTask> queue;

  public FileWorker(Queue<FileTask> queue)
  {
    this.queue = queue;
  }

  @Override
  public void execute(FileTask task) throws InvalidTaskException
  {
    File file = task.getFile();
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null) {
        for (File f : files) {
          queue.add(new FileTask(f, task.getDepth() + 1));
        }
      }
    } else {
      if (log.isInfoEnabled()) {
        log.info("FILE: " + file.getAbsolutePath());
      }
    }
  }
}
