package com.xiaopeng.xui.sound;

import androidx.annotation.NonNull;
/* loaded from: classes25.dex */
abstract class AbsSoundEffect implements XSoundEffect {
    SoundEffectResource mSoundEffectResource;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbsSoundEffect(@NonNull SoundEffectResource soundEffectResource) {
        this.mSoundEffectResource = soundEffectResource;
    }
}
