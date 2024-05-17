package com.xiaopeng.speech.protocol.bean.stats;

import com.xiaopeng.speech.common.util.DontProguardClass;
@DontProguardClass
/* loaded from: classes23.dex */
public class SkillStatisticsBean extends StatCommonBean {
    public static final String PAGE_AIR = "air";
    public static final String PAGE_ANSWER = "answer";
    public static final String PAGE_CAR = "car";
    public static final String PAGE_ENJOY = "enjoy";
    public static final String PAGE_HOME = "home";
    public static final String PAGE_MUSIC = "music";
    public static final String PAGE_NAVI = "navi";
    public static final String PAGE_PHONE = "phone";
    public static final String PAGE_RECOMMEND = "recommend";
    public static final String PAGE_SETTING = "setting";
    public static final String PAGE_STATUS = "status";
    private long endTime;
    private long enterTime;
    private String page;

    public SkillStatisticsBean(String page, long enterTime) {
        super(501);
        this.enterTime = enterTime;
        this.page = page;
    }

    public SkillStatisticsBean(long endTime, String page) {
        super(501);
        this.endTime = endTime;
        this.page = page;
    }
}
