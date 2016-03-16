package com.wuchuanlong.stockview.chart;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.example.stockview.R;
import com.wedroid.framework.common.WedroidLog;

/***
 * 日k、周k、月k
 * 
 * @author 吴传龙 <BR/>
 *         QQ:312037487
 */
public class StockView extends BaseChartView {
	@Override
	public int getChartType() {
		return 1;
	}
	
	/**
	 * 獲取最大成交量
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

	public double[] getMaxPrice() {
		double max = 0;
		double min = 0;
		try {
			if (stockList != null && !stockList.isEmpty()) {
				min = stockList.get(0).getLow();
				for (SingleStockInfo info : stockList) {
					max = Math.max(max, info.getHigh());
					min = Math.min(min, info.getLow());
				}
			}
			// max = Math.rint(max);
			// min = Math.rint(min);
			// double[] d = { max , min };
			// double[] d = { max + 5, min < 5 ? min : (min - 5) };
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double[] d = { max + max * 0.1f, min - min * 0.1f };
		return d;
	}

	public void drawChart(Canvas canvas, Rect topDirty, Paint grayPaint, Rect bottomDirty, Path path,
			Paint textGrayPaint, double maxPrice, double maxDealNumber, double heightPriceScale,
			double heightDealScale) {
		try {
			if (canvas==null || topDirty == null || bottomDirty==null)return;
			// 每根十字線的內邊距,也就是说真正的十字线是从perWidth * (1 / 6)处开始画到perWidth * (5 /6)处结束
			int padding = (int) (perWidth * per16);
			// 循环画出十字线图
			float x1 = mChartLeftMargin;// k线图的x1
			float x2 = 0;// k线图的x2
			int y1 = 0;// k线图的y1
			int y2 = 0;// k线图的y2
			int middleX = 0;// 中间线条的x
			int middleY1 = 0;// 中间线条的y1
			int middleY2 = 0;// 中间线条的y2
			int color = 0;
			int startDate = 0;
			// 画笔，颜色需要判断
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(1);
			
//			Paint downColorPaint = paint;
			Paint downColorPaint = new Paint();
			downColorPaint.setAntiAlias(true);
			downColorPaint.setStyle(Paint.Style.FILL);
			downColorPaint.setStrokeWidth(1);
			
			float lastDrawDataX = 0;
			for (int i = 0; i < stockList.size(); i++) {
				SingleStockInfo info = stockList.get(i);
				x1 = mChartLeftMargin + i * perWidth + padding;
				x2 = x1 + perWidth - padding;
				if (info.getOpen() < info.getClose()) {
					color = KChartUtil.UP_COLOR;
					// 涨 收盘价>开盘价
					y1 = (int) (topDirty.top + (maxPrice - info.getClose()) * heightPriceScale);
					y2 = (int) (topDirty.top + (maxPrice - info.getOpen()) * heightPriceScale);
				} else if (info.getOpen() >= info.getClose()) {
					color = KChartUtil.DOWN_COLOR;
					// 跌
					y1 = (int) (topDirty.top + (maxPrice - info.getOpen()) * heightPriceScale);
					y2 = (int) (topDirty.top + (maxPrice - info.getClose()) * heightPriceScale);
				}
				if(color == KChartUtil.DOWN_COLOR){
					downColorPaint.setColor(info.getColor());
				}else{
					paint.setColor(info.getColor());
				}
				// 十字线
				if(info.getOpen() == info.getClose()){
					// 开盘价 == 收盘价  一字板
					canvas.drawRect(x1, y1-2, x2, y2, color == KChartUtil.DOWN_COLOR?downColorPaint:paint);
				}else{
					canvas.drawRect(x1, y1, x2, y2, color == KChartUtil.DOWN_COLOR?downColorPaint:paint);
				}
				// 十字线的中间的线条
				middleX = (int) (x1 + perWidth * perHalf);// 每个十字图的总宽度*0.5
				middleY1 = (int) (topDirty.top + (maxPrice - info.getHigh()) * heightPriceScale);
				middleY2 = (int) (topDirty.top + (maxPrice - info.getLow()) * heightPriceScale);
				canvas.drawRect(middleX - 1, middleY1, middleX, y1, color == KChartUtil.DOWN_COLOR?downColorPaint:paint);
				canvas.drawRect(middleX - 1, y2, middleX, middleY2, color == KChartUtil.DOWN_COLOR?downColorPaint:paint);
				// 画日期和提起对应的垂直线条
				// 是月k的话，就比较前面是否是同一年，即前面四位数
				// 是日k的话就比较前面6位数，即年月
				// 是周k的话就每隔两周画一个日期
				int date = 0;
				if (Type.MONTH.getValue().equals(info.getType().getValue())) {
					date = Integer.parseInt(String.valueOf(info.getDate()).substring(0, 4));
				} else {
					date = Integer.parseInt(String.valueOf(info.getDate()).substring(0, 6));
				}
				if (date > startDate) {
					if (startDate > 0) {
						// 第一根线和第一个日期不画
						boolean drawAble = true;
						if (Type.WEEK.getValue().equals(info.getType().getValue())) {
							drawAble = false;
							// 是周k的话就每隔两周画一个日期
							if (date - startDate > 3) {
								drawAble = true;
							}
						}
						if (drawAble) {
							startDate = date;
							float w = textGrayPaint.measureText(date + "");
							float h = textGrayPaint.descent() - textGrayPaint.ascent();
							float x = x1 + perWidth * perHalf;
							if ((x - lastDrawDataX) >= w) {
								float dx = x1 - w / 2 + perWidth * perHalf;
								if(dx+w<topDirty.right){
									canvas.drawText(date + "", dx, topDirty.bottom + h,
											textGrayPaint);
									path.moveTo(x1 + perWidth * perHalf, topDirty.top);
									path.lineTo(x1 + perWidth * perHalf, topDirty.bottom);
									canvas.drawPath(path, grayPaint);
								}
							}
							lastDrawDataX = x;
						}
					} else {
						startDate = date;
					}
				}
				// 画成交量
				y1 = (int) (bottomDirty.top + (maxDealNumber - info.getDealCount()) * heightDealScale);
				canvas.drawRect(x1, y1, x2, bottomDirty.bottom, color == KChartUtil.DOWN_COLOR?downColorPaint:paint);
			}

			// 画均线
			Path path5 = new Path();
			Path path10 = new Path();
			Path path20 = new Path();
			float maStart = mChartLeftMargin;
			float maStartY;
			path5.moveTo(maStart, (float) (topDirty.top + (maxPrice - stockList.get(0).getMaValue5()) * heightPriceScale));
			path10.moveTo(maStart,
					(float) (topDirty.top + (maxPrice - stockList.get(0).getMaValue10()) * heightPriceScale));
			path20.moveTo(maStart,
					(float) (topDirty.top + (maxPrice - stockList.get(0).getMaValue20()) * heightPriceScale));

			for (int i = 0; i < stockList.size(); i++) {
				SingleStockInfo info = stockList.get(i);
				maStart = mChartLeftMargin + i * perWidth + padding;
				maStartY = (float) (topDirty.top + (maxPrice - info.getMaValue5()) * heightPriceScale);
				path5.lineTo(maStart + perWidth * perHalf, maStartY);
				maStartY = (float) (topDirty.top + (maxPrice - info.getMaValue10()) * heightPriceScale);
				path10.lineTo(maStart + perWidth * perHalf, maStartY);
				maStartY = (float) (topDirty.top + (maxPrice - info.getMaValue20()) * heightPriceScale);
				path20.lineTo(maStart + perWidth * perHalf, maStartY);
				// maStart += perWidth * perHalf;
			}

			Paint maPaint = new Paint();
			maPaint.setColor(context.getResources().getColor(R.color.thinkive_ma_5));
			maPaint.setAntiAlias(true);
			maPaint.setStrokeWidth(2);
			maPaint.setStyle(Style.STROKE);
			canvas.drawPath(path5, maPaint);
			maPaint.setColor(context.getResources().getColor(R.color.thinkive_ma_10));
			canvas.drawPath(path10, maPaint);
			maPaint.setColor(context.getResources().getColor(R.color.thinkive_ma_20));
			canvas.drawPath(path20, maPaint);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public StockView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public StockView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StockView(Context context) {
		this(context, null, 0);
	}

	@Override
	public void touchEvent(Canvas canvas, Rect topDirty, Rect bottomDirty, double maxPrice, double heightPriceScale) {
		try {
			if (canvas == null) {
				if (touchCallBack != null) {
					touchCallBack.updateViewInTouch(null);
				}
				return;
			}
			Paint grayPaint;
			float textWidth;
			float textHeight;
			// 手指触摸
			if (pointerPostion>0 && pointerPostion <= stockList.size() ) {
				Path pathPointer = new Path();
				float x = pointerPostion * perWidth + mChartLeftMargin - perWidth * perHalf;
				// 垂直线
				pathPointer.moveTo(x + 1, topDirty.top);
				pathPointer.lineTo(x + 1, bottomDirty.bottom);
				// 水平线
				SingleStockInfo singleStockInfo = stockList.get(pointerPostion - 1);
				double price = singleStockInfo.getClose();
				int y = (int) (topDirty.top + (maxPrice - price) * heightPriceScale);
				pathPointer.moveTo(mChartLeftMargin, y);
				pathPointer.lineTo(topDirty.right - 1, y);
				Paint p = getLineBlackPaint();
				// p.setPathEffect(effects);
				canvas.drawPath(pathPointer, p);
				pathPointer.close();
				// 画对应的日期
				grayPaint = getTextGrayPaint();
				String date = singleStockInfo.getDate() + "";
				textWidth = grayPaint.measureText(date);
				textHeight = grayPaint.descent() - grayPaint.ascent();
				canvas.drawText(date, x - textWidth / 2, topDirty.top - textHeight / 4, grayPaint);
				float priceLeft = 0;
				// 画对应的价格
				String priceString = KChartUtil.format2(price) + "";
				textWidth = grayPaint.measureText(priceString);
				if(isBigChart){
					priceLeft  = mChartLeftMargin - textWidth - div;
				}else{
					priceLeft = mChartLeftMargin + div;
				}
				textHeight = grayPaint.descent() - grayPaint.ascent();
				canvas.drawText(priceString, priceLeft, y + textHeight / 4, grayPaint);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
