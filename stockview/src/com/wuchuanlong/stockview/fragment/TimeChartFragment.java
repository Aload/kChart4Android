package com.wuchuanlong.stockview.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.stockview.R;
import com.wedroid.framework.adapter.WeDroidAdapter;
import com.wedroid.framework.fragment.WeDroidFragment;
import com.wuchuanlong.stockview.BigStockChartActivity;
import com.wuchuanlong.stockview.chart.ChartTouchEvent;
import com.wuchuanlong.stockview.chart.CircleLoadingView;
import com.wuchuanlong.stockview.chart.PriceInfo;
import com.wuchuanlong.stockview.chart.SingleStockInfo;
import com.wuchuanlong.stockview.chart.StockBusiness;
import com.wuchuanlong.stockview.chart.StockCache;
import com.wuchuanlong.stockview.chart.TimeKChartView;
import com.wuchuanlong.stockview.chart.TouchCallBack;
import com.wuchuanlong.stockview.chart.Type;
import com.wuchuanlong.stockview.chart.WuDangInfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TimeChartFragment extends WeDroidFragment {
	TimeKChartView timeChartView;
	CircleLoadingView circleLoadingView;
	ListView mSellListView;
	ListView mBuyListView;
	WuDangInfo wuDangInfo;
	private List<SingleStockInfo> hourList;
	private int oriSize = 240;
	private PriceInfo stockInfo;// 头部数据

	@Override
	public void onResume() {
		super.onResume();
		List<SingleStockInfo> infos = StockCache.get(StockCache.TIME_CHAR_DATE, List.class);
		if (infos != null && !infos.isEmpty()) {
			hourList = infos;
			updateStockView(hourList);
		}
		initListener();
	}

	@Override
	public void onPause() {
		super.onPause();
		StockCache.put(StockCache.TIME_CHAR_DATE, hourList);
	}

	@Override
	protected View initContentView(LayoutInflater inflater) {
		return View.inflate(mContext, R.layout.chart_stock_time_chart, null);
	}

	@Override
	protected void initViewById(View view) {
		timeChartView = (TimeKChartView) $(R.id.stock_view);
		circleLoadingView = (CircleLoadingView) $(R.id.loading_view);
		mSellListView = (ListView) $(R.id.listview_sell);
		mBuyListView = (ListView) $(R.id.listview_buy);
	}

	@Override
	protected void initListener() {
		timeChartView.setTouchCallback(new TouchCallBack() {
			@Override
			public void updateViewInTouch(SingleStockInfo info) {
				Activity activity = getActivity();
				if (activity != null && activity instanceof ChartTouchEvent) {
					ChartTouchEvent event = (ChartTouchEvent) activity;
					event.updateRelativeView(info, Type.HOUR);
				}
			}

			@Override
			public void ifParentIterceptorEvent(boolean interceptor) {
				Activity activity = getActivity();
				if (activity != null && activity instanceof ChartTouchEvent) {
					ChartTouchEvent event = (ChartTouchEvent) activity;
					event.ifParentIterceptorEvent(interceptor);
				}
			}

			@Override
			public void enterBigView() {
				Activity activity = getActivity();
				if (activity != null && activity instanceof ChartTouchEvent) {
					ChartTouchEvent event = (ChartTouchEvent) activity;
					event.clickedTwo();
				}
			}
		});
	}

//	boolean requesting;

	@Override
	protected void initData(Bundle savedInstanceState) {
		if (timeChartView != null) {
//			if (!requesting && timeChartView != null) {
//			requesting = true;
			circleLoadingView.showLoading();
			timeChartView.setVisibility(View.GONE);
			String code = StockCache.get(StockCache.CODE, String.class);
			String market = StockCache.get(StockCache.MARKET, String.class);
			PriceInfo pinf = StockCache.get(StockCache.STOCK_INFO, PriceInfo.class);
			if (pinf!=null){
				getTimeData(pinf);
			}else{
				// 获取头部数据
				Map<String, String> param = new HashMap<String, String>();
				param.put("stock_list", market + ":" + code);
				new StockBusiness(StockBusiness.SINGLE_STOCK, this, param).execute();
			}

			// 五档
			Map<String, String> params = new HashMap<String, String>();

			params.put("stock_list", market + ":" + code);
			new StockBusiness(StockBusiness.SELL_AND_BUY, this, params).execute();
		}
	}

	public void requestSuccess(Object result, int requestToken) {
//		requesting = false;
		timeChartView.setVisibility(View.VISIBLE);
		if (requestToken == StockBusiness.FEN_SHI_CHART) {
			circleLoadingView.hiden();
			hourList = (List<SingleStockInfo>) result;
			updateStockView(hourList);
		} else if (requestToken == StockBusiness.SELL_AND_BUY) {
			// 五档信息
			wuDangInfo = (WuDangInfo) result;
			mSellListView.setAdapter(new WuDangAdapter(false));
			mBuyListView.setAdapter(new WuDangAdapter(true));
			// StockActivity activity = (StockActivity) getActivity();
			// if (activity!=null)
			// activity.updateRelativeView(wuDangInfo);
		} else if (requestToken == StockBusiness.SINGLE_STOCK) {
			// 个股获取完成之后，获取分时图,因为分时图中需要用到个股行情数据
			getTimeData((PriceInfo) result);
		}
	}

	private void getTimeData(PriceInfo result) {
		Map<String, String> map = new HashMap<String, String>();
		String code = StockCache.get(StockCache.CODE, String.class);
		String market = StockCache.get(StockCache.MARKET, String.class);
		map.put("stock_code", code);
		map.put("market", market);
		map.put("type", Type.HOUR.getValue());
		map.put("count", oriSize + "");
		new StockBusiness(StockBusiness.FEN_SHI_CHART, this, map).execute();
		stockInfo = result;

		Activity activity = getActivity();
		if (activity != null && activity instanceof ChartTouchEvent) {
			ChartTouchEvent event = (ChartTouchEvent) activity;
			event.updateRelativeView(stockInfo, Type.HOUR);
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint()) {
			if (hourList == null || hourList.isEmpty() || stockInfo == null || wuDangInfo == null) {
				initData(null);
			}
		}
	}

	@Override
	public void requestFail(Object errorMessage, int requestToken) {
//		requesting = false;
	}

	class WuDangAdapter extends WeDroidAdapter {
		boolean buy;

		public WuDangAdapter(boolean buy) {
			this.buy = buy;
		}

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.chart_wudang_layout, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.price = (TextView) convertView.findViewById(R.id.price);
				holder.count = (TextView) convertView.findViewById(R.id.count);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String[] info = getWuDangText(position, buy);
			holder.name.setText(info[0]);
			holder.price.setText(info[1]);
			holder.count.setText(info[2]);
			return convertView;
		}
	}

	public String[] getWuDangText(int position, boolean buy) {
		String[] string = new String[3];
		if (!buy) {
			position = 5 - position - 1;
		}
		if (position == 0) {
			if (buy) {
				string[0] = String.valueOf("买1");
				string[1] = String.valueOf(wuDangInfo.getBuy1());
				string[2] = String.valueOf(wuDangInfo.getBuyValume1());
			} else {
				string[0] = String.valueOf("卖1");
				string[1] = String.valueOf(wuDangInfo.getSell1());
				string[2] = String.valueOf(wuDangInfo.getSellValume1());
			}
		} else if (position == 1) {
			if (buy) {
				string[0] = String.valueOf("买2");
				string[1] = String.valueOf(wuDangInfo.getBuy2());
				string[2] = String.valueOf(wuDangInfo.getBuyValume2());
			} else {
				string[0] = String.valueOf("卖2");
				string[1] = String.valueOf(wuDangInfo.getSell2());
				string[2] = String.valueOf(wuDangInfo.getSellValume2());
			}
		} else if (position == 2) {
			if (buy) {
				string[0] = String.valueOf("买3");
				string[1] = String.valueOf(wuDangInfo.getBuy3());
				string[2] = String.valueOf(wuDangInfo.getBuyValume3());
			} else {
				string[0] = String.valueOf("卖3");
				string[1] = String.valueOf(wuDangInfo.getSell3());
				string[2] = String.valueOf(wuDangInfo.getSellValume3());
			}
		} else if (position == 3) {
			if (buy) {
				string[0] = String.valueOf("买4");
				string[1] = String.valueOf(wuDangInfo.getBuy4());
				string[2] = String.valueOf(wuDangInfo.getBuyValume4());
			} else {
				string[0] = String.valueOf("卖4");
				string[1] = String.valueOf(wuDangInfo.getSell4());
				string[2] = String.valueOf(wuDangInfo.getSellValume4());
			}
		} else if (position == 4) {
			if (buy) {
				string[0] = String.valueOf("买5");
				string[1] = String.valueOf(wuDangInfo.getBuy5());
				string[2] = String.valueOf(wuDangInfo.getBuyValume5());
			} else {
				string[0] = String.valueOf("卖5");
				string[1] = String.valueOf(wuDangInfo.getSell5());
				string[2] = String.valueOf(wuDangInfo.getSellValume5());
			}
		}
		return string;
	}

	class ViewHolder {
		TextView name;
		TextView price;
		TextView count;
	}

	public void updateStockView(List<SingleStockInfo> list) {
		timeChartView.setStockList(list);
		timeChartView.setOriSize(oriSize);
		timeChartView.invalidate();
	}
}
