/* EnumerationIterator.java (c) 2010 Fredrik Rambris. All rights reserved */
package com.rambris;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * @author Fredrik Rambris <fredrik@rambris.com>
 * @version $Id: EnumerationIterator.java 45 2010-09-01 16:10:39Z boost $
 * 
 */
public class EnumerationIterator<I> implements Iterator<I>, Iterable<I>
{
	private final Enumeration<I> e;

	public EnumerationIterator(Enumeration<I> e)
	{
		this.e = e;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext()
	{
		return e.hasMoreElements();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public I next()
	{
		return e.nextElement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<I> iterator()
	{
		// TODO Auto-generated method stub
		return this;
	}

}