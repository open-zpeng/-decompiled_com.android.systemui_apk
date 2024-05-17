package com.xiaopeng.speech.protocol.bean.stats;

import com.xiaopeng.speech.common.util.DontProguardClass;
@DontProguardClass
/* loaded from: classes23.dex */
public class RecommendStatBean extends StatCommonBean {
    private int clickPosition;
    private String clickText;
    private long clickTime;
    private String firstText;
    private String msgId;
    private String refMsgId;
    private String secondText;
    private long showTime;
    private String subType;
    private String thirdText;
    private String type;

    public String getSubType() {
        return this.subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getRefMsgId() {
        return this.refMsgId;
    }

    public void setRefMsgId(String refMsgId) {
        this.refMsgId = refMsgId;
    }

    public RecommendStatBean() {
        super(401);
    }

    public String getClickText() {
        return this.clickText;
    }

    public void setClickText(String clickText) {
        this.clickText = clickText;
    }

    public int getClickPosition() {
        return this.clickPosition;
    }

    public void setClickPosition(int clickPosition) {
        this.clickPosition = clickPosition;
    }

    public long getClickTime() {
        return this.clickTime;
    }

    public void setClickTime(long clickTime) {
        this.clickTime = clickTime;
    }

    public void setShowTime(long showTime) {
        this.showTime = showTime;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getShowTime() {
        return this.showTime;
    }

    public String getFirstText() {
        return this.firstText;
    }

    public void setFirstText(String firstText) {
        this.firstText = firstText;
    }

    public String getSecondText() {
        return this.secondText;
    }

    public void setSecondText(String secondText) {
        this.secondText = secondText;
    }

    public String getThirdText() {
        return this.thirdText;
    }

    public void setThirdText(String thirdText) {
        this.thirdText = thirdText;
    }

    public String toString() {
        return "RecommendStatBean{type='" + this.type + "', showTime='" + this.showTime + "', firstText='" + this.firstText + "', secondText='" + this.secondText + "', thirdText='" + this.thirdText + "'}";
    }
}
