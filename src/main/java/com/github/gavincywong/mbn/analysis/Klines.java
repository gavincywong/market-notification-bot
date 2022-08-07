package com.github.gavincywong.mbn.analysis;

import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.json.JSONArray;

import com.binance.api.Client;
import com.binance.api.Kline;
import com.binance.api.ParseJSON;
import com.binance.api.Time;
import com.binance.api.Validate;
import com.discord.bot.Bot;
import com.github.gavincywong.mbn.gui.ConnectionLog;
import com.github.gavincywong.mbn.gui.MainFrame;
import com.github.gavincywong.mbn.gui.Sorter;

/**
 * The Klines class gathers and processes a series of Klines
 * 
 * @author Gavin Wong
 * @version Aug 5, 2022
 */
public class Klines
{
	private static Map<Integer, Kline> kMap;
	private static Map<String, String> tickerMap;
	private static Map<String, String> intervalMap;
	private static Map<Integer, Double> macd = null;
	private static Map<Integer, Double> rsi = null;

	public static final int DAYS_IN_A_WEEK = 7;
	public static final int DAYS_IN_A_MONTH = 30;
	public static final int REQUESTS = 450; // default 500, min 25 (number req'd to calculate MACD), max 1000
	public static final int MACD_FIRST_KEY = 25;
	public static final int MAX_REQUESTS = 1000;

	/**
	 * Collects Kline data by making the API call and stores data in kMap Function
	 * also verifies the program is not over-request data such that the rate limit
	 * will be exceeded This is performed using the timeOut and logTime methods
	 * 
	 * @param c: HTTP client
	 * @throws InterruptedException: when program doesn't pause correctly
	 */
	public static void collectKlines(Client c) throws InterruptedException
	{
		Validate.validateRequest();

		String ticker = MainFrame.getTicker();
		String interval = MainFrame.getInterval();
		long startTime = c.getServerTime() - getTimeUnit(interval, REQUESTS);
		kMap = new HashMap<>();
		String[] data;

		Time.timeOut();
		HttpURLConnection connection = c.getKline(ticker, interval, startTime);
		JSONArray arr = ParseJSON.getJSONList(c.getContent(connection));
		int numRequests = arr.length();

		for(int index = 0; index < numRequests; ++index)
		{
			data = ParseJSON.getKlineString(arr.get(index)).split(",");
			long openTime = Long.parseLong(data[0]);
			long closeTime = Long.parseLong(data[6]);
			String open = data[1];
			String high = data[2];
			String low = data[3];
			String close = data[4];
			String volume = data[5];
			int numOfTrades = Integer.parseInt(data[8]);

			kMap.put(index, new Kline(openTime, closeTime, open, high, low, close, volume, numOfTrades));
		}

		Time.logTime(REQUESTS);
	}

	/**
	 * Processes the Kline data and outputs alerts to the window and data to
	 * connection log.
	 */
	public static void processKlines()
	{
		macd = MACD.getMACD(kMap);
		rsi = RSI.getRSI(kMap);
		Set<Integer> macdKeys = macd.keySet();
		Set<Integer> rsiKeys = rsi.keySet();
		StyledDocument doc = ConnectionLog.getTheTextPane().getStyledDocument();
		StyledDocument alert = MainFrame.getTheAlertTextPane().getStyledDocument();
		DecimalFormat df = new DecimalFormat("0.00");
		boolean alertBot = false;
		boolean rsiAlert = false;
		String message = null;

		try
		{
			for(int key : macdKeys)
			{
				if(macd.get(key) < MainFrame.getMacdText())
				{
					alert.insertString(alert.getLength(),
							Time.getAlertTimeStamp() + " MACD: " + df.format(macd.get(key)) + " | RSI: "
									+ df.format(rsi.get(key)) + " | "
									+ Time.getCloseTimestamp(kMap.get(key).getCloseTime()) + "\n", null);
					
					alertBot = true;
					message = "New MACD alert";
				}
				doc.insertString(doc.getLength(),
						Time.getTimeStamp() + " Key: " + key + " MACD: " + df.format(macd.get(key)) + "\n", null);
			}

			for(int key : rsiKeys)
			{
				if(rsi.get(key) < MainFrame.getRsiText() && key >= MACD_FIRST_KEY)
				{
					alert.insertString(alert.getLength(),
							Time.getAlertTimeStamp() + " RSI: " + df.format(rsi.get(key)) + " | MACD: "
									+ df.format(macd.get(key)) + " | "
									+ Time.getCloseTimestamp(kMap.get(key).getCloseTime()) + "\n", null);
					
					alertBot = true;
					rsiAlert = true;
				}
				else if(rsi.get(key) < MainFrame.getRsiText())
				{
					alert.insertString(alert.getLength(), Time.getAlertTimeStamp() + " RSI: " + df.format(rsi.get(key))
							+ " | " + Time.getCloseTimestamp(kMap.get(key).getCloseTime()) + "\n", null);
					
					alertBot = true;
					rsiAlert = true;
				doc.insertString(doc.getLength(),
						Time.getTimeStamp() + " Key: " + key + " RSI: " + df.format(rsi.get(key)) + "\n", null);
				}
			}
			
			if(alertBot)
			{
				if(rsiAlert && message != null)
				{
					Bot.sendNotification("New MACD and RSI alert");
				}
				else if(rsiAlert && message == null)
				{
					Bot.sendNotification("New RSI alert");
				}
				else
				{
					Bot.sendNotification(message);
				}
			}

			Thread.sleep(REQUESTS * 60); // pause program based on the number of requests
		}
		catch (BadLocationException | InterruptedException e)
		{
			System.out.println(e.getMessage());
			MainFrame.setOnlineStatus(false);
		}
	}

	/**
	 * Initialize the interval map
	 */
	public static void initIntervalMap()
	{
		intervalMap = new HashMap<>();
		intervalMap.put("1 min", Kline.INTERVAL_1M);
		intervalMap.put("5 min", Kline.INTERVAL_5MIN);
		intervalMap.put("15 min", Kline.INTERVAL_15MIN);
		intervalMap.put("30 min", Kline.INTERVAL_30MIN);
		intervalMap.put("1 hr", Kline.INTERVAL_1H);
		intervalMap.put("4 hr", Kline.INTERVAL_4H);
		intervalMap.put("6 hr", Kline.INTERVAL_6H);
		intervalMap.put("12 hr", Kline.INTERVAL_12H);
		intervalMap.put("1 day", Kline.INTERVAL_1D);
		intervalMap.put("1 wk", Kline.INTERVAL_1W);
		intervalMap.put("1 mo", Kline.INTERVAL_1M);
	}

	/**
	 * Initialize the ticker map
	 */
	public static void initTickerMap()
	{
		tickerMap = new HashMap<>();
		tickerMap.put("ETH-BTC", Kline.ETH_BTC);
		tickerMap.put("BTC-USDT", Kline.BTC_USDT);
		tickerMap.put("BNB-BTC", Kline.BNB_BTC);
		tickerMap.put("XRP-BTC", Kline.XRP_BTC);
		tickerMap.put("LTC-BTC", Kline.LTC_BTC);
	}

	/**
	 * Get ticker keys in array
	 * 
	 * @return array of ticker keys
	 */
	public static String[] getTickerArray()
	{
		return tickerMap.keySet().stream().toArray(n -> new String[n]);
	}

	/**
	 * Get sorted interval array
	 * 
	 * @return sorted interval array
	 */
	public static String[] getIntervalArray()
	{
		String[] map = intervalMap.keySet().stream().toArray(n -> new String[n]);

		Arrays.sort(map, new Sorter.IntervalCompare());

		return map;
	}

	/**
	 * Get ticker map
	 * 
	 * @return ticker map
	 */
	public static Map<String, String> getTickerMap()
	{
		return tickerMap;
	}

	/**
	 * Get interval map
	 * 
	 * @return interval map
	 */
	public static Map<String, String> getIntervalMap()
	{
		return intervalMap;
	}

	/**
	 * Get Kline Map
	 * 
	 * @return kline map
	 */
	public static Map<Integer, Kline> getkMap()
	{
		return kMap;
	}

	/**
	 * Get MACD map
	 * 
	 * @return MACD map
	 */
	public static Map<Integer, Double> getMacd()
	{
		return macd;
	}

	/**
	 * Get RSI map
	 * 
	 * @return RSI map
	 */
	public static Map<Integer, Double> getRsi()
	{
		return rsi;
	}

	/**
	 * Time unit is primarily used to retrieve new Klines after the selected
	 * interval has elapsed
	 * 
	 * @param interval: interval
	 * @param limit:    number of repeating interval
	 * @return time interval in milliseconds
	 */
	public static long getTimeUnit(String interval, int limit)
	{
		long value;
		String timeUnit = interval.substring(interval.length() - 1);

		switch(timeUnit)
		{
		case "m":
			value = TimeUnit.MINUTES.toMillis(limit);
			break;
		case "h":
			value = TimeUnit.HOURS.toMillis(limit);
			break;
		case "d":
			value = TimeUnit.DAYS.toMillis(limit);
			break;
		case "w":
			value = TimeUnit.DAYS.toMillis(limit) * DAYS_IN_A_WEEK;
			break;
		case "M":
			value = TimeUnit.DAYS.toMillis(limit) * DAYS_IN_A_MONTH;
			break;
		default:
			throw new IllegalArgumentException("Unrecognized time unit: " + timeUnit);
		}

		return value;
	}
}
