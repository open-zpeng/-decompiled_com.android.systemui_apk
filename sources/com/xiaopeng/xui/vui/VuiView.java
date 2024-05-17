package com.xiaopeng.xui.vui;

import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import androidx.annotation.UiThread;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.IVuiElementChangedListener;
import com.xiaopeng.vui.commons.VuiAction;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.VuiFeedbackType;
import com.xiaopeng.vui.commons.VuiMode;
import com.xiaopeng.vui.commons.VuiPriority;
import com.xiaopeng.vui.commons.VuiUpdateType;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.utils.XuiUtils;
import com.xiaopeng.xui.vui.utils.VuiCommonUtils;
import com.xiaopeng.xui.vui.utils.VuiViewUtils;
import java.lang.ref.WeakReference;
import org.json.JSONObject;
/* loaded from: classes25.dex */
public interface VuiView extends IVuiElement {
    public static final SparseArray<XAttr> msMap = new SparseArray<>();

    default void initVui(View view, AttributeSet attrs) {
        if (!Xui.isVuiEnable() || view == null || attrs == null) {
            return;
        }
        XAttr xAttr = new XAttr();
        TypedArray ta = view.getContext().obtainStyledAttributes(attrs, R.styleable.vui);
        xAttr.vuiAction = ta.getString(R.styleable.vui_vuiAction);
        xAttr.vuiElementType = VuiCommonUtils.getElementType(ta.getInteger(R.styleable.vui_vuiElementType, -1));
        if (xAttr.vuiElementType == VuiElementType.UNKNOWN) {
            xAttr.vuiElementType = VuiViewUtils.getElementType(view);
        }
        xAttr.vuiPosition = Integer.valueOf(ta.getInteger(R.styleable.vui_vuiPosition, -1));
        xAttr.vuiFatherElementId = ta.getString(R.styleable.vui_vuiFatherElementId);
        xAttr.vuiLabel = ta.getString(R.styleable.vui_vuiLabel);
        xAttr.vuiFatherLabel = ta.getString(R.styleable.vui_vuiFatherLabel);
        xAttr.vuiElementId = ta.getString(R.styleable.vui_vuiElementId);
        xAttr.vuiLayoutLoadable = ta.getBoolean(R.styleable.vui_vuiLayoutLoadable, false);
        xAttr.vuiMode = VuiCommonUtils.getVuiMode(ta.getInteger(R.styleable.vui_vuiMode, 4));
        xAttr.vuiBizId = ta.getString(R.styleable.vui_vuiBizId);
        int priority = ta.getInt(R.styleable.vui_vuiPriority, 2);
        xAttr.vuiPriority = VuiCommonUtils.getViewLeveByPriority(priority);
        xAttr.vuiFeedbackType = VuiCommonUtils.getFeedbackType(ta.getInteger(R.styleable.vui_vuiFeedbackType, 1));
        xAttr.vuiDisableHitEffect = ta.getBoolean(R.styleable.vui_vuiDisableHitEffect, false);
        xAttr.vuiEnableViewVuiMode = ta.getBoolean(R.styleable.vui_vuiEnableViewVuiMode, false);
        ta.recycle();
        xAttr.mVuiVisibility = view.getVisibility();
        xAttr.mVuiSelected = view.isSelected();
        synchronized (msMap) {
            msMap.put(hashCode(), xAttr);
        }
    }

    default XAttr checkVuiExit() {
        XAttr xAttr;
        synchronized (msMap) {
            xAttr = msMap.get(hashCode());
        }
        if (xAttr != null) {
            return xAttr;
        }
        logD("xAttr is null");
        XAttr xAttr2 = new XAttr();
        if (xAttr2.vuiElementType == VuiElementType.UNKNOWN) {
            xAttr2.vuiElementType = VuiViewUtils.getElementType(this);
        }
        synchronized (msMap) {
            msMap.put(hashCode(), xAttr2);
        }
        return xAttr2;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default boolean isVuiLayoutLoadable() {
        XAttr xAttr = checkVuiExit();
        return xAttr.vuiLayoutLoadable;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiLayoutLoadable(boolean vuiLayoutLoadable) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiLayoutLoadable = vuiLayoutLoadable;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default VuiPriority getVuiPriority() {
        XAttr xAttr = checkVuiExit();
        return xAttr.vuiPriority;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiPriority(VuiPriority vuiPriority) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiPriority = vuiPriority;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default String getVuiAction() {
        XAttr xAttr = checkVuiExit();
        return xAttr.vuiAction;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiAction(String vuiAction) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiAction = vuiAction;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default VuiElementType getVuiElementType() {
        XAttr xAttr = checkVuiExit();
        return xAttr.vuiElementType;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiElementType(VuiElementType vuiElementType) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiElementType = vuiElementType;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default String getVuiFatherElementId() {
        XAttr xAttr = checkVuiExit();
        return xAttr.vuiFatherElementId;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiFatherElementId(String vuiFatherElementId) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiFatherElementId = vuiFatherElementId;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default String getVuiFatherLabel() {
        XAttr xAttr = checkVuiExit();
        return xAttr.vuiFatherLabel;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiFatherLabel(String vuiFatherLabel) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiFatherLabel = vuiFatherLabel;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default String getVuiLabel() {
        XAttr xAttr = checkVuiExit();
        return xAttr.vuiLabel;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiLabel(String vuiLabel) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiLabel = vuiLabel;
        if (isVuiLayoutLoadable() && (this instanceof View)) {
            updateVui((View) this);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default String getVuiElementId() {
        XAttr xAttr = checkVuiExit();
        return xAttr.vuiElementId;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiElementId(String vuiElementId) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiElementId = vuiElementId;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiPosition(int vuiPosition) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiPosition = Integer.valueOf(vuiPosition);
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default int getVuiPosition() {
        XAttr xAttr = checkVuiExit();
        return xAttr.vuiPosition.intValue();
    }

    default void releaseVui() {
        synchronized (msMap) {
            msMap.remove(hashCode());
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default VuiFeedbackType getVuiFeedbackType() {
        XAttr xAttr = checkVuiExit();
        return xAttr.vuiFeedbackType;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiFeedbackType(VuiFeedbackType vuiFeedbackType) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiFeedbackType = vuiFeedbackType;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default boolean isPerformVuiAction() {
        XAttr xAttr = checkVuiExit();
        return xAttr.performVuiAction;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setPerformVuiAction(boolean performVuiAction) {
        XAttr xAttr = checkVuiExit();
        xAttr.performVuiAction = performVuiAction;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiProps(JSONObject vuiProps) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiProps = vuiProps;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default JSONObject getVuiProps() {
        XAttr xAttr = checkVuiExit();
        return xAttr.vuiProps;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default VuiMode getVuiMode() {
        XAttr xAttr = checkVuiExit();
        return xAttr.vuiMode;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiMode(VuiMode vuiMode) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiMode = vuiMode;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiBizId(String vuiBizId) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiBizId = vuiBizId;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default String getVuiBizId() {
        XAttr xAttr = checkVuiExit();
        return xAttr.vuiBizId;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiDisableHitEffect(boolean vuiDisableHitEffect) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiDisableHitEffect = vuiDisableHitEffect;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default boolean getVuiDisableHitEffect() {
        XAttr xAttr = checkVuiExit();
        if (!xAttr.vuiDisableHitEffect && (VuiAction.SCROLLBYY.getName().equals(xAttr.vuiAction) || VuiAction.SCROLLBYX.getName().equals(xAttr.vuiAction))) {
            return true;
        }
        return xAttr.vuiDisableHitEffect;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void enableViewVuiMode(boolean b) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiEnableViewVuiMode = b;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default boolean isVuiModeEnabled() {
        XAttr xAttr = checkVuiExit();
        return xAttr.vuiEnableViewVuiMode;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiValue(Object obj) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiValue = obj;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiValue(Object obj, View view) {
        XAttr xAttr = checkVuiExit();
        xAttr.vuiValue = obj;
        if (VuiElementType.STATEFULBUTTON.getType().equals(getVuiElementType().getType()) && view != null) {
            updateVui(view);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default Object getVuiValue() {
        XAttr xAttr = checkVuiExit();
        return xAttr.vuiValue;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default void setVuiElementChangedListener(IVuiElementChangedListener listener) {
        XAttr xAttr = checkVuiExit();
        xAttr.mVuiElementChangedListener = new WeakReference(listener);
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    default IVuiElementChangedListener getVuiElementChangedListener() {
        XAttr xAttr = checkVuiExit();
        if (xAttr.mVuiElementChangedListener != null) {
            synchronized (msMap) {
                if (xAttr.mVuiElementChangedListener == null) {
                    return null;
                }
                return (IVuiElementChangedListener) xAttr.mVuiElementChangedListener.get();
            }
        }
        return null;
    }

    @UiThread
    default void setVuiVisibility(View view, int visibility) {
        XAttr xAttr = checkVuiExit();
        if (xAttr.mVuiVisibility != visibility) {
            if (XLogUtils.isLogLevelEnabled(2)) {
                logD("setVuiVisibility; xAttr.mVuiVisibility : " + XuiUtils.formatVisibility(xAttr.mVuiVisibility) + ",visibility " + XuiUtils.formatVisibility(visibility));
            }
            xAttr.mVuiVisibility = visibility;
            try {
                JSONObject props = getVuiProps();
                if (props != null && props.has(VuiConstants.PROPS_VOICECONTROL)) {
                    boolean canVoiceControl = props.getBoolean(VuiConstants.PROPS_VOICECONTROL);
                    if (canVoiceControl) {
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateVui(view);
        }
    }

    @UiThread
    default void setVuiSelected(View view, boolean selected) {
        XAttr xAttr = checkVuiExit();
        if (xAttr.mVuiSelected == selected) {
            return;
        }
        xAttr.mVuiSelected = selected;
        String type = getVuiElementType().getType();
        if (VuiElementType.CHECKBOX.getType().equals(type) || VuiElementType.SWITCH.getType().equals(type) || VuiElementType.RADIOBUTTON.getType().equals(type)) {
            updateVui(view);
        }
    }

    default void updateVui(View view) {
        updateVui(view, VuiUpdateType.UPDATE_VIEW_ATTRIBUTE);
    }

    default void updateVui(View view, VuiUpdateType updateType) {
        IVuiElementChangedListener listener = getVuiElementChangedListener();
        if (listener != null) {
            VuiViewUtils.updateVui(listener, view, updateType);
        } else if (XLogUtils.isLogLevelEnabled(2)) {
            logD("listener is null");
        }
    }

    default void logD(String msg) {
        XLogUtils.d("xpui", "%s %s hashCode:%s", getClass().getSimpleName(), msg, Integer.valueOf(hashCode()));
    }

    default void logI(String msg) {
        XLogUtils.i("xpui", "%s %s hashCode:%s", getClass().getSimpleName(), msg, Integer.valueOf(hashCode()));
    }

    /* loaded from: classes25.dex */
    public static class XAttr {
        private volatile WeakReference<IVuiElementChangedListener> mVuiElementChangedListener;
        private int mVuiVisibility;
        private boolean performVuiAction;
        private String vuiAction;
        private String vuiBizId;
        private boolean vuiDisableHitEffect;
        private String vuiElementId;
        private String vuiFatherElementId;
        private String vuiFatherLabel;
        private VuiFeedbackType vuiFeedbackType;
        private String vuiLabel;
        private boolean vuiLayoutLoadable;
        private Object vuiValue;
        private boolean mVuiSelected = false;
        private VuiElementType vuiElementType = VuiCommonUtils.getElementType(-1);
        private Integer vuiPosition = -1;
        private VuiMode vuiMode = VuiMode.NORMAL;
        private boolean vuiEnableViewVuiMode = false;
        private VuiPriority vuiPriority = VuiCommonUtils.getViewLeveByPriority(2);
        private JSONObject vuiProps = null;

        XAttr() {
        }
    }
}
