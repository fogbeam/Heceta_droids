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

import java.io.File;

import junit.framework.Assert;

import org.apache.droids.robot.walker.FileTask;
import org.junit.Before;
import org.junit.Test;

public class TestSimpleQueue {

  MaxDepthTaskValidator<FileTask> validator; 
  
  @Before
  public final void initialize(){
    validator = new MaxDepthTaskValidator<FileTask>();
    validator.setMaxDepth(5);
  }
  
  @Test
  public void whenTaskBelowMaxDepthIsValidated_thenTaskIsValid() throws Exception {
    final FileTask task = new FileTask(new File(""), 3);
    
    boolean isValid = validator.validate(task);
    
    Assert.assertTrue(isValid);
  }
  
  @Test
  public void whenTaskEqualToMaxDepthIsValidated_thenTaskIsValid() throws Exception {
    final FileTask task = new FileTask(new File(""), 5);
    
    boolean isValid = validator.validate(task);
    
    Assert.assertTrue(isValid);
  }
  
  @Test
  public void whenTaskOverMaxDepthIsValidated_thenTaskIsNotValid() throws Exception {
    final FileTask task = new FileTask(new File(""), 7);
    
    boolean isValid = validator.validate(task);
    
    Assert.assertFalse(isValid);
  }
  
}
