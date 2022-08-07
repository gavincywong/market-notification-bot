package com.github.gavincywong.mbn.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import com.binance.api.Time;
import com.github.gavincywong.mbn.analysis.Klines;
import com.github.gavincywong.mnb.driver.Driver;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import java.awt.event.KeyEvent;
import java.nio.channels.NotYetConnectedException;
import java.awt.event.InputEvent;
import java.awt.SystemColor;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

/**
 * The MainFrame forms the main frame of the program's GUI
 * 
 * @author Gavin Wong
 * @version Aug 5, 2022
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame
{
	private static JLabel statusLabel = new JLabel("<html>Status: <font color='#F67280'><b>Offline</b></font></html>");
	private static ConnectionLog connectionFrame = new ConnectionLog();
	private static JTextPane theAlertTextPane = new JTextPane();
	private static MainFrame theMainFrame = new MainFrame();
	private static JTextField macdText;
	private static JTextField rsiText;
	private static JComboBox tickerBox;
	private static JList list;
	private static JButton connectButton;

	private JPanel contentPane;

	public static final double MAX_MACD = 500.0;
	public static final double MIN_MACD = -500.0;
	public static final double MAX_RSI = 100.0;
	public static final double MIN_RSI = 0.0;
	public static final boolean CONNECT = false;
	public static final boolean DISCONNECT = true;

	private static final int CONFIRM_DIALOG_NO = 1;
	private static final int CONFIRM_DIALOG_YES = 0;

	/**
	 * MainFrame ctor initializing menus, labels, text boxes, and action listeners
	 */
	public MainFrame()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu infoMenu = new JMenu("Info");
		JMenu chartMenu = new JMenu("Charts");
		JMenuItem rsiMenuItem = new JMenuItem("RSI Chart");
		JMenuItem macdMenuItem = new JMenuItem("MACD Chart");
		JMenuItem exitFile = new JMenuItem("Exit");
		JMenuItem aboutMenuItem = new JMenuItem("About");
		JMenuItem connectionMenuItem = new JMenuItem("Connection Log");
		JMenuItem exportDataMenuItem = new JMenuItem("Export Data");
		JMenuItem exportFile = new JMenuItem("Export Alerts");
		JSeparator separator = new JSeparator();
		Image frameImage = new ImageIcon("lib/icon.png").getImage();

		String[] tickerList = Klines.getTickerArray();
		String[] intervalList = Klines.getIntervalArray();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(450, 300);
		setLocationRelativeTo(null);
		setTitle("Market Notification Bot v1.0");
		setJMenuBar(menuBar);
		setResizable(false);
		setIconImage(frameImage);

		exportFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
		exitFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		connectionMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		exportDataMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));

		menuBar.add(fileMenu);
		fileMenu.add(exportFile);
		fileMenu.add(exportDataMenuItem);
		fileMenu.add(separator);
		fileMenu.add(exitFile);
		menuBar.add(infoMenu);
		infoMenu.add(connectionMenuItem);
		infoMenu.add(aboutMenuItem);
		menuBar.add(chartMenu);
		chartMenu.add(rsiMenuItem);
		chartMenu.add(macdMenuItem);

		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.windowBorder);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][150.00][][][170.00][][160.00][][][][]", "[][][grow][][][][][]"));

		JLabel intervalLabel = new JLabel("Intervals:");
		JLabel alertLabel = new JLabel("Alerts:");
		JLabel macdLabel = new JLabel("<html>MACD Limit<font color='#FF4646'>*</font>:</html>");
		JLabel ticker = new JLabel("Ticker: ");
		JLabel rsiLabel = new JLabel("<html>RSI Limit<font color='#FF4646'>*</font>:</html>");
		JScrollPane scrollPane = new JScrollPane(theAlertTextPane);
		list = new JList(intervalList);
		tickerBox = new JComboBox(tickerList);
		macdText = new JTextField();
		rsiText = new JTextField();
		connectButton = new JButton("Connect");

		contentPane.add(intervalLabel, "cell 1 0 3 1,alignx left");
		contentPane.add(alertLabel, "cell 4 0");
		contentPane.add(list, "cell 1 1 3 7,growx,aligny top");
		contentPane.add(macdLabel, "cell 4 6,alignx right");
		contentPane.add(macdText, "cell 5 6,growx");
		contentPane.add(rsiLabel, "cell 6 6,alignx right");
		contentPane.add(rsiText, "cell 8 6,growx");
		contentPane.add(ticker, "cell 4 7,alignx trailing");
		contentPane.add(scrollPane, "cell 4 1 11 5,grow");
		contentPane.add(tickerBox, "cell 5 7,growx");
		contentPane.add(connectButton, "cell 6 7 3 1,alignx right");
		contentPane.add(statusLabel, "cell 6 0 3 1,alignx right");

		statusLabel.setForeground(Color.LIGHT_GRAY);
		statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(4);
		tickerBox.setSelectedIndex(1);
		macdText.setColumns(12);
		macdText.setText("0");
		rsiText.setColumns(10);
		rsiText.setText("30");
		macdLabel.setToolTipText("A notification will appear when the actual MACD value\ndrops below the limit.");
		rsiLabel.setToolTipText("A notification will appear when the actual RSI value\ndrops below the limit.");
		macdText.setToolTipText("Local minimum");
		rsiText.setToolTipText("Overbought when above 70\nOversold when below 30");
		theAlertTextPane.setEditable(false);
		connectButton.setPreferredSize(new Dimension(100, 20));

		aboutMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(MainFrame.this,
						"Market Notification Bot\nversion 1.0\nby Gavin Wong\nhttps://github.com/gavincywong", "About",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});

		connectionMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				connectionFrame.setVisible(true);
			}
		});

		connectButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				StyledDocument doc = ConnectionLog.getTheTextPane().getStyledDocument();
				
				if(connectButton.getText().equalsIgnoreCase("Connect"))
				{
					Driver.init();
				}
				else 
				{
					Driver.getTimer().cancel();
					setConnectButton(CONNECT);
					setOnlineStatus(false);
					try
					{
						doc.insertString(doc.getLength(), "\n" + 
								Time.getTimeStamp() + " Application terminated.\n\n", null);
					}
					catch (BadLocationException e1)
					{
						System.out.println(e1.getMessage());
					}
				}
			}
		});

		exitFile.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});

		exportFile.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int selection = CONFIRM_DIALOG_NO;

				selection = JOptionPane.showConfirmDialog(null, "Do you want to export alerts?", "Export Alerts",
						JOptionPane.YES_NO_OPTION);

				if(selection == CONFIRM_DIALOG_YES)
				{
					Export.exportAlerts(theAlertTextPane);
					JOptionPane.showMessageDialog(MainFrame.this, "Export successful!", "",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		exportDataMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int selection = CONFIRM_DIALOG_NO;

				selection = JOptionPane.showConfirmDialog(null, "Do you want to export downloaded k-lines?",
						"Export Alerts", JOptionPane.YES_NO_OPTION);

				if(selection == CONFIRM_DIALOG_YES)
				{
					Export.exportKlines(Klines.getkMap());
					JOptionPane.showMessageDialog(MainFrame.this, "Export successful!", "",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		rsiMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(Klines.getMacd() == null || Klines.getRsi() == null)
				{
					JOptionPane.showMessageDialog(MainFrame.this, "No data. Must be connected to server.", "",
							JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					Chart.generatePlot(Klines.getRsi(), "rsi");
				}
			}
		});

		macdMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(Klines.getMacd() == null || Klines.getRsi() == null)
				{
					JOptionPane.showMessageDialog(MainFrame.this, "No data. Must be connected to server.", "",
							JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					Chart.generatePlot(Klines.getMacd(), "macd");
				}
			}
		});
	}

	/**
	 * Set the visual status of program
	 * 
	 * @param connected: true when connected
	 */
	public static void setOnlineStatus(boolean connected)
	{
		if(connected)
		{
			statusLabel.setText("<html>Status: <font color='#50C878'><b>Online</b></font></html>");
		}
		else
		{
			statusLabel.setText("<html>Status: <font color='#F67280'><b>Offline</b></font></html>");
		}
	}

	/**
	 * 
	 * @return Singleton theMainFrame
	 */
	public static MainFrame getTheMainFrame()
	{
		return theMainFrame;
	}

	/**
	 * 
	 * @return value in MACD text field
	 */
	public static double getMacdText()
	{
		if(macdText == null || macdText.getText().isBlank())
		{
			JOptionPane.showMessageDialog(theMainFrame, "MACD limit field is empty", "Warning",
					JOptionPane.INFORMATION_MESSAGE);
			return Driver.ERROR_VALUE;
		}

		char[] arr = macdText.getText().toCharArray();

		for(char c : arr)
		{
			if(!Character.isDigit(c) && c != '-')
			{
				JOptionPane.showMessageDialog(theMainFrame, "MACD limit field contains non-digit values", "Warning",
						JOptionPane.INFORMATION_MESSAGE);
				return Driver.ERROR_VALUE;
			}
		}

		double num = Double.parseDouble(macdText.getText());

		if(num > MAX_MACD || num < MIN_MACD)
		{
			JOptionPane.showMessageDialog(theMainFrame,
					"MACD value should fall between " + MIN_MACD + " and " + MAX_MACD, "Warning",
					JOptionPane.INFORMATION_MESSAGE);
			return Driver.ERROR_VALUE;
		}

		return num;
	}

	/**
	 * 
	 * @return value in RSI text field
	 */
	public static double getRsiText()
	{
		if(rsiText == null || rsiText.getText().isBlank())
		{
			JOptionPane.showMessageDialog(theMainFrame, "RSI limit field is empty", "Warning",
					JOptionPane.INFORMATION_MESSAGE);
			return Driver.ERROR_VALUE;
		}

		char[] arr = rsiText.getText().toCharArray();

		for(char c : arr)
		{
			if(!Character.isDigit(c) && c != '-')
			{
				JOptionPane.showMessageDialog(theMainFrame, "RSI limit field contains non-digit values", "Warning",
						JOptionPane.INFORMATION_MESSAGE);
				return Driver.ERROR_VALUE;
			}
		}

		double num = Double.parseDouble(rsiText.getText());

		if(num > MAX_RSI || num < MIN_RSI)
		{
			JOptionPane.showMessageDialog(theMainFrame, "RSI value should fall between " + MIN_RSI + " and " + MAX_RSI,
					"Warning", JOptionPane.INFORMATION_MESSAGE);
			return Driver.ERROR_VALUE;
		}

		return num;
	}

	/**
	 * 
	 * @return the selected ticker
	 */
	public static String getTicker()
	{
		if(tickerBox == null || tickerBox.getSelectedItem().toString().isBlank())
		{
			return null;
		}

		return Klines.getTickerMap().get(tickerBox.getSelectedItem());
	}

	/**
	 * 
	 * @return the selected interval
	 */
	public static String getInterval()
	{
		if(list == null || list.getSelectedValue().toString().isBlank())
		{
			return null;
		}
		return Klines.getIntervalMap().get(list.getSelectedValue());
	}

	/**
	 * 
	 * @return Singleton theAlertTextPane
	 */
	public static JTextPane getTheAlertTextPane()
	{
		return theAlertTextPane;
	}
	
	public static void setConnectButton(boolean connected)
	{
		if(connected)
		{
			connectButton.setText("Disconnect");
		}
		else 
		{
			connectButton.setText("Connect");
		}
	}
}
