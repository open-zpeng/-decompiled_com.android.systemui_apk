package com.xiaopeng.speech.protocol.bean.config;

import com.xiaopeng.speech.common.util.DontProguardClass;
import java.util.Map;
@DontProguardClass
/* loaded from: classes23.dex */
public class OperationInfoBean {
    private Map<String, OperationBean> data;
    private boolean isOpened;
    private String subTitle;
    private String title;

    public boolean isOpened() {
        return this.isOpened;
    }

    public void setOpened(boolean opened) {
        this.isOpened = opened;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return this.subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Map<String, OperationBean> getData() {
        return this.data;
    }

    public void setData(Map<String, OperationBean> data) {
        this.data = data;
    }
}
