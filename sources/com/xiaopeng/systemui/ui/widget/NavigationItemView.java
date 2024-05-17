package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.rastermill.FrameSequenceUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
/* loaded from: classes24.dex */
public class NavigationItemView extends AlphaOptimizedRelativeLayout {
    private static final String TAG = "NavigationItemView";
    private AlphaOptimizedRelativeLayout mBackground;
    private boolean mEnableSelected;
    private int mImageDrawableId;
    protected AnimatedImageView mImageView;
    private int mPressedDrawableId;
    private int mSelectedDrawableId;

    public NavigationItemView(Context context) {
        super(context);
        this.mEnableSelected = false;
        this.mImageDrawableId = 0;
        this.mPressedDrawableId = 0;
        this.mSelectedDrawableId = 0;
        init(context, null, 0, 0);
    }

    public NavigationItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mEnableSelected = false;
        this.mImageDrawableId = 0;
        this.mPressedDrawableId = 0;
        this.mSelectedDrawableId = 0;
        init(context, attrs, 0, 0);
    }

    public NavigationItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mEnableSelected = false;
        this.mImageDrawableId = 0;
        this.mPressedDrawableId = 0;
        this.mSelectedDrawableId = 0;
        init(context, attrs, defStyleAttr, 0);
    }

    public NavigationItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mEnableSelected = false;
        this.mImageDrawableId = 0;
        this.mPressedDrawableId = 0;
        this.mSelectedDrawableId = 0;
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        LayoutInflater.from(context).inflate(R.layout.view_navigation_item, this);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.NavigationItemView, defStyleAttr, 0);
        this.mEnableSelected = attributes.getBoolean(0, this.mEnableSelected);
        this.mImageDrawableId = attributes.getResourceId(4, 0);
        this.mPressedDrawableId = attributes.getResourceId(5, 0);
        this.mSelectedDrawableId = attributes.getResourceId(6, 0);
        attributes.recycle();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mImageView = (AnimatedImageView) findViewById(R.id.id_image_view);
        this.mBackground = (AlphaOptimizedRelativeLayout) findViewById(R.id.layout_background);
        updateAttributeSet();
    }

    private void updateAttributeSet() {
        int i = this.mImageDrawableId;
        if (i != 0) {
            setImageResource(i);
        }
    }

    public void setFocused(boolean focused) {
    }

    public void setImageResource(int resId) {
        if (resId != 0) {
            this.mImageView.setImageResource(resId);
            this.mImageView.setImageDrawable(getResources().getDrawable(resId));
        }
    }

    public void setImageLevel(int level) {
        this.mImageView.setImageLevel(level);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    public void startAnimation(int resourceId, int finishId) {
        Logger.d(TAG, "showAnimation : resourceId = " + resourceId + " finishId = " + finishId);
        if (finishId == 0) {
            FrameSequenceUtil.with(this.mImageView).resourceId(resourceId).applyAsync();
        } else {
            FrameSequenceUtil.with(this.mImageView).resourceId(resourceId).finish(finishId).applyAsync();
        }
    }
}
