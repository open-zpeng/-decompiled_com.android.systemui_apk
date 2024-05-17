package com.xiaopeng.speech.protocol.bean.skill;

import com.xiaopeng.speech.common.util.DontProguardClass;
import java.util.List;
@DontProguardClass
/* loaded from: classes23.dex */
public class DataBean {
    private int endColor;
    private String headInfo;
    private String imageRes;
    private boolean isChange = false;
    private String modeName;
    private List<SkillBean> skill;
    private String skillDetailBg;
    private int startColor;

    public String getModeName() {
        return this.modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public List<SkillBean> getSkill() {
        return this.skill;
    }

    public void setSkill(List<SkillBean> skill) {
        this.skill = skill;
    }

    public String getImageRes() {
        return this.imageRes;
    }

    public void setImageRes(String imageRes) {
        this.imageRes = imageRes;
    }

    public String getSkillDetailBg() {
        return this.skillDetailBg;
    }

    public int getStartColor() {
        return this.startColor;
    }

    public void setStartColor(int startColor) {
        this.startColor = startColor;
    }

    public int getEndColor() {
        return this.endColor;
    }

    public void setEndColor(int endColor) {
        this.endColor = endColor;
    }

    public void setSkillDetailBg(String skillDetailBg) {
        this.skillDetailBg = skillDetailBg;
    }

    public boolean isChange() {
        return this.isChange;
    }

    public void setIsChange(boolean isChange) {
        this.isChange = isChange;
    }

    public String getHeadInfo() {
        return this.headInfo;
    }

    public void setHeadInfo(String headInfo) {
        this.headInfo = headInfo;
    }
}
