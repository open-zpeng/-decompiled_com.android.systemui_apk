package com.android.systemui.plugins;

import android.content.ComponentName;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.os.Handler;
import android.os.VibrationEffect;
import android.util.SparseArray;
import com.android.systemui.plugins.annotations.Dependencies;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
@Dependencies({@DependsOn(target = StreamState.class), @DependsOn(target = State.class), @DependsOn(target = Callbacks.class)})
@ProvidesInterface(version = 1)
/* loaded from: classes21.dex */
public interface VolumeDialogController {
    public static final int VERSION = 1;

    @ProvidesInterface(version = 1)
    /* loaded from: classes21.dex */
    public interface Callbacks {
        public static final int VERSION = 1;

        void onAccessibilityModeChanged(Boolean bool);

        void onCaptionComponentStateChanged(Boolean bool, Boolean bool2);

        void onConfigurationChanged();

        void onDismissRequested(int i);

        void onLayoutDirectionChanged(int i);

        void onScreenOff();

        void onShowRequested(int i);

        void onShowSafetyWarning(int i);

        void onShowSilentHint();

        void onShowVibrateHint();

        void onStateChanged(State state);
    }

    void addCallback(Callbacks callbacks, Handler handler);

    boolean areCaptionsEnabled();

    AudioManager getAudioManager();

    void getCaptionsComponentState(boolean z);

    void getState();

    boolean hasVibrator();

    boolean isCaptionStreamOptedOut();

    void notifyVisible(boolean z);

    void removeCallback(Callbacks callbacks);

    void scheduleTouchFeedback();

    void setActiveStream(int i);

    void setCaptionsEnabled(boolean z);

    void setRingerMode(int i, boolean z);

    void setStreamVolume(int i, int i2);

    void userActivity();

    void vibrate(VibrationEffect vibrationEffect);

    @ProvidesInterface(version = 1)
    /* loaded from: classes21.dex */
    public static final class StreamState {
        public static final int VERSION = 1;
        public boolean dynamic;
        public int level;
        public int levelMax;
        public int levelMin;
        public boolean muteSupported;
        public boolean muted;
        public int name;
        public String remoteLabel;
        public boolean routedToBluetooth;

        public StreamState copy() {
            StreamState rt = new StreamState();
            rt.dynamic = this.dynamic;
            rt.level = this.level;
            rt.levelMin = this.levelMin;
            rt.levelMax = this.levelMax;
            rt.muted = this.muted;
            rt.muteSupported = this.muteSupported;
            rt.name = this.name;
            rt.remoteLabel = this.remoteLabel;
            rt.routedToBluetooth = this.routedToBluetooth;
            return rt;
        }
    }

    @ProvidesInterface(version = 1)
    /* loaded from: classes21.dex */
    public static final class State {
        public static int NO_ACTIVE_STREAM = -1;
        public static final int VERSION = 1;
        public boolean disallowAlarms;
        public boolean disallowMedia;
        public boolean disallowRinger;
        public boolean disallowSystem;
        public ComponentName effectsSuppressor;
        public String effectsSuppressorName;
        public int ringerModeExternal;
        public int ringerModeInternal;
        public int zenMode;
        public final SparseArray<StreamState> states = new SparseArray<>();
        public int activeStream = NO_ACTIVE_STREAM;

        public State copy() {
            State rt = new State();
            for (int i = 0; i < this.states.size(); i++) {
                rt.states.put(this.states.keyAt(i), this.states.valueAt(i).copy());
            }
            int i2 = this.ringerModeExternal;
            rt.ringerModeExternal = i2;
            rt.ringerModeInternal = this.ringerModeInternal;
            rt.zenMode = this.zenMode;
            ComponentName componentName = this.effectsSuppressor;
            if (componentName != null) {
                rt.effectsSuppressor = componentName.clone();
            }
            rt.effectsSuppressorName = this.effectsSuppressorName;
            rt.activeStream = this.activeStream;
            rt.disallowAlarms = this.disallowAlarms;
            rt.disallowMedia = this.disallowMedia;
            rt.disallowSystem = this.disallowSystem;
            rt.disallowRinger = this.disallowRinger;
            return rt;
        }

        public String toString() {
            return toString(0);
        }

        public String toString(int indent) {
            StringBuilder sb = new StringBuilder("{");
            if (indent > 0) {
                sep(sb, indent);
            }
            for (int i = 0; i < this.states.size(); i++) {
                if (i > 0) {
                    sep(sb, indent);
                }
                int stream = this.states.keyAt(i);
                StreamState ss = this.states.valueAt(i);
                sb.append(AudioSystem.streamToString(stream));
                sb.append(NavigationBarInflaterView.KEY_IMAGE_DELIM);
                sb.append(ss.level);
                sb.append('[');
                sb.append(ss.levelMin);
                sb.append("..");
                sb.append(ss.levelMax);
                sb.append(']');
                if (ss.muted) {
                    sb.append(" [MUTED]");
                }
                if (ss.dynamic) {
                    sb.append(" [DYNAMIC]");
                }
            }
            sep(sb, indent);
            sb.append("ringerModeExternal:");
            sb.append(this.ringerModeExternal);
            sep(sb, indent);
            sb.append("ringerModeInternal:");
            sb.append(this.ringerModeInternal);
            sep(sb, indent);
            sb.append("zenMode:");
            sb.append(this.zenMode);
            sep(sb, indent);
            sb.append("effectsSuppressor:");
            sb.append(this.effectsSuppressor);
            sep(sb, indent);
            sb.append("effectsSuppressorName:");
            sb.append(this.effectsSuppressorName);
            sep(sb, indent);
            sb.append("activeStream:");
            sb.append(this.activeStream);
            sep(sb, indent);
            sb.append("disallowAlarms:");
            sb.append(this.disallowAlarms);
            sep(sb, indent);
            sb.append("disallowMedia:");
            sb.append(this.disallowMedia);
            sep(sb, indent);
            sb.append("disallowSystem:");
            sb.append(this.disallowSystem);
            sep(sb, indent);
            sb.append("disallowRinger:");
            sb.append(this.disallowRinger);
            if (indent > 0) {
                sep(sb, indent);
            }
            sb.append('}');
            return sb.toString();
        }

        private static void sep(StringBuilder sb, int indent) {
            if (indent > 0) {
                sb.append('\n');
                for (int i = 0; i < indent; i++) {
                    sb.append(' ');
                }
                return;
            }
            sb.append(',');
        }
    }
}
