package com.android.systemui.qs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.systemui.R;
import java.util.ArrayList;
/* loaded from: classes21.dex */
public class PageIndicator extends ViewGroup {
    private static final long ANIMATION_DURATION = 250;
    private static final boolean DEBUG = false;
    private static final float MINOR_ALPHA = 0.42f;
    private static final float SINGLE_SCALE = 0.4f;
    private static final String TAG = "PageIndicator";
    private boolean mAnimating;
    private final Runnable mAnimationDone;
    private final int mPageDotWidth;
    private final int mPageIndicatorHeight;
    private final int mPageIndicatorWidth;
    private int mPosition;
    private final ArrayList<Integer> mQueuedPositions;

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mQueuedPositions = new ArrayList<>();
        this.mPosition = -1;
        this.mAnimationDone = new Runnable() { // from class: com.android.systemui.qs.PageIndicator.1
            @Override // java.lang.Runnable
            public void run() {
                PageIndicator.this.mAnimating = false;
                if (PageIndicator.this.mQueuedPositions.size() != 0) {
                    PageIndicator pageIndicator = PageIndicator.this;
                    pageIndicator.setPosition(((Integer) pageIndicator.mQueuedPositions.remove(0)).intValue());
                }
            }
        };
        this.mPageIndicatorWidth = (int) this.mContext.getResources().getDimension(R.dimen.qs_page_indicator_width);
        this.mPageIndicatorHeight = (int) this.mContext.getResources().getDimension(R.dimen.qs_page_indicator_height);
        this.mPageDotWidth = (int) (this.mPageIndicatorWidth * SINGLE_SCALE);
    }

    public void setNumPages(int numPages) {
        setVisibility(numPages > 1 ? 0 : 8);
        if (this.mAnimating) {
            Log.w(TAG, "setNumPages during animation");
        }
        while (numPages < getChildCount()) {
            removeViewAt(getChildCount() - 1);
        }
        TypedArray array = getContext().obtainStyledAttributes(new int[]{16843818});
        int color = array.getColor(0, 0);
        array.recycle();
        while (numPages > getChildCount()) {
            ImageView v = new ImageView(this.mContext);
            v.setImageResource(R.drawable.minor_a_b);
            v.setImageTintList(ColorStateList.valueOf(color));
            addView(v, new ViewGroup.LayoutParams(this.mPageIndicatorWidth, this.mPageIndicatorHeight));
        }
        setIndex(this.mPosition >> 1);
    }

    public void setLocation(float location) {
        int index = (int) location;
        setContentDescription(getContext().getString(R.string.accessibility_quick_settings_page, Integer.valueOf(index + 1), Integer.valueOf(getChildCount())));
        int position = (index << 1) | (location != ((float) index) ? 1 : 0);
        int lastPosition = this.mPosition;
        if (this.mQueuedPositions.size() != 0) {
            ArrayList<Integer> arrayList = this.mQueuedPositions;
            lastPosition = arrayList.get(arrayList.size() - 1).intValue();
        }
        if (position == lastPosition) {
            return;
        }
        if (this.mAnimating) {
            this.mQueuedPositions.add(Integer.valueOf(position));
        } else {
            setPosition(position);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPosition(int position) {
        if (isVisibleToUser() && Math.abs(this.mPosition - position) == 1) {
            animate(this.mPosition, position);
        } else {
            setIndex(position >> 1);
        }
        this.mPosition = position;
    }

    private void setIndex(int index) {
        int N = getChildCount();
        int i = 0;
        while (i < N) {
            ImageView v = (ImageView) getChildAt(i);
            v.setTranslationX(0.0f);
            v.setImageResource(R.drawable.major_a_b);
            v.setAlpha(getAlpha(i == index));
            i++;
        }
    }

    private void animate(int from, int to) {
        int fromIndex = from >> 1;
        int toIndex = to >> 1;
        setIndex(fromIndex);
        boolean fromTransition = (from & 1) != 0;
        boolean isAState = !fromTransition ? from >= to : from <= to;
        int firstIndex = Math.min(fromIndex, toIndex);
        int secondIndex = Math.max(fromIndex, toIndex);
        if (secondIndex == firstIndex) {
            secondIndex++;
        }
        ImageView first = (ImageView) getChildAt(firstIndex);
        ImageView second = (ImageView) getChildAt(secondIndex);
        if (first == null || second == null) {
            return;
        }
        second.setTranslationX(first.getX() - second.getX());
        playAnimation(first, getTransition(fromTransition, isAState, false));
        first.setAlpha(getAlpha(false));
        playAnimation(second, getTransition(fromTransition, isAState, true));
        second.setAlpha(getAlpha(true));
        this.mAnimating = true;
    }

    private float getAlpha(boolean isMajor) {
        if (isMajor) {
            return 1.0f;
        }
        return MINOR_ALPHA;
    }

    private void playAnimation(ImageView imageView, int res) {
        AnimatedVectorDrawable avd = (AnimatedVectorDrawable) getContext().getDrawable(res);
        imageView.setImageDrawable(avd);
        avd.forceAnimationOnUI();
        avd.start();
        postDelayed(this.mAnimationDone, ANIMATION_DURATION);
    }

    private int getTransition(boolean fromB, boolean isMajorAState, boolean isMajor) {
        if (isMajor) {
            if (fromB) {
                if (isMajorAState) {
                    return R.drawable.major_b_a_animation;
                }
                return R.drawable.major_b_c_animation;
            } else if (isMajorAState) {
                return R.drawable.major_a_b_animation;
            } else {
                return R.drawable.major_c_b_animation;
            }
        } else if (fromB) {
            if (isMajorAState) {
                return R.drawable.minor_b_c_animation;
            }
            return R.drawable.minor_b_a_animation;
        } else if (isMajorAState) {
            return R.drawable.minor_c_b_animation;
        } else {
            return R.drawable.minor_a_b_animation;
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int N = getChildCount();
        if (N == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int widthChildSpec = View.MeasureSpec.makeMeasureSpec(this.mPageIndicatorWidth, 1073741824);
        int heightChildSpec = View.MeasureSpec.makeMeasureSpec(this.mPageIndicatorHeight, 1073741824);
        for (int i = 0; i < N; i++) {
            getChildAt(i).measure(widthChildSpec, heightChildSpec);
        }
        int i2 = this.mPageIndicatorWidth;
        int i3 = this.mPageDotWidth;
        int width = ((i2 - i3) * (N - 1)) + i3;
        setMeasuredDimension(width, this.mPageIndicatorHeight);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int N = getChildCount();
        if (N == 0) {
            return;
        }
        for (int i = 0; i < N; i++) {
            int left = (this.mPageIndicatorWidth - this.mPageDotWidth) * i;
            getChildAt(i).layout(left, 0, this.mPageIndicatorWidth + left, this.mPageIndicatorHeight);
        }
    }
}
