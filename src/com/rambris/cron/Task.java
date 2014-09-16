package com.rambris.cron;

import java.util.Date;

/**
 * Our own runnables with extra features
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * @version $Id: Task.java 29 2010-05-30 14:55:16Z boost $
 */
public abstract class Task implements Runnable
{
	private boolean active = true;
	private Date lastRun = null;
	private long lastDuration = 0;
	private boolean running = false;

	public boolean isActive()
	{
		return active;
	}

	public boolean setActive(boolean newState)
	{
		boolean oldState = active;
		active = newState;
		return oldState;
	}

	/**
	 * Tasks should implement start as a run method to enable this superclass to
	 * do cool stuff before and after
	 */
	public abstract void start();

	/**
	 * Tracks the state of this job. Also allows to turn disable job by setting
	 * active to false
	 * 
	 * @param force
	 *            run the job even if it's not active.
	 */
	public synchronized final void run(boolean force)
	{
		if (!active && !force) return;
		running = true;
		lastRun = new Date();
		this.start();
		lastDuration = System.currentTimeMillis() - lastRun.getTime();
		running = false;
	}

	public final void run()
	{
		this.run(false);
	}

	public Date getLastRun()
	{
		return lastRun;
	}

	public long getLastDuration()
	{
		return lastDuration;
	}

	public boolean isRunning()
	{
		return running;
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName();
	}
}
