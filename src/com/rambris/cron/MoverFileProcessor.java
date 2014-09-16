package com.rambris.cron;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Moves the file to destination directory
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 */
public class MoverFileProcessor implements FileProcessor
{
	protected String destination;
	protected Logger log;

	/**
	 * 
	 * @param destination_directory
	 *            Where to put the files
	 * @throws IOException
	 */
	public MoverFileProcessor(File destination_directory) throws IOException
	{
		if (!destination_directory.isDirectory()) throw new IOException("'" + destination_directory.getPath() + "' is not a directory");
		if (!destination_directory.canWrite()) throw new IOException("'" + destination_directory.getPath() + "' is not writable");
		this.destination = destination_directory.getPath();
		log = Logger.getLogger(this.getClass());
	}

	/*
	 * Move files to destination dir
	 * 
	 * @see com.rambris.cron.FileProcessor#process(java.io.File)
	 */
	@Override
	public boolean process(File file) throws IOException
	{
		File destination_file = new File(destination + File.separator + file.getName());
		log.debug("Moving '" + file.getAbsolutePath() + "' to '" + destination_file + "'");
		return file.renameTo(destination_file);
	}

	/*
	 * Always accept
	 * 
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File pathname)
	{
		return true;
	}

}
