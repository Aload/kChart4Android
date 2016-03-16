package com.wuchuanlong.stockview.chart;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.wedroid.framework.module.business.WeDroidBusiness;
import com.wedroid.framework.module.http.WeDroidRequestCallBack;
/**
 * k线图业务处理类
 * @author 吴传龙
 * <BR/> QQ:312037487
 */
public class StockKChartBusiness extends WeDroidBusiness {
	
	public static final int FEN_SHI_CHART = 300;// 分时图
	public static final int WEEK_CHART = 301;// 周k
	public static final int DAY_CHART = 302;// 日k
	public static final int MONTH_CHART = 303;
	public static final int SELL_AND_BUY = 304;// 五档
	public static final int SINGLE_STOCK = 305;// 个股行情
	public static final int SINGLE_STOCK_NOTICE = 306;// 公告
	public static final int SINGLE_STOCK_MARKET_NEWS = 307;// 研报
	public static final int STOCK_INDEX_K_CHART_LIST = 310;// 证券指数k线图 涨幅榜
	public static final int STOCK_INDEX_K_CHART_LIST_HSL = 308;// 证券指数k线图 换手率榜
	public static final int STOCK_INDEX_K_CHART_LIST_ZF = 309;// 证券指数k线图 振幅榜
	public static final int LOAD_NEWS_DATA = 311;// 公告内容获取

	public StockKChartBusiness(int requestToken, WeDroidRequestCallBack httpRequestCallBack,
			Map<String, String> params) {
		super(requestToken, httpRequestCallBack, params);
	}

	@Override
	public void run() {
		String URI = "http://wxhq.essence.com.cn/market/json";
		if (requestToken == FEN_SHI_CHART) {
			// 分时图
			params.put("funcno", "20001");
			params.put("version", "1");
			params.put("start", "");
			postRequest(URI);
			// {http://wxhq.essence.com.cn/market/json?stock_code=600875&market=SH&count=50&type=day&version=1&funcno=20002}
		} else if (requestToken == WEEK_CHART || requestToken == DAY_CHART || requestToken == MONTH_CHART) {
			// 周k 日k
			params.put("funcno", "20002");
			params.put("version", "1");
			postRequest(URI);
		} else if (requestToken == SELL_AND_BUY) {
			// 五档
			// http://wxhq.essence.com.cn/market/json?funcno=20003&&version=1&stock_list=SH:600875
			params.put("funcno", "20003");
			params.put("version", "1");
			// parameter.addParameter("stock_list",market + ":"
			// +String.valueOf(code));
			postRequest(URI);
		} else if (requestToken == SINGLE_STOCK) {
			// 个股行情
			params.put("funcno", "20000");
			params.put("version", "1");
			// params.put("stock_list",String.valueOf(market + ":" + code));
			params.put("field", String.valueOf("22:23:24:2:3:1:9:12:14:16:10:11:6:18:19:31:13:27:8:21"));
			postRequest(URI);
		} else if (requestToken == SINGLE_STOCK_NOTICE) {
			// 公告
			params.put("funcNo", "200102");
			params.put("curpage", "1");
			params.put("rowofpage", "10");
			postRequest("https://xiaofang2.foundersc.com:8282/servlet/json");
		} else if (requestToken == SINGLE_STOCK_MARKET_NEWS) {
			params.put("funcNo", "200100");
			params.put("curpage", "1");
			params.put("rowofpage", "10");
			postRequest("https://xiaofang2.foundersc.com:8282/servlet/json");
		} else if (requestToken == STOCK_INDEX_K_CHART_LIST || requestToken == STOCK_INDEX_K_CHART_LIST_HSL
				|| requestToken == STOCK_INDEX_K_CHART_LIST_ZF) {
			// 指数k线图中的换手率、振幅等排行榜
			params.put("curPage", "1");
			params.put("rowOfPage", "10");
			params.put("version", "1");
			params.put("curPage", String.valueOf("1"));
			params.put("rowOfPage", String.valueOf("10"));
			String code = params.get("code");
			if (code.equals("000001")) {
				// 上证指数
				params.put("funcno", "21000");
				params.put("type", getType(code));
			} else {
				// 其他指数
				params.put("funcno", "21002");
				params.put("stockIndex", params.get("market") + ":" + code);
			}
			params.put("field", String.valueOf("22:23:24:2:21:12:" + params.get("sort")));
//			params.put("sort", sort);
//			params.put("order", order);
//			params.put("code", code);
//			params.put("market", market);
			postRequest(URI);
		} else if (requestToken == LOAD_NEWS_DATA) {
			// 公告内容获取
			params.put("funcNo", "200101");
			postRequest("https://xiaofang2.foundersc.com:8282/servlet/json?");
		}
	}

	public String getType(String stockCode) {
		String stockType = "0:2:9:18";
		if (stockCode != null) {
			switch (Integer.parseInt(stockCode)) {
			case 000001:
				stockType = "9:10";
				break;// 上证指数
			case 399001:
				stockType = "0:1";
				break;// 深圳成指
			case 399006:
				stockType = "18";
				break;// 创业板指
			case 000002:
				stockType = "0:2:9:18";
				break;// A股指数
			case 000003:
				stockType = "1:10";
				break;// B股指数
			case 399106:
				stockType = "0:1:18";
				break;// 深圳综指
			case 399107:
				stockType = "0";
				break;// 深圳A指
			case 399108:
				stockType = "1";
				break;// 深圳B指
			case 000016:
				stockType = "9";
				break;// 上证50
			case 000010:
				stockType = "9";
				break;// 上证180
			case 9:
				stockType = "9";
				break;// 上证380
			default:
				break;
			}
		}
		return stockType;
	}


	public static int compareDate(String DATE) {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String nowDate = df.format(new Date());
		try {
			Date dt1 = df.parse(DATE);
			Date now = df.parse(nowDate);
			if (dt1.getTime() > now.getTime()) {
				return 1;
			} else if (dt1.getTime() < now.getTime()) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	/**
	 * 判断两个日期间隔 是否大于一天
	 */
	public static boolean compare_date1(String DATE) {

		// Time time= Time.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String nowDate = df.format(new Date());
		try {
			Date dt1 = df.parse(DATE);
			Date now = df.parse(nowDate);
			long daysBetween = (now.getTime() - dt1.getTime() + 1000000) / (3600 * 24 * 1000);
			if (daysBetween > 1) {
				return false;
			} else if (daysBetween <= 1) {
				return true;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return true;
	}

	@Override
	public Object requestSucessFinished(Object result, int requestToken) {
		if (requestToken == WEEK_CHART || requestToken == DAY_CHART || requestToken == MONTH_CHART) {
			return processKChartJson((String) result);
		} else if (requestToken == FEN_SHI_CHART) {
			return processMiuteKchatJson((String) result);
		} else if (requestToken == SELL_AND_BUY) {
			return wuDangProcess((String) result);
		} else if (requestToken == SINGLE_STOCK) {
			return getSingleStockinfoProcess((String) result);
		} else if (requestToken == STOCK_INDEX_K_CHART_LIST || requestToken == STOCK_INDEX_K_CHART_LIST_HSL
				|| requestToken == STOCK_INDEX_K_CHART_LIST_ZF) {
			return kchartIndexList((String) result);
		} 
		return super.requestSucessFinished(result, requestToken);
	}


	public List<PriceInfo> kchartIndexList(String json) {
		List<PriceInfo> infos = new ArrayList<PriceInfo>();
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("results");
			if (jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONArray priceInfoArray = jsonArray.getJSONArray(i);
					PriceInfo priceInfo = new PriceInfo();
					// "22:23:24:2:21:12" + sort
					priceInfo.setName(priceInfoArray.getString(0));
					priceInfo.setMarket(priceInfoArray.getString(1));
					priceInfo.setCode(priceInfoArray.getString(2));
					priceInfo.setNow(format(priceInfoArray.getDouble(3)));
					priceInfo.setType(priceInfoArray.getString(4));
					priceInfo.setYesterday(priceInfoArray.getDouble(5));
					priceInfo.setUppercent(format((priceInfoArray.getDouble(6)) * 100));
					infos.add(priceInfo);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return infos;
	}

	public PriceInfo getSingleStockinfoProcess(String json) {
		PriceInfo priceInfo = new PriceInfo();
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("results");
			if (jsonArray.length() > 0) {
				JSONArray priceInfoArray = jsonArray.getJSONArray(0);
				priceInfo.setName(priceInfoArray.getString(0));
				priceInfo.setMarket(priceInfoArray.getString(1));
				priceInfo.setCode(priceInfoArray.getString(2));
				priceInfo.setNow(priceInfoArray.getDouble(3));
				priceInfo.setUp(format(priceInfoArray.getDouble(4)));
				priceInfo.setUppercent(format((priceInfoArray.getDouble(5)) * 100));
				// 9
				priceInfo.setOpen(format(priceInfoArray.getDouble(6)));
				// 12
				priceInfo.setYesterday(format(priceInfoArray.getDouble(7)));
				// 14
				priceInfo.setAmount(priceInfoArray.getString(8));
				// 16
				priceInfo.setFlux(format((priceInfoArray.getDouble(9)) * 100));
				// 10
				priceInfo.setHigh(format(priceInfoArray.getDouble(10)));
				// 11
				priceInfo.setLow(format(priceInfoArray.getDouble(11)));
				// 6
				priceInfo.setVolume(priceInfoArray.getString(12));
				priceInfo.setInside(format(Double.valueOf((priceInfoArray.getDouble(13)))) + "");
				priceInfo.setOutside(format(Double.valueOf((priceInfoArray.getDouble(14)))));
				priceInfo.setZsz(priceInfoArray.getString(15));
				priceInfo.setPrg(format(Double.valueOf(format1(priceInfoArray.getDouble(16)))));
				priceInfo.setLtsz(priceInfoArray.getString(17));

				priceInfo.setHsl(format((priceInfoArray.getDouble(18)) * 100));
				priceInfo.setType(priceInfoArray.getString(19));
				StockCache.put("stock", priceInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return priceInfo;
	}

	/**
	 * DecimalFormat转换最简便
	 */
	public String format1(double input) {
		DecimalFormat df = new DecimalFormat("#.#");
		return df.format(input);
	}

	/**
	 * DecimalFormat转换最简便
	 */
	public double format(double input) {
		DecimalFormat df = new DecimalFormat("#.00");
		return Double.valueOf(df.format(input));
	}

	public List<SingleStockInfo> processMiuteKchatJson(String resultJson) {
		List<SingleStockInfo> minuteInfos = new ArrayList<SingleStockInfo>();
		try {
			JSONObject jsonObject = new JSONObject(resultJson);
			JSONArray jsonArray = jsonObject.getJSONArray("results");
			if (jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONArray minuteArray = jsonArray.getJSONArray(i);
					int minute = minuteArray.getInt(0);
					double now = minuteArray.getDouble(1);
					double avgPrice = minuteArray.getDouble(2);
					double volume = minuteArray.getDouble(3);
					SingleStockInfo minuteInfo = new SingleStockInfo();
					minuteInfo.setMinute(minute);
					minuteInfo.setNow(now);
					minuteInfo.setAvgPrice(avgPrice);
					minuteInfo.setDealCount(volume);
					if (i == 0) {
						minuteInfo.setColor(KChartUtil.UP_COLOR);
					} else {
						if (now >= jsonArray.getJSONArray(i - 1).getDouble(1)) {
							minuteInfo.setColor(KChartUtil.UP_COLOR);
						} else {
							minuteInfo.setColor(KChartUtil.DOWN_COLOR);
						}
					}
					minuteInfo.setType(Type.HOUR);
					minuteInfos.add(minuteInfo);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		StockCache.put(StockCache.TIME_CHART_VIEW_DATA, minuteInfos);
		return minuteInfos;
	}

	public List<SingleStockInfo> processKChartJson(String mResult) {
		List<SingleStockInfo> infos = new ArrayList<SingleStockInfo>();
		String kType = params.get("type");
		try {
			JSONObject jsonObject = new JSONObject(mResult);
			int errorCode = jsonObject.getInt("errorNo");
			if (0 == errorCode) {
				JSONArray array = jsonObject.getJSONArray("results");
				int multiple = 100;
				String mType = StockCache.get(StockCache.STOCK_TYPE, String.class);
				// PriceInfo info = StockCache.get(StockCache.STOCK_INFO,
				// PriceInfo.class);
				// if (info!=null){
				// mType = String.valueOf(info.getStktype());
				if (null != mType && ("0".equals(mType) || "1".equals(mType) || "2".equals(mType) || "9".equals(mType)
						|| "7".equals(mType) || "15".equals(mType) || "18".equals(mType))) {
					multiple = 100;
				} else if (null == mType){
					multiple = 100;
				}else{
					multiple = 1000;
				}
				// }
				int index = 0;
				/**
				 * 数据截止到上一个交易日
				 */
				if (null != array && array.length() > 0) {
					while (index < array.length()) {
						JSONArray tmp = array.getJSONArray(index);
						int color = tmp.getDouble(1) > tmp.getDouble(3) ? KChartUtil.DOWN_COLOR : KChartUtil.UP_COLOR;// 如果是跌，标记颜色为绿色
						// 如果是涨，标记颜色为红色
						double open = tmp.getDouble(1) / multiple; // 开盘价
						double close = tmp.getDouble(3) / multiple; // 收盘价
						double high = tmp.getDouble(2) / multiple;// 最高价
						double low = tmp.getDouble(4) / multiple;// 最高价
						int date = tmp.getInt(0);// 日期
						double totalCount = tmp.getDouble(5) / 100;// 一天成交成交的手数，总成交量
						double totalPrice = tmp.getDouble(6);// 总成交金额;
						SingleStockInfo singleStockInfo = new SingleStockInfo();
						singleStockInfo.setColor(color);
						singleStockInfo.setClose(close);
						singleStockInfo.setDate(date);
						singleStockInfo.setHigh(high);
						singleStockInfo.setLow(low);
						singleStockInfo.setOpen(open);
						singleStockInfo.setDealCount(totalCount);
						singleStockInfo.setDealPrice(totalPrice);
						if (Type.DAY.getValue().equals(kType)) {
							singleStockInfo.setType(Type.DAY);
						} else if (Type.MONTH.getValue().equals(kType)) {
							singleStockInfo.setType(Type.MONTH);
						} else if (Type.WEEK.getValue().equals(kType)) {
							singleStockInfo.setType(Type.WEEK);
						}
						infos.add(singleStockInfo);
						index++;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		KChartUtil.calcMAF2T(infos, 5);
		KChartUtil.calcMAF2T(infos, 10);
		KChartUtil.calcMAF2T(infos, 20);
		if (Type.DAY.getValue().equals(kType)) {
			StockCache.put(StockCache.DAY_CHART_VIEW_DATA, infos);
		} else if (Type.MONTH.getValue().equals(kType)) {
			StockCache.put(StockCache.MONTH_CHART_VIEW_DATA, infos);
		} else if (Type.WEEK.getValue().equals(kType)) {
			StockCache.put(StockCache.WEEK_CHART_VIEW_DATA, infos);
		}
		return infos;
	}

	/**
	 * 五档
	 */
	public WuDangInfo wuDangProcess(String resultJson) {
		WuDangInfo wuDangInfo = new WuDangInfo();
		try {
			JSONObject jsonObject = new JSONObject(resultJson);
			JSONArray jsonArray = jsonObject.getJSONArray("results");
			if (jsonArray.length() > 0) {
				JSONArray wudangArray = jsonArray.getJSONArray(0);
				// 股票代码
				String code = wudangArray.getString(0);
				// 股票名称
				String name = wudangArray.getString(1);
				// 股票类型
				String stktype = wudangArray.getString(2);
				// 市场类型
				String market = wudangArray.getString(3);
				// 日期
				String date = wudangArray.getString(4);
				// 分钟数
				String minute = wudangArray.getString(5);
				// 卖价5
				double sell5 = KChartUtil.format2(wudangArray.getDouble(6));
				// 卖价4
				double sell4 = KChartUtil.format2(wudangArray.getDouble(7));
				// 卖价3
				double sell3 = KChartUtil.format2(wudangArray.getDouble(8));
				// 卖价2
				double sell2 = KChartUtil.format2(wudangArray.getDouble(9));
				// 卖价1
				double sell1 = KChartUtil.format2(wudangArray.getDouble(10));

				// 卖量5
				double sellValume5 = KChartUtil.format2(wudangArray.getDouble(11));
				// 卖量4
				double sellValume4 = KChartUtil.format2(wudangArray.getDouble(12));
				// 卖量3
				double sellValume3 =KChartUtil.format2( wudangArray.getDouble(13));
				// 卖量2
				double sellValume2 = KChartUtil.format2(wudangArray.getDouble(14));
				// 卖量1
				double sellValume1 = KChartUtil.format2(wudangArray.getDouble(15));

				// 买价1
				double buy1 = KChartUtil.format2(wudangArray.getDouble(16));
				// 买价2
				double buy2 = KChartUtil.format2(wudangArray.getDouble(17));
				// 买价3
				double buy3 = KChartUtil.format2(wudangArray.getDouble(18));
				// 买价4
				double buy4 = KChartUtil.format2(wudangArray.getDouble(19));
				// 买价5
				double buy5 = KChartUtil.format2(wudangArray.getDouble(20));

				// 买量1
				double buyValume1 = KChartUtil.format2(wudangArray.getDouble(21));
				// 买量2
				double buyValume2 = KChartUtil.format2(wudangArray.getDouble(22));
				// 买量3
				double buyValume3 = KChartUtil.format2(wudangArray.getDouble(23));
				// 买量4
				double buyValume4 = KChartUtil.format2(wudangArray.getDouble(24));
				// 买量5
				double buyValume5 = KChartUtil.format2(wudangArray.getDouble(25));

				// 昨收
				double yesterday = wudangArray.getDouble(26);
				// 今开
				double open = wudangArray.getDouble(27);
				// 最高
				double high = wudangArray.getDouble(28);
				// 最低
				double low = wudangArray.getDouble(29);
				// 现价
				double now = wudangArray.getDouble(30);
				// 总成交量
				double totalValume = wudangArray.getDouble(31);
				// 总成交额
				double totalAmount = wudangArray.getDouble(32);
				// 外盘
				double outside = wudangArray.getDouble(33);
				// 内盘
				double inside = wudangArray.getDouble(34);
				// 涨跌
				double up = wudangArray.getDouble(35);
				// 涨跌百分比
				double uppercent = wudangArray.getDouble(36);
				// 成交手数
				double volume = wudangArray.getDouble(37);
				// 量比
				double volrate = wudangArray.getDouble(38);
				// PE(市盈率)
				double pgr = wudangArray.getDouble(39);
				// 拼音简称
				String pyname = wudangArray.getString(40);

				wuDangInfo = new WuDangInfo();
				wuDangInfo.setCode(code);
				wuDangInfo.setName(name);
				wuDangInfo.setStktype(stktype);
				wuDangInfo.setMarket(market);
				wuDangInfo.setDate(date);
				wuDangInfo.setMinute(minute);
				wuDangInfo.setSell5(sell5);
				wuDangInfo.setSell4(sell4);
				wuDangInfo.setSell3(sell3);
				wuDangInfo.setSell2(sell2);
				wuDangInfo.setSell1(sell1);
				wuDangInfo.setSellValume5(sellValume5);
				wuDangInfo.setSellValume4(sellValume4);
				wuDangInfo.setSellValume3(sellValume3);
				wuDangInfo.setSellValume2(sellValume2);
				wuDangInfo.setSellValume1(sellValume1);
				wuDangInfo.setBuy1(buy1);
				wuDangInfo.setBuy2(buy2);
				wuDangInfo.setBuy3(buy3);
				wuDangInfo.setBuy4(buy4);
				wuDangInfo.setBuy5(buy5);
				wuDangInfo.setBuyValume1(buyValume1);
				wuDangInfo.setBuyValume2(buyValume2);
				wuDangInfo.setBuyValume3(buyValume3);
				wuDangInfo.setBuyValume4(buyValume4);
				wuDangInfo.setBuyValume5(buyValume5);
				wuDangInfo.setYesterday(yesterday);
				wuDangInfo.setOpen(open);
				wuDangInfo.setHigh(high);
				wuDangInfo.setLow(low);
				wuDangInfo.setNow(now);
				wuDangInfo.setTotalValume(totalValume);
				wuDangInfo.setTotalAmount(totalAmount);
				wuDangInfo.setOutside(outside);
				wuDangInfo.setInside(inside);
				wuDangInfo.setUp(up);
				wuDangInfo.setUppercent(uppercent);
				wuDangInfo.setVolume(volume);
				wuDangInfo.setVolrate(volrate);
				wuDangInfo.setPgr(pgr);
				wuDangInfo.setPyname(pyname);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		StockCache.put(StockCache.WUDANG_CHART_DATA, wuDangInfo);
		return wuDangInfo;
	}

}
