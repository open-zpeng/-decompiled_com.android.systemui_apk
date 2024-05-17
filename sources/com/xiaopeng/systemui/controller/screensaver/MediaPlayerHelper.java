package com.xiaopeng.systemui.controller.screensaver;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import com.xiaopeng.systemui.Logger;
import java.io.File;
import java.io.IOException;
/* loaded from: classes24.dex */
public class MediaPlayerHelper {
    private static final String TAG = "MediaPlayerHelper";
    private static volatile MediaPlayerHelper instance;
    private final Context mContext;
    private MediaPlayer mMediaPlayer;
    private String mPath;

    public static MediaPlayerHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (MediaPlayerHelper.class) {
                if (instance == null) {
                    instance = new MediaPlayerHelper(context);
                }
            }
        }
        return instance;
    }

    private MediaPlayerHelper(Context context) {
        this.mContext = context;
    }

    public void setPlay(SurfaceHolder surfaceHolder, String path) {
        this.mPath = path;
        File file = new File(this.mPath);
        Logger.d(TAG, "file name=" + file.getName());
        if (this.mMediaPlayer != null) {
            stop();
        }
        if (this.mMediaPlayer == null) {
            this.mMediaPlayer = new MediaPlayer();
        }
        this.mMediaPlayer.reset();
        Logger.d(TAG, "reset player");
        this.mMediaPlayer.setAudioStreamType(3);
        this.mMediaPlayer.setDisplay(surfaceHolder);
        this.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.xiaopeng.systemui.controller.screensaver.-$$Lambda$MediaPlayerHelper$zASVabGqEW90YZpJ-4pL2Ktt50c
            @Override // android.media.MediaPlayer.OnPreparedListener
            public final void onPrepared(MediaPlayer mediaPlayer) {
                MediaPlayerHelper.lambda$setPlay$0(mediaPlayer);
            }
        });
        this.mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() { // from class: com.xiaopeng.systemui.controller.screensaver.-$$Lambda$MediaPlayerHelper$LU9MhBMrQqsSch1gJh4YVCCKn-Q
            @Override // android.media.MediaPlayer.OnInfoListener
            public final boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
                return MediaPlayerHelper.lambda$setPlay$1(mediaPlayer, i, i2);
            }
        });
        this.mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.xiaopeng.systemui.controller.screensaver.-$$Lambda$MediaPlayerHelper$9LKZkJIUTHXiNOZxvsTOEly2vJQ
            @Override // android.media.MediaPlayer.OnErrorListener
            public final boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                return MediaPlayerHelper.this.lambda$setPlay$2$MediaPlayerHelper(mediaPlayer, i, i2);
            }
        });
        this.mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() { // from class: com.xiaopeng.systemui.controller.screensaver.-$$Lambda$MediaPlayerHelper$7VbIf_wmwQ_sicXQt4QGfnfWrV0
            @Override // android.media.MediaPlayer.OnBufferingUpdateListener
            public final void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                Logger.d(MediaPlayerHelper.TAG, "on buffering update,percent=" + i);
            }
        });
        try {
            Logger.d(TAG, "set data source:" + this.mPath);
            this.mMediaPlayer.setDataSource(this.mPath);
            this.mMediaPlayer.setLooping(true);
            Logger.d(TAG, "prepare...");
            this.mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            Logger.e(TAG, "set datasource e=" + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$setPlay$0(MediaPlayer mp) {
        Logger.d(TAG, "on prepared");
        mp.start();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$setPlay$1(MediaPlayer mp, int what, int extra) {
        Logger.d(TAG, "on info,what=" + what + ",extra=" + extra);
        return true;
    }

    public /* synthetic */ boolean lambda$setPlay$2$MediaPlayerHelper(MediaPlayer mp, int what, int extra) {
        try {
            Logger.d(TAG, "on error,what=" + what + ",extra=" + extra);
            mp.reset();
            mp.setDataSource(this.mPath);
            this.mMediaPlayer.setLooping(true);
            mp.prepareAsync();
        } catch (IOException e) {
            Logger.e(TAG, "set datasource e=" + e);
        }
        return true;
    }

    public String getPath() {
        return this.mPath;
    }

    public void start() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer == null || mediaPlayer.isPlaying()) {
            return;
        }
        this.mMediaPlayer.start();
        Logger.d(TAG, "start");
    }

    public boolean isPlaying() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void stop() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            Logger.d(TAG, "stop");
        }
    }
}
