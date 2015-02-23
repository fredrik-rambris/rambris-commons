package com.rambris.cron;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Watch a directory and process the files in it
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 */
public class WatchDirectoryTask extends Task
{
	protected File directory;
	protected FileProcessor processor;
	protected FileProcessor preprocessor = null;
	protected FileProcessor postprocessor = null;
	protected Logger log;

	public WatchDirectoryTask(File directory, FileProcessor processor) throws IOException
	{
		if (!directory.isDirectory()) throw new IOException("'" + directory.getPath() + "' is not a directory");
		if (!directory.canRead()) throw new IOException("'" + directory.getPath() + "' is not readable");
		if (!directory.canExecute()) throw new IOException("'" + directory.getPath() + "' is not listable");

		this.directory = directory;
		this.processor = processor;
		log = Logger.getLogger(this.getClass());
		log.debug("Watching directory: " + this.directory.getPath());
	}

	public WatchDirectoryTask(File directory, FileProcessor processor, FileProcessor preprocessor, FileProcessor postprocessor) throws IOException
	{
		this(directory, processor);
		this.preprocessor = preprocessor;
		this.postprocessor = postprocessor;
	}

	public FileProcessor getProcessor()
	{
		return processor;
	}

	public FileProcessor getPreProcessor()
	{
		return preprocessor;
	}

	public FileProcessor getPostProcessor()
	{
		return postprocessor;
	}
	
	protected void processFile(File file)
	{
		try
		{
			if (preprocessor != null)
			{
				log.debug("Pre-Processing file: " + file.getAbsolutePath());
				if (!preprocessor.process(file)) return;
			}
			log.info("Processing file: " + file.getAbsolutePath());
			if (!processor.process(file)) return;
			if (postprocessor != null)
			{
				log.debug("Post-Processing file: " + file.getAbsolutePath());
				if (!postprocessor.process(file)) return;
			}
		}
		catch (Exception e)
		{
			log.error("Could not process file: " + file.getName(), e);
		}		
	}

	@Override
	public void start()
	{
		log.debug("Checking directory: " + directory.getPath());
		for (File file : directory.listFiles(processor))
		{
			log.debug("Found file: " + file.getPath());
			processFile(file);
		}
	}

}
