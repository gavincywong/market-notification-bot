package com.github.gavincywong.mbn.gui;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import javax.swing.JTextPane;
import java.awt.Color;

/**
 * ConnectionLog class models the connection JDialog
 * 
 * @author Gavin Wong
 * @version Aug 5, 2022
 */
@SuppressWarnings("serial")
public class ConnectionLog extends JDialog
{
	private static JTextPane theTextPane = new JTextPane();

	/**
	 * ConnectionLog ctor
	 */
	public ConnectionLog()
	{
		JScrollPane scrollPane = new JScrollPane();
		setSize(400, 200);
		setLocationRelativeTo(null);
		setTitle("Connection Log");
		getContentPane().setLayout(new MigLayout("", "[grow]", "[grow]"));
		getContentPane().add(scrollPane, "cell 0 0,grow");

		theTextPane.setBackground(Color.GRAY);
		theTextPane.setEditable(false);
		scrollPane.setViewportView(theTextPane);
	}

	/**
	 * 
	 * @return Singleton ConnectionLog textpane
	 */
	public static JTextPane getTheTextPane()
	{
		return theTextPane;
	}
}
