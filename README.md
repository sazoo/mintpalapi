# Mintpal API

Java-based API to access Mintpal.com data using Google's gson library.

Author is affiliated with neither [Mintpal](https://www.mintpal.com/) nor [Google](https://www.google.com/).

[https://www.mintpal.com/api](https://www.mintpal.com/api)

[https://code.google.com/p/google-gson/](https://code.google.com/p/google-gson/)



## Goal

This Java-based Mintpal API is designed to get any data available from the official Mintpal API.



## Included

Included in this repository is the Mintpal API query code and a few basic API based utilities.


### MarketWatcher

Given an output path and market pairing, this will record trade history as long as it is running.

Ex. `java -jar MarketWatcher.jar .\output.csv MINT/BTC`

Output is stored in a in a list of Comma Seperated Values, but feel free to use any extension and it should be easily imported into Excel or most other spreadsheet programs.



## Functions


### `void Mintpal.init()`

Call once to initialize the gson library used to parse json data.


### `ExchangeSummaryData getExchangeSummary()`

### `ExchangeSummaryData getExchangeSummary( String exchange )`

Returns a summary of the entire Mintpal exchange in a list of `MarketSummaryData`. Optionally `exchange` can be specified for filtering based on an exchange coin such as BTC or LTC.


### `MarketSummaryData getMarketSummary( String marketPair )`

`marketPair` is a String in the format of `Coin/Exchange` such as `MINT/BTC`.

Returns a `MarketSummaryData` of an individual market.


### `MarketTradesData getMarketTrades( String marketPair )`

`marketPair` is a String in the format of `Coin/Exchange` such as `MINT/BTC`.

Returns a list of `TradeData` for an individual market.



## Objects


### `ExchangeSummaryData`

Stores data for every market in the exchange.

`MarketSummaryData[] marketDataArray`

Contains a list of `MarketSummaryData` objects corresponding to each market.


### `MarketSummaryData`

Contains data about a single market's most recent and daily properties.

`int marketId`

Numerical market identifier.

`String code`

Ticker for the coin you buy and sell in this market pairing. In the case of MINT/BTC or MINT/LTC, `code` would be MINT.

`double lastPrice`

Price at which the coin was last traded before the time of this query.

`double yesterdayPrice`

Close price at midnight of the previous day.

`String exchange`

Base coin you are exchanging to or from. In the case of MINT/BTC or MINT/LTC, `exchange` would be BTC or LTC.

`double change`

Percent change from `yesterdayPrice`

`double dailyHigh`

Highest price in the past 24 hours.

`double dailyLow`

Lowest price in the past 24 hours.

`double dailyVolume`

Trade volume over the past 24 hours.


### `MarketTradesData`

Contains data about the market's most recent trades.

`int count`

Number of transactions returned by the query.

`TradeData[] trades`

Array of `TradeData` objects corresponding to previously executed trades. `trades[0]` is most recent.


### `TradeData`

Contains data about a single trade on a market.

`int type`

`TYPE_BUY` and `TYPE_SELL` are integers 0 and 1 that correspond with the `type` variable, denoting whether it is a buy or sell.

`double price`

Price at which the coins were traded.

`amount`

Number of coins traded.

`double total`

Total value of coins traded, calculated in the exchange coin.

`time`

Time at which the trade occured in microseconds.



## Donations

[http://addie.cc/isoitiro](http://addie.cc/isoitiro)

**BC:** `BR2uuJ5hzoM6A4oz7TLQvk55uyvQHh8KJf`