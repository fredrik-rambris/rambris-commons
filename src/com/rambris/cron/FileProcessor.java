package com.rambris.cron;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * Process accepts a file and process it
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * @version $Id: FileProcessor.java 29 2010-05-30 14:55:16Z boost $
 */
public interface FileProcessor extends FileFilter
{
	/**
	 * Do something with the file.
	 * 
	 * @param file
	 * @return Success of processing. It may not be an error not to succeed.
	 * @throws IOException
	 */
	public boolean process(File file) throws IOException;
}
