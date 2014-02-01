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
package org.apache.droids.solr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.droids.api.ContentEntity;
import org.apache.droids.api.Link;
import org.apache.droids.api.Parse;
import org.apache.droids.parse.ParseImpl;

public class MockContentEntity implements ContentEntity {

	private String text;
	
	private String charset = "UTF-8";
	
	private String mimeType = "text/html";
	
	private Collection<Link> outlinks = new ArrayList<Link>();

	public Collection<Link> getOutlinks() {
		return outlinks;
	}

	public void setOutlinks(Collection<Link> outlinks) {
		this.outlinks = outlinks;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	@Override
	public String getMimeType() {
		return mimeType;
	}
	
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public Parse getParse() {
		return new ParseImpl(text, outlinks);
	}

	@Override
	public InputStream obtainContent() throws IOException {
		return new ByteArrayInputStream(text.getBytes());
	}

}
