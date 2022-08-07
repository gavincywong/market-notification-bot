package com.github.gavincywong.mbn.gui;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import com.binance.api.Kline;
import com.binance.api.Time;

/**
 * The Export class enables the export of alerts and Kline data
 * 
 * @author Gavin Wong
 * @version Aug 5, 2022
 */
public class Export
{
	/**
	 * Export alerts
	 * 
	 * @param text: alert data
	 */
	public static void exportAlerts(JTextPane text)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MMMdd");
		Timestamp time = new Timestamp(System.currentTimeMillis());
		StyledDocument doc = ConnectionLog.getTheTextPane().getStyledDocument();

		try
		{
			FileWriter fw = new FileWriter(sdf.format(time) + "-alerts-export.txt");

			fw.write(text.getText());
			doc.insertString(doc.getLength(), Time.getTimeStamp() + " Alerts exported!\n\n", null);
			fw.close();
		}
		catch (IOException | BadLocationException e)
		{
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Export Klines
	 * 
	 * @param map: Kline data
	 */
	public static void exportKlines(Map<Integer, Kline> map)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MMMdd");
		Timestamp time = new Timestamp(System.currentTimeMillis());
		StyledDocument doc = ConnectionLog.getTheTextPane().getStyledDocument();
		Set<Integer> keys = map.keySet();
		long openTime;
		long closeTime;
		String open;
		String high;
		String low;
		String close;
		String volume;
		int numOfTrades;

		try
		{
			FileWriter fw = new FileWriter(sdf.format(time) + "-klines-export.txt");
			for(int key : keys)
			{
				openTime = map.get(key).getOpenTime();
				closeTime = map.get(key).getCloseTime();
				open = map.get(key).getOpen();
				close = map.get(key).getClose();
				high = map.get(key).getHigh();
				low = map.get(key).getLow();
				volume = map.get(key).getVolume();
				numOfTrades = map.get(key).getNumOfTrades();

				fw.write("Open time: " + openTime + " Close time: " + closeTime + " Open: " + open + " Close: " + close
						+ " High: " + high + " Low: " + low + " Volume: " + volume + " Number of trades: " + numOfTrades
						+ "\n");
			}

			doc.insertString(doc.getLength(), Time.getTimeStamp() + " Klines exported!\n\n", null);
			fw.close();
		}
		catch (IOException | BadLocationException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
