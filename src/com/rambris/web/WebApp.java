/* WebApp.java (c) 2014 Fredrik Rambris. All rights reserved */
package com.rambris.web;

import com.rambris.Config;
import com.rambris.Database;


/**
 * The base application must implement this interface
 * @author Fredrik Rambris <fredrik@rambris.com>
 *
 */
public interface WebApp
{
	public Config getConfig();
	public Database getDatabase();
	public String getVersion();
}
