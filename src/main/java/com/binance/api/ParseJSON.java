package com.binance.api;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ParseJSON class manipulates JSON objects to strings
 * @author Gavin Wong
 * @version Aug 5, 2022
 */
public class ParseJSON
{
	/**
	 * Get JSON String
	 * @param res: data in StringBuilder format
	 * @param req: requests
	 * @return data in string format
	 */
	public static String getJSONString(StringBuilder res, String req)
	{
		Validate.newString(res);
		Validate.newString(req);
		
		JSONObject data = new JSONObject(res.toString());
		
		return String.valueOf(data.get(req));	
	}
	
	/**
	 * Get JSON List
	 * @param res: data in StringBuilder format
	 * @return data in JSONArray format
	 */
	public static JSONArray getJSONList(StringBuilder res)
	{
		Validate.newString(res);

		JSONArray data = new JSONArray(res.toString());
		
		return data;	
	}
	
	/**
	 * Get Kline String
	 * @param arr: Kline data
	 * @return: string of Kline data in CSV format
	 */
	public static String getKlineString(Object arr)
	{
		int length = String.valueOf(arr).length();
		
		return String.valueOf(arr).substring(1, length - 1);
	}
}
