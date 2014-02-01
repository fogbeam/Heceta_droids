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
package org.apache.droids.tika.parse;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.droids.api.Link;
import org.apache.droids.parse.ParseImpl;
import org.apache.droids.tika.api.TikaParse;
import org.apache.tika.metadata.Metadata;

public class TikaParseImpl extends ParseImpl implements TikaParse {

  private String plainText;
  private String mainContent;
  private Metadata metadata;
  
  public TikaParseImpl(String text, Collection<Link> outlinks) {
    super(text,outlinks);
  }
  
  public TikaParseImpl(String text, Object data, Collection<Link> outlinks) {
    super(text,data,outlinks);
  }

  public TikaParseImpl(String xmlContent, ArrayList<Link> extractedTasks,
      String plainText, String mainContent, Metadata metadata) {
    this(xmlContent, extractedTasks);
    this.plainText = plainText;
    this.mainContent = mainContent;
    this.metadata = metadata;
  }

  @Override
  public String getMainContent() {
    return mainContent;
  }

  @Override
  public Metadata getMetadata() {
    return metadata;
  }

  @Override
  public String getXml() {
    return super.text;
  }

  @Override
  public String getPlainText() {
    return plainText;
  }

  @Override
  public boolean isFollowed() {
    if(metadata.get("robots") != null && metadata.get("robots").toLowerCase().contains("nofollow")) {
      return false;
    }
    return true;
  }

  @Override
  public boolean isIndexed() {
    if(metadata.get("robots") != null && metadata.get("robots").toLowerCase().contains("noindex")) {
      return false;
    }
    return true;
  }

}
