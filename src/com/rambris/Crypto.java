package com.rambris;

/**
 * Crypto.java version $id$
 *
 * Copyright (c) 2008-2010 Fredrik Rambris. All rights reserved.
 */

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

/**
 * Crypto functions are stored here
 * 
 * @version 1.0
 * @author Fredrik Rambris <fredrik@rambris.com>
 */
public class Crypto
{
	Charset charset;

	/**
	 * Generate a 128-bit random number in hex notation
	 * 
	 * @return generated number
	 */
	public String GetGUID()
	{
		// StringWriter buf = new StringWriter();
		// PrintWriter out = new PrintWriter(buf);
		int[] rnd = { 0, 0, 0, 0 };
		for (int p = 0; p < 4; p++)
		{
			rnd[p] = (int) ((Math.random() - 0.5) * Integer.MAX_VALUE);
		}
		return String.format("%08X%08X%08X%08X", rnd[0], rnd[1], rnd[2], rnd[3]);
		// out.printf("%08X%08X%08X%08X", rnd[0], rnd[1], rnd[2], rnd[3]);
		// return buf.toString();
	}

	public Crypto()
	{
		charset = Charset.forName("utf-8");
	}

	/**
	 * Generate SHA-1 hash for string
	 * 
	 * @param str
	 *            Input string
	 * @return Generated hash
	 */
	public String hash(String str)
	{
		return toHex(hash(str.getBytes(charset)));
	}

	/**
	 * Generate MD5 hash for string
	 * 
	 * @param str
	 *            Input string
	 * @return Generated hash
	 */
	public String md5hash(String str)
	{

		return toHex(md5hash(str.getBytes(charset)));
	}

	public String md5hashbase64(String str)
	{
		return new String(Base64.encodeBase64(md5hash(str.getBytes(charset))));
	}

	/**
	 * Generate MD5-hash of bytes
	 * 
	 * @param bytes
	 *            Input bytes
	 * @return the hash in binary form
	 */
	private byte[] md5hash(byte[] bytes)
	{
		try
		{
			MessageDigest md5digest = MessageDigest.getInstance("md5");
			md5digest.reset();
			return md5digest.digest(bytes);
		}
		catch (NoSuchAlgorithmException e)
		{
			return null;
		}
	}

	/**
	 * Generate hash of bytes
	 * 
	 * @param bytes
	 *            Input bytes
	 * @return the hash in binary form
	 */
	private byte[] hash(byte[] bytes)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("sha-1");
			digest.reset();
			return digest.digest(bytes);
		}
		catch (NoSuchAlgorithmException e)
		{
			return null;
		}
	}

	public String sha1hashbase64(String str)
	{
		return new String(Base64.encodeBase64(hash(str.getBytes(charset))));
	}

	/**
	 * Convert binary data to hexadecimal notation
	 * 
	 * @param bytes
	 * @return
	 */
	public static String toHex(byte[] bytes)
	{
		StringWriter buf = new StringWriter();
		PrintWriter out = new PrintWriter(buf);
		for (Byte b : bytes)
		{
			out.printf("%02x", b & 0xff);
		}
		return buf.toString();
	}

	static String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_@";

	public static String numtochar(int number)
	{
		String str = new String();

		for (int s = 0; s < 6; s++)
		{
			int hextet = (number >> s * 6) & 63;
			str += chars.charAt(hextet);
		}
		return str;
	}

	public static int chartonum(String str)
	{
		if (str.length() != 6) throw new NumberFormatException("String must be 6 chars long");
		int num = 0;
		for (int s = 0; s < 6; s++)
		{
			char ch = str.charAt(5 - s);
			int pos = chars.indexOf(ch);
			if (pos == -1) throw new NumberFormatException("Char not found '" + ch + "'");
			num = (num << 6) | (pos & 63);
		}
		return num;
	}

	/**
	 * Mod10 check. Source from http://sv.wikipedia.org/wiki/Luhn-algoritmen
	 * 
	 * @param indata
	 * @return
	 */
	public int luhn(String indata)
	{
		int a = 1;
		int sum = 0;
		int term;
		for (int i = indata.length() - 1; i >= 0; i--)
		{
			term = Character.digit(indata.charAt(i), 10) * a;
			if (term > 9) term -= 9;
			sum += term;
			a = 3 - a;
		}
		return sum % 10;
	}
}
