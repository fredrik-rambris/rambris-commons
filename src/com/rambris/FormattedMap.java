/* FormattedMap.java (c) 2014 Fredrik Rambris. All rights reserved */
package com.rambris;

import java.util.HashMap;


/**
 * Implements a put method where the value is formatted with String.format
 * @author Fredrik Rambris <fredrik@rambris.com>
 *
 */
public class FormattedMap extends HashMap<String, String>
{
	private final String format;
	public FormattedMap(String format)
	{
		this.format=format;
	}

	public String put(String key, Object value)
	{
		return super.put(key, String.format(format,value));
	}
	
	
}
