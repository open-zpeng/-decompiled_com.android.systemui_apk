package com.xiaopeng.systemui.infoflow.speech.core.speech.behavior;

import com.xiaopeng.speech.common.util.SimpleCallbackList;
import com.xiaopeng.speech.jarvisproto.DMWait;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
/* loaded from: classes24.dex */
public class SpeechAvatarManager {
    private SimpleCallbackList<ISpeechAvatar> mSpriteAvatarCallbacks = new SimpleCallbackList<>();

    public void addCallback(ISpeechAvatar behavior) {
        this.mSpriteAvatarCallbacks.addCallback(behavior);
    }

    public void removeCallback(ISpeechAvatar behavior) {
        this.mSpriteAvatarCallbacks.removeCallback(behavior);
    }

    public void onDialogStart(int type) {
        Object[] callbacks = this.mSpriteAvatarCallbacks.collectCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ISpeechAvatar) obj).onDialogStart(type);
            }
        }
    }

    public void onDialogEnd(DialogEndReason endReason) {
        Object[] callbacks = this.mSpriteAvatarCallbacks.collectCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ISpeechAvatar) obj).onDialogEnd(endReason);
            }
        }
    }

    public void onSilence() {
        Object[] callbacks = this.mSpriteAvatarCallbacks.collectCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ISpeechAvatar) obj).onSilence();
            }
        }
    }

    public void onVadEnd() {
        Object[] callbacks = this.mSpriteAvatarCallbacks.collectCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ISpeechAvatar) obj).onVadEnd();
            }
        }
    }

    public void onVadBegin() {
        Object[] callbacks = this.mSpriteAvatarCallbacks.collectCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ISpeechAvatar) obj).onVadBegin();
            }
        }
    }

    public void onDialogWait(DMWait reason) {
        Object[] callbacks = this.mSpriteAvatarCallbacks.collectCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ISpeechAvatar) obj).onDialogWait(reason);
            }
        }
    }

    public void onAvatarWakerupDisable(String reason) {
        Object[] callbacks = this.mSpriteAvatarCallbacks.collectCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ISpeechAvatar) obj).onAvatarWakerupDisable(reason);
            }
        }
    }

    public void onAvatarWakerupEnable(String reason) {
        Object[] callbacks = this.mSpriteAvatarCallbacks.collectCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ISpeechAvatar) obj).onAvatarWakerupEnable(reason);
            }
        }
    }

    public void onWakeupStatusChanged(int status, int type, String info) {
        Object[] callbacks = this.mSpriteAvatarCallbacks.collectCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ISpeechAvatar) obj).onWakeupStatusChanged(status, type, info);
            }
        }
    }
}
