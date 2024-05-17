package com.xiaopeng.speech.protocol.bean.stats;

import com.xiaopeng.speech.common.util.DontProguardClass;
@DontProguardClass
/* loaded from: classes23.dex */
public class SceneSettingStatisticsBean extends StatCommonBean {
    public static final String PAGE_CUSTOM = "custom";
    public static final String PAGE_DIALOGUE = "dialogue";
    public static final String PAGE_GUIDE = "guide";
    public static final String PAGE_HOME = "home";
    public static final String PAGE_INTERRUPTION = "interruption";
    public static final String PAGE_KEY_WAKEUP = "keyWakeup";
    public static final String PAGE_PROTOCOLPOLICY = "protocolPolicy";
    public static final String PAGE_USERPROTOCOL = "userProtocol";
    public static final String PAGE_VISIBLE = "visible";
    public static final String PAGE_ZONE_LOCK = "zoneLock";
    private long endTime;
    private long enterTime;
    private String page;

    public SceneSettingStatisticsBean(String page, long enterTime) {
        super(StatCommonBean.EVENT_SCENE_SETTING_ID);
        this.enterTime = enterTime;
        this.page = page;
    }

    public SceneSettingStatisticsBean(long endTime, String page) {
        super(StatCommonBean.EVENT_SCENE_SETTING_ID);
        this.endTime = endTime;
        this.page = page;
    }
}
