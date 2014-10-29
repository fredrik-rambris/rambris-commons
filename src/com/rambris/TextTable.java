package com.rambris;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generates a plain text layout of a table, much like the mysql command line
 * client.
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * 
 */
public class TextTable extends Table
{
	protected char vertChar = '|';
	protected char horizChar = '-';
	protected char crossChar = '+';
	protected char padChar = ' ';

	public TextTable()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public TextTable(Map<String, String> columns, Map<String, String> opts)
	{
		super(columns, opts);
		// TODO Auto-generated constructor stub
	}

	public TextTable(Map<String, String> columns)
	{
		super(columns);
		// TODO Auto-generated constructor stub
	}

	public TextTable(Table table)
	{
		super(table);
		// TODO Auto-generated constructor stub
	}

	public Map<String, Integer> calcLengths()
	{
		Map<String, Integer> lengths = new LinkedHashMap<String, Integer>();
		if(isRowNumbers()) lengths.put("#", Integer.toString(rows.size()).length());
		for (Map.Entry<String, String> col : columns.entrySet())
		{
			lengths.put(col.getKey(), col.getValue().length());
		}
		for (Map<String, String> row : rows)
		{
			for (Map.Entry<String, String> col : row.entrySet())
			{
				if (lengths.containsKey(col.getKey()))
				{
					lengths.put(col.getKey(), Math.max(lengths.get(col.getKey()), col.getValue().length()));
				}
			}
		}
		return lengths;
	}

	protected void strRepeat(Writer w, char c, int num)
	{
		try
		{
			while (num-- > 0)
			{
				w.write(c);
			}
		}
		catch (IOException e)
		{

		}
	}

	private void line(PrintWriter out, Map<String, Integer> lengths)
	{
		out.print(crossChar);
		for (Map.Entry<String, Integer> col : lengths.entrySet())
		{
			strRepeat(out, horizChar, col.getValue() + 2);
			out.print(crossChar);
		}
		out.println();
	}

	@Override
	public void print(PrintWriter out)
	{
		Map<String, Integer> lengths = calcLengths();
		int rep;
		int rowno=1;

		line(out, lengths);
		out.print(vertChar);
		if(isRowNumbers())
		{
			int length = lengths.get("#");
			out.print(padChar + "#" + padChar);
			strRepeat(out, padChar, length - 1);
			out.print(vertChar);
		}

		for (Map.Entry<String, String> col : columns.entrySet())
		{
			int length = lengths.get(col.getKey());
			out.print(padChar + col.getValue() + padChar);
			strRepeat(out, padChar, length - col.getValue().length());
			out.print(vertChar);
		}
		out.println();
		line(out, lengths);
		for (Map<String, String> row : rows)
		{
			out.print(vertChar);

			if(isRowNumbers())
			{
				int length = lengths.get("#");
				String value = String.format("%"+length+"d", rowno);
				out.print(padChar + value + padChar);
				strRepeat(out, padChar, length - value.length());
				out.print(vertChar);
			}
			
			for (Map.Entry<String, String> col : columns.entrySet())
			{
				int length = lengths.get(col.getKey());
				String value = row.get(col.getKey());
				if (value == null) value = "";
				out.print(padChar + value + padChar);
				strRepeat(out, padChar, length - value.length());
				out.print(vertChar);
			}
			out.println();
			rowno++;
		}
		line(out, lengths);
		out.println();
	}
}
