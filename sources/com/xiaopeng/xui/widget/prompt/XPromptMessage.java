package com.xiaopeng.xui.widget.prompt;

import androidx.annotation.DrawableRes;
/* loaded from: classes25.dex */
public class XPromptMessage {
    private CharSequence mButton;
    private int mDuration;
    private int mIcon;
    private String mId;
    private CharSequence mText;

    public XPromptMessage(CharSequence text) {
        this.mText = text;
    }

    public XPromptMessage(int duration, CharSequence text) {
        this.mDuration = duration;
        this.mText = text;
    }

    public XPromptMessage(int duration, CharSequence text, int icon, String id) {
        this.mDuration = duration;
        this.mText = text;
        this.mIcon = icon;
        this.mId = id;
    }

    public XPromptMessage(int duration, CharSequence text, CharSequence button, String id) {
        this.mDuration = duration;
        this.mText = text;
        this.mButton = button;
        this.mId = id;
    }

    public XPromptMessage setDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    public XPromptMessage setText(CharSequence text) {
        this.mText = text;
        return this;
    }

    public XPromptMessage setId(String id) {
        this.mId = id;
        return this;
    }

    public int getIcon() {
        return this.mIcon;
    }

    public XPromptMessage setIcon(@DrawableRes int icon) {
        this.mIcon = icon;
        return this;
    }

    public int getDuration() {
        return this.mDuration;
    }

    public CharSequence getText() {
        return this.mText;
    }

    public String getId() {
        return this.mId;
    }

    public CharSequence getButton() {
        return this.mButton;
    }

    public void setButton(CharSequence button) {
        this.mButton = button;
    }
}
