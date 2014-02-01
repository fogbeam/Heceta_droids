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
package org.apache.droids.protocol.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.apache.droids.api.ManagedContentEntity;
import org.apache.droids.api.Parse;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;

public class HttpContentEntity implements ManagedContentEntity {

  private final HttpEntity entity;
  private final String mimeType;
  private final String charset;
  
  private Parse parse = null;
  
  public HttpContentEntity(HttpEntity entity, long maxlen) throws IOException {
    super();
    if (entity.isRepeatable()) {
      this.entity = entity;
    } else {
      this.entity = new DroidHttpEntity(entity, maxlen);
    }
    
    String mimeType = null;
    String charset = null;
    Header header = entity.getContentType();
    if (header != null) {
      HeaderElement[] helems = header.getElements();
      if (helems != null && helems.length > 0) {
        HeaderElement helem = helems[0];
        mimeType = helem.getName();
        NameValuePair nvp = helem.getParameterByName("charset");
        if (nvp != null) {
          charset = nvp.getValue();
        }
      }
    }
    if (mimeType != null) {
      this.mimeType = mimeType.toLowerCase(Locale.ENGLISH);
    } else {
      this.mimeType = "binary/octet-stream";
    }
    if (charset != null) {
      this.charset = charset;
    } else {
      if (this.mimeType.startsWith("text/")) {
        this.charset = HTTP.ISO_8859_1;
      } else {
        this.charset = null;
      }
    }
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
  public InputStream obtainContent() throws IOException {
    return entity.getContent();
  }

  @Override
  public Parse getParse() {
    return this.parse;
  }

  @Override
  public void setParse(Parse parse) {
    this.parse = parse;
  }

  @Override
  public void finish() {
  }

}
