package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import androidx.appcompat.widget.AppCompatSeekBar;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.vui.commons.model.VuiEvent;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.vui.VuiView;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
/* loaded from: classes25.dex */
public class XSeekBar extends AppCompatSeekBar implements VuiView, IVuiElementListener {
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
    protected XViewDelegate mXViewDelegate;

    public XSeekBar(Context context) {
        super(context);
        init();
    }

    public XSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mXViewDelegate = XViewDelegate.create(this, attrs);
        init();
        initVui(this, attrs);
    }

    public XSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mXViewDelegate = XViewDelegate.create(this, attrs, defStyleAttr, 0);
        init();
        initVui(this, attrs);
    }

    private void init() {
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListenerProxy = new SeekBar.OnSeekBarChangeListener() { // from class: com.xiaopeng.xui.widget.XSeekBar.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (XSeekBar.this.mOnSeekBarChangeListener != null) {
                    XSeekBar.this.mOnSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
                }
                XSeekBar xSeekBar = XSeekBar.this;
                xSeekBar.updateVui(xSeekBar);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (XSeekBar.this.mOnSeekBarChangeListener != null) {
                    XSeekBar.this.mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (XSeekBar.this.mOnSeekBarChangeListener != null) {
                    XSeekBar.this.mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }
        };
        super.setOnSeekBarChangeListener(onSeekBarChangeListenerProxy);
    }

    @Override // android.widget.SeekBar
    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener l) {
        this.mOnSeekBarChangeListener = l;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onConfigurationChanged(newConfig);
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onAttachedToWindow();
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onDetachedFromWindow();
        }
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        setVuiVisibility(this, visibility);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        releaseVui();
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public VuiElement onBuildVuiElement(String s, IVuiElementBuilder iVuiElementBuilder) {
        return null;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public boolean onVuiElementEvent(final View view, VuiEvent vuiEvent) {
        final Double value;
        logD("SeekBar onVuiElementEvent");
        if (view == null || (value = (Double) vuiEvent.getEventValue(vuiEvent)) == null) {
            return false;
        }
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XSeekBar$TblhEJ8EWsLpzlro7gSK773Zaig
            @Override // java.lang.Runnable
            public final void run() {
                XSeekBar.this.lambda$onVuiElementEvent$0$XSeekBar(value, view);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$onVuiElementEvent$0$XSeekBar(Double value, View view) {
        if (value.doubleValue() >= 0.0d && value.doubleValue() <= 100.0d) {
            setProgress(value.intValue());
        }
        int offsetX = (int) ((((getProgress() * 1.0f) / getMax()) - 0.5d) * getWidth());
        VuiFloatingLayerManager.show(view, offsetX, 0);
    }
}
