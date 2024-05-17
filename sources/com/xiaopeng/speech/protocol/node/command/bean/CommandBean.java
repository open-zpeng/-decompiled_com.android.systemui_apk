package com.xiaopeng.speech.protocol.node.command.bean;

import com.xiaopeng.speech.common.bean.BaseBean;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class CommandBean extends BaseBean {
    private String json;
    private String packagename;

    public static CommandBean fromJson(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            CommandBean commandBean = new CommandBean();
            commandBean.setPackagename(jsonObject.optString("packagename"));
            commandBean.setJson(jsonObject.optString("json"));
            return commandBean;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPackagename() {
        return this.packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public String getJson() {
        return this.json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String toString() {
        return "CommandBean{packagename='" + this.packagename + "', json='" + this.json + "'}";
    }
}
