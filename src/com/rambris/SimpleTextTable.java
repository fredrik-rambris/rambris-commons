package com.rambris;

import java.util.Map;

/**
 * A TextTable that generate a more simple layout
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * @version $Id: SimpleTextTable.java 27 2010-05-23 18:48:54Z boost $
 * 
 */
public class SimpleTextTable extends TextTable
{
	static
	{
		vertChar = ' ';
		horizChar = '-';
		crossChar = '-';
		padChar = ' ';
	}

	public SimpleTextTable()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public SimpleTextTable(Map<String, String> columns, Map<String, String> opts)
	{
		super(columns, opts);
		// TODO Auto-generated constructor stub
	}

	public SimpleTextTable(Map<String, String> columns)
	{
		super(columns);
		// TODO Auto-generated constructor stub
	}

	public SimpleTextTable(Table table)
	{
		super(table);
		// TODO Auto-generated constructor stub
	}

}
