package com.wuchuanlong.stockview.chart;

public interface TouchCallBack {
	public void updateViewInTouch(SingleStockInfo info);
	void ifParentIterceptorEvent(boolean interceptor);
	void enterBigView();
}