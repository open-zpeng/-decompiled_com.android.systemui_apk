package com.xiaopeng.speech.protocol.bean.stats;

import com.xiaopeng.speech.common.util.DontProguardClass;
@DontProguardClass
/* loaded from: classes23.dex */
public abstract class StatCommonBean {
    public static final int EVENT_RECOMMEND_ID = 401;
    public static final int EVENT_SCENE_SETTING_ID = 601;
    public static final int EVENT_SCENE_SETTING_SWITCH_ID = 603;
    public static final int EVENT_Skill_ID = 501;
    private float driveTemp;
    public int eventId;
    public String hwVersion;
    public String speechVersionCode;
    public String speechVersionName;
    public float speed;
    public long startTime = System.currentTimeMillis();
    public String userId;

    public StatCommonBean(int eventId) {
        this.eventId = eventId;
    }

    public StatCommonBean addhwVersion(String hwVersion) {
        this.hwVersion = hwVersion;
        return this;
    }

    public StatCommonBean addSpeechVersionName(String speechVersionName) {
        this.speechVersionName = speechVersionName;
        return this;
    }

    public StatCommonBean addHardwareId(String hardwareId) {
        this.userId = hardwareId;
        return this;
    }

    public StatCommonBean addhAppVersionCode(String appVersionCode) {
        this.speechVersionCode = appVersionCode;
        return this;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setDriveTemp(float driveTemp) {
        this.driveTemp = driveTemp;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
