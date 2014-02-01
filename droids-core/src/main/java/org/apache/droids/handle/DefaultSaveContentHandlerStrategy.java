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
package org.apache.droids.handle;

import org.apache.droids.api.ContentEntity;

import java.net.URI;

/**
 * Implementation of the {@link SaveContentHandlerStrategy} that saves
 * all data in a path associated with the {@link URI} for the content.
 *
 * @version 1.0
 */
public class DefaultSaveContentHandlerStrategy
    implements SaveContentHandlerStrategy {

    private String outputDir;
    private boolean includeHost;

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculateFilePath(URI uri, ContentEntity entity) {
        String filePath = outputDir;
        if (includeHost) {
          filePath += uri.getHost() + uri.getPath();
        } else {
          filePath += uri.getPath().substring(1);
        }
        return filePath;
    }

    /**
     * @param outputDir the output directory to set
     */
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * @param includeHost if we want to include the host in the path
     */
    public void setIncludeHost(boolean includeHost) {
        this.includeHost = includeHost;
    }
}