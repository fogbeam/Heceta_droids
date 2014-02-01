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
package org.apache.droids.handle;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import org.apache.droids.api.ContentEntity;
import org.apache.droids.api.Handler;

/**
 * Handler that write the stream to the sysout. Mostly added for debugging
 * reasons.
 * 
 * @version 1.0
 * 
 */
public class SysoutHandler extends WriterHandler implements Handler {

  private static void writeOutput(InputStream stream) throws IOException {
    Reader reader = new InputStreamReader(stream);
    Writer output = new OutputStreamWriter(System.out);
    pipe(reader, output);
  }

  @Override
  public void handle(URI uri, ContentEntity entity) throws IOException {
    InputStream instream = entity.obtainContent();
    try {
      writeOutput(instream);
    } finally {
      instream.close();
    }
  }

}
