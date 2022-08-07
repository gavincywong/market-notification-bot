package com.github.gavincywong.mbn.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Chart class depicts RSI and MACD values in a line-plot
 * 
 * @author Gavin Wong
 * @version Aug 5, 2022 
 * 
 * Credit: https://stackoverflow.com/questions/32447043/adding-axis-labels-and-titles-to-a-simple-line-graph-chart
 */
public class Chart extends JPanel
{
	private static List<Double> mapValues;
	private static Color lineColor;
	private Color gridColor = new Color(220, 220, 220, 220);
	private Color pointColor = new Color(100, 100, 100, 180);
	private int padding = 25;
	private int labelPadding = 25;
	private int numberYDivisions = 10;
	private int pointWidth = 4;
	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);

	/**
	 * Chart constructor
	 * 
	 * @param mapValues: values extracted from the RSI or MACD map
	 */
	public Chart(List<Double> mapValues)
	{
		if(mapValues == null || mapValues.size() == 0)
		{
			throw new IllegalArgumentException("Error: Map value is empty.");
		}

		this.mapValues = mapValues;
	}

	/**
	 * Generates the graph plot
	 * 
	 * @param map:  contains either RSI or MACD values
	 * @param type: identifies if map contains RSI or MACD values
	 */
	public static void generatePlot(Map<Integer, Double> map, String type)
	{
		mapValues = new ArrayList<>();

		if(map == null)
		{
			throw new IllegalArgumentException("Chart cannot be plotted. Map is null\n");
		}

		for(int i = 0; i < Collections.max(map.keySet()); ++i)
		{
			if(map.get(i) != null)
			{
				mapValues.add(map.get(i));
			}
			else
			{
				mapValues.add(0.0);
			}
		}

		if(type.equalsIgnoreCase("rsi"))
		{
			lineColor = new Color(255, 215, 0, 220);
		}
		else
		{
			lineColor = new Color(255, 0, 255, 220);
		}

		MainPanel mainPanel = new MainPanel(mapValues, type);
		mainPanel.setPreferredSize(new Dimension(800, 600));
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(mainPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/**
	 * Draws the axes, line, and points
	 */
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (mapValues.size() - 1);
		double yScale = ((double) getHeight() - (2 * padding) - labelPadding) / (getMaxValue() - getMinValue());

		List<Point> graphPoints = new ArrayList<>();
		for(int i = 0; i < mapValues.size(); i++)
		{
			int x1 = (int) (i * xScale + padding + labelPadding);
			int y1 = (int) ((getMaxValue() - mapValues.get(i)) * yScale + padding);
			graphPoints.add(new Point(x1, y1));
		}

		g2.setColor(Color.WHITE);
		g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding,
				getHeight() - 2 * padding - labelPadding);
		g2.setColor(Color.BLACK);

		for(int i = 0; i < numberYDivisions + 1; i++)
		{
			int x0 = padding + labelPadding;
			int x1 = pointWidth + padding + labelPadding;
			int y0 = getHeight()
					- ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
			int y1 = y0;
			if(mapValues.size() > 0)
			{
				g2.setColor(gridColor);
				g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
				g2.setColor(Color.WHITE);
				String yLabel = String.format("%s",
						((int) ((getMinValue() + (getMaxValue() - getMinValue()) * ((i * 1.0) / numberYDivisions))
								* 100)) / 100);
				FontMetrics metrics = g2.getFontMetrics();
				int labelWidth = metrics.stringWidth(yLabel);
				g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
			}
			g2.drawLine(x0, y0, x1, y1);
		}

		// and for x axis
		for(int i = 0; i < mapValues.size(); i++)
		{
			if(mapValues.size() > 1)
			{
				int x0 = i * (getWidth() - padding * 2 - labelPadding) / (mapValues.size() - 1) + padding
						+ labelPadding;
				int x1 = x0;
				int y0 = getHeight() - padding - labelPadding;
				int y1 = y0 - pointWidth;
				if((i % ((int) ((mapValues.size() / 20.0)) + 1)) == 0)
				{
					g2.setColor(gridColor);
					g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
					g2.setColor(Color.WHITE);
					String xLabel = i + "";
					FontMetrics metrics = g2.getFontMetrics();
					int labelWidth = metrics.stringWidth(xLabel);
					g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
				}
				g2.drawLine(x0, y0, x1, y1);
			}
		}

		// create x and y axes
		g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
		g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding,
				getHeight() - padding - labelPadding);

		Stroke oldStroke = g2.getStroke();
		g2.setColor(lineColor);
		g2.setStroke(GRAPH_STROKE);
		for(int i = 0; i < graphPoints.size() - 1; i++)
		{
			int x1 = graphPoints.get(i).x;
			int y1 = graphPoints.get(i).y;
			int x2 = graphPoints.get(i + 1).x;
			int y2 = graphPoints.get(i + 1).y;
			g2.drawLine(x1, y1, x2, y2);
		}

		g2.setStroke(oldStroke);
		g2.setColor(pointColor);
		for(int i = 0; i < graphPoints.size(); i++)
		{
			int x = graphPoints.get(i).x - pointWidth / 2;
			int y = graphPoints.get(i).y - pointWidth / 2;
			int ovalW = pointWidth;
			int ovalH = pointWidth;
			g2.fillOval(x, y, ovalW, ovalH);
		}

	}

	/**
	 * 
	 * @return minimum value in the RSI / MACD map
	 */
	private double getMinValue()
	{
		double minValue = Double.MAX_VALUE;

		for(double num : mapValues)
		{
			minValue = Math.min(minValue, num);
		}

		return minValue;
	}

	/**
	 * 
	 * @return maximum value in the RSI / MACD map
	 */
	private double getMaxValue()
	{
		double maxValue = Double.MIN_VALUE;

		for(double num : mapValues)
		{
			maxValue = Math.max(maxValue, num);
		}

		return maxValue;
	}

	static class MainPanel extends JPanel
	{
		/**
		 * Main panel layout
		 * 
		 * @param mapValues: contains RSI / MACD values
		 * @param type:      identifies if map contains RSI or MACD values
		 */
		public MainPanel(List<Double> mapValues, String type)
		{
			setLayout(new BorderLayout());

			JLabel title = new JLabel(type.toUpperCase() + " Chart - " + MainFrame.getTicker());
			title.setFont(new Font("Arial", Font.BOLD, 25));
			title.setHorizontalAlignment(JLabel.CENTER);

			JPanel chart = new Chart(mapValues);
			VerticalPanel vertPanel = new VerticalPanel(type);
			HorizontalPanel horizPanel = new HorizontalPanel();

			add(title, BorderLayout.NORTH);
			add(horizPanel, BorderLayout.SOUTH);
			add(vertPanel, BorderLayout.WEST);
			add(chart, BorderLayout.CENTER);
		}

		class VerticalPanel extends JPanel
		{
			private final String type;

			/**
			 * 
			 * @param type: identifies if map contains RSI or MACD values
			 */
			public VerticalPanel(final String type)
			{
				this.type = type;
				setPreferredSize(new Dimension(25, 0));
			}

			/**
			 * Draws the vertical label
			 */
			@Override
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);

				Graphics2D gg = (Graphics2D) g;
				gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				Font font = new Font("Arial", Font.PLAIN, 15);

				String string = type.toUpperCase() + " Values";

				FontMetrics metrics = g.getFontMetrics(font);
				int width = metrics.stringWidth(string);
				int height = metrics.getHeight();

				gg.setFont(font);

				drawRotate(gg, getWidth(), (getHeight() + width) / 2, 270, string);
			}

			/**
			 * Rotates the label
			 * 
			 * @param gg:    2D graphics
			 * @param x:     width
			 * @param y:     height
			 * @param angle: rotation angle
			 * @param text:  text to rotate
			 */
			public void drawRotate(Graphics2D gg, double x, double y, int angle, String text)
			{
				gg.translate((float) x, (float) y);
				gg.rotate(Math.toRadians(angle));
				gg.drawString(text, 0, 0);
				gg.rotate(-Math.toRadians(angle));
				gg.translate(-(float) x, -(float) y);
			}
		}

		class HorizontalPanel extends JPanel
		{
			public HorizontalPanel()
			{
				setPreferredSize(new Dimension(0, 25));
			}

			/**
			 * Draws the horizontal label
			 */
			@Override
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);

				Graphics2D gg = (Graphics2D) g;
				gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				Font font = new Font("Arial", Font.PLAIN, 15);

				String string = "Intervals - " + MainFrame.getInterval();

				FontMetrics metrics = g.getFontMetrics(font);
				int width = metrics.stringWidth(string);
				int height = metrics.getHeight();

				gg.setFont(font);

				gg.drawString(string, (getWidth() - width) / 2, 11);
			}
		}
	}
}
