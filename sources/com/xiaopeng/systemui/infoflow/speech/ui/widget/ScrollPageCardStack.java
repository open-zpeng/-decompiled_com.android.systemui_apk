package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.speech.core.speech.SpeechManager;
import com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechPageScroll;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.infoflow.widget.CardStack;
/* loaded from: classes24.dex */
public class ScrollPageCardStack extends CardStack {
    private static final String TAG = "ScrollPageCardStack";
    private int mPageItemSize;
    private ISpeechPageScroll mSpeechPageScroll;

    /* renamed from: com.xiaopeng.systemui.infoflow.speech.ui.widget.ScrollPageCardStack$1  reason: invalid class name */
    /* loaded from: classes24.dex */
    class AnonymousClass1 implements ISpeechPageScroll {
        AnonymousClass1() {
        }

        @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechPageScroll
        public void onPageNext() {
            Log.d(ScrollPageCardStack.TAG, "onPageNext");
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.ScrollPageCardStack.1.1
                @Override // java.lang.Runnable
                public void run() {
                    if (ScrollPageCardStack.this.getVisibility() != 0) {
                        ScrollPageCardStack.this.setVisibility(0);
                    }
                    int location = ScrollPageCardStack.this.getWidgetLastLocation();
                    RecyclerView.SmoothScroller smoothScroller = ScrollPageCardStack.this.createSmoothScroller(location);
                    ScrollPageCardStack.this.mLinearLayoutManager.startSmoothScroll(smoothScroller);
                }
            });
        }

        @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechPageScroll
        public void onPagePrev() {
            Log.d(ScrollPageCardStack.TAG, "onPagePrev");
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.ScrollPageCardStack.1.2
                @Override // java.lang.Runnable
                public void run() {
                    if (ScrollPageCardStack.this.getVisibility() != 0) {
                        ScrollPageCardStack.this.setVisibility(0);
                    }
                    int firstItemPosition = ScrollPageCardStack.this.getLinearLayoutManager().findFirstVisibleItemPosition();
                    int lastItemPosition = ScrollPageCardStack.this.getLinearLayoutManager().findLastVisibleItemPosition();
                    int scrollItemSize = lastItemPosition - firstItemPosition;
                    int target = AnonymousClass1.this.getWidgetCurrLocation() - scrollItemSize;
                    if (target < 0) {
                        target = 0;
                    }
                    RecyclerView.SmoothScroller smoothScroller = ScrollPageCardStack.this.createSmoothScroller(target);
                    ScrollPageCardStack.this.mLinearLayoutManager.startSmoothScroll(smoothScroller);
                }
            });
        }

        @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechPageScroll
        public void onPageSetLow() {
            Log.d(ScrollPageCardStack.TAG, "onPageSetLow");
            RecyclerView.SmoothScroller smoothScroller = ScrollPageCardStack.this.createSmoothScroller(getWidgetListSize());
            ScrollPageCardStack.this.mLinearLayoutManager.startSmoothScroll(smoothScroller);
        }

        @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechPageScroll
        public void onPageTopping() {
            Log.d(ScrollPageCardStack.TAG, "onPageTopping");
            RecyclerView.SmoothScroller smoothScroller = ScrollPageCardStack.this.createSmoothScroller(0);
            ScrollPageCardStack.this.mLinearLayoutManager.startSmoothScroll(smoothScroller);
        }

        @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechPageScroll
        public int getWidgetListSize() {
            int size = ScrollPageCardStack.this.getAdapter().getItemCount();
            Log.d(ScrollPageCardStack.TAG, "getWidgetListSize size:" + size);
            return size;
        }

        @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechPageScroll
        public int getWidgetPageSize() {
            int result = ScrollPageCardStack.this.getDynamicPageSize();
            Log.d(ScrollPageCardStack.TAG, "getWidgetPageSize result:" + result);
            return result;
        }

        @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechPageScroll
        public int getWidgetCurrLocation() {
            int location = 0;
            if (ScrollPageCardStack.this.getLinearLayoutManager() != null) {
                location = ScrollPageCardStack.this.getLinearLayoutManager().findFirstVisibleItemPosition();
            }
            Log.d(ScrollPageCardStack.TAG, "getWidgetCurrLocation location:" + location);
            return location;
        }

        @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechPageScroll
        public int getInfoFlowOnePage() {
            return getWidgetPageSize() == getWidgetListSize() ? 1 : 0;
        }

        @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechPageScroll
        public int getInfoFlowScrollToBottom() {
            return !ScrollPageCardStack.this.canScrollVertically(1);
        }

        @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechPageScroll
        public int getInfoFlowScrollToTop() {
            return !ScrollPageCardStack.this.canScrollVertically(-1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getWidgetLastLocation() {
        int location = 0;
        if (getLinearLayoutManager() != null) {
            location = getLinearLayoutManager().findLastVisibleItemPosition();
        }
        Log.d(TAG, "getWidgetLastLocation location:" + location);
        return location;
    }

    public ScrollPageCardStack(Context context) {
        super(context);
        this.mSpeechPageScroll = new AnonymousClass1();
    }

    public ScrollPageCardStack(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSpeechPageScroll = new AnonymousClass1();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow setSpeechPageScrollCallback");
        if (getVisibility() == 0) {
            SpeechManager.instance().getSpeechContextManager().setSpeechPageScrollCallback(this.mSpeechPageScroll);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.ui.widget.XRecyclerView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SpeechManager.instance().getSpeechContextManager().setSpeechPageScrollCallback(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.ui.widget.XRecyclerView, androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        calculatePageItemSize();
    }

    private void calculatePageItemSize() {
        int stackHeight = getMeasuredHeight();
        int itemHeight = getResources().getDimensionPixelSize(R.dimen.card_height);
        this.mPageItemSize = stackHeight / itemHeight;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDynamicPageSize() {
        int pageSize = this.mPageItemSize;
        int stackHeight = getMeasuredHeight();
        View view = getChildAt(0);
        if (view != null) {
            int itemHeight = view.getMeasuredHeight();
            int pageSize2 = stackHeight / itemHeight;
            return pageSize2;
        }
        return pageSize;
    }
}
