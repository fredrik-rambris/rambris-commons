package com.rambris;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static utility functions
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 */
public class Util
{
	/**
	 * Cast almost anything to long or return 0
	 * 
	 * @param o
	 * @return
	 */
	public static long Long(Object o)
	{
		if (o == null) return 0;
		else if (o instanceof Long) return (Long) o;
		else if (o instanceof Integer) return (Integer) o;
		else if (o instanceof Short) return (Short) o;
		else if (o instanceof String)
		{
			try
			{
				long l = Long.parseLong((String) o);
				return l;
			}
			catch (NumberFormatException e)
			{}
		}
		return 0;
	}

	/**
	 * Get the bytes that make up a long. This could also be made using
	 * ByteArrayWriters and such. This is much faster
	 * 
	 * @param val
	 * @return
	 */
	public static byte[] getByteArray(long val)
	{
		return new byte[] { (byte) ((val >> 56) & 0xff), (byte) ((val >> 48) & 0xff), (byte) ((val >> 40) & 0xff), (byte) ((val >> 32) & 0xff),
				(byte) ((val >> 24) & 0xff), (byte) ((val >> 16) & 0xff), (byte) ((val >> 8) & 0xff), (byte) (val & 0xff) };
	}

	public static Map<String, Object>[] Array(Object o)
	{
		if (o == null) return null;
		if (o instanceof Vector)
		{
			Vector<Map<String, Object>> v = (Vector<Map<String, Object>>) o;
			return v.toArray(new Map[0]);
		}
		else if (o.getClass().isArray())
		{
			Object[] objarr = (Object[]) o;
			Map<String, Object>[] maparr = new Map[objarr.length];
			int p = 0;
			for (Object obj : objarr)
			{
				maparr[p++] = (Map<String, Object>) obj;
			}
			return maparr;
		}
		else return null;
	}

	public static String Beautify(String string)
	{
		if (string == null) return string;
		string = string.trim();
		if (string.length() == 0) return string;

		// Check if we're all big ones or all little ones
		if (string.toUpperCase().equals(string) || string.toLowerCase().equals(string))
		{
			String[] words = string.split("\\s+");
			string = "";
			for (String word : words)
			{
				string += word.substring(0, 1).toUpperCase();
				if (word.length() > 1)
				{
					string += word.substring(1).toLowerCase();
				}
				string += " ";
			}
			string = string.trim();
		}
		return string;
	}

	/**
	 * Get the stack trace string from a throwable
	 * 
	 * @param t
	 * @return
	 */
	public static String GetStacktrace(Throwable t)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}

	/**
	 * Repeat c l times.
	 * 
	 * @param c
	 * @param l
	 * @return
	 */
	public static String repeat(char c, int l)
	{
		String s = new String();
		while (l-- > 0)
		{
			s += c;
		}
		return s;
	}

	private static final Pattern quotedAttributePattern = Pattern.compile("([a-z0-9]+)\\s*=\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
	private static final Pattern unquotedAttributePattern = Pattern.compile("([a-z0-9]+)\\s*=\\s*([\\S]+)", Pattern.CASE_INSENSITIVE);

	/**
	 * Get attributes from a HTML or XML tag.
	 * 
	 * @param tag
	 * @return
	 */
	static public Map<String, String> getAttributes(String tag)
	{
		Map<String, String> attributes = new LinkedHashMap<String, String>();
		int lastPos = 0;
		StringBuilder leftovers = new StringBuilder();

		Matcher mt = quotedAttributePattern.matcher(tag);
		while (mt.find())
		{
			leftovers.append(tag.substring(lastPos, mt.start()));
			attributes.put(mt.group(1), mt.group(2));
			lastPos = mt.end();
		}
		if (lastPos > 0)
		{
			leftovers.append(tag.substring(lastPos));
		}
		tag = leftovers.toString();

		lastPos = 0;
		mt = unquotedAttributePattern.matcher(tag);
		while (mt.find())
		{
			leftovers.append(tag.substring(lastPos, mt.start()));
			attributes.put(mt.group(1), mt.group(2));
			lastPos = mt.end();
		}

		return attributes;
	}

	private static final Pattern tagPattern = Pattern.compile("<[/!]?([^\\s>]*)(\\s*[^>]*)>", Pattern.CASE_INSENSITIVE);

	/**
	 * Wrapper around StripTags that list the tags and attributes in a comma
	 * separated string
	 * 
	 * @param text
	 * @param allowedTags
	 * @param allowedAttrs
	 * @return
	 */
	public static String StripTags(String text, String allowedTags, String allowedAttrs)
	{
		String[] tag_list = allowedTags.split("[, ]+");
		String[] attr_list = allowedAttrs.split("[, ]+");
		return StripTags(text, tag_list, attr_list);
	}

	/**
	 * Removes all but the allowedTags and allowedAttrs from text.
	 * 
	 * @param text
	 * @param allowedTags
	 * @param allowedAttrs
	 * @return
	 */
	public static String StripTags(String text, String[] allowedTags, String[] allowedAttrs)
	{
		Arrays.sort(allowedTags);
		Arrays.sort(allowedAttrs);
		Matcher m = tagPattern.matcher(text);
		StringBuffer out = new StringBuffer();
		int lastPos = 0;

		while (m.find())
		{
			String tag = m.group(1);

			// if tag not allowed: skip it
			if (Arrays.binarySearch(allowedTags, tag) < 0)
			{
				out.append(text.substring(lastPos, m.start())).append(" ");
			}
			else
			{
				out.append(text.substring(lastPos, m.start()));
				String wholetag = text.substring(m.start(), m.end());
				if (!wholetag.startsWith("</"))
				{
					Map<String, String> attrs = getAttributes(wholetag.substring(tag.length() + 1, wholetag.length() - 1).trim());
					StringBuilder outTag = new StringBuilder("<" + tag);

					for (Map.Entry<String, String> e : attrs.entrySet())
					{
						if (Arrays.binarySearch(allowedAttrs, e.getKey()) >= 0)
						{
							// De-wordifier
							if (e.getKey().equalsIgnoreCase("class") && e.getValue().trim().equalsIgnoreCase("MsoNormal")) continue;
							outTag.append(" " + e.getKey() + "=\"" + e.getValue() + "\"");
						}
					}
					if (wholetag.endsWith("/>")) outTag.append(" />");
					else outTag.append(">");

					wholetag = outTag.toString();
				}
				out.append(wholetag);
			}

			lastPos = m.end();
		}

		if (lastPos > 0)
		{
			out.append(text.substring(lastPos));
			return out.toString().trim();
		}
		else
		{
			return text;
		}
	}

	/**
	 * Makes an Interator iterable with foreach
	 * 
	 * @param <I>
	 * @param iterator
	 * @return
	 */
	public static <I> Iterable<I> iterable(final Iterator<I> iterator)
	{
		return new Iterable<I>()
		{

			@Override
			public Iterator<I> iterator()
			{
				return iterator;
			}
		};
	}

	public static SimpleDateFormat RFC822DATEFORMAT = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);

	/**
	 * RFC822 has strict rules how to format a date. This meets that standard
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateAsRFC822String(Date date)
	{
		return RFC822DATEFORMAT.format(date);
	}

	public static SimpleDateFormat MYSQLDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);

	public static String getDateAsMysqlString(Date date)
	{
		return MYSQLDATEFORMAT.format(date);
	}

	/**
	 * Returns a Map of common date variables.
	 * 
	 * @return
	 */
	public static Map<String, String> DateVars()
	{
		Map<String, String> vars = new HashMap<String, String>();
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("sv"));
		Calendar cal = Calendar.getInstance(new Locale("sv"));
		vars.put("date", df.format(new Date()));
		vars.put("week", String.format("%02d", cal.get(Calendar.WEEK_OF_YEAR)));
		vars.put("nextweek", String.format("%02d", cal.get(Calendar.WEEK_OF_YEAR) + 1));
		vars.put("year", String.format("%04d", cal.get(Calendar.YEAR)));
		vars.put("yr", String.format("%02d", cal.get(Calendar.YEAR) % 100));
		return vars;
	}

	private static Map<Character, String> entityTable = new HashMap<Character, String>();

	static
	{
		entityTable.put('¡', "&iexcl;");
		entityTable.put('¢', "&cent;");
		entityTable.put('£', "&pound;");
		entityTable.put('¤', "&curren;");
		entityTable.put('¥', "&yen;");
		entityTable.put('¦', "&brvbar;");
		entityTable.put('§', "&sect;");
		entityTable.put('¨', "&uml;");
		entityTable.put('©', "&copy;");
		entityTable.put('ª', "&ordf;");
		entityTable.put('«', "&laquo;");
		entityTable.put('¬', "&not;");
		entityTable.put('®', "&reg;");
		entityTable.put('¯', "&macr;");
		entityTable.put('°', "&deg;");
		entityTable.put('±', "&plusmn;");
		entityTable.put('²', "&sup2;");
		entityTable.put('³', "&sup3;");
		entityTable.put('´', "&acute;");
		entityTable.put('µ', "&micro;");
		entityTable.put('¶', "&para;");
		entityTable.put('·', "&middot;");
		entityTable.put('¸', "&cedil;");
		entityTable.put('¹', "&sup1;");
		entityTable.put('º', "&ordm;");
		entityTable.put('»', "&raquo;");
		entityTable.put('¼', "&frac14;");
		entityTable.put('½', "&frac12;");
		entityTable.put('¾', "&frac34;");
		entityTable.put('¿', "&iquest;");
		entityTable.put('À', "&Agrave;");
		entityTable.put('Á', "&Aacute;");
		entityTable.put('Â', "&Acirc;");
		entityTable.put('Ã', "&Atilde;");
		entityTable.put('Ä', "&Auml;");
		entityTable.put('Å', "&Aring;");
		entityTable.put('Æ', "&AElig;");
		entityTable.put('Ç', "&Ccedil;");
		entityTable.put('È', "&Egrave;");
		entityTable.put('É', "&Eacute;");
		entityTable.put('Ê', "&Ecirc;");
		entityTable.put('Ë', "&Euml;");
		entityTable.put('Ì', "&Igrave;");
		entityTable.put('Í', "&Iacute;");
		entityTable.put('Î', "&Icirc;");
		entityTable.put('Ï', "&Iuml;");
		entityTable.put('Ð', "&ETH;");
		entityTable.put('Ñ', "&Ntilde;");
		entityTable.put('Ò', "&Ograve;");
		entityTable.put('Ó', "&Oacute;");
		entityTable.put('Ô', "&Ocirc;");
		entityTable.put('Õ', "&Otilde;");
		entityTable.put('Ö', "&Ouml;");
		entityTable.put('×', "&times;");
		entityTable.put('Ø', "&Oslash;");
		entityTable.put('Ù', "&Ugrave;");
		entityTable.put('Ú', "&Uacute;");
		entityTable.put('Û', "&Ucirc;");
		entityTable.put('Ü', "&Uuml;");
		entityTable.put('Ý', "&Yacute;");
		entityTable.put('Þ', "&THORN;");
		entityTable.put('ß', "&szlig;");
		entityTable.put('à', "&agrave;");
		entityTable.put('á', "&aacute;");
		entityTable.put('â', "&acirc;");
		entityTable.put('ã', "&atilde;");
		entityTable.put('ä', "&auml;");
		entityTable.put('å', "&aring;");
		entityTable.put('æ', "&aelig;");
		entityTable.put('ç', "&ccedil;");
		entityTable.put('è', "&egrave;");
		entityTable.put('é', "&eacute;");
		entityTable.put('ê', "&ecirc;");
		entityTable.put('ë', "&euml;");
		entityTable.put('ì', "&igrave;");
		entityTable.put('í', "&iacute;");
		entityTable.put('î', "&icirc;");
		entityTable.put('ï', "&iuml;");
		entityTable.put('ð', "&eth;");
		entityTable.put('ñ', "&ntilde;");
		entityTable.put('ò', "&ograve;");
		entityTable.put('ó', "&oacute;");
		entityTable.put('ô', "&ocirc;");
		entityTable.put('õ', "&otilde;");
		entityTable.put('ö', "&ouml;");
		entityTable.put('÷', "&divide;");
		entityTable.put('ø', "&oslash;");
		entityTable.put('ù', "&ugrave;");
		entityTable.put('ú', "&uacute;");
		entityTable.put('û', "&ucirc;");
		entityTable.put('ü', "&uuml;");
		entityTable.put('ý', "&yacute;");
		entityTable.put('þ', "&thorn;");
		entityTable.put('ÿ', "&yuml;");
		entityTable.put('€', "&euro;");
	}

	public static String htmlEntities(String html)
	{
		StringBuilder out = new StringBuilder(html.length() * 2);
		for (int p = 0; p < html.length(); p++)
		{
			Character c = html.charAt(p);
			String entity = entityTable.get(c);
			if (entity != null) out.append(entity);
			else out.append(c);
		}
		return out.toString();
	}

	private static Map<Character, String> deWordifyMap = new HashMap<Character, String>();
	static
	{
		deWordifyMap.put('\u2026', "...");
		deWordifyMap.put('\u02c6', "^");
		deWordifyMap.put('\u2039', "<");
		deWordifyMap.put('\u203a', ">");
		deWordifyMap.put('\u2018', "'");
		deWordifyMap.put('\u2019', "'");
		deWordifyMap.put('\u201a', "'");
		deWordifyMap.put('\u201c', "\"");
		deWordifyMap.put('\u201d', "\"");
		deWordifyMap.put('\u201e', "\"");
		deWordifyMap.put('\u2012', "-");
		deWordifyMap.put('\u2014', "-");
		deWordifyMap.put('\u2013', "-");
	}

	/**
	 * Remove high characters that MS Office likes to replace common ASCII chars
	 * like minus and quotes with. This method converts the most common ones
	 * back to ASCII.
	 * 
	 * @param ugly
	 * @return
	 */
	public static String deWordify(String ugly)
	{
		StringBuilder nice = new StringBuilder(ugly.length() * 2);
		int l = ugly.length();
		for (int c = 0; c < l; c++)
		{
			char uglyChar = ugly.charAt(c);
			String replace = deWordifyMap.get(uglyChar);
			if (replace != null) nice.append(replace);
			else nice.append(uglyChar);
		}
		return nice.toString();
	}

	public static String truncateString(String str, int maxlen)
	{
		return truncateString(str, maxlen, false);
	}

	public static String truncateString(String str, int maxlen, boolean elipsis)
	{
		if (str == null) return "";
		if (elipsis) maxlen -= 3;
		if (str.length() > maxlen) str = str.substring(0, maxlen);
		if (elipsis) return str + "...";
		else return str;
	}

	/*
	 * Following is taken from
	 * http://www.flickr.com/groups/api/discuss/72157616713786392/
	 */
	protected static String alphabetString = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
	protected static char[] alphabet = alphabetString.toCharArray();
	protected static int base_count = alphabet.length;

	/**
	 * Base58 encoder
	 * 
	 * @param num
	 * @return
	 */
	public static String encodeFlickr(long num)
	{
		String result = "";
		long div;
		int mod = 0;

		while (num >= base_count)
		{
			div = num / base_count;
			mod = (int) (num - (base_count * div));
			result = alphabet[mod] + result;
			num = div;
		}
		if (num > 0)
		{
			result = alphabet[(int) num] + result;
		}
		return result;
	}

	public static long decodeFlickr(String link)
	{
		long result = 0;
		long multi = 1;
		while (link.length() > 0)
		{
			String digit = link.substring(link.length() - 1);
			result = result + multi * alphabetString.lastIndexOf(digit);
			multi = multi * base_count;
			link = link.substring(0, link.length() - 1);
		}
		return result;
	}

}
