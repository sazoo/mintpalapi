
package com.isoitiro.crypto.mintpal.marketlogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.isoitiro.crypto.mintpal.Mintpal;
import com.isoitiro.crypto.mintpal.Mintpal.APIException;
import com.isoitiro.crypto.mintpal.Mintpal.MarketTradesData;
import com.isoitiro.crypto.mintpal.Mintpal.TradeData;

public class MarketLogger implements Runnable {
  private BufferedWriter writer;
  
  private String         marketPair;
  private double         lastTradeTime;
  
  public static void main( String[] args ) {
    if( args.length < 2 ) {
      System.out.println( "Run this program with an output file and market pair as arguments. i.e.\n"
          + "java MarketLogger \"C:\\output.csv\" \"MINT/BTC\"" );
      System.out.println( "It will not overwrite a file if it already exists and will, instead, "
          + "create a new file with an appended number." );
      
      System.exit( 1 );
    }
    
    Mintpal.init();
    
    ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
    ex.scheduleAtFixedRate( new MarketLogger( args[0], args[1] ), 0, 2, TimeUnit.SECONDS );
  }
  
  public MarketLogger( String outputFile, String marketPair ) {
    this.marketPair = marketPair;
    
    // Create output file.
    File csvOutput = new File( outputFile );
    int append = 1;
    while( csvOutput.exists() ) { // Try to find a filename that isn't in use.
      csvOutput = new File( outputFile + "." + append );
      ++append;
    }
    
    try {
      csvOutput.createNewFile();
    } catch( IOException e ) {
      e.printStackTrace();
    }
    
    try {
      writer = new BufferedWriter( new FileWriter( csvOutput ) );
      writer.write( "Type,Price,Amount,BTC Value,Time\n" );
    } catch( IOException e ) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void run() {
    MarketTradesData marketTrades;
    try {
      marketTrades = Mintpal.getMarketTrades( marketPair );
      if( marketTrades == null )
        return;
      
      TradeData[] trades = marketTrades.trades;
      
      try {
        for( int i = trades.length - 1; i >= 0; --i ) {
          TradeData t = trades[i];
          if( lastTradeTime == 0 || t.time > lastTradeTime ) {
            lastTradeTime = t.time;
            System.out.println( t );
            
            writer.append( t + "\n" );
          }
        }
        
        writer.flush();
      } catch( IOException e ) {
        e.printStackTrace();
      }
    } catch( APIException e1 ) {
      System.out.println( "Mintpal did not respond." );
    }
  }
}
