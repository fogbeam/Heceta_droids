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

/**
 * An instance of this class is used to generate Gaussian distributed
 * random delays.
 */
public class GaussianRandomDelayTimer extends RandomDelayTimer implements DelayTimer
{

  /**
   * Creates a new Gaussian distributed random delay generator
   */
  public GaussianRandomDelayTimer()
  {
    super(0, 0);
  }

  /**
   * Creates a new Gaussian distributed delay generator with minimum and 
   * range constraints.
   * 
   * @param min
   * @param range
   */
  public GaussianRandomDelayTimer(int min, int range)
  {
    super(min, range);
  }

  @Override
  public long getDelayMillis()
  {
    double delay;
    do {
      delay = (random.nextGaussian() + 1) / 2;
    } while (delay < 0 || delay > 1);
    return (long) (delay * delaySpread + minimumDelay);
  }
  
}
