package com.wuchuanlong.stockview.chart;

import com.example.stockview.R;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;

public class TimeKChartView extends BaseChartView {

	@Override
	public int getChartType() {
		return 0;
	}
	
	/**
	 * �@ȡ��ߺ���ͳɽ��r�� ��֮ͬ��
	 * 
	 * @return
	 */
	public double[] getMaxPrice() {
		double max = 0;
		double min = 0;
		double maxZdf = 0;
		double minZdf = 0;
		try {
			if (stockList != null && !stockList.isEmpty()) {
				

				PriceInfo info2 = StockCache.get(StockCache.STOCK_INFO, PriceInfo.class);
				
				for (SingleStockInfo info : stockList) {
					maxZdf = Math.max(maxZdf, KChartUtil.getZdF2(info.getNow(), info2.getYesterday()));
					minZdf = Math.min(minZdf, KChartUtil.getZdF2(info.getNow(), info2.getYesterday()));
				}
				// �����ǵ���������ߺ������ʾ�۸�
				if (maxZdf == minZdf) {
					max = KChartUtil.getPrice(info2.getYesterday(), maxZdf);
					min = info2.getYesterday()-(max - info2.getYesterday());
				}else if(Math.abs(maxZdf) > Math.abs(minZdf)){
					max = KChartUtil.getPrice(info2.getYesterday(), maxZdf);
					min = info2.getYesterday()-(max - info2.getYesterday());
				}else{
					min = KChartUtil.getPrice(info2.getYesterday(), minZdf);
					max = info2.getYesterday() + (info2.getYesterday() - min);
				}
				
//			min = stockList.get(0).getNow();
//			for (SingleStockInfo info : stockList) {
//				max = Math.max(max, info.getNow());
//				min = Math.min(min, info.getNow());
//			}
//			if (max == min) {
//				// һ�ְ�
//				min = min - min / 2f;
//			} else {
//				PriceInfo info2 = StockCache.get(StockCache.STOCK_INFO, PriceInfo.class);
//				if (info2 != null) {
//					max = info2.getYesterday() + info2.getYesterday() * 0.12f;
//					min = info2.getYesterday() - info2.getYesterday() * 0.12f;
//				}
//			}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double[] d = { max, min ,maxZdf,min};
		return d;
	}

	/**
	 * �@ȡ���ɽ��� ��֮ͬ��
	 * 
	 * @return
	 */
	public double getMaxDealNumber() {
		double max = 0;
		try {
			if (stockList != null && !stockList.isEmpty()) {
				for (SingleStockInfo info : stockList) {
					if (max < info.getDealCount()) {
						max = info.getDealCount();
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Math.rint(max + max / 20);
	}

	public TimeKChartView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	public TimeKChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TimeKChartView(Context context) {
		this(context, null, 0);
	}

	@Override
	public void drawChart(Canvas canvas, Rect topDirty, Paint grayPaint, Rect bottomDirty, Path path,
			Paint textGrayPaint, double maxPrice, double maxDealNumber, double heightPriceScale,
			double heightDealScale) {
		try {
			if (canvas==null || topDirty == null || bottomDirty==null)return;
			// ÿ��ʮ�־��ă�߅��,Ҳ����˵������ʮ�����Ǵ�perWidth * (1 / 6)����ʼ����perWidth * (5 /6)������
			float padding = (float) (perWidth * (2 / 6f));
			/**
			 * ����ʱͼ
			 */
			int x1 = mChartLeftMargin;// k��ͼ��x1
			float x2 = 0;// k��ͼ��x2
			int y1 = 0;// k��ͼ��y1
			int y2 = 0;// k��ͼ��y2
			// ���ʣ���ɫ��Ҫ�ж�
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeWidth(2);
			Path timePath = new Path();
			// timePath.moveTo(mChartLeftMargin - 1, topDirty.bottom);
			Path timeAlphaPath = new Path();
			timeAlphaPath.moveTo(mChartLeftMargin - 1, topDirty.bottom);
			int startMinute = stockList.get(0).getMinute();
			for (int i = 0; i < stockList.size(); i++) {
				SingleStockInfo info = stockList.get(i);
				x1 = (int) (mChartLeftMargin + i * perWidth);
				y1 = (int) (topDirty.top + (maxPrice - info.getNow()) * heightPriceScale);
				if (i == 0) {
					timePath.moveTo(mChartLeftMargin - 1, y1);
				}
				timePath.lineTo(x1 + perHalf * perWidth, y1);
				timeAlphaPath.lineTo(x1 + perHalf * perWidth, y1);
				if (i == stockList.size() - 1) {
					// timePath.lineTo(x1 + perHalf * perWidth, topDirty.bottom);
					timeAlphaPath.lineTo(x1 + perHalf * perWidth, topDirty.bottom);
					// timePath.close();
					Paint LinePaint = new Paint();
					LinePaint.setColor(context.getResources().getColor(R.color.all_k_color));
					LinePaint.setAntiAlias(true);
					LinePaint.setStrokeWidth(1);
					LinePaint.setStyle(Style.STROKE);
					canvas.drawPath(timePath, LinePaint);

					LinePaint.setAlpha(20);
					LinePaint.setStyle(Style.FILL);
					canvas.drawPath(timeAlphaPath, LinePaint);
				}
				if (isBigChart && i != 0 && (info.getMinute() - startMinute) >= 90) {
					// 
					// ÿ��һ��Сʱ���ͻ�һ�� 10:00 11:00 13:00
					startMinute = info.getMinute();
					String min = KChartUtil.getMinute(startMinute);
					float w = textGrayPaint.measureText(min);
					float h = textGrayPaint.descent() - textGrayPaint.ascent();

					canvas.drawText(min, x1 - w / 2, topDirty.bottom + h, textGrayPaint);
				} else {
					// startMinute = info.getMinute();
				}

				// �ɽ���
				y1 = (int) (bottomDirty.top + (maxDealNumber - info.getDealCount()) * heightDealScale);
				if (i == 0) {
					PriceInfo info2 = StockCache.get(StockCache.STOCK_INFO, PriceInfo.class);
					if (info2 != null) {
						if (info.getNow() >= info2.getYesterday()) {
							paint.setColor(KChartUtil.UP_COLOR);
						} else {
							paint.setColor(KChartUtil.DOWN_COLOR);
						}
					} else {
						paint.setColor(info.getColor());
					}
				} else {
					paint.setColor(info.getColor());
				}
				x2 = x1 + perWidth - padding;
				canvas.drawRect(x1, y1, x2, bottomDirty.bottom, paint);

			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void touchEvent(Canvas canvas, Rect topDirty, Rect bottomDirty, double maxPrice, double heightPriceScale) {
		try {
			if (canvas == null) {
				if (touchCallBack != null) {
					touchCallBack.updateViewInTouch(null);
				}
			}
			Paint grayPaint;
			float textWidth;
			float textHeight;
			// ��ָ����
			if (pointerPostion > 0 && pointerPostion <= stockList.size()) {
				Path pathPointer = new Path();
				float x = pointerPostion * perWidth + mChartLeftMargin - perWidth * perHalf;
				// ��ֱ��
				pathPointer.moveTo(x + 1, topDirty.top);
				pathPointer.lineTo(x + 1, bottomDirty.bottom);
				// ˮƽ��
				SingleStockInfo singleStockInfo = stockList.get(pointerPostion - 1);
				double price = singleStockInfo.getNow();
				int y = (int) (topDirty.top + (maxPrice - price) * heightPriceScale);
				pathPointer.moveTo(mChartLeftMargin, y);
				pathPointer.lineTo(topDirty.right - 1, y);
				Paint p = getLineBlackPaint();
				// p.setPathEffect(effects);
				canvas.drawPath(pathPointer, p);
				pathPointer.close();
				// ����Ӧ������
				grayPaint = getTextGrayPaint();
				String date = KChartUtil.getMinute(singleStockInfo.getMinute());
				textWidth = grayPaint.measureText(date);
				textHeight = grayPaint.descent() - grayPaint.ascent();
				canvas.drawText(date, x - textWidth / 2, topDirty.top - textHeight / 4, grayPaint);
				// ����Ӧ�ļ۸�
				String priceString = KChartUtil.format2(price) + "";
				textWidth = grayPaint.measureText(priceString);
				textHeight = grayPaint.descent() - grayPaint.ascent();
				float priceLeft = 0;
				if(isBigChart){
					priceLeft  = mChartLeftMargin - textWidth - div;
				}else{
					priceLeft = mChartLeftMargin + div;
				}
				canvas.drawText(priceString, priceLeft, y + textHeight / 4, grayPaint);
				// ���ǵ���
				PriceInfo info = StockCache.get(StockCache.STOCK_INFO, PriceInfo.class);
				if (info != null) {
					String priceZd = KChartUtil.getZdF(singleStockInfo.getNow(), info.getYesterday());
					if (priceZd != null && priceZd.contains("-")) {
						grayPaint.setColor(KChartUtil.DOWN_COLOR);
					} else {
						grayPaint.setColor(KChartUtil.UP_COLOR);
					}
					textWidth = grayPaint.measureText(priceZd);
					textHeight = grayPaint.descent() - grayPaint.ascent();
					canvas.drawText(priceZd, topDirty.right - textWidth-div, y, grayPaint);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
