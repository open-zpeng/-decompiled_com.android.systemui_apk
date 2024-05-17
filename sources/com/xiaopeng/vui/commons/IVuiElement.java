package com.xiaopeng.vui.commons;

import android.view.View;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public interface IVuiElement {
    default boolean isVuiLayoutLoadable() {
        return false;
    }

    default void setVuiLayoutLoadable(boolean vuiLayoutLoadable) {
    }

    default VuiPriority getVuiPriority() {
        return VuiPriority.LEVEL3;
    }

    default void setVuiPriority(VuiPriority vuiPriority) {
    }

    default String getVuiAction() {
        return "";
    }

    default void setVuiAction(String vuiAction) {
    }

    default VuiElementType getVuiElementType() {
        return VuiElementType.UNKNOWN;
    }

    default void setVuiElementType(VuiElementType vuiElementType) {
    }

    default String getVuiFatherElementId() {
        return "-1";
    }

    default void setVuiFatherElementId(String vuiFatherElementId) {
    }

    default String getVuiFatherLabel() {
        return "";
    }

    default void setVuiFatherLabel(String vuiFatherLabel) {
    }

    default String getVuiLabel() {
        return null;
    }

    default void setVuiLabel(String vuiLabel) {
    }

    default String getVuiElementId() {
        return "";
    }

    default void setVuiElementId(String vuiElementId) {
    }

    default void setVuiPosition(int vuiPosition) {
    }

    default int getVuiPosition() {
        return -1;
    }

    default VuiFeedbackType getVuiFeedbackType() {
        return VuiFeedbackType.TTS;
    }

    default void setVuiFeedbackType(VuiFeedbackType vuiFeedbackType) {
    }

    default boolean isPerformVuiAction() {
        return false;
    }

    default void setPerformVuiAction(boolean performVuiAction) {
    }

    default void setVuiProps(JSONObject vuiProps) {
    }

    default JSONObject getVuiProps() {
        return null;
    }

    default VuiMode getVuiMode() {
        return VuiMode.NORMAL;
    }

    default void setVuiMode(VuiMode vuiMode) {
    }

    default void setVuiBizId(String vuiBizId) {
    }

    default String getVuiBizId() {
        return null;
    }

    default void setVuiDisableHitEffect(boolean vuiDisableHitEffect) {
    }

    default boolean getVuiDisableHitEffect() {
        return false;
    }

    default void enableViewVuiMode(boolean b) {
    }

    default boolean isVuiModeEnabled() {
        return false;
    }

    default void setVuiElementChangedListener(IVuiElementChangedListener listener) {
    }

    default IVuiElementChangedListener getVuiElementChangedListener() {
        return null;
    }

    default void setVuiValue(Object obj) {
    }

    default Object getVuiValue() {
        return null;
    }

    default void setVuiValue(Object obj, View view) {
    }
}
