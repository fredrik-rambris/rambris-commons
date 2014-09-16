package com.rambris;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Java implementation of tail --follow
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * @version $Id: Follower.java 27 2010-05-23 18:48:54Z boost $
 * 
 */
public class Follower extends Thread
{
	private final File file;
	private boolean following = true;
	private final boolean startFromBeginning;
	private final LinkedBlockingDeque<String> queue;
	private final int sampleInterval;

	public Follower(File file, boolean beginning, int sampleInterval, int queueSize)
	{
		this.file = file;
		queue = new LinkedBlockingDeque<String>(queueSize);
		this.sampleInterval = sampleInterval;
		this.startFromBeginning = beginning;
		this.setDaemon(true);
		start();
	}

	public Follower(File file, boolean beginning)
	{
		this(file, beginning, 250, 10);
	}

	public Follower(File file)
	{
		this(file, false);
	}

	public String readLine()
	{
		try
		{
			return queue.takeLast();
		}
		catch (InterruptedException e)
		{
			return null;
		}
	}

	@Override
	public void run()
	{
		RandomAccessFile fileHandle = null;
		try
		{
			fileHandle = new RandomAccessFile(file, "r");
			long filePointer;

			if (startFromBeginning)
			{
				filePointer = 0;
			}
			else
			{
				filePointer = this.file.length();
			}

			while (following)
			{
				if (file.exists())
				{
					try
					{
						// Compare the length of the file to the file pointer
						long fileLength = this.file.length();
						if (fileLength < filePointer)
						{
							// Log file must have been rotated or deleted;
							// reopen the file and reset the file pointer
							fileHandle = new RandomAccessFile(file, "r");
							filePointer = 0;
						}

						if (fileLength > filePointer)
						{
							// There is data to read
							fileHandle.seek(filePointer);
							try
							{
								String line = fileHandle.readLine();
								while (line != null)
								{
									queue.put(line);
									line = fileHandle.readLine();
								}
							}
							catch (EOFException e)
							{

							}
							filePointer = fileHandle.getFilePointer();
						}

					}
					catch (IOException e)
					{
					}
				}
				// Sleep for the specified interval
				sleep(sampleInterval);
			}
		}
		catch (IOException e)
		{
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (fileHandle != null)
				{
					fileHandle.close();
					fileHandle = null;
				}
			}
			catch (IOException e)
			{

			}
		}
	}

	public void close()
	{
		if (this.following)
		{
			this.following = false;
			try
			{
				this.join(sampleInterval * 2);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
