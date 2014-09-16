package com.rambris;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Database abstraction and support methods.
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * 
 */
public abstract class Database
{
	private final Logger log;
	protected Properties config;

	public Database(Properties config)
	{
		this.config = config;
		log = Logger.getLogger(Database.class);
	}

	protected String getDSN()
	{
		String dbhost = config.getProperty("address", config.getProperty("dbhost"));
		String dbname = config.getProperty("name", config.getProperty("dbname"));
		String dbtype = config.getProperty("type", config.getProperty("dbtype", "mysql"));

		boolean useCompression = config.containsKey("compression")
				&& "true".equalsIgnoreCase(config.getProperty("compression"));
		return config.getProperty("dsn", "jdbc:" + dbtype + "://" + dbhost + "/" + dbname
				+ (useCompression ? "?useCompression=true" : ""));
	}

	public void closeConnection(Connection conn)
	{
		if (conn != null)
		{
			try
			{
				if (!conn.getAutoCommit()) conn.rollback();
			}
			catch (SQLException e)
			{
				log.warn(e.getMessage());
			}

			try
			{
				conn.setAutoCommit(true);
			}
			catch (SQLException e)
			{
				log.warn(e.getMessage());
			}

			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				log.warn(e.getMessage());
			}
		}
	}

	public void closeStatement(Statement stmt)
	{
		if (stmt != null)
		{
			try
			{
				stmt.close();
			}
			catch (SQLException e)
			{
				log.warn(e.getMessage());
			}
		}
	}

	public void closeResultSet(ResultSet rs)
	{
		if (rs != null)
		{
			try
			{
				rs.close();
			}
			catch (SQLException e)
			{
				log.warn(e.getMessage());
			}
		}
	}

	public void closeAll(Connection conn, Statement stmt, ResultSet rs)
	{
		closeResultSet(rs);
		closeStatement(stmt);
		closeConnection(conn);
	}

	public abstract Connection getConnection() throws SQLException;

	public void close()
	{

	}

	public static java.util.Date TimestampToDate(Timestamp ts)
	{
		if (ts == null) return null;
		else return new java.util.Date(ts.getTime());
	}

	public static java.sql.Timestamp DateToTimestamp(java.util.Date date)
	{
		if (date == null) return null;
		else return new java.sql.Timestamp(date.getTime());
	}

	/**
	 * Executes a statement and retry a few times if we get transaction timeout
	 * 
	 * @param stmt
	 * @param query
	 * @return
	 * @throws SQLException
	 */
	public int executeUpdate(Statement stmt, String query) throws SQLException
	{
		int retries = Integer.parseInt(config.getProperty("retries", "5"));
		while (retries-- > 0)
		{
			try
			{
				return stmt.executeUpdate(query);
			}
			catch (SQLException e)
			{
				if (e.getErrorCode() != 1205 && e.getErrorCode() != 1213) throw e;
				if (retries == 0) throw e;
				log.warn(e.getMessage() + ". Retrying");
				try
				{
					Thread.sleep(2);
				}
				catch (InterruptedException e1)
				{

				}
			}
		}
		return 0;
	}

	/**
	 * Executes a statement and retry a few times if we get transaction timeout
	 * 
	 * @param stmt
	 * @return
	 * @throws SQLException
	 */
	public int executeUpdate(PreparedStatement stmt) throws SQLException
	{
		int retries = Integer.parseInt(config.getProperty("retries", "5"));
		while (retries-- > 0)
		{
			try
			{
				return stmt.executeUpdate();
			}
			catch (SQLException e)
			{
				if (e.getErrorCode() != 1205 && e.getErrorCode() != 1213) throw e;
				if (retries == 0) throw e;
				log.warn(e.getMessage() + ". Retrying");
				try
				{
					Thread.sleep(2);
				}
				catch (InterruptedException e1)
				{

				}
			}
		}
		return 0;
	}

	/**
	 * Executes a query and retry a few times if we get transaction timeout
	 * 
	 * @param stmt
	 * @param query
	 * @return
	 * @throws SQLException
	 */
	public ResultSet executeQuery(Statement stmt, String query) throws SQLException
	{
		int retries = Integer.parseInt(config.getProperty("retries", "5"));
		while (retries-- > 0)
		{
			try
			{
				return stmt.executeQuery(query);
			}
			catch (SQLException e)
			{
				if (e.getErrorCode() != 1205 && e.getErrorCode() != 1213) throw e;
				if (retries == 0) throw e;
				log.warn(e.getMessage() + ". Retrying");
				try
				{
					Thread.sleep(2);
				}
				catch (InterruptedException e1)
				{

				}
			}
		}
		return null;
	}

	/**
	 * Executes a query and retry a few times if we get transaction timeout
	 * 
	 * @param stmt
	 * @return
	 * @throws SQLException
	 */
	public ResultSet executeQuery(PreparedStatement stmt) throws SQLException
	{
		int retries = Integer.parseInt(config.getProperty("retries", "5"));
		while (retries-- > 0)
		{
			try
			{
				return stmt.executeQuery();
			}
			catch (SQLException e)
			{
				if (e.getErrorCode() != 1205 && e.getErrorCode() != 1213) throw e;
				if (retries == 0) throw e;
				log.warn(e.getMessage() + ". Retrying");
				try
				{
					Thread.sleep(2);
				}
				catch (InterruptedException e1)
				{

				}
			}
		}
		return null;
	}

	public static String getQuery(Statement stmt)
	{
		if (stmt == null) return null;
		String query = stmt.toString();
		return query.substring(query.indexOf(":") + 2);
	}

	public static Map<String, Integer> getColumns(ResultSet rs) throws SQLException
	{
		ResultSetMetaData meta = rs.getMetaData();
		HashMap<String, Integer> columns = new HashMap<String, Integer>();
		for (int c = 1; c <= meta.getColumnCount(); c++)
		{
			columns.put(meta.getColumnLabel(c), meta.getColumnType(c));
		}
		return columns;
	}

	public static void processError(ResultSet rs) throws SQLException
	{
		String error = null;
		try
		{
			error = rs.getString("error");
		}
		catch (SQLException e)
		{

		}
		if (error != null) throw new SQLException(error);
	}

	public static Map<String, Object> processRow(ResultSet rs) throws SQLException
	{
		ResultSetMetaData meta = rs.getMetaData();
		Map<String, Object> row = new LinkedHashMap<String, Object>();
		int numcols = meta.getColumnCount();
		int ival;
		for (int col = 1; col <= numcols; col++)
		{
			String name = meta.getColumnLabel(col).toLowerCase();
			switch (meta.getColumnType(col))
			{
				case Types.BIGINT:
					row.put(name, rs.getLong(col));
					break;
				case Types.CLOB:
				case Types.BLOB:
				case Types.BINARY:
					Blob blob = rs.getBlob(col);
					// TODO: Max 2GB due to int length. GetBinaryStream instead
					row.put(name, blob.getBytes(0l, (int) blob.length()));
					blob.free();
					break;
				case Types.BOOLEAN:
					row.put(name, rs.getBoolean(col));
					break;
				case Types.BIT:
					ival = rs.getInt(col);
					if (ival == 0) row.put(name, false);
					else row.put(name, true);
					break;
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
				case Types.VARBINARY:
					row.put(name, rs.getString(col));
					break;
				case Types.DATE:
					row.put(name, new Date(rs.getDate(col).getTime()));
					break;
				case Types.DECIMAL:
				case Types.DOUBLE:
				case Types.REAL:
					row.put(name, rs.getDouble(col));
					break;
				case Types.FLOAT:
					row.put(name, rs.getFloat(col));
					break;
				case Types.INTEGER:
				case Types.NUMERIC:
					row.put(name, rs.getInt(col));
					break;
				case Types.NULL:
					row.put(name, null);
					break;
				case Types.SMALLINT:
					row.put(name, rs.getShort(col));
					break;
				case Types.TIME:
					row.put(name, new Date(rs.getTime(col).getTime()));
					break;
				case Types.TIMESTAMP:
					Timestamp ts = rs.getTimestamp(col);
					if (ts == null) break;
					row.put(name, new Date(ts.getTime()));
					break;
				case Types.TINYINT:
					row.put(name, rs.getShort(col));
					break;
				default:
					// log.warn("Unknown fieldtype: " + name + "=" + meta.getColumnType(col));
					System.err.println("Unknown fieldtype: " + name + "=" + meta.getColumnType(col));
					break;
			}
		}
		return row;
	}

}