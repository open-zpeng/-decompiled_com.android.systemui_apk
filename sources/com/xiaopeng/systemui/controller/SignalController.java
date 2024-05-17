package com.xiaopeng.systemui.controller;

import android.content.Context;
import android.util.Log;
import com.xiaopeng.systemui.controller.NetworkController;
import com.xiaopeng.systemui.controller.SignalController.State;
import com.xiaopeng.systemui.statusbar.CallbackHandler;
/* loaded from: classes24.dex */
public abstract class SignalController<T extends State> {
    protected static final boolean DEBUG = true;
    static final int HISTORY_SIZE = 64;
    static final boolean RECORD_HISTORY = true;
    protected static final String TAG = "SignalController";
    protected final CallbackHandler mCallbackHandler;
    protected final Context mContext;
    private int mHistoryIndex;
    protected final NetworkController mNetworkController;
    protected final String mTag;
    protected final int mTransportType;
    protected final T mCurrentState = cleanState();
    protected final T mLastState = cleanState();
    private final State[] mHistory = new State[64];

    protected abstract T cleanState();

    public abstract void notifyListeners(NetworkController.SignalCallback signalCallback);

    public SignalController(String tag, Context context, int type, CallbackHandler callbackHandler, NetworkController networkController) {
        this.mTag = "SignalController." + tag;
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

    public boolean isDirty() {
        if (!this.mLastState.equals(this.mCurrentState)) {
            String str = this.mTag;
            Log.d(str, "Change in state from: " + this.mLastState + "\n\tto: " + this.mCurrentState);
            return true;
        }
        return false;
    }

    public void saveLastState() {
        recordLastState();
        this.mLastState.copyFrom(this.mCurrentState);
    }

    public void notifyListenersIfNecessary() {
        if (isDirty()) {
            saveLastState();
            notifyListeners();
        }
    }

    protected void recordLastState() {
        State[] stateArr = this.mHistory;
        int i = this.mHistoryIndex;
        this.mHistoryIndex = i + 1;
        stateArr[i & 63].copyFrom(this.mLastState);
    }

    public final void notifyListeners() {
        notifyListeners(this.mCallbackHandler);
    }

    /* loaded from: classes24.dex */
    public static class State {
        boolean connected;
        boolean enabled;
        public int level;
        int rssi;

        public void copyFrom(State state) {
            this.connected = state.connected;
            this.enabled = state.enabled;
            this.level = state.level;
            this.rssi = state.rssi;
        }

        public String toString() {
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
            builder.append("rssi=");
            builder.append(this.rssi);
            builder.append(',');
        }

        public boolean equals(Object o) {
            if (o.getClass().equals(getClass())) {
                State other = (State) o;
                return other.connected == this.connected && other.enabled == this.enabled && other.level == this.level && other.rssi == this.rssi;
            }
            return false;
        }
    }
}
