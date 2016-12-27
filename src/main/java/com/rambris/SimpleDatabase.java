package com.rambris;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * A simple implementation of Database that returns a real connection each time.
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * 
 */
public class SimpleDatabase extends Database
{
	private final Logger log;

	public SimpleDatabase(Properties config) throws SQLException
	{
		super(config);
		log = Logger.getLogger(SimpleDatabase.class);
		String dbdriver = config.getProperty("driver", config.getProperty("dbdriver", "com.mysql.jdbc.Driver"));

		try
		{
			Class.forName(dbdriver).newInstance();
		}
		catch (ClassNotFoundException e)
		{
			log.fatal("JDBC Driver not found: " + dbdriver);
			throw new SQLException(e.getMessage(), e);
		}
		catch (InstantiationException e)
		{
			throw new SQLException(e.getMessage(), e);
		}
		catch (IllegalAccessException e)
		{
			throw new SQLException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rambris.DatabaseInterface#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException
	{
		Connection conn = null;

		String dbuser = config.getProperty("username", config.getProperty("dbuser"));
		String dbpass = config.getProperty("password", config.getProperty("dbpass"));
		String dsn = getDSN();

		try
		{
			return conn = DriverManager.getConnection(dsn, dbuser, dbpass);
		}
		catch (SQLException e)
		{
			closeConnection(conn);
			throw e;
		}

	}
}
