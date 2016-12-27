package com.rambris.cron;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Runs a chain of processors. If runAll is true it keeps running
 * as long as it is successful, if one returns false, the process
 * stops and returns false.
 * If runAll is false then it will run the first one that accepts
 * and return its status.
 * @author Fredrik Rambris <fredrik.rambris@it.cdon.com>
 *
 */
public class ChainFileProcessor implements FileProcessor
{
	protected boolean runAll;
	List<FileProcessor>processors=new LinkedList<FileProcessor>();

	/**
	 * 
	 * @param runAll If True run all processors that accepts, not just the first
	 */
	public ChainFileProcessor(boolean runAll)
	{
		this.runAll=runAll;
	}

	@Override
	public boolean accept(File pathName)
	{
		for(FileProcessor p:processors)
		{
			if(p.accept(pathName)) return true;
		}
		return false;
	}

	@Override
	public boolean process(File file) throws IOException
	{
		for(FileProcessor p:processors)
		{
			if(p.accept(file))
			{
				if(!runAll)	return p.process(file);
				else if(!p.process(file)) return false;
			}
		}
		return true;
	}
	
	public void addProcessor(FileProcessor processor)
	{
		processors.add(processor);
	}
	

}
