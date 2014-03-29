
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
      builder.registerTypeAdapter( MarketSummaryData.class, new MarketSummaryDataDeserializer() );
      
      gson = builder.create();
    }
  }
  
  private static void checkGson() throws APIException {
    if( gson == null )
      throw new APIException( -1, "initialize Mintpal API before attempting to retrieve data." );
  }
  
  private static String makeRequest( String url ) throws APIException {
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
      throw new APIException( -1, "Error reading data from stream." );
    }
  }
  
  private static void checkRequestError( String data ) throws APIException {
    JsonObject root = gson.fromJson( data, JsonElement.class ).getAsJsonObject();
    
    if( root.has( "error" ) ) {
      JsonObject error = root.get( "error" ).getAsJsonObject();
      int code = error.get( "code" ).getAsInt();
      String message = error.get( "message" ).getAsString();
      throw new APIException( code, message );
    }
  }
  
  public static MarketTradesData getMarketTrades( String marketPair ) throws APIException {
    String url = "https://api.mintpal.com/market/trades/" + marketPair.toUpperCase();
    String data = makeRequest( url );
    
    checkRequestError( data );
    
    MarketTradesData marketTrades = gson.fromJson( data, MarketTradesData.class );
    return marketTrades;
  }
  
  public static MarketSummaryData getMarketSummary( String marketPair ) throws APIException {
    String url = "https://api.mintpal.com/market/stats/" + marketPair.toUpperCase();
    String data = makeRequest( url );
    
    checkRequestError( data );
    
    MarketSummaryData market = gson.fromJson( data, MarketSummaryData.class );
    return market;
  }
  
  public static ExchangeSummaryData getExchangeSummary() throws APIException {
    String url = "https://api.mintpal.com/market/summary/";
    String data = makeRequest( url );
    
    checkRequestError( data );
    
    ExchangeSummaryData exchangeData = gson.fromJson( data, ExchangeSummaryData.class );
    return exchangeData;
  }
  
  public static ExchangeSummaryData getExchangeSummary( String exchange ) throws APIException {
    String url = "https://api.mintpal.com/market/summary/" + exchange.toUpperCase();
    String data = makeRequest( url );
    
    checkRequestError( data );
    
    ExchangeSummaryData exchangeData = gson.fromJson( data, ExchangeSummaryData.class );
    return exchangeData;
  }
  
  private static class MarketSummaryDataDeserializer implements JsonDeserializer< MarketSummaryData > {
    @Override
    public MarketSummaryData deserialize( final JsonElement json, Type typeOfT, final JsonDeserializationContext context )
        throws JsonParseException {
      final JsonObject jsonObject = json.getAsJsonObject();
      final MarketSummaryData market = new MarketSummaryData();
      
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
      trade.total = jsonObject.get( "total" ).getAsDouble();
      trade.time = jsonObject.get( "time" ).getAsDouble();
      
      return trade;
    }
  }
  
  public static class ExchangeSummaryData {
    public MarketSummaryData[] marketDataArray = null;
    
    @Override
    public String toString() {
      String output = "" + marketDataArray[0];
      for( int i = 1; i < marketDataArray.length; ++i )
        output += "\n" + marketDataArray[i];
      return output;
    }
  }
  
  public static class MarketSummaryData {
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
    public int         count  = -1;
    public TradeData[] trades = null;
    
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
    public double    total     = -1.0; // BTC value of coins traded
    public double    time      = -1.0; // Some weird thing with milliseconds as the fractional part.
                                       
    @Override
    public String toString() {
      DecimalFormat format = new DecimalFormat( "###0.00000000" );
      return String.format( "%s,%s,%s,%s,%s", (type == TYPE_BUY) ? "buy" : "sell", format.format( price ),
          format.format( amount ), format.format( total ), new Date( (long) (time * 1000) ).toString() );
    }
    
  }
  
  @SuppressWarnings( "serial" )
  public static class APIException extends Exception {
    int errorCode = -1;
    
    public APIException( int errorCode, String error ) {
      super( error );
    }
    
    @Override
    public String getMessage() {
      if( errorCode != -1 )
        return "Code " + errorCode + ": " + super.getMessage();
      else
        return super.getMessage();
    }
  }
}

// https://api.mintpal.com/market/summary/
// https://api.mintpal.com/market/stats/MINT/BTC
// https://api.mintpal.com/market/trades/MINT/BTC
