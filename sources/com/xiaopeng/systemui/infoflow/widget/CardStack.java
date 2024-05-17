package com.xiaopeng.systemui.infoflow.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.infoflow.dao.InfoFlowConfigDao;
import com.xiaopeng.systemui.infoflow.message.KeyConfig;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.MusicCardHolder;
import com.xiaopeng.systemui.infoflow.message.helper.FocusHelper;
import com.xiaopeng.systemui.infoflow.message.helper.SoundHelper;
import com.xiaopeng.systemui.infoflow.speech.ui.adapter.NaviPoiAdapter;
import com.xiaopeng.systemui.infoflow.speech.ui.adapter.NaviRouterAdapter;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.widget.layer.CardAngleLayout;
import com.xiaopeng.systemui.ui.widget.XRecyclerView;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class CardStack extends XRecyclerView {
    private static final int DELAY_AI_FOCUS = 1000;
    public static final int INVAILD_POSITION = -1;
    private static final int MSG_AI_FOCUS = 18;
    private static final int MSG_UNFOCUS = 17;
    private static final String TAG = CardStack.class.getSimpleName();
    protected boolean mAutoUnfocus;
    private boolean mCheckPositionBeforeFocus;
    protected IFocusView mCurrentFocusItem;
    private int mFocusDirection;
    private int mFocusItemPosAfterScroll;
    protected int mFocusedPosition;
    protected Handler mHandler;
    private int mInitKeyCodeNum;
    private int mLastKeyCode;
    private int mLastKeyCodeNum;
    private int mLastKeycode;
    private int mLastKeycodeForSlide;
    private long mLastKeytime;
    private float mLastXAngle;
    private float mLastYAngle;
    protected LinearLayoutManager mLinearLayoutManager;
    private boolean mNeedClearFocusAfterScroll;
    private boolean mNeedFocusItemAfterScroll;
    private boolean mNeedStopScrollOnTouchUp;
    private boolean mNeedTriggerListenerAfterScroll;
    private IFocusView mPreFocusItem;
    private float mScrollSpeedValue;
    private int mSlideMode;

    /* loaded from: classes24.dex */
    public class CardStackSmoothScroller extends LinearSmoothScroller {
        public CardStackSmoothScroller(Context context) {
            super(context);
        }

        @Override // androidx.recyclerview.widget.LinearSmoothScroller
        protected int getVerticalSnapPreference() {
            return -1;
        }

        @Override // androidx.recyclerview.widget.LinearSmoothScroller
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return 60.0f / displayMetrics.densityDpi;
        }
    }

    public CardStack(Context context) {
        super(context);
        this.mFocusedPosition = -1;
        this.mFocusDirection = 0;
        this.mScrollSpeedValue = 25.0f;
        this.mLastXAngle = 0.0f;
        this.mLastYAngle = 0.0f;
        this.mSlideMode = SystemProperties.getInt("persist.infoflow.straight.control", 0);
        this.mLastKeycode = 0;
        this.mLastKeycodeForSlide = 0;
        this.mLastKeytime = 0L;
        this.mNeedFocusItemAfterScroll = false;
        this.mFocusItemPosAfterScroll = 0;
        this.mNeedTriggerListenerAfterScroll = false;
        this.mAutoUnfocus = true;
        this.mCheckPositionBeforeFocus = true;
        this.mNeedClearFocusAfterScroll = true;
        this.mNeedStopScrollOnTouchUp = false;
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.widget.CardStack.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                int i = msg.what;
                if (i != 17) {
                    if (i == 18) {
                        CardStack.this.doForceFocusAICard();
                        return;
                    }
                    return;
                }
                Logger.d(CardStack.TAG, "MSG_UNFOCUS");
                CardStack.this.resetPreFocusViews();
                CardStack.this.resetFocus();
            }
        };
        init();
    }

    public CardStack(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mFocusedPosition = -1;
        this.mFocusDirection = 0;
        this.mScrollSpeedValue = 25.0f;
        this.mLastXAngle = 0.0f;
        this.mLastYAngle = 0.0f;
        this.mSlideMode = SystemProperties.getInt("persist.infoflow.straight.control", 0);
        this.mLastKeycode = 0;
        this.mLastKeycodeForSlide = 0;
        this.mLastKeytime = 0L;
        this.mNeedFocusItemAfterScroll = false;
        this.mFocusItemPosAfterScroll = 0;
        this.mNeedTriggerListenerAfterScroll = false;
        this.mAutoUnfocus = true;
        this.mCheckPositionBeforeFocus = true;
        this.mNeedClearFocusAfterScroll = true;
        this.mNeedStopScrollOnTouchUp = false;
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.widget.CardStack.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                int i = msg.what;
                if (i != 17) {
                    if (i == 18) {
                        CardStack.this.doForceFocusAICard();
                        return;
                    }
                    return;
                }
                Logger.d(CardStack.TAG, "MSG_UNFOCUS");
                CardStack.this.resetPreFocusViews();
                CardStack.this.resetFocus();
            }
        };
        init();
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (this.mNeedClearFocusAfterScroll) {
            resetPreFocusViews();
            resetFocus();
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void setLayoutManager(@Nullable RecyclerView.LayoutManager layout) {
        super.setLayoutManager(layout);
        if (layout instanceof LinearLayoutManager) {
            this.mLinearLayoutManager = (LinearLayoutManager) layout;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == 1 && this.mNeedStopScrollOnTouchUp) {
            stopScroll();
        }
        return super.onTouchEvent(e);
    }

    private void init() {
    }

    public List<View> findVisiableItems() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        int fistVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        List<View> visibleItems = new ArrayList<>();
        for (int i = fistVisiblePosition; i <= lastVisiblePosition; i++) {
            View child = layoutManager.findViewByPosition(i);
            RecyclerView.ViewHolder holder = getChildViewHolder(child);
            if (!(holder instanceof MusicCardHolder)) {
                visibleItems.add(child);
            }
        }
        return visibleItems;
    }

    public void performClickItem() {
        if (this.mFocusedPosition != -1) {
            RecyclerView.Adapter adapter = getAdapter();
            if (adapter instanceof NaviPoiAdapter) {
                NaviPoiAdapter naviPoiAdapter = (NaviPoiAdapter) adapter;
                naviPoiAdapter.sendSelectedEvent(this.mFocusedPosition);
            } else if (adapter instanceof NaviRouterAdapter) {
                NaviRouterAdapter naviRouterAdapter = (NaviRouterAdapter) adapter;
                naviRouterAdapter.sendSelectedEvent(this.mFocusedPosition);
            } else {
                View focusItem = getViewByPosition(this.mFocusedPosition);
                if (focusItem == null) {
                    return;
                }
                if (focusItem.isEnabled()) {
                    focusItem.setClickable(true);
                    SoundHelper.play(SoundHelper.PATH_WHEEL_OK);
                    focusItem.performClick();
                    return;
                }
                SoundHelper.play(SoundHelper.PATH_TOUCH_DISABLE);
            }
        }
    }

    private int dealStraightSlide(int keycode) {
        int ret = -1;
        long curtime = System.currentTimeMillis();
        long delta_time = curtime - this.mLastKeytime;
        String str = TAG;
        Logger.d(str, "dealStraightSlide keycode:" + keycode + "delta_time:" + delta_time);
        if (delta_time > 0 && delta_time < 500) {
            String str2 = TAG;
            Logger.d(str2, "dealStraightSlide ret:-1 keycode:" + keycode + " mLastKeycodeForSlide:" + this.mLastKeycodeForSlide);
            if (keycode == 1055) {
                int i = this.mLastKeycodeForSlide;
                if (i == 1052 || i == 1054) {
                    ret = 33;
                } else if (i == 1051 || i == 1053) {
                    ret = 130;
                }
            } else if (this.mLastKeycodeForSlide == 1055) {
                if (keycode == 1052 || keycode == 1054) {
                    ret = 130;
                } else if (keycode == 1051 || keycode == 1053) {
                    ret = 33;
                }
            }
        }
        this.mLastKeytime = curtime;
        if (ret == -1) {
            this.mLastKeycodeForSlide = keycode;
        } else {
            this.mLastKeycodeForSlide = -1;
        }
        return ret;
    }

    public RecyclerView.SmoothScroller createSmoothScroller(int targetPosition) {
        RecyclerView.SmoothScroller smoothScroller = new CardStackSmoothScroller(this.mContext);
        smoothScroller.setTargetPosition(targetPosition);
        return smoothScroller;
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x004b, code lost:
        if (r9.mSlideMode < 2) goto L24;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void performFocusNavigation(android.view.KeyEvent r10) {
        /*
            r9 = this;
            r9.startCountdownUnfocus()
            int r0 = r10.getAction()
            if (r0 == 0) goto La
            return
        La:
            java.lang.String r0 = com.xiaopeng.systemui.infoflow.widget.CardStack.TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "performFocusNavigation code:"
            r1.append(r2)
            int r2 = r10.getKeyCode()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.xiaopeng.systemui.infoflow.util.Logger.d(r0, r1)
            boolean r0 = r9.shouldProcessCurrentKeyEvent(r10)
            if (r0 != 0) goto L32
            java.lang.String r0 = com.xiaopeng.systemui.infoflow.widget.CardStack.TAG
            java.lang.String r1 = "not should Process Current KeyEvent"
            com.xiaopeng.systemui.infoflow.util.Logger.d(r0, r1)
            return
        L32:
            r0 = 0
            int r1 = r10.getKeyCode()
            r2 = 1015(0x3f7, float:1.422E-42)
            if (r1 == r2) goto Lcb
            r2 = 1083(0x43b, float:1.518E-42)
            r3 = -1
            if (r1 == r2) goto L5e
            r2 = 1084(0x43c, float:1.519E-42)
            if (r1 == r2) goto L5b
            switch(r1) {
                case 1051: goto L4e;
                case 1052: goto L4e;
                case 1053: goto L48;
                case 1054: goto L48;
                case 1055: goto L4e;
                default: goto L47;
            }
        L47:
            goto L61
        L48:
            int r2 = r9.mSlideMode
            r4 = 2
            if (r2 >= r4) goto L4e
            goto L61
        L4e:
            int r2 = r9.mSlideMode
            if (r2 <= 0) goto L61
            int r2 = r9.dealStraightSlide(r1)
            if (r2 != r3) goto L59
            return
        L59:
            r0 = r2
            goto L61
        L5b:
            r0 = 33
            goto L61
        L5e:
            r0 = 130(0x82, float:1.82E-43)
        L61:
            if (r0 == 0) goto Lca
            int r2 = r9.xfocusSearch(r0)
            java.lang.String r4 = com.xiaopeng.systemui.infoflow.widget.CardStack.TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "nextPosition:"
            r5.append(r6)
            r5.append(r2)
            java.lang.String r6 = " &mLastFocusedPosition:"
            r5.append(r6)
            int r6 = r9.mFocusedPosition
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            com.xiaopeng.systemui.infoflow.util.Logger.d(r4, r5)
            if (r2 == r3) goto Lc6
            int r3 = r9.mFocusedPosition
            if (r2 != r3) goto L8e
            goto Lc6
        L8e:
            android.view.View r3 = r9.getViewByPosition(r3)
            androidx.recyclerview.widget.RecyclerView$SmoothScroller r4 = r9.createSmoothScroller(r2)
            androidx.recyclerview.widget.LinearLayoutManager r5 = r9.mLinearLayoutManager
            r5.startSmoothScroll(r4)
            if (r3 == 0) goto La8
            boolean r5 = r3 instanceof com.xiaopeng.systemui.infoflow.widget.IFocusView
            if (r5 == 0) goto La8
            r5 = r3
            com.xiaopeng.systemui.infoflow.widget.IFocusView r5 = (com.xiaopeng.systemui.infoflow.widget.IFocusView) r5
            r6 = 0
            r5.setFocused(r6)
        La8:
            android.view.View r5 = r9.getViewByPosition(r2)
            r9.mFocusDirection = r0
            if (r5 == 0) goto Lb4
            r9.focusItem(r2)
            goto Lca
        Lb4:
            java.lang.String r6 = com.xiaopeng.systemui.infoflow.widget.CardStack.TAG
            java.lang.String r7 = "focus next view with next invalidate"
            com.xiaopeng.systemui.infoflow.util.Logger.d(r6, r7)
            com.xiaopeng.systemui.infoflow.widget.CardStack$2 r6 = new com.xiaopeng.systemui.infoflow.widget.CardStack$2
            r6.<init>()
            r7 = 100
            r9.postDelayed(r6, r7)
            goto Lca
        Lc6:
            r9.assureScrolledPosition(r2)
            return
        Lca:
            return
        Lcb:
            com.xiaopeng.systemui.carconfig.config.IConfig r2 = com.xiaopeng.systemui.carconfig.CarModelsManager.getConfig()
            boolean r2 = r2.isAllWheelKeyToICMSupport()
            if (r2 != 0) goto Ld8
            r9.performClickItem()
        Ld8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.systemui.infoflow.widget.CardStack.performFocusNavigation(android.view.KeyEvent):void");
    }

    private void assureScrolledPosition(int targetPosition) {
        if (targetPosition == 0) {
            int firstVisibleItemPosition = this.mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
            String str = TAG;
            Logger.d(str, "assureScrolledPosition targetPosition:" + targetPosition + "& firstVisibleItemPosition:" + firstVisibleItemPosition);
            if (targetPosition != firstVisibleItemPosition) {
                RecyclerView.SmoothScroller smoothScroller = createSmoothScroller(targetPosition);
                this.mLinearLayoutManager.startSmoothScroll(smoothScroller);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void focusItem(int position) {
        forceFocusItem(position, true);
        this.mFocusDirection = 0;
    }

    protected void focusItem(int position, boolean triggerListener) {
        View focusView = getViewByPosition(position);
        if (focusView == null) {
            Logger.d(TAG, "focusItem : focusView = null");
        }
        if (focusView instanceof IFocusView) {
            IFocusView cardView = (IFocusView) focusView;
            cardView.setFocused(true, triggerListener);
            this.mCurrentFocusItem = cardView;
        }
    }

    protected View getViewByPosition(int position) {
        if (position == -1) {
            return null;
        }
        return this.mLinearLayoutManager.findViewByPosition(position);
    }

    private int xfocusSearch(int direction) {
        if (this.mFocusedPosition == -1) {
            int nextPosition = this.mLinearLayoutManager.findFirstVisibleItemPosition();
            return nextPosition;
        }
        int itemCount = this.mLinearLayoutManager.getItemCount();
        if (direction == 130) {
            int i = this.mFocusedPosition;
            if (i == itemCount - 1) {
                int nextPosition2 = this.mFocusedPosition;
                return nextPosition2;
            }
            int nextPosition3 = i + 1;
            return nextPosition3;
        } else if (direction != 33) {
            return 0;
        } else {
            int i2 = this.mFocusedPosition;
            if (i2 == 0) {
                int nextPosition4 = this.mFocusedPosition;
                return nextPosition4;
            }
            int nextPosition5 = i2 - 1;
            return nextPosition5;
        }
    }

    private void unFocusSelectedItem() {
        IFocusView iFocusView = this.mCurrentFocusItem;
        if (iFocusView != null) {
            iFocusView.setFocused(false);
        }
    }

    public void resetFocus() {
        Logger.d(TAG, "resetFocus");
        this.mFocusedPosition = -1;
        PresenterCenter.getInstance().getInfoFlow().onCardFocusedChanged(this.mFocusedPosition);
        resetKeyCode();
        FocusHelper.saveFocusItem(-1);
        unFocusSelectedItem();
        stopCountdownUnfocus();
    }

    private void resetPreFocus() {
        Logger.d(TAG, "resetPreFocus");
        IFocusView iFocusView = this.mPreFocusItem;
        if (iFocusView != null && (iFocusView instanceof ShimmerLayout)) {
            ((ShimmerLayout) iFocusView).resetPrefocusAnimationIfStarted();
            this.mPreFocusItem = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetPreFocusViews() {
        Logger.d(TAG, "resetPreFocusViews");
        if (this.mFocusedPosition != -1) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
            int fistVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
            int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
            for (int i = fistVisiblePosition; i <= lastVisiblePosition; i++) {
                View child = layoutManager.findViewByPosition(i);
                if ((child instanceof ShimmerLayout) && child != this.mCurrentFocusItem) {
                    ((ShimmerLayout) child).resetPrefocusAnimationIfStarted();
                }
            }
        }
    }

    private void startCountdownUnfocus() {
        this.mHandler.removeMessages(17);
        this.mHandler.sendEmptyMessageDelayed(17, getCardFocusDuration());
    }

    private void stopCountdownUnfocus() {
        this.mHandler.removeMessages(17);
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void onScrollStateChanged(int state) {
        String str = TAG;
        Logger.d(str, "onScrollStateChanged : state = " + state + " mNeedFocusItemAfterScroll = " + this.mNeedFocusItemAfterScroll + " mFocusItemPosAfterScroll = " + this.mFocusItemPosAfterScroll);
        if (state == 0 && this.mNeedFocusItemAfterScroll) {
            focusItem(this.mFocusItemPosAfterScroll, this.mNeedTriggerListenerAfterScroll);
            this.mNeedFocusItemAfterScroll = false;
        }
    }

    @Override // android.view.View
    protected int getTopPaddingOffset() {
        return -getPaddingTop();
    }

    @Override // android.view.View
    protected boolean isPaddingOffsetRequired() {
        return true;
    }

    private boolean shouldProcessCurrentKeyEvent(KeyEvent keyEvent) {
        int keyConfig = KeyConfig.getCurrentConfig();
        Logger.d(TAG, "current keyConfig --" + keyConfig);
        if (keyConfig == KeyConfig.NORMAL) {
            return true;
        }
        int currentKeyCode = keyEvent.getKeyCode();
        boolean isStraghtControl = false;
        isStraghtControl = (currentKeyCode == 1051 || currentKeyCode == 1052 || currentKeyCode == 1055) ? true : true;
        if (this.mFocusedPosition == -1 && !isStraghtControl) {
            this.mInitKeyCodeNum++;
            if (this.mInitKeyCodeNum == 3) {
                this.mInitKeyCodeNum = 0;
                return true;
            }
            return false;
        } else if (currentKeyCode == 1015 || keyConfig == KeyConfig.THREE_FOCUS_ONE_SCROLL || isStraghtControl) {
            return true;
        } else {
            if (keyConfig == KeyConfig.THREE_FOCUS_TWO_SCROLL) {
                if (this.mLastKeyCode == currentKeyCode) {
                    resetKeyCode();
                    return true;
                }
                doPreSelectedEffect(keyEvent);
                this.mLastKeyCode = currentKeyCode;
                return false;
            } else if (keyConfig == KeyConfig.THREE_FOCUS_FOUR_SCROLL) {
                if (this.mLastKeyCode == currentKeyCode) {
                    this.mLastKeyCodeNum++;
                    if (this.mLastKeyCodeNum == 4) {
                        resetKeyCode();
                        return true;
                    }
                    return false;
                }
                this.mLastKeyCode = currentKeyCode;
                this.mLastKeyCodeNum = 1;
                return false;
            } else {
                return true;
            }
        }
    }

    private void resetKeyCode() {
        this.mLastKeyCode = 0;
        this.mLastKeyCodeNum = 0;
        this.mInitKeyCodeNum = 0;
    }

    private void doPreSelectedEffect(KeyEvent keyEvent) {
        int direction = 0;
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == 1083) {
            direction = 130;
        } else if (keyCode == 1084) {
            direction = 33;
        }
        int nextPosition = xfocusSearch(direction);
        String str = TAG;
        Logger.d(str, "doPreSelectEffect : nextPosition = " + nextPosition);
        if (nextPosition == -1 || nextPosition == this.mFocusedPosition) {
            return;
        }
        this.mPreFocusItem = (IFocusView) getViewByPosition(nextPosition);
        IFocusView iFocusView = this.mPreFocusItem;
        if (iFocusView != null) {
            iFocusView.setPreFocused(true);
        }
    }

    public void setScrollSpeedValue(float value) {
        this.mScrollSpeedValue = value;
    }

    public float getScrollSpeedValue() {
        return this.mScrollSpeedValue;
    }

    public void forceFocusFirstAICard() {
        this.mHandler.removeMessages(18);
        this.mHandler.sendEmptyMessageDelayed(18, 1000L);
    }

    public void setAutoUnfocus(boolean autoUnfocus) {
        this.mAutoUnfocus = autoUnfocus;
    }

    public void setCheckPositionBeforeFocus(boolean checkPositionBeforeFocus) {
        this.mCheckPositionBeforeFocus = checkPositionBeforeFocus;
    }

    public void setNeedClearFocusAfterScroll(boolean needClearFocusAfterScroll) {
        this.mNeedClearFocusAfterScroll = needClearFocusAfterScroll;
    }

    public void setNeedStopScrollOnTouchUp(boolean needStopScrollOnTouchUp) {
        this.mNeedStopScrollOnTouchUp = needStopScrollOnTouchUp;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doForceFocusAICard() {
        Logger.d(TAG, "doForceFocusAICard");
        resetFocus();
        focusItem(0);
        this.mHandler.sendEmptyMessageDelayed(17, getCardFocusDuration());
    }

    public void forceFocusItem(int position, boolean triggerListener) {
        String str = TAG;
        Logger.d(str, "forceFocusItem : position = " + position + " mLastFocusedPosition = " + this.mFocusedPosition);
        if (this.mCheckPositionBeforeFocus && this.mFocusedPosition == position) {
            return;
        }
        resetFocus();
        if (this.mLinearLayoutManager.findViewByPosition(position) != null) {
            focusItem(position, triggerListener);
        } else {
            RecyclerView.SmoothScroller smoothScroller = createSmoothScroller(position);
            this.mLinearLayoutManager.startSmoothScroll(smoothScroller);
            this.mNeedFocusItemAfterScroll = true;
            this.mNeedTriggerListenerAfterScroll = triggerListener;
            this.mFocusItemPosAfterScroll = position;
        }
        if (this.mAutoUnfocus) {
            startCountdownUnfocus();
        }
        this.mFocusedPosition = position;
        PresenterCenter.getInstance().getInfoFlow().onCardFocusedChanged(this.mFocusedPosition);
    }

    public void unfocusItem(int position) {
        if (this.mFocusedPosition == position) {
            resetFocus();
        }
    }

    public void forceFocusItem(int position) {
        forceFocusItem(position, true);
    }

    private long getCardFocusDuration() {
        return InfoFlowConfigDao.getInstance().getConfig().cardFocusedTime;
    }

    public void changeAngle(float x, float y) {
        String str = TAG;
        Logger.d(str, "change angle x--" + x + "&y--" + y);
        if (x == this.mLastXAngle && y == this.mLastYAngle) {
            return;
        }
        this.mLastXAngle = x;
        this.mLastYAngle = y;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof CardAngleLayout) {
                ((CardAngleLayout) child).angleMove(x, y);
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (isLayoutFrozen()) {
            Logger.d(TAG, "card stack isLayoutFrozen");
        }
        super.requestLayout();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        tryEnableDrawFade();
    }

    private void tryEnableDrawFade() {
        if (isHorizontalFadingEdgeEnabled() || isVerticalFadingEdgeEnabled() || isHorizontalScrollBarEnabled() || isVerticalScrollBarEnabled()) {
            setWillNotDraw(false);
        }
    }

    public LinearLayoutManager getLinearLayoutManager() {
        return this.mLinearLayoutManager;
    }

    public View getMusicCardPosition(Rect rect) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        int fistVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        for (int i = fistVisiblePosition; i <= lastVisiblePosition; i++) {
            View child = layoutManager.findViewByPosition(i);
            RecyclerView.ViewHolder holder = getChildViewHolder(child);
            if (holder instanceof MusicCardHolder) {
                rect.left = child.getLeft();
                rect.top = child.getTop();
                rect.right = child.getRight();
                rect.bottom = child.getBottom();
                return child;
            }
        }
        return null;
    }

    public int getFocusIndex() {
        return this.mFocusedPosition;
    }
}
