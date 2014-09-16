package com.rambris;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Generate text representations of tabular data. Abstract base class
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * @version $Id: Table.java 27 2010-05-23 18:48:54Z boost $
 * 
 */
public abstract class Table
{
	protected Map<String, String> columns = new LinkedHashMap<String, String>();
	protected Vector<Map<String, String>> rows = new Vector<Map<String, String>>();
	protected Map<String, String> opts = new HashMap<String, String>();

	public Table(Map<String, String> columns)
	{
		this.columns = columns;
	}

	public Table(Map<String, String> columns, Map<String, String> opts)
	{
		this(columns);
		this.opts = opts;
	}

	public Table()
	{

	}

	public Table(Table table)
	{
		this.columns = table.columns;
		this.rows = table.rows;
		this.opts = table.opts;
	}

	public void addColumn(String key, String title)
	{
		columns.put(key, title);
	}

	public void addRow(Map<String, String> row)
	{
		rows.add(row);
	}

	public void setOpt(String option, String value)
	{
		opts.put(option, value);
	}

	public void clearOpts()
	{
		opts.clear();
	}

	public abstract void print(PrintWriter out);

	@Override
	public String toString()
	{
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		print(out);
		return str.toString();
	}

	public void loadResultSet(ResultSet rs) throws SQLException
	{
		ResultSetMetaData meta = rs.getMetaData();
		if (columns.size() != meta.getColumnCount())
		{
			columns.clear();
			for (int col = 1; col <= meta.getColumnCount(); col++)
			{
				addColumn(meta.getColumnLabel(col), meta.getColumnLabel(col));
			}
		}
		while (rs.next())
		{
			Map<String, String> row = new HashMap<String, String>();
			for (int col = 1; col <= meta.getColumnCount(); col++)
			{
				row.put(meta.getColumnLabel(col), rs.getString(col));
			}
			addRow(row);
		}
	}
}
