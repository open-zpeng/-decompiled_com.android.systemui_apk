package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.SignalController.IconGroup;
import com.android.systemui.statusbar.policy.SignalController.State;
import java.io.PrintWriter;
import java.util.BitSet;
/* loaded from: classes21.dex */
public abstract class SignalController<T extends State, I extends IconGroup> {
    static final int HISTORY_SIZE = 64;
    static final boolean RECORD_HISTORY = true;
    private final CallbackHandler mCallbackHandler;
    protected final Context mContext;
    private int mHistoryIndex;
    protected final NetworkControllerImpl mNetworkController;
    protected final String mTag;
    protected final int mTransportType;
    protected static final boolean DEBUG = NetworkControllerImpl.DEBUG;
    protected static final boolean CHATTY = NetworkControllerImpl.CHATTY;
    protected final T mCurrentState = cleanState();
    protected final T mLastState = cleanState();
    private final State[] mHistory = new State[64];

    protected abstract T cleanState();

    public abstract void notifyListeners(NetworkController.SignalCallback signalCallback);

    public SignalController(String tag, Context context, int type, CallbackHandler callbackHandler, NetworkControllerImpl networkController) {
        this.mTag = "NetworkController." + tag;
        this.mNetworkController = networkController;
        this.mTransportType = type;
        this.mContext = context;
        this.mCallbackHandler = callbackHandler;
        for (int i = 0; i < 64; i++) {
            this.mHistory[i] = cleanState();
        }
    }

    public T getState() {
        return this.mCurrentState;
    }

    public void updateConnectivity(BitSet connectedTransports, BitSet validatedTransports) {
        this.mCurrentState.inetCondition = validatedTransports.get(this.mTransportType) ? 1 : 0;
        notifyListenersIfNecessary();
    }

    public void resetLastState() {
        this.mCurrentState.copyFrom(this.mLastState);
    }

    public boolean isDirty() {
        if (!this.mLastState.equals(this.mCurrentState)) {
            if (DEBUG) {
                String str = this.mTag;
                Log.d(str, "Change in state from: " + this.mLastState + "\n\tto: " + this.mCurrentState);
                return true;
            }
            return true;
        }
        return false;
    }

    public void saveLastState() {
        recordLastState();
        this.mCurrentState.time = System.currentTimeMillis();
        this.mLastState.copyFrom(this.mCurrentState);
    }

    public int getQsCurrentIconId() {
        if (this.mCurrentState.connected) {
            return getIcons().mQsIcons[this.mCurrentState.inetCondition][this.mCurrentState.level];
        }
        if (this.mCurrentState.enabled) {
            return getIcons().mQsDiscState;
        }
        return getIcons().mQsNullState;
    }

    public int getCurrentIconId() {
        if (this.mCurrentState.connected) {
            return getIcons().mSbIcons[this.mCurrentState.inetCondition][this.mCurrentState.level];
        }
        if (this.mCurrentState.enabled) {
            return getIcons().mSbDiscState;
        }
        return getIcons().mSbNullState;
    }

    public int getContentDescription() {
        if (this.mCurrentState.connected) {
            return getIcons().mContentDesc[this.mCurrentState.level];
        }
        return getIcons().mDiscContentDesc;
    }

    public void notifyListenersIfNecessary() {
        if (isDirty()) {
            saveLastState();
            notifyListeners();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequence getStringIfExists(int resId) {
        return resId != 0 ? this.mContext.getText(resId) : "";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public I getIcons() {
        return (I) this.mCurrentState.iconGroup;
    }

    protected void recordLastState() {
        State[] stateArr = this.mHistory;
        int i = this.mHistoryIndex;
        this.mHistoryIndex = i + 1;
        stateArr[i & 63].copyFrom(this.mLastState);
    }

    public void dump(PrintWriter pw) {
        pw.println("  - " + this.mTag + " -----");
        StringBuilder sb = new StringBuilder();
        sb.append("  Current State: ");
        sb.append(this.mCurrentState);
        pw.println(sb.toString());
        int size = 0;
        for (int i = 0; i < 64; i++) {
            if (this.mHistory[i].time != 0) {
                size++;
            }
        }
        int i2 = this.mHistoryIndex;
        int i3 = i2 + 64;
        while (true) {
            i3--;
            if (i3 < (this.mHistoryIndex + 64) - size) {
                return;
            }
            pw.println("  Previous State(" + ((this.mHistoryIndex + 64) - i3) + "): " + this.mHistory[i3 & 63]);
        }
    }

    public final void notifyListeners() {
        notifyListeners(this.mCallbackHandler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public static class IconGroup {
        final int[] mContentDesc;
        final int mDiscContentDesc;
        final String mName;
        final int mQsDiscState;
        final int[][] mQsIcons;
        final int mQsNullState;
        final int mSbDiscState;
        final int[][] mSbIcons;
        final int mSbNullState;

        public IconGroup(String name, int[][] sbIcons, int[][] qsIcons, int[] contentDesc, int sbNullState, int qsNullState, int sbDiscState, int qsDiscState, int discContentDesc) {
            this.mName = name;
            this.mSbIcons = sbIcons;
            this.mQsIcons = qsIcons;
            this.mContentDesc = contentDesc;
            this.mSbNullState = sbNullState;
            this.mQsNullState = qsNullState;
            this.mSbDiscState = sbDiscState;
            this.mQsDiscState = qsDiscState;
            this.mDiscContentDesc = discContentDesc;
        }

        public String toString() {
            return "IconGroup(" + this.mName + NavigationBarInflaterView.KEY_CODE_END;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public static class State {
        public boolean activityDormant;
        boolean activityIn;
        boolean activityOut;
        boolean connected;
        boolean enabled;
        IconGroup iconGroup;
        int inetCondition;
        int level;
        int rssi;
        long time;

        public void copyFrom(State state) {
            this.connected = state.connected;
            this.enabled = state.enabled;
            this.level = state.level;
            this.iconGroup = state.iconGroup;
            this.inetCondition = state.inetCondition;
            this.activityIn = state.activityIn;
            this.activityOut = state.activityOut;
            this.activityDormant = state.activityDormant;
            this.rssi = state.rssi;
            this.time = state.time;
        }

        public String toString() {
            if (this.time != 0) {
                StringBuilder builder = new StringBuilder();
                toString(builder);
                return builder.toString();
            }
            return "Empty " + getClass().getSimpleName();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void toString(StringBuilder builder) {
            builder.append("connected=");
            builder.append(this.connected);
            builder.append(',');
            builder.append("enabled=");
            builder.append(this.enabled);
            builder.append(',');
            builder.append("level=");
            builder.append(this.level);
            builder.append(',');
            builder.append("inetCondition=");
            builder.append(this.inetCondition);
            builder.append(',');
            builder.append("iconGroup=");
            builder.append(this.iconGroup);
            builder.append(',');
            builder.append("activityIn=");
            builder.append(this.activityIn);
            builder.append(',');
            builder.append("activityOut=");
            builder.append(this.activityOut);
            builder.append(',');
            builder.append("activityDormant=");
            builder.append(this.activityDormant);
            builder.append(',');
            builder.append("rssi=");
            builder.append(this.rssi);
            builder.append(',');
            builder.append("lastModified=");
            builder.append(DateFormat.format("MM-dd HH:mm:ss", this.time));
        }

        public boolean equals(Object o) {
            if (o.getClass().equals(getClass())) {
                State other = (State) o;
                return other.connected == this.connected && other.enabled == this.enabled && other.level == this.level && other.inetCondition == this.inetCondition && other.iconGroup == this.iconGroup && other.activityIn == this.activityIn && other.activityOut == this.activityOut && other.activityDormant == this.activityDormant && other.rssi == this.rssi;
            }
            return false;
        }
    }
}
