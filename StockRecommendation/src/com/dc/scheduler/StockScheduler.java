package com.dc.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.dc.main.StockRecommender;

public class StockScheduler  {
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public static void main(String[] args) throws Exception{
		scheduler.scheduleAtFixedRate(new StockRecommender(), 0, 5, TimeUnit.SECONDS);		
	}

}
