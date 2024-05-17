package com.xiaopeng.speech.protocol.node.phone.bean;

import android.text.TextUtils;
import com.xiaopeng.speech.common.bean.BaseBean;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class PhoneBean extends BaseBean {
    private String address;
    private String id;
    private String name;
    private String number;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static PhoneBean fromJson(String data) {
        PhoneBean phone = new PhoneBean();
        try {
            JSONObject jsonObject = new JSONObject(data);
            phone.name = jsonObject.optString("联系人");
            if (TextUtils.isEmpty(phone.name)) {
                phone.name = jsonObject.optString("任意联系人");
            }
            phone.number = jsonObject.optString("号码");
            phone.address = jsonObject.optString("归属地");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return phone;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("任意联系人", this.name).putOpt("号码", this.number).putOpt("归属地", this.address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
