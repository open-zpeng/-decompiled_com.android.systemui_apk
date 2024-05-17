package com.xiaopeng.speech.protocol.bean.stats;

import com.xiaopeng.speech.common.util.DontProguardClass;
@DontProguardClass
/* loaded from: classes23.dex */
public class SceneSwitchStatisticsBean extends StatCommonBean {
    public static final String NAME_ONESHOT = "oneshot";
    public static final String NAME_SCENE = "scene";
    public static final String NAME_USERPROTOCOL = "userProtocol";
    public static final String NAME_WAKEUP = "wakeup";
    private String name;
    private boolean state;

    public SceneSwitchStatisticsBean(String name, boolean state) {
        super(StatCommonBean.EVENT_SCENE_SETTING_SWITCH_ID);
        this.name = name;
        this.state = state;
    }
}
