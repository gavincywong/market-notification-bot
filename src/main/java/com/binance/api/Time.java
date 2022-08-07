package com.binance.api;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import com.github.gavincywong.mbn.gui.ConnectionLog;
import com.github.gavincywong.mbn.gui.MainFrame;

/**
 * The Time class offers a collection of timestamps used in the app, and methods
 * pertinent to pausing the app to prevent API rate limits from being exceeded
 * 
 * @author Gavin Wong
 * @version Aug 5, 2022
 */
public class Time
{
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm MMM dd");

	/**
	 * Timestamp in connection log
	 * 
	 * @return: connection log timestamp
	 */
	public static String getTimeStamp()
	{
		return "[" + sdf.format(new Timestamp(System.currentTimeMillis())) + "]";
	}

	/**
	 * Timestamp in alert window
	 * 
	 * @return: Alert timestamp
	 */
	public static String getAlertTimeStamp()
	{
		return "[" + sdf2.format(new Timestamp(System.currentTimeMillis())) + "]";
	}

	/**
	 * Closing Timestamp
	 * 
	 * @param time: closing time
	 * @return: closing timestamp
	 */
	public static String getCloseTimestamp(long time)
	{
		return sdf3.format(new Timestamp(time));
	}

	/**
	 * Calculate the end pause time in case the app is exited and re-opened before
	 * the full pause time has elapsed. Saving the end pause time in persistence
	 * prevents the program from exceeding the API rate limit
	 * 
	 * @param requests: number of intervals
	 */
	public static void logTime(int requests)
	{
		StyledDocument doc = ConnectionLog.getTheTextPane().getStyledDocument();

		try
		{
			FileWriter fw = new FileWriter("timelog.txt");
			long start = System.currentTimeMillis();
			long end = start + requests * 60;

			fw.write(String.valueOf(end));
			doc.insertString(doc.getLength(), Time.getTimeStamp() + " timelog.txt saved...\n\n", null);
			fw.close();
		}
		catch (IOException | BadLocationException e)
		{
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Method opens the textfile saved in persistence and determines if additional
	 * pause time is necessary.
	 */
	public static void timeOut()
	{
		StyledDocument doc = ConnectionLog.getTheTextPane().getStyledDocument();

		try
		{
			FileReader fr = new FileReader("timelog.txt");
			Scanner fileScanner = new Scanner(fr);
			long endTime = Long.parseLong(fileScanner.nextLine());
			long currTime = System.currentTimeMillis();

			if((endTime - currTime) > 0)
			{
				doc.insertString(doc.getLength(),
						Time.getTimeStamp() + " Additional timeout required: \n" + (endTime - currTime) + "ms.", null);
				Thread.sleep(endTime - currTime);
				doc.insertString(doc.getLength(), Time.getTimeStamp() + " Timeout complete.\n", null);
			}
			fr.close();
		}
		catch (InterruptedException | BadLocationException | IOException e)
		{
			System.out.println(e.getMessage());
			MainFrame.setOnlineStatus(false);
		}
	}

	/**
	 * @return the long time of the selected interval
	 */
	public static long timerInterval()
	{
		long timer = 0;

		switch(MainFrame.getInterval())
		{
		case "1m":
		case "3m":
		case "5m":
		case "15m":
		case "30m":
		case "1h":
			timer = 3600;
			break;
		case "2h":
			timer = 7200;
			break;
		case "4h":
			timer = 14400;
			break;
		case "6h":
			timer = 21600;
			break;
		case "8h":
			timer = 28800;
			break;
		case "12h":
			timer = 43200;
			break;
		case "1d":
			timer = 86400;
			break;
		case "3d":
			timer = 259200;
			break;
		case "1w":
			timer = 604800;
			break;
		case "1M":
			timer = 2678400;
			break;
		}

		return timer * 1000;
	}
}
