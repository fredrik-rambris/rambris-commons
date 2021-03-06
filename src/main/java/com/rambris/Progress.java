package com.rambris;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Calculates progress, ETA etc.
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * 
 */
public class Progress
{
	private double units = 1;
	private String timeUnit = "s";
	private double current;
	private final double total;
	private double last;
	private long before;
	private boolean done=false;

	private double unitsPerTimeUnit;
	private final Average average;
	
	private short decimals=1;

	class Average
	{
		double[] values;
		int pos = 0, num = 0;

		public Average(int capacity)
		{
			values = new double[capacity];
		}

		public void set(double value)
		{
			values[pos] = value;
			pos = (pos + 1) % values.length;
			if (num < values.length) num++;
		}

		public double get()
		{
			if (num == 0) return 0;
			double average = 0;
			int lpos = 0;
			while (lpos < num)
			{
				average += values[lpos];
				lpos++;
			}
			return average / num;
		}
	}

	public Progress(TimeUnit unit)
	{
		this(0, 0, unit);
	}

	public Progress(double begin, double total, TimeUnit unit)
	{
		before = System.currentTimeMillis();
		last = current = begin;
		this.total = total;
		switch (unit)
		{
			case SECONDS:
				units = 1;
				timeUnit = "s";
				break;
			case MINUTES:
				units = 60;
				timeUnit = "m";
				break;
			case HOURS:
				units = 3600;
				timeUnit = "h";
				break;
		}
		average = new Average(5);
	}

	public double set(double value)
	{
		long now = System.currentTimeMillis();
		double timeSpent = (now - before) / 1000.0 / units;

		double unitsProgressed = value - current;
		unitsPerTimeUnit = unitsProgressed / timeSpent;
		before = now;
		last = current;
		current = value;
		average.set(unitsPerTimeUnit);
		return current - last;
	}
	


	public Date getETA()
	{
		double left = total - current;
		return new Date((long) (before + (left / average.get() * units * 1000.0)));
	}

	public String getThroughput()
	{
		return String.format("%."+decimals+"f/%s", average.get(), timeUnit);
	}

	public double getAverage()
	{
		return average.get();
	}

	@Override
	public String toString()
	{
		if (total != 0)
		{
			return String.format("%."+decimals+"f/%."+decimals+"f, %."+decimals+"f/%s", current, total, average.get(), timeUnit);
		}
		else
		{
			return String.format("%."+decimals+"f, %."+decimals+"f/%s", current, average.get(), timeUnit);
		}
	}

	public short getPercent()
	{
		return (short) Math.round(100.0 * current / total);
	}

	public String getTimeSpent()
	{
		long now = System.currentTimeMillis();
		double timeSpent = (now - before) / 1000.0 / units;
		return String.format("%."+decimals+"f%s", timeSpent, timeUnit);
	}

	public long getSecondsSpent()
	{
		return Math.round((System.currentTimeMillis() - before) / 1000.0);
	}

	public double getCurrent()
	{
		return current;
	}
	
	/**
	 * When were we last updated
	 * @return
	 */
	public long getWhenUpdated()
	{
		return before;
	}
	
	public int getAge()
	{
		return Math.round((System.currentTimeMillis()-before)/1000.0f);
	}

	public boolean isDone()
	{
		return done;
	}

	public void setDone(boolean done)
	{
		this.done = done;
	}

	/**
	 * @return the number of decimals shown
	 */
	public short getDecimals()
	{
		return decimals;
	}

	/**
	 * @param decimals the number of decimals to show
	 */
	public void setDecimals(int decimals)
	{
		this.decimals = (short)decimals;
	}
}
