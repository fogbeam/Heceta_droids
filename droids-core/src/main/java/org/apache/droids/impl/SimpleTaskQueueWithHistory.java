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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.droids.api.Task;

/**
 * Extend the task queue to ignore any tasks we have already seen
 */
public class SimpleTaskQueueWithHistory<T extends Task> extends LinkedBlockingQueue<T> {

  private final Set<String> previous;

  /**
   * Simple queue constructor.
   */
  public SimpleTaskQueueWithHistory() {
    super();
    previous = Collections.synchronizedSet(new HashSet<String>());
  }

  @Override
  public boolean offer(T e) {
    if (previous.add(e.getId())) {
      return super.offer(e);
    } else {
      return false;
    }
  }

  @Override
  public boolean add(T e) {
    return this.offer(e);
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    boolean hasChanged = false;
    for (T e : c) {
      // Must be in this order otherwise the short circuiting or
      // will make it so that items aren't added.
      hasChanged = this.offer(e) || hasChanged;
    }
    return hasChanged;
  }

  public void clearHistory() {
    previous.clear();
  }
}
