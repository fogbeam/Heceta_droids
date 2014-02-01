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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * A simple implementation of {@link ContentLoader} based on {@link URLConnection}.
 */
public class SimpleContentLoader implements ContentLoader
{

  @Override
  public boolean exists(URI uri) throws IOException
  {
    URL url = uri.toURL();
    try {
      URLConnection conn = url.openConnection();
      return conn != null;
    } catch (IOException ex) {
      return false;
    }
  }

  @Override
  public InputStream load(URI uri) throws IOException {
    URL url = uri.toURL();
    URLConnection conn = url.openConnection();
    return conn.getInputStream();
  }

}
