/*
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.dc.sms;

import java.net.HttpURLConnection;
  
public class Way2Sms {
	 private static int responseCode = -1;
	 private static String userCredentials = null;
	 private static String cookie = null;
	 private static String site = null;
	 private static String actionStr = null;
	 private static Credentials credentials = new Credentials();

	 public static void main(String[] args) {
	  
	  login("9830796726", "M2222A");
	  sendSMS("9830796726", "test");
	  
	  System.out.println("Message has been sent successfully!");
	 }

	 private static void setProxy(String host, int port) {
	  URLConnector.setProxy(host, port);
	 }

	 private static void getSite() {
	  URLConnector.connect("http://www.way2sms.com/", false, "GET", null, null);
	  responseCode = URLConnector.getResponseCode();
	  if(responseCode != HttpURLConnection.HTTP_MOVED_TEMP && responseCode != HttpURLConnection.HTTP_OK)
	   exit("getSite failed!");
	  else {
	   site = URLConnector.getLocation();
	   if(site != null)
	    site = site.substring(7, site.length() - 1);
	  }
	  URLConnector.disconnect();
	 }

	 private static void preHome() {
	  URLConnector.connect("http://" + site + "/content/prehome.jsp", false, "GET", null, null);
	  responseCode = URLConnector.getResponseCode();
	  if(responseCode != HttpURLConnection.HTTP_MOVED_TEMP && responseCode != HttpURLConnection.HTTP_OK)
	   exit("preHome failed");
	  else
	   cookie = URLConnector.getCookie();
	  URLConnector.disconnect();
	 }

	 public static void login(String uid, String pwd) {
	  getSite();
	  preHome();

	  String location = null;

	  credentials.set("username", uid);
	  credentials.append("password", pwd);
	  credentials.append("button", "Login");
	  userCredentials = credentials.getUserCredentials();

	  URLConnector.connect("http://" + site + "/Login1.action", false, "POST", cookie, userCredentials);
	  responseCode = URLConnector.getResponseCode();
	  if(responseCode != HttpURLConnection.HTTP_MOVED_TEMP && responseCode != HttpURLConnection.HTTP_OK)
	   exit("authentication failed!");
	  else
	   location = URLConnector.getLocation();
	  URLConnector.disconnect();

	  URLConnector.connect(location, false, "GET", cookie, null);
	  responseCode = URLConnector.getResponseCode();
	  if(responseCode != HttpURLConnection.HTTP_MOVED_TEMP && responseCode != HttpURLConnection.HTTP_OK)
	   exit("redirection failed!");
	  URLConnector.disconnect();
	 }

	 private static void getActionString() {
	  URLConnector.connect("http://" + site + "/jsp/InstantSMS.jsp", false, "GET", cookie, null);
	  responseCode = URLConnector.getResponseCode();
	  if(responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_OK) {
	   String str = URLConnector.getResponse();

	   String aStr = "name=\"Action\" id=\"Action\"";
	   int aStrLen = aStr.length();

	   int index = str.indexOf(aStr);
	   if(index > 0) {
	    str = str.substring(index + aStrLen);
	    index = str.indexOf("\"");
	    if(index > 0) {
	     str = str.substring(index + 1);
	     index = str.indexOf("\"");
	     if(index > 0)
	      actionStr = str.substring(0, index);
	    }
	   }
	  } else
	   exit("getActionString failed!");
	  URLConnector.disconnect();
	 }

	 public static void sendSMS(String receiversMobNo, String msg) {
	  getActionString();

	  credentials.reset();
	  credentials.set("HiddenAction", "instantsms");
	  credentials.append("catnamedis", "Birthday");
	  if(actionStr != null)
	   credentials.append("Action", actionStr);
	  else
	   exit("Action string missing!");
	  credentials.append("chkall", "on");
	  credentials.append("MobNo", receiversMobNo);
	  credentials.append("textArea", msg);

	  userCredentials = credentials.getUserCredentials();

	  URLConnector.connect("http://" + site + "/quicksms.action", true, "POST", cookie, userCredentials);
	  responseCode = URLConnector.getResponseCode();
	  if(responseCode != HttpURLConnection.HTTP_MOVED_TEMP && responseCode != HttpURLConnection.HTTP_OK)
	   exit("sendSMS failed!");
	  URLConnector.disconnect();
	 }

	 private static void sendSMS(String[] receiversMobNos, String msg) {
	 int noOfReceivers = receiversMobNos.length;

	 for(int i = 0; i < noOfReceivers; i++)
	  sendSMS(receiversMobNos[i], msg);
	 }

	 private static void exit(String errorMsg) {
	  System.err.println(errorMsg);
	  System.exit(1);
	 }
	}
