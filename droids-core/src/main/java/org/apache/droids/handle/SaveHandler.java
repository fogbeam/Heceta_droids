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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.droids.api.ContentEntity;
import org.apache.droids.api.Handler;
import org.apache.droids.helper.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler which is writing the stream to the file system.
 * <p>
 * Before using make sure you have set the {@link SaveContentHandlerStrategy}.
 * 
 * @version 1.0
 * 
 */
public class SaveHandler extends WriterHandler implements Handler {

	private final Logger log = LoggerFactory.getLogger(SaveHandler.class);

	private URI uri;

	private SaveContentHandlerStrategy saveContentHandlerStrategy;

	public SaveHandler() {
		super();
	}

	/**
	 * Handle saving content.
	 * 
	 * @param uri
	 *            the uri we are currently processing
	 * @param entity
	 *            the entity to save
	 * 
	 * @throws IOException
	 *             on error
	 */
	public void handle(URI uri, ContentEntity entity) throws IOException {
		this.uri = uri;
		InputStream instream = entity.obtainContent();
		String path = saveContentHandlerStrategy.calculateFilePath(uri, entity);
		try {
			writeOutput(path, instream);
		} finally {
			instream.close();
		}
	}

	/**
	 * Write the output.
	 * 
	 * @param path
	 *            the path to write to
	 * @param stream
	 *            the stream
	 * @throws IOException
	 *             on error
	 */
	private void writeOutput(String path, InputStream stream)
			throws IOException {
		if (!uri.getPath().endsWith("/")) {
			log.info("Trying to save " + uri + " to " + path);
			File cache = new File(path);
			FileUtil.createFile(cache);

			writeContentToFile(stream, cache);
		}
	}

	/**
	 * Write contents to a file.
	 * 
	 * @param stream
	 *            the stream
	 * @param cache
	 *            the file
	 * @throws FileNotFoundException
	 *             on file not found
	 * @throws IOException
	 *             on error
	 */
	private void writeContentToFile(InputStream stream, File cache)
			throws FileNotFoundException, IOException {
		OutputStream output = null;
		final int bufferSize = 8192;
		byte[] buffer = new byte[bufferSize];
		int length = -1;
		try {
			output = new BufferedOutputStream(new FileOutputStream(cache));
			while ((length = stream.read(buffer)) > -1) {
				output.write(buffer, 0, length);
			}
		} finally {
			if (null != output) {
				output.flush();
				output.close();
			}
		}
	}

	/**
	 * 
	 * @return the {@link SaveContentHandlerStrategy}.
	 * 
	 */
	public SaveContentHandlerStrategy getSaveContentHandlerStrategy() {
		return saveContentHandlerStrategy;
	}

	/**
	 * 
	 * @param saveContentHandlerStrategy
	 *            the {@link SaveContentHandlerStrategy} to set.
	 */
	public void setSaveContentHandlerStrategy(
			SaveContentHandlerStrategy saveContentHandlerStrategy) {
		this.saveContentHandlerStrategy = saveContentHandlerStrategy;
	}

}
