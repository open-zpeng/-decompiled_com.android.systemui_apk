package com.xiaopeng.speech.protocol.bean.skill;

import com.xiaopeng.speech.common.util.DontProguardClass;
@DontProguardClass
/* loaded from: classes23.dex */
public class TipsBean {
    private String action;
    private String tipName;

    public String getTipName() {
        return this.tipName;
    }

    public void setTipName(String tipName) {
        this.tipName = tipName;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
