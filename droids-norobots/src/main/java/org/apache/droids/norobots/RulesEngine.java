/*
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.droids.norobots;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a series of Rules. It then runs a path against these 
 * to decide if it is allowed or not. 
 */
class RulesEngine {

  private List<Rule> rules;

  public RulesEngine() {
    this.rules = new ArrayList<Rule>();
  }

  public void allowPath(String path) {
    add( new AllowedRule(path) );
  }

  public void disallowPath(String path) {
    add( new DisallowedRule(path) );
  }

  public void add(Rule rule) {
    this.rules.add(rule);
  }

  /**
   * Run each Rule in series on the path. 
   * If a Rule returns a Boolean, return that.
   * When no more rules are left, return null to indicate there were 
   * no rules for this path.. 
   */
  public Boolean isAllowed(String path) {
    for( Rule rule : rules ) {
      Boolean test = rule.isAllowed(path);
      if(test != null) {
        return test;
      }
    }
    return null;
  }

  public boolean isEmpty() {
    return this.rules.isEmpty();
  }

  @Override
  public String toString() {
    return "RulesEngine: " + this.rules;
  }
}
