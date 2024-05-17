package com.xiaopeng.speech.protocol.bean.config;

import com.xiaopeng.speech.common.util.DontProguardClass;
@DontProguardClass
/* loaded from: classes23.dex */
public class OperationBean {
    private String des;
    private String imgDisable;
    private String imgEnable;
    private boolean isOpened;
    private String tag;
    private String tips;
    private String title;
    private String type;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isOpened() {
        return this.isOpened;
    }

    public void setOpened(boolean opened) {
        this.isOpened = opened;
    }

    public String getTips() {
        return this.tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return this.des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getImgEnable() {
        return this.imgEnable;
    }

    public void setImgEnable(String imgEnable) {
        this.imgEnable = imgEnable;
    }

    public String getImgDisable() {
        return this.imgDisable;
    }

    public void setImgDisable(String imgDisable) {
        this.imgDisable = imgDisable;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
