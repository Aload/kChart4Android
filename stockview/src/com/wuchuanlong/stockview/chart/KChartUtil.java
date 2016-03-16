package com.wuchuanlong.stockview.chart;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
/**
 * k��ͼ������
 * @author �⴫��
 * <BR/> QQ:312037487
 */
public class KChartUtil {
	public static final int UP_COLOR = 0xffd70b17;
	public static final int DOWN_COLOR = 0xff339900;
	/**
	 * DecimalFormatת������
	 */
	public static String format1(double input) {
		DecimalFormat df = new DecimalFormat("#.#");
		return df.format(input);
	}

	/**
	 * DecimalFormatת������
	 */
	public static double format2(double input) {
		DecimalFormat df = new DecimalFormat("#.00");
		return Double.valueOf(df.format(input));
	}

	// ����ǰ�۸�-�������̼۸�/ �������̼۸� = �ǵ���
	public static String getZdF(double now, double yesClose) {
		try {
			double zd = (now - yesClose) / yesClose;
			return format2((zd * 100)) + "%";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0%";
	}
	
	public static double getPrice(double yesClose, double zdf) {
		double price = (zdf/100)*yesClose+yesClose;
		return format2(price);
	}
	
	public static double getZdF2(double now, double yesClose) {
		try {
			double zd = (now - yesClose) / yesClose;
			return format2((zd * 100));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	// �ǵ�=������-�����̡�
	public static String getZd(double todClose, double yesClose) {
		return format2((todClose - yesClose * 100)) + "";
	}

	public static String getDateYYYYMM(int offset) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, offset);
		return calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH);
	}

	public static int getMonth() {
		return Calendar.getInstance().get(Calendar.MONTH);
	}

	public static int getDayOfMonth(int offset) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, offset);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public static String getMinute(int minute) {
		int hour = minute / 60;// ʱ
		int min = minute % 60;// ��
		return hour + ":" + (min < 10 ? ("0" + min) : min);
	}

	public static void calcMA(SingleStockInfo kChartDayBean, int days, int position) {
		if (days < 2) {
			return;
		}
		float sum = 0;
		float avg = 0;
		float close = (float) kChartDayBean.getClose();
		if (position < days) {
			sum = sum + close;
			avg = sum / (position + 1f);
		} else {
			sum = sum + close - (float) (float) kChartDayBean.getClose();
			avg = sum / days;
		}
		if (days == 5) {
			kChartDayBean.setMaValue5(avg);
		} else if (days == 10) {
			kChartDayBean.setMaValue10(avg);
		} else if (days == 20) {
			kChartDayBean.setMaValue20(avg);
		}
	}

	/**
	 * �������
	 * 
	 * @param kChartDayBean
	 * @param days
	 * @return
	 */
	public static List<SingleStockInfo> calcMAF2T(List<SingleStockInfo> kChartDayBean, int days) {

		if (days < 2) {
			return null;
		}

		float sum = 0;
		float avg = 0;
		for (int i = 0; i < kChartDayBean.size(); i++) {
			float close = (float) kChartDayBean.get(i).getClose();
			if (i < days) {
				sum = sum + close;
				avg = sum / (i + 1f);
			} else {
				sum = sum + close - (float) (float) kChartDayBean.get(i - days).getClose();
				avg = sum / days;
			}
			if (days == 5) {
				kChartDayBean.get(i).setMaValue5(avg);
			} else if (days == 10) {
				kChartDayBean.get(i).setMaValue10(avg);
			} else if (days == 20) {
				kChartDayBean.get(i).setMaValue20(avg);
			}
		}
		return kChartDayBean;
	}

}
