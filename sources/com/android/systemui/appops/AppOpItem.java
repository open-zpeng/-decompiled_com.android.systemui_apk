package com.android.systemui.appops;

import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
/* loaded from: classes21.dex */
public class AppOpItem {
    private int mCode;
    private String mPackageName;
    private String mState;
    private long mTimeStarted;
    private int mUid;

    public AppOpItem(int code, int uid, String packageName, long timeStarted) {
        this.mCode = code;
        this.mUid = uid;
        this.mPackageName = packageName;
        this.mTimeStarted = timeStarted;
        this.mState = "AppOpItem(Op code=" + code + ", UID=" + uid + ", Package name=" + packageName + NavigationBarInflaterView.KEY_CODE_END;
    }

    public int getCode() {
        return this.mCode;
    }

    public int getUid() {
        return this.mUid;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public long getTimeStarted() {
        return this.mTimeStarted;
    }

    public String toString() {
        return this.mState;
    }
}
