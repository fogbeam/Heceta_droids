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
package org.apache.droids.norobots;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestNorobotsClient
{

  private static URI BASE_URI;
  
  @BeforeClass
  public static void setupBaseURL() throws URISyntaxException {
    ClassLoader cl = TestNorobotsClient.class.getClassLoader();
    BASE_URI = cl.getResource("data/").toURI();
  }
  
  //-----------------------------------------------------------------------
  // To test: 
  // create -> parse -> isUrlAllowed?

  @Test
  public void testAllowed() throws Exception {
    URI target = BASE_URI.resolve("basic/");
    NoRobotClient nrc = new NoRobotClient("Scabies-1.0");
    nrc.parse(target);
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("index.html")));
    Assert.assertFalse(nrc.isUrlAllowed(target.resolve("view-cvs/")));
  }

  // Tests the example given in the RFC
  @Test
  public void testRfcExampleUnhipbot() throws Exception {
    URI target = BASE_URI.resolve("rfc/");

    NoRobotClient nrc = new NoRobotClient("unhipbot");
    nrc.parse(target);

    // Start of rfc test
    Assert.assertFalse(nrc.isUrlAllowed(target));
    Assert.assertFalse(nrc.isUrlAllowed(target.resolve("index.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("robots.txt")));
    Assert.assertFalse(nrc.isUrlAllowed(target.resolve("server.html")));
    Assert.assertFalse(nrc.isUrlAllowed(target.resolve("services/fast.html")));
    Assert.assertFalse(nrc.isUrlAllowed(target.resolve("services/slow.html")));
    Assert.assertFalse(nrc.isUrlAllowed(target.resolve("orgo.gif")));
    Assert.assertFalse(nrc.isUrlAllowed(target.resolve("org/about.html")));
    Assert.assertFalse(nrc.isUrlAllowed(target.resolve("org/plans.html")));
    Assert.assertFalse(nrc.isUrlAllowed(target.resolve("%7Ejim/jim.html")));
    Assert.assertFalse(nrc.isUrlAllowed(target.resolve("%7Emak/mak.html")));
    // End of rfc test
  }

  @Test
  public void testRfcExampleWebcrawler() throws Exception {
    URI target = BASE_URI.resolve("rfc/");

    NoRobotClient nrc = new NoRobotClient("webcrawler");
    nrc.parse(target);
    // Start of rfc test
    Assert.assertTrue(nrc.isUrlAllowed(target));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("index.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("robots.txt")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("server.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("services/fast.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("services/slow.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("orgo.gif")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("org/about.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("org/plans.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("%7Ejim/jim.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("%7Emak/mak.html")));
    // End of rfc test
  }

  @Test
  public void testRfcExampleExcite() throws Exception {
    URI target = BASE_URI.resolve("rfc/");

    NoRobotClient nrc = new NoRobotClient("excite");
    nrc.parse(target);
    // Start of rfc test
    Assert.assertTrue(nrc.isUrlAllowed(target));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("index.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("robots.txt")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("server.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("services/fast.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("services/slow.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("orgo.gif")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("org/about.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("org/plans.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("%7Ejim/jim.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("%7Emak/mak.html")));
    // End of rfc test
  }

  @Test
  public void testRfcExampleOther() throws Exception {
    URI target = BASE_URI.resolve("rfc/");

    NoRobotClient nrc = new NoRobotClient("other");
    nrc.parse(target);
    // Start of rfc test
    Assert.assertFalse(nrc.isUrlAllowed(target));
    Assert.assertFalse(nrc.isUrlAllowed(target.resolve("index.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("robots.txt")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("server.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("services/fast.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("services/slow.html")));
    Assert.assertFalse(nrc.isUrlAllowed(target.resolve("orgo.gif")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("org/about.html")));
    Assert.assertFalse(nrc.isUrlAllowed(target.resolve("org/plans.html")));
    Assert.assertFalse(nrc.isUrlAllowed(target.resolve("%7Ejim/jim.html")));
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("%7Emak/mak.html")));
    // End of rfc test
  }

  @Test
  public void testRfcBadWebDesigner() throws Exception {
    URI target = BASE_URI.resolve("bad/");

    NoRobotClient nrc = new NoRobotClient("other");
    nrc.parse(target);

    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("%7Etest/%7Efoo.html")));
  }

  // Tests NRB-3
  // http://www.osjava.org:8080/jira/secure/ViewIssue.jspa?key=NRB-3
  @Test
  public void testNrb3() throws Exception {
    URI target = BASE_URI.resolve("basic/");
    NoRobotClient nrc = new NoRobotClient("Scabies-1.0");
    nrc.parse(target);
    Assert.assertTrue(nrc.isUrlAllowed(target.resolve("basic")));
  }

  // Tests NRB-6
  // http://issues.osjava.org/jira/secure/ViewIssue.jspa?key=NRB-6
  @Test
  public void testNrb6() throws Exception {
    URI target = BASE_URI.resolve("order/");
    NoRobotClient nrc = new NoRobotClient("Scabies-1.0");
    nrc.parse(target);
    Assert.assertTrue("Specific then Wildcard not working as expected", nrc
        .isUrlAllowed(target.resolve("order/")));

    target = BASE_URI.resolve("order-reverse/");
    nrc = new NoRobotClient("Scabies-1.0");
    nrc.parse(target);
    Assert.assertTrue("Wildcard then Specific not working as expected", nrc
        .isUrlAllowed(target.resolve("order/")));
  }      

  // Tests NRB-9
  // http://issues.osjava.org/jira/secure/ViewIssue.jspa?key=NRB-9
  @Test
  public void testNrb9() throws Exception {
    URI target = BASE_URI.resolve("disallow-empty/");
    NoRobotClient nrc = new NoRobotClient("test");
    nrc.parse(target);
    Assert.assertTrue("'Disallow: ' should mean to disallow nothing", nrc
        .isUrlAllowed(target.resolve("index.html")));
  }

  // Tests NRB-8
  // http://issues.osjava.org/jira/secure/ViewIssue.jspa?key=NRB-8
  @Test
  public void testNrb8() throws Exception {
    URI target = BASE_URI.resolve("ua-case-insensitive/");
    String[] names = new String[] {"test", "TEST", "tEsT"};
    for (int i = 0; i < names.length; i++) {
      NoRobotClient nrc = new NoRobotClient(names[i]);
      nrc.parse(target);
      Assert.assertFalse("User-Agent names should be case insensitive", nrc
          .isUrlAllowed(target.resolve("index.html")));
    }
  }

  @Test
  public void testRobotsParsing() throws Exception {
    String s = 
      "User-agent: *\r\n" +
      "Disallow: /tmp/\r\n" +
      "User-agent: BadRobot\r\n" +
      "Disallow: /cgi-bin/\r\n" +
      "Disallow: /blah/";
    Map<String, RulesEngine> map = NoRobotClient.parse(
        new ByteArrayInputStream(s.getBytes("US-ASCII")));
    Assert.assertNotNull(map);
    Assert.assertEquals(2, map.size());
    Assert.assertNotNull(map.get("*"));
    Assert.assertNotNull(map.get("badrobot"));
    Assert.assertNull(map.get("BadRobot"));
    Assert.assertNull(map.get("wnatever"));
  }
  
  @Test
  public void testComplexRobotsParsing() throws Exception {
    String s = 
      "User-agent: *\r\n" +
      "Disallow: /tmp/\r\n" +
      "User-agent: BadRobot1\r\n" +
      "User-agent: BadRobot2\r\n" +
      "User-agent: BadRobot3\r\n" +
      "Disallow: /cgi-bin/\r\n" +
      "Disallow: /blah/\r\n" +
      "User-agent: BadRobot1\r\n" +
      "Disallow: /yada/\r\n" +
      "User-agent: BadRobot3\r\n" +
      "Allow: /haha/";
    Map<String, RulesEngine> map = NoRobotClient.parse(
        new ByteArrayInputStream(s.getBytes("US-ASCII")));
    Assert.assertNotNull(map);
    Assert.assertEquals(4, map.size());
    Assert.assertNotNull(map.get("*"));
    Assert.assertNotNull(map.get("badrobot1"));
    Assert.assertNotNull(map.get("badrobot2"));
    Assert.assertNotNull(map.get("badrobot3"));
    Assert.assertNull(map.get("badrobot4"));
    Assert.assertNull(map.get("wnatever"));
    
    RulesEngine e1 = map.get("*");
    Assert.assertEquals(Boolean.FALSE, e1.isAllowed("/tmp/"));
    Assert.assertNull(e1.isAllowed("/blah/"));
    Assert.assertNull(e1.isAllowed("/yada/"));
    Assert.assertNull(e1.isAllowed("/haha/"));

    RulesEngine e2 = map.get("badrobot1");
    Assert.assertEquals(Boolean.FALSE, e2.isAllowed("/cgi-bin/"));
    Assert.assertEquals(Boolean.FALSE, e2.isAllowed("/blah/"));
    Assert.assertEquals(Boolean.FALSE, e2.isAllowed("/yada/"));
    Assert.assertNull(e2.isAllowed("/haha/"));

    RulesEngine e3 = map.get("badrobot2");
    Assert.assertEquals(Boolean.FALSE, e3.isAllowed("/cgi-bin/"));
    Assert.assertEquals(Boolean.FALSE, e3.isAllowed("/blah/"));
    Assert.assertNull(e3.isAllowed("/yada/"));
    Assert.assertNull(e3.isAllowed("/haha/"));

    RulesEngine e4 = map.get("badrobot3");
    Assert.assertEquals(Boolean.FALSE, e4.isAllowed("/cgi-bin/"));
    Assert.assertEquals(Boolean.FALSE, e4.isAllowed("/blah/"));
    Assert.assertNull(e4.isAllowed("/yada/"));
    Assert.assertEquals(Boolean.TRUE, e4.isAllowed("/haha/"));
  }
  
  @Test
  public void testSimpleRobotsCheck() throws Exception {
    String s = 
      "User-agent: *\r\n" +
      "Disallow: /cgi-bin/\r\n" +
      "Disallow: /tmp/\r\n" +
      "Disallow: /~mine/";
    NoRobotClient nrc = new NoRobotClient(new SimpleContentLoader(), "whatever");
    nrc.parseText(new ByteArrayInputStream(s.getBytes("US-ASCII")));
    Assert.assertTrue(nrc.isUrlAllowed(new URI("/whatever/")));
    Assert.assertFalse(nrc.isUrlAllowed(new URI("/~mine/")));
    Assert.assertFalse(nrc.isUrlAllowed(new URI("/tmp/")));
  }
  
}
