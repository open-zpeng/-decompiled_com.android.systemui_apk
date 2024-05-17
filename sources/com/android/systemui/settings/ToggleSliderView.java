package com.android.systemui.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.settingslib.RestrictedLockUtils;
import com.android.systemui.R;
import com.android.systemui.settings.ToggleSlider;
import com.android.systemui.statusbar.policy.BrightnessMirrorController;
/* loaded from: classes21.dex */
public class ToggleSliderView extends RelativeLayout implements ToggleSlider {
    private final CompoundButton.OnCheckedChangeListener mCheckListener;
    private TextView mLabel;
    private ToggleSlider.Listener mListener;
    private ToggleSliderView mMirror;
    private BrightnessMirrorController mMirrorController;
    private final SeekBar.OnSeekBarChangeListener mSeekListener;
    private ToggleSeekBar mSlider;
    private CompoundButton mToggle;
    private boolean mTracking;

    public ToggleSliderView(Context context) {
        this(context, null);
    }

    public ToggleSliderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleSliderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCheckListener = new CompoundButton.OnCheckedChangeListener() { // from class: com.android.systemui.settings.ToggleSliderView.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton toggle, boolean checked) {
                ToggleSliderView.this.mSlider.setEnabled(!checked);
                if (ToggleSliderView.this.mListener != null) {
                    ToggleSlider.Listener listener = ToggleSliderView.this.mListener;
                    ToggleSliderView toggleSliderView = ToggleSliderView.this;
                    listener.onChanged(toggleSliderView, toggleSliderView.mTracking, checked, ToggleSliderView.this.mSlider.getProgress(), false);
                }
                if (ToggleSliderView.this.mMirror != null) {
                    ToggleSliderView.this.mMirror.mToggle.setChecked(checked);
                }
            }
        };
        this.mSeekListener = new SeekBar.OnSeekBarChangeListener() { // from class: com.android.systemui.settings.ToggleSliderView.2
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (ToggleSliderView.this.mListener != null) {
                    ToggleSlider.Listener listener = ToggleSliderView.this.mListener;
                    ToggleSliderView toggleSliderView = ToggleSliderView.this;
                    listener.onChanged(toggleSliderView, toggleSliderView.mTracking, ToggleSliderView.this.mToggle.isChecked(), progress, false);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
                ToggleSliderView.this.mTracking = true;
                if (ToggleSliderView.this.mListener != null) {
                    ToggleSlider.Listener listener = ToggleSliderView.this.mListener;
                    ToggleSliderView toggleSliderView = ToggleSliderView.this;
                    listener.onChanged(toggleSliderView, toggleSliderView.mTracking, ToggleSliderView.this.mToggle.isChecked(), ToggleSliderView.this.mSlider.getProgress(), false);
                }
                ToggleSliderView.this.mToggle.setChecked(false);
                if (ToggleSliderView.this.mMirrorController != null) {
                    ToggleSliderView.this.mMirrorController.showMirror();
                    ToggleSliderView.this.mMirrorController.setLocation((View) ToggleSliderView.this.getParent());
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                ToggleSliderView.this.mTracking = false;
                if (ToggleSliderView.this.mListener != null) {
                    ToggleSlider.Listener listener = ToggleSliderView.this.mListener;
                    ToggleSliderView toggleSliderView = ToggleSliderView.this;
                    listener.onChanged(toggleSliderView, toggleSliderView.mTracking, ToggleSliderView.this.mToggle.isChecked(), ToggleSliderView.this.mSlider.getProgress(), true);
                }
                if (ToggleSliderView.this.mMirrorController != null) {
                    ToggleSliderView.this.mMirrorController.hideMirror();
                }
            }
        };
        View.inflate(context, R.layout.status_bar_toggle_slider, this);
        context.getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ToggleSliderView, defStyle, 0);
        this.mToggle = (CompoundButton) findViewById(R.id.toggle);
        this.mToggle.setOnCheckedChangeListener(this.mCheckListener);
        this.mSlider = (ToggleSeekBar) findViewById(R.id.slider);
        this.mSlider.setOnSeekBarChangeListener(this.mSeekListener);
        this.mLabel = (TextView) findViewById(R.id.label);
        this.mLabel.setText(a.getString(R.styleable.ToggleSliderView_text));
        this.mSlider.setAccessibilityLabel(getContentDescription().toString());
        a.recycle();
    }

    public void setMirror(ToggleSliderView toggleSlider) {
        this.mMirror = toggleSlider;
        ToggleSliderView toggleSliderView = this.mMirror;
        if (toggleSliderView != null) {
            toggleSliderView.setChecked(this.mToggle.isChecked());
            this.mMirror.setMax(this.mSlider.getMax());
            this.mMirror.setValue(this.mSlider.getProgress());
        }
    }

    public void setMirrorController(BrightnessMirrorController c) {
        this.mMirrorController = c;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ToggleSlider.Listener listener = this.mListener;
        if (listener != null) {
            listener.onInit(this);
        }
    }

    public void setEnforcedAdmin(RestrictedLockUtils.EnforcedAdmin admin) {
        this.mToggle.setEnabled(admin == null);
        this.mSlider.setEnabled(admin == null);
        this.mSlider.setEnforcedAdmin(admin);
    }

    @Override // com.android.systemui.settings.ToggleSlider
    public void setOnChangedListener(ToggleSlider.Listener l) {
        this.mListener = l;
    }

    @Override // com.android.systemui.settings.ToggleSlider
    public void setChecked(boolean checked) {
        this.mToggle.setChecked(checked);
    }

    @Override // com.android.systemui.settings.ToggleSlider
    public boolean isChecked() {
        return this.mToggle.isChecked();
    }

    @Override // com.android.systemui.settings.ToggleSlider
    public void setMax(int max) {
        this.mSlider.setMax(max);
        ToggleSliderView toggleSliderView = this.mMirror;
        if (toggleSliderView != null) {
            toggleSliderView.setMax(max);
        }
    }

    @Override // com.android.systemui.settings.ToggleSlider
    public void setValue(int value) {
        this.mSlider.setProgress(value);
        ToggleSliderView toggleSliderView = this.mMirror;
        if (toggleSliderView != null) {
            toggleSliderView.setValue(value);
        }
    }

    @Override // com.android.systemui.settings.ToggleSlider
    public int getValue() {
        return this.mSlider.getProgress();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this.mMirror != null) {
            MotionEvent copy = ev.copy();
            this.mMirror.dispatchTouchEvent(copy);
            copy.recycle();
        }
        return super.dispatchTouchEvent(ev);
    }
}
