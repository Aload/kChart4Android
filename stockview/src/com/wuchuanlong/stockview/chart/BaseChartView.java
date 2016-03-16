package com.wuchuanlong.stockview.chart;

import java.util.List;

import com.wedroid.framework.common.DensityUtil;
import com.wuchuanlong.stockview.chart.TouchCallBack;
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
import com.wedroid.framework.common.WedroidLog;
import android.view.MotionEvent;
import android.view.View;

/**
 * k��ͼ����
 * 
 * @author �⴫�� <BR/>
 *         QQ:312037487
 */
public abstract class BaseChartView extends View {
	public List<SingleStockInfo> stockList;
	public Context context;
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
	int dashMargin;
	// ��߅�������c��ֱ���ľ��x
	int div;
	// �Ƿ��Ǵ�ͼ
	boolean isBigChart;
	
	
	public int getmChartToatalHeight() {
		return mChartToatalHeight;
	}
	public boolean isBigChart(){
		return isBigChart;
	}
	public void setIsBigChart(boolean isBigChart){
		this.isBigChart = isBigChart;
	}
	public abstract int getChartType();
	
	public void init(Context context, AttributeSet attrs, int defStyleAttr) {
		this.context = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		try {
			setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
			int measuredWidth = getMeasuredWidth();
			int measuredHeight = getMeasuredHeight();
			dashMargin = DensityUtil.dip2px(getContext(), 8);
			div = DensityUtil.dip2px(getContext(), 5);
			mChartLeftMargin = getLeft();
			if(isBigChart){
				// k��ͼ
				// ����ͼ������ߣ�k��ͼ��X�����
				mChartLeftMargin += getPaddingLeft();
			}else{
				mChartLeftMargin +=getPaddingRight();
			}
			// ����ͼ�����ұ�
			mChartRightMargin = getPaddingRight();
			// ����ͼ���
			mChartWidth = measuredWidth - mChartLeftMargin - mChartRightMargin;
			// ����ͼ�ĸ߶�
			mChartToatalHeight = measuredHeight - getPaddingTop() - getPaddingBottom();
			// ͼ�ϰ벿��k��ͼ�ĸ߶�
			mChartTopKHeight = (int) (mChartToatalHeight * 0.64);
			// ͼ�°벿�ֳɽ����ĸ߶� k��ͼ��ɽ���ͼ
			mChartBottomDealHeight = (int) (mChartToatalHeight * 0.36);
			// ͼ��Y�����
			mChartTop = getPaddingTop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		try {
			// super.onDraw(canvas);
			Paint textGrayPaint = getTextGrayPaint();
			textGrayPaint.measureText("9");
			int zhHeights = (int) (textGrayPaint.descent() - textGrayPaint.ascent());
			if(isBigChart || getChartType()==1){
				mChartBottomDealHeight = (int) (mChartToatalHeight * 0.36 - zhHeights - zhHeights/2f);
			}else{
				mChartBottomDealHeight = (int) (mChartToatalHeight * 0.36 - zhHeights/4f);
			}
			
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
				double[] prices = getMaxPrice();
				double maxPrice = KChartUtil.format2(prices[0]);
				double minPrice = KChartUtil.format2(prices[1]);
				/**
				 * ����߅�ĳɽ���
				 */
				double maxDealNumber = getMaxDealNumber();
				
					// k��ͼ
					/**
					 * ����߅�������r��
					 */
					float textWidth = textGrayPaint.measureText(maxPrice + "");
					float priceLeft = 0;
					if(isBigChart){
						priceLeft = topDirty.left - textWidth - div;
					}else{
						priceLeft = topDirty.left + div;
					}
					// ��߼۸�
					float textHeight = textGrayPaint.descent() - textGrayPaint.ascent();
					canvas.drawText(maxPrice + "", priceLeft, mChartTop +textHeight/2+4,textGrayPaint);
					//�������̼۸񣬾����м�ļ۸�
					double minddlePrice = KChartUtil.format2((maxPrice - minPrice) / 2f + minPrice);
					canvas.drawText(minddlePrice + "", priceLeft,(topDirty.bottom - topDirty.top) / 2 + topDirty.top + textHeight / 4, textGrayPaint);
					//��ͼ۸�
					canvas.drawText(minPrice + "", priceLeft,topDirty.bottom-4, textGrayPaint);
					if(getChartType() == 0){
						// ��ʱͼ�����ұߵ��Ƿ�
						PriceInfo info2 = StockCache.get(StockCache.STOCK_INFO, PriceInfo.class);
						if (info2 != null) {
							String maxZdf = "  "+KChartUtil.getZdF(maxPrice, info2.getYesterday());
							textWidth = textGrayPaint.measureText(maxZdf);
							canvas.drawText(maxZdf, 
									topDirty.right - textWidth - div, mChartTop +textHeight/2+4,textGrayPaint);
							canvas.drawText(KChartUtil.getZdF(minPrice, info2.getYesterday()) ,
									topDirty.right - textWidth - div, topDirty.bottom-4,textGrayPaint);
						}
					}
					double showDealNumber = Math.rint(maxDealNumber / 10000f);
					textWidth = textGrayPaint.measureText(showDealNumber + "");
					textHeight = textGrayPaint.descent() - textGrayPaint.ascent();
//				canvas.drawText("�ɽ���", bottomDirty.left, bottomDirty.top - textHeight / 2f, textGrayPaint);
//				left = bottomDirty.left - textWidth - div
					if(isBigChart){
						canvas.drawText(showDealNumber + "", priceLeft, bottomDirty.top + textHeight / 2f,textGrayPaint);
						
						float y = bottomDirty.top + (bottomDirty.bottom - bottomDirty.top) / 2f + textHeight / 2f;
						canvas.drawText(showDealNumber / 2f + "",priceLeft, y, textGrayPaint);
						
						y = bottomDirty.top + (bottomDirty.bottom - bottomDirty.top) / 2f;
						canvas.drawLine(bottomDirty.left, y, bottomDirty.right, y + 1, textGrayPaint);
						
						textWidth = textGrayPaint.measureText("����");
						canvas.drawText("����", priceLeft, bottomDirty.bottom + textHeight / 4f,textGrayPaint);
					}
							/**
							 * ��ʮ�־����ɽ����D������
							 */
							/**
							 * ��ʮ�־�
							 */
				// �۸�������γɸ�һ������
				double heightPriceScale = (topDirty.bottom - topDirty.top) / (maxPrice - minPrice);
				// �۸�ͳɽ����γɸ�һ������
				double heightDealScale = (bottomDirty.bottom - bottomDirty.top) / (maxDealNumber);
				perWidth = mChartWidth / Float.valueOf(oriSize);
				/**
				 * ��ͼ��k��ͼ��ʱͼ�ͳɽ���ͼ
				 */
				drawChart(canvas, topDirty, grayPaint, bottomDirty, path, textGrayPaint, maxPrice, maxDealNumber,
						heightPriceScale, heightDealScale);

				path.close();
				touchEvent(canvas, topDirty, bottomDirty, maxPrice, heightPriceScale);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public abstract void touchEvent(Canvas canvas, Rect topDirty, Rect bottomDirty, double maxPrice,
			double heightPriceScale);

	public abstract void drawChart(Canvas canvas, Rect topDirty, Paint grayPaint, Rect bottomDirty, Path path,
			Paint textGrayPaint, double maxPrice, double maxDealNumber, double heightPriceScale,
			double heightDealScale);

	/**
	 * �@ȡ��ߺ���ͳɽ��r��
	 * 
	 * @return
	 */
	public abstract double[] getMaxPrice();

	/**
	 * �@ȡ���ɽ���
	 * 
	 * @return
	 */
	public abstract double getMaxDealNumber();

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

	public float per16 = 0.196666666f;
	public float perHalf = 0.5f;

	public float perWidth;

	int pointerPostion = 0;

	long periodMill;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			float x = event.getX();
			float y = event.getY();
			int action = MotionEventCompat.getActionMasked(event);
			switch (action) {
			case (MotionEvent.ACTION_DOWN):
				WedroidLog.e("onTouchEventperiodMill", "" + periodMill);
//			if (periodMill == 0) {
//				periodMill = System.currentTimeMillis();
//				drawGesture(x, y);
//			} else if (System.currentTimeMillis() - periodMill < 800) {
//				enterBigView();
//				periodMill = 0;
//			} else {
//				if (periodMill != 0 && System.currentTimeMillis() - periodMill > 800) {
//					periodMill = 0;
//				}
//				drawGesture(x, y);
//			}
				if (System.currentTimeMillis() - periodMill < 800) {
					enterBigView();
				} else {
					drawGesture(x, y);
				}
				return true;
			case (MotionEvent.ACTION_MOVE):
				drawGesture(x, y);
				return true;
			case (MotionEvent.ACTION_UP):
				periodMill = System.currentTimeMillis();
				pointerPostion = 0;
				invalidate();
				touchEvent(null, null, null, 0, 0);
				return true;
			case (MotionEvent.ACTION_CANCEL):
				return true;
			case (MotionEvent.ACTION_OUTSIDE):
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.onTouchEvent(event);
	}

	TouchCallBack touchCallBack;

	public void setTouchCallback(TouchCallBack callBack) {
		this.touchCallBack = callBack;
	}

	public void enterBigView() {
		WedroidLog.e("enterBigView", "touchCallBack" + touchCallBack);
		try {
			if (touchCallBack != null) {
				touchCallBack.enterBigView();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean drawGesture(float x, float y) {
		try {
			WedroidLog.e("onTouchEvent", "x:" + x + "---y:" + y);
			if (perWidth != 0) {
				pointerPostion = (int) ((x - mChartLeftMargin) / perWidth);
				if (pointerPostion <= stockList.size() && pointerPostion > 0) {
					WedroidLog.e("onTouchEvent", "position:" + pointerPostion);
					invalidate();
					SingleStockInfo singleStockInfo = stockList.get(pointerPostion - 1);
					if (touchCallBack != null) {
						if (y > mChartTop && y < (mChartTopKHeight + mChartTop)) {
							WedroidLog.e("ifParentIterceptorEvent", "����");
							touchCallBack.ifParentIterceptorEvent(true);
						} else {
							WedroidLog.e("ifParentIterceptorEvent", "������");
							touchCallBack.ifParentIterceptorEvent(false);
						}
						touchCallBack.updateViewInTouch(singleStockInfo);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void setStockList(List<SingleStockInfo> stockList) {
		this.stockList = stockList;
	}

	// ԭ�����ȡ�ĸ�����ʵ�����п���û����ô�����ݣ�������Щ��Ʊû���ж��
	int oriSize;

	public void setOriSize(int oriSize) {
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
		lineGrayPaint.setColor(Color.GRAY);
		lineGrayPaint.setAntiAlias(true);
		lineGrayPaint.setStrokeWidth(1);
		lineGrayPaint.setStyle(Style.STROKE);
		return lineGrayPaint;
	}

	public Paint getTextGrayPaint() {
		Paint textGrayPaint = new Paint();
		textGrayPaint.setColor(Color.GRAY);
		textGrayPaint.setAntiAlias(true);
		textGrayPaint.setTextSize(DensityUtil.sp2px(getContext(), 8));
		return textGrayPaint;
	}

	public BaseChartView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	public BaseChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BaseChartView(Context context) {
		this(context, null, 0);
	}

}
