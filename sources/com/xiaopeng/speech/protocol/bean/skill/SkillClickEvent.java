package com.xiaopeng.speech.protocol.bean.skill;

import com.xiaopeng.speech.common.util.DontProguardClass;
@DontProguardClass
/* loaded from: classes23.dex */
public class SkillClickEvent {
    public static final String ACTION_CLICK_TIP = "skill_action_click_tip";
    public String action;
    public String text;

    public SkillClickEvent(String action, String text) {
        this.action = action;
        this.text = text;
    }
}
