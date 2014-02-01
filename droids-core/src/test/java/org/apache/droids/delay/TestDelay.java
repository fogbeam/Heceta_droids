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
package org.apache.droids.delay;

import org.apache.droids.api.DelayTimer;
import org.junit.Test;

import junit.framework.Assert;

public class TestDelay
{

  @Test
  public void testTimers()
  {
    int i=0;
    int min = 10;
    int spread = 100;
    RandomDelayTimer timer = new RandomDelayTimer( min, spread );
    for( i=0; i<100; i++ ) {
      long delay = timer.getDelayMillis();
      Assert.assertTrue( delay >= min );
      Assert.assertTrue( delay < (min+spread) );
    }
    min = 300; spread = 20;
    timer.setDelaySpread( spread );
    timer.setMinimumDelay( min );
    for( i=0; i<100; i++ ) {
      long delay = timer.getDelayMillis();
      Assert.assertTrue( delay >= min );
      Assert.assertTrue( delay < (min+spread) );
    }
    
    timer = new GaussianRandomDelayTimer( min, spread );
    for( i=0; i<100; i++ ) {
      long delay = timer.getDelayMillis();
      Assert.assertTrue( "DELAY:"+delay, delay >= min );
      Assert.assertTrue( "DELAY:"+delay, delay < (min+(spread*4)) );
    }
    
    DelayTimer t = new SimpleDelayTimer( 1000 );
    Assert.assertTrue( 1000 == t.getDelayMillis() );
    
    // default timers all have time zero
    Assert.assertEquals( 0, new SimpleDelayTimer().getDelayMillis() );
    Assert.assertEquals( 0, new RandomDelayTimer().getDelayMillis() );
    Assert.assertEquals( 0, new GaussianRandomDelayTimer().getDelayMillis() );
  }
}
