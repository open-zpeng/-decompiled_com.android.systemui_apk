package com.android.systemui.qs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Scroller;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.systemui.R;
import com.android.systemui.qs.QSPanel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
/* loaded from: classes21.dex */
public class PagedTileLayout extends ViewPager implements QSPanel.QSTileLayout {
    private static final long BOUNCE_ANIMATION_DURATION = 450;
    private static final float BOUNCE_ANIMATION_TENSION = 1.3f;
    private static final String CURRENT_PAGE = "current_page";
    private static final boolean DEBUG = false;
    private static final int REVEAL_SCROLL_DURATION_MILLIS = 750;
    private static final Interpolator SCROLL_CUBIC = new Interpolator() { // from class: com.android.systemui.qs.-$$Lambda$PagedTileLayout$fHkBmUM3ca-ZV4_eDd9ap-VT7Ho
        @Override // android.animation.TimeInterpolator
        public final float getInterpolation(float f) {
            return PagedTileLayout.lambda$static$0(f);
        }
    };
    private static final String TAG = "PagedTileLayout";
    private static final int TILE_ANIMATION_STAGGER_DELAY = 85;
    private final PagerAdapter mAdapter;
    private AnimatorSet mBounceAnimatorSet;
    private final Rect mClippingRect;
    private boolean mDistributeTiles;
    private int mHorizontalClipBound;
    private float mLastExpansion;
    private int mLastMaxHeight;
    private int mLayoutDirection;
    private int mLayoutOrientation;
    private boolean mListening;
    private final ViewPager.OnPageChangeListener mOnPageChangeListener;
    private PageIndicator mPageIndicator;
    private float mPageIndicatorPosition;
    private PageListener mPageListener;
    private int mPageToRestore;
    private final ArrayList<TilePage> mPages;
    private Scroller mScroller;
    private final ArrayList<QSPanel.TileRecord> mTiles;

    /* loaded from: classes21.dex */
    public interface PageListener {
        void onPageChanged(boolean z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ float lambda$static$0(float t) {
        float t2 = t - 1.0f;
        return (t2 * t2 * t2) + 1.0f;
    }

    public PagedTileLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTiles = new ArrayList<>();
        this.mPages = new ArrayList<>();
        this.mDistributeTiles = false;
        this.mPageToRestore = -1;
        this.mLastMaxHeight = -1;
        this.mOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() { // from class: com.android.systemui.qs.PagedTileLayout.2
            @Override // androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener, androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int position) {
                PagedTileLayout.this.updateSelected();
                if (PagedTileLayout.this.mPageIndicator != null && PagedTileLayout.this.mPageListener != null) {
                    PageListener pageListener = PagedTileLayout.this.mPageListener;
                    boolean z = false;
                    if (PagedTileLayout.this.isLayoutRtl()) {
                        if (position == PagedTileLayout.this.mPages.size() - 1) {
                            z = true;
                        }
                    } else if (position == 0) {
                        z = true;
                    }
                    pageListener.onPageChanged(z);
                }
            }

            @Override // androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener, androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (PagedTileLayout.this.mPageIndicator == null) {
                    return;
                }
                PagedTileLayout.this.mPageIndicatorPosition = position + positionOffset;
                PagedTileLayout.this.mPageIndicator.setLocation(PagedTileLayout.this.mPageIndicatorPosition);
                if (PagedTileLayout.this.mPageListener != null) {
                    PageListener pageListener = PagedTileLayout.this.mPageListener;
                    boolean z = true;
                    if (positionOffsetPixels != 0 || (!PagedTileLayout.this.isLayoutRtl() ? position != 0 : position != PagedTileLayout.this.mPages.size() - 1)) {
                        z = false;
                    }
                    pageListener.onPageChanged(z);
                }
            }
        };
        this.mAdapter = new PagerAdapter() { // from class: com.android.systemui.qs.PagedTileLayout.3
            @Override // androidx.viewpager.widget.PagerAdapter
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
                PagedTileLayout.this.updateListening();
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public Object instantiateItem(ViewGroup container, int position) {
                if (PagedTileLayout.this.isLayoutRtl()) {
                    position = (PagedTileLayout.this.mPages.size() - 1) - position;
                }
                ViewGroup view = (ViewGroup) PagedTileLayout.this.mPages.get(position);
                if (view.getParent() != null) {
                    container.removeView(view);
                }
                container.addView(view);
                PagedTileLayout.this.updateListening();
                return view;
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public int getCount() {
                return PagedTileLayout.this.mPages.size();
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        };
        this.mScroller = new Scroller(context, SCROLL_CUBIC);
        setAdapter(this.mAdapter);
        setOnPageChangeListener(this.mOnPageChangeListener);
        setCurrentItem(0, false);
        this.mLayoutOrientation = getResources().getConfiguration().orientation;
        this.mLayoutDirection = getLayoutDirection();
        this.mClippingRect = new Rect();
    }

    @Override // com.android.systemui.qs.QSPanel.QSTileLayout
    public void saveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_PAGE, getCurrentItem());
    }

    @Override // com.android.systemui.qs.QSPanel.QSTileLayout
    public void restoreInstanceState(Bundle savedInstanceState) {
        this.mPageToRestore = savedInstanceState.getInt(CURRENT_PAGE, -1);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.mLayoutOrientation != newConfig.orientation) {
            this.mLayoutOrientation = newConfig.orientation;
            setCurrentItem(0, false);
            this.mPageToRestore = 0;
        }
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        if (this.mLayoutDirection != layoutDirection) {
            this.mLayoutDirection = layoutDirection;
            setAdapter(this.mAdapter);
            setCurrentItem(0, false);
            this.mPageToRestore = 0;
        }
    }

    @Override // androidx.viewpager.widget.ViewPager
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (isLayoutRtl()) {
            item = (this.mPages.size() - 1) - item;
        }
        super.setCurrentItem(item, smoothScroll);
    }

    private int getCurrentPageNumber() {
        int page = getCurrentItem();
        if (this.mLayoutDirection == 1) {
            return (this.mPages.size() - 1) - page;
        }
        return page;
    }

    @Override // com.android.systemui.qs.QSPanel.QSTileLayout
    public void setListening(boolean listening) {
        if (this.mListening == listening) {
            return;
        }
        this.mListening = listening;
        updateListening();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateListening() {
        Iterator<TilePage> it = this.mPages.iterator();
        while (it.hasNext()) {
            TilePage tilePage = it.next();
            tilePage.setListening(tilePage.getParent() == null ? false : this.mListening);
        }
    }

    @Override // androidx.viewpager.widget.ViewPager, android.view.View
    public void computeScroll() {
        if (!this.mScroller.isFinished() && this.mScroller.computeScrollOffset()) {
            fakeDragBy(getScrollX() - this.mScroller.getCurrX());
            postInvalidateOnAnimation();
            return;
        }
        if (isFakeDragging()) {
            endFakeDrag();
            this.mBounceAnimatorSet.start();
            setOffscreenPageLimit(1);
        }
        super.computeScroll();
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mPages.add((TilePage) LayoutInflater.from(getContext()).inflate(R.layout.qs_paged_page, (ViewGroup) this, false));
        this.mAdapter.notifyDataSetChanged();
    }

    public void setPageIndicator(PageIndicator indicator) {
        this.mPageIndicator = indicator;
        this.mPageIndicator.setNumPages(this.mPages.size());
        this.mPageIndicator.setLocation(this.mPageIndicatorPosition);
    }

    @Override // com.android.systemui.qs.QSPanel.QSTileLayout
    public int getOffsetTop(QSPanel.TileRecord tile) {
        ViewGroup parent = (ViewGroup) tile.tileView.getParent();
        if (parent == null) {
            return 0;
        }
        return parent.getTop() + getTop();
    }

    @Override // com.android.systemui.qs.QSPanel.QSTileLayout
    public void addTile(QSPanel.TileRecord tile) {
        this.mTiles.add(tile);
        this.mDistributeTiles = true;
        requestLayout();
    }

    @Override // com.android.systemui.qs.QSPanel.QSTileLayout
    public void removeTile(QSPanel.TileRecord tile) {
        if (this.mTiles.remove(tile)) {
            this.mDistributeTiles = true;
            requestLayout();
        }
    }

    @Override // com.android.systemui.qs.QSPanel.QSTileLayout
    public void setExpansion(float expansion) {
        this.mLastExpansion = expansion;
        updateSelected();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSelected() {
        float f = this.mLastExpansion;
        if (f > 0.0f && f < 1.0f) {
            return;
        }
        boolean selected = this.mLastExpansion == 1.0f;
        setImportantForAccessibility(4);
        int currentItem = getCurrentPageNumber();
        int i = 0;
        while (i < this.mPages.size()) {
            this.mPages.get(i).setSelected(i == currentItem ? selected : false);
            i++;
        }
        setImportantForAccessibility(0);
    }

    public void setPageListener(PageListener listener) {
        this.mPageListener = listener;
    }

    private void distributeTiles() {
        emptyAndInflateOrRemovePages();
        int tileCount = this.mPages.get(0).maxTiles();
        int index = 0;
        int NT = this.mTiles.size();
        for (int i = 0; i < NT; i++) {
            QSPanel.TileRecord tile = this.mTiles.get(i);
            if (this.mPages.get(index).mRecords.size() == tileCount) {
                index++;
            }
            this.mPages.get(index).addTile(tile);
        }
    }

    private void emptyAndInflateOrRemovePages() {
        int nTiles = this.mTiles.size();
        int numPages = Math.max(nTiles / this.mPages.get(0).maxTiles(), 1);
        if (nTiles > this.mPages.get(0).maxTiles() * numPages) {
            numPages++;
        }
        int NP = this.mPages.size();
        for (int i = 0; i < NP; i++) {
            this.mPages.get(i).removeAllViews();
        }
        if (NP == numPages) {
            return;
        }
        while (this.mPages.size() < numPages) {
            this.mPages.add((TilePage) LayoutInflater.from(getContext()).inflate(R.layout.qs_paged_page, (ViewGroup) this, false));
        }
        while (this.mPages.size() > numPages) {
            ArrayList<TilePage> arrayList = this.mPages;
            arrayList.remove(arrayList.size() - 1);
        }
        this.mPageIndicator.setNumPages(this.mPages.size());
        setAdapter(this.mAdapter);
        this.mAdapter.notifyDataSetChanged();
        int i2 = this.mPageToRestore;
        if (i2 != -1) {
            setCurrentItem(i2, false);
            this.mPageToRestore = -1;
        }
    }

    @Override // com.android.systemui.qs.QSPanel.QSTileLayout
    public boolean updateResources() {
        Resources res = getContext().getResources();
        this.mHorizontalClipBound = res.getDimensionPixelSize(R.dimen.notification_side_paddings);
        setPadding(0, 0, 0, getContext().getResources().getDimensionPixelSize(R.dimen.qs_paged_tile_layout_padding_bottom));
        boolean changed = false;
        for (int i = 0; i < this.mPages.size(); i++) {
            changed |= this.mPages.get(i).updateResources();
        }
        if (changed) {
            this.mDistributeTiles = true;
            requestLayout();
        }
        return changed;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.viewpager.widget.ViewPager, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Rect rect = this.mClippingRect;
        int i = this.mHorizontalClipBound;
        rect.set(i, 0, (r - l) - i, b - t);
        setClipBounds(this.mClippingRect);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.viewpager.widget.ViewPager, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int nTiles = this.mTiles.size();
        if (this.mDistributeTiles || this.mLastMaxHeight != View.MeasureSpec.getSize(heightMeasureSpec)) {
            this.mLastMaxHeight = View.MeasureSpec.getSize(heightMeasureSpec);
            if (this.mPages.get(0).updateMaxRows(heightMeasureSpec, nTiles) || this.mDistributeTiles) {
                this.mDistributeTiles = false;
                distributeTiles();
            }
            int nRows = this.mPages.get(0).mRows;
            for (int i = 0; i < this.mPages.size(); i++) {
                TilePage t = this.mPages.get(i);
                t.mRows = nRows;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int maxHeight = 0;
        int N = getChildCount();
        for (int i2 = 0; i2 < N; i2++) {
            int height = getChildAt(i2).getMeasuredHeight();
            if (height > maxHeight) {
                maxHeight = height;
            }
        }
        int i3 = getMeasuredWidth();
        setMeasuredDimension(i3, getPaddingBottom() + maxHeight);
    }

    public int getColumnCount() {
        if (this.mPages.size() == 0) {
            return 0;
        }
        return this.mPages.get(0).mColumns;
    }

    @Override // com.android.systemui.qs.QSPanel.QSTileLayout
    public int getNumVisibleTiles() {
        if (this.mPages.size() == 0) {
            return 0;
        }
        TilePage currentPage = this.mPages.get(getCurrentPageNumber());
        return currentPage.mRecords.size();
    }

    public void startTileReveal(Set<String> tileSpecs, final Runnable postAnimation) {
        if (tileSpecs.isEmpty() || this.mPages.size() < 2 || getScrollX() != 0 || !beginFakeDrag()) {
            return;
        }
        int lastPageNumber = this.mPages.size() - 1;
        TilePage lastPage = this.mPages.get(lastPageNumber);
        ArrayList<Animator> bounceAnims = new ArrayList<>();
        Iterator<QSPanel.TileRecord> it = lastPage.mRecords.iterator();
        while (it.hasNext()) {
            QSPanel.TileRecord tr = it.next();
            if (tileSpecs.contains(tr.tile.getTileSpec())) {
                bounceAnims.add(setupBounceAnimator(tr.tileView, bounceAnims.size()));
            }
        }
        if (bounceAnims.isEmpty()) {
            endFakeDrag();
            return;
        }
        this.mBounceAnimatorSet = new AnimatorSet();
        this.mBounceAnimatorSet.playTogether(bounceAnims);
        this.mBounceAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.qs.PagedTileLayout.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PagedTileLayout.this.mBounceAnimatorSet = null;
                postAnimation.run();
            }
        });
        setOffscreenPageLimit(lastPageNumber);
        int dx = getWidth() * lastPageNumber;
        this.mScroller.startScroll(getScrollX(), getScrollY(), isLayoutRtl() ? -dx : dx, 0, REVEAL_SCROLL_DURATION_MILLIS);
        postInvalidateOnAnimation();
    }

    private static Animator setupBounceAnimator(View view, int ordinal) {
        view.setAlpha(0.0f);
        view.setScaleX(0.0f);
        view.setScaleY(0.0f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat(View.ALPHA, 1.0f), PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f), PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f));
        animator.setDuration(BOUNCE_ANIMATION_DURATION);
        animator.setStartDelay(ordinal * 85);
        animator.setInterpolator(new OvershootInterpolator(1.3f));
        return animator;
    }

    /* loaded from: classes21.dex */
    public static class TilePage extends TileLayout {
        public TilePage(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public boolean isFull() {
            return this.mRecords.size() >= maxTiles();
        }

        public int maxTiles() {
            return Math.max(this.mColumns * this.mRows, 1);
        }

        @Override // com.android.systemui.qs.TileLayout, com.android.systemui.qs.QSPanel.QSTileLayout
        public boolean updateResources() {
            int sidePadding = getContext().getResources().getDimensionPixelSize(R.dimen.notification_side_paddings);
            setPadding(sidePadding, 0, sidePadding, 0);
            return super.updateResources();
        }
    }
}
