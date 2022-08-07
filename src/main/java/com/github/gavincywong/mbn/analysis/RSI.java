package com.github.gavincywong.mbn.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Math;

import com.binance.api.Kline;
import com.binance.api.Validate;

/**
 * The RSI class models the calculation of RSI values RSI stands for Relative
 * Strength Index
 * 
 * @author Gavin Wong
 * @version Aug 5, 2022
 */
public class RSI
{
	private static Map<Long, Double> rsiMap = new HashMap<>();

	private static final int WINDOW_LENGTH = 14;

	/**
	 * Get RSI values
	 * 
	 * @param data: Kline data
	 * @return RSI data
	 */
	public static Map<Integer, Double> getRSI(Map<Integer, Kline> data)
	{
		if(data == null || data.isEmpty())
		{
			throw new IllegalArgumentException("Kline map cannot be null or empty");
		}

		List<Double> gains = new ArrayList<>();
		List<Double> losses = new ArrayList<>();
		Map<Integer, Double> RSI = new HashMap<>();

		double currentValue = 0.0;
		double previousValue = 0.0;
		double previousGain = 0.0;
		double previousLoss = 0.0;
		double diff = 0.0;

		for(Integer key : data.keySet())
		{
			if(key == 0)
			{
				currentValue = Double.parseDouble(data.get(key).getClose().replaceAll("\"", ""));
			}
			else
			{
				previousValue = currentValue;
				currentValue = Double.parseDouble(data.get(key).getClose().replaceAll("\"", ""));
				diff = currentValue - previousValue;

				if(diff > 0)
				{
					gains.add(diff);
					losses.add(0.0);
				}
				else if(diff < 0)
				{
					gains.add(0.0);
					losses.add(diff);
				}
				else
				{
					gains.add(0.0);
					losses.add(0.0);
				}

				if(key >= WINDOW_LENGTH)
				{
					double averageGain = calculateAverage(gains, previousGain, key);
					double averageLoss = calculateAverage(losses, previousLoss, key);
					double relativeStrength = Math.abs(averageGain / averageLoss);
					RSI.put(key, 100.0 - (100.0 / (1 + relativeStrength)));

					previousGain = averageGain;
					previousLoss = averageLoss;
				}
			}
		}

		return RSI;
	}

	/**
	 * Calculates averages of gain and losses as used in RSI calculation
	 * 
	 * @param values:   list of values
	 * @param previous: previous gain
	 * @param index:    index key
	 * @return the average gain/loss
	 */
	public static double calculateAverage(List<Double> values, double previous, int index)
	{
		Validate.validateList(values);
		Validate.validateInt(index);

		double average = 0.0;

		if(index == WINDOW_LENGTH)
		{
			for(int i = 1; i <= WINDOW_LENGTH; i++)
			{
				average += values.get(values.size() - i);
			}
		}
		else
		{
			average = previous * (WINDOW_LENGTH - 1) + values.get(values.size() - 1);
		}

		return average / WINDOW_LENGTH;
	}
}
