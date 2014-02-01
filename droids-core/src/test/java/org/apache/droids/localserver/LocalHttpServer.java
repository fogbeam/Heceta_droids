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
package org.apache.droids.localserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Preconditions;

import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local HTTP server for tests that require one.
 */
public class LocalHttpServer
{

  private final Logger log = LoggerFactory.getLogger(LocalHttpServer.class);
  /**
   * The local address to bind to. The host is an IP number rather than
   * "localhost" to avoid surprises on hosts that map "localhost" to an IPv6
   * address or something else. The port is 0 to let the system pick one.
   */
  public final static InetSocketAddress TEST_SERVER_ADDR = new InetSocketAddress("127.0.0.1", 0);
  /** The request handler registry. */
  private final HttpRequestHandlerRegistry handlerRegistry;
  /**
   * The HTTP processor. If the interceptors are thread safe and the list is not
   * modified during operation, the processor is thread safe.
   */
  private final BasicHttpProcessor httpProcessor;
  /** The server parameters. */
  private final HttpParams params;
  /** The server socket, while being served. */
  private volatile ServerSocket servicedSocket;
  /** The request listening thread, while listening. */
  private volatile Thread listenerThread;
  /** The number of connections this accepted. */
  private final AtomicInteger acceptedConnections = new AtomicInteger(0);

  /**
   * Creates a new test server.
   */
  public LocalHttpServer()
  {
    this.handlerRegistry = new HttpRequestHandlerRegistry();
    this.httpProcessor = new BasicHttpProcessor();
    this.httpProcessor.addInterceptor(new ResponseDate());
    this.httpProcessor.addInterceptor(new ResponseServer());
    this.httpProcessor.addInterceptor(new ResponseContent());
    this.httpProcessor.addInterceptor(new ResponseConnControl());
    this.params = new BasicHttpParams();
    this.params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000).setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024).setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false).setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true).setParameter(CoreProtocolPNames.ORIGIN_SERVER, "LocalTestServer/1.1");
  }

  /**
   * Returns the number of connections this test server has accepted.
   */
  public int getAcceptedConnectionCount()
  {
    return this.acceptedConnections.get();
  }

  /**
   * Registers a handler with the local registry.
   * 
   * @param pattern
   *          the URL pattern to match
   * @param handler
   *          the handler to apply
   */
  public void register(String pattern, HttpRequestHandler handler)
  {
    this.handlerRegistry.register(pattern, handler);
  }

  /**
   * Unregisters a handler from the local registry.
   * 
   * @param pattern
   *          the URL pattern
   */
  public void unregister(String pattern)
  {
    this.handlerRegistry.unregister(pattern);
  }

  /**
   * Starts this test server. Use {@link #getServicePort getServicePort} to
   * obtain the port number afterwards.
   */
  public void start() throws IOException
  {
    if (servicedSocket != null) {
      return; // Already running
    }

    ServerSocket ssock = new ServerSocket();
    ssock.setReuseAddress(true); // probably pointless for port '0'
    ssock.bind(TEST_SERVER_ADDR);
    this.servicedSocket = ssock;

    this.listenerThread = new Thread(new RequestListener());
    this.listenerThread.setDaemon(false);
    this.listenerThread.start();
  }

  /**
   * Stops this test server.
   */
  public void stop() throws IOException
  {
    if (this.servicedSocket == null) {
      return; // not running
    }

    try {
      this.servicedSocket.close();
    } catch (IOException ex) {
      log.error(ex.getMessage(), ex);
    } finally {
      this.servicedSocket = null;
    }

    if (this.listenerThread != null) {
      this.listenerThread.interrupt();
      this.listenerThread = null;
    }
  }

  @Override
  public String toString()
  {
    ServerSocket ssock = servicedSocket; // avoid synchronization
    StringBuffer sb = new StringBuffer(80);
    sb.append("LocalTestServer/");
    if (ssock == null) {
      sb.append("stopped");
    } else {
      sb.append(ssock.getLocalSocketAddress());
    }
    return sb.toString();
  }

  /**
   * Obtains the port this server is servicing.
   * 
   * @return the service port
   */
  public int getServicePort()
  {
    ServerSocket ssock = this.servicedSocket; // avoid synchronization
    Preconditions.checkState(ssock != null, "not running");
    return ssock.getLocalPort();
  }

  /**
   * Obtains the local address the server is listening on
   * 
   * @return the service address
   */
  public SocketAddress getServiceAddress()
  {
    ServerSocket ssock = this.servicedSocket; // avoid synchronization
    Preconditions.checkState(ssock != null, "not running");
    return ssock.getLocalSocketAddress();
  }

  /**
   * The request listener. Accepts incoming connections and launches a service
   * thread.
   */
  public class RequestListener implements Runnable
  {

    /** The workers launched from here. */
    private final Set<Thread> workerThreads;

    public RequestListener()
    {
      super();
      this.workerThreads = Collections.synchronizedSet(new HashSet<Thread>());
    }

    public void run()
    {
      try {
        while ((servicedSocket != null) && (listenerThread == Thread.currentThread())
                && !Thread.interrupted()) {
          try {
            accept();
          } catch (Exception ex) {
            ServerSocket ssock = servicedSocket;
            if ((ssock != null) && !ssock.isClosed()) {
              log.error(LocalHttpServer.this.toString() + " could not accept", ex);
            }
            // otherwise ignore the exception silently
            break;
          }
        }
      } finally {
        cleanup();
      }
    }

    protected void accept() throws IOException
    {
      // Set up HTTP connection
      Socket socket = servicedSocket.accept();
      acceptedConnections.incrementAndGet();
      DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
      conn.bind(socket, params);

      // Set up the HTTP service
      HttpService httpService = new HttpService(httpProcessor,
              new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
      httpService.setParams(params);
      httpService.setHandlerResolver(handlerRegistry);

      // Start worker thread
      Thread t = new Thread(new Worker(httpService, conn));
      workerThreads.add(t);
      t.setDaemon(true);
      t.start();

    }

    protected void cleanup()
    {
      Thread[] threads = workerThreads.toArray(new Thread[0]);
      for (int i = 0; i < threads.length; i++) {
        if (threads[i] != null) {
          threads[i].interrupt();
        }
      }
    }

    /**
     * A worker for serving incoming requests.
     */
    public class Worker implements Runnable
    {

      private final HttpService httpservice;
      private final HttpServerConnection conn;

      public Worker(final HttpService httpservice, final HttpServerConnection conn)
      {

        this.httpservice = httpservice;
        this.conn = conn;
      }

      public void run()
      {
        HttpContext context = new BasicHttpContext(null);
        try {
          while ((servicedSocket != null) && this.conn.isOpen() && !Thread.interrupted()) {
            this.httpservice.handleRequest(this.conn, context);
          }
        } catch (IOException ex) {
          // ignore silently
        } catch (HttpException ex) {
          // ignore silently
        } finally {
          workerThreads.remove(Thread.currentThread());
          try {
            this.conn.shutdown();
          } catch (IOException ignore) {
          }
        }
      }
    }
  }
}
