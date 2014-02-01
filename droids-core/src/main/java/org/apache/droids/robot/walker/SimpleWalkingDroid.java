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
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.droids.AbstractDroid;
import org.apache.droids.api.TaskMaster;
import org.apache.droids.exception.InvalidTaskException;
import org.apache.droids.impl.MultiThreadedTaskMaster;

public class SimpleWalkingDroid extends AbstractDroid<FileTask> implements WalkingDroid
{

  private Collection<File> initialFiles;

  public SimpleWalkingDroid(Queue<FileTask> queue, TaskMaster<FileTask> taskMaster)
  {
    super(queue, taskMaster);
  }

  @Override
  public void setInitialFiles(Collection<File> initialFiles)
  {
    this.initialFiles = initialFiles;
  }

  @Override
  public void init() throws InvalidTaskException
  {
    Preconditions.checkState(initialFiles != null, "FileSystemWalker requires at least one starting file");
    Preconditions.checkState(!initialFiles.isEmpty(), "FileSystemWalker requires at least one starting file");
    for (File file : initialFiles) {
      queue.add(new FileTask(file, 0));
    }
  }

  @Override
  public void finished()
  {
    log.info("FINISHED!!!");
  }

  @Override
  public FileWorker getNewWorker()
  {
    return new FileWorker(queue);
  }

  //------------------------------------------------------------------
  //------------------------------------------------------------------
  public static void main(String[] args) throws Exception
  {
    MultiThreadedTaskMaster<FileTask> taskMaster = new MultiThreadedTaskMaster<FileTask>();
    taskMaster.setPoolSize(3);

    Queue<FileTask> queue = new LinkedList<FileTask>();

    Collection<File> files = new ArrayList<File>();
    files.add(new File(args[0])); //APACHE/droids/src" ) );

    SimpleWalkingDroid simple = new SimpleWalkingDroid(queue, taskMaster);
    simple.setInitialFiles(files);
    simple.init();
    simple.start();  // TODO? perhaps start internally calls init()?
    simple.getTaskMaster().awaitTermination(0, TimeUnit.SECONDS);
  }
}
