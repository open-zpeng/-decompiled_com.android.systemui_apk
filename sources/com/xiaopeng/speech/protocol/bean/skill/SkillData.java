package com.xiaopeng.speech.protocol.bean.skill;

import com.xiaopeng.speech.common.util.DontProguardClass;
import java.util.List;
@DontProguardClass
/* loaded from: classes23.dex */
public class SkillData {
    private String aiSubTitle;
    private String aiTitle;
    private List<DataBean> data;
    private String subtitle;
    private int switchSkill;
    private String title;

    public List<DataBean> getData() {
        return this.data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return this.subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getSwitchSkill() {
        return this.switchSkill;
    }

    public void setSwitchSkill(int switchSkill) {
        this.switchSkill = switchSkill;
    }

    public String getAiTitle() {
        return this.aiTitle;
    }

    public void setAiTitle(String aiTitle) {
        this.aiTitle = aiTitle;
    }

    public String getAiSubTitle() {
        return this.aiSubTitle;
    }

    public void setAiSubTitle(String aiSubTitle) {
        this.aiSubTitle = aiSubTitle;
    }
}
