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
import java.util.Date;

import org.apache.droids.api.Task;


public class FileTask implements Task
{
  private static final long serialVersionUID = 122177113684842951L;

  private final Date started;
  private final int depth;
  private final File file;
  private boolean aborted = false;
  
  public FileTask( File file, int depth )
  {
    this.file = file;
    this.depth = depth;
    this.started = new Date();
  }

  @Override
  public String getId() {
    return file.getPath();
  }

  @Override
  public Date getTaskDate() {
    return started;
  }

  @Override
  public int getDepth() {
    return depth;
  }

  public File getFile() {
    return file;
  }
  
  @Override
  public String toString()
  {
    return "Task["+depth+"]["+file.getAbsolutePath()+"]";
  }
  
  @Override
  public void abort() {
    aborted = true;
  }
  
  @Override
  public boolean isAborted() {
    return aborted;
  }
}