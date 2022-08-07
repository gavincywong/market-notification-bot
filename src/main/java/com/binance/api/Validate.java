package com.binance.api;

import java.net.HttpURLConnection;
import java.util.List;

import org.json.JSONArray;

import com.github.gavincywong.mbn.analysis.Klines;

/**
 * This class Validates a number of values
 * 
 * @author Gavin Wong
 * @version Aug 5, 2022
 */
public class Validate
{
	/**
	 * Validates new string
	 * 
	 * @param value: String value
	 */
	public static void newString(final String value)
	{
		if(value == null || value.isBlank())
		{
			throw new IllegalArgumentException("Invalid string value: " + value);
		}
	}

	/**
	 * Validates new string
	 * 
	 * @param value: StringBuilder value
	 */
	public static void newString(final StringBuilder value)
	{
		if(value == null || value.isEmpty())
		{
			throw new IllegalArgumentException("StringBuilder cannot be null or empty");
		}
	}

	/**
	 * Checks connection
	 * 
	 * @param connection: HTTP connection
	 */
	public static void connectCheck(final HttpURLConnection connection)
	{
		if(connection == null)
		{
			throw new IllegalArgumentException("Invalid connection");
		}
	}

	/**
	 * Validates long integers
	 * 
	 * @param value: long integers
	 */
	public static void validateLong(final long value)
	{
		if(value <= 0)
		{
			throw new IllegalArgumentException("Invalid long value: " + value);
		}
	}

	/**
	 * Validates integers
	 * 
	 * @param value: integers
	 */
	public static void validateInt(final int value)
	{
		if(value <= 0)
		{
			throw new IllegalArgumentException("Invalid long value: " + value);
		}
	}

	/**
	 * Validates JSON Array
	 * 
	 * @param arr: JSON Array
	 */
	public static void validateJSONArray(final JSONArray arr)
	{
		if(arr == null || arr.isEmpty())
		{
			throw new IllegalArgumentException("JSONArray cannot be null or empty.");
		}
	}

	/**
	 * Validates a list of generic types
	 * 
	 * @param <T>:  generic type
	 * @param list: list of generic types
	 */
	public static <T> void validateList(final List<T> list)
	{
		if(list == null || list.isEmpty())
		{
			throw new IllegalArgumentException("List cannot be null or empty.");
		}
	}
	
	/**
	 * Secondary check in case user dismisses the comment on request limits
	 * This will prevent accidental rate limit exceedance
	 */
	@SuppressWarnings("unused")
	public static void validateRequest()
	{
		if(Klines.REQUESTS > Klines.MAX_REQUESTS)
		{
			throw new IllegalArgumentException("WARNING: Number of requests exceeds limit!");
		}
	}
}
