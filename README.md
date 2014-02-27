# Mintpal API

Java-based API to access Mintpal.com data using Google's gson library.

Author is affiliated with neither [Mintpal](https://www.mintpal.com/) nor [Google](https://www.google.com/).

[https://support.mintpal.com/index.php?module=announce&sec=view&id=4](https://support.mintpal.com/index.php?module=announce&sec=view&id=4)

[https://code.google.com/p/google-gson/](https://code.google.com/p/google-gson/)



## Goal

This Java-based Mintpal API is designed to get any data available from the official Mintpal API.



## Functions


### `void Mintpal.init()`

Call once to initialize the gson library used to parse json data.


### `ExchangeOverviewData getExchangeOverview()`

Returns an object containing a list of data for each market pairing in the Mintpal exchange. If a market does not appear in the listing, please contact Mintpal staff to fix it and not the author. The structure of the object is given in the Objects section.


### `MarketData getMarketData( String marketPair )`

Returns an object containing a list of data for a particular market pairing. The structure of the object is given in the Objects section.


### `MarketTradesData getMarketTrades( String marketPair )`

Returns an object containing a list of the past trades for the given market pairing. The structure of the object is given in the Objects section.



## Objects


### `ExchangeOverviewData`

`MarketData[] marketDataArray`

Contains a list of MarketData objects corresponding to each market.


### `MarketData`

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

Base coin you are exchanging to or from. In the case of TOP/BTC or MINT/LTC, `exchange` would be BTC or LTC.

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

Array of Trade objects each containing data about specific trade. `trades[0]` is most recent.

`String marketPair`

Market pair submitted by user and used for the query.


### `TradeData`

Contains data about a single trade on a market.

`int type`

`TYPE_BUY` and `TYPE_SELL` are integers 0 and 1 that correspond with the `type` variable, denoting whether it is a buy or sell.

`double price`

Price at which the coins were traded.

`double btcValue`

BTC value of coins traded.

`amount`

Number of coins traded.

`time`

Timestamp that uses the fractional part to define milliseconds. I don't know, I just multiply it by 1000 when I need to convert it to a date.



## Donations

BTC: 13jg5WJRJDxiHgwPZY5x6ay8a3gtYxzdk4

LTC: LajigoAbGgKT2jyHvYCJuaBqaw4jeC9C9K

XPM: AWJvwE4Q9as8qr5dLU2WNCw2RDe77EV45x

DOGE: DDUfXoeCrWkg1sXV71WaUA6zT1rg5h4Wbm

MINT: MgKnhkCW6qCS73feqxdHRzQpMn3upMApFy