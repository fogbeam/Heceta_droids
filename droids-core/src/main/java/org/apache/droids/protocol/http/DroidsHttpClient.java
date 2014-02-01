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

import java.net.ProxySelector;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.client.protocol.RequestProxyAuthentication;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultProxyAuthenticationHandler;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.client.DefaultUserTokenHandler;
import org.apache.http.impl.conn.DefaultHttpRoutePlanner;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;

/**
 * {@link HttpClient} implementation optimized specifically for web crawling.
 * This HTTP agent has no support for HTTP state management and authentication
 * and is expected to be used for retrieving information from publicly 
 * accessible sites using stateless, idempotent HTTP methods. 
 */
public class DroidsHttpClient extends AbstractHttpClient
{

  public static final String MAX_BODY_LENGTH = "droids.http..max-body-length";
  
  public DroidsHttpClient()
  {
    super(null, null);
  }

  public DroidsHttpClient(HttpParams params)
  {
    super(null, params);
  }

  @Override
  protected HttpParams createHttpParams()
  {
    HttpParams params = new BasicHttpParams();
    params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
    params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.DEFAULT_CONTENT_CHARSET);
    params.setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
    params.setParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
    params.setIntParameter(CoreConnectionPNames.MAX_HEADER_COUNT, 256);
    params.setIntParameter(CoreConnectionPNames.MAX_LINE_LENGTH, 5 * 1024);
    params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
    params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
    params.setParameter(CoreConnectionPNames.TCP_NODELAY, false);
    //params.setLongParameter(MAX_BODY_LENGTH, 512 * 1024);
    return params;
  }

  @Override
  protected BasicHttpProcessor createHttpProcessor()
  {
    BasicHttpProcessor httpproc = new BasicHttpProcessor();
    httpproc.addInterceptor(new RequestDefaultHeaders());
    // Required protocol interceptors
    httpproc.addInterceptor(new RequestContent());
    httpproc.addInterceptor(new RequestTargetHost());
    // Recommended protocol interceptors
    httpproc.addInterceptor(new RequestConnControl());
    httpproc.addInterceptor(new RequestUserAgent());
    httpproc.addInterceptor(new RequestExpectContinue());
    // HTTP authentication interceptors
    httpproc.addInterceptor(new RequestProxyAuthentication());
    return httpproc;
  }

  @Override
  protected ClientConnectionManager createClientConnectionManager()
  {
    SchemeRegistry schemeRegistry = new SchemeRegistry();
    schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
    schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
    return new DroidsHttpConnectionManager(getParams(), schemeRegistry);
  }

  @Override
  protected AuthSchemeRegistry createAuthSchemeRegistry()
  {
    AuthSchemeRegistry registry = new AuthSchemeRegistry(); 
    registry.register(
            AuthPolicy.BASIC, 
            new BasicSchemeFactory());
    registry.register(
            AuthPolicy.DIGEST, 
            new DigestSchemeFactory());
    return registry;
  }

  @Override
  protected CookieSpecRegistry createCookieSpecRegistry()
  {
    // Return empty cookie scheme registry. There'll be no cookie support
    return new CookieSpecRegistry();
  }


  @Override
  protected HttpContext createHttpContext()
  {
    HttpContext context = new BasicHttpContext();
    context.setAttribute(
            ClientContext.AUTHSCHEME_REGISTRY, 
            getAuthSchemes());
    context.setAttribute(
            ClientContext.CREDS_PROVIDER, 
            getCredentialsProvider());
    return context;
  }

  @Override
  protected HttpRequestRetryHandler createHttpRequestRetryHandler()
  {
    return new DroidsRequestRetryHandler();
  }

  /**
   * Added ProxySelectorRoutePlanner to support JVM Proxy Settings
   */
  @Override
  protected HttpRoutePlanner createHttpRoutePlanner()
  {
    return new ProxySelectorRoutePlanner(
	        this.getConnectionManager().getSchemeRegistry(),
	        ProxySelector.getDefault());  
  }

  @Override
  protected AuthenticationHandler createTargetAuthenticationHandler()
  {
    return new NoAuthHandler();
  }

}
