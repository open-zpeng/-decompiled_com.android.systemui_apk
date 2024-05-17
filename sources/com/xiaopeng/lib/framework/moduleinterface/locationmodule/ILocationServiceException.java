package com.xiaopeng.lib.framework.moduleinterface.locationmodule;

import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
/* loaded from: classes23.dex */
public abstract class ILocationServiceException extends Exception {
    public static final int REASON_SERVICE_BOUND = 3;
    public static final int REASON_SERVICE_NOT_INITIALIZED = 5;
    public static final int REASON_SERVICE_STARTED = 2;
    public static final int REASON_SERVICE_STOPPED = 1;
    public static final int REASON_SERVICE_UNBOUND = 4;

    public abstract int getReasonCode();

    public ILocationServiceException(String message) {
        super(message);
    }

    @Override // java.lang.Throwable
    public String toString() {
        StringBuilder sb;
        String str;
        if (getMessage() != null) {
            sb = new StringBuilder();
            sb.append(getMessage());
            str = " (";
        } else {
            sb = new StringBuilder();
            str = "Reason: (";
        }
        sb.append(str);
        sb.append(getReasonCode());
        sb.append(NavigationBarInflaterView.KEY_CODE_END);
        return sb.toString();
    }
}
