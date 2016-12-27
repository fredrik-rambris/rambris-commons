package com.rambris.cron;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;




public class ParallelWatchDirectoryTask extends WatchDirectoryTask
{
	protected ThreadPoolExecutor executor;
	protected BlockingQueue<Runnable> queue;
	private Logger log=Logger.getLogger(ParallelWatchDirectoryTask.class);

	
	
	private void init(int threads)
	{
		queue=new LinkedBlockingQueue<Runnable>();
		executor=new ThreadPoolExecutor(threads, threads, 10, TimeUnit.SECONDS, queue);
	}

	public ParallelWatchDirectoryTask(int threads, File directory, FileProcessor processor, FileProcessor preprocessor, FileProcessor postprocessor) throws IOException
	{
		super(directory, processor, preprocessor, postprocessor);
		init(threads);
	}

	public ParallelWatchDirectoryTask(int threads, File directory, FileProcessor processor) throws IOException
	{
		super(directory, processor);
		init(threads);
	}
	
	@Override
	public void start()
	{
		log.debug("Checking directory: " + directory.getPath());
		ArrayList<Future<?>>runners=new ArrayList<Future<?>>();
		for (File file : directory.listFiles(processor))
		{
			log.debug("Found file: " + file.getPath());	
			runners.add(executor.submit(new ProcessRunner(file)));
		}
		log.debug("Waiting for files to be processed");
		while(runners.size()>0)
		{
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				log.info(e.getMessage());
			}

			for(Iterator<Future<?>> iter=runners.iterator();iter.hasNext();)
			{
				Future<?>future=iter.next();
				if(future.isDone()) iter.remove();
			}
		}
		executor.shutdown();
	}
	
	private class ProcessRunner implements Runnable
	{
		File file;
		public ProcessRunner(File file)
		{
			this.file=file;
		}
		@Override
		public void run()
		{
			ParallelWatchDirectoryTask.this.processFile(file);
			
		}
	}
	

}
