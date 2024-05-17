package com.xiaopeng.speech.protocol.bean.config;

import com.xiaopeng.speech.common.util.DontProguardClass;
import java.util.List;
@DontProguardClass
/* loaded from: classes23.dex */
public class SceneGuideData {
    private List<GuideDataBean> data;
    private boolean isOpened;
    private String subtitle;
    private String title;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return this.subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public boolean isOpened() {
        return this.isOpened;
    }

    public void setOpened(boolean opened) {
        this.isOpened = opened;
    }

    public List<GuideDataBean> getData() {
        return this.data;
    }

    public void setData(List<GuideDataBean> data) {
        this.data = data;
    }
}
