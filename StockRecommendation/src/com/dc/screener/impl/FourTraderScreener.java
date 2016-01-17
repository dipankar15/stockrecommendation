package com.dc.screener.impl;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.dc.screener.IScreener;

public class FourTraderScreener implements IScreener {

	public List<String> execute() throws Exception {
		List<String> recommendations = new ArrayList<String>();
		Document doc = Jsoup.connect("http://www.4-traders.com/top-records/ratings/?Req=0|10|x|x|x|0|127;|29|26|30|30|30|D|D|D|D|D|:::|0|10|0|10|0|10|0|10|0|10|0|10|0|10|0|10|0|10|0|10|0|10|0|10|0|10|0|10|0|10|0|10|0|10|0|10|0|10|0|3|0|3|0|3|0|10|0|10|0|10|0|10|0|10|0|10|0|10|0|10&p=1").get();
		
		Element table = doc.getElementById("ZBS_restab"); 
		
			  for (Element row : table.select("tr")) {				
				  for (Element td : table.select("td")) {
					  String str=null;
					  if(td.id().contains("iAL")){
						  //System.out.println(td.outerHtml());
						  str = td.outerHtml().substring(td.outerHtml().indexOf("ZBS_setCookie("), td.outerHtml().indexOf("</a>"));
						 // System.out.println(str);
						  str = str.substring(str.indexOf("5)")+4);
						  System.out.println(str);
						  recommendations.add(str);
					  }					 
				}
				 // System.out.println(row.outerHtml());
			  }
	
		return recommendations;
	}
	
public static void main(String[] args) throws Exception {
	FourTraderScreener v = new FourTraderScreener();
	v.execute();
	}

}
