package com.wuchuanlong.stockview.chart;

import android.os.Parcel;
import android.os.Parcelable;


public class WuDangInfo implements Parcelable {
    //股票代码
    private String code;
    //股票名称
    private String name;
    //股票类型
    private String stktype;
    //市场类型
    private String market;
    //日期
    private String date;
    //分钟数
    private String minute;
    //卖价5
    private double sell5;
    //卖价4
    private double sell4;
    //卖价3
    private double sell3;
    //卖价2
    private double sell2;
    //卖价1
    private double sell1;

    //卖量5
    private double sellValume5;
    //卖量4
    private double sellValume4;
    //卖量3
    private double sellValume3;
    //卖量2
    private double sellValume2;
    //卖量1
    private double sellValume1;

    //买价1
    private double buy1;
    //买价2
    private double buy2;
    //买价3
    private double buy3;
    //买价4
    private double buy4;
    //买价5
    private double buy5;

    //买量1
    private double buyValume1;
    //买量2
    private double buyValume2;
    //买量3
    private double buyValume3;
    //买量4
    private double buyValume4;
    //买量5
    private double buyValume5;

    //昨收
    private double yesterday;
    //今开
    private double open;
    //最高
    private double high;
    //最低
    private double low;
    //现价
    private double now;
    //总成交量
    private double totalValume;
    //总成交额
    private double totalAmount;
    //外盘
    private double outside;
    //内盘
    private double inside;
    //涨跌
    private double up;
    //涨跌百分比
    private double uppercent;
    //成交手数
    private double volume;
    //量比
    private double volrate;
    //PE(市盈率)
    private double pgr;
    //拼音简称
    private String pyname;

    public WuDangInfo() {

    }

    public WuDangInfo(Parcel source) {
        this.code = source.readString();
        this.name = source.readString();
        this.stktype = source.readString();
        this.market = source.readString();
        this.date = source.readString();
        this.minute = source.readString();
        this.sell5 = source.readDouble();
        this.sell4 = source.readDouble();
        this.sell3 = source.readDouble();
        this.sell2 = source.readDouble();
        this.sell1 = source.readDouble();

        this.sellValume5 = source.readDouble();
        this.sellValume4 = source.readDouble();
        this.sellValume3 = source.readDouble();
        this.sellValume2 = source.readDouble();
        this.sellValume1 = source.readDouble();

        this.buy1 = source.readDouble();
        this.buy2 = source.readDouble();
        this.buy3 = source.readDouble();
        this.buy4 = source.readDouble();
        this.buy5 = source.readDouble();

        this.buyValume1 = source.readDouble();
        this.buyValume2 = source.readDouble();
        this.buyValume3 = source.readDouble();
        this.buyValume4 = source.readDouble();
        this.buyValume5 = source.readDouble();

        this.yesterday = source.readDouble();
        this.open = source.readDouble();
        this.high = source.readDouble();
        this.low = source.readDouble();
        this.now = source.readDouble();

        this.totalValume = source.readDouble();
        this.totalAmount = source.readDouble();
        this.outside = source.readDouble();
        this.inside = source.readDouble();
        this.up = source.readDouble();

        this.uppercent = source.readDouble();
        this.volume = source.readDouble();
        this.volrate = source.readDouble();
        this.pgr = source.readDouble();
        this.pyname = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
        dest.writeString(stktype);
        dest.writeString(market);
        dest.writeString(date);
        dest.writeString(minute);

        dest.writeDouble(sell5);
        dest.writeDouble(sell4);
        dest.writeDouble(sell3);
        dest.writeDouble(sell2);
        dest.writeDouble(sell1);

        dest.writeDouble(sellValume5);
        dest.writeDouble(sellValume4);
        dest.writeDouble(sellValume3);
        dest.writeDouble(sellValume2);
        dest.writeDouble(sellValume1);

        dest.writeDouble(buy1);
        dest.writeDouble(buy2);
        dest.writeDouble(buy3);
        dest.writeDouble(buy4);
        dest.writeDouble(buy5);

        dest.writeDouble(buyValume1);
        dest.writeDouble(buyValume2);
        dest.writeDouble(buyValume3);
        dest.writeDouble(buyValume4);
        dest.writeDouble(buyValume5);

        dest.writeDouble(yesterday);
        dest.writeDouble(open);
        dest.writeDouble(high);
        dest.writeDouble(low);
        dest.writeDouble(now);
        dest.writeDouble(totalValume);
        dest.writeDouble(totalAmount);
        dest.writeDouble(outside);
        dest.writeDouble(inside);
        dest.writeDouble(up);
        dest.writeDouble(uppercent);
        dest.writeDouble(volume);
        dest.writeDouble(volrate);
        dest.writeDouble(pgr);
        dest.writeString(pyname);

    }

    public static final Creator<WuDangInfo> CREATOR = new Creator<WuDangInfo>() {
        @Override
        public WuDangInfo createFromParcel(Parcel source) {
            return new WuDangInfo(source);
        }

        @Override
        public WuDangInfo[] newArray(int i) {
            return new WuDangInfo[i];
        }
    };

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStktype() {
        return stktype;
    }

    public void setStktype(String stktype) {
        this.stktype = stktype;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public double getSell5() {
        return sell5;
    }

    public void setSell5(double sell5) {
        this.sell5 = sell5;
    }

    public double getSell4() {
        return sell4;
    }

    public void setSell4(double sell4) {
        this.sell4 = sell4;
    }

    public double getSell3() {
        return sell3;
    }

    public void setSell3(double sell3) {
        this.sell3 = sell3;
    }

    public double getSell2() {
        return sell2;
    }

    public void setSell2(double sell2) {
        this.sell2 = sell2;
    }

    public double getSell1() {
        return sell1;
    }

    public void setSell1(double sell1) {
        this.sell1 = sell1;
    }

    public double getSellValume5() {
        return sellValume5;
    }

    public void setSellValume5(double sellValume5) {
        this.sellValume5 = sellValume5;
    }

    public double getSellValume4() {
        return sellValume4;
    }

    public void setSellValume4(double sellValume4) {
        this.sellValume4 = sellValume4;
    }

    public double getSellValume3() {
        return sellValume3;
    }

    public void setSellValume3(double sellValume3) {
        this.sellValume3 = sellValume3;
    }

    public double getSellValume2() {
        return sellValume2;
    }

    public void setSellValume2(double sellValume2) {
        this.sellValume2 = sellValume2;
    }

    public double getSellValume1() {
        return sellValume1;
    }

    public void setSellValume1(double sellValume1) {
        this.sellValume1 = sellValume1;
    }

    public double getBuy1() {
        return buy1;
    }

    public void setBuy1(double buy1) {
        this.buy1 = buy1;
    }

    public double getBuy2() {
        return buy2;
    }

    public void setBuy2(double buy2) {
        this.buy2 = buy2;
    }

    public double getBuy3() {
        return buy3;
    }

    public void setBuy3(double buy3) {
        this.buy3 = buy3;
    }

    public double getBuy4() {
        return buy4;
    }

    public void setBuy4(double buy4) {
        this.buy4 = buy4;
    }

    public double getBuy5() {
        return buy5;
    }

    public void setBuy5(double buy5) {
        this.buy5 = buy5;
    }

    public double getBuyValume1() {
        return buyValume1;
    }

    public void setBuyValume1(double buyValume1) {
        this.buyValume1 = buyValume1;
    }

    public double getBuyValume2() {
        return buyValume2;
    }

    public void setBuyValume2(double buyValume2) {
        this.buyValume2 = buyValume2;
    }

    public double getBuyValume3() {
        return buyValume3;
    }

    public void setBuyValume3(double buyValume3) {
        this.buyValume3 = buyValume3;
    }

    public double getBuyValume4() {
        return buyValume4;
    }

    public void setBuyValume4(double buyValume4) {
        this.buyValume4 = buyValume4;
    }

    public double getBuyValume5() {
        return buyValume5;
    }

    public void setBuyValume5(double buyValume5) {
        this.buyValume5 = buyValume5;
    }

    public double getYesterday() {
        return yesterday;
    }

    public void setYesterday(double yesterday) {
        this.yesterday = yesterday;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getNow() {
        return now;
    }

    public void setNow(double now) {
        this.now = now;
    }

    public double getTotalValume() {
        return totalValume;
    }

    public void setTotalValume(double totalValume) {
        this.totalValume = totalValume;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getOutside() {
        return outside;
    }

    public void setOutside(double outside) {
        this.outside = outside;
    }

    public double getInside() {
        return inside;
    }

    public void setInside(double inside) {
        this.inside = inside;
    }

    public double getUp() {
        return up;
    }

    public void setUp(double up) {
        this.up = up;
    }

    public double getUppercent() {
        return uppercent;
    }

    public void setUppercent(double uppercent) {
        this.uppercent = uppercent;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getVolrate() {
        return volrate;
    }

    public void setVolrate(double volrate) {
        this.volrate = volrate;
    }

    public double getPgr() {
        return pgr;
    }

    public void setPgr(double pgr) {
        this.pgr = pgr;
    }

    public String getPyname() {
        return pyname;
    }

    public void setPyname(String pyname) {
        this.pyname = pyname;
    }
}
