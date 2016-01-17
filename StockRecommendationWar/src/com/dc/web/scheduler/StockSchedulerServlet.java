package com.dc.web.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.dc.main.StockRecommender;

public class StockSchedulerServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	@Override
	public void init() throws ServletException {
		super.init();
		scheduler.scheduleAtFixedRate(new StockRecommender(), 0, 5, TimeUnit.SECONDS);
	}

}
