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
package org.apache.droids.examples;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.droids.AbstractDroid;
import org.apache.droids.api.*;
import org.apache.droids.robot.walker.FileTask;
import org.apache.droids.impl.MultiThreadedTaskMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileRenameDroid extends AbstractDroid<FileTask> {

	private static final Logger LOG = LoggerFactory
			.getLogger(FileRenameDroid.class);
	private Collection<File> initialFiles;

	public FileRenameDroid(Queue<FileTask> queue, TaskMaster<FileTask> taskMaster) {
		super(queue, taskMaster);
	}

	public void setInitialFiles(Collection<File> initialFiles) {
		this.initialFiles = initialFiles;
	}

	public void init() {
		Preconditions.checkNotNull(initialFiles);
		Preconditions.checkState(!initialFiles.isEmpty());
		for (File file : initialFiles) {
			queue.add(new FileTask(file, 0));
		}
	}

	public LinkedHashMap<String, String> cleaner = null;

	public LinkedHashMap<String, String> getCleaner() {
		if (null == cleaner) {
			populateCleaner();
		}
		return cleaner;
	}

	public void setCleaner(LinkedHashMap<String, String> cleaner) {
		this.cleaner = cleaner;
	}

	private void populateCleaner() {
		cleaner = new LinkedHashMap<String, String>();
		cleaner.put(" ", ".");
		cleaner.put(".-.", ".");
		cleaner.put(",", "");
	}

	public void finished() {
		System.out.println("FINISHED!!!");
	}

	public RenameWorker getNewWorker() {
		return new RenameWorker();
	}

	public class RenameWorker implements Worker<FileTask> {

		String replace;

		public void execute(FileTask task) {

			for (String pattern : getCleaner().keySet()) {
				replace = getCleaner().get(pattern);

				cleanFileName(task.getFile(), pattern, replace);
			}
		}

		private void cleanFileName(File file, String pattern, String replace) {
			LOG.debug("Processing: " + file.getName());
			LOG.debug("finding pattern: " + pattern);
			LOG.debug("replacing it with: " + replace);
			String fileName = file.getName();
			if (fileName.contains(pattern)
					|| !fileName.toLowerCase().equals(fileName)) {
				LOG.debug("need to process this file: " + fileName + " in "
						+ file.getAbsolutePath());
				File replacement = new File(fileName.substring(0, file
						.getAbsolutePath().indexOf(fileName))
						+ fileName.replaceAll(pattern, replace).toLowerCase());
				LOG.debug("Renaming to: " + replacement.getName() + " in "
						+ replacement.getAbsolutePath());

				LOG.info("TODO! actually do the rename!");
				// TODO -- actually do the rename...file.renameTo(replacement);
			}
		}
	}

	// ------------------------------------------------------------------
	// ------------------------------------------------------------------
	public static void main(String[] args) {
		MultiThreadedTaskMaster<FileTask> taskMaster = new MultiThreadedTaskMaster<FileTask>();
		taskMaster.setPoolSize(3);

		Queue<FileTask> queue = new LinkedList<FileTask>();

		Collection<File> files = new ArrayList<File>();
		files.add(new File(args[0]));

		FileRenameDroid simple = new FileRenameDroid(queue, taskMaster);
		simple.setInitialFiles(files);
		simple.init();
		simple.start(); // TODO? perhaps start internally calls init()?
	}
}
