
package com.isoitiro.crypto.mintpal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.isoitiro.crypto.mintpal.Mintpal.APIException;
import com.isoitiro.crypto.mintpal.Mintpal.MarketChartData;
import com.isoitiro.crypto.mintpal.Mintpal.MarketOrdersData;
import com.isoitiro.crypto.mintpal.Mintpal.MarketStatsData;
import com.isoitiro.crypto.mintpal.Mintpal.MarketSummaryData;
import com.isoitiro.crypto.mintpal.Mintpal.MarketTradesData;

public class Tester {
  public static void main( String[] args ) throws APIException, IOException, InterruptedException {
    File csvOutput = new File( "C:\\TestFile.txt" );
    try {
      csvOutput.createNewFile();
    } catch( IOException e ) {
      e.printStackTrace();
    }
    
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter( new FileWriter( csvOutput ) );
    } catch( IOException e ) {
      e.printStackTrace();
    }
    
    MarketSummaryData ms1 = Mintpal.getMarketSummary();
    writer.append( String.format( "%s\n\n\n", ms1 ) );
    Thread.sleep( Mintpal.REQUEST_FREQUENCY_MS );
    
    MarketSummaryData ms2 = Mintpal.getMarketSummary( "LTC" );
    writer.append( String.format( "%s\n\n\n", ms2 ) );
    Thread.sleep( Mintpal.REQUEST_FREQUENCY_MS );
    
    
    MarketStatsData mst = Mintpal.getMarketStats( "MINT/BTC" );
    writer.append( String.format( "%s\n\n\n", mst ) );
    Thread.sleep( Mintpal.REQUEST_FREQUENCY_MS );
    
    
    MarketTradesData mt = Mintpal.getMarketTrades( "MINT/BTC" );
    writer.append( String.format( "%s\n\n\n", mt ) );
    Thread.sleep( Mintpal.REQUEST_FREQUENCY_MS );
    
    
    MarketOrdersData mo = Mintpal.getMarketOrders( "MINT/BTC", Mintpal.ORDER_TYPE_BUY );
    writer.append( String.format( "%s\n\n\n", mo ) );
    Thread.sleep( Mintpal.REQUEST_FREQUENCY_MS );
    
    
    MarketChartData mc1 = Mintpal.getMarketChartData( 5 );
    writer.append( String.format( "%s\n\n\n", mc1 ) );
    Thread.sleep( Mintpal.REQUEST_FREQUENCY_MS );
    MarketChartData mc2 = Mintpal.getMarketChartData( 5, Mintpal.CHART_PERIOD_MAX );
    writer.append( String.format( "%s", mc2 ) );
    
    writer.close();
  }
}
