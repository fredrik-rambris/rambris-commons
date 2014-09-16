package com.rambris.cron;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

/**
 * Processes the contents of a zipfile much like WatchDirectoryTask processes
 * files in a directory
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * @version $Id: ZipFileProcessor.java 29 2010-05-30 14:55:16Z boost $
 */
abstract public class ZipFileProcessor implements FileProcessor
{
	FileProcessor processor;
	Logger log = Logger.getLogger(ZipFileProcessor.class);

	/**
	 * Used internally. Checks if the file inside is to be accepted.
	 * 
	 * @param entry
	 * @return
	 */
	abstract protected boolean accept(ZipEntry entry);

	/**
	 * Used internally. Process a file inside a zip archive
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	abstract protected boolean process(InputStream in) throws IOException;

	@SuppressWarnings("unchecked")
	@Override
	public boolean process(File file) throws IOException
	{
		ZipFile zip = new ZipFile(file, ZipFile.OPEN_READ);
		Enumeration entries = zip.entries();
		while (entries.hasMoreElements())
		{
			ZipEntry entry = (ZipEntry) entries.nextElement();
			if (accept(entry))
			{
				log.debug("Processing file in zip: " + entry.getName());
				if (!process(zip.getInputStream(entry))) return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File file)
	{
		return file.getPath().endsWith(".zip");
	}

}
