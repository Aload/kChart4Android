package com.wuchuanlong.stockview.chart;

public interface ChartTouchEvent {

	void updateRelativeView(SingleStockInfo info,Type chartType);

	void updateRelativeView(PriceInfo stockInfo,Type chartType);
	
	void ifParentIterceptorEvent(boolean interceptor);
	
	void clickedTwo();
}
