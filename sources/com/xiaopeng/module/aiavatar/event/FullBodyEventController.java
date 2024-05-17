package com.xiaopeng.module.aiavatar.event;

import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.util.wakelock.WakeLock;
import com.xiaopeng.module.aiavatar.event.AvatarEvents;
import com.xiaopeng.module.aiavatar.helper.GsonHelper;
import com.xiaopeng.module.aiavatar.helper.TextToSpeechHelper;
import com.xiaopeng.module.aiavatar.helper.ThreadUtils;
import com.xiaopeng.module.aiavatar.mvp.avatar.AvatarRootView;
import com.xiaopeng.module.aiavatar.mvp.avatar.SpeechTextView;
import com.xiaopeng.module.aiavatar.mvp.avatar.bean.AvatarBean;
import com.xiaopeng.module.aiavatar.system.EventDispatcherManager;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
/* loaded from: classes23.dex */
public class FullBodyEventController {
    private static final int INTERVAL_EVENT_TIME = 3000;
    private static final int NEXT_INTERVAL_EVENT_TIME = 20000;
    private static final String TAG = "FullBodyEventController";
    private static final int TIMEOUT_EVENT = 28000;
    private static final FullBodyEventController sInstance = new FullBodyEventController();
    private AvatarRootView mAvatarRootView;
    private AvatarEvents.AvatarEvent mDefaultEvent;
    private SpeechTextView mSpeechTextView;
    private final int MAX_CUR_AVATAR_COUNT = 2;
    private final Object LOCK = new Object();
    private Random mRandom = new Random();
    private List<AvatarEvents.AvatarEvent> mAvatarList = new ArrayList();
    private LinkedList<AvatarEvents.AvatarEvent> mCurAvatarList = new LinkedList<>();
    private String[] mTtsList = {"嗨，试着戳一下我，打个招呼吧", "试试说：你好小P，打开冥想模式", "试试说：你好小P，我有点热", "试试说：你好小P，附近的充电站", "试试说：你好小P，播放热门歌曲"};
    private Runnable mEventTask = new Runnable() { // from class: com.xiaopeng.module.aiavatar.event.FullBodyEventController.1
        @Override // java.lang.Runnable
        public void run() {
            synchronized (FullBodyEventController.this.LOCK) {
                final AvatarEvents.AvatarEvent curEvent = FullBodyEventController.this.getCurEvent();
                if (curEvent != null) {
                    String eventJson = GsonHelper.getInstance().getGson().toJson(curEvent);
                    if (!FullBodyEventController.this.mAvatarRootView.isFullBody()) {
                        return;
                    }
                    Log.d(FullBodyEventController.TAG, "dispatch :" + eventJson);
                    EventDispatcherManager.getInstance().dispatch(eventJson);
                    if (FullBodyEventController.this.mSpeechTextView != null) {
                        ThreadUtils.postMain(new Runnable() { // from class: com.xiaopeng.module.aiavatar.event.FullBodyEventController.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                String tts = curEvent.getTts();
                                if (!TextUtils.isEmpty(tts) && curEvent != FullBodyEventController.this.mDefaultEvent && FullBodyEventController.this.mAvatarList.contains(curEvent)) {
                                    FullBodyEventController.this.mSpeechTextView.setText(tts);
                                    if (FullBodyEventController.this.mSpeechTextView.getVisibility() != 0) {
                                        FullBodyEventController.this.mSpeechTextView.show();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    };
    private Runnable mEventTimeOutTask = new Runnable() { // from class: com.xiaopeng.module.aiavatar.event.FullBodyEventController.2
        @Override // java.lang.Runnable
        public void run() {
            FullBodyEventController.this.onEventEnd("");
        }
    };

    public static final FullBodyEventController instance() {
        return sInstance;
    }

    public void pushEvent(AvatarEvents.AvatarEvent avatarEvent) {
        dispatchEvent(avatarEvent, 0L);
    }

    public AvatarEvents.AvatarEvent getCurEvent() {
        if (!this.mCurAvatarList.isEmpty()) {
            return this.mCurAvatarList.getLast();
        }
        return null;
    }

    public void enterFullBody(AvatarBean avatarBean) {
        Log.d(TAG, "enterFullBody");
        this.mCurAvatarList.clear();
        this.mAvatarList.clear();
        this.mAvatarList.add(new AvatarEvents.Default2Event(avatarBean));
        this.mAvatarList.add(new AvatarEvents.Default4Event(avatarBean));
        this.mAvatarList.add(new AvatarEvents.Default5Event(avatarBean));
        this.mAvatarList.add(new AvatarEvents.Default9Event(avatarBean));
        this.mDefaultEvent = new AvatarEvents.Default2Event(avatarBean);
        dispatchEvent(randomAvatarEvent(), 0L);
    }

    public void exitFullBody(AvatarBean avatarBean) {
        synchronized (this.LOCK) {
            this.mAvatarList.clear();
            this.mCurAvatarList.clear();
        }
        Log.d(TAG, "exitFullBody");
        ThreadUtils.removeWorker(this.mEventTask);
        ThreadUtils.removeWorker(this.mEventTimeOutTask);
        TextToSpeechHelper.instance().stop();
        SpeechTextView speechTextView = this.mSpeechTextView;
        if (speechTextView != null) {
            speechTextView.hide();
        }
    }

    public void onEventEnd(String id) {
        AvatarEvents.AvatarEvent avatarEvent;
        AvatarEvents.AvatarEvent curEvent = getCurEvent();
        if (this.mSpeechTextView.getVisibility() == 0) {
            this.mSpeechTextView.hide();
        }
        if (curEvent != this.mDefaultEvent) {
            avatarEvent = this.mDefaultEvent;
        } else {
            avatarEvent = randomAvatarEvent();
        }
        if (avatarEvent != null) {
            dispatchEvent(avatarEvent, WakeLock.DEFAULT_MAX_TIMEOUT);
        }
        if (this.mAvatarRootView != null && !TextUtils.isEmpty(id)) {
            this.mAvatarRootView.setIsRequestRender(false);
        }
    }

    private AvatarEvents.AvatarEvent randomAvatarEvent() {
        AvatarEvents.AvatarEvent avatarEvent = null;
        if (!this.mAvatarList.isEmpty() && this.mAvatarList.size() > 2) {
            do {
                int index = this.mRandom.nextInt(this.mAvatarList.size());
                avatarEvent = this.mAvatarList.get(index);
            } while (isCurAvatar(avatarEvent));
            String[] strArr = this.mTtsList;
            avatarEvent.tts = new String[]{strArr[this.mRandom.nextInt(strArr.length)]};
        }
        return avatarEvent;
    }

    private void dispatchEvent(AvatarEvents.AvatarEvent event, long delay) {
        synchronized (this.LOCK) {
            this.mCurAvatarList.add(event);
            while (this.mCurAvatarList.size() > 2) {
                this.mCurAvatarList.poll();
            }
        }
        if (event == null) {
            return;
        }
        ThreadUtils.removeWorker(this.mEventTask);
        ThreadUtils.removeWorker(this.mEventTimeOutTask);
        ThreadUtils.postWorker(this.mEventTask, delay);
        ThreadUtils.postWorker(this.mEventTimeOutTask, 28000L);
    }

    private boolean isCurAvatar(AvatarEvents.AvatarEvent avatarEvent) {
        for (int i = 0; i < this.mCurAvatarList.size(); i++) {
            if (avatarEvent == this.mCurAvatarList.get(i)) {
                return true;
            }
        }
        return false;
    }

    public SpeechTextView getSpeechTextView() {
        return this.mSpeechTextView;
    }

    public void setSpeechTextView(SpeechTextView speechTextView) {
        this.mSpeechTextView = speechTextView;
    }

    public void setAvatarRootView(AvatarRootView avatarRootView) {
        this.mAvatarRootView = avatarRootView;
    }
}
