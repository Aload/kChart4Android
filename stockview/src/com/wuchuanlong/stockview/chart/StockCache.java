package com.wuchuanlong.stockview.chart;

import java.util.HashMap;
import java.util.Map;

public class StockCache {
	public static String STOCK_INFO = "stock";// 个股行情
	public static String STOCK_TYPE = "stock_type";// 个股行情
	public static String NAME = "name";// 代码
	public static String CODE = "code";// 代码
	public static String MARKET = "market";// 代码
	public static String DAY_CHAR_DATE = "day";
	public static String WEEK_CHAR_DATE = "week";
	public static String MONTH_CHAR_DATE = "month";
//	public static String TIME_CHAR_DATE = "time";
	public static String TIME_CHART_VIEW_DATA = "time_chart";
	public static String WEEK_CHART_VIEW_DATA = "week_chart";
	public static String MONTH_CHART_VIEW_DATA = "month_chart";
	public static String DAY_CHART_VIEW_DATA = "day_chart";
	public static String WUDANG_CHART_DATA = "wudang";

	private static Map<String, Object> map = new HashMap<String, Object>();
	public static String TIME_CHAR_DATE;

	public static void put(String key, Object value) {
		map.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(String key, Class<T> clazz) {
		return (T) map.get(key);
	}
	
	public static void removeAll(){
		if (map!=null){
			map.clear();
		}
	}
}
