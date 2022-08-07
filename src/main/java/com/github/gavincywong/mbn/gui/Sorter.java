package com.github.gavincywong.mbn.gui;

import java.util.Comparator;

/**
 * The Sorter class allows the interval list to be sorted
 * 
 * @author Gavin Wong
 * @version Aug 5, 2022
 */
public class Sorter
{
	/**
	 * Sort strings by comparing interval values
	 */
	public static class IntervalCompare implements Comparator<String>
	{
		@Override
		public int compare(String string1, String string2)
		{
			String[] s1 = string1.split(" ");
			String[] s2 = string2.split(" ");

			return (Integer.parseInt(s1[0]) + unitCompare(s1[1])) - (Integer.parseInt(s2[0]) + unitCompare(s2[1]));
		}
	}

	/**
	 * The values for each time interval was contrived to create the desired order
	 * of ascending integer and ascending time interval
	 * 
	 * @param s: time interval
	 * @return value used to sort interval in the desired order
	 */
	public static int unitCompare(String s)
	{
		int value = 0;

		switch(s)
		{
		case "min":
			value = 0;
			break;
		case "hr":
			value = 30;
			break;
		case "day":
			value = 42;
			break;
		case "wk":
			value = 43;
			break;
		case "mo":
			value = 44;
			break;
		default:
			throw new IllegalArgumentException("Unrecognized time unit: " + s);
		}

		return value;
	}
}
