package com.xiaopeng.speech.protocol.bean.skill;

import com.xiaopeng.speech.common.util.DontProguardClass;
import java.util.List;
@DontProguardClass
/* loaded from: classes23.dex */
public class SkillBean {
    private String function;
    private boolean isChange = true;
    private List<TipsBean> tips;

    public String getFunction() {
        return this.function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public List<TipsBean> getTips() {
        return this.tips;
    }

    public void setTips(List<TipsBean> tips) {
        this.tips = tips;
    }

    public boolean isChange() {
        return this.isChange;
    }

    public void setChange(boolean isChange) {
        this.isChange = isChange;
    }
}
