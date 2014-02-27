
package com.isoitiro.crypto.mintpal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Date;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Mintpal {
  private static Gson gson;
  
  // Disallow creation of an instance.
  private Mintpal() {
  }
  
  public static void init() {
    if( gson == null ) {
      GsonBuilder builder = new GsonBuilder();
      
      builder.registerTypeAdapter( TradeData.class, new TradeDataDeserializer() );
      builder.registerTypeAdapter( MarketData.class, new MarketDataDeserializer() );
      
      gson = builder.create();
    }
  }
  
  private static void checkGson() {
    if( gson == null ) {
      System.out.println( "ERROR: Please initialize Mintpal API before attempting to retrieve data." );
      System.exit( 1 );
    }
  }
  
  private static String makeRequest( String url ) {
    checkGson();
    
    String data = "";
    try {
      URL urlObj = new URL( url );
      
      // Have to use a user-agent spoof in order to connect to the Mintpal API (As of 25/2/14)
      URLConnection urlConn = urlObj.openConnection();
      urlConn.addRequestProperty( "User-Agent",
          "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)" );
      urlConn.connect();
      BufferedInputStream inputStream = new BufferedInputStream( urlConn.getInputStream() );
      
      byte[] chunk = new byte[1024];
      int bytesRead = 0;
      while( (bytesRead = inputStream.read( chunk )) != -1 )
        data += new String( chunk, 0, bytesRead );
      
      return data;
    } catch( IOException e ) {
      return "";
    }
  }
  
  public static MarketTradesData getMarketTrades( String marketPair ) {
    String url = "https://api.mintpal.com/market/trades/" + marketPair.toUpperCase();
    String data = makeRequest( url );
    if( data.equals( "" ) )
      return null;
    MarketTradesData marketTrades = gson.fromJson( data, MarketTradesData.class );
    marketTrades.marketPair = marketPair;
    return marketTrades;
  }
  
  public static MarketData getMarketData( String marketPair ) {
    String url = "https://api.mintpal.com/market/stats/" + marketPair.toUpperCase();
    String data = makeRequest( url );
    if( data.equals( "" ) )
      return null;
    MarketData market = gson.fromJson( data, MarketData.class );
    return market;
  }
  
  public static ExchangeOverviewData getExchangeOverview() {
    String url = "https://api.mintpal.com/market/summary/";
    String data = makeRequest( url );
    if( data.equals( "" ) )
      return null;
    ExchangeOverviewData exchange = gson.fromJson( data, ExchangeOverviewData.class );
    return exchange;
  }
  
  private static class MarketDataDeserializer implements JsonDeserializer< MarketData > {
    @Override
    public MarketData deserialize( final JsonElement json, Type typeOfT, final JsonDeserializationContext context )
        throws JsonParseException {
      final JsonObject jsonObject = json.getAsJsonObject();
      final MarketData market = new MarketData();
      
      market.marketId = jsonObject.get( "market_id" ).getAsInt();
      market.code = jsonObject.get( "code" ).getAsString();
      market.exchange = jsonObject.get( "exchange" ).getAsString();
      market.lastPrice = jsonObject.get( "last_price" ).getAsDouble();
      market.yesterdayPrice = jsonObject.get( "yesterday_price" ).getAsDouble();
      market.change = jsonObject.get( "change" ).getAsDouble();
      market.dailyHigh = jsonObject.get( "24hhigh" ).getAsDouble();
      market.dailyLow = jsonObject.get( "24hlow" ).getAsDouble();
      market.dailyVolume = jsonObject.get( "24hvol" ).getAsDouble();
      
      return market;
    }
  }
  
  private static class TradeDataDeserializer implements JsonDeserializer< TradeData > {
    @Override
    public TradeData deserialize( final JsonElement json, Type typeOfT, final JsonDeserializationContext context )
        throws JsonParseException {
      final JsonObject jsonObject = json.getAsJsonObject();
      final TradeData trade = new TradeData();
      
      trade.type = jsonObject.get( "type" ).getAsInt();
      trade.price = jsonObject.get( "price" ).getAsDouble();
      trade.amount = jsonObject.get( "amount" ).getAsDouble();
      trade.btcValue = jsonObject.get( "total" ).getAsDouble();
      trade.time = jsonObject.get( "time" ).getAsDouble();
      
      return trade;
    }
  }
  
  public static class ExchangeOverviewData {
    public MarketData[] marketDataArray = null;
    
    @Override
    public String toString() {
      String output = "" + marketDataArray[0];
      for( int i = 1; i < marketDataArray.length; ++i )
        output += "\n" + marketDataArray[i];
      return output;
    }
  }
  
  public static class MarketData {
    public int    marketId       = -1;
    public String code           = null;
    public String exchange       = null;
    public double lastPrice      = -1.0;
    public double yesterdayPrice = -1.0;
    public double change         = -1.0;
    public double dailyHigh      = -1.0;
    public double dailyLow       = -1.0;
    public double dailyVolume    = -1.0;
    
    @Override
    public String toString() {
      DecimalFormat format = new DecimalFormat( "###0.00000000" );
      return String.format( "%d,%s,%s,%f,%f,%f,%f,%f,%f", marketId, code, exchange, format.format( lastPrice ),
          format.format( yesterdayPrice ), format.format( change ), format.format( dailyHigh ),
          format.format( dailyLow ), format.format( dailyVolume ) );
    }
  }
  
  public static class MarketTradesData {
    public int         count      = -1;
    public TradeData[] trades     = null;
    public String      marketPair = null;
    
    @Override
    public String toString() {
      String output = count + " trades included.";
      for( TradeData t : trades )
        output += "\n" + t;
      return output;
    }
  }
  
  public static class TradeData {
    public final int TYPE_BUY  = 0;
    public final int TYPE_SELL = 1;
    
    public int       type      = -1;  // TYPE_BUY or TYPE_SELL
    public double    price     = -1.0;
    public double    amount    = -1.0; // Number of coins traded
    public double    btcValue  = -1.0; // BTC value of coins traded
    public double    time      = -1.0; // Some weird thing with milliseconds as the fractional part.
                                       
    @Override
    public String toString() {
      DecimalFormat format = new DecimalFormat( "###0.00000000" );
      return String.format( "%s,%s,%s,%s,%s", (type == TYPE_BUY) ? "buy" : "sell", format.format( price ),
          format.format( amount ), format.format( btcValue ), new Date( (long) (time * 1000) ).toString() );
    }
    
  }
}

// TODO: https://api.mintpal.com/market/summary/
// TODO: https://api.mintpal.com/market/stats/MINT/BTC

// https://api.mintpal.com/market/trades/MINT/BTC
