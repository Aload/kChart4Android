package com.wuchuanlong.stockview.chart;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wedroid.framework.module.business.WeDroidBusiness;
import com.wedroid.framework.module.http.WeDroidRequestCallBack;

public class StockBusiness extends WeDroidBusiness {
	
	public static final String URI = "http://wxhq.essence.com.cn/market/json";
	public static final int FEN_SHI_CHART = 0;
	public static final int WEEK_CHART = 1;
	public static final int DAY_CHART = 2;
	public static final int MONTH_CHART = 3;
	public static final int SELL_AND_BUY = 4;// �嵵
	public static final int SINGLE_STOCK = 5;// ��������

	public StockBusiness(int requestToken, WeDroidRequestCallBack httpRequestCallBack, Map<String, String> params) {
		super(requestToken, httpRequestCallBack, params);
	}

	@Override
	public void run() {
		if (requestToken == FEN_SHI_CHART) {
			params.put("funcno", "20001");
			params.put("version", "1");
			params.put("start", "");
			// {http://wxhq.essence.com.cn/market/json?stock_code=600875&market=SH&count=50&type=day&version=1&funcno=20002}
		} else if (requestToken == WEEK_CHART || requestToken == DAY_CHART || requestToken == MONTH_CHART) {
			params.put("funcno", "20002");
			params.put("version", "1");
		} else if (requestToken == SELL_AND_BUY) {
			// �嵵
			// http://wxhq.essence.com.cn/market/json?funcno=20003&&version=1&stock_list=SH:600875
			params.put("funcno", "20003");
			params.put("version", "1");
			// parameter.addParameter("stock_list",market + ":" +
			// String.valueOf(code));
		} else if (requestToken == SINGLE_STOCK) {
			params.put("funcno", "20000");
			params.put("version", "1");
			// params.put("stock_list",String.valueOf(market + ":" + code));
			params.put("field", String.valueOf("22:23:24:2:3:1:9:12:14:16:10:11:6:18:19:31:13:27:8:21"));
		}
		postRequest(URI);
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
		}
		return super.requestSucessFinished(result, requestToken);
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
	 * DecimalFormatת������
	 */
	public String format1(double input) {
		DecimalFormat df = new DecimalFormat("#.#");
		return df.format(input);
	}

	/**
	 * DecimalFormatת������
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
					if (i == 0){
						minuteInfo.setColor(KChartUtil.UP_COLOR);
					}else{
						if (now >= jsonArray.getJSONArray(i-1).getDouble(1)) {
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
		return minuteInfos;
	}

	public List<SingleStockInfo> processKChartJson(String mResult) {
		List<SingleStockInfo> infos = new ArrayList<SingleStockInfo>();
		try {
			JSONObject jsonObject = new JSONObject(mResult);
			int errorCode = jsonObject.getInt("errorNo");
			if (0 == errorCode) {
				JSONArray array = jsonObject.getJSONArray("results");
				int multiple = 100;
				String mType = StockCache.get(StockCache.STOCK_TYPE, String.class);
//				PriceInfo info = StockCache.get(StockCache.STOCK_INFO, PriceInfo.class);
//				if (info!=null){
//					mType = String.valueOf(info.getStktype());
					if (null != mType && ("0".equals(mType) || "1".equals(mType) || "2".equals(mType) || "9".equals(mType)
							|| "7".equals(mType) || "15".equals(mType) || "18".equals(mType))) {
						multiple  = 100;
					} else {
						multiple = 1000;
					}		
//				}
				int index = 0;
				String kType = params.get("type");
				/**
				 * ���ݽ�ֹ����һ��������
				 */
				if (null != array && array.length() > 0) {
					while (index < array.length()) {
						JSONArray tmp = array.getJSONArray(index);
						int color = tmp.getDouble(1) > tmp.getDouble(3) ? KChartUtil.DOWN_COLOR : KChartUtil.UP_COLOR;// ����ǵ��������ɫΪ��ɫ
																						// ������ǣ������ɫΪ��ɫ
						double open = tmp.getDouble(1) / multiple; // ���̼�
						double close = tmp.getDouble(3) / multiple; // ���̼�
						double high = tmp.getDouble(2) / multiple;// ��߼�
						double low = tmp.getDouble(4) / multiple;// ��߼�
						int date = tmp.getInt(0);// ����
						double totalCount = tmp.getDouble(5) / 100;// һ��ɽ��ɽ����������ܳɽ���
						double totalPrice = tmp.getDouble(6);// �ܳɽ����;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		KChartUtil.calcMAF2T(infos, 5);
		KChartUtil.calcMAF2T(infos, 10);
		KChartUtil.calcMAF2T(infos, 20);
		return infos;
	}
	/**
	 * �嵵
	 */
	public WuDangInfo wuDangProcess(String resultJson) {
		WuDangInfo wuDangInfo = new WuDangInfo();
		try {
			JSONObject jsonObject = new JSONObject(resultJson);
			JSONArray jsonArray = jsonObject.getJSONArray("results");
			if (jsonArray.length() > 0) {
				JSONArray wudangArray = jsonArray.getJSONArray(0);
				// ��Ʊ����
				String code = wudangArray.getString(0);
				// ��Ʊ����
				String name = wudangArray.getString(1);
				// ��Ʊ����
				String stktype = wudangArray.getString(2);
				// �г�����
				String market = wudangArray.getString(3);
				// ����
				String date = wudangArray.getString(4);
				// ������
				String minute = wudangArray.getString(5);
				// ����5
				double sell5 = wudangArray.getDouble(6);
				// ����4
				double sell4 = wudangArray.getDouble(7);
				// ����3
				double sell3 = wudangArray.getDouble(8);
				// ����2
				double sell2 = wudangArray.getDouble(9);
				// ����1
				double sell1 = wudangArray.getDouble(10);

				// ����5
				double sellValume5 = wudangArray.getDouble(11);
				// ����4
				double sellValume4 = wudangArray.getDouble(12);
				// ����3
				double sellValume3 = wudangArray.getDouble(13);
				// ����2
				double sellValume2 = wudangArray.getDouble(14);
				// ����1
				double sellValume1 = wudangArray.getDouble(15);

				// ���1
				double buy1 = wudangArray.getDouble(16);
				// ���2
				double buy2 = wudangArray.getDouble(17);
				// ���3
				double buy3 = wudangArray.getDouble(18);
				// ���4
				double buy4 = wudangArray.getDouble(19);
				// ���5
				double buy5 = wudangArray.getDouble(20);

				// ����1
				double buyValume1 = wudangArray.getDouble(21);
				// ����2
				double buyValume2 = wudangArray.getDouble(22);
				// ����3
				double buyValume3 = wudangArray.getDouble(23);
				// ����4
				double buyValume4 = wudangArray.getDouble(24);
				// ����5
				double buyValume5 = wudangArray.getDouble(25);

				// ����
				double yesterday = wudangArray.getDouble(26);
				// ��
				double open = wudangArray.getDouble(27);
				// ���
				double high = wudangArray.getDouble(28);
				// ���
				double low = wudangArray.getDouble(29);
				// �ּ�
				double now = wudangArray.getDouble(30);
				// �ܳɽ���
				double totalValume = wudangArray.getDouble(31);
				// �ܳɽ���
				double totalAmount = wudangArray.getDouble(32);
				// ����
				double outside = wudangArray.getDouble(33);
				// ����
				double inside = wudangArray.getDouble(34);
				// �ǵ�
				double up = wudangArray.getDouble(35);
				// �ǵ��ٷֱ�
				double uppercent = wudangArray.getDouble(36);
				// �ɽ�����
				double volume = wudangArray.getDouble(37);
				// ����
				double volrate = wudangArray.getDouble(38);
				// PE(��ӯ��)
				double pgr = wudangArray.getDouble(39);
				// ƴ�����
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
		return wuDangInfo;
	}

	

}
