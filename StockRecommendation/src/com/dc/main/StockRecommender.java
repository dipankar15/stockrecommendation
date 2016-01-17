package com.dc.main;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;

import com.dc.screener.IScreener;

public class StockRecommender implements Runnable {
	
	public static final String DELETE_RECOMMENDATION ="delete from dc.STOCK_RECOMMENDATIONS where RECO_DATE = ?"; 
	public static final String INSERT_RECOMMENDATION ="insert into dc.STOCK_RECOMMENDATIONS values (?,?)";
	public static final String SELECT_RECOMMENDATION ="select  Stock_name,Count(RECO_DATE) from dc.STOCK_RECOMMENDATIONS where RECO_DATE between ? and ? group by Stock_name order by count(RECO_DATE) desc FETCH FIRST 5 ROWS ONLY";
	
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://127.10.186.2:3306/dc";

	//  Database credentials
	static final String USER = "admin412ExUa";
	static final String PASS = "r8LWifsMb2uL";
	   
	public void run(){
		cleanOlderRecommendations();
		//saveRecommendations(extractRecommendations());
		getRecommendations();
	}

	private static Set<String> extractRecommendations(){
		Set<String> recommendations = new HashSet<String>();
		 try {
			Reflections reflections = new Reflections("com.dc.screener.impl");
			 
			 Set<Class<? extends IScreener>> subTypes = 
			           reflections.getSubTypesOf(IScreener.class);
			 for (Class<? extends IScreener> screener : subTypes) {
				 recommendations.addAll(screener.newInstance().execute());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recommendations;
	}
	
	private static void cleanOlderRecommendations(){
		   Connection conn = null;
		   PreparedStatement pstmt = null;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("cleanOlderRecommendations...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      pstmt = conn.prepareStatement(DELETE_RECOMMENDATION);
		      pstmt.setDate(1, new Date(subtractDays(new Date(System.currentTimeMillis()), 11)));
		      pstmt.executeUpdate();
		      
		      pstmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(pstmt!=null)
		            pstmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		  
		}
	
	private static void saveRecommendations(Set<String> recommendations){
	   Connection conn = null;
	   PreparedStatement pstmt = null;
	   try{
	      //STEP 2: Register JDBC driver
	      Class.forName(JDBC_DRIVER);

	      //STEP 3: Open a connection
	      System.out.println("saveRecommendations...");
	      conn = DriverManager.getConnection(DB_URL,USER,PASS);

	      //STEP 4: Execute a query
	      pstmt = conn.prepareStatement(INSERT_RECOMMENDATION);
	      pstmt.setDate(1, new Date(System.currentTimeMillis()));
	      for (String recommendation : recommendations) {	    	  
	    	  pstmt.setString(2, recommendation);
	    	  pstmt.executeUpdate();
		}
	      
	      pstmt.close();
	      conn.close();
	   }catch(SQLException se){
	      //Handle errors for JDBC
	      se.printStackTrace();
	   }catch(Exception e){
	      //Handle errors for Class.forName
	      e.printStackTrace();
	   }finally{
	      //finally block used to close resources
	      try{
	         if(pstmt!=null)
	            pstmt.close();
	      }catch(SQLException se2){
	      }// nothing we can do
	      try{
	         if(conn!=null)
	            conn.close();
	      }catch(SQLException se){
	         se.printStackTrace();
	      }//end finally try
	   }//end try
	   
	}
	
	private static Set<String> getRecommendations(){
		Connection conn = null;
		PreparedStatement pstmt = null;
		Set<String> recommendations = new HashSet<String>();
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("getRecommendations...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      pstmt = conn.prepareStatement(SELECT_RECOMMENDATION);
		      pstmt.setDate(1, new Date(subtractDays(new Date(System.currentTimeMillis()), 10)));
		      pstmt.setDate(2, new Date(System.currentTimeMillis()));
		      ResultSet rs = pstmt.executeQuery();
		      
		      
		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name		         
		         String stockName = rs.getString("Stock_name");		         
		         //Display values
		         System.out.println("Stock Name: " + stockName);
		         recommendations.add(stockName);
		         
		      }
		      //STEP 6: Clean-up environment
		      rs.close();
		      pstmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(pstmt!=null)
		            pstmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try		   
		return recommendations;
	}
	
	/**
	 * subtract days to date in java
	 * @param date
	 * @param days
	 * @return
	 */
	public static long subtractDays(Date date, int days) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, -days);				
		return cal.getTime().getTime();
	}
}
