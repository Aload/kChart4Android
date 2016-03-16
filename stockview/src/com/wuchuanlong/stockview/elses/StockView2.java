package com.wuchuanlong.stockview.elses;

import java.util.List;

import com.wuchuanlong.stockview.MainActivity;
import com.wuchuanlong.stockview.chart.KChartUtil;
import com.wuchuanlong.stockview.chart.SingleStockInfo;
import com.wuchuanlong.stockview.chart.StockBusiness;
import com.wuchuanlong.stockview.chart.TouchCallBack;
import com.wuchuanlong.stockview.chart.Type;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class StockView2 extends View {
	List<SingleStockInfo> stockList;

	// 图的Y轴起点
	int mChartTop;
	// 整个图距离左边，k线图的X轴起点
	int mChartLeftMargin;
	// 整个图距离右边
	int mChartRightMargin;
	// 整个图宽度
	int mChartWidth;
	// 整个图的高度
	int mChartToatalHeight;
	// 图上半部分k线图的高度
	int mChartTopKHeight;
	// 图下半部分成交量的高度 k线图与成交量图之间的间隔就是mChartToatalHeight - mChartTopHeight -
	// mChartTopMarginBotton
	int mChartBottomDealHeight;
	// 虚线与实现之间的上下边距
	int dashMargin = 20;
	// 左邊的文字與垂直線的距離
	int div = 5;

	public void init(Context context, AttributeSet attrs, int defStyleAttr) {
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		int measuredWidth = getMeasuredWidth();
		int measuredHeight = getMeasuredHeight();
		// 整个图距离左边，k线图的X轴起点
		mChartLeftMargin = getLeft() + getPaddingLeft();
		// 整个图距离右边
		mChartRightMargin = getPaddingRight();
		// 整个图宽度
		mChartWidth = measuredWidth - mChartLeftMargin - mChartRightMargin;
		// 整个图的高度
		mChartToatalHeight = measuredHeight - getPaddingTop() - getPaddingBottom();
		// 图上半部分k线图的高度
		mChartTopKHeight = (int) (mChartToatalHeight * 0.5);
		// 图下半部分成交量的高度 k线图与成交量图
		mChartBottomDealHeight = (int) (mChartToatalHeight * 0.4);
		// 图的Y轴起点
		mChartTop = getPaddingTop();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);

		int rightY = mChartWidth + mChartLeftMargin;

		/**
		 * 画上半部分的k线图的背景
		 */
		Rect topDirty = new Rect(mChartLeftMargin, mChartTop, rightY, mChartTopKHeight + mChartTop);
		Paint grayPaint = getLineGrayPaint();
		canvas.drawRect(topDirty, grayPaint);

		/**
		 * 画下半部分的成交量的背景
		 */
		int top = topDirty.bottom + mChartToatalHeight - mChartTopKHeight - mChartBottomDealHeight;
		Rect bottomDirty = new Rect(mChartLeftMargin, top, rightY, mChartBottomDealHeight + top);
		canvas.drawRect(bottomDirty, grayPaint);

		/**
		 * 画k线图的两根上下虚线
		 */
		PathEffect effects = new DashPathEffect(new float[] { 5, 5, 5, 5 }, 1);
		grayPaint.setPathEffect(effects);
		Path path = new Path();
		// 第一根虚线
		path.moveTo(mChartLeftMargin, mChartTop + dashMargin);
		path.lineTo(rightY, mChartTop + dashMargin);
		// 第二根虚线
		path.moveTo(mChartLeftMargin, topDirty.bottom - dashMargin);
		path.lineTo(rightY, topDirty.bottom - dashMargin);
		// 第三根虛線
		path.moveTo(mChartLeftMargin, (topDirty.bottom - topDirty.top) / 2 + topDirty.top);
		path.lineTo(rightY, (topDirty.bottom - topDirty.top) / 2 + topDirty.top);
		canvas.drawPath(path, grayPaint);

		if (stockList != null && !stockList.isEmpty()) {
			/**
			 * 計算最高價格和最低價格,全部精確到00.00
			 */
			Paint textGrayPaint = getTextGrayPaint();
			double[] prices = getMaxPrice();
			double maxPrice = prices[0];
			double minPrice = prices[1];
			
			/**
			 * 畫左邊的三個價格
			 */
			float textWidth = textGrayPaint.measureText(maxPrice + "");
			float textHeight = textGrayPaint.descent() - textGrayPaint.ascent();
			canvas.drawText(maxPrice + "", mChartLeftMargin - textWidth - div, mChartTop + dashMargin + textHeight / 4,
					textGrayPaint);
			double minddlePrice = Math.rint((maxPrice - minPrice) / 2 + minPrice);
			canvas.drawText(minddlePrice + "", mChartLeftMargin - textWidth - div,
					(topDirty.bottom - topDirty.top) / 2 + topDirty.top + textHeight / 4, textGrayPaint);
			canvas.drawText(minPrice + "", mChartLeftMargin - textWidth - div,
					topDirty.bottom - dashMargin + textHeight / 4, textGrayPaint);

			/**
			 * 畫左邊的成交量
			 */
			double maxDealNumber = getMaxDealNumber();
			double showDealNumber = Math.rint(maxDealNumber / 10000);
			textWidth = textGrayPaint.measureText(showDealNumber + "");
			textHeight = textGrayPaint.descent() - textGrayPaint.ascent();
			canvas.drawText("成交量", mChartLeftMargin, bottomDirty.top - textHeight / 2, textGrayPaint);
			canvas.drawText(showDealNumber + "", mChartLeftMargin - textWidth - div, bottomDirty.top + textHeight / 2,
					textGrayPaint);
			textWidth = textGrayPaint.measureText("万手");
			canvas.drawText("万手", mChartLeftMargin - textWidth - div, bottomDirty.bottom + textHeight / 4,
					textGrayPaint);
			/**
			 * 畫十字線、成交量圖、日期
			 */
					/**
					 * 畫十字線
					 */
//			int size = stockList.size();
//			perWidth = mChartWidth / size;
			perWidth = mChartWidth / oriSize;
			// 每根十字線的內邊距,也就是说真正的十字线是从perWidth * (1 / 6)处开始画到perWidth * (5 /6)处结束
			int padding = (int) (perWidth * per16);
			// 价格和像素形成个一个比例
			double heightPriceScale = (topDirty.bottom - topDirty.top) / (maxPrice - minPrice);
			// 价格和成交量形成个一个比例
			double heightDealScale = (bottomDirty.bottom - bottomDirty.top) / (maxDealNumber);
			// 循环画出十字线图
			int x1 = mChartLeftMargin;// k线图的x1
			int x2 = 0;// k线图的x2
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
			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeWidth(2);
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
				paint.setColor(info.getColor());
				canvas.drawRect(x1, y1, x2, y2, paint);
				Log.e("drawRect", y1 + "-----" + y2 + "--" + heightPriceScale + "--" + topDirty.bottom);
				// 十字线的中间的线条
				middleX = (int) (x1 + perWidth * perHalf);// 每个十字图的总宽度*0.5
				middleY1 = (int) (topDirty.top + (maxPrice - info.getHigh()) * heightPriceScale);
				middleY2 = (int) (topDirty.top + (maxPrice - info.getLow()) * heightPriceScale);
				canvas.drawRect(middleX - 1, middleY1, middleX, middleY2, paint);
				// 画日期和提起对应的垂直线条
				// 是月k的话，就比较前面是否是同一年，即前面四位数
				// 是日k的话就比较前面6位数，即年月
				// 是周k的话就每隔两周画一个日期
				int date = 0;
				if (Type.MONTH.getValue().equals(info.getType().getValue())){
					date = Integer.parseInt(String.valueOf(info.getDate()).substring(0, 4));
				}else{
					date = Integer.parseInt(String.valueOf(info.getDate()).substring(0, 6));
				}
				if (date > startDate) {
					if (startDate > 0) {
						// 第一根线和第一个日期不画
						boolean drawAble = true;
						if (Type.WEEK.getValue().equals(info.getType().getValue())){
							drawAble = false;
							 // 是周k的话就每隔两周画一个日期
							if (date - startDate>2){
								drawAble = true;
							}
						}
						if (drawAble){
							startDate = date;
							float w = textGrayPaint.measureText(date + "");
							float h = textGrayPaint.descent() - textGrayPaint.ascent();
							canvas.drawText(date + "", x1 - w / 2 + perWidth * perHalf, topDirty.bottom + h, textGrayPaint);
							path.moveTo(x1 + perWidth * perHalf, topDirty.top);
							path.lineTo(x1 + perWidth * perHalf, topDirty.bottom);
							canvas.drawPath(path, grayPaint);
						}
					} else {
						startDate = date;
					}
				}
				// 画成交量
				y1 = (int) (bottomDirty.top + (maxDealNumber - info.getDealCount()) * heightDealScale);
				canvas.drawRect(x1, y1, x2, bottomDirty.bottom, paint);
			}

			// 画均线
			Path path5 = new Path();
			Path path10 = new Path();
			Path path20 = new Path();
			int maStart = mChartLeftMargin;
			float maStartY;
			path5.moveTo(maStart,
					(float) (topDirty.top + (maxPrice - stockList.get(0).getMaValue5()) * heightPriceScale));
			path10.moveTo(maStart,
					(float) (topDirty.top + (maxPrice - stockList.get(0).getMaValue10()) * heightPriceScale));
			path20.moveTo(maStart,
					(float) (topDirty.top + (maxPrice - stockList.get(0).getMaValue20()) * heightPriceScale));

			for (int i = 0; i < stockList.size(); i++) {
				SingleStockInfo info = stockList.get(i);
				maStart = mChartLeftMargin + i * perWidth + padding;
				// maStart += perWidth * perHalf;// 每一天实际所占的数据是4/6，左右边距各1/6
				maStartY = (float) (topDirty.top + (maxPrice - info.getMaValue5()) * heightPriceScale);
				path5.lineTo(maStart + perWidth * perHalf, maStartY);
				maStartY = (float) (topDirty.top + (maxPrice - info.getMaValue10()) * heightPriceScale);
				path10.lineTo(maStart + perWidth * perHalf, maStartY);
				maStartY = (float) (topDirty.top + (maxPrice - info.getMaValue20()) * heightPriceScale);
				path20.lineTo(maStart + perWidth * perHalf, maStartY);
				// maStart += perWidth * perHalf;
			}

			Paint maPaint = new Paint();
			maPaint.setColor(Color.BLUE);
			maPaint.setAntiAlias(true);
			maPaint.setStrokeWidth(2);
			maPaint.setStyle(Style.STROKE);
			canvas.drawPath(path5, maPaint);
			maPaint.setColor(Color.MAGENTA);
			canvas.drawPath(path10, maPaint);
			maPaint.setColor(Color.GREEN);
			canvas.drawPath(path20, maPaint);
			path.close();
			// 手指触摸
			if (pointerPostion != 0) {
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
				// 画对应的价格
				String priceString = Math.rint(price) + "";
				textWidth = grayPaint.measureText(priceString);
				textHeight = grayPaint.descent() - grayPaint.ascent();
				canvas.drawText(priceString, mChartLeftMargin - textWidth - div, y + textHeight / 4, grayPaint);
			}
		}
	}

	public int measureHeight(int heightMeasureSpec) {
		int size = 0;
		int specSize = MeasureSpec.getSize(heightMeasureSpec);
		int specMode = MeasureSpec.getMode(heightMeasureSpec);
		switch (specMode) {
		case MeasureSpec.AT_MOST:
			// 最多的，子元素最多是specSize的值
			break;
		case MeasureSpec.EXACTLY:
			// 确定的，就是说父元素确定了子元素的大小，子元素将被限定在给定的边界而忽略自身的大小
			size = specSize;
			break;
		case MeasureSpec.UNSPECIFIED:
			// 父元素不限制子元素的大小，子元素可以获取任意的大小
			break;
		}
		return size;
	}

	public int measureWidth(int widthMeasureSpec) {
		int size = 0;
		int specSize = MeasureSpec.getSize(widthMeasureSpec);
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		switch (specMode) {
		case MeasureSpec.AT_MOST:
			// 最多的，子元素最多是specSize的值
			break;
		case MeasureSpec.EXACTLY:
			// 确定的，就是说父元素确定了子元素的大小，子元素将被限定在给定的边界而忽略自身的大小
			size = specSize;
			break;
		case MeasureSpec.UNSPECIFIED:
			// 父元素不限制子元素的大小，子元素可以获取任意的大小
			break;
		}
		return size;
	}

	private float per16 = 0.166666666f;
	private float perHalf = 0.5f;

	private int perWidth;

	/**
	 * 獲取最高和最低成交價格
	 * 
	 * @return
	 */
	public double[] getMaxPrice() {
		double max = 0;
		double min = stockList.get(0).getLow();

		for (SingleStockInfo info : stockList) {
			max = Math.max(max, info.getHigh());
			min = Math.min(min, info.getLow());
		}
		max = Math.rint(max);
		min = Math.rint(min);
		double[] d = { max + 5, min < 5 ? min : (min - 5) };
		return d;
	}

	/**
	 * 獲取最大成交量
	 * 
	 * @return
	 */
	public double getMaxDealNumber() {
		double max = 0;

		for (SingleStockInfo info : stockList) {
			if (max < info.getDealCount()) {
				max = info.getDealCount();
			}
		}
		return Math.rint(max + max / 20);
	}

	int pointerPostion = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		int action = MotionEventCompat.getActionMasked(event);
		switch (action) {
		case (MotionEvent.ACTION_DOWN):
			drawGesture(x, y);
			return true;
		case (MotionEvent.ACTION_MOVE):
			drawGesture(x, y);
			return true;
		case (MotionEvent.ACTION_UP):
			pointerPostion = 0;
			invalidate();
			return true;
		case (MotionEvent.ACTION_CANCEL):
			return true;
		case (MotionEvent.ACTION_OUTSIDE):
			return true;
		}
		return super.onTouchEvent(event);
	}

	TouchCallBack callBack;

	public void setCallback(TouchCallBack callBack){
		this.callBack = callBack;
	}

	private void drawGesture(float x, float y) {
		Log.e("onTouchEvent", "x:" + x + "---y:" + y);
		if (perWidth != 0) {
			pointerPostion = (int) ((x - mChartLeftMargin) / perWidth);
			if (pointerPostion <= stockList.size() && pointerPostion > 0) {
				Log.e("onTouchEvent", "position:" + pointerPostion);
				invalidate();
				SingleStockInfo singleStockInfo = stockList.get(pointerPostion - 1);
				if (callBack!=null){
					callBack.updateViewInTouch(singleStockInfo);
				}
			}
		}
	}

	public void setStockList(List<SingleStockInfo> stockList) {
		this.stockList = stockList;
	}
	// 原本想获取的个数，实际上有可能没有这么多数据，可能有些股票没上市多久
	int oriSize;
	public void setOriSize(int oriSize){
		this.oriSize = oriSize;
	}
	public Paint getLineGrayPaint() {
		Paint lineGrayPaint = new Paint();
		lineGrayPaint.setColor(Color.GRAY);
		lineGrayPaint.setAntiAlias(true);
		lineGrayPaint.setStrokeWidth(1);
		lineGrayPaint.setStyle(Style.STROKE);
		return lineGrayPaint;
	}

	public Paint getLineBlackPaint() {
		Paint lineGrayPaint = new Paint();
		lineGrayPaint.setColor(Color.BLACK);
		lineGrayPaint.setAntiAlias(true);
		lineGrayPaint.setStrokeWidth(1);
		lineGrayPaint.setStyle(Style.STROKE);
		return lineGrayPaint;
	}

	public Paint getTextGrayPaint() {
		Paint textGrayPaint = new Paint();
		textGrayPaint.setColor(Color.GRAY);
		textGrayPaint.setAntiAlias(true);
		textGrayPaint.setTextSize(22);
		return textGrayPaint;
	}

	public StockView2(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	public StockView2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StockView2(Context context) {
		this(context, null, 0);
	}

}
