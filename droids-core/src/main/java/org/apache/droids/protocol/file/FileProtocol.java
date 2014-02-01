/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.apache.droids.protocol.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.droids.api.ManagedContentEntity;
import org.apache.droids.api.Parse;
import org.apache.droids.api.Protocol;

/**
 * 
 * Protocol implementation for system-independent
 * file and directory pathnames based on java.io.File.
 * 
 * @version 1.0
 * 
 */
public class FileProtocol implements Protocol {

  @Override
  public boolean isAllowed(URI uri) {
    File file = new File(extractLocation(uri));
    return file.canRead();
  }

  @Override
  public ManagedContentEntity load(URI uri) throws IOException {
    File file = new File(extractLocation(uri));
    return new FileContentEntity(file);
  }

  /**
   * Removes the scheme from the URI to get the pathname of the file.
   * 
   * @param uri The URI of the file
   * @return	The pathname of the file
   */
  private String extractLocation(URI uri) {
    String location = uri.toString();
    final int start = location.indexOf("://");
    if(start>-1){
      location = location.substring(start+3);
    }
    return location;
  }

  /**
   * 
   * Content entity representing the body of a file.
   * 
   */
  static class FileContentEntity implements ManagedContentEntity {
    
	/**
	 * The file represented by the ContentEntity
	 */
    private final File file;
    /**
     * The mime-type of the file
     */
    private final String mimeType;
    /**
     * The charset of the file
     */
    private final String charset;
    /**
     * The parse object
     */
    private Parse parse = null;
    
    /**
     * Creates a new FileContentEntity instance for the give file.
     * 
     * @param file 
     * @throws IOException
     */
    public FileContentEntity(File file) throws IOException {
      super();
      this.file = file;
      String s = file.getName().toLowerCase();
      if (s.endsWith(".html") || s.endsWith(".htm")) {
        this.mimeType = "text/html";
        this.charset = "ISO-8859-1";
      } else if (s.endsWith(".txt")) {
        this.mimeType = "text/plain";
        this.charset = "ISO-8859-1";
      } else {
        this.mimeType = "binary/octet-stream";
        this.charset = null;
      }
    }

    @Override
    public InputStream obtainContent() throws IOException {
      return new BufferedInputStream(new FileInputStream(file));
    }

    @Override
    public void finish() {
    }

    @Override
    public String getMimeType() {
      return mimeType;
    }

    @Override
    public String getCharset() {
      return charset;
    }

    @Override
    public Parse getParse() {
      return this.parse;
    }

    @Override
    public void setParse(Parse parse) {
      this.parse = parse;
    }

  }
  
}
