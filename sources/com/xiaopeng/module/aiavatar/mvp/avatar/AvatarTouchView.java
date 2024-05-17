package com.xiaopeng.module.aiavatar.mvp.avatar;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.xiaopeng.module.aiavatar.event.AvatarEvents;
import com.xiaopeng.module.aiavatar.event.FullBodyEventController;
import com.xiaopeng.module.aiavatar.helper.TextToSpeechHelper;
import com.xiaopeng.module.aiavatar.mvp.avatar.bean.AvatarBean;
import com.xiaopeng.module.aiavatar.system.EventDispatcherManager;
/* loaded from: classes23.dex */
public class AvatarTouchView extends ViewGroup implements View.OnClickListener {
    private static final String TAG = "AvatarTouchView";
    private int clickCount;
    private AvatarRootView mAvatarRootView;
    private View mBody;
    private Runnable mClickReset;
    private View mEarsLeft;
    private View mEarsRight;
    private View mHalfHead;
    private View mHandLeft;
    private View mHandRight;
    private View mHead;
    private boolean mIsDefaultSkin;
    private View mLegLeft;
    private View mLegRight;
    private View mRightBottom;
    private int mRightBottomClickCount;
    private Runnable mRightBottomClickReset;
    private SpeechTextView mSpeechTextView;

    public AvatarTouchView(Context context) {
        super(context);
        this.mClickReset = new Runnable() { // from class: com.xiaopeng.module.aiavatar.mvp.avatar.AvatarTouchView.1
            @Override // java.lang.Runnable
            public void run() {
                AvatarTouchView.this.clickCount = 0;
            }
        };
        this.mRightBottomClickReset = new Runnable() { // from class: com.xiaopeng.module.aiavatar.mvp.avatar.AvatarTouchView.2
            @Override // java.lang.Runnable
            public void run() {
                AvatarTouchView.this.mRightBottomClickCount = 0;
            }
        };
        init();
    }

    public AvatarTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mClickReset = new Runnable() { // from class: com.xiaopeng.module.aiavatar.mvp.avatar.AvatarTouchView.1
            @Override // java.lang.Runnable
            public void run() {
                AvatarTouchView.this.clickCount = 0;
            }
        };
        this.mRightBottomClickReset = new Runnable() { // from class: com.xiaopeng.module.aiavatar.mvp.avatar.AvatarTouchView.2
            @Override // java.lang.Runnable
            public void run() {
                AvatarTouchView.this.mRightBottomClickCount = 0;
            }
        };
        init();
    }

    private void init() {
        this.mHalfHead = new View(getContext());
        addView(this.mHalfHead, new TouchViewLayoutParams(0.33f, 0.14f, 0.67f, 0.33f));
        this.mHalfHead.setOnClickListener(this);
        this.mHead = new View(getContext());
        addView(this.mHead, new TouchViewLayoutParams(0.35f, 0.29f, 0.67f, 0.47f));
        this.mHead.setOnClickListener(this);
        this.mEarsLeft = new View(getContext());
        addView(this.mEarsLeft, new TouchViewLayoutParams(0.25f, 0.32f, 0.38f, 0.44f));
        this.mEarsLeft.setOnClickListener(this);
        this.mEarsRight = new View(getContext());
        addView(this.mEarsRight, new TouchViewLayoutParams(0.64f, 0.32f, 0.78f, 0.44f));
        this.mEarsRight.setOnClickListener(this);
        this.mBody = new View(getContext());
        addView(this.mBody, new TouchViewLayoutParams(0.4f, 0.48f, 0.65f, 0.68f));
        this.mBody.setOnClickListener(this);
        this.mLegLeft = new View(getContext());
        addView(this.mLegLeft, new TouchViewLayoutParams(0.35f, 0.68f, 0.49f, 0.95f));
        this.mLegLeft.setOnClickListener(this);
        this.mLegRight = new View(getContext());
        addView(this.mLegRight, new TouchViewLayoutParams(0.5f, 0.68f, 0.65f, 0.95f));
        this.mLegRight.setOnClickListener(this);
        this.mHandLeft = new View(getContext());
        addView(this.mHandLeft, new TouchViewLayoutParams(0.26f, 0.46f, 0.42f, 0.72f));
        this.mHandLeft.setOnClickListener(this);
        this.mHandRight = new View(getContext());
        addView(this.mHandRight, new TouchViewLayoutParams(0.62f, 0.46f, 0.76f, 0.72f));
        this.mHandRight.setOnClickListener(this);
        this.mRightBottom = new View(getContext());
        addView(this.mRightBottom, new TouchViewLayoutParams(0.8f, 0.7f, 1.0f, 1.0f));
        this.mRightBottom.setOnClickListener(this);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int w = r - l;
        int h = b - t;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            ViewGroup.LayoutParams lp = child.getLayoutParams();
            if (lp instanceof TouchViewLayoutParams) {
                TouchViewLayoutParams touchLp = (TouchViewLayoutParams) lp;
                child.layout((int) (touchLp.mLeft * w), (int) (touchLp.mTop * h), (int) (touchLp.mRight * w), (int) (touchLp.mBottom * h));
            }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (isFullStatus()) {
            onFullStatusClick(v);
        } else {
            onHalfStatusClick(v);
        }
    }

    private void onHalfStatusClick(View v) {
        if (v == this.mHalfHead) {
            Log.i(TAG, "Touch head");
            this.mAvatarRootView.onAvatarClick();
        }
    }

    private void onFullStatusClick(View v) {
        this.clickCount++;
        removeCallbacks(this.mClickReset);
        postDelayed(this.mClickReset, 500L);
        this.mAvatarRootView.onAvatarClick();
        AvatarEvents.AvatarEvent curEvent = FullBodyEventController.instance().getCurEvent();
        if (curEvent == null) {
            return;
        }
        if (this.clickCount == 5) {
            Log.i(TAG, "Touch tiaowu");
            AvatarEvents.Default3Event avatarEvent = new AvatarEvents.Default3Event(curEvent);
            pustEvent(avatarEvent);
        } else if (v == this.mHead) {
            Log.i(TAG, "Touch head");
            AvatarEvents.HeadEvent avatarEvent2 = new AvatarEvents.HeadEvent(curEvent);
            pustEvent(avatarEvent2);
        } else if (v == this.mBody) {
            Log.i(TAG, "Touch body");
            AvatarEvents.BodyEvent avatarEvent3 = new AvatarEvents.BodyEvent(curEvent);
            pustEvent(avatarEvent3);
        } else if (v == this.mEarsLeft || v == this.mEarsRight) {
            Log.i(TAG, "Touch ears");
            AvatarEvents.EarsEvent avatarEvent4 = new AvatarEvents.EarsEvent(curEvent);
            pustEvent(avatarEvent4);
        } else if (v == this.mHandLeft || v == this.mHandRight) {
            Log.i(TAG, "Touch hand");
            AvatarEvents.HandEvent avatarEvent5 = new AvatarEvents.HandEvent(curEvent);
            pustEvent(avatarEvent5);
        } else if (v == this.mLegLeft || v == this.mLegRight) {
            Log.i(TAG, "Touch leg");
            AvatarEvents.LegEvent avatarEvent6 = new AvatarEvents.LegEvent(curEvent);
            pustEvent(avatarEvent6);
        } else if (v == this.mRightBottom) {
            Log.i(TAG, "Touch right bottom");
            removeCallbacks(this.mRightBottomClickReset);
            this.mRightBottomClickCount++;
            if (this.mRightBottomClickCount >= 3) {
                this.mRightBottomClickCount = 0;
                AvatarBean avatarBean = new AvatarBean();
                AvatarBean.Skin skin = new AvatarBean.Skin();
                this.mIsDefaultSkin = !this.mIsDefaultSkin;
                if (this.mIsDefaultSkin) {
                    skin.halfDay = "model/mapalbedob.png";
                    skin.FullDay = "model/mapalbedo.png";
                } else {
                    skin.halfDay = "model/map_cloth_sport_B.png";
                    skin.FullDay = "model/map_cloth_sport_A.png";
                }
                avatarBean.skin = skin;
                EventDispatcherManager.getInstance().dispatch(new Gson().toJson(avatarBean));
                return;
            }
            postDelayed(this.mRightBottomClickReset, 800L);
        }
    }

    private void pustEvent(AvatarEvents.AvatarEvent avatarEvent) {
        FullBodyEventController.instance().pushEvent(avatarEvent);
        String tts = avatarEvent.getTts();
        if (!TextUtils.isEmpty(tts)) {
            TextToSpeechHelper.instance().speak(getContext(), tts, null);
            this.mSpeechTextView.setText(tts);
            this.mSpeechTextView.show();
        }
    }

    public void setSpeakCallback(SpeechTextView speakCallback) {
        this.mSpeechTextView = speakCallback;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes23.dex */
    public class TouchViewLayoutParams extends ViewGroup.LayoutParams {
        float mBottom;
        float mLeft;
        float mRight;
        float mTop;

        public TouchViewLayoutParams(float left, float top, float right, float bottom) {
            super((int) (right - left), (int) (bottom - top));
            this.mLeft = left;
            this.mTop = top;
            this.mRight = right;
            this.mBottom = bottom;
        }
    }

    public void setRootView(AvatarRootView rootView) {
        this.mAvatarRootView = rootView;
    }

    public boolean isFullStatus() {
        AvatarRootView avatarRootView = this.mAvatarRootView;
        if (avatarRootView != null && avatarRootView.isFullBody()) {
            return true;
        }
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isClickable()) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }
}
