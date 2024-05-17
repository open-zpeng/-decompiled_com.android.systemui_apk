package com.xiaopeng.speech.vui.event;

import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import com.google.gson.internal.LinkedTreeMap;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.util.Map;
/* loaded from: classes.dex */
public class SetCheckEvent extends BaseEvent {
    @Override // com.xiaopeng.speech.vui.event.IVuiEvent
    public <T extends View> T run(T view, VuiElement vuiElement) {
        Boolean isCheck;
        if (view == null) {
            return null;
        }
        if (vuiElement != null && vuiElement.getResultActions() != null && !vuiElement.getResultActions().isEmpty() && (isCheck = (Boolean) getSetCheck(vuiElement)) != null) {
            if (view instanceof CompoundButton) {
                if ((!isCheck.booleanValue()) == ((CompoundButton) view).isChecked()) {
                    LogUtils.d("SetCheckEvent run on CompoundButton");
                    if (view instanceof IVuiElement) {
                        ((IVuiElement) view).setPerformVuiAction(true);
                    }
                    ((CompoundButton) view).setChecked(isCheck.booleanValue());
                    if (view instanceof IVuiElement) {
                        ((IVuiElement) view).setPerformVuiAction(false);
                    }
                }
            } else if (view instanceof Checkable) {
                if ((!isCheck.booleanValue()) == ((Checkable) view).isChecked()) {
                    LogUtils.d("SetCheckEvent run on Checkable view");
                    if (view instanceof IVuiElement) {
                        ((IVuiElement) view).setPerformVuiAction(true);
                    }
                    view.performClick();
                    if (view instanceof IVuiElement) {
                        ((IVuiElement) view).setPerformVuiAction(false);
                    }
                }
            } else if ((!isCheck.booleanValue()) == view.isSelected()) {
                LogUtils.d("SetCheckEvent run on setSelected view");
                if (view instanceof IVuiElement) {
                    ((IVuiElement) view).setPerformVuiAction(true);
                }
                view.performClick();
                if (view instanceof IVuiElement) {
                    ((IVuiElement) view).setPerformVuiAction(false);
                }
            }
        }
        return view;
    }

    private <T> T getSetCheck(VuiElement vuiElement) {
        Map<String, Object> value;
        if (vuiElement == null || vuiElement.getResultActions() == null || vuiElement.getResultActions().isEmpty()) {
            return null;
        }
        String key = vuiElement.getResultActions().get(0);
        if (!(vuiElement.getValues() instanceof LinkedTreeMap) || (value = (Map) vuiElement.getValues()) == null) {
            return null;
        }
        if (value.get(key) instanceof LinkedTreeMap) {
            Map<String, Object> insideValue = (Map) value.get(key);
            if (insideValue == null || insideValue.get(VuiConstants.ELEMENT_VALUE) == null) {
                return null;
            }
            return (T) insideValue.get(VuiConstants.ELEMENT_VALUE);
        } else if (value.get(value) == null) {
            return null;
        } else {
            return (T) value.get(VuiConstants.ELEMENT_VALUE);
        }
    }
}
