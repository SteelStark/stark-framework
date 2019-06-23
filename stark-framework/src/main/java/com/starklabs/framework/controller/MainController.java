package com.starklabs.framework.controller;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Handles requests for the application home page.
 */
@Controller
public class MainController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	/** 최고점 가격 */
	private int maxPrice = 0;
	
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
	public String price(@RequestParam("max_price") int price, Model model) {
		maxPrice = price;
		
		model.addAttribute("max price", maxPrice);
		model.addAttribute("msg", "change price");
		
		return "jsonView";
	}
	
	/**
	 * 리포트
	 */
	@RequestMapping(value = "/report", method = RequestMethod.GET)
	public String report(Model model) {
		// api 호출
		//https://api.cryptowat.ch/markets/coinbase-pro/btcusd/summary
		
		// 1달 고점
		
		// 1주일 고점
		// 오늘 고점
		// 1주일 저점
		// 오늘 저점
		model.addAttribute("max price", maxPrice);
		
		return "jsonView";
	}
}
