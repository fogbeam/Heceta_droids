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
package org.apache.droids.helper;

import java.io.File;
import java.io.IOException;

/**
 * This is an utility class for file related operations. 
 */
public final class FileUtil {

  private FileUtil() {
    throw new AssertionError();
  }

  public static void createFile(File cache) throws IOException {
    if (!cache.isDirectory() && !cache.getAbsolutePath().endsWith("/")) {
      try {
        cache.createNewFile();
      } catch (IOException e) {
        // if we cannot create a file that means that the parent path
        // does not exists
        final File path = new File(cache.getParent());
        path.mkdirs();
        cache.createNewFile();
      }
    }
  }

}
