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
package org.apache.droids.dynamic;

import org.apache.droids.api.Droid;
import org.apache.droids.exception.DroidsException;
import org.apache.droids.helper.factories.DroidFactory;
import org.apache.droids.helper.factories.HandlerFactory;
import org.apache.droids.helper.factories.ParserFactory;
import org.apache.droids.helper.factories.ProtocolFactory;
import org.apache.droids.helper.factories.URLFiltersFactory;

/**
 * Core configuration mainly holding the different factories we are using.
 * 
 * @version 1.0
 *
 */
public class DroidsConfig {

  private DroidFactory droids=null;

  /**
   * Set the droidsFactory we are using. 
   * This is the core component that knows all registered droids
   * that the application offers.  
   * @param droids 
   * @see DroidFactory
   */
  public void setDroids(DroidFactory droids) {
    this.droids = droids;
  }

  /**
   * Return the droid we want to use identified
   * by the given name. Will contact the droidsFactory
   * and looks up the droid.
   * @param name the name of the droid we want to use
   * @return The droid that is identified by the name
   * @see DroidFactory
   */
  public Droid getDroid(String name) {
    return droids.getDroid(name);
  }

  private ParserFactory parserFactory=null;
  
  private ProtocolFactory protocolFactory=null;

  private URLFiltersFactory filtersFactory=null;
  
  private HandlerFactory handlerFactory=null;

  /**
   * Returns the protocolFactory that knows all registered 
   * protocol.
   * @return protocolFactory that knows all registered 
   * protocol.
   * @see ProtocolFactory
   */
  public ProtocolFactory getProtocolFactory() {
    return protocolFactory;
  }

  /**
   * Set the pre-configured protocolFactory that knows all registered 
   * protocol.
   * @param protocolFactory pre-configured protocolFactory
   * @see ProtocolFactory
   */
  public void setProtocolFactory(ProtocolFactory protocolFactory) {
    this.protocolFactory = protocolFactory;
  }

  /**
   * Start a given Droid. First we will look up the droid
   * identified by the given name and then start it.
   * @param name the name of the droid we want to use
   */
  public void start(String name) throws DroidsException {
    Droid droid = getDroid(name);
    
    droid.init();
    droid.start();
  }


  /**
   * Returns the parserFactory that knows all registered 
   * parser.
   * @return parserFactory that knows all registered 
   * parser.
   * @see ParserFactory
   */
  public ParserFactory getParserFactory() {
    return parserFactory;
  }

  /**
   * Set the pre-configured parserFactory that knows all registered 
   * parser.
   * @param parserFactory pre-configured parserFactory
   * @see ParserFactory
   */
  public void setParserFactory(ParserFactory parserFactory) {
    this.parserFactory = parserFactory;
  }

  /**
   * Returns the filtersFactory that knows all registered 
   * filters.
   * @return filtersFactory that knows all registered 
   * filters.
   * @see URLFiltersFactory
   */
  public URLFiltersFactory getFiltersFactory() {
    return filtersFactory;
  }

  /**
   * Set the pre-configured filtersFactory that knows all registered filters. 
   * @param filtersFactory filtersFactory that knows all registered 
   * filters.
   * @see URLFiltersFactory
   */
  public void setFiltersFactory(URLFiltersFactory filtersFactory) {
    this.filtersFactory = filtersFactory;
  }

  /**
   * Returns the handlerFactory that knows all registered handlers.
   * @return handlerFactory that knows all registered handlers.
   * @see HandlerFactory
   */
  public HandlerFactory getHandlerFactory() {
    return handlerFactory;
  }

  /**
   * Set the pre-configured handlerFactory that knows all registered handlers.
   * @param handlerFactory pre-configured handlerFactory that knows all registered handlers.
   * @see HandlerFactory
   */
  public void setHandlerFactory(HandlerFactory handlerFactory) {
    this.handlerFactory = handlerFactory;
  }


}
