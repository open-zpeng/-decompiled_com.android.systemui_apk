package com.xiaopeng.systemui.infoflow.message.data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes24.dex */
public class DataEvent {
    public static final int ADD = 0;
    public static final int REMOVE = 1;
    public static final int UPDATE = 2;
    public CardEntry entry;
    public int eventType;
    public int fromPosition;
    public int toPosition;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface EventType {
    }
}
