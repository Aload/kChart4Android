package com.wuchuanlong.stockview.chart;
import com.example.stockview.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
/**
 * 鍔犺浇妗�
 * @author 鍚翠紶榫�
 * <BR/> QQ:312037487
 */
public class CircleLoadingView extends ViewGroup {

	public View normalView;
	private View errorView;
	private View view;
	// loading
	private TextView loaingText;
	// 鍔犺浇澶辫触
	private TextView loaingErrorText;

	public CircleLoadingView(Context context) {
		this(context, null, 0);
	}

	public CircleLoadingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		view = View.inflate(getContext(), R.layout.chart_data_loading_layout, null);
		addView(view);
		normalView = view.findViewById(R.id.ll_data_loading);
		errorView = view.findViewById(R.id.ll_data_loading_error);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.loading_dialog);
		int defaultColor = context.getResources().getColor(android.R.color.white);
		int color = typedArray.getColor(R.styleable.loading_dialog_loading_text_color, defaultColor);
		loaingText = (TextView) view.findViewById(R.id.tv_data_loading);
		loaingErrorText = (TextView) view.findViewById(R.id.tv_data_loading_error);
		loaingText.setTextColor(color);
		loaingErrorText.setTextColor(color);
		typedArray.recycle();
	}

	public CircleLoadingView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).layout(l, 0, r, b);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	public void hiden() {
		setVisibility(View.GONE);
		view.setVisibility(View.GONE);
	}

	public void asynHidden() {
		post(new Runnable() {
			@Override
			public void run() {
				setVisibility(View.GONE);
				view.setVisibility(View.GONE);
			}
		});
	}

	public void asynShowLoading() {
		post(new Runnable() {
			@Override
			public void run() {
				setVisibility(View.VISIBLE);
				view.setVisibility(View.VISIBLE);
				errorView.setVisibility(View.GONE);
				normalView.setVisibility(View.VISIBLE);
			}
		});
	}

	public void showLoading() {
		setVisibility(View.VISIBLE);
		view.setVisibility(View.VISIBLE);
		errorView.setVisibility(View.GONE);
		normalView.setVisibility(View.VISIBLE);
	}

	public void showLoading(String text) {
		setVisibility(View.VISIBLE);
		view.setVisibility(View.VISIBLE);
		errorView.setVisibility(View.GONE);
		normalView.setVisibility(View.VISIBLE);
		loaingText.setText(text);
	}

	public void showLoadingError() {
		setVisibility(View.VISIBLE);
		view.setVisibility(View.VISIBLE);
		errorView.setVisibility(View.VISIBLE);
		normalView.setVisibility(View.GONE);
	}

	public void showLoadingError(String text) {
		setVisibility(View.VISIBLE);
		view.setVisibility(View.VISIBLE);
		errorView.setVisibility(View.VISIBLE);
		normalView.setVisibility(View.GONE);
		loaingErrorText.setText(text);
	}

	public void asynShowLoadingError() {
		post(new Runnable() {
			@Override
			public void run() {
				setVisibility(View.VISIBLE);
				view.setVisibility(View.VISIBLE);
				errorView.setVisibility(View.VISIBLE);
				normalView.setVisibility(View.GONE);
			}
		});
	}

}
