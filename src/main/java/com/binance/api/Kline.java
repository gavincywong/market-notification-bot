package com.binance.api;

/**
 * The Kline class stores data for each interval 
 * @author Gavin Wong
 * @version Aug 5, 2022
 */
public class Kline
{
	public static final String INTERVAL_1MIN;
	public static final String INTERVAL_3MIN;
	public static final String INTERVAL_5MIN;
	public static final String INTERVAL_15MIN;
	public static final String INTERVAL_30MIN;
	public static final String INTERVAL_1H;
	public static final String INTERVAL_2H;
	public static final String INTERVAL_4H;
	public static final String INTERVAL_6H;
	public static final String INTERVAL_8H;
	public static final String INTERVAL_12H;
	public static final String INTERVAL_1D;
	public static final String INTERVAL_3D;
	public static final String INTERVAL_1W;
	public static final String INTERVAL_1M;
	
	public static final String ETH_BTC;
	public static final String BTC_USDT;
	public static final String BNB_BTC;
	public static final String XRP_BTC;
	public static final String LTC_BTC;
	
	private final long   openTime;
	private final long   closeTime;
	private final String open;
	private final String high;
	private final String low;
	private final String close;
	private final String volume;
	private final int    numOfTrades;
	
	static
	{
		INTERVAL_1MIN  = "1m";      
		INTERVAL_3MIN  = "3m";      
		INTERVAL_5MIN  = "5m";      
		INTERVAL_15MIN = "15m";     
		INTERVAL_30MIN = "30m";     
		INTERVAL_1H    = "1h";      
		INTERVAL_2H    = "2h";      
		INTERVAL_4H    = "4h";      
		INTERVAL_6H    = "6h";      
		INTERVAL_8H    = "8h";      
		INTERVAL_12H   = "12h";     
		INTERVAL_1D    = "1d";      
		INTERVAL_3D    = "3d";      
		INTERVAL_1W    = "1w";      
		INTERVAL_1M    = "1M";
		
		ETH_BTC  = "ETHBTC";
		BTC_USDT = "BTCUSDT";
		BNB_BTC  = "BNBBTC";
		XRP_BTC  = "XRPBTC";
		LTC_BTC  = "LTCBTC";
	}
	
	/**
	 * Kline ctor
	 * @param openTime: open time
	 * @param closeTime: close time
	 * @param open: opening price
	 * @param high: highest price
	 * @param low: lowest price
	 * @param close: closing price
	 * @param volume: trading volume
	 * @param numOfTrades: number of trades
	 */
	public Kline(long openTime, long closeTime, String open, String high, String low, String close, 
			String volume, int numOfTrades)
	{
		super();
	
		Validate.validateLong(openTime);
		Validate.validateLong(closeTime);
		Validate.newString(open);
		Validate.newString(high);
		Validate.newString(low);
		Validate.newString(close);
		Validate.newString(volume);
		Validate.validateInt(numOfTrades);
		
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		this.numOfTrades = numOfTrades;
	}

	/**
	 * 
	 * @return open time
	 */
	public long getOpenTime()
	{
		return openTime;
	}

	/**
	 * 
	 * @return close time
	 */
	public long getCloseTime()
	{
		return closeTime;
	}

	/**
	 * 
	 * @return opening price
	 */
	public String getOpen()
	{
		return open;
	}

	/**
	 * 
	 * @return highest price
	 */
	public String getHigh()
	{
		return high;
	}

	/**
	 * 
	 * @return lowest price
	 */
	public String getLow()
	{
		return low;
	}

	/**
	 * 
	 * @return closing price
	 */
	public String getClose()
	{
		return close;
	}

	/**
	 * 
	 * @return trading volume
	 */
	public String getVolume()
	{
		return volume;
	}

	/**
	 * 
	 * @return number of trades
	 */
	public int getNumOfTrades()
	{
		return numOfTrades;
	}

	@Override
	public String toString()
	{
		return "Kline [openTime=" + openTime + ", closeTime=" + closeTime + ", open=" + open + ", high=" + high
				+ ", low=" + low + ", close=" + close + ", volume=" + volume + ", numOfTrades=" + numOfTrades + "]";
	}
}
