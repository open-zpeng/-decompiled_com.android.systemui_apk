package com.xiaopeng.speech.protocol.bean.recommend;

import com.xiaopeng.speech.common.util.DontProguardClass;
import com.xiaopeng.speech.protocol.bean.base.ButtonBean;
import java.io.Serializable;
import java.util.List;
@DontProguardClass
/* loaded from: classes23.dex */
public class RecommendBean implements Serializable {
    public static final String SHOW_TIME_GLOBAL = "global";
    public static final String SHOW_TIME_RESULT = "result";
    public static final String SHOW_TIME_TTS = "tts";
    public static final String SHOW_TIME_WAKEUP = "wakeup";
    private int cardType;
    private String duiWidget;
    private String hitText;
    private String hitTips;
    private boolean isHit;
    private String msgId;
    private String refMsgId;
    private List<ButtonBean> relateList;
    private String relateText;
    private String showStage;
    private String showType;
    private String subType;
    private String type;
    private String updateType;

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

    public String getShowStage() {
        return this.showStage;
    }

    public void setShowStage(String showStage) {
        this.showStage = showStage;
    }

    public String getDuiWidget() {
        return this.duiWidget;
    }

    public void setDuiWidget(String duiWidget) {
        this.duiWidget = duiWidget;
    }

    public String getShowType() {
        return this.showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelateText() {
        return this.relateText;
    }

    public void setRelateText(String relateText) {
        this.relateText = relateText;
    }

    public List<ButtonBean> getRelateList() {
        return this.relateList;
    }

    public void setRelateList(List<ButtonBean> relateList) {
        this.relateList = relateList;
    }

    public boolean isHit() {
        return this.isHit;
    }

    public void setHit(boolean hit) {
        this.isHit = hit;
    }

    public String getHitTips() {
        return this.hitTips;
    }

    public void setHitTips(String hitTips) {
        this.hitTips = hitTips;
    }

    public String getHitText() {
        return this.hitText;
    }

    public void setHitText(String hitText) {
        this.hitText = hitText;
    }

    public int getCardType() {
        return this.cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getUpdateType() {
        return this.updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public String toString() {
        return "RecommendBean{relateText='" + this.relateText + "', relateList=" + this.relateList + ", showStage=" + this.showStage + ", showType=" + this.showType + ", isHit=" + this.isHit + ", hitTips=" + this.hitTips + ", cardType=" + this.cardType + '}';
    }
}
