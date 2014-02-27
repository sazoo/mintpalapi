
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
      
      builder.registerTypeAdapter( TradeData.class, new TradeDeserializer() );
      
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
    String url = "http://api.mintpal.com/market/trades/" + marketPair.toUpperCase();
    String data = makeRequest( url );
    if( data.equals( "" ) )
      return null;
    MarketTradesData marketTrades = gson.fromJson( data, MarketTradesData.class );
    marketTrades.setMarketPair( marketPair );
    return marketTrades;
  }
  
  private static class TradeDeserializer implements JsonDeserializer< TradeData > {
    @Override
    public TradeData deserialize( final JsonElement json, Type typeOfT, final JsonDeserializationContext context )
        throws JsonParseException {
      final JsonObject jsonObject = json.getAsJsonObject();
      
      final int type = jsonObject.get( "type" ).getAsInt();
      final double price = jsonObject.get( "price" ).getAsDouble();
      final double btcValue = jsonObject.get( "total" ).getAsDouble();
      final double amount = jsonObject.get( "amount" ).getAsDouble();
      final double time = jsonObject.get( "time" ).getAsDouble();
      
      // Build object
      final TradeData trade = new TradeData();
      trade.setType( type );
      trade.setPrice( price );
      trade.setBtcValue( btcValue );
      trade.setAmount( amount );
      trade.setTime( time );
      return trade;
    }
  }
  
  public static class MarketTradesData {
    private int     count      = -1;
    private TradeData[] trades     = null;
    private String  marketPair = null;
    
    public int getCount() {
      return count;
    }
    
    public void setCount( int count ) {
      if( this.count == -1 )
        this.count = count;
    }
    
    public TradeData[] getTrades() {
      return trades;
    }
    
    public void setTrades( TradeData[] trades ) {
      if( this.trades == null )
        this.trades = trades;
    }
    
    public String getMarketPair() {
      return marketPair;
    }
    
    public void setMarketPair( String marketPair ) {
      if( this.marketPair == null )
        this.marketPair = marketPair;
    }
    
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
    
    private int      type      = -1;  // TYPE_BUY or TYPE_SELL
    private double   price     = -1.0;
    private double   btcValue  = -1.0; // BTC value of coins traded
    private double   amount    = -1.0; // Number of coins traded
    private double   time      = -1.0; // Some weird thing with milliseconds as the fractional part.
                                       
    public int getType() {
      return type;
    }
    
    public void setType( int type ) {
      if( this.type == -1 )
        this.type = type;
    }
    
    public double getPrice() {
      return price;
    }
    
    public void setPrice( double price ) {
      if( this.price == -1.0 )
        this.price = price;
    }
    
    public double getBtcValue() {
      return btcValue;
    }
    
    public void setBtcValue( double btcValue ) {
      if( this.btcValue == -1.0 )
        this.btcValue = btcValue;
    }
    
    public double getAmount() {
      return amount;
    }
    
    public void setAmount( double amount ) {
      if( this.amount == -1.0 )
        this.amount = amount;
    }
    
    public double getTime() {
      return time;
    }
    
    public void setTime( double time ) {
      if( this.time == -1.0 )
        this.time = time;
    }
    
    @Override
    public String toString() {
      DecimalFormat format = new DecimalFormat( "###0.00000000" );
      return String.format( "%s,%s,%s,%s,%s", (type == TYPE_BUY) ? "buy" : "sell", format.format( price ),
          format.format( btcValue ), format.format( amount ), new Date( (long) (time * 1000) ).toString() );
    }
    
  }
}

// TODO: https://api.mintpal.com/market/summary/
// TODO: https://api.mintpal.com/market/stats/MINT/BTC

// https://api.mintpal.com/market/trades/MINT/BTC
