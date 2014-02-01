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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A Client which may be used to decide which urls on a website 
 * may be looked at, according to the norobots specification 
 * located at: 
 * http://www.robotstxt.org/wc/norobots-rfc.html
 */
public class NoRobotClient {

  private static final String US_ASCII = "US-ASCII";
  
  private final ContentLoader contentLoader;
  private final String userAgent;
  
  private URI baseURI;
  private URI robotsURI;
  private RulesEngine rules;
  private RulesEngine wildcardRules;

  /**
   * Create a Client for a particular user-agent name and the given
   * {@link ContentLoader}. 
   *
   * @param userAgent name for the robot
   */
  public NoRobotClient(ContentLoader contentLoader, String userAgent) {
    super();
    if (contentLoader == null) {
      throw new IllegalArgumentException("Content loader may not be null");
    }
    this.contentLoader = contentLoader;
    if (userAgent != null) {
      this.userAgent = userAgent.toLowerCase(Locale.ENGLISH);
    } else {
      this.userAgent = null;
    }
  }

  /**
   * Create a Client for a particular user-agent name. 
   *
   * @param userAgent name for the robot
   */
  public NoRobotClient(String userAgent) {
    this(new SimpleContentLoader(), userAgent);
  }
  
  /**
   * Head to a website and suck in their robots.txt file. 
   * Note that the URL passed in is for the website and does 
   * not include the robots.txt file itself.
   *
   * @param baseUrl of the site
   */
  public void parse(URI baseUri) throws IOException, NoRobotException {
    URI uri = resolveURI(baseUri, "robots.txt");
    baseURI = baseUri;
    robotsURI = uri;
    rules = null;
    wildcardRules = null;
    // fetch baseUrl+"robots.txt"
    if (!contentLoader.exists(uri)) {
      return;
    }
    InputStream instream = contentLoader.load(uri);
    doParseText(instream);
  }

  public void parseText(InputStream instream) throws IOException, NoRobotException {
    doParseText(instream);
    baseURI = createURI("/");
    robotsURI = resolveURI(baseURI, "robots.txt");
  }

  private void doParseText(InputStream instream) throws IOException {
    Map<String, RulesEngine> map = parse(instream);
    this.rules = map.get(this.userAgent);
    if (this.rules == null) {
      this.rules = new RulesEngine();
    }
    this.wildcardRules = map.get("*");
    if (this.wildcardRules == null) {
      this.wildcardRules = new RulesEngine();
    }
  }

  public static Map<String, RulesEngine> parse(InputStream instream) throws IOException {
    try {
      return doParse(instream);
    } finally {
      instream.close();
    }
  }
  
  enum ParserState 
  {
    USER_AGENT_DEF, ALLOW_DISALLOW_DEF
  }
  
  private static Map<String, RulesEngine> doParse(InputStream instream) throws IOException {

    Map<String, RulesEngine> map = new HashMap<String, RulesEngine>();
    // Classic basic parser style, read an element at a time, 
    // changing a state variable [parsingAllowBlock]

    // take each line, one at a time
    BufferedReader rdr = new BufferedReader(new InputStreamReader(instream, US_ASCII));
    
    Set<RulesEngine> engines = new HashSet<RulesEngine>();
    
    ParserState state = ParserState.ALLOW_DISALLOW_DEF;
    
    String line = "";
    while( (line = rdr.readLine()) != null ) {
      // trim whitespace from either side
      line = line.trim();

      // ignore startsWith('#')
      if(line.startsWith("#")) {
        continue;
      }

      if(line.startsWith("User-agent:")) {
        if (state == ParserState.ALLOW_DISALLOW_DEF) {
          engines.clear();
        }
        state = ParserState.USER_AGENT_DEF;
        String userAgent = line.substring("User-agent:".length());
        userAgent = userAgent.trim().toLowerCase(Locale.ENGLISH);
        RulesEngine engine = map.get(userAgent);
        if (engine == null) {
          engine = new RulesEngine();
          map.put(userAgent, engine);
        }
        engines.add(engine);
      } 
      else {
        if (engines.isEmpty()) {
          continue;
        }
        if(line.startsWith("Allow:")) {
          state = ParserState.ALLOW_DISALLOW_DEF;
          String value = line.substring("Allow:".length()).trim();
          value = URLDecoder.decode(value, US_ASCII);
          for (RulesEngine engine: engines) {
            engine.allowPath( value );
          }
        } else 
        if(line.startsWith("Disallow:")) {
          state = ParserState.ALLOW_DISALLOW_DEF;
          String value = line.substring("Disallow:".length()).trim();
          value = URLDecoder.decode(value, US_ASCII);
          for (RulesEngine engine: engines) {
            engine.disallowPath( value );
          }
        } else {
          // ignore
          continue;
        }
      }
    }
    return map;
  }

  /**
   * Decide if the parsed website will allow this URL to be 
   * be seen. 
   *
   * Note that parse(URL) must be called before this method 
   * is called. 
   *
   * @param url in question
   * @return is the url allowed?
   *
   * @throws IllegalStateException when parse has not been called
   */
  public boolean isUrlAllowed(URI uri) throws IllegalStateException, IllegalArgumentException {
    if (baseURI == null || robotsURI == null) {
      throw new IllegalStateException("You must call parse before you call this method.  ");
    }

    if (!equals(baseURI.getHost(), uri.getHost()) ||
        baseURI.getPort() != uri.getPort() ||
        !equals(baseURI.getScheme(), uri.getScheme()))
    {
      throw new IllegalArgumentException(
          "Illegal to use a different url, " + uri.toString() + 
          ",  for this robots.txt: " + baseURI.toString());
    }
    if (uri.equals(robotsURI)) {
      return true;
    }
    
    String path = uri.getPath();
    String basepath = baseURI.getPath();
    if (path.startsWith(basepath)) {
      path = path.substring(basepath.length());
      if (!path.startsWith("/")) {
        path = "/" + path;
      }
    }
    
    try {
      path = URLDecoder.decode(path, US_ASCII);
    } catch (UnsupportedEncodingException ex) {
      // ASCII always supported
      return false;
    }
    Boolean allowed = this.rules != null ? this.rules.isAllowed( path ) : null;
    if(allowed == null) {
      allowed = this.wildcardRules != null ? this.wildcardRules.isAllowed( path ) : null;
    }
    if(allowed == null) {
      allowed = Boolean.TRUE;
    }

    return allowed.booleanValue();
  }

  
  /*
   * Utility methods. 
   */
  private static URI createURI(String s) throws NoRobotException {
    try {
      return new URI(s);
    } catch (URISyntaxException ex) {
      throw new NoRobotException("Invalid URI: " + ex.getInput());
    }
  }
  
  private static URI resolveURI(URI base, String s) throws NoRobotException {
    try {
      return base.resolve(new URI(s));
    } catch (URISyntaxException ex) {
      throw new NoRobotException("Invalid URI: " + ex.getInput());
    }
  }
  
  private static boolean equals(final Object obj1, final Object obj2) {
    return obj1 == null ? obj2 == null : obj1.equals(obj2);
  }
  
}
