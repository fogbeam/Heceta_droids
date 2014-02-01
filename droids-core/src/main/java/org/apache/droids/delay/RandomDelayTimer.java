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

import java.util.Random;

import org.apache.droids.api.DelayTimer;

/**
 * An instance of this class is used to generate random delays.
 */
public class RandomDelayTimer implements DelayTimer
{

  protected final Random random;
  protected int minimumDelay;
  protected int delaySpread;

  /**
   * Creates a new random delay generator,
   */
  public RandomDelayTimer()
  {
    this(0, 0);
  }

  /**
   * Creates a new random delay generator with minimum and range constraints.
   *
   * @param min
   * @param range
   */
  public RandomDelayTimer(int min, int range)
  {
    random = new Random();
    minimumDelay = min;
    delaySpread = range;
  }

  @Override
  public long getDelayMillis()
  {
    if (delaySpread > 0) {
      return (long) this.random.nextInt(delaySpread) + minimumDelay;
    }
    return 0;
  }

  /**
   * Returns the minimum delay
   *
   * @return the minimum delay
   */
  public int getMinimumDelay()
  {
    return minimumDelay;
  }

  /**
   * Sets the minimum delay
   *
   * @param minimumDelay
   */
  public void setMinimumDelay(int minimumDelay)
  {
    this.minimumDelay = minimumDelay;
  }

  /**
   * Returns the delay spread
   *
   * @return the delay spread
   */
  public int getDelaySpread()
  {
    return delaySpread;
  }

  /**
   * Sets the delay spread
   *
   * @param delaySpread
   */
  public void setDelaySpread(int delaySpread)
  {
    this.delaySpread = delaySpread;
  }

}
