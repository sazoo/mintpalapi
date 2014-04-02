# Mintpal API

Java-based API to access Mintpal.com data using Google's gson library.

Author is affiliated with neither [Mintpal](https://www.mintpal.com/) nor [Google](https://www.google.com/).

[https://code.google.com/p/google-gson/](https://code.google.com/p/google-gson/)

[https://www.mintpal.com/api](https://www.mintpal.com/api)



## Goal

To query data from the official Mintpal API through a Java interface.



## Included

In this repository is the Mintpal API query code and a few basic API based utilities.


### MarketLogger

Given an output path and market pairing, this will record trade history as long as it is running.

Ex. `java -jar MarketWatcher.jar .\output.csv MINT/BTC`

Output is stored in a in a list of Comma Seperated Values, but feel free to use any extension and it should be easily imported into Excel or most other spreadsheet programs.



## Constants

```java
// Recommended frequency to make requests by Mintpal admins
public static final int REQUEST_FREQUENCY_MS = 2000;

public static final int TRADE_TYPE_BUY  = 0;
public static final int TRADE_TYPE_SELL = 1;
// Buy and sell constants for `getMarketTrades` queries.


public static final String CHART_PERIOD_6H  = "6hh";
public static final String CHART_PERIOD_1D  = "1DD";
public static final String CHART_PERIOD_3D  = "3DD";
public static final String CHART_PERIOD_7D  = "7DD";
public static final String CHART_PERIOD_MAX = "MAX";
// Time periods used for `getMarketChartData` queries.


public static final String ORDER_TYPE_BUY  = "BUY";
public static final String ORDER_TYPE_SELL = "SELL";
// Buy and sell constants for `getMarketOrders` queries.
```



## Functions

```java
MarketSummaryData getMarketSummary()
MarketSummaryData getMarketSummary( String exchange )
// GET https://api.mintpal.com/market/summary/{EXCHANGE}
// Returns a summary of the entire Mintpal exchange in a list of `MarketStatsData`.
// `exchange` can be specified for filtering based on an exchange coin such as BTC or LTC.


MarketStatsData getMarketStats( String marketPair )
// GET https://api.mintpal.com/market/stats/{COIN}/{EXCHANGE}
// Returns a `MarketStatsData` of an individual market.
// `marketPair` is a String in the format of `Coin/Exchange` such as `MINT/BTC`.


MarketTradesData getMarketTrades( String marketPair )
// GET https://api.mintpal.com/market/trades/{COIN}/{EXCHANGE}
// Returns a list of `TradeData` for an individual market.
// `marketPair` is a String in the format of `Coin/Exchange` such as `MINT/BTC`.


MarketOrdersData getMarketOrders( String marketPair, String type )
// GET https://api.mintpal.com/market/orders/{COIN}/{EXCHANGE}/{TYPE}
// Returns a list of `OrderData` for an individual market.
// `marketPair` is a String in the format of `Coin/ExchangeCoin` such as `MINT/BTC`.
// `type` is either `BUY` or `SELL` and determines which orderbook you receive. A list of values is contained in the Mintpal class.


MarketChartData getMarketChartData( int marketId )
MarketChartData getMarketChartData( int marketId, String period )
// GET https://api.mintpal.com/market/chartdata/{MARKET_ID}/{PERIOD}
// Returns a list of `OHLCData` for the time period.
// `marketId` is the numerical identifier for the market to retrieve data from.
// `period` defines the time span for data to be requested. A list of values is contained in the Mintpal class.


```


## Objects


### `MarketSummaryData`

Stores data for every market in the exchange.

```java
MarketStatsData[] stats
// Contains a list of `MarketStatsData` objects corresponding to each market.
```


### `MarketStatsData`

Contains data about a single market's most recent and daily properties.

```java
int marketId
// Numerical market identifier.

String code
// Ticker for the coin you buy and sell in this market pairing. In the case of MINT/BTC or MINT/LTC, `code` would be MINT.

double lastPrice
// Price at which the coin was last traded before the time of this query.

double yesterdayPrice
// Close price at midnight of the previous day.

String exchange
// Base coin you are exchanging to or from. In the case of MINT/BTC or MINT/LTC, `exchange` would be BTC or LTC.

double change
// Percent change from `yesterdayPrice`

double dailyHigh
// Highest price in the past 24 hours.

double dailyLow
// Lowest price in the past 24 hours.

double dailyVolume
// Trade volume over the past 24 hours.

double topBid
// Highest bid (buy) price for the coin

double topAsk
// Lowest ask (sell) price for the coin
```


### `MarketTradesData`

Contains data about the market's most recent trades.

```java
int count
// Number of transactions returned by the query up to 100.

TradeData[] trades
// Array of `TradeData` objects corresponding to previously executed trades.
// `trades[0]` is most recent.
```

### `TradeData`

Contains data about a single trade on a market.

```java
int type
// `type` is either 0 for a buy or 1 for a sell. A list of values is contained in the Mintpal class.

double price
// Price at which the coins were traded.

double amount
// Number of coins traded.

double total
// Total value of coins traded, calculated in the exchange coin.

double time
// Time at which the trade occured in microseconds.
```


### `MarketOrdersData`

Contains data about either the buy or sell orderbook.

```java
int count
// Total number of orders included in this query up to 50.

String type
// Either `BUY` or `SELL` corresponding to the requested orderbook. A list of values is contained in the Mintpal class.

OrderData[] orders
// Array of `OrderData` objects corresponding to standing orders.
// `orders[0]` is either highest buy or lowest sell.
```


### `OrderData`

Contains data about a single standing order on the market.

```java
double price
// Price at which the standing order was made.

double amount
// Number of coins traded.

double total
// Total value of coins traded, calculated in the exchange coin.
```

### `MarketChartData`

Contains data about historical chart data over a given time period.

```java
OHLCPointData[] points
// Array of `OHLCPointData` corresponding to historical data points.
// `points[0]` is earliest data point.
```

### `OHLCPointData`

Contains a single data point for historical chart data.

```java
String date
// Date of this time frame given in the string "YYYY-MM-DD hh:mm"

double open
// Opening price for the time frame of the data point.

double close
// Closing price for the time frame of the data point.

double high
// Highest price within the time frame of the data point.

double low
// Lowest price within the time frame of the data point.

double exchangeVolume
// Total volume of trades within the time frame of the data point given in a quantity of the exchange coin.

double coinVolume
// Total volume of trades within the time frame of the data point given in a quantity of the coin.
```


## Donations

[http://addie.cc/isoitiro](http://addie.cc/isoitiro)

**AUR:**   `AXKHD8Xy2JXzc2xVt9b1RG8o9XaKQMxdLn`

**BC:**    `BR2uuJ5hzoM6A4oz7TLQvk55uyvQHh8KJf`

**BTCS:**  `1AErJ7vUBhox2kQTpo1qnBjXSKHWeEsfgb`

**CAGE:**  `DqVhNwCYrNHuscXiUkgjyzFf2GuF6oV1CJ`

**CTM:**   `CZq8Lv5y8Efvmhxovb3qghKsoZR2fMo8VK`

**DGB:**   `DSqY1jJTbyh4T9B9bKCucUNRqPaYJYTvVD`

**DOPE:**  `4NioHZvQFT6E1u4SQTVumASVYFASYJs43U`

**DRK:**   `XrLZbRfqJJ97C4yEtKVTSaiCcmfJMmEPob`

**ECC:**   `ELJAWAuuSrzYcdnCRJWeGspWhbJc4PssWH`

**EMO:**   `6N2EGxTCezczxkhUUGLXTEt8YqREFQyiky`

**FLT:**   `FEsrMEvX4nW5btYshKzHUFknXUtFLbYo6J`

**HIC:**   `HUCiS9HbLAb3JfRrCrLyu3eXraf2wt5NML`

**HVC:**   `HTaE9Ys6Bc4AyXLoGbdpbXkb2eMuu1jcou`

**KARM:**  `KSxxbdbYFbgNrCRFD7AMufZJo5KgwV1qkA`

**KDC:**   `LHPE3FSM5LJuAEcYtY5ZhmLbWPoW7bnWEb`

**MRC:**   `1JWBVsUnkhaS2zpxsJupGGMXxBoD93HM3i`

**MRS:**   `MLBjZimin5KgKrUfuvc5rsYZiK4sADobEV`

**MZC:**   `MVBvvjR4EMhYqTaFtJFkWZh5WyPKcsVGxS`

**OLY:**   `5imoxgFvLrhEo3yxJx4skor8XvoEPXstWi`

**PANDA:** `PmwE4ek8SBtnQFWeWXADvxninFeDVvBTKH`

**PENG:**  `p7GsipLPATSA9tBe3ret6rofnuxxQjRbtT`

**PND:**   `PPeqko8mK46yZjL8HyDmpagSxb4wqv8rxw`

**POC:**   `QjZ6wmMNkVdKvb5YG1LKf8s8Khi5u1ub9M`

**POT:**   `PBQTQVxYcGqbZTvzyJAwLnfiYTJX7vj6Mc`

**Q2C:**   `GLxfPnsEhf66CYSDyprrmBqKaMxfnkTkvK`

**RBBT:**  `RMnCghdafjjnSVUKBnB9UcmNn4ixSyfMLj`

**RIC:**   `RQzb7fMuMm8o2CbqdMP55UkHkzo2Vkp6H1`

**SAT:**   `SaeymyjCw8je4b25ArHccDb4Sqwackcfdp`

**SPA:**   `STULeABSX8FWVqLWjFgEwRrtsytgUMR265`

**SUN:**   `SXaRJw7HCstLAsLsQkDVVxkPaWS9ni1fia`

**TAK:**   `TR1HUparXs8hiBD5n6r5QEUs8V4sNReHPC`

**TES:**   `5VbKxFEmP72QPfm9zSy2Z7VjJkFiqGwoQG`

**TOP:**   `TawwSNYP5ZiVDFFQnFe4ngrSYs61g6ZCdD`

**UNO:**   `udiyR7dkBtYaFA6NTvXxDih5DG5yiYNb1W`

**USDe:**  `GZrpdy2QPer9WBeUv4eRQNNQe4y2ZigXfJ`

**UTC:**   `UVbWWFtW7QySGND2MVeBYUL7F9f32euFnB`

**ZED:**   `ZFYmnaVHu712WnWpCWrABzLLRuzd2JRpQ3`

**ZEIT:**  `Mnbv9pDoVzD7s5xJkrQ7KPPYCUhGjkhMG2`