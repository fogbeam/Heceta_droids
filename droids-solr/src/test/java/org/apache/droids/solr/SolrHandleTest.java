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

import junit.framework.TestCase;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;

public class SolrHandleTest extends TestCase {
 
  SolrServer solr;

  protected String getSolrHome()
  {
    return "example";
  }
  
  @Override 
  public void setUp() throws Exception 
  {
    super.setUp();
    
    SolrResourceLoader loader = new SolrResourceLoader( getSolrHome() );
    CoreContainer container = new CoreContainer( loader ); 
    CoreDescriptor descriptor = new CoreDescriptor(container, 
        "cname", "." );
    SolrCore core = container.create(descriptor);
    container.register( core.getName(), core, false );

    solr = new EmbeddedSolrServer(container, core.getName());
  }
  
  public void tearDown() throws Exception
  {
    // remove everything....
    solr.deleteByQuery( "*:*" );
    solr.commit();
  }
  
  public void testHello() throws Exception
  {
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField( "id", "aaaa" );
    solr.add( doc );
    solr.commit();
    
    assertEquals( 1, solr.query( new SolrQuery( "*:*" ) ).getResults().getNumFound() );
  }
  
  public void testAfter() throws Exception
  {
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField( "id", "bbbb" );
    solr.add( doc );
    solr.commit();
    
    assertEquals( 1, solr.query( new SolrQuery( "*:*" ) ).getResults().getNumFound() );
  }
}