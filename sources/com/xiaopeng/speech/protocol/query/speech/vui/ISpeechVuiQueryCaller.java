package com.xiaopeng.speech.protocol.query.speech.vui;

import com.xiaopeng.speech.IQueryCaller;
/* loaded from: classes23.dex */
public interface ISpeechVuiQueryCaller extends IQueryCaller {
    String getStatefulButtonValue(String str, String str2, String str3, String str4);

    int getXSliderIndex(String str, String str2, String str3, String str4);

    boolean isCheckBoxChecked(String str, String str2, String str3, String str4);

    boolean isElementCanScrolled(String str, String str2, String str3, String str4, String str5);

    boolean isElementEnabled(String str, String str2, String str3, String str4);

    boolean isStatefulButtonChecked(String str, String str2, String str3, String str4);

    boolean isSwitchChecked(String str, String str2, String str3, String str4);

    boolean isTableLayoutSelected(int i, String str, String str2, String str3, String str4);

    boolean isVuiSwitchOpened();

    default boolean isRadiobuttonChecked(String elementId, String sceneId, String packageName, String packageVersion) {
        return false;
    }

    default String isViewVisibleByScrollView(String packageName, String sceneId, String elementsId, String packageVersion) {
        return "";
    }

    default boolean reBuildScene(String[] sceneId) {
        return true;
    }
}
