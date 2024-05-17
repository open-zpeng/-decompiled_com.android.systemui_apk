package com.android.systemui.pip.phone;

import android.app.IActivityManager;
import android.app.PendingIntent;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.pip.phone.PipMediaController;
import com.android.systemui.statusbar.policy.UserInfoController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class PipMediaController {
    private static final String ACTION_NEXT = "com.android.systemui.pip.phone.NEXT";
    private static final String ACTION_PAUSE = "com.android.systemui.pip.phone.PAUSE";
    private static final String ACTION_PLAY = "com.android.systemui.pip.phone.PLAY";
    private static final String ACTION_PREV = "com.android.systemui.pip.phone.PREV";
    private final IActivityManager mActivityManager;
    private final Context mContext;
    private MediaController mMediaController;
    private final MediaSessionManager mMediaSessionManager;
    private RemoteAction mNextAction;
    private RemoteAction mPauseAction;
    private RemoteAction mPlayAction;
    private RemoteAction mPrevAction;
    private BroadcastReceiver mPlayPauseActionReceiver = new BroadcastReceiver() { // from class: com.android.systemui.pip.phone.PipMediaController.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(PipMediaController.ACTION_PLAY)) {
                PipMediaController.this.mMediaController.getTransportControls().play();
            } else if (action.equals(PipMediaController.ACTION_PAUSE)) {
                PipMediaController.this.mMediaController.getTransportControls().pause();
            } else if (action.equals(PipMediaController.ACTION_NEXT)) {
                PipMediaController.this.mMediaController.getTransportControls().skipToNext();
            } else if (action.equals(PipMediaController.ACTION_PREV)) {
                PipMediaController.this.mMediaController.getTransportControls().skipToPrevious();
            }
        }
    };
    private final MediaController.Callback mPlaybackChangedListener = new MediaController.Callback() { // from class: com.android.systemui.pip.phone.PipMediaController.2
        @Override // android.media.session.MediaController.Callback
        public void onPlaybackStateChanged(PlaybackState state) {
            PipMediaController.this.notifyActionsChanged();
        }
    };
    private final MediaSessionManager.OnActiveSessionsChangedListener mSessionsChangedListener = new MediaSessionManager.OnActiveSessionsChangedListener() { // from class: com.android.systemui.pip.phone.PipMediaController.3
        @Override // android.media.session.MediaSessionManager.OnActiveSessionsChangedListener
        public void onActiveSessionsChanged(List<MediaController> controllers) {
            PipMediaController.this.resolveActiveMediaController(controllers);
        }
    };
    private ArrayList<ActionListener> mListeners = new ArrayList<>();

    /* loaded from: classes21.dex */
    public interface ActionListener {
        void onMediaActionsChanged(List<RemoteAction> list);
    }

    public PipMediaController(Context context, IActivityManager activityManager) {
        this.mContext = context;
        this.mActivityManager = activityManager;
        IntentFilter mediaControlFilter = new IntentFilter();
        mediaControlFilter.addAction(ACTION_PLAY);
        mediaControlFilter.addAction(ACTION_PAUSE);
        mediaControlFilter.addAction(ACTION_NEXT);
        mediaControlFilter.addAction(ACTION_PREV);
        this.mContext.registerReceiver(this.mPlayPauseActionReceiver, mediaControlFilter);
        createMediaActions();
        this.mMediaSessionManager = (MediaSessionManager) context.getSystemService("media_session");
        UserInfoController userInfoController = (UserInfoController) Dependency.get(UserInfoController.class);
        userInfoController.addCallback(new UserInfoController.OnUserInfoChangedListener() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMediaController$neOVZxIcmRkhimcM6huwsIEiXEw
            @Override // com.android.systemui.statusbar.policy.UserInfoController.OnUserInfoChangedListener
            public final void onUserInfoChanged(String str, Drawable drawable, String str2) {
                PipMediaController.this.lambda$new$0$PipMediaController(str, drawable, str2);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$PipMediaController(String name, Drawable picture, String userAccount) {
        registerSessionListenerForCurrentUser();
    }

    public void onActivityPinned() {
        resolveActiveMediaController(this.mMediaSessionManager.getActiveSessionsForUser(null, -2));
    }

    public void addListener(ActionListener listener) {
        if (!this.mListeners.contains(listener)) {
            this.mListeners.add(listener);
            listener.onMediaActionsChanged(getMediaActions());
        }
    }

    public void removeListener(ActionListener listener) {
        listener.onMediaActionsChanged(Collections.EMPTY_LIST);
        this.mListeners.remove(listener);
    }

    private List<RemoteAction> getMediaActions() {
        MediaController mediaController = this.mMediaController;
        if (mediaController == null || mediaController.getPlaybackState() == null) {
            return Collections.EMPTY_LIST;
        }
        ArrayList<RemoteAction> mediaActions = new ArrayList<>();
        int state = this.mMediaController.getPlaybackState().getState();
        boolean isPlaying = MediaSession.isActiveState(state);
        long actions = this.mMediaController.getPlaybackState().getActions();
        this.mPrevAction.setEnabled((16 & actions) != 0);
        mediaActions.add(this.mPrevAction);
        if (!isPlaying && (4 & actions) != 0) {
            mediaActions.add(this.mPlayAction);
        } else if (isPlaying && (2 & actions) != 0) {
            mediaActions.add(this.mPauseAction);
        }
        this.mNextAction.setEnabled((32 & actions) != 0);
        mediaActions.add(this.mNextAction);
        return mediaActions;
    }

    private void createMediaActions() {
        String pauseDescription = this.mContext.getString(R.string.pip_pause);
        this.mPauseAction = new RemoteAction(Icon.createWithResource(this.mContext, R.drawable.ic_pause_white), pauseDescription, pauseDescription, PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_PAUSE), 134217728));
        String playDescription = this.mContext.getString(R.string.pip_play);
        this.mPlayAction = new RemoteAction(Icon.createWithResource(this.mContext, R.drawable.ic_play_arrow_white), playDescription, playDescription, PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_PLAY), 134217728));
        String nextDescription = this.mContext.getString(R.string.pip_skip_to_next);
        this.mNextAction = new RemoteAction(Icon.createWithResource(this.mContext, R.drawable.ic_skip_next_white), nextDescription, nextDescription, PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_NEXT), 134217728));
        String prevDescription = this.mContext.getString(R.string.pip_skip_to_prev);
        this.mPrevAction = new RemoteAction(Icon.createWithResource(this.mContext, R.drawable.ic_skip_previous_white), prevDescription, prevDescription, PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_PREV), 134217728));
    }

    private void registerSessionListenerForCurrentUser() {
        this.mMediaSessionManager.removeOnActiveSessionsChangedListener(this.mSessionsChangedListener);
        this.mMediaSessionManager.addOnActiveSessionsChangedListener(this.mSessionsChangedListener, null, -2, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resolveActiveMediaController(List<MediaController> controllers) {
        ComponentName topActivity;
        if (controllers != null && (topActivity = (ComponentName) PipUtils.getTopPinnedActivity(this.mContext, this.mActivityManager).first) != null) {
            for (int i = 0; i < controllers.size(); i++) {
                MediaController controller = controllers.get(i);
                if (controller.getPackageName().equals(topActivity.getPackageName())) {
                    setActiveMediaController(controller);
                    return;
                }
            }
        }
        setActiveMediaController(null);
    }

    private void setActiveMediaController(MediaController controller) {
        MediaController mediaController = this.mMediaController;
        if (controller != mediaController) {
            if (mediaController != null) {
                mediaController.unregisterCallback(this.mPlaybackChangedListener);
            }
            this.mMediaController = controller;
            if (controller != null) {
                controller.registerCallback(this.mPlaybackChangedListener);
            }
            notifyActionsChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyActionsChanged() {
        if (!this.mListeners.isEmpty()) {
            final List<RemoteAction> actions = getMediaActions();
            this.mListeners.forEach(new Consumer() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMediaController$PGZH9Rcf3EMC5cibv13aaStfc2E
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((PipMediaController.ActionListener) obj).onMediaActionsChanged(actions);
                }
            });
        }
    }
}
