package com.xiaopeng.systemui.infoflow.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.xiaopeng.systemui.infoflow.music.IMusicView;
/* loaded from: classes24.dex */
public class MusicModeReceiver extends BroadcastReceiver {
    private static final String BROADCAST_ENTER_MUSIC_MODE = "com.xiaopeng.systemui.infoflow.music.enterMusicMode";
    private static final String BROADCAST_EXIT_MUSIC_MODE = "com.xiaopeng.systemui.infoflow.music.exitMusicMode";
    private Context mContext;
    private IMusicView mIMusicView;

    public MusicModeReceiver(Context context, IMusicView musicView) {
        this.mContext = context;
        this.mIMusicView = musicView;
    }

    public void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ENTER_MUSIC_MODE);
        intentFilter.addAction(BROADCAST_EXIT_MUSIC_MODE);
        this.mContext.registerReceiver(this, intentFilter);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BROADCAST_ENTER_MUSIC_MODE.equals(action)) {
            this.mIMusicView.enterMusicMode();
        } else if (BROADCAST_EXIT_MUSIC_MODE.equals(action)) {
            this.mIMusicView.exitMusicMode();
        }
    }

    public void unRegister() {
    }
}
