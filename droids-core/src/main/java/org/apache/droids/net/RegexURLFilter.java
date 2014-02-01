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
package org.apache.droids.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.droids.api.URLFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Regular expression implementation of an UrlFilter. Evaluates the url based on
 * regular expression.
 * 
 * @version 1.0
 * 
 */
public class RegexURLFilter implements URLFilter {

  private static final Logger LOG = LoggerFactory.getLogger(RegexURLFilter.class);

  /** An array of applicable rules */
  private final List<RegexRule> rules;
  
  public RegexURLFilter(){
    rules = new ArrayList< RegexRule >(16);
  }
  
  /**
   * Adds a new regex rule to this filter <br>
   * @param sign
   * @param regex 
   */
  public void addRule(final boolean sign, final String regex) {
    if( regex == null ){
      throw new IllegalArgumentException();
    }

    RegexRule rule = createRule(sign, regex);
    rules.add(rule);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.droids.api.URLFilter#filter(java.lang.String)
   */
  @Override
  public String filter(String url) {
    synchronized (rules) {
      for (RegexRule rule : rules) {
        if (rule.match(url)) {
          return rule.accept() ? url : null;
        }
      }
    }
    return null;
  }
  
  /**
   * @param file
   * @throws IOException
   */
  public void setFile(String file) throws IOException {
    URL url = null;
    if (file.startsWith("classpath:/")) {
      url = this.getClass().getResource(file.substring("classpath:/".length() - 1));
    } else {
      url = (file.contains(":/") ? new URL(file) : new URL("file://" + file));
    }
    LOG.debug("url " + url);
    Reader reader = new InputStreamReader(url.openStream());
    try {
    	rules.addAll( readRulesFile(reader) );
    } finally {
    	reader.close();
    }
  }

  private List<RegexRule> readRulesFile(Reader reader) throws IOException {
    BufferedReader in = new BufferedReader(reader);
    List<RegexRule> localRules = new ArrayList<RegexRule>(16);
    String line = null;

    while ((line = in.readLine()) != null) {
      if (line.length() == 0) {
        continue;
      }
      char first = line.charAt(0);
      boolean sign = false;
      switch (first) {
      case '+':
        sign = true;
        break;
      case '-':
        sign = false;
        break;
      case ' ':
      case '\n':
      case '#': // skip blank & comment lines
        continue;
      default:
        throw new IOException("Invalid first character: " + line);
      }

      String regex = line.substring(1);
      if (LOG.isTraceEnabled()) {
        LOG.trace("Adding rule [" + regex + "]");
      }
      final RegexRule rule = createRule(sign, regex);
      localRules.add(rule);
    }
    return localRules;

  }

  private static RegexRule createRule(boolean sign, String regex) {
    return new Rule(sign, regex);
  }

  private static class Rule extends RegexRule {

    private Pattern pattern;

    Rule(boolean sign, String regex) {
      super(sign);
      pattern = Pattern.compile(regex);
    }

    @Override
    protected boolean match(String url) {
      return pattern.matcher(url).find();
    }
  }
}
