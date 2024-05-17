package com.xiaopeng.speech.vui.utils;

import android.view.View;
import com.xiaopeng.vui.commons.VuiAction;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.VuiPriority;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.util.Map;
/* loaded from: classes.dex */
public class XpVuiUtils {
    public static void setValueAttribute(View view, VuiElement element) {
        VuiUtils.setValueAttribute(view, element);
    }

    public static void addScrollProps(VuiElement element, View view) {
        VuiUtils.addScrollProps(element, view);
    }

    public static void addProps(VuiElement element, Map<String, Boolean> propsMap) {
        VuiUtils.addProps(element, propsMap);
    }

    public static VuiElement generateCommonVuiElement(String id, VuiElementType type, String label, String action) {
        return VuiUtils.generateCommonVuiElement(id, type, label, action);
    }

    public static VuiElement generateCommonVuiElement(int id, VuiElementType type, String label, String action) {
        return VuiUtils.generateCommonVuiElement("" + id, type, label, action);
    }

    public static VuiElement generateCommonVuiElement(int id, VuiElementType type, String label) {
        return VuiUtils.generateCommonVuiElement("" + id, type, label, (String) null);
    }

    public static VuiElement generateCommonVuiElement(String id, VuiElementType type, String label) {
        return VuiUtils.generateCommonVuiElement(id, type, label, (String) null);
    }

    public static VuiElement generateCommonVuiElement(String id, VuiElementType type, String label, boolean isLayoutLoadable) {
        return VuiUtils.generateCommonVuiElement(id, type, label, null, isLayoutLoadable, VuiPriority.LEVEL3);
    }

    public static VuiElement generateLayoutLoadableVuiElement(String id, VuiElementType type, String label) {
        return VuiUtils.generateCommonVuiElement(id, type, label, null, true, VuiPriority.LEVEL3);
    }

    public static VuiElement generateLayoutLoadableVuiElement(String id, VuiElementType type, String label, String action) {
        return VuiUtils.generateCommonVuiElement(id, type, label, action, true, VuiPriority.LEVEL3);
    }

    public static VuiElement generatePriorityVuiElement(String id, VuiElementType type, String label, VuiPriority priority) {
        return VuiUtils.generateCommonVuiElement(id, type, label, null, false, priority);
    }

    public static VuiElement generateVideoVuiElement(String id, VuiElementType type, String label, String action) {
        return VuiUtils.generateCommonVuiElement(id, type, label, action, false, VuiPriority.LEVEL2, VuiUtils.LIST_VEDIO_TYPE);
    }

    public static VuiElement generatePriorityVuiElement(String id, VuiElementType type, String label, String action, VuiPriority priority) {
        return VuiUtils.generateCommonVuiElement(id, type, label, action, false, priority);
    }

    public static VuiElement generateStatefulButtonElement(int currIndex, String[] vuilabels, int id) {
        String name = VuiAction.SETVALUE.getName();
        return VuiUtils.generateStatefulButtonElement(currIndex, vuilabels, name, "" + id, "");
    }

    public static VuiElement generateStatefulButtonElement(int currIndex, String[] vuilabels, String id) {
        return VuiUtils.generateStatefulButtonElement(currIndex, vuilabels, VuiAction.SETVALUE.getName(), id, "");
    }

    public static VuiElement generateStatefulButtonElement(int currIndex, String[] vuilabels, String action, String id) {
        return VuiUtils.generateStatefulButtonElement(currIndex, vuilabels, action, id, "");
    }

    public static VuiElement generateStatefulButtonElement(int currIndex, String[] vuilabels, String action, int id) {
        return VuiUtils.generateStatefulButtonElement(currIndex, vuilabels, action, "" + id, "");
    }

    public static VuiElement generateStatefulButtonElement(int currIndex, String[] vuilabels, String action, String id, String label) {
        return VuiUtils.generateStatefulButtonElement(currIndex, vuilabels, action, id, label);
    }

    public static <T> T getValueByName(VuiElement vuiElement, String name) {
        return (T) VuiUtils.getValueByName(vuiElement, name);
    }
}
