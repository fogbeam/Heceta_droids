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

import org.apache.droids.api.Task;
import org.apache.droids.api.TaskValidator;

/**
 * A simple task validator that honors task depth.
 */
public class MaxDepthTaskValidator<T extends Task> implements TaskValidator<T> {
  private int maxDepth = -1;

  public MaxDepthTaskValidator() {
    super();
  }

  public MaxDepthTaskValidator(int maxDepth) {
    this.maxDepth = maxDepth;
  }

  @Override
  public boolean validate(final T task) {
    if (maxDepth > 0 && task.getDepth() > maxDepth) {
      return false;
    }
    return true;
  }

  public int getMaxDepth() {
    return maxDepth;
  }

  public void setMaxDepth(int maxDepth) {
    this.maxDepth = maxDepth;
  }
}
