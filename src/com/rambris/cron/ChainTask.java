package com.rambris.cron;

import java.util.ArrayList;
import java.util.List;

/**
 * Runns a chain of tasks
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * @version $Id: ChainTask.java 29 2010-05-30 14:55:16Z boost $
 */
public class ChainTask extends Task
{
	private final List<Task> tasks;
	public String name = "ChainTask";

	public ChainTask()
	{
		tasks = new ArrayList<Task>();
	}

	public void addTask(Task task)
	{
		tasks.add(task);
	}

	@Override
	public void start()
	{
		for (Task task : tasks)
		{
			task.run();
		}
	}

	public List<Task> getTasks()
	{
		return tasks;
	}

	@Override
	public String toString()
	{
		return name + " (" + tasks.size() + ")";
	}
}
