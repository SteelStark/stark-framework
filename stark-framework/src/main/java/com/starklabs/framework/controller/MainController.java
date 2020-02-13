package com.starklabs.framework.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.starklabs.framework.service.impl.WatchService;


/**
 * Handles requests for the application home page.
 */
@Controller
public class MainController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
	
	/** Watch Service */
	@Resource(name = "watchService")
	protected WatchService watchService;
	
	/** 최고점 가격 */
	private double maxPrice = 0;
	
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String home(@RequestParam HashMap<String, Object> paramMap, Model model) {
		
		return "login";
	}
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/main", method = RequestMethod.GET)
	public String main(@RequestParam HashMap<String, Object> paramMap, Model model) {
		
		return "main";
	}
	
	
	/**
	 * 비트코인의 가격을 가져온다
	 */
	@RequestMapping(value = "/price", method = RequestMethod.GET)
	public String price(@RequestParam("max_price") double price, Model model) {
		maxPrice = price;
		
		model.addAttribute("max price", maxPrice);
		model.addAttribute("msg", "change price");
		
		return "jsonView";
	}
	
	/**
	 * 비트코인의 가격을 가져온다
	 */
	@RequestMapping(value = "/sell", method = RequestMethod.GET)
	public String price(@RequestParam("price") double price, @RequestParam("rate") double rate, Model model) {
		model.addAttribute("price", price);
		model.addAttribute("rate", rate);
		model.addAttribute("sell price", (int)(price+(price*rate)));
		
		return "jsonView";
	}
	
	
	/**
	 * 리포트
	 */
	@RequestMapping(value = "/report", method = RequestMethod.GET)
	public String report(Model model) {
		// api 호출
		//https://api.cryptowat.ch/markets/coinbase-pro/btcusd/summary
		//HashMap<String, Object> watchMap = watchService.getWatchSmry("coinbase-pro");
		HashMap<String, Object> watchMap = watchService.getWatchSmry("bithumb");
		double dayHigh = Double.parseDouble(String.valueOf(watchMap.get("day-high")));
		double dayLow = Double.parseDouble(String.valueOf(watchMap.get("day-low")));
		double dayLast = Double.parseDouble(String.valueOf(watchMap.get("day-last")));
		
		if(dayHigh > maxPrice)
			maxPrice = dayHigh;
		double change = (dayHigh-dayLow)/dayHigh;

		HashMap<String, Object> priceMap = new LinkedHashMap<String, Object> ();
		priceMap.put("최근 고점 가격", maxPrice);
		priceMap.put("금일 고점 가격", dayHigh);
		priceMap.put("금일 저점 가격", dayLow);
		priceMap.put("현재 가격", dayLast);
		priceMap.put("변동성", String.format("%.2f", change*100) + "%");
				
		HashMap<String, Object> reportMap = new LinkedHashMap<String, Object> ();
		HashMap<String, Object> shortTermReportMap = new LinkedHashMap<String, Object> ();
		HashMap<String, Object> longTermReportMap = new LinkedHashMap<String, Object> ();
		double todayRate = 1-(dayLast/dayHigh);
		double highRate = 1-(dayLast/maxPrice);
		
		shortTermReportMap.put("금일고점 대비 하락률", String.format("%.2f", todayRate*100) + "%");
		longTermReportMap.put("최근고점 대비 하락률", String.format("%.2f", highRate*100) + "%");
		
		// 심각
		if(todayRate >= 0.10) {
			shortTermReportMap.put("데이 트레이딩 리포트", "하루 10프로 이상 하락이 된 상태로 악재 없는 경우 '강력 매수' 상태");
		} else if(todayRate >= 0.05) {
			shortTermReportMap.put("데이 트레이딩 리포트", "하루 5프로 이상 하락이 된 상태로 악재 및 조정장이 아닌 경우 '매수 추천' 상태");
		} else if(todayRate >= 0.02) {
			shortTermReportMap.put("데이 트레이딩 리포트", "하루 2~5프로 하락이 된 상태로 악재 및 조정장이 아닌 경우 '매수' 상태");
		} else if(todayRate >= 0.01) {
			shortTermReportMap.put("데이 트레이딩 리포트", "하루 1프로 하락이 된 상태로 악재 및 조정장이 아닌 경우 0.75프로 먹기 위한 적절한 '매수' 상태");
		} else {
			double dayLowRecmmd = dayHigh-(dayHigh*0.01);
			double dayMidRecmmd = dayHigh-(dayHigh*0.02);
			double dayHighRecmmd = dayHigh-(dayHigh*0.03);
			
			shortTermReportMap.put("데이 트레이딩 리포트", 
					"금일 고점 대비, 1프로 이상 하락이 아닌 상태로 횡보 단타(1,2,3%)를 노릴 경우, " + 
					String.format("%.0f", dayLowRecmmd) + "," +
					String.format("%.0f", dayMidRecmmd) + "," +
					String.format("%.0f", dayHighRecmmd));
		}
		
		double longTerm1Recmmd = maxPrice-(maxPrice*0.15);
		double longTerm2Recmmd = maxPrice-(maxPrice*0.25);
		double longTerm3Recmmd = maxPrice-(maxPrice*0.35);
		double longTerm4Recmmd = maxPrice-(maxPrice*0.45);
		double longTerm5Recmmd = maxPrice-(maxPrice*0.5);
		
		longTermReportMap.put("중장기 트레이딩 리포트 포지션 1", "최근 고점 대비, 15프로 하락일 경우 1차 매수 " + String.format("%.0f", longTerm1Recmmd));
		longTermReportMap.put("중장기 트레이딩 리포트 포지션 2", "최근 고점 대비, 25프로 하락일 경우 2차 매수 " + String.format("%.0f", longTerm2Recmmd));
		longTermReportMap.put("중장기 트레이딩 리포트 포지션 3", "최근 고점 대비, 35프로 하락일 경우 3차 매수 " + String.format("%.0f", longTerm3Recmmd));
		longTermReportMap.put("중장기 트레이딩 리포트 포지션 4", "최근 고점 대비, 45프로 하락일 경우 4차 매수 " + String.format("%.0f", longTerm4Recmmd));
		longTermReportMap.put("중장기 트레이딩 리포트 포지션 5", "최근 고점 대비, 50프로 하락일 경우 5차 매수 " + String.format("%.0f", longTerm5Recmmd));
		
		reportMap.put("short-term", shortTermReportMap);
		reportMap.put("long-term", longTermReportMap);
		
		model.addAttribute("price", priceMap);
		model.addAttribute("report", reportMap);
		
		return "jsonView";
	}
}
