package com.rambris;

import java.util.Map;

/**
 * A TextTable that generate a more simple layout
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * 
 */
public class SimpleTextTable extends TextTable
{
	public SimpleTextTable()
	{
		super();
		vertChar = ' ';
		horizChar = '-';
		crossChar = '-';
		padChar = ' ';
	}

	public SimpleTextTable(Map<String, String> columns, Map<String, String> opts)
	{
		super(columns, opts);
	}

	public SimpleTextTable(Map<String, String> columns)
	{
		super(columns);
	}

	public SimpleTextTable(Table table)
	{
		super(table);
	}

}
