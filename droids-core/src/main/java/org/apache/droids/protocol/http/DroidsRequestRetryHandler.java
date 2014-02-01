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
import java.io.InterruptedIOException;
  import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

import com.google.common.base.Preconditions;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.protocol.HttpContext;

class DroidsRequestRetryHandler implements HttpRequestRetryHandler
{

  private final int retryCount;

  public DroidsRequestRetryHandler(int retryCount)
  {
    super();
    this.retryCount = retryCount;
  }

  public DroidsRequestRetryHandler()
  {
    this(3);
  }

  @Override
  public boolean retryRequest(final IOException exception, int executionCount,
      final HttpContext context)
  {
    Preconditions.checkArgument(exception != null, "Exception parameter may not be null" );
    Preconditions.checkArgument(context != null, "HTTP context may not be null" );
    
    if (executionCount > this.retryCount) {
      // Do not retry if over max retry count
      return false;
    }
    if (exception instanceof NoHttpResponseException) {
      // Retry if the server dropped connection on us
      return true;
    }
    if (exception instanceof InterruptedIOException) {
      // Timeout
      return false;
    }
    if (exception instanceof UnknownHostException) {
      // Unknown host
      return false;
    }
    if (exception instanceof HttpHostConnectException) {
      // Connection refused
      return false;
    }
    if (exception instanceof SSLHandshakeException) {
      // SSL handshake exception
      return false;
    }
    // otherwise retry
    return true;
  }

}
