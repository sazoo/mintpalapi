
package com.isoitiro.crypto.mintpal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Mintpal {
  // Recommended frequency to make requests by Mintpal admins
  public static final int    REQUEST_FREQUENCY_MS = 2000;
  
  // TradeData type constants
  public static final int    TRADE_TYPE_BUY       = 0;
  public static final int    TRADE_TYPE_SELL      = 1;
  
  // OrderData type constants
  public static final String ORDER_TYPE_BUY       = "BUY";
  public static final String ORDER_TYPE_SELL      = "SELL";
  
  // MarketChartData period constants
  public static final String CHART_PERIOD_6H      = "6hh";
  public static final String CHART_PERIOD_1D      = "1DD";
  public static final String CHART_PERIOD_3D      = "3DD";
  public static final String CHART_PERIOD_7D      = "7DD";
  public static final String CHART_PERIOD_MAX     = "MAX";
  
  private static Gson        gson;
  
  // Disallow creation of an instance.
  private Mintpal() {
  }
  
  private static void init() {
    if( gson == null ) {
      GsonBuilder builder = new GsonBuilder();
      
      // Commented classes are not parsed using deserializers
      // builder.registerTypeAdapter( MarketSummaryData.class, new MarketSummaryDataDeserializer() );
      builder.registerTypeAdapter( MarketStatsData.class, new MarketStatsDataDeserializer() );
      // builder.registerTypeAdapter( MarketTradesData.class, new MarketTradesDataDeserializer() );
      builder.registerTypeAdapter( TradeData.class, new TradeDataDeserializer() );
      // builder.registerTypeAdapter( MarketOrdersData.class, new MarketOrdersDataDeserializer() );
      builder.registerTypeAdapter( OrderData.class, new OrderDataDeserializer() );
      // builder.registerTypeAdapter( MarketChartData.class, new MarketChartDataDeserializer() );
      builder.registerTypeAdapter( OHLCPointData.class, new OHLCPointDataDeserializer() );
      
      gson = builder.create();
    }
  }
  
  private static String makeRequest( String url ) throws APIException {
    if( gson == null )
      init();
    
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
    } catch( IOException e ) {
      throw new APIException( -1, "Error reading data from stream." );
    }
    
    checkRequestError( data );
    
    return data;
  }
  
  private static void checkRequestError( String data ) throws APIException {
    if( gson.fromJson( data, JsonElement.class ).getClass().equals( JsonObject.class ) ) {
      JsonObject root = gson.fromJson( data, JsonElement.class ).getAsJsonObject();
      
      if( root.has( "error" ) ) {
        JsonObject error = root.get( "error" ).getAsJsonObject();
        int code = error.get( "code" ).getAsInt();
        String message = error.get( "message" ).getAsString();
        throw new APIException( code, message );
      }
    }
  }
  
  public static MarketSummaryData getMarketSummary() throws APIException {
    String url = "https://api.mintpal.com/market/summary/";
    String data = makeRequest( url );
    
    MarketStatsData[] stats = gson.fromJson( data, MarketStatsData[].class );
    MarketSummaryData marketSummaryData = new MarketSummaryData();
    marketSummaryData.stats = stats;
    return marketSummaryData;
  }
  
  public static MarketSummaryData getMarketSummary( String exchange ) throws APIException {
    String url = String.format( "https://api.mintpal.com/market/summary/%s", exchange );
    String data = makeRequest( url );
    
    MarketStatsData[] stats = gson.fromJson( data, MarketStatsData[].class );
    MarketSummaryData marketSummaryData = new MarketSummaryData();
    marketSummaryData.stats = stats;
    return marketSummaryData;
  }
  
  public static MarketStatsData getMarketStats( String marketPair ) throws APIException {
    String url = String.format( "https://api.mintpal.com/market/stats/%s", marketPair );
    String data = makeRequest( url );
    
    MarketStatsData[] stats = gson.fromJson( data, MarketStatsData[].class );
    return stats[0];
  }
  
  public static MarketTradesData getMarketTrades( String marketPair ) throws APIException {
    String url = String.format( "https://api.mintpal.com/market/trades/%s", marketPair );
    String data = makeRequest( url );
    
    MarketTradesData marketTrades = gson.fromJson( data, MarketTradesData.class );
    return marketTrades;
  }
  
  public static MarketOrdersData getMarketOrders( String marketPair, String type ) throws APIException {
    if( !(type.toUpperCase().equals( ORDER_TYPE_BUY ) || type.toUpperCase().equals( ORDER_TYPE_SELL )) ) {
      throw new APIException( -1, type + " is not a valid order type." );
    }
    
    String url = String.format( "https://api.mintpal.com/market/orders/%s/%s", marketPair, type );
    String data = makeRequest( url );
    
    MarketOrdersData marketOrders = gson.fromJson( data, MarketOrdersData.class );
    return marketOrders;
  }
  
  public static MarketChartData getMarketChartData( int marketId ) throws APIException {
    String url = String.format( "https://api.mintpal.com/market/chartdata/%d", marketId );
    String data = makeRequest( url );
    
    OHLCPointData[] points = gson.fromJson( data, OHLCPointData[].class );
    MarketChartData marketChartData = new MarketChartData();
    marketChartData.points = points;
    return marketChartData;
  }
  
  public static MarketChartData getMarketChartData( int marketId, String period ) throws APIException {
    if( !(period.toLowerCase().equals( CHART_PERIOD_6H ) || period.toUpperCase().equals( CHART_PERIOD_1D )
        || period.toUpperCase().equals( CHART_PERIOD_3D ) || period.toUpperCase().equals( CHART_PERIOD_7D ) || period
        .toUpperCase().equals( CHART_PERIOD_MAX )) ) {
      throw new APIException( -1, period + " is not a valid time period." );
    }
    
    String url = String.format( "https://api.mintpal.com/market/chartdata/%d/%s", marketId, period );
    String data = makeRequest( url );
    
    OHLCPointData[] points = gson.fromJson( data, OHLCPointData[].class );
    MarketChartData marketChartData = new MarketChartData();
    marketChartData.points = points;
    return marketChartData;
  }
  
  private static class MarketStatsDataDeserializer implements JsonDeserializer< MarketStatsData > {
    @Override
    public MarketStatsData deserialize( final JsonElement json, Type typeOfT, final JsonDeserializationContext context )
        throws JsonParseException {
      final JsonObject jsonObject = json.getAsJsonObject();
      final MarketStatsData marketStatsData = new MarketStatsData();
      
      marketStatsData.marketId = jsonObject.get( "market_id" ).getAsInt();
      marketStatsData.code = jsonObject.get( "code" ).getAsString();
      marketStatsData.exchange = jsonObject.get( "exchange" ).getAsString();
      marketStatsData.lastPrice = jsonObject.get( "last_price" ).getAsDouble();
      marketStatsData.yesterdayPrice = jsonObject.get( "yesterday_price" ).getAsDouble();
      marketStatsData.change = jsonObject.get( "change" ).getAsDouble();
      marketStatsData.dailyHigh = jsonObject.get( "24hhigh" ).getAsDouble();
      marketStatsData.dailyLow = jsonObject.get( "24hlow" ).getAsDouble();
      marketStatsData.dailyVolume = jsonObject.get( "24hvol" ).getAsDouble();
      marketStatsData.topBid = jsonObject.get( "top_bid" ).getAsDouble();
      marketStatsData.topAsk = jsonObject.get( "top_ask" ).getAsDouble();
      
      return marketStatsData;
    }
  }
  
  private static class TradeDataDeserializer implements JsonDeserializer< TradeData > {
    @Override
    public TradeData deserialize( final JsonElement json, Type typeOfT, final JsonDeserializationContext context )
        throws JsonParseException {
      final JsonObject jsonObject = json.getAsJsonObject();
      final TradeData tradeData = new TradeData();
      
      tradeData.type = jsonObject.get( "type" ).getAsInt();
      tradeData.price = jsonObject.get( "price" ).getAsDouble();
      tradeData.amount = jsonObject.get( "amount" ).getAsDouble();
      tradeData.total = jsonObject.get( "total" ).getAsDouble();
      tradeData.time = jsonObject.get( "time" ).getAsDouble();
      
      return tradeData;
    }
  }
  
  private static class OrderDataDeserializer implements JsonDeserializer< OrderData > {
    @Override
    public OrderData deserialize( final JsonElement json, Type typeOfT, final JsonDeserializationContext context )
        throws JsonParseException {
      final JsonObject jsonObject = json.getAsJsonObject();
      final OrderData orderData = new OrderData();
      
      orderData.price = jsonObject.get( "price" ).getAsDouble();
      orderData.amount = jsonObject.get( "amount" ).getAsDouble();
      orderData.total = jsonObject.get( "total" ).getAsDouble();
      
      return orderData;
    }
  }
  
  private static class OHLCPointDataDeserializer implements JsonDeserializer< OHLCPointData > {
    @Override
    public OHLCPointData deserialize( final JsonElement json, Type typeOfT, final JsonDeserializationContext context )
        throws JsonParseException {
      final JsonObject jsonObject = json.getAsJsonObject();
      final OHLCPointData pointData = new OHLCPointData();
      
      pointData.date = jsonObject.get( "date" ).getAsString();
      pointData.open = jsonObject.get( "open" ).getAsDouble();
      pointData.close = jsonObject.get( "close" ).getAsDouble();
      pointData.high = jsonObject.get( "high" ).getAsDouble();
      pointData.low = jsonObject.get( "low" ).getAsDouble();
      pointData.exchangeVolume = jsonObject.get( "exchange_volume" ).getAsDouble();
      pointData.coinVolume = jsonObject.get( "coin_volume" ).getAsDouble();
      
      return pointData;
    }
  }
  
  public static class MarketSummaryData {
    public MarketStatsData[] stats = null;
    
    @Override
    public String toString() {
      String output = "" + stats[0];
      for( int i = 1; i < stats.length; ++i )
        output += "\n" + stats[i];
      return output;
    }
  }
  
  public static class MarketStatsData {
    public int    marketId       = -1;
    public String code           = null;
    public String exchange       = null;
    public double lastPrice      = -1.0;
    public double yesterdayPrice = -1.0;
    public double change         = -1.0;
    public double dailyHigh      = -1.0;
    public double dailyLow       = -1.0;
    public double dailyVolume    = -1.0;
    public double topBid         = -1.0;
    public double topAsk         = -1.0;
    
    @Override
    public String toString() {
      DecimalFormat format = new DecimalFormat( "###0.00000000" );
      return String.format( "%d,%s,%s,%s,%s,%s,%s,%s,%s", marketId, code, exchange, format.format( lastPrice ),
          format.format( yesterdayPrice ), format.format( change ), format.format( dailyHigh ),
          format.format( dailyLow ), format.format( dailyVolume ) );
    }
  }
  
  public static class MarketTradesData {
    public int         count  = -1;
    public TradeData[] trades = null;
    
    @Override
    public String toString() {
      String output = String.format( "%d trades included.", count );
      for( TradeData t : trades )
        output += "\n" + t;
      return output;
    }
  }
  
  public static class TradeData {
    public int    type   = -1;  // TRADE_TYPE_BUY or TRADE_TYPE_SELL
    public double price  = -1.0;
    public double amount = -1.0; // Number of coins traded
    public double total  = -1.0; // BTC value of coins traded
    public double time   = -1.0; // Microseconds
                                 
    @Override
    public String toString() {
      DecimalFormat format = new DecimalFormat( "###0.00000000" );
      SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
      return String.format( "%s,%s,%s,%s,%s", dateFormat.format( new Date( (long) (time * 1000) ) ),
          (type == Mintpal.TRADE_TYPE_BUY) ? "buy" : "sell", format.format( price ), format.format( amount ),
          format.format( total ) );
    }
  }
  
  public static class MarketOrdersData {
    public int         count  = -1;
    public String      type   = null;
    public OrderData[] orders = null;
    
    @Override
    public String toString() {
      String output = String.format( "%d %s orders included.", count, type );
      for( OrderData o : orders )
        output += "\n" + o;
      return output;
    }
  }
  
  public static class OrderData {
    public double price  = -1.0;
    public double amount = -1.0; // Number of coins traded
    public double total  = -1.0; // BTC value of coins traded
                                 
    @Override
    public String toString() {
      DecimalFormat format = new DecimalFormat( "###0.00000000" );
      return String.format( "%s,%s,%s", format.format( price ), format.format( amount ), format.format( total ) );
    }
  }
  
  public static class MarketChartData {
    public OHLCPointData[] points = null;
    
    @Override
    public String toString() {
      String output = "";
      for( OHLCPointData p : points ) {
        if( p == points[0] )
          output += p;
        output += "\n" + p;
      }
      return output;
    }
  }
  
  public static class OHLCPointData {
    public String date           = null; // "YYYY-MM-DD hh:mm"
    public double open           = -1;
    public double close          = -1;
    public double high           = -1;
    public double low            = -1;
    public double exchangeVolume = -1;
    public double coinVolume     = -1;
    
    @Override
    public String toString() {
      DecimalFormat format = new DecimalFormat( "###0.00000000" );
      return String.format( "%s,%s,%s", date, format.format( open ), format.format( high ), format.format( low ),
          format.format( close ), format.format( coinVolume ), format.format( exchangeVolume ) );
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
