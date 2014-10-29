package com.rambris;

import java.io.PrintWriter;
import java.util.Map;

/**
 * Generates HTML tables
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * 
 */
public class HTMLTable extends Table
{

	public HTMLTable()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public HTMLTable(Map<String, String> columns, Map<String, String> opts)
	{
		super(columns, opts);
		// TODO Auto-generated constructor stub
	}

	public HTMLTable(Map<String, String> columns)
	{
		super(columns);
		// TODO Auto-generated constructor stub
	}

	public HTMLTable(Table table)
	{
		super(table);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void print(PrintWriter out)
	{
		out.println("<table" + (opts.containsKey("table") ? " " + opts.get("table") : "") + ">");
		out.println("<thead>");
		out.print("<tr" + (opts.containsKey("tr") ? " " + opts.get("tr") : "") + ">");
		if(isRowNumbers())
		{
			out.print("<th>#</th>");
		}
		for (Map.Entry<String, String> col : columns.entrySet())
		{
			out.print("<th" + (opts.containsKey("th") ? " " + opts.get("th") : "") + ">" + col.getValue() + "</th>");
		}
		out.println("</tr>");
		out.println("</thead>");
		out.println("<tbody>");
		int rowno=1;
		for (Map<String, String> row : rows)
		{
			out.print("<tr" + (opts.containsKey("tr") ? " " + opts.get("tr") : "") + ">");
			if(isRowNumbers())
			{
				out.print("<th>"+Integer.toString(rowno)+"</th>");
			}

			for (Map.Entry<String, String> col : columns.entrySet())
			{
				String what = row.get(col.getKey());
				if (what != null) out.print("<td" + (opts.containsKey("td") ? " " + opts.get("td") : "") + ">" + what
						+ "</td>");
				else out.print("<td" + (opts.containsKey("td") ? " " + opts.get("td") : "") + "></td>");
			}
			out.println("</tr>");
			rowno++;
		}
		out.println("</tbody>");
		out.println("</table>");
	}
}
