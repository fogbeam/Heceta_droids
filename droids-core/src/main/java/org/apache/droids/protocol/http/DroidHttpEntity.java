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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.base.Preconditions;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.util.ByteArrayBuffer;

class DroidHttpEntity extends HttpEntityWrapper
{
  private final byte[] buffer;

  public DroidHttpEntity(final HttpEntity entity, long maxlen) throws IOException
  {
    super(entity);
    if (!entity.isRepeatable() || entity.getContentLength() < 0)
    {
      InputStream instream = entity.getContent();
      ByteArrayBuffer buf = new ByteArrayBuffer(4096);
      try
      {
        byte[] tmp = new byte[4096];
        long total = 0;
        int l;
        while ((l = instream.read(tmp)) != -1)
        {
          buf.append(tmp, 0, l);
          total += l;
          if (maxlen > 0 && total >= maxlen) {
            throw new ContentTooLongException("Content length exceeds " + maxlen + " byte limit");
          }
        }
        this.buffer = buf.toByteArray();
      } finally
      {
        instream.close();
      }
    } else
    {
      this.buffer = null;
    }
  }

  public long getContentLength()
  {
    if (this.buffer != null)
    {
      return this.buffer.length;
    } else
    {
      return wrappedEntity.getContentLength();
    }
  }

  public InputStream getContent() throws IOException
  {
    if (this.buffer != null)
    {
      return new ByteArrayInputStream(this.buffer);
    } else
    {
      return wrappedEntity.getContent();
    }
  }

  public boolean isChunked()
  {
    return (buffer == null) && wrappedEntity.isChunked();
  }

  public boolean isRepeatable()
  {
    return true;
  }

  public void writeTo(final OutputStream outstream) throws IOException
  {
    Preconditions.checkArgument( outstream != null, "Output stream may not be null" );
    if (this.buffer != null)
    {
      outstream.write(this.buffer);
    } else
    {
      wrappedEntity.writeTo(outstream);
    }
  }

  public boolean isStreaming()
  {
    return (buffer == null) && wrappedEntity.isStreaming();
  }

}
