package com.android.systemui.dock;
/* loaded from: classes21.dex */
public interface DockManager {
    public static final int ALIGN_STATE_GOOD = 0;
    public static final int ALIGN_STATE_POOR = 1;
    public static final int ALIGN_STATE_TERRIBLE = 2;
    public static final int ALIGN_STATE_UNKNOWN = -1;
    public static final int STATE_DOCKED = 1;
    public static final int STATE_DOCKED_HIDE = 2;
    public static final int STATE_NONE = 0;

    /* loaded from: classes21.dex */
    public interface AlignmentStateListener {
        void onAlignmentStateChanged(int i);
    }

    /* loaded from: classes21.dex */
    public interface DockEventListener {
        void onEvent(int i);
    }

    void addAlignmentStateListener(AlignmentStateListener alignmentStateListener);

    void addListener(DockEventListener dockEventListener);

    boolean isDocked();

    void removeAlignmentStateListener(AlignmentStateListener alignmentStateListener);

    void removeListener(DockEventListener dockEventListener);
}
