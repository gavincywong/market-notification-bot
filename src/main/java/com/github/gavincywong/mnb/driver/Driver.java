package com.github.gavincywong.mnb.driver;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.json.JSONArray;
import org.json.JSONObject;

import com.binance.api.Client;
import com.binance.api.ParseJSON;
import com.binance.api.Time;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.github.gavincywong.mbn.analysis.Klines;
import com.github.gavincywong.mbn.gui.ConnectionLog;
import com.github.gavincywong.mbn.gui.MainFrame;
import com.github.gavincywong.mnb.config.BinanceConfig;

/**
 * Driver class
 * 
 * @author Gavin Wong
 * @version Aug 5, 2022
 */
public class Driver implements BinanceConfig
{
	public static final int STATUS_SUCCESS;
	public static final String REQUEST_WEIGHT;
	public static final double ERROR_VALUE;

	private static Client client;
	private static Timer timer;

	public static boolean connected = false;

	static
	{
		STATUS_SUCCESS = 200;
		REQUEST_WEIGHT = "REQUEST_WEIGHT";
		ERROR_VALUE = -1.0;
	}

	public static void main(final String[] args)
	{
		Klines.initTickerMap();
		Klines.initIntervalMap();
		invokeMainFrame();
	}

	/**
	 * Initializes the program by verifying good connection to API call and fetches
	 * Kline data. The program will cycle at a fixed schedule based on the selected
	 * time interval
	 * 
	 * @return true if connected
	 */
	public static boolean init()
	{
		client = new Client(KEY, SECRET, false);

		int status;
		StringBuilder res;
		String rateLimits;
		HttpURLConnection connection;
		double macdValue = MainFrame.getMacdText();
		double rsiValue = MainFrame.getRsiText();
		StyledDocument doc = ConnectionLog.getTheTextPane().getStyledDocument();
		try
		{
			status = client.get("ping").getResponseCode();

			if(status == STATUS_SUCCESS && macdValue != ERROR_VALUE && rsiValue != ERROR_VALUE)
			{
				doc.insertString(doc.getLength(), Time.getTimeStamp() + " Connecting...\n", null);

				connection = client.get("exchangeInfo");
				res = client.getContent(connection);
				connected = true;
				timer = new Timer();

				MainFrame.setOnlineStatus(connected);
				MainFrame.setConnectButton(MainFrame.DISCONNECT);
				doc.insertString(doc.getLength(), Time.getTimeStamp() + " Connection successful.\n", null);
				doc.insertString(doc.getLength(),
						Time.getTimeStamp() + " Current date and time: " + new Date(client.getServerTime()) + "\n",
						null);

				rateLimits = ParseJSON.getJSONString(res, "rateLimits");
				JSONArray rateLimitArr = new JSONArray(rateLimits);

				for(int i = 0; i < rateLimitArr.length(); ++i)
				{
					JSONObject o = rateLimitArr.getJSONObject(i);
					if(o.getString("rateLimitType").equals(REQUEST_WEIGHT))
					{
						doc.insertString(doc.getLength(), "\n" + Time.getTimeStamp() + " Rate limit is "
								+ o.getInt("limit") + " requests per " + o.getString("interval") + "...\n", null);
					}
				}

				timer.scheduleAtFixedRate(new RunnableTimerTask(Driver::perform), 0, Time.timerInterval());
			}
			else
			{
				MainFrame.setOnlineStatus(false);
				MainFrame.setConnectButton(true);

				if(status == STATUS_SUCCESS)
				{
					doc.insertString(doc.getLength(), Time.getTimeStamp()
							+ " Error: check MACD limit and RSI limit text fields have been entered correctly.\n\n",
							null);
				}
				else
				{
					doc.insertString(doc.getLength(),
							Time.getTimeStamp() + " Error code " + status + ": unable to connect to server.\n\n", null);
				}
			}
		}
		catch (IOException | BadLocationException e)
		{
			System.out.println(e.getMessage());
			MainFrame.setOnlineStatus(false);
			MainFrame.setConnectButton(MainFrame.CONNECT);
		}

		return connected;
	}

	/**
	 * Launch the GUI main frame
	 * 
	 * @return GUI main frame
	 */
	public static MainFrame invokeMainFrame()
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					FlatDarculaLaf.setup();
					MainFrame frame = MainFrame.getTheMainFrame();
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					System.out.println(e.getMessage());
				}
			}
		});

		return MainFrame.getTheMainFrame();
	}

	/**
	 * Timer to fetch new Kline data at the set interval. Also implemented to get
	 * data on a separate thread for better GUI performance
	 */
	private static class RunnableTimerTask extends TimerTask
	{
		private Runnable runnable;

		public RunnableTimerTask(Runnable runnable)
		{
			this.runnable = runnable;
		}

		@Override
		public void run()
		{
			runnable.run();
		}
	}

	/**
	 * Calls for Klines to be collected and processed
	 */
	public static void perform()
	{
		try
		{
			Klines.collectKlines(client);
			Klines.processKlines();
		}
		catch (InterruptedException e)
		{
			System.out.println(e.getMessage());
			MainFrame.setOnlineStatus(false);
			MainFrame.setConnectButton(MainFrame.CONNECT);
		}
		finally
		{
			timer.cancel();
		}
	}

	/**
	 * @return timer
	 */
	public static Timer getTimer()
	{
		return timer;
	}
}
