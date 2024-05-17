package com.xiaopeng.speech.protocol.bean.config;

import com.xiaopeng.speech.common.util.DontProguardClass;
import java.util.List;
@DontProguardClass
/* loaded from: classes23.dex */
public class GuideDataBean {
    private List<GuideConentBean> content;
    private String isChange;
    private boolean isOpened;
    private String name;
    private int type;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsChange() {
        return this.isChange;
    }

    public void setIsChange(String isChange) {
        this.isChange = isChange;
    }

    public boolean isOpened() {
        return this.isOpened;
    }

    public void setOpened(boolean opened) {
        this.isOpened = opened;
    }

    public List<GuideConentBean> getContent() {
        return this.content;
    }

    public void setContent(List<GuideConentBean> content) {
        this.content = content;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
