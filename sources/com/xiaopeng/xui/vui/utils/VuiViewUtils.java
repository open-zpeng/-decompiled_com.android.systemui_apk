package com.xiaopeng.xui.vui.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.RestrictTo;
import com.xiaopeng.vui.commons.IVuiElementChangedListener;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.VuiUpdateType;
import com.xiaopeng.xui.widget.XButton;
import com.xiaopeng.xui.widget.XCheckBox;
import com.xiaopeng.xui.widget.XEditText;
import com.xiaopeng.xui.widget.XGroupHeader;
import com.xiaopeng.xui.widget.XImageButton;
import com.xiaopeng.xui.widget.XImageView;
import com.xiaopeng.xui.widget.XProgressBar;
import com.xiaopeng.xui.widget.XRadioButton;
import com.xiaopeng.xui.widget.XRadioGroup;
import com.xiaopeng.xui.widget.XRecyclerView;
import com.xiaopeng.xui.widget.XScrollView;
import com.xiaopeng.xui.widget.XSeekBar;
import com.xiaopeng.xui.widget.XSegmented;
import com.xiaopeng.xui.widget.XSwitch;
import com.xiaopeng.xui.widget.XTabLayout;
import com.xiaopeng.xui.widget.XTextView;
import com.xiaopeng.xui.widget.XViewPager;
import com.xiaopeng.xui.widget.slider.XSlider;
import com.xiaopeng.xui.widget.timepicker.XTimePicker;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes25.dex */
public class VuiViewUtils {
    static Handler mHandler;
    static HandlerThread mThread = new HandlerThread("VuiUpdate");

    public static VuiElementType getElementType(Object view) {
        if (view instanceof XImageView) {
            return VuiElementType.IMAGEVIEW;
        }
        if (view instanceof XImageButton) {
            return VuiElementType.IMAGEBUTTON;
        }
        if (view instanceof XButton) {
            return VuiElementType.BUTTON;
        }
        if (view instanceof XTextView) {
            return VuiElementType.TEXTVIEW;
        }
        if (view instanceof XRadioButton) {
            return VuiElementType.RADIOBUTTON;
        }
        if (view instanceof XCheckBox) {
            return VuiElementType.CHECKBOX;
        }
        if (view instanceof XSwitch) {
            return VuiElementType.SWITCH;
        }
        if (view instanceof XRecyclerView) {
            return VuiElementType.RECYCLEVIEW;
        }
        if (view instanceof XProgressBar) {
            return VuiElementType.PROGRESSBAR;
        }
        if (view instanceof XScrollView) {
            return VuiElementType.SCROLLVIEW;
        }
        if (view instanceof XSlider) {
            return VuiElementType.XSLIDER;
        }
        if (view instanceof XTabLayout) {
            return VuiElementType.XTABLAYOUT;
        }
        if (view instanceof XSegmented) {
            return VuiElementType.XTABLAYOUT;
        }
        if (view instanceof XRadioGroup) {
            return VuiElementType.RADIOGROUP;
        }
        if (view instanceof XEditText) {
            return VuiElementType.EDITTEXT;
        }
        if (view instanceof XGroupHeader) {
            return VuiElementType.XGROUPHEADER;
        }
        if (view instanceof XSeekBar) {
            return VuiElementType.SEEKBAR;
        }
        if (view instanceof XTimePicker) {
            return VuiElementType.TIMEPICKER;
        }
        if (view instanceof XViewPager) {
            return VuiElementType.VIEWPAGER;
        }
        if (view instanceof ViewGroup) {
            return VuiElementType.GROUP;
        }
        return VuiElementType.UNKNOWN;
    }

    public static void updateVui(final IVuiElementChangedListener listener, final View view, final VuiUpdateType type) {
        try {
            if (mHandler == null) {
                mThread.start();
                mHandler = new Handler(mThread.getLooper());
            }
            if (mHandler != null) {
                mHandler.post(new Runnable() { // from class: com.xiaopeng.xui.vui.utils.VuiViewUtils.1
                    @Override // java.lang.Runnable
                    public void run() {
                        View view2;
                        IVuiElementChangedListener iVuiElementChangedListener = IVuiElementChangedListener.this;
                        if (iVuiElementChangedListener != null && (view2 = view) != null) {
                            iVuiElementChangedListener.onVuiElementChaned(view2, type);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
