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

	// ͼ��Y�����
	int mChartTop;
	// ����ͼ������ߣ�k��ͼ��X�����
	int mChartLeftMargin;
	// ����ͼ�����ұ�
	int mChartRightMargin;
	// ����ͼ���
	int mChartWidth;
	// ����ͼ�ĸ߶�
	int mChartToatalHeight;
	// ͼ�ϰ벿��k��ͼ�ĸ߶�
	int mChartTopKHeight;
	// ͼ�°벿�ֳɽ����ĸ߶� k��ͼ��ɽ���ͼ֮��ļ������mChartToatalHeight - mChartTopHeight -
	// mChartTopMarginBotton
	int mChartBottomDealHeight;
	// ������ʵ��֮������±߾�
	int dashMargin = 20;
	// ��߅�������c��ֱ���ľ��x
	int div = 5;

	public void init(Context context, AttributeSet attrs, int defStyleAttr) {
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		int measuredWidth = getMeasuredWidth();
		int measuredHeight = getMeasuredHeight();
		// ����ͼ������ߣ�k��ͼ��X�����
		mChartLeftMargin = getLeft() + getPaddingLeft();
		// ����ͼ�����ұ�
		mChartRightMargin = getPaddingRight();
		// ����ͼ���
		mChartWidth = measuredWidth - mChartLeftMargin - mChartRightMargin;
		// ����ͼ�ĸ߶�
		mChartToatalHeight = measuredHeight - getPaddingTop() - getPaddingBottom();
		// ͼ�ϰ벿��k��ͼ�ĸ߶�
		mChartTopKHeight = (int) (mChartToatalHeight * 0.5);
		// ͼ�°벿�ֳɽ����ĸ߶� k��ͼ��ɽ���ͼ
		mChartBottomDealHeight = (int) (mChartToatalHeight * 0.4);
		// ͼ��Y�����
		mChartTop = getPaddingTop();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);

		int rightY = mChartWidth + mChartLeftMargin;

		/**
		 * ���ϰ벿�ֵ�k��ͼ�ı���
		 */
		Rect topDirty = new Rect(mChartLeftMargin, mChartTop, rightY, mChartTopKHeight + mChartTop);
		Paint grayPaint = getLineGrayPaint();
		canvas.drawRect(topDirty, grayPaint);

		/**
		 * ���°벿�ֵĳɽ����ı���
		 */
		int top = topDirty.bottom + mChartToatalHeight - mChartTopKHeight - mChartBottomDealHeight;
		Rect bottomDirty = new Rect(mChartLeftMargin, top, rightY, mChartBottomDealHeight + top);
		canvas.drawRect(bottomDirty, grayPaint);

		/**
		 * ��k��ͼ��������������
		 */
		PathEffect effects = new DashPathEffect(new float[] { 5, 5, 5, 5 }, 1);
		grayPaint.setPathEffect(effects);
		Path path = new Path();
		// ��һ������
		path.moveTo(mChartLeftMargin, mChartTop + dashMargin);
		path.lineTo(rightY, mChartTop + dashMargin);
		// �ڶ�������
		path.moveTo(mChartLeftMargin, topDirty.bottom - dashMargin);
		path.lineTo(rightY, topDirty.bottom - dashMargin);
		// ������̓��
		path.moveTo(mChartLeftMargin, (topDirty.bottom - topDirty.top) / 2 + topDirty.top);
		path.lineTo(rightY, (topDirty.bottom - topDirty.top) / 2 + topDirty.top);
		canvas.drawPath(path, grayPaint);

		if (stockList != null && !stockList.isEmpty()) {
			/**
			 * Ӌ����߃r�����̓r��,ȫ�����_��00.00
			 */
			Paint textGrayPaint = getTextGrayPaint();
			double[] prices = getMaxPrice();
			double maxPrice = prices[0];
			double minPrice = prices[1];
			
			/**
			 * ����߅�������r��
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
			 * ����߅�ĳɽ���
			 */
			double maxDealNumber = getMaxDealNumber();
			double showDealNumber = Math.rint(maxDealNumber / 10000);
			textWidth = textGrayPaint.measureText(showDealNumber + "");
			textHeight = textGrayPaint.descent() - textGrayPaint.ascent();
			canvas.drawText("�ɽ���", mChartLeftMargin, bottomDirty.top - textHeight / 2, textGrayPaint);
			canvas.drawText(showDealNumber + "", mChartLeftMargin - textWidth - div, bottomDirty.top + textHeight / 2,
					textGrayPaint);
			textWidth = textGrayPaint.measureText("����");
			canvas.drawText("����", mChartLeftMargin - textWidth - div, bottomDirty.bottom + textHeight / 4,
					textGrayPaint);
			/**
			 * ��ʮ�־����ɽ����D������
			 */
					/**
					 * ��ʮ�־�
					 */
//			int size = stockList.size();
//			perWidth = mChartWidth / size;
			perWidth = mChartWidth / oriSize;
			// ÿ��ʮ�־��ă�߅��,Ҳ����˵������ʮ�����Ǵ�perWidth * (1 / 6)����ʼ����perWidth * (5 /6)������
			int padding = (int) (perWidth * per16);
			// �۸�������γɸ�һ������
			double heightPriceScale = (topDirty.bottom - topDirty.top) / (maxPrice - minPrice);
			// �۸�ͳɽ����γɸ�һ������
			double heightDealScale = (bottomDirty.bottom - bottomDirty.top) / (maxDealNumber);
			// ѭ������ʮ����ͼ
			int x1 = mChartLeftMargin;// k��ͼ��x1
			int x2 = 0;// k��ͼ��x2
			int y1 = 0;// k��ͼ��y1
			int y2 = 0;// k��ͼ��y2
			int middleX = 0;// �м�������x
			int middleY1 = 0;// �м�������y1
			int middleY2 = 0;// �м�������y2
			int color = 0;
			int startDate = 0;
			// ���ʣ���ɫ��Ҫ�ж�
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
					// �� ���̼�>���̼�
					y1 = (int) (topDirty.top + (maxPrice - info.getClose()) * heightPriceScale);
					y2 = (int) (topDirty.top + (maxPrice - info.getOpen()) * heightPriceScale);
				} else if (info.getOpen() >= info.getClose()) {
					color = KChartUtil.DOWN_COLOR;
					// ��
					y1 = (int) (topDirty.top + (maxPrice - info.getOpen()) * heightPriceScale);
					y2 = (int) (topDirty.top + (maxPrice - info.getClose()) * heightPriceScale);
				}
				paint.setColor(info.getColor());
				canvas.drawRect(x1, y1, x2, y2, paint);
				Log.e("drawRect", y1 + "-----" + y2 + "--" + heightPriceScale + "--" + topDirty.bottom);
				// ʮ���ߵ��м������
				middleX = (int) (x1 + perWidth * perHalf);// ÿ��ʮ��ͼ���ܿ��*0.5
				middleY1 = (int) (topDirty.top + (maxPrice - info.getHigh()) * heightPriceScale);
				middleY2 = (int) (topDirty.top + (maxPrice - info.getLow()) * heightPriceScale);
				canvas.drawRect(middleX - 1, middleY1, middleX, middleY2, paint);
				// �����ں������Ӧ�Ĵ�ֱ����
				// ����k�Ļ����ͱȽ�ǰ���Ƿ���ͬһ�꣬��ǰ����λ��
				// ����k�Ļ��ͱȽ�ǰ��6λ����������
				// ����k�Ļ���ÿ�����ܻ�һ������
				int date = 0;
				if (Type.MONTH.getValue().equals(info.getType().getValue())){
					date = Integer.parseInt(String.valueOf(info.getDate()).substring(0, 4));
				}else{
					date = Integer.parseInt(String.valueOf(info.getDate()).substring(0, 6));
				}
				if (date > startDate) {
					if (startDate > 0) {
						// ��һ���ߺ͵�һ�����ڲ���
						boolean drawAble = true;
						if (Type.WEEK.getValue().equals(info.getType().getValue())){
							drawAble = false;
							 // ����k�Ļ���ÿ�����ܻ�һ������
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
				// ���ɽ���
				y1 = (int) (bottomDirty.top + (maxDealNumber - info.getDealCount()) * heightDealScale);
				canvas.drawRect(x1, y1, x2, bottomDirty.bottom, paint);
			}

			// ������
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
				// maStart += perWidth * perHalf;// ÿһ��ʵ����ռ��������4/6�����ұ߾��1/6
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
			// ��ָ����
			if (pointerPostion != 0) {
				Path pathPointer = new Path();
				float x = pointerPostion * perWidth + mChartLeftMargin - perWidth * perHalf;
				// ��ֱ��
				pathPointer.moveTo(x + 1, topDirty.top);
				pathPointer.lineTo(x + 1, bottomDirty.bottom);
				// ˮƽ��
				SingleStockInfo singleStockInfo = stockList.get(pointerPostion - 1);
				double price = singleStockInfo.getClose();
				int y = (int) (topDirty.top + (maxPrice - price) * heightPriceScale);
				pathPointer.moveTo(mChartLeftMargin, y);
				pathPointer.lineTo(topDirty.right - 1, y);
				Paint p = getLineBlackPaint();
				// p.setPathEffect(effects);
				canvas.drawPath(pathPointer, p);
				pathPointer.close();
				// ����Ӧ������
				grayPaint = getTextGrayPaint();
				String date = singleStockInfo.getDate() + "";
				textWidth = grayPaint.measureText(date);
				textHeight = grayPaint.descent() - grayPaint.ascent();
				canvas.drawText(date, x - textWidth / 2, topDirty.top - textHeight / 4, grayPaint);
				// ����Ӧ�ļ۸�
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
			// ���ģ���Ԫ�������specSize��ֵ
			break;
		case MeasureSpec.EXACTLY:
			// ȷ���ģ�����˵��Ԫ��ȷ������Ԫ�صĴ�С����Ԫ�ؽ����޶��ڸ����ı߽����������Ĵ�С
			size = specSize;
			break;
		case MeasureSpec.UNSPECIFIED:
			// ��Ԫ�ز�������Ԫ�صĴ�С����Ԫ�ؿ��Ի�ȡ����Ĵ�С
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
			// ���ģ���Ԫ�������specSize��ֵ
			break;
		case MeasureSpec.EXACTLY:
			// ȷ���ģ�����˵��Ԫ��ȷ������Ԫ�صĴ�С����Ԫ�ؽ����޶��ڸ����ı߽����������Ĵ�С
			size = specSize;
			break;
		case MeasureSpec.UNSPECIFIED:
			// ��Ԫ�ز�������Ԫ�صĴ�С����Ԫ�ؿ��Ի�ȡ����Ĵ�С
			break;
		}
		return size;
	}

	private float per16 = 0.166666666f;
	private float perHalf = 0.5f;

	private int perWidth;

	/**
	 * �@ȡ��ߺ���ͳɽ��r��
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
	 * �@ȡ���ɽ���
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
	// ԭ�����ȡ�ĸ�����ʵ�����п���û����ô�����ݣ�������Щ��Ʊû���ж��
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
