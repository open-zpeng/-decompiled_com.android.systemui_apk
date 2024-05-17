package com.xiaopeng.speech.vui.model;

import com.xiaopeng.speech.vui.VuiSceneManager;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.util.List;
/* loaded from: classes.dex */
public class VuiScene {
    private String appVersion;
    private List<VuiElement> elements;
    private String packageName;
    private String sceneId;
    private long timestamp;
    private String vuiVersion;

    public String getVuiVersion() {
        return this.vuiVersion;
    }

    public void setVuiVersion(String vuiVersion) {
        this.vuiVersion = vuiVersion;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public long getTimeStamp() {
        return this.timestamp;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<VuiElement> getElements() {
        return this.elements;
    }

    public String getSceneId() {
        return this.sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public void setElements(List<VuiElement> elements) {
        this.elements = elements;
    }

    public void setVersion(String version) {
        this.appVersion = version;
    }

    public String getVersion() {
        return this.appVersion;
    }

    public String toString() {
        return "VuiScene{sceneId='" + this.sceneId + "', elements=" + this.elements + ", packageName='" + this.packageName + "', appVersion='" + this.appVersion + "', timestamp=" + this.timestamp + '}';
    }

    private VuiScene(Builder builder) {
        this.sceneId = null;
        this.elements = null;
        this.packageName = null;
        this.appVersion = null;
        this.vuiVersion = "2.0";
        this.sceneId = builder.sceneId;
        this.packageName = builder.packageName;
        this.appVersion = builder.appVersion;
        this.timestamp = builder.timestamp;
    }

    public VuiScene() {
        this.sceneId = null;
        this.elements = null;
        this.packageName = null;
        this.appVersion = null;
        this.vuiVersion = "2.0";
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private long timestamp;
        private String sceneId = null;
        private String packageName = null;
        private String appVersion = null;

        public Builder sceneId(String id) {
            this.sceneId = id;
            return this;
        }

        public Builder packageName(String name) {
            this.packageName = name;
            return this;
        }

        public Builder appVersion(String version) {
            this.appVersion = version;
            return this;
        }

        public Builder timestamp(long timeStamp) {
            this.timestamp = timeStamp;
            return this;
        }

        public VuiScene build() {
            return new VuiScene(this);
        }
    }

    public void publish() {
        VuiSceneManager.instance().buildScene(this, true, true);
    }
}
