package com.rambris.cron;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

/**
 * All asynchronous tasks gets run by this class
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 */
public class Cron extends ScheduledThreadPoolExecutor
{
	private final Configuration config;

	private final HashMap<String, Task> cronjobs;
	private final Logger log = Logger.getLogger(Cron.class);

	public Cron(Configuration config)
	{
		super(config.getInt("cron.threads", 2));
		this.config = config;
		log.info("Starting cron with " + config.getInt("cron.threads", 2) + " threads");
		cronjobs = new HashMap<String, Task>();
	}

	public Map<String, Boolean> getActive()
	{
		Map<String, Boolean> active = new HashMap<String, Boolean>();
		for (Map.Entry<String, Task> e : cronjobs.entrySet())
		{
			active.put(e.getKey(), e.getValue().isActive());
		}
		return active;
	}

	public Map<String, Task> getJobs()
	{
		return cronjobs;
	}

	public boolean setActive(String name, boolean newState)
	{
		Task job = getCronJob(name);
		if (job != null)
		{
			log.debug((newState ? "Activating" : "Deactivating") + " " + name);
			return job.setActive(newState);
		}
		else
		{
			log.debug("Job " + name + " not found");
		}
		return false;
	}

	public Task getCronJob(String name)
	{
		return cronjobs.get(name);
	}

	public void runCronJob(String name) throws Exception
	{
		Task job = cronjobs.get(name);
		if (job == null) throw new Exception("Cronjob " + name + " does not exist");
		job.run(true);
	}

	/**
	 * Schedule a job to be run each day at a specific time
	 * 
	 * @param job
	 *            runnable to run
	 * @param start
	 *            time of day when to run the job
	 */
	public void scheduleDaily(Task job, Date start)
	{
		long now = new Date().getTime();
		long then = start.getTime();
		long day = 3600000L * 24L;
		then = then % day;
		now = now % day;
		if (then < now) then += day;
		DateFormat df = DateFormat.getTimeInstance();
		log.info("Scheduling " + job + " to be run daily at " + df.format(start));
		scheduleAtFixedRate(job, then - now, day, TimeUnit.MILLISECONDS);
	}

	public void scheduleDaily(Task job, String start) throws ParseException
	{
		DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM, new Locale("sv"));
		scheduleDaily(job, df.parse(start));
	}

	public void addCronjob(String name, Task task)
	{
		cronjobs.put(name, task);
	}
}
