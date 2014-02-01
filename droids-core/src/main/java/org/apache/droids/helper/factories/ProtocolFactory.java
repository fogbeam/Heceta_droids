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
package org.apache.droids.helper.factories;

import java.net.URI;

import org.apache.droids.api.Protocol;
import org.apache.droids.exception.ProtocolNotFoundException;

/**
 * Factory that will lookup a protocol plugin and return it.
 * 
 * @version 1.0
 * 
 */
public class ProtocolFactory extends GenericFactory<Protocol> {

  /**
   * Will lookup a protocol based on the underlying uri
   * 
   * @param uri
   *                the string that contains the protocol
   * @return ready to use protocol plugin or null if non have been found
   * @throws ProtocolNotFoundException
   */
  public Protocol getProtocol(URI uri) throws ProtocolNotFoundException {
    Protocol protocol = null;
    try {
      String protocolName = uri.getScheme();
      if (protocolName == null) {
        throw new ProtocolNotFoundException(uri);
      }
      protocol = getMap().get(protocolName);
    } catch (ProtocolNotFoundException e) {
      throw new ProtocolNotFoundException(uri, e.toString());
    }
    return protocol;
  }

}
