package com.rambris;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;

public class GZip
{

	public static byte[] compress(String str)
	{
		if (str == null || str.length() == 0) { return new byte[0]; }
		try
		{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(stream);
			gzip.write(str.getBytes("UTF-8"));
			IOUtils.closeQuietly(gzip);
			return stream.toByteArray();
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static String decompress(byte[] bytes)
	{
		if (bytes == null || bytes.length == 0) return null;
		try
		{
			GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(bytes));
			InputStreamReader isr = new InputStreamReader(gzip, "UTF-8");
			return IOUtils.toString(isr);
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
