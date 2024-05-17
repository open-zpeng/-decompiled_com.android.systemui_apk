package com.xiaopeng.systemui.infoflow.speech.core.speech.model;

import android.text.TextUtils;
import android.util.Log;
import com.xiaopeng.speech.jarvisproto.DMListening;
import com.xiaopeng.speech.jarvisproto.DMRecognized;
import com.xiaopeng.speech.jarvisproto.DMWait;
import com.xiaopeng.speech.jarvisproto.DialogSoundAreaStatus;
import com.xiaopeng.speech.jarvisproto.SoundAreaStatus;
import com.xiaopeng.speech.protocol.SpeechModel;
import com.xiaopeng.speech.protocol.node.avatar.AbsAvatarListener;
import com.xiaopeng.speech.protocol.node.avatar.AvatarNode;
import com.xiaopeng.speech.protocol.node.bugreport.BugReportEndValue;
import com.xiaopeng.speech.protocol.node.bugreport.BugReportNode;
import com.xiaopeng.speech.protocol.node.dialog.AbsDialogListener;
import com.xiaopeng.speech.protocol.node.dialog.DialogNode;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogExitReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.WakeupReason;
import com.xiaopeng.speech.protocol.node.internal.InternalListener;
import com.xiaopeng.speech.protocol.node.internal.InternalNode;
import com.xiaopeng.speech.protocol.node.tts.TtsEchoValue;
import com.xiaopeng.speech.protocol.node.tts.TtsListener;
import com.xiaopeng.speech.protocol.node.tts.TtsNode;
import com.xiaopeng.speech.protocol.node.wakeup.AbsWakeupStatusListener;
import com.xiaopeng.speech.protocol.node.wakeup.WakeupStatusNode;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.infoflow.helper.ContextHelper;
import com.xiaopeng.systemui.infoflow.speech.IVoiceWavwView;
import com.xiaopeng.systemui.infoflow.speech.SpeechPresenter;
import com.xiaopeng.systemui.infoflow.speech.VoiceWaveView;
import com.xiaopeng.systemui.infoflow.speech.core.speech.SpeechManager;
import com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.SpeechAvatarManager;
import com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.SpeechContextManager;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
/* loaded from: classes24.dex */
public class DialogModel extends SpeechModel {
    private static final String TAG = DialogModel.class.getSimpleName();
    private SpeechManager mSpriteManager;
    private IVoiceWavwView mVoiceWaveView = new VoiceWaveView();

    public DialogModel(SpeechManager spriteManager) {
        this.mSpriteManager = spriteManager;
        subscribe(DialogNode.class, new DialogListener());
        subscribe(AvatarNode.class, new AvatarListener());
        subscribe(BugReportNode.class, new BugReportListener());
        subscribe(TtsNode.class, new SpeechTtsListener());
        subscribe(WakeupStatusNode.class, new WakeupStatusListener());
        subscribe(InternalNode.class, new InternalListener() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.DialogModel.1
            @Override // com.xiaopeng.speech.protocol.node.internal.InternalListener
            public void onDmOutput(String s) {
            }

            @Override // com.xiaopeng.speech.protocol.node.internal.InternalListener
            public void onInputDmData(String s, String s1) {
            }

            @Override // com.xiaopeng.speech.protocol.node.internal.InternalListener
            public void onLocalWakeupResult(String s, String s1) {
            }

            @Override // com.xiaopeng.speech.protocol.node.internal.InternalListener
            public void onLocalWakeupResultWithChannel(String type, String word, String channel) {
                String str = DialogModel.TAG;
                Logger.d(str, "onLocalWakeupResultWithChannel : channel = " + channel);
                SpeechPresenter.getInstance().onVoiceLocChanged(Integer.valueOf(channel).intValue() + 1);
            }

            @Override // com.xiaopeng.speech.protocol.node.internal.InternalListener
            public void onResourceUpdateFinish(String s, String s1) {
            }

            @Override // com.xiaopeng.speech.protocol.node.internal.InternalListener
            public void onRebootComplete(String s, String s1) {
            }
        });
    }

    /* loaded from: classes24.dex */
    private class BugReportListener implements com.xiaopeng.speech.protocol.node.bugreport.BugReportListener {
        private BugReportListener() {
        }

        @Override // com.xiaopeng.speech.protocol.node.bugreport.BugReportListener
        public void onBugReportBegin() {
            ContextHelper.setBugReport(true);
            Log.d(DialogModel.TAG, "onBugReportBegin");
            DialogModel.this.getSpriteContext().onBugReportBegin();
        }

        @Override // com.xiaopeng.speech.protocol.node.bugreport.BugReportListener
        public void onBugReportEnd(BugReportEndValue bugReportEndValue) {
            Log.d(DialogModel.TAG, String.format("onBugReportEnd:%b, %s", Boolean.valueOf(bugReportEndValue.isSuc()), bugReportEndValue.getReason()));
            ContextHelper.setBugReport(false);
            DialogModel.this.getSpriteContext().onBugReportEnd();
            DialogModel.this.getSpriteAvatar().onDialogEnd(null);
        }

        @Override // com.xiaopeng.speech.protocol.node.bugreport.BugReportListener
        public void onBugReportVolume(String data) {
            String str = DialogModel.TAG;
            Log.d(str, "onBugReportVolume = " + data);
        }

        @Override // com.xiaopeng.speech.protocol.node.bugreport.BugReportListener
        public void onBugReportEndtts() {
            Log.d(DialogModel.TAG, "onBugReportEndtts");
        }
    }

    /* loaded from: classes24.dex */
    private class DialogListener extends AbsDialogListener {
        private DialogListener() {
        }

        @Override // com.xiaopeng.speech.protocol.node.dialog.AbsDialogListener, com.xiaopeng.speech.protocol.node.dialog.DialogListener
        public void onDialogStart(final WakeupReason wakeupReason) {
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.DialogModel.DialogListener.1
                @Override // java.lang.Runnable
                public void run() {
                    if (wakeupReason != null) {
                        String str = DialogModel.TAG;
                        Log.d(str, "onDialogStart : " + wakeupReason.soundArea);
                    } else {
                        Log.d(DialogModel.TAG, "onDialogStart");
                    }
                    ContextHelper.setBugReport(false);
                    SpeechPresenter.getInstance().updateVoiceLoc(wakeupReason);
                    DialogModel.this.getSpriteAvatar().onDialogStart(2);
                    SpeechPresenter.getInstance().setSpeechViewType(2);
                    if (wakeupReason != null) {
                        SpeechPresenter.getInstance().showVoiceWaveAnim(wakeupReason.soundArea, 0);
                    }
                }
            });
        }

        @Override // com.xiaopeng.speech.protocol.node.dialog.AbsDialogListener, com.xiaopeng.speech.protocol.node.dialog.DialogListener
        public void onDialogEnd(DialogEndReason endReason) {
            Log.i(DialogModel.TAG, "onDialogEnd");
            if (!ContextHelper.isBugReport()) {
                DialogModel.this.getSpriteAvatar().onDialogEnd(endReason);
            }
        }

        @Override // com.xiaopeng.speech.protocol.node.dialog.AbsDialogListener, com.xiaopeng.speech.protocol.node.dialog.DialogListener
        public void onDialogExit(final DialogExitReason exitReason) {
            Log.i(DialogModel.TAG, "onDialogExit");
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.DialogModel.DialogListener.2
                @Override // java.lang.Runnable
                public void run() {
                    SpeechPresenter.getInstance().onDialogExit(exitReason.soundArea);
                }
            });
        }

        @Override // com.xiaopeng.speech.protocol.node.dialog.AbsDialogListener, com.xiaopeng.speech.protocol.node.dialog.DialogListener
        public void onVadBegin() {
            Logger.d(DialogModel.TAG, "onVadBegin");
            DialogModel.this.getSpriteAvatar().onVadBegin();
        }

        @Override // com.xiaopeng.speech.protocol.node.dialog.AbsDialogListener, com.xiaopeng.speech.protocol.node.dialog.DialogListener
        public void onVadEnd() {
            Logger.d(DialogModel.TAG, "onVadEnd");
            DialogModel.this.getSpriteAvatar().onVadEnd();
        }

        @Override // com.xiaopeng.speech.protocol.node.dialog.AbsDialogListener, com.xiaopeng.speech.protocol.node.dialog.DialogListener
        public void onDialogWait(DMWait reason) {
            if (reason != null) {
                String str = DialogModel.TAG;
                Logger.d(str, "onDialogWait : " + reason.reason);
            } else {
                Logger.d(DialogModel.TAG, "onDialogWait");
            }
            DialogModel.this.getSpriteAvatar().onDialogWait(reason);
        }

        @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
        public void onSoundAreaStatusChanged(SoundAreaStatus status) {
            String str = DialogModel.TAG;
            Logger.i(str, "onSoundAreaStatusChanged " + status);
            SpeechPresenter.getInstance().onSoundAreaStatusChanged(status);
        }

        @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
        public void onDialogSoundAreaStatusChanged(DialogSoundAreaStatus status) {
            int soundType;
            if (status.recognizingStatus || status.underStandingStatus) {
                soundType = 0;
            } else if (status.ttsPlayingStatus) {
                soundType = 2;
            } else if (!status.listeningStatus) {
                DialogModel.this.mVoiceWaveView.stopVoiceWaveAnim(status.soundArea);
                String str = DialogModel.TAG;
                Logger.i(str, "onDialogSoundAreaStatusChanged " + status);
                return;
            } else {
                soundType = 1;
            }
            String str2 = DialogModel.TAG;
            Logger.i(str2, "onDialogSoundAreaStatusChanged " + status + " , soundType: " + soundType);
            DialogModel.this.mVoiceWaveView.showVoiceWaveAnim(status.soundArea, soundType);
        }
    }

    /* loaded from: classes24.dex */
    private class AvatarListener extends AbsAvatarListener {
        int mVolume;

        private AvatarListener() {
            this.mVolume = 0;
        }

        @Override // com.xiaopeng.speech.protocol.node.avatar.AbsAvatarListener, com.xiaopeng.speech.protocol.node.avatar.AvatarListener
        public void onSilence(DMRecognized dmRecognized) {
            super.onSilence(dmRecognized);
            DialogModel.this.getSpriteAvatar().onSilence();
        }

        @Override // com.xiaopeng.speech.protocol.node.avatar.AbsAvatarListener, com.xiaopeng.speech.protocol.node.avatar.AvatarListener
        public void onListening(final DMListening dmListening) {
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.DialogModel.AvatarListener.1
                @Override // java.lang.Runnable
                public void run() {
                    if (AvatarListener.this.mVolume != dmListening.volume) {
                        AvatarListener.this.mVolume = dmListening.volume;
                        if (dmListening.soundArea != 0) {
                            SpeechPresenter.getInstance().updateVoiceLoc(dmListening.soundArea);
                        }
                        SpeechPresenter.setVolume(AvatarListener.this.mVolume);
                        String str = DialogModel.TAG;
                        Logger.d(str, "onListening : volume = " + AvatarListener.this.mVolume);
                        if (AvatarListener.this.mVolume > 0) {
                            SpeechPresenter.getInstance().showVoiceWaveAnim(SpeechPresenter.getVoiceLoc(), 1);
                        }
                    }
                }
            });
        }

        @Override // com.xiaopeng.speech.protocol.node.avatar.AbsAvatarListener, com.xiaopeng.speech.protocol.node.avatar.AvatarListener
        public void onSpeaking() {
            Logger.d(DialogModel.TAG, "onSpeaking");
            super.onSpeaking();
        }

        @Override // com.xiaopeng.speech.protocol.node.avatar.AbsAvatarListener, com.xiaopeng.speech.protocol.node.avatar.AvatarListener
        public void onStandby() {
            super.onStandby();
        }

        @Override // com.xiaopeng.speech.protocol.node.avatar.AbsAvatarListener, com.xiaopeng.speech.protocol.node.avatar.AvatarListener
        public void onUnderstanding() {
        }

        @Override // com.xiaopeng.speech.protocol.node.avatar.AbsAvatarListener, com.xiaopeng.speech.protocol.node.avatar.AvatarListener
        public void onAvatarWakerupDisable(String reason) {
            super.onAvatarWakerupDisable(reason);
            DialogModel.this.getSpriteAvatar().onAvatarWakerupDisable(reason);
        }

        @Override // com.xiaopeng.speech.protocol.node.avatar.AbsAvatarListener, com.xiaopeng.speech.protocol.node.avatar.AvatarListener
        public void onAvatarWakerupEnable(String reason) {
            super.onAvatarWakerupEnable(reason);
            DialogModel.this.getSpriteAvatar().onAvatarWakerupEnable(reason);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SpeechAvatarManager getSpriteAvatar() {
        return this.mSpriteManager.getSpeechAvatarManager();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SpeechContextManager getSpriteContext() {
        return this.mSpriteManager.getSpeechContextManager();
    }

    private int getVoiceLocByChannel(String channel) {
        if (TextUtils.isEmpty(channel)) {
            return 1;
        }
        char c = 65535;
        int hashCode = channel.hashCode();
        if (hashCode != 48) {
            if (hashCode == 49 && channel.equals("1")) {
                c = 1;
            }
        } else if (channel.equals("0")) {
            c = 0;
        }
        if (c == 0 || c != 1) {
            return 1;
        }
        return 2;
    }

    /* loaded from: classes24.dex */
    private class SpeechTtsListener implements TtsListener {
        private SpeechTtsListener() {
        }

        @Override // com.xiaopeng.speech.protocol.node.tts.TtsListener
        public void ttsStart(final String s) {
            String str = DialogModel.TAG;
            Logger.d(str, "ttsStart : " + s);
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.DialogModel.SpeechTtsListener.1
                @Override // java.lang.Runnable
                public void run() {
                    SpeechPresenter.getInstance().onTtsStart(s);
                }
            });
        }

        @Override // com.xiaopeng.speech.protocol.node.tts.TtsListener
        public void ttsEnd(final String s) {
            String str = DialogModel.TAG;
            Logger.d(str, "ttsEnd : " + s);
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.DialogModel.SpeechTtsListener.2
                @Override // java.lang.Runnable
                public void run() {
                    SpeechPresenter.getInstance().onTtsEnd(s);
                }
            });
        }

        @Override // com.xiaopeng.speech.protocol.node.tts.TtsListener
        public void ttsEcho(final TtsEchoValue data) {
            if (data != null) {
                String str = DialogModel.TAG;
                Logger.d(str, "ttsEcho : " + data.text + "," + data.soundArea);
                ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.DialogModel.SpeechTtsListener.3
                    @Override // java.lang.Runnable
                    public void run() {
                        SpeechPresenter.getInstance().onTtsEcho(data);
                    }
                });
            }
        }
    }

    /* loaded from: classes24.dex */
    private class WakeupStatusListener extends AbsWakeupStatusListener {
        private WakeupStatusListener() {
        }

        @Override // com.xiaopeng.speech.protocol.node.wakeup.AbsWakeupStatusListener, com.xiaopeng.speech.protocol.node.wakeup.WakeupStatusListener
        public void onWakeupStatusChanged(int status, int type, String info) {
            String str = DialogModel.TAG;
            Logger.d(str, "onWakeupStatusChanged status:" + status + " &type=" + type + " &info:" + info);
            super.onWakeupStatusChanged(status, type, info);
            DialogModel.this.getSpriteAvatar().onWakeupStatusChanged(status, type, info);
        }
    }
}
