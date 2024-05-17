package com.xiaopeng.systemui.infoflow.helper;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes24.dex */
public class ContextHelper {
    private static AtomicBoolean sSpeeching = new AtomicBoolean();
    private static AtomicBoolean sBugReport = new AtomicBoolean();
    private static AtomicReference<String> sOutputTextRef = new AtomicReference<>();
    private static AtomicReference<String> sInputTextRef = new AtomicReference<>();
    private static AtomicReference<String> sSessionIdRef = new AtomicReference<>();

    public static void setOutputText(String outputText) {
        sOutputTextRef.set(outputText);
    }

    public static String getOutputText() {
        return sOutputTextRef.get();
    }

    public static void setInputText(String inputText) {
        sInputTextRef.set(inputText);
    }

    public static String getInputText() {
        return sInputTextRef.get();
    }

    public static void setSpeeching(boolean speeching) {
        sSpeeching.set(speeching);
    }

    public static boolean isSpeeching() {
        return sSpeeching.get();
    }

    public static void setSessionId(String sessionId) {
        sSessionIdRef.set(sessionId);
    }

    public static String getSessionId() {
        return sSessionIdRef.get();
    }

    public static void setBugReport(boolean bugReport) {
        sBugReport.set(bugReport);
    }

    public static boolean isBugReport() {
        return sBugReport.get();
    }
}
