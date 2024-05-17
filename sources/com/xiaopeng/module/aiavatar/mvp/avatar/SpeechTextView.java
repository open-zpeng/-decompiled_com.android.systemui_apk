package com.xiaopeng.module.aiavatar.mvp.avatar;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;
import com.xiaopeng.module.aiavatar.R;
/* loaded from: classes23.dex */
public class SpeechTextView extends TextView {
    public SpeechTextView(Context context) {
        super(context);
    }

    public SpeechTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpeechTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SpeechTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Drawable bg = getResources().getDrawable(R.drawable.avatar_speech_text_bg, null);
        int textColor = getResources().getColor(R.color.avatar_speech_text_color);
        setBackground(bg);
        setTextColor(textColor);
    }

    public void show() {
        post(new Runnable() { // from class: com.xiaopeng.module.aiavatar.mvp.avatar.SpeechTextView.1
            @Override // java.lang.Runnable
            public void run() {
                SpeechTextView.this.setVisibility(0);
                SpeechTextView.this.animate().alpha(1.0f).setDuration(200L).start();
            }
        });
    }

    public void hide() {
        post(new Runnable() { // from class: com.xiaopeng.module.aiavatar.mvp.avatar.SpeechTextView.2
            @Override // java.lang.Runnable
            public void run() {
                SpeechTextView.this.animate().alpha(0.0f).setDuration(200L).withEndAction(new Runnable() { // from class: com.xiaopeng.module.aiavatar.mvp.avatar.SpeechTextView.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        SpeechTextView.this.setVisibility(8);
                    }
                }).start();
            }
        });
    }

    @Override // android.view.View
    public void setBackground(Drawable background) {
        super.setBackground(background);
        setPadding(getPaddingLeft(), 90, getPaddingRight(), 90);
    }
}
