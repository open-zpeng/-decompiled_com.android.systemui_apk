package com.xiaopeng.speech.protocol.node.navi.bean;

import com.xiaopeng.speech.common.util.DontProguardClass;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.protocol.utils.CarTypeUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
@DontProguardClass
/* loaded from: classes23.dex */
public class PoiExtraBean {
    private static final String TAG = "PoiExtraBean";
    private int acFreeNums;
    private int acNums;
    private boolean busiState;
    private String busiTime;
    private List<ChargeBean> content;
    private int dcFreeNums;
    private int dcNums;
    private String payment;
    private int selfSupportFlag;

    public int getSelfSupportFlag() {
        return this.selfSupportFlag;
    }

    public void setSelfSupportFlag(int selfSupportFlag) {
        this.selfSupportFlag = selfSupportFlag;
    }

    public int getDcNums() {
        return this.dcNums;
    }

    public void setDcNums(int dcNums) {
        this.dcNums = dcNums;
    }

    public int getDcFreeNums() {
        return this.dcFreeNums;
    }

    public void setDcFreeNums(int dcFreeNums) {
        this.dcFreeNums = dcFreeNums;
    }

    public int getAcNums() {
        return this.acNums;
    }

    public void setAcNums(int acNums) {
        this.acNums = acNums;
    }

    public int getAcFreeNums() {
        return this.acFreeNums;
    }

    public void setAcFreeNums(int acFreeNums) {
        this.acFreeNums = acFreeNums;
    }

    public boolean getBusiState() {
        return this.busiState;
    }

    public void setBusiState(boolean busiState) {
        this.busiState = busiState;
    }

    public String getBusiTime() {
        return this.busiTime;
    }

    public void setBusiTime(String busiTime) {
        this.busiTime = busiTime;
    }

    public String getPayment() {
        return this.payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public List<ChargeBean> getContent() {
        return this.content;
    }

    public void setContent(List<ChargeBean> content) {
        this.content = content;
    }

    public static PoiExtraBean fromJson(String fromJson) {
        PoiExtraBean poiExtra = new PoiExtraBean();
        boolean isOverseasCarType = CarTypeUtils.isOverseasCarType();
        LogUtils.d(TAG, "fromJson, isOverseasCarType: " + isOverseasCarType);
        try {
            JSONObject json = new JSONObject(fromJson);
            if (json.has("dcNums")) {
                poiExtra.dcNums = json.optInt("dcNums");
            }
            if (json.has("dcFreeNums")) {
                poiExtra.dcFreeNums = json.optInt("dcFreeNums");
            }
            if (json.has("acNums")) {
                poiExtra.acNums = json.optInt("acNums");
            }
            if (json.has("acFreeNums")) {
                if (!isOverseasCarType) {
                    poiExtra.acFreeNums = json.optInt("acFreeNums");
                } else {
                    poiExtra.dcNums = json.optInt("acFreeNums");
                }
            }
            if (json.has("selfSupportFlag")) {
                poiExtra.selfSupportFlag = json.optInt("selfSupportFlag");
            }
            if (isOverseasCarType) {
                if (json.has("busiState")) {
                    poiExtra.busiState = json.optBoolean("busiState");
                }
                if (json.has("busiTime")) {
                    poiExtra.busiTime = json.optString("busiTime");
                }
                if (json.has("payment")) {
                    poiExtra.payment = json.optString("payment");
                }
                JSONArray charges = json.optJSONArray("content");
                if (charges != null && charges.length() > 0) {
                    List<ChargeBean> chargeBeans = new ArrayList<>();
                    for (int i = 0; i < charges.length(); i++) {
                        JSONObject object = charges.getJSONObject(i);
                        ChargeBean chargeBean = parseChargeBean(object);
                        if (chargeBean != null) {
                            chargeBeans.add(chargeBean);
                        }
                    }
                    poiExtra.setContent(chargeBeans);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return poiExtra;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("dcNums", this.dcNums);
        json.put("dcFreeNums", this.dcFreeNums);
        json.put("acNums", this.acNums);
        json.put("acFreeNums", this.acFreeNums);
        json.put("selfSupportFlag", this.selfSupportFlag);
        json.put("busiState", this.busiState);
        json.put("busiTime", this.busiTime);
        json.put("payment", this.payment);
        JSONArray jsArray = new JSONArray();
        List<ChargeBean> list = this.content;
        if (list != null && list.size() > 0) {
            for (ChargeBean chargeBean : this.content) {
                jsArray.put(chargeBean.toJson());
            }
        }
        json.put("content", jsArray);
        return json;
    }

    private static ChargeBean parseChargeBean(JSONObject beanObject) {
        if (beanObject == null) {
            return null;
        }
        ChargeBean chargeBean = new ChargeBean();
        chargeBean.setTotal(beanObject.optInt("total"));
        chargeBean.setAvailable(beanObject.optInt("available"));
        chargeBean.setPower(beanObject.optString("power"));
        return chargeBean;
    }

    public String toString() {
        return "PoiExtraBean{dcNums='" + this.dcNums + "', dcFreeNums='" + this.dcFreeNums + "', acNums='" + this.acNums + "', acFreeNums=" + this.acFreeNums + ", selfSupportFlag=" + this.selfSupportFlag + ", busiState='" + this.busiState + "', busiTime=" + this.busiTime + ", payment='" + this.payment + "', content='" + this.content + '}';
    }
}
