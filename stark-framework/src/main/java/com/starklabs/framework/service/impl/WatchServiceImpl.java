package com.starklabs.framework.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.starklabs.framework.controller.MainController;


/**
 * 크립토워치를 호출하여, 데이터를 주기적으로 가져온다
 * 
 * @author Steel Stark
 * @since 2019.06.23
 * @team only one
 */
@Service("watchService")
public class WatchServiceImpl implements WatchService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

	/**
	 * 크립토워치에 거래소를 선택 한 후, Summary를 가져온다
	 * @param exchange
	 * @return
	 */
	@Override
	public HashMap<String, Object> getWatchSmry(String exchange) {
		BufferedReader in = null;
		HashMap<String, Object> rtnMap = new HashMap<String, Object> (); 
		String url = "https://api.cryptowat.ch/markets/coinbase-pro/btcusd/summary";
		
		try {
			URL obj = new URL(url);
	        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	        //전송방식
	        con.setRequestMethod("GET");
	        //Request Header 정의
	        con.setRequestProperty("User-Agent", "Mozilla/5.0");
	        con.setConnectTimeout(1000);       //컨텍션타임아웃 1초
	        
	        Charset charset = Charset.forName("UTF-8");
	        in = new BufferedReader(new InputStreamReader(con.getInputStream(),charset));
	        String inputLine;
	        StringBuffer response = new StringBuffer();
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        
	        // parse by jsonParser
	        rtnMap = setResponseToMap(response.toString());
	        
	        if(rtnMap.size() > 0)
	        	rtnMap.put("status", true);
		} catch(Exception e) {
			LOGGER.error("parse err : " + e.getMessage());
			rtnMap.put("status", false);
			rtnMap.put("err_msg", e.getMessage());
		} finally {
			try {
				if(in != null)
					in.close();
			} catch (IOException e) {}
		}
		
		return rtnMap;
	}
	
	
	/**
	 * response를 맵에 세팅하여 리턴
	 * 
	 * @param str
	 * @return
	 */
	public HashMap<String, Object> setResponseToMap(String response) {
		HashMap<String, Object> rtnMap = new HashMap<String, Object> ();
		JSONParser parser = new JSONParser();
		
		Object obj;
		try {
			obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject resultObject = (JSONObject) jsonObject.get("result");
			JSONObject price = (JSONObject) resultObject.get("price");
			
			rtnMap.put("day-high", price.get("high"));
			rtnMap.put("day-low", price.get("low"));
			rtnMap.put("day-last", price.get("last"));
			
		} catch (ParseException e) {
			e.printStackTrace();
			LOGGER.error("parse error:" + e.getMessage());
		}
		
		return rtnMap;
	}
	
	
	/**
	 * 값이 Long이나 Double이냐에 따라 형변환 
	 * 
	 * @param price
	 * @return
	 */
	public double parsingDouble(Object price) {
		return Double.parseDouble(String.valueOf(price));		
	}
}
