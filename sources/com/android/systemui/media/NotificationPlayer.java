package com.android.systemui.media;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlayerBase;
import android.net.Uri;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.xiaopeng.speech.speechwidget.ListWidget;
import java.lang.Thread;
import java.util.LinkedList;
/* loaded from: classes21.dex */
public class NotificationPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private static final boolean DEBUG = false;
    private static final int PLAY = 1;
    private static final int STOP = 2;
    @GuardedBy({"mQueueAudioFocusLock"})
    private AudioManager mAudioManagerWithAudioFocus;
    @GuardedBy({"mCompletionHandlingLock"})
    private CreationAndCompletionThread mCompletionThread;
    @GuardedBy({"mCompletionHandlingLock"})
    private Looper mLooper;
    @GuardedBy({"mPlayerLock"})
    private MediaPlayer mPlayer;
    private String mTag;
    @GuardedBy({"mCmdQueue"})
    private CmdThread mThread;
    @GuardedBy({"mCmdQueue"})
    private PowerManager.WakeLock mWakeLock;
    private final LinkedList<Command> mCmdQueue = new LinkedList<>();
    private final Object mCompletionHandlingLock = new Object();
    private final Object mPlayerLock = new Object();
    private final Object mQueueAudioFocusLock = new Object();
    private int mNotificationRampTimeMs = 0;
    private int mState = 2;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static final class Command {
        AudioAttributes attributes;
        int code;
        Context context;
        boolean looping;
        long requestTime;
        Uri uri;

        private Command() {
        }

        public String toString() {
            return "{ code=" + this.code + " looping=" + this.looping + " attributes=" + this.attributes + " uri=" + this.uri + " }";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class CreationAndCompletionThread extends Thread {
        public Command mCmd;

        public CreationAndCompletionThread(Command cmd) {
            this.mCmd = cmd;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            MediaPlayer mp;
            Looper.prepare();
            NotificationPlayer.this.mLooper = Looper.myLooper();
            MediaPlayer player = null;
            synchronized (this) {
                AudioManager audioManager = (AudioManager) this.mCmd.context.getSystemService(ListWidget.EXTRA_TYPE_AUDIO);
                try {
                    player = new MediaPlayer();
                    if (this.mCmd.attributes == null) {
                        this.mCmd.attributes = new AudioAttributes.Builder().setUsage(5).setContentType(4).build();
                    }
                    player.setAudioAttributes(this.mCmd.attributes);
                    player.setDataSource(this.mCmd.context, this.mCmd.uri);
                    player.setLooping(this.mCmd.looping);
                    player.setOnCompletionListener(NotificationPlayer.this);
                    player.setOnErrorListener(NotificationPlayer.this);
                    player.prepare();
                    if (this.mCmd.uri != null && this.mCmd.uri.getEncodedPath() != null && this.mCmd.uri.getEncodedPath().length() > 0 && !audioManager.isMusicActiveRemotely()) {
                        synchronized (NotificationPlayer.this.mQueueAudioFocusLock) {
                            if (NotificationPlayer.this.mAudioManagerWithAudioFocus == null) {
                                int focusGain = 3;
                                if (this.mCmd.looping) {
                                    focusGain = 1;
                                }
                                NotificationPlayer.this.mNotificationRampTimeMs = audioManager.getFocusRampTimeMs(focusGain, this.mCmd.attributes);
                                audioManager.requestAudioFocus(null, this.mCmd.attributes, focusGain, 0);
                                NotificationPlayer.this.mAudioManagerWithAudioFocus = audioManager;
                            }
                        }
                    }
                    try {
                        Thread.sleep(NotificationPlayer.this.mNotificationRampTimeMs);
                    } catch (InterruptedException e) {
                        Log.e(NotificationPlayer.this.mTag, "Exception while sleeping to sync notification playback with ducking", e);
                    }
                    player.start();
                } catch (Exception e2) {
                    if (0 != 0) {
                        player.release();
                        player = null;
                    }
                    String str = NotificationPlayer.this.mTag;
                    Log.w(str, "error loading sound for " + this.mCmd.uri, e2);
                    NotificationPlayer.this.abandonAudioFocusAfterError();
                }
                synchronized (NotificationPlayer.this.mPlayerLock) {
                    mp = NotificationPlayer.this.mPlayer;
                    NotificationPlayer.this.mPlayer = player;
                }
                if (mp != null) {
                    mp.release();
                }
                notify();
            }
            Looper.loop();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void abandonAudioFocusAfterError() {
        synchronized (this.mQueueAudioFocusLock) {
            if (this.mAudioManagerWithAudioFocus != null) {
                this.mAudioManagerWithAudioFocus.abandonAudioFocus(null);
                this.mAudioManagerWithAudioFocus = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSound(Command cmd) {
        try {
            synchronized (this.mCompletionHandlingLock) {
                if (this.mLooper != null && this.mLooper.getThread().getState() != Thread.State.TERMINATED) {
                    this.mLooper.quit();
                }
                this.mCompletionThread = new CreationAndCompletionThread(cmd);
                synchronized (this.mCompletionThread) {
                    this.mCompletionThread.start();
                    this.mCompletionThread.wait();
                }
            }
            long delay = SystemClock.uptimeMillis() - cmd.requestTime;
            if (delay > 1000) {
                String str = this.mTag;
                Log.w(str, "Notification sound delayed by " + delay + "msecs");
            }
        } catch (Exception e) {
            String str2 = this.mTag;
            Log.w(str2, "error loading sound for " + cmd.uri, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class CmdThread extends Thread {
        CmdThread() {
            super("NotificationPlayer-" + NotificationPlayer.this.mTag);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Command cmd;
            MediaPlayer mp;
            while (true) {
                synchronized (NotificationPlayer.this.mCmdQueue) {
                    cmd = (Command) NotificationPlayer.this.mCmdQueue.removeFirst();
                }
                int i = cmd.code;
                if (i == 1) {
                    NotificationPlayer.this.startSound(cmd);
                } else if (i == 2) {
                    synchronized (NotificationPlayer.this.mPlayerLock) {
                        mp = NotificationPlayer.this.mPlayer;
                        NotificationPlayer.this.mPlayer = null;
                    }
                    if (mp == null) {
                        Log.w(NotificationPlayer.this.mTag, "STOP command without a player");
                    } else {
                        long delay = SystemClock.uptimeMillis() - cmd.requestTime;
                        if (delay > 1000) {
                            String str = NotificationPlayer.this.mTag;
                            Log.w(str, "Notification stop delayed by " + delay + "msecs");
                        }
                        try {
                            mp.stop();
                        } catch (Exception e) {
                        }
                        mp.release();
                        synchronized (NotificationPlayer.this.mQueueAudioFocusLock) {
                            if (NotificationPlayer.this.mAudioManagerWithAudioFocus != null) {
                                NotificationPlayer.this.mAudioManagerWithAudioFocus.abandonAudioFocus(null);
                                NotificationPlayer.this.mAudioManagerWithAudioFocus = null;
                            }
                        }
                        synchronized (NotificationPlayer.this.mCompletionHandlingLock) {
                            if (NotificationPlayer.this.mLooper != null && NotificationPlayer.this.mLooper.getThread().getState() != Thread.State.TERMINATED) {
                                NotificationPlayer.this.mLooper.quit();
                            }
                        }
                    }
                }
                synchronized (NotificationPlayer.this.mCmdQueue) {
                    if (NotificationPlayer.this.mCmdQueue.size() == 0) {
                        NotificationPlayer.this.mThread = null;
                        NotificationPlayer.this.releaseWakeLock();
                        return;
                    }
                }
            }
        }
    }

    @Override // android.media.MediaPlayer.OnCompletionListener
    public void onCompletion(MediaPlayer mp) {
        synchronized (this.mQueueAudioFocusLock) {
            if (this.mAudioManagerWithAudioFocus != null) {
                this.mAudioManagerWithAudioFocus.abandonAudioFocus(null);
                this.mAudioManagerWithAudioFocus = null;
            }
        }
        synchronized (this.mCmdQueue) {
            synchronized (this.mCompletionHandlingLock) {
                if (this.mCmdQueue.size() == 0) {
                    if (this.mLooper != null) {
                        this.mLooper.quit();
                    }
                    this.mCompletionThread = null;
                }
            }
        }
        synchronized (this.mPlayerLock) {
            if (mp == this.mPlayer) {
                this.mPlayer = null;
            }
        }
        if (mp != null) {
            mp.release();
        }
    }

    @Override // android.media.MediaPlayer.OnErrorListener
    public boolean onError(MediaPlayer mp, int what, int extra) {
        String str = this.mTag;
        Log.e(str, "error " + what + " (extra=" + extra + ") playing notification");
        onCompletion(mp);
        return true;
    }

    public NotificationPlayer(String tag) {
        if (tag != null) {
            this.mTag = tag;
        } else {
            this.mTag = "NotificationPlayer";
        }
    }

    @Deprecated
    public void play(Context context, Uri uri, boolean looping, int stream) {
        PlayerBase.deprecateStreamTypeForPlayback(stream, "NotificationPlayer", "play");
        Command cmd = new Command();
        cmd.requestTime = SystemClock.uptimeMillis();
        cmd.code = 1;
        cmd.context = context;
        cmd.uri = uri;
        cmd.looping = looping;
        cmd.attributes = new AudioAttributes.Builder().setInternalLegacyStreamType(stream).build();
        synchronized (this.mCmdQueue) {
            enqueueLocked(cmd);
            this.mState = 1;
        }
    }

    public void play(Context context, Uri uri, boolean looping, AudioAttributes attributes) {
        Command cmd = new Command();
        cmd.requestTime = SystemClock.uptimeMillis();
        cmd.code = 1;
        cmd.context = context;
        cmd.uri = uri;
        cmd.looping = looping;
        cmd.attributes = attributes;
        synchronized (this.mCmdQueue) {
            enqueueLocked(cmd);
            this.mState = 1;
        }
    }

    public void stop() {
        synchronized (this.mCmdQueue) {
            if (this.mState != 2) {
                Command cmd = new Command();
                cmd.requestTime = SystemClock.uptimeMillis();
                cmd.code = 2;
                enqueueLocked(cmd);
                this.mState = 2;
            }
        }
    }

    @GuardedBy({"mCmdQueue"})
    private void enqueueLocked(Command cmd) {
        this.mCmdQueue.add(cmd);
        if (this.mThread == null) {
            acquireWakeLock();
            this.mThread = new CmdThread();
            this.mThread.start();
        }
    }

    public void setUsesWakeLock(Context context) {
        synchronized (this.mCmdQueue) {
            if (this.mWakeLock != null || this.mThread != null) {
                throw new RuntimeException("assertion failed mWakeLock=" + this.mWakeLock + " mThread=" + this.mThread);
            }
            PowerManager pm = (PowerManager) context.getSystemService("power");
            this.mWakeLock = pm.newWakeLock(1, this.mTag);
        }
    }

    @GuardedBy({"mCmdQueue"})
    private void acquireWakeLock() {
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null) {
            wakeLock.acquire();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @GuardedBy({"mCmdQueue"})
    public void releaseWakeLock() {
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null) {
            wakeLock.release();
        }
    }
}
