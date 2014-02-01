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
package org.apache.droids.solr;

import java.io.IOException;
import java.net.URI;

import org.apache.droids.api.ContentEntity;
import org.apache.droids.api.Handler;
import org.apache.droids.exception.DroidsException;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;


public class SolrHandler implements Handler {

  private SolrServer solr;

  @Override
  public void handle(URI uri, ContentEntity entity) 
    throws IOException, DroidsException
  {
    SolrInputDocument doc = createSolrInputDocument(uri, entity);
    try {
      solr.add( doc );
    } 
    catch (SolrServerException e) {
      throw new DroidsException( e );
    }
  }

  public SolrInputDocument createSolrInputDocument(URI url, ContentEntity entity) 
  { 
    SolrInputDocument doc = new SolrInputDocument();
    doc.setField( "id", url.getPath() );
    doc.setField( "name", url.toASCIIString() );
    doc.setField( "host", url.getHost() );
    doc.setField( "mime", entity.getMimeType() );
    doc.setField( "content", entity.getParse().getText() );
    return doc;
  }
}
