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
package org.apache.droids.parse;

import java.util.Collection;

import org.apache.droids.api.Link;
import org.apache.droids.api.Parse;

/**
 * Default implementation of Parse
 *
 * @version 1.0
 */
public class ParseImpl implements Parse {
    protected String text;
    protected Object data;
    protected Collection<Link> outlinks;

    public ParseImpl() {}

    public ParseImpl(String text, Collection<Link> outlinks) {
        this.text = text;
        this.outlinks = outlinks;
    }

    public ParseImpl(String text, Object data, Collection<Link> outlinks) {
        this.text = text;
        this.data = data;
        this.outlinks = outlinks;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setOutlinks(Collection<Link> outlinks) {
        this.outlinks = outlinks;
    }

	@Override
	public Collection<Link> getNewTasks() {
		return outlinks;
	}
	
	@Override
	public Collection<Link> getOutlinks() {
        return getNewTasks();
    }
	
	/**
	 * Always returns true.
	 */
	@Override
	public boolean isFollowed() {
		return true;
	}

}
