package com.starklabs.framework.service.impl;

import java.util.HashMap;


/**
 * 크립토워치를 호출하여, 데이터를 주기적으로 가져온다
 * 
 * @author Steel Stark
 * @since 2019.06.23
 * @team only one
 */
public class WatchServiceImpl implements WatchService {

	/**
	 * 크립토워치에 거래소를 선택 한 후, Summary를 가져온다
	 * @param exchange
	 * @return
	 */
	@Override
	public HashMap<String, Double> getWatchSmry(String exchange) {
		HashMap<String, Double> rtnObj = new HashMap<String, Double> ();
		
		
		return rtnObj;
	}
}
