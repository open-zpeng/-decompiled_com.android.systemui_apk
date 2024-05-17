package com.xiaopeng.speech.protocol.node.navi.bean;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class AddressBean {
    public static final String ADDRESS_FROM_NOT_CORRECTBIGDATA_ = "大数据地址猜错";
    public static final String ADDRESS_FROM_NO_BIGDATA = "没有大数据地址";
    public static final String ADDRESS_TYPE_COMPANY = "公司";
    public static final String ADDRESS_TYPE_DAYCARE = "daycare";
    public static final String ADDRESS_TYPE_EN_COMPANY = "work";
    public static final String ADDRESS_TYPE_EN_FAVOURITE = "favourite";
    public static final String ADDRESS_TYPE_EN_HOME = "home";
    public static final String ADDRESS_TYPE_FAVOURITE = "收藏夹";
    public static final String ADDRESS_TYPE_GYM = "gym";
    public static final String ADDRESS_TYPE_HOME = "家";
    public static final String ADDRESS_TYPE_SCHOOL = "school";
    private String addressType;
    private String from;

    public static final AddressBean fromJson(String data) {
        AddressBean addressBean = new AddressBean();
        try {
            JSONObject jsonObject = new JSONObject(data);
            addressBean.addressType = jsonObject.optString("addressType");
            addressBean.from = jsonObject.optString("from");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return addressBean;
    }

    public String getAddressType() {
        return this.addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String toString() {
        return "AddressBean{addressType='" + this.addressType + "', from='" + this.from + "'}";
    }
}
