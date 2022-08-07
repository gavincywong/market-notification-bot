package com.binance.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The client class establishes the HTTP connection to Binance
 */
public class Client
{
	public static final String API_URL;
	public static final String API_TESTNET_URL;
	public static final String VERSION;

	@SuppressWarnings("unused")
	private String apiKey;
	@SuppressWarnings("unused")
	private String apiSecret;
	private boolean isTestNet;
	private String url;

	private static HttpURLConnection connection;

	static
	{
		API_URL = "https://api.binance.com/api";
		API_TESTNET_URL = "https://testnet.binance.vision/api";
		VERSION = "v3";
	}

	/**
	 * Client ctor. The API KEY and SECRET are not required since no POST request is
	 * necessary.
	 * 
	 * @param apiKey:    api key
	 * @param apiSecret: api secret
	 * @param isTestNet: true if using test net
	 */
	public Client(String apiKey, String apiSecret, boolean isTestNet)
	{
		Validate.newString(apiKey);
		Validate.newString(apiSecret);

		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
		this.isTestNet = isTestNet;
		this.url = createUri();
	}

	/**
	 * Create the URI
	 * 
	 * @return api url
	 */
	private String createUri()
	{
		String url = API_URL;

		if(isTestNet)
		{
			url = API_TESTNET_URL;
		}

		return url + "/" + VERSION;
	}

	/**
	 * 
	 * @param method: method request
	 * @return connection of request
	 */
	public HttpURLConnection get(String method)
	{
		Validate.newString(method);

		try
		{
			URL fullUrl = new URL(url + "/" + method);

			connection = (HttpURLConnection) fullUrl.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}

		return connection;
	}

	/**
	 * Gets the server time
	 * 
	 * @return server time
	 */
	public long getServerTime()
	{
		connection = get("time");
		String serverTime = ParseJSON.getJSONString(getContent(connection), "serverTime");

		return Long.parseLong(serverTime);
	}

	/**
	 * Reads data from the connected URL
	 * 
	 * @param connection: HTTP connection
	 * @return: content in string format
	 */
	public StringBuilder getContent(HttpURLConnection connection)
	{
		BufferedReader reader = null;
		String line;
		StringBuilder res = new StringBuilder();

		Validate.connectCheck(connection);

		try
		{
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			while((line = reader.readLine()) != null)
			{
				res.append(line);
			}

			reader.close();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}

		return res;
	}

	/**
	 * Gets Kline Method
	 * 
	 * @param symbol:    ticker symbol
	 * @param interval:  ticker interval
	 * @param startTime: start time
	 * @param endTime:   end time
	 * @param limit:     number of intervals to requests
	 * @return HTTP connection of the Kline URL
	 */
	public HttpURLConnection getKline(String symbol, String interval, long startTime, long endTime, int limit)
	{
		return get("klines?symbol=" + symbol + "&interval=" + interval + "&startTime=" + startTime + "&endTime="
				+ endTime + "&limit=" + limit);
	}

	/**
	 * Gets Kline Method
	 * 
	 * @param symbol:    ticker symbol
	 * @param interval:  ticker interval
	 * @param startTime: start time
	 * @param endTime:   end time
	 * @return HTTP connection of the Kline URL
	 */
	public HttpURLConnection getKline(String symbol, String interval, long startTime, long endTime)
	{
		return get("klines?symbol=" + symbol + "&interval=" + interval + "&startTime=" + startTime + "&endTime="
				+ endTime);
	}

	/**
	 * Gets Kline Method
	 * 
	 * @param symbol:    ticker symbol
	 * @param interval:  ticker interval
	 * @param startTime: start time
	 * @return HTTP connection of the Kline URL
	 */
	public HttpURLConnection getKline(String symbol, String interval, long startTime)
	{
		return get("klines?symbol=" + symbol + "&interval=" + interval + "&startTime=" + startTime);
	}

	/**
	 * Gets Kline Method
	 * 
	 * @param symbol:    ticker symbol
	 * @param interval:  ticker interval
	 * @param startTime: start time
	 * @param limit:     number of intervals to requests
	 * @return HTTP connection of the Kline URL
	 */
	public HttpURLConnection getKline(String symbol, String interval, long startTime, int limit)
	{
		return get("klines?symbol=" + symbol + "&interval=" + interval + "&startTime=" + startTime + "&limit=" + limit);
	}

	/**
	 * Gets Kline Method
	 * 
	 * @param symbol:   ticker symbol
	 * @param interval: ticker interval
	 * @param limit:    number of intervals to requests
	 * @return HTTP connection of the Kline URL
	 */
	public HttpURLConnection getKline(String symbol, String interval, int limit)
	{
		return get("klines?symbol=" + symbol + "&interval=" + interval + "&limit=" + limit);
	}

	/**
	 * Gets Kline Method
	 * 
	 * @param symbol:   ticker symbol
	 * @param interval: ticker interval
	 * @return HTTP connection of the Kline URL
	 */
	public HttpURLConnection getKline(String symbol, String interval)
	{
		return get("klines?symbol=" + symbol + "&interval=" + interval);
	}
}
