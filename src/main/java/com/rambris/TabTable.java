package com.rambris;

import java.io.PrintWriter;
import java.util.Map;

/**
 * Tab separated text file suitable for Excel etc.
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * 
 */
public class TabTable extends Table
{
	public TabTable(Map<String, String> columns, Map<String, String> opts)
	{
		super(columns, opts);
	}

	public TabTable(Map<String, String> columns)
	{
		super(columns);
	}

	public TabTable(Table table)
	{
		super(table);
	}
	
	public TabTable()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see com.rambris.Table#print(java.io.PrintWriter)
	 */
	@Override
	public void print(PrintWriter out)
	{
		char tab='\0';
		for(String col:columns.values())
		{
			if(tab!='\0') out.print(tab);
			out.print(col);
			tab='\t';
		}
		out.println();
		for(Map<String,String>row:rows)
		{
			tab='\0';
			for(String col:columns.keySet())
			{
				String val=row.get(col);
				if(val==null) val="";
				if(tab!='\0') out.print(tab);
				out.print(val);
				tab='\t';				
			}
			out.println();
		}
	}
	
	

}
