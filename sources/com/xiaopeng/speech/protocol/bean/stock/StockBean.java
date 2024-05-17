package com.xiaopeng.speech.protocol.bean.stock;

import com.xiaopeng.speech.common.bean.BaseBean;
import com.xiaopeng.speech.protocol.utils.StringUtil;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.systemui.carmanager.controller.IIcmController;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class StockBean extends BaseBean {
    public float curMaxTransNum;
    public float curMaxTransPrice;
    public int curPosition;
    public BaseData mBaseData;
    public List<ChartData> mStockList;
    public List<String> mTimeList;
    public String res = "";
    public float baseLineDate = 0.0f;
    public float length = 0.0f;

    public StockBean() {
        this.mBaseData = null;
        this.mStockList = null;
        this.mTimeList = null;
        this.curPosition = 0;
        this.curMaxTransPrice = 0.0f;
        this.curMaxTransNum = 0.0f;
        this.mStockList = new ArrayList();
        this.curPosition = 0;
        this.curMaxTransPrice = 0.0f;
        this.curMaxTransNum = 0.0f;
        this.mBaseData = new BaseData();
        this.mTimeList = new ArrayList();
    }

    public void clearStockData() {
        List<ChartData> list = this.mStockList;
        if (list == null) {
            this.mStockList = new ArrayList();
        } else {
            list.clear();
        }
        List<String> list2 = this.mTimeList;
        if (list2 == null) {
            this.mTimeList = new ArrayList();
        } else {
            list2.clear();
        }
        this.curPosition = 0;
        this.curMaxTransPrice = 0.0f;
        this.curMaxTransNum = 0.0f;
    }

    public void clearChartData() {
        List<ChartData> list = this.mStockList;
        if (list != null && list.size() > 0) {
            this.mStockList.clear();
        }
        List<String> list2 = this.mTimeList;
        if (list2 != null && list2.size() > 0) {
            this.mTimeList.clear();
        }
    }

    public void insertChartData(ChartData item) {
        List<ChartData> list = this.mStockList;
        if (list != null) {
            list.add(item);
        }
    }

    public void notifyMaxDataRetrive() {
        int length = this.mStockList.size();
        for (int i = this.curPosition; i < length; i++) {
            ChartData item = this.mStockList.get(i);
            if (item.mTransPrice > this.curMaxTransPrice) {
                this.curMaxTransPrice = item.mTransPrice;
            }
            if (item.mTransNum > this.curMaxTransNum) {
                this.curMaxTransNum = item.mTransNum;
            }
        }
        int i2 = length - 1;
        this.curPosition = i2;
    }

    public void parseJson(JSONObject jsonObject) {
        if (jsonObject != null) {
            JSONObject result = jsonObject.optJSONObject(SpeechWidget.WIDGET_EXTRA);
            if (result == null) {
                this.res = "fail";
                return;
            }
            this.res = "success";
            if (this.mBaseData == null) {
                this.mBaseData = new BaseData();
            }
            this.mBaseData.symbol = result.optString("symbol");
            this.mBaseData.code = result.optString("code");
            this.mBaseData.name = result.optString("name");
            this.mBaseData.exchange = result.optString("exchange");
            this.mBaseData.low = StringUtil.isDecimalNumber(result.optString("low")) ? Float.valueOf(result.optString("low")).floatValue() : 0.0f;
            this.mBaseData.current = StringUtil.isDecimalNumber(result.optString("current")) ? Float.valueOf(result.optString("current")).floatValue() : 0.0f;
            this.mBaseData.peLYR = StringUtil.isDecimalNumber(result.optString("peLYR")) ? Float.valueOf(result.optString("peLYR")).floatValue() : 0.0f;
            this.mBaseData.peTTM = StringUtil.isDecimalNumber(result.optString("peTTM")) ? Float.valueOf(result.optString("peTTM")).floatValue() : 0.0f;
            this.mBaseData.high = StringUtil.isDecimalNumber(result.optString("high")) ? Float.valueOf(result.optString("high")).floatValue() : 0.0f;
            this.mBaseData.lastClose = StringUtil.isDecimalNumber(result.optString("lastClose")) ? Float.valueOf(result.optString("lastClose")).floatValue() : 0.0f;
            this.mBaseData.percentage = StringUtil.isDecimalNumber(result.optString("percentage")) ? Float.valueOf(result.optString("percentage")).floatValue() : 0.0f;
            this.mBaseData.change = StringUtil.isDecimalNumber(result.optString("change")) ? Float.valueOf(result.optString("change")).floatValue() : 0.0f;
            this.mBaseData.amount = StringUtil.isDecimalNumber(result.optString("amount")) ? Float.valueOf(result.optString("amount")).floatValue() : 0.0f;
            this.mBaseData.open = StringUtil.isDecimalNumber(result.optString(IIcmController.CMD_OPEN)) ? Float.valueOf(result.optString(IIcmController.CMD_OPEN)).floatValue() : 0.0f;
            this.mBaseData.adate = dateStrConvert(result.optString("date") == null ? "" : result.optString("date"));
            this.mBaseData.avolume = (StringUtil.isDecimalNumber(result.optString("volume")) ? Float.valueOf(result.optString("volume")).floatValue() : 0.0f) * 100.0f;
            this.mBaseData.turnoverRate = StringUtil.isDecimalNumber(result.optString("turnoverRate")) ? Float.valueOf(result.optString("turnoverRate")).floatValue() : 0.0f;
            this.mBaseData.state = result.optString("state");
            if (Math.abs(this.mBaseData.high - this.mBaseData.lastClose) > Math.abs(this.mBaseData.lastClose - Math.abs(this.mBaseData.low))) {
                this.baseLineDate = Math.abs(this.mBaseData.lastClose - Math.abs(this.mBaseData.high - this.mBaseData.lastClose));
                this.length = Math.abs(this.mBaseData.high - this.mBaseData.lastClose) * 2.0f;
            } else {
                this.baseLineDate = Math.abs(this.mBaseData.lastClose - Math.abs(this.mBaseData.low - this.mBaseData.lastClose));
                this.length = Math.abs(this.mBaseData.low - this.mBaseData.lastClose) * 2.0f;
            }
            JSONArray time = result.optJSONArray("tradeTime");
            if (time != null) {
                int length = time.length();
                for (int i = 0; i < length; i++) {
                    String item = time.optString(i);
                    List<String> list = this.mTimeList;
                    if (list != null) {
                        list.add(item);
                    }
                }
            }
            JSONArray list2 = result.optJSONObject("chart") != null ? result.optJSONObject("chart").optJSONArray(SpeechWidget.TYPE_LIST) : null;
            if (list2 != null) {
                int length2 = list2.length();
                for (int i2 = 0; i2 < length2; i2++) {
                    try {
                        JSONObject item2 = list2.getJSONObject(i2);
                        if (item2 != null) {
                            String date = item2.optString("date");
                            String volume = item2.optString("volume");
                            String price = item2.optString("price");
                            insertChartData(new ChartData(Float.valueOf(price).floatValue(), Float.valueOf(volume).floatValue(), date));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
            return;
        }
        this.res = "fail";
    }

    private String dateStrConvert(String date) {
        int index;
        String[] sets = date.split("\\+");
        if (sets != null && sets.length == 2) {
            String convertDate = sets[0].replace("T", " ");
            return convertDate;
        }
        String[] sets2 = date.split("T");
        if (sets2 == null || sets2.length != 2 || (index = sets2[1].lastIndexOf("-")) == -1 || index >= sets2[1].length()) {
            return null;
        }
        String convertDate2 = sets2[0] + " " + sets2[1].substring(0, index);
        return convertDate2;
    }

    /* loaded from: classes23.dex */
    public static class ChartData {
        public String mTime;
        public float mTransNum;
        public float mTransPrice;

        public ChartData(float transPrice, float transNum, String mTime) {
            this.mTransPrice = 0.0f;
            this.mTransNum = 0.0f;
            this.mTransPrice = transPrice;
            this.mTransNum = transNum;
            this.mTime = mTime;
        }
    }

    /* loaded from: classes23.dex */
    public static class BaseData {
        public float amount;
        public float avolume;
        public float change;
        public float current;
        public float high;
        public float lastClose;
        public float low;
        public float open;
        public float peLYR;
        public float peTTM;
        public float percentage;
        public float turnoverRate;
        public String symbol = "";
        public String code = "";
        public String adate = "";
        public String name = "";
        public String exchange = "";
        public String state = "";

        public String toString() {
            return "BaseData{symbol='" + this.symbol + "', low=" + this.low + ", current=" + this.current + ", peLYR=" + this.peLYR + ", peTTM=" + this.peTTM + ", high=" + this.high + ", lastClose=" + this.lastClose + ", percentage=" + this.percentage + ", change=" + this.change + ", turnoverRate=" + this.turnoverRate + ", code='" + this.code + "', amount=" + this.amount + ", open=" + this.open + ", adate='" + this.adate + "', avolume=" + this.avolume + ", name='" + this.name + "', exchange='" + this.exchange + "', state='" + this.state + "'}";
        }
    }

    public String toString() {
        return "StockBean{mBaseData=" + this.mBaseData + ", mStockList=" + this.mStockList + ", mTimeList=" + this.mTimeList + ", curPosition=" + this.curPosition + ", curMaxTransPrice=" + this.curMaxTransPrice + ", curMaxTransNum=" + this.curMaxTransNum + ", res='" + this.res + "', baseLineDate=" + this.baseLineDate + ", length=" + this.length + '}';
    }
}
