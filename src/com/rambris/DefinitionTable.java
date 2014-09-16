package com.rambris;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * An adaption of Table that creates a definition table of key and values.
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * @version $Id: DefinitionTable.java 27 2010-05-23 18:48:54Z boost $
 * 
 */
public class DefinitionTable extends TextTable
{
	public DefinitionTable()
	{
		super();
		columns.put("k", "k");
		columns.put("v", "v");
	}

	public void put(String key, String value)
	{
		Map<String, String> row = new HashMap<String, String>();
		row.put("k", key);
		row.put("v", value);
		super.addRow(row);
	}

	public void putAll(Map<String, Object> values)
	{
		for (Map.Entry<String, Object> e : values.entrySet())
		{
			put(e.getKey(), e.getValue().toString());
		}
	}

	@Override
	public void print(PrintWriter out)
	{
		Map<String, Integer> lengths = super.calcLengths();
		int keylength = lengths.get("k");
		for (Map<String, String> row : rows)
		{
			String key = row.get("k");
			String value = row.get("v");
			out.print(key);
			strRepeat(out, padChar, keylength - key.length());
			out.println(": " + value);
		}
	}

	public void printHTML(PrintWriter out)
	{
		out.println("<dl>");
		for (Map<String, String> row : rows)
		{
			String key = row.get("k");
			String value = row.get("v");
			out.println("<dt>" + key + "</dt>");
			out.println("<dd>" + value + "</dd>");
		}
		out.println("</dl>");
	}

	public String toHTML()
	{
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		printHTML(out);
		return str.toString();
	}
}
