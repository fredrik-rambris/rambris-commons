package com.rambris;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * Database handling, but with a connection pool instead of getting a new
 * connection every time.
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * 
 */
public class DatabasePool extends Database
{
	private BasicDataSource ds = null;
	private final int attempts;

	/**
	 * @param config
	 * @throws SQLException
	 */
	public DatabasePool(Properties config) throws SQLException
	{
		super(config);

		String dbdriver = config.getProperty("driver", config.getProperty("dbdriver", "com.mysql.jdbc.Driver"));
		String dbuser = config.getProperty("username", config.getProperty("dbuser"));
		String dbpass = config.getProperty("password", config.getProperty("dbpass"));
		String dsn = getDSN();

		ds = new BasicDataSource();
		ds.setDriverClassName(dbdriver);
		ds.setUsername(dbuser);
		ds.setPassword(dbpass);
		ds.setUrl(dsn);
		ds.setValidationQuery("SELECT 1");

		attempts = (int) Util.Long(config.getProperty("connect_retries", config.getProperty("retries")));

	}

	/**
	 * Sometimes we just want the underlying DataSource and this is a convenient
	 * way to get it.
	 * 
	 * @param config
	 * @return
	 * @throws SQLException
	 */
	public static DataSource GetDataSource(Properties config) throws SQLException
	{
		return new DatabasePool(config).getDataSource();
	}

	@Override
	public Connection getConnection() throws SQLException
	{
		int ourAttempts;
		ourAttempts = attempts;
		Connection conn = null;
		while ((conn = ds.getConnection()) == null)
		{
			ourAttempts--;
			if (ourAttempts < 1) throw new SQLException("Could not get connection");
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				throw new SQLException("Could not get connection.", e);
			}
		}
		return conn;
	}

	/**
	 * Return the underlying datasource.
	 * 
	 * @return
	 */
	public DataSource getDataSource()
	{
		return ds;
	}
}
