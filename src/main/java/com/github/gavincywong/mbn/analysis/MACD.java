package com.github.gavincywong.mbn.analysis;

import java.util.HashMap;
import java.util.Map;

import com.binance.api.Kline;
import com.binance.api.Validate;
import com.github.gavincywong.mbn.gui.MainFrame;

/**
 * The MACD class models the calculation for MACD values
 * MACD stands for Moving Average Convergence Divergence
 * @author Gavin Wong
 * @version Aug 5, 2022
 */
public class MACD
{
	public static final int EMA_12_PERIOD;
	public static final int EMA_26_PERIOD;

	static
	{
		EMA_12_PERIOD = 12;
		EMA_26_PERIOD = 26;
	}

	/**
	 * Get MACD
	 * 
	 * @param data: Kline data
	 * @return: MACD data
	 */
	public static Map<Integer, Double> getMACD(Map<Integer, Kline> data)
	{
		if(data == null || data.isEmpty())
		{
			throw new IllegalArgumentException("Kline map cannot be null or empty.");
		}

		Map<Integer, Double> ema12Data = getEMA(EMA_12_PERIOD, data);
		Map<Integer, Double> ema26Data = getEMA(EMA_26_PERIOD, data);
		Map<Integer, Double> macd = new HashMap<>();

		ema26Data.keySet().forEach(key -> macd.put(key, ema12Data.get(key) - ema26Data.get(key)));

		return macd;
	}

	/**
	 * Get EMA values needed to calculate MACD EMA stands for Exponential Moving
	 * Average
	 * 
	 * @param period: period of EMA
	 * @param data:   Kline data
	 * @return EMA data
	 */
	public static Map<Integer, Double> getEMA(int period, Map<Integer, Kline> data)
	{
		Map<Integer, Double> ema = new HashMap<>();
		double initialEMA = 0.0;
		double currentEMA = 0.0;
		double previousEMA = 0.0;
		double K = 2.0 / (period + 1.0); // multiplier constant

		Validate.validateInt(period);

		if(data == null)
		{
			throw new IllegalArgumentException("Kline map cannot be null.");
		}
		else if(data.size() < period)
		{
			MainFrame.setOnlineStatus(false);
			throw new IllegalArgumentException("Insufficient data to calculate " + period + " periods. Only "
					+ data.size() + " periods were supplied.");
		}

		for(int key : data.keySet())
		{
			double close = Double.parseDouble(data.get(key).getClose().replaceAll("\"", ""));

			if(key < period)
			{
				initialEMA += close / period;
			}
			else if(key == period) // index starts at 0, so EMA starts calculating at index 'period - 1'
			{
				ema.put(key - 1, initialEMA);
				currentEMA = (close * K) + ((1 - K) * initialEMA);
				ema.put(key, currentEMA);
				previousEMA = currentEMA;
			}
			else
			{
				currentEMA = (close * K) + ((1 - K) * previousEMA);
				ema.put(key, currentEMA);
				previousEMA = currentEMA;
			}
		}

		return ema;
	}
}
