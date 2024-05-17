package com.xiaopeng.speech.protocol.node.message.bean;

import com.xiaopeng.speech.common.bean.BaseBean;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class IndexBean extends BaseBean {
    private int index;

    public static final IndexBean fromJson(String data) {
        IndexBean indexBean = new IndexBean();
        try {
            JSONObject jsonObject = new JSONObject(data);
            indexBean.index = jsonObject.optInt("index");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return indexBean;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String toString() {
        return "IndexBean{index='" + this.index + "'}";
    }
}
