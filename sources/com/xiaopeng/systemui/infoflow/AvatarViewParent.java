package com.xiaopeng.systemui.infoflow;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.android.systemui.R;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.infoflow.helper.AIAvatarViewServiceHelper;
import com.xiaopeng.systemui.infoflow.helper.AnimationHelper;
import com.xiaopeng.systemui.infoflow.manager.MediaManager;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.speech.SpeechRootView;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.infoflow.widget.IFocusView;
import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
import java.util.List;
/* loaded from: classes24.dex */
public class AvatarViewParent extends RelativeLayout implements MediaManager.OnPlayStatusChangedListener {
    private static final String TAG = "AvatarViewParent";
    public static int TYPE_ACTION_MUSIC_PLAY = 0;
    private final long AVATAR_LONG_CLICKTIME;
    private final int AVATAR_STATE_SPEAKING;
    private final int DISMISS_DELAY;
    private final int DURATION_ANIMATOR;
    private final int DURATION_PATH_ANIMATION;
    private final int MSG_DISMISS_AVATAR_SHOT;
    private final int TYPE_ACTION_DANCE;
    private final int TYPE_ACTION_MUSIC;
    private boolean inBackAnimation;
    private boolean inSpeechMode;
    private boolean isDialogStarted;
    private AnimationHelper mAnimationHelper;
    private boolean mAvatarLongClick;
    private long mAvatarLongClickStartTime;
    private int mAvatarMoveOffsetX;
    private int mAvatarMoveOffsetY;
    private RelativeLayout mAvatarProxyView;
    private Handler mAvatarShotHandler;
    private Drawable mAvatarSnapshotDrawable;
    private ImageView mAvatarSnapshotView;
    private int mAvatarState;
    private AvatarViewParentContainer mAvatarViewParentContainer;
    private int mCardContentMarginLeft;
    private float[] mCurrentPosition;
    private int mCurrentX;
    private int mCurrentY;
    private ValueAnimator mHighTopMarginAnimator;
    private int mInfoFlowWidth;
    private int mInfoflowMarginTopNormal;
    private int mInfoflowMarginTopSmall;
    private boolean mIsMusicFocused;
    private int mMaxAvatarShotLeftParams;
    private MediaInfo mMediaInfo;
    private int mMediaState;
    private MessageViewParent mMessageViewParent;
    private int mMinAvatarShotLeftParams;
    private Rect mMusicCardRect;
    private IFocusView mMusicView;
    private int mNavBarW;
    private PathMeasure mPathMeasure;
    private float mScaleValue;
    private Rect mSmallAvatarBounds;
    private int mSmallAvatarMarginLeft;
    private int mSmallAvatarMarginTop;
    private int mSmallAvatarSize;
    private ValueAnimator mSmallTopMarginAnimator;

    public AvatarViewParent(Context context) {
        this(context, null);
    }

    public AvatarViewParent(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AvatarViewParent(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.inSpeechMode = false;
        this.DURATION_ANIMATOR = 500;
        this.isDialogStarted = false;
        this.mMusicCardRect = new Rect(-1, -1, -1, -1);
        this.mIsMusicFocused = false;
        this.AVATAR_LONG_CLICKTIME = 600L;
        this.mAvatarLongClick = false;
        this.TYPE_ACTION_DANCE = 0;
        this.TYPE_ACTION_MUSIC = 1;
        this.mCurrentPosition = new float[2];
        this.DURATION_PATH_ANIMATION = 250;
        this.inBackAnimation = false;
        this.mScaleValue = 1.15f;
        this.AVATAR_STATE_SPEAKING = 1;
        this.MSG_DISMISS_AVATAR_SHOT = 17;
        this.DISMISS_DELAY = 1000;
        this.mAvatarShotHandler = new Handler(Looper.getMainLooper()) { // from class: com.xiaopeng.systemui.infoflow.AvatarViewParent.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 17) {
                    Log.w(AvatarViewParent.TAG, "handleMessage dismiss avatar shot");
                    AvatarViewParent.this.dismissAvatarSnapshotView();
                }
            }
        };
        init(context);
    }

    private void init(Context context) {
        this.mAnimationHelper = new AnimationHelper(context);
        this.mInfoflowMarginTopNormal = getResources().getDimensionPixelSize(R.dimen.rootview_margin_top);
        this.mInfoflowMarginTopSmall = getResources().getDimensionPixelSize(R.dimen.rootview_margin_top_small);
        this.mSmallAvatarSize = getResources().getDimensionPixelSize(R.dimen.infoflow_small_avatar_size);
        this.mSmallAvatarMarginLeft = getResources().getDimensionPixelSize(R.dimen.infoflow_small_avatar_margin_left);
        this.mSmallAvatarMarginTop = getResources().getDimensionPixelSize(R.dimen.infoflow_small_avatar_margin_top);
        this.mInfoFlowWidth = getResources().getDimensionPixelSize(R.dimen.infoflow_small_avatar_move_max_left);
        this.mNavBarW = getResources().getDimensionPixelSize(R.dimen.navbar_item_width);
        this.mCardContentMarginLeft = getResources().getDimensionPixelSize(R.dimen.infoflow_card_content_margin_left);
        int i = this.mSmallAvatarMarginLeft;
        int i2 = this.mSmallAvatarMarginTop;
        int i3 = this.mSmallAvatarSize;
        this.mSmallAvatarBounds = new Rect(i, i2, i + i3, i3 + i2);
        this.mMaxAvatarShotLeftParams = this.mInfoFlowWidth - this.mCardContentMarginLeft;
        this.mMinAvatarShotLeftParams = getResources().getDimensionPixelSize(R.dimen.infoflow_min_avatar_margin_left);
    }

    public void setupWithViewContainer(AvatarViewParentContainer container) {
        this.mAvatarViewParentContainer = container;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mAvatarProxyView = (RelativeLayout) findViewById(R.id.stub_view_avatar);
        this.mMessageViewParent = (MessageViewParent) findViewById(R.id.view_immerse);
        MediaManager.getInstance().addOnPlayStatusChangedListener(this);
        this.mAvatarProxyView.setOnTouchListener(new View.OnTouchListener() { // from class: com.xiaopeng.systemui.infoflow.AvatarViewParent.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                AvatarViewParent.this.moveAvatarSnapshotView(motionEvent);
                if (!AvatarViewParent.this.mAvatarLongClick) {
                    AvatarViewParent.this.mAvatarViewParentContainer.notifyAvatarMotionEvent(motionEvent);
                    return true;
                }
                return true;
            }
        });
    }

    public void enterSpeechMode(final SpeechRootView speechRootView) {
        Logger.d(TAG, "enterSpeechMode() called :" + this.inSpeechMode);
        if (!this.inSpeechMode) {
            this.inSpeechMode = true;
            this.mAnimationHelper.hideCard(this.mMessageViewParent, new AnimationHelper.CardAnimationListener() { // from class: com.xiaopeng.systemui.infoflow.-$$Lambda$AvatarViewParent$c94i9h5MSWa63tw4s_af7Jbn4qk
                @Override // com.xiaopeng.systemui.infoflow.helper.AnimationHelper.CardAnimationListener
                public final void onAnimationEnd() {
                    AvatarViewParent.this.lambda$enterSpeechMode$0$AvatarViewParent(speechRootView);
                }
            }, false);
        }
    }

    public /* synthetic */ void lambda$enterSpeechMode$0$AvatarViewParent(SpeechRootView speechRootView) {
        if (this.inSpeechMode && speechRootView != null) {
            this.mAnimationHelper.showCard(speechRootView);
            this.mMessageViewParent.setVisibility(8);
        }
    }

    public void exitSpeechMode(SpeechRootView speechRootView) {
        Logger.d(TAG, "exitSpeechMode");
        if (this.inSpeechMode) {
            this.inSpeechMode = false;
            if (speechRootView != null) {
                this.mAnimationHelper.hideCard(speechRootView, new AnimationHelper.CardAnimationListener() { // from class: com.xiaopeng.systemui.infoflow.-$$Lambda$AvatarViewParent$e2udunEsnr4qTOVbAqk9lUs0VFc
                    @Override // com.xiaopeng.systemui.infoflow.helper.AnimationHelper.CardAnimationListener
                    public final void onAnimationEnd() {
                        AvatarViewParent.this.lambda$exitSpeechMode$1$AvatarViewParent();
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$exitSpeechMode$1$AvatarViewParent() {
        this.mAnimationHelper.destroyAnimation();
        PresenterCenter.getInstance().getInfoFlow().checkToShowMessageViewGroup();
    }

    public void onWidgetCancel() {
        Logger.d(TAG, "onWidgetCancel");
        this.mAnimationHelper.destroyAnimation();
        MessageViewParent messageViewParent = this.mMessageViewParent;
        if (messageViewParent != null) {
            messageViewParent.setVisibility(0);
        }
    }

    public void onDialogStart() {
        Log.w(TAG, "onDialogStart");
        this.isDialogStarted = true;
        backToAvatarStartPoint(this.mCurrentX + this.mSmallAvatarMarginLeft, this.mCurrentY);
        AIAvatarViewServiceHelper.instance().updateDialogStatus(true);
    }

    public void onDialogEnd(DialogEndReason endReason) {
        Log.w(TAG, "onDialogEnd");
        if (this.isDialogStarted) {
            AIAvatarViewServiceHelper.instance().updateDialogStatus(false);
        }
        this.isDialogStarted = false;
    }

    public void startHighTopMarginAnimation() {
        ValueAnimator valueAnimator = this.mHighTopMarginAnimator;
        if (valueAnimator == null) {
            initHighTopMarginAnimator();
        } else if (valueAnimator.isRunning()) {
            return;
        }
        ValueAnimator valueAnimator2 = this.mSmallTopMarginAnimator;
        if (valueAnimator2 != null && valueAnimator2.isRunning()) {
            this.mSmallTopMarginAnimator.cancel();
        }
        MessageViewParent messageViewParent = this.mMessageViewParent;
        if (messageViewParent != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) messageViewParent.getLayoutParams();
            Logger.d(TAG, "startHighTopMarginAnimation current topMargin" + layoutParams.topMargin);
            if (layoutParams.topMargin != this.mInfoflowMarginTopNormal) {
                this.mHighTopMarginAnimator.start();
            }
        }
    }

    public void startSmallTopMarginAnimation() {
        ValueAnimator valueAnimator = this.mSmallTopMarginAnimator;
        if (valueAnimator == null) {
            initSmallTopMarginAnimator();
        } else if (valueAnimator.isRunning()) {
            return;
        }
        ValueAnimator valueAnimator2 = this.mHighTopMarginAnimator;
        if (valueAnimator2 != null && valueAnimator2.isRunning()) {
            this.mHighTopMarginAnimator.cancel();
        }
        MessageViewParent messageViewParent = this.mMessageViewParent;
        if (messageViewParent != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) messageViewParent.getLayoutParams();
            Logger.d(TAG, "startSmallTopMarginAnimation current topMargin" + layoutParams.topMargin);
            if (layoutParams.topMargin != this.mInfoflowMarginTopSmall) {
                this.mSmallTopMarginAnimator.start();
            }
        }
    }

    private void initHighTopMarginAnimator() {
        this.mHighTopMarginAnimator = ValueAnimator.ofInt(this.mInfoflowMarginTopSmall, this.mInfoflowMarginTopNormal);
        this.mHighTopMarginAnimator.setDuration(500L);
        this.mHighTopMarginAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.AvatarViewParent.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                AvatarViewParent.this.updataMarginLayoutParams(((Integer) valueAnimator.getAnimatedValue()).intValue());
            }
        });
    }

    private void initSmallTopMarginAnimator() {
        this.mSmallTopMarginAnimator = ValueAnimator.ofInt(this.mInfoflowMarginTopNormal, this.mInfoflowMarginTopSmall);
        this.mSmallTopMarginAnimator.setDuration(500L);
        this.mSmallTopMarginAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.AvatarViewParent.4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                AvatarViewParent.this.updataMarginLayoutParams(((Integer) valueAnimator.getAnimatedValue()).intValue());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updataMarginLayoutParams(int marginTop) {
        MessageViewParent messageViewParent = this.mMessageViewParent;
        if (messageViewParent != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) messageViewParent.getLayoutParams();
            layoutParams.topMargin = marginTop;
            this.mMessageViewParent.setLayoutParams(layoutParams);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveAvatarSnapshotView(MotionEvent motionEvent) {
        if (this.isDialogStarted) {
            return;
        }
        int x = ((int) motionEvent.getX()) + this.mNavBarW;
        int y = (int) motionEvent.getY();
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mAvatarShotHandler.removeMessages(17);
            this.inBackAnimation = false;
            this.mAvatarLongClick = false;
            this.mAvatarLongClickStartTime = System.currentTimeMillis();
            return;
        }
        if (action != 1) {
            if (action == 2) {
                if (!this.inBackAnimation) {
                    if (!this.mAvatarLongClick) {
                        checkAvatarLongClick(x, y);
                    } else {
                        showAvatarSnapshotView(x, y);
                    }
                }
                this.mAvatarShotHandler.removeMessages(17);
                this.mAvatarShotHandler.sendEmptyMessageDelayed(17, 1000L);
                return;
            } else if (action != 3) {
                return;
            }
        }
        this.mAvatarShotHandler.removeMessages(17);
        if (!this.inBackAnimation) {
            if (this.mIsMusicFocused) {
                notifyAvatarAction(0, null);
            }
            dismissAvatarSnapshotView();
        }
        post(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.AvatarViewParent.5
            @Override // java.lang.Runnable
            public void run() {
                AvatarViewParent.this.mAvatarLongClick = false;
            }
        });
    }

    private void checkAvatarLongClick(int x, int y) {
        long time = System.currentTimeMillis();
        long timeLong = time - this.mAvatarLongClickStartTime;
        boolean inFullAvatarMode = PresenterCenter.getInstance().getInfoFlow().isInFullAvatarMode();
        if (timeLong >= 600 && this.mSmallAvatarBounds.contains(x, y) && !inFullAvatarMode && this.mAvatarState != 1) {
            this.mAvatarLongClick = true;
            onStatusChanged(this.mMediaState);
            return;
        }
        this.mAvatarLongClick = false;
    }

    private void initAvatarSnapshotView() {
        if (this.mAvatarSnapshotView == null) {
            this.mAvatarSnapshotView = new ImageView(getContext());
            Drawable drawable = this.mAvatarSnapshotDrawable;
            if (drawable != null) {
                this.mAvatarSnapshotView.setImageDrawable(drawable);
            } else {
                this.mAvatarSnapshotView.setImageResource(R.mipmap.ic_avatar_snapshot_default);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissAvatarSnapshotView() {
        ImageView imageView = this.mAvatarSnapshotView;
        if (imageView != null) {
            removeView(imageView);
            setAvatarVisible(true);
        }
        resetMusicRect();
        setMusicCardPress(false);
    }

    private void resetMusicRect() {
        Rect rect = this.mMusicCardRect;
        rect.bottom = -1;
        rect.top = -1;
        rect.right = -1;
        rect.left = -1;
    }

    private void showAvatarSnapshotView(int left, int top) {
        initAvatarSnapshotView();
        this.mCurrentX = left;
        this.mCurrentY = top;
        if (!this.mAvatarSnapshotView.isVisibleToUser()) {
            this.mAvatarMoveOffsetX = left - this.mSmallAvatarBounds.left;
            this.mAvatarMoveOffsetY = top - this.mSmallAvatarBounds.top;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (this.mSmallAvatarBounds.width() * this.mScaleValue), (int) (this.mSmallAvatarBounds.height() * this.mScaleValue));
            layoutParams.topMargin = top - this.mAvatarMoveOffsetY;
            layoutParams.leftMargin = left - this.mAvatarMoveOffsetX;
            if (this.mAvatarSnapshotView.getParent() == null) {
                addView(this.mAvatarSnapshotView, layoutParams);
            }
            setAvatarVisible(false);
            this.mMusicView = getMusicCardPosition(this.mMusicCardRect);
            return;
        }
        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.mAvatarSnapshotView.getLayoutParams();
        layoutParams2.topMargin = top - this.mAvatarMoveOffsetY;
        layoutParams2.leftMargin = left - this.mAvatarMoveOffsetX;
        int i = layoutParams2.leftMargin;
        int i2 = this.mMaxAvatarShotLeftParams;
        if (i > i2) {
            layoutParams2.leftMargin = i2;
        }
        int i3 = layoutParams2.leftMargin;
        int i4 = this.mNavBarW;
        if (i3 < i4) {
            layoutParams2.leftMargin = i4;
        }
        this.mAvatarSnapshotView.setLayoutParams(layoutParams2);
        int touchLeft = left - this.mAvatarMoveOffsetX;
        if (touchLeft < this.mMinAvatarShotLeftParams || touchLeft > this.mMaxAvatarShotLeftParams) {
            backToAvatarStartPoint(layoutParams2.leftMargin, layoutParams2.topMargin);
            setMusicCardPress(false);
            return;
        }
        checkHitMusicCard(layoutParams2.leftMargin, top);
    }

    private void setAvatarVisible(boolean visible) {
        this.mAvatarViewParentContainer.setAvatarVisible(visible);
    }

    private void checkHitMusicCard(int left, int top) {
        if (!isAllowDance()) {
            return;
        }
        if (this.mMusicCardRect.top == -1 && this.mMusicCardRect.left == -1) {
            return;
        }
        int realTop = top - this.mSmallAvatarMarginTop;
        if (this.mMusicCardRect.contains(left, realTop)) {
            setMusicCardPress(true);
        } else {
            setMusicCardPress(false);
        }
    }

    private IFocusView getMusicCardPosition(Rect rect) {
        View view;
        MessageViewParent messageViewParent = this.mMessageViewParent;
        if (messageViewParent != null && (view = messageViewParent.getMusicCardPosition(rect)) != null && (view instanceof IFocusView)) {
            return (IFocusView) view;
        }
        return null;
    }

    private void setMusicCardPress(boolean press) {
        IFocusView iFocusView = this.mMusicView;
        if (iFocusView != null) {
            iFocusView.setFocused(press);
            this.mIsMusicFocused = press;
        }
    }

    private void notifyAvatarAction(final int actionType, final String params) {
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.AvatarViewParent.6
            @Override // java.lang.Runnable
            public void run() {
                AvatarViewParent.this.mAvatarViewParentContainer.notifyAvatarAction(actionType, params);
            }
        });
    }

    private void backToAvatarStartPoint(int currentX, int currentY) {
        if (this.inBackAnimation) {
            return;
        }
        this.inBackAnimation = true;
        Path path = new Path();
        path.moveTo(currentX, currentY);
        path.quadTo(currentX, currentY, 0.0f, 0.0f);
        this.mPathMeasure = new PathMeasure(path, false);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, this.mPathMeasure.getLength());
        valueAnimator.setDuration(250L);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.AvatarViewParent.7
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = ((Float) animation.getAnimatedValue()).floatValue();
                AvatarViewParent.this.mPathMeasure.getPosTan(value, AvatarViewParent.this.mCurrentPosition, null);
                AvatarViewParent avatarViewParent = AvatarViewParent.this;
                avatarViewParent.translateAvatarSnapshotView((int) avatarViewParent.mCurrentPosition[0], (int) AvatarViewParent.this.mCurrentPosition[1]);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() { // from class: com.xiaopeng.systemui.infoflow.AvatarViewParent.8
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                AvatarViewParent.this.inBackAnimation = true;
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                AvatarViewParent.this.dismissAvatarSnapshotView();
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void translateAvatarSnapshotView(int left, int top) {
        ImageView imageView = this.mAvatarSnapshotView;
        if (imageView != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.leftMargin = this.mSmallAvatarMarginLeft + left;
            layoutParams.topMargin = top;
            this.mAvatarSnapshotView.setLayoutParams(layoutParams);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.manager.MediaManager.OnPlayStatusChangedListener
    public void onStatusChanged(int status) {
        Logger.d(TAG, "onMusicStatusChanged : " + status);
        this.mMediaState = status;
        this.mMediaInfo = MediaManager.getInstance().getCurrentMediaInfo();
        AIAvatarViewServiceHelper.EventParams eventParams = new AIAvatarViewServiceHelper.EventParams();
        eventParams.setMusicState(status);
        MediaInfo mediaInfo = this.mMediaInfo;
        if (mediaInfo != null) {
            eventParams.setTitle(mediaInfo.getTitle());
            eventParams.setStyleName(this.mMediaInfo.getStyleName());
            eventParams.setSource(this.mMediaInfo.getSource());
        }
        notifyAvatarAction(1, GsonUtil.toJson(eventParams));
    }

    private boolean isAllowDance() {
        MediaInfo mediaInfo = this.mMediaInfo;
        if (mediaInfo != null && mediaInfo.getSource() == 0 && TYPE_ACTION_MUSIC_PLAY == this.mMediaState) {
            return true;
        }
        return false;
    }

    public void showMessageViewGroup(boolean show) {
        MessageViewParent messageViewParent = this.mMessageViewParent;
        if (messageViewParent != null) {
            messageViewParent.setVisibility(show ? 0 : 8);
        }
    }

    public void onAvatarSkinUpdate(Drawable skin) {
        this.mAvatarSnapshotDrawable = skin;
        ImageView imageView = this.mAvatarSnapshotView;
        if (imageView != null) {
            imageView.setImageDrawable(this.mAvatarSnapshotDrawable);
        }
    }

    public void onAvatarStateChanged(int state) {
        this.mAvatarState = state;
    }

    public void refreshList(List<CardEntry> entries) {
        this.mMessageViewParent.refreshList(entries);
    }
}
