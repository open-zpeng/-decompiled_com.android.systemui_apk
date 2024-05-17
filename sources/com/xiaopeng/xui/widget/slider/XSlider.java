package com.xiaopeng.xui.widget.slider;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.vui.commons.model.VuiEvent;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes25.dex */
public class XSlider extends AbsSlider {
    private ProgressChangeListener progressChangeListener;
    private SliderProgressListener sliderProgressListener;

    /* loaded from: classes25.dex */
    public interface ProgressChangeListener {
        void onProgressChanged(XSlider xSlider, float f, String str, boolean z);
    }

    /* loaded from: classes25.dex */
    public interface SliderProgressListener {
        void onProgressChanged(XSlider xSlider, float f, String str);

        void onStartTrackingTouch(XSlider xSlider);

        void onStopTrackingTouch(XSlider xSlider);
    }

    public XSlider(Context context) {
        this(context, null);
    }

    public XSlider(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            int action = event.getAction();
            if (action != 0) {
                if (action == 1) {
                    if (this.mIsDragging) {
                        this.mIsDragging = false;
                    } else {
                        SliderProgressListener sliderProgressListener = this.sliderProgressListener;
                        if (sliderProgressListener != null) {
                            sliderProgressListener.onStartTrackingTouch(this);
                        }
                    }
                    this.indicatorX = event.getX();
                    stickIndicator();
                    notifyChildren(true, true);
                    getParent().requestDisallowInterceptTouchEvent(false);
                    SliderProgressListener sliderProgressListener2 = this.sliderProgressListener;
                    if (sliderProgressListener2 != null) {
                        sliderProgressListener2.onStopTrackingTouch(this);
                    }
                    invalidateAll();
                } else if (action != 2) {
                    if (action == 3) {
                        if (this.mIsDragging) {
                            this.mIsDragging = false;
                        }
                        invalidateAll();
                    }
                } else if (this.mIsDragging) {
                    this.indicatorX = event.getX();
                    notifyChildren(true, false);
                    invalidateAll();
                } else {
                    float x = event.getX();
                    if (Math.abs(x - this.mTouchDownX) > this.mScaledTouchSlop) {
                        this.mIsDragging = true;
                        SliderProgressListener sliderProgressListener3 = this.sliderProgressListener;
                        if (sliderProgressListener3 != null) {
                            sliderProgressListener3.onStartTrackingTouch(this);
                        }
                        this.indicatorX = event.getX();
                        getParent().requestDisallowInterceptTouchEvent(true);
                        notifyChildren(true, false);
                        invalidateAll();
                    }
                }
            } else if (isInScrollContainer()) {
                this.mTouchDownX = event.getX();
            } else {
                this.mIsDragging = true;
                getParent().requestDisallowInterceptTouchEvent(true);
                SliderProgressListener sliderProgressListener4 = this.sliderProgressListener;
                if (sliderProgressListener4 != null) {
                    sliderProgressListener4.onStartTrackingTouch(this);
                }
                this.indicatorX = event.getX();
                notifyChildren(true, false);
                invalidateAll();
            }
            return true;
        }
        return true;
    }

    private void notifyChildren(boolean isNeedUpdate, boolean isForce) {
        float xLocation = filterValidValue();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            SlideLineView child = (SlideLineView) getChildAt(i);
            if (child.getX() + (child.getWidth() / 2) <= filterValidValue()) {
                if (!child.isSelect()) {
                    child.setSelect(true);
                }
            } else {
                child.setSelect(false);
            }
        }
        if (isNeedUpdate) {
            this.indicatorValue = ((xLocation - INDICATOR_MARGIN) / this.workableTotalWidth) * (this.endIndex - this.startIndex);
            if (this.sliderProgressListener != null) {
                if (isForce || this.indicatorValue + this.startIndex >= this.currentUpdateIndex + this.accuracy || this.indicatorValue + this.startIndex <= this.currentUpdateIndex - this.accuracy || this.currentUpdateIndex == 0.0f) {
                    this.sliderProgressListener.onProgressChanged(this, this.indicatorValue + this.startIndex, this.unit);
                    this.indicatorValue = ((xLocation - INDICATOR_MARGIN) / this.workableTotalWidth) * (this.endIndex - this.startIndex);
                    this.currentUpdateIndex = ((int) this.indicatorValue) + this.startIndex;
                    updateVui(this);
                }
            }
        }
    }

    @Override // com.xiaopeng.xui.widget.slider.AbsSlider
    public float getIndicatorValue() {
        return (this.indicatorValue + this.startIndex) * this.mStep;
    }

    @Override // com.xiaopeng.xui.widget.slider.AbsSlider
    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    @Override // com.xiaopeng.xui.widget.slider.AbsSlider
    public float getIndicatorLocationX() {
        return this.indicatorX;
    }

    public void setSliderProgressListener(SliderProgressListener sliderProgressListener) {
        this.sliderProgressListener = sliderProgressListener;
    }

    public void setProgressChangeListener(ProgressChangeListener progressChangeListener) {
        this.progressChangeListener = progressChangeListener;
    }

    public void setCurrentIndex(int currentIndex) {
        setCurrentIndex(currentIndex, false);
    }

    public void setCurrentIndex(final int currentIndex, final boolean fromUser) {
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.slider.-$$Lambda$XSlider$6nRMnQGFhZSAnHXSTyWZZ5qLEF4
            @Override // java.lang.Runnable
            public final void run() {
                XSlider.this.lambda$setCurrentIndex$0$XSlider(currentIndex, fromUser);
            }
        });
    }

    public /* synthetic */ void lambda$setCurrentIndex$0$XSlider(int currentIndex, boolean fromUser) {
        ProgressChangeListener progressChangeListener;
        this.indicatorX = (((currentIndex - this.startIndex) / (this.endIndex - this.startIndex)) * this.workableTotalWidth) + INDICATOR_MARGIN;
        this.indicatorValue = currentIndex - this.startIndex;
        invalidate();
        notifyChildren(false, false);
        if (!this.hidePop) {
            this.indicatorDrawable.updateCenter(filterValidValue(), getPopString(), this.isNight, getWidth());
        }
        if (fromUser && (progressChangeListener = this.progressChangeListener) != null) {
            progressChangeListener.onProgressChanged(this, this.indicatorValue + this.startIndex, this.unit, true);
        }
        if (getVuiValue() != null && ((Float) getVuiValue()).floatValue() == getIndicatorValue()) {
            return;
        }
        updateVui(this);
    }

    @Override // android.view.View
    public void setEnabled(boolean enable) {
        if (!enable) {
            this.mIsDragging = false;
        }
        super.setEnabled(enable);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setEnabled(enable);
        }
        setAlphaByEnable(enable);
        invalidate();
    }

    public void setStartIndex(int startIndex) {
        if (startIndex == this.endIndex) {
            throw new RuntimeException("startIndex = endIndex!!!");
        }
        this.startIndex = startIndex;
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.slider.-$$Lambda$XSlider$Xz60DHZ7J60ywvILqd--vaYYV3k
            @Override // java.lang.Runnable
            public final void run() {
                XSlider.this.lambda$setStartIndex$1$XSlider();
            }
        });
    }

    public /* synthetic */ void lambda$setStartIndex$1$XSlider() {
        if (!this.hidePop) {
            this.indicatorDrawable.updateCenter(filterValidValue(), getPopString(), this.isNight, getWidth());
        }
        invalidate();
    }

    public void setEndIndex(int endIndex) {
        if (this.startIndex == endIndex) {
            throw new RuntimeException("startIndex = endIndex!!!");
        }
        this.endIndex = endIndex;
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.slider.-$$Lambda$XSlider$kB2KeMUVNgZs-Tjtgxuk4WuttL4
            @Override // java.lang.Runnable
            public final void run() {
                XSlider.this.lambda$setEndIndex$2$XSlider();
            }
        });
    }

    public /* synthetic */ void lambda$setEndIndex$2$XSlider() {
        invalidate();
    }

    public void setInitIndex(int initIndex) {
        if (initIndex > this.endIndex) {
            this.initIndex = this.endIndex;
        } else if (initIndex < this.startIndex) {
            this.initIndex = this.startIndex;
        } else {
            this.initIndex = initIndex;
            this.indicatorValue = initIndex - this.startIndex;
            invalidate();
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public VuiElement onBuildVuiElement(String s, IVuiElementBuilder iVuiElementBuilder) {
        try {
            setVuiValue(Float.valueOf(getIndicatorValue()));
            if (getVuiProps() != null && getVuiProps().has(VuiConstants.PROPS_SETPROPS)) {
                boolean customSet = getVuiProps().getBoolean(VuiConstants.PROPS_SETPROPS);
                if (customSet) {
                    return null;
                }
            }
            JSONObject jsonObject = getVuiProps();
            if (jsonObject == null) {
                jsonObject = new JSONObject();
            }
            jsonObject.put(VuiConstants.PROPS_MINVALUE, this.startIndex);
            jsonObject.put(VuiConstants.PROPS_MAXVALUE, this.endIndex);
            jsonObject.put(VuiConstants.PROPS_INTERVAL, (int) Math.ceil((this.endIndex - this.startIndex) / 10.0d));
            setVuiProps(jsonObject);
        } catch (JSONException e) {
        }
        return null;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public boolean onVuiElementEvent(final View view, VuiEvent vuiEvent) {
        Double value;
        int index;
        logD("slider onVuiElementEvent");
        if (view == null || (value = (Double) vuiEvent.getEventValue(vuiEvent)) == null) {
            return false;
        }
        if (this.mStep == 1) {
            index = (int) Math.ceil(value.doubleValue());
        } else {
            index = (int) Math.round(value.doubleValue() / this.mStep);
        }
        if (index < this.startIndex || index > this.endIndex) {
            return true;
        }
        setCurrentIndex(index, true);
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.slider.-$$Lambda$XSlider$8q6TCB-9AxkZNaxjbRsq1Ki1ixY
            @Override // java.lang.Runnable
            public final void run() {
                XSlider.this.lambda$onVuiElementEvent$3$XSlider(view);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$onVuiElementEvent$3$XSlider(View view) {
        int offsetY = (int) ((getHeightExIndicator() / 2.0f) - (getHeight() / 2));
        int offsetX = ((int) getIndicatorLocationX()) - (getWidth() / 2);
        VuiFloatingLayerManager.show(view, offsetX, offsetY);
    }
}
