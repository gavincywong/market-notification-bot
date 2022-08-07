# Market Notification Bot

The Market Notification Bot provides a graphical user interface to select a cryptocurrency symbol and an interval to perform MACD and RSI calculations. MACD and RSI stand for Moving Average Convergence Divergence and Relative Strength Index, respectively. These are technical indicators often used in analyzing the market. 

The application allows the user to select an interval, ticker symbol, MACD limit, and RSI limit. The bot will then make an API call to Binance to retrieve the data. 

![GUI](https://github.com/gavincywong/market-notification-bot/blob/main/App%20GUI/GUI.png)

A notification will appear in the alert text pane of the application when the calculated MACD and RSI values fall below the thresholds. In addition, the Discord Bot will also send a notification to the channel where the bot resides. A Discord token needs to be added to Bot.java in order for the bot to work.
![Discord notification](https://github.com/gavincywong/market-notification-bot/blob/main/App%20GUI/Discord%20Notification.png)

# Other Features
- Display MACD and RSI values in charts
![MACD Chart](https://github.com/gavincywong/market-notification-bot/blob/main/App%20GUI/MACD%20Chart.png)
![RSI Chart](https://github.com/gavincywong/market-notification-bot/blob/main/App%20GUI/RSI%20Chart.png)

- Export MACD and RSI data
- Export alert text
