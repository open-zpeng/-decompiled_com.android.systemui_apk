package com.xiaopeng.speech.vui.model;

import com.xiaopeng.vui.commons.VuiFeedbackType;
/* loaded from: classes.dex */
public class VuiFeedback {
    public VuiFeedbackCode code;
    public String content;
    private VuiFeedbackType feedbackType;
    public String resourceName;
    public int state;

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    public VuiFeedbackCode getCode() {
        return this.code;
    }

    public String getContent() {
        return this.content;
    }

    public void setCode(VuiFeedbackCode code) {
        this.code = code;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setFeedbackType(VuiFeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public VuiFeedbackType getFeedbackType() {
        return this.feedbackType;
    }

    public String toString() {
        return "VuiFeedback{code=" + this.code.getFeedbackCode() + ", content='" + this.content + "', resourceName='" + this.resourceName + "', state=" + this.state + ", feedbackType=" + this.feedbackType.getType() + '}';
    }

    private VuiFeedback(Builder builder) {
        this.code = VuiFeedbackCode.SUCCESS;
        this.feedbackType = VuiFeedbackType.TTS;
        this.state = builder.state;
        this.content = builder.content;
        this.code = builder.code;
        this.feedbackType = builder.type;
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private String content;
        private int state = -1;
        private VuiFeedbackCode code = VuiFeedbackCode.SUCCESS;
        private VuiFeedbackType type = VuiFeedbackType.TTS;

        public Builder state(int state) {
            this.state = state;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder code(VuiFeedbackCode code) {
            this.code = code;
            return this;
        }

        public Builder type(VuiFeedbackType type) {
            this.type = type;
            return this;
        }

        public VuiFeedback build() {
            return new VuiFeedback(this);
        }
    }
}
