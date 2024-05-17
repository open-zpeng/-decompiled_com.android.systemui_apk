package com.xiaopeng.xui.vui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.IVuiEngine;
import com.xiaopeng.vui.commons.VuiAction;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.vui.commons.model.VuiEvent;
import com.xiaopeng.xui.vui.listenner.VuiRecyclerViewGlobalLayoutListener;
import com.xiaopeng.xui.vui.listenner.VuiRecyclerViewScrollListener;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes25.dex */
public class VuiRecyclerView extends RecyclerView implements VuiView, IVuiElementListener {
    private static final String TAG = "VuiRecyclerView";
    private int UPDATE_DELAY_TIME;
    public boolean isVuiCanScrollDown;
    public boolean isVuiCanScrollRight;
    private final RecyclerView.AdapterDataObserver mAdapterDataObserver;
    private VuiRecyclerViewGlobalLayoutListener mGlobalLayoutListener;
    private Handler mHandler;
    private boolean mIsEnableInvisible;
    private boolean mIsItemChanged;
    private boolean mIsNeedLayout;
    private boolean mIsNeedScroll;
    private boolean mIsReverse;
    private boolean mIsVuiCanControlScroll;
    private int mItemCount;
    private Runnable mRun;
    private String mSceneId;
    private VuiRecyclerViewScrollListener mScrollListener;
    private int mStartPosition;
    private List<String[]> mSupportVuiLabels;
    private IVuiEngine mVuiEngine;

    public VuiRecyclerView(Context context) {
        super(context);
        this.mIsVuiCanControlScroll = true;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.UPDATE_DELAY_TIME = 1000;
        this.mRun = new Runnable() { // from class: com.xiaopeng.xui.vui.VuiRecyclerView.1
            @Override // java.lang.Runnable
            public void run() {
                if (VuiRecyclerView.this.mVuiEngine != null && !TextUtils.isEmpty(VuiRecyclerView.this.mSceneId)) {
                    VuiRecyclerView.this.mVuiEngine.updateScene(VuiRecyclerView.this.mSceneId, VuiRecyclerView.this);
                }
            }
        };
        this.mIsNeedScroll = true;
        this.mIsNeedLayout = true;
        this.mIsItemChanged = false;
        this.mAdapterDataObserver = new RecyclerView.AdapterDataObserver() { // from class: com.xiaopeng.xui.vui.VuiRecyclerView.2
            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                if (VuiRecyclerView.this.mIsNeedLayout && VuiRecyclerView.this.mGlobalLayoutListener != null) {
                    VuiRecyclerView.this.mGlobalLayoutListener.setUpdateNum();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeChanged(int positionStart, int itemCount) {
                if (VuiRecyclerView.this.mIsItemChanged) {
                    VuiRecyclerView.this.updateVuiScene();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                if (VuiRecyclerView.this.mIsItemChanged) {
                    VuiRecyclerView.this.updateVuiScene();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (VuiRecyclerView.this.mIsNeedLayout && VuiRecyclerView.this.mGlobalLayoutListener != null) {
                    VuiRecyclerView.this.mGlobalLayoutListener.setUpdateNum();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (VuiRecyclerView.this.mIsNeedLayout && VuiRecyclerView.this.mGlobalLayoutListener != null) {
                    VuiRecyclerView.this.mGlobalLayoutListener.setUpdateNum();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                if (VuiRecyclerView.this.mIsNeedLayout && VuiRecyclerView.this.mGlobalLayoutListener != null) {
                    VuiRecyclerView.this.mGlobalLayoutListener.setUpdateNum();
                }
            }
        };
        this.isVuiCanScrollDown = false;
        this.isVuiCanScrollRight = false;
        this.mIsEnableInvisible = false;
        this.mStartPosition = 0;
        this.mItemCount = -1;
        this.mIsReverse = false;
        this.mSupportVuiLabels = null;
        initListener();
    }

    public VuiRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsVuiCanControlScroll = true;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.UPDATE_DELAY_TIME = 1000;
        this.mRun = new Runnable() { // from class: com.xiaopeng.xui.vui.VuiRecyclerView.1
            @Override // java.lang.Runnable
            public void run() {
                if (VuiRecyclerView.this.mVuiEngine != null && !TextUtils.isEmpty(VuiRecyclerView.this.mSceneId)) {
                    VuiRecyclerView.this.mVuiEngine.updateScene(VuiRecyclerView.this.mSceneId, VuiRecyclerView.this);
                }
            }
        };
        this.mIsNeedScroll = true;
        this.mIsNeedLayout = true;
        this.mIsItemChanged = false;
        this.mAdapterDataObserver = new RecyclerView.AdapterDataObserver() { // from class: com.xiaopeng.xui.vui.VuiRecyclerView.2
            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                if (VuiRecyclerView.this.mIsNeedLayout && VuiRecyclerView.this.mGlobalLayoutListener != null) {
                    VuiRecyclerView.this.mGlobalLayoutListener.setUpdateNum();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeChanged(int positionStart, int itemCount) {
                if (VuiRecyclerView.this.mIsItemChanged) {
                    VuiRecyclerView.this.updateVuiScene();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                if (VuiRecyclerView.this.mIsItemChanged) {
                    VuiRecyclerView.this.updateVuiScene();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (VuiRecyclerView.this.mIsNeedLayout && VuiRecyclerView.this.mGlobalLayoutListener != null) {
                    VuiRecyclerView.this.mGlobalLayoutListener.setUpdateNum();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (VuiRecyclerView.this.mIsNeedLayout && VuiRecyclerView.this.mGlobalLayoutListener != null) {
                    VuiRecyclerView.this.mGlobalLayoutListener.setUpdateNum();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                if (VuiRecyclerView.this.mIsNeedLayout && VuiRecyclerView.this.mGlobalLayoutListener != null) {
                    VuiRecyclerView.this.mGlobalLayoutListener.setUpdateNum();
                }
            }
        };
        this.isVuiCanScrollDown = false;
        this.isVuiCanScrollRight = false;
        this.mIsEnableInvisible = false;
        this.mStartPosition = 0;
        this.mItemCount = -1;
        this.mIsReverse = false;
        this.mSupportVuiLabels = null;
        initVui(this, attrs);
        initListener();
    }

    public VuiRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIsVuiCanControlScroll = true;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.UPDATE_DELAY_TIME = 1000;
        this.mRun = new Runnable() { // from class: com.xiaopeng.xui.vui.VuiRecyclerView.1
            @Override // java.lang.Runnable
            public void run() {
                if (VuiRecyclerView.this.mVuiEngine != null && !TextUtils.isEmpty(VuiRecyclerView.this.mSceneId)) {
                    VuiRecyclerView.this.mVuiEngine.updateScene(VuiRecyclerView.this.mSceneId, VuiRecyclerView.this);
                }
            }
        };
        this.mIsNeedScroll = true;
        this.mIsNeedLayout = true;
        this.mIsItemChanged = false;
        this.mAdapterDataObserver = new RecyclerView.AdapterDataObserver() { // from class: com.xiaopeng.xui.vui.VuiRecyclerView.2
            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                if (VuiRecyclerView.this.mIsNeedLayout && VuiRecyclerView.this.mGlobalLayoutListener != null) {
                    VuiRecyclerView.this.mGlobalLayoutListener.setUpdateNum();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeChanged(int positionStart, int itemCount) {
                if (VuiRecyclerView.this.mIsItemChanged) {
                    VuiRecyclerView.this.updateVuiScene();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                if (VuiRecyclerView.this.mIsItemChanged) {
                    VuiRecyclerView.this.updateVuiScene();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (VuiRecyclerView.this.mIsNeedLayout && VuiRecyclerView.this.mGlobalLayoutListener != null) {
                    VuiRecyclerView.this.mGlobalLayoutListener.setUpdateNum();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (VuiRecyclerView.this.mIsNeedLayout && VuiRecyclerView.this.mGlobalLayoutListener != null) {
                    VuiRecyclerView.this.mGlobalLayoutListener.setUpdateNum();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                if (VuiRecyclerView.this.mIsNeedLayout && VuiRecyclerView.this.mGlobalLayoutListener != null) {
                    VuiRecyclerView.this.mGlobalLayoutListener.setUpdateNum();
                }
            }
        };
        this.isVuiCanScrollDown = false;
        this.isVuiCanScrollRight = false;
        this.mIsEnableInvisible = false;
        this.mStartPosition = 0;
        this.mItemCount = -1;
        this.mIsReverse = false;
        this.mSupportVuiLabels = null;
        initVui(this, attrs);
        initListener();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        VuiRecyclerViewScrollListener vuiRecyclerViewScrollListener;
        super.onAttachedToWindow();
        logD("onAttachedToWindow:" + this.mSceneId);
        if (this.mIsNeedLayout && this.mGlobalLayoutListener != null) {
            getViewTreeObserver().addOnGlobalLayoutListener(this.mGlobalLayoutListener);
        }
        if (this.mIsNeedScroll && (vuiRecyclerViewScrollListener = this.mScrollListener) != null) {
            addOnScrollListener(vuiRecyclerViewScrollListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        VuiRecyclerViewScrollListener vuiRecyclerViewScrollListener;
        super.onDetachedFromWindow();
        logD("onDetachedFromWindow:" + this.mSceneId);
        if (this.mIsNeedLayout && this.mGlobalLayoutListener != null) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this.mGlobalLayoutListener);
        }
        if (this.mIsNeedScroll && (vuiRecyclerViewScrollListener = this.mScrollListener) != null) {
            removeOnScrollListener(vuiRecyclerViewScrollListener);
        }
        this.mHandler.removeCallbacks(this.mRun);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        releaseVui();
    }

    private void initListener() {
        this.mGlobalLayoutListener = new VuiRecyclerViewGlobalLayoutListener(this);
        this.mScrollListener = new VuiRecyclerViewScrollListener(this);
        setVuiLayoutLoadable(true);
    }

    public void updateVuiScene() {
        if (!TextUtils.isEmpty(this.mSceneId) && this.mVuiEngine != null) {
            this.mHandler.removeCallbacks(this.mRun);
            this.mHandler.postDelayed(this.mRun, this.UPDATE_DELAY_TIME);
            return;
        }
        logD("updateVuiScene sceneid is empty");
    }

    public void updateVuiScene(int time) {
        if (!TextUtils.isEmpty(this.mSceneId) && this.mVuiEngine != null) {
            this.mHandler.removeCallbacks(this.mRun);
            this.mHandler.postDelayed(this.mRun, time);
            return;
        }
        logD("updateVuiScene sceneid is empty");
    }

    public void initVuiAttr(String sceneId, IVuiEngine vuiEngine) {
        this.mSceneId = sceneId;
        this.mVuiEngine = vuiEngine;
    }

    public void initVuiAttr(String sceneId, IVuiEngine vuiEngine, boolean isItemChanged) {
        this.mSceneId = sceneId;
        this.mVuiEngine = vuiEngine;
        this.mIsItemChanged = isItemChanged;
    }

    public void initVuiAttr(String sceneId, IVuiEngine vuiEngine, boolean isNeedScroll, boolean isNeedLayout) {
        this.mSceneId = sceneId;
        this.mIsNeedScroll = isNeedScroll;
        this.mIsNeedLayout = isNeedLayout;
        this.mVuiEngine = vuiEngine;
    }

    public void initVuiAttr(String sceneId, IVuiEngine vuiEngine, boolean isNeedScroll, boolean isNeedLayout, boolean isItemChanged) {
        this.mSceneId = sceneId;
        this.mIsNeedScroll = isNeedScroll;
        this.mIsNeedLayout = isNeedLayout;
        this.mVuiEngine = vuiEngine;
        this.mIsItemChanged = isItemChanged;
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void setAdapter(RecyclerView.Adapter adapter) {
        VuiRecyclerViewGlobalLayoutListener vuiRecyclerViewGlobalLayoutListener;
        RecyclerView.Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(this.mAdapterDataObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.mAdapterDataObserver);
        }
        if (this.mIsNeedLayout && (vuiRecyclerViewGlobalLayoutListener = this.mGlobalLayoutListener) != null) {
            vuiRecyclerViewGlobalLayoutListener.setUpdateNum();
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public VuiElement onBuildVuiElement(String s, IVuiElementBuilder builder) {
        RecyclerView.LayoutManager layoutManager;
        if (this.mIsVuiCanControlScroll) {
            boolean canScrollUp = canScrollVertically(-1);
            boolean z = true;
            boolean canScrollDown = canScrollVertically(1);
            boolean canScrollLeft = canScrollHorizontally(-1);
            boolean canScrollRight = canScrollHorizontally(1);
            if (canScrollUp || canScrollDown || this.isVuiCanScrollDown) {
                setVuiAction(VuiAction.SCROLLBYY.getName());
            } else if (canScrollLeft || canScrollRight || this.isVuiCanScrollRight) {
                setVuiAction(VuiAction.SCROLLBYX.getName());
            }
            try {
                JSONObject jsonObject = getVuiProps();
                if (jsonObject == null) {
                    jsonObject = new JSONObject();
                }
                if (this.mIsEnableInvisible && (layoutManager = getLayoutManager()) != null && (layoutManager instanceof LinearLayoutManager)) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    jsonObject.put(VuiConstants.PROPS_MINVALUE, this.mStartPosition);
                    int maxValue = this.mItemCount != -1 ? this.mStartPosition + this.mItemCount : this.mStartPosition + layoutManager.getItemCount();
                    jsonObject.put(VuiConstants.PROPS_MAXVALUE, maxValue);
                    jsonObject.put("displayStart", this.mIsReverse ? maxValue - linearLayoutManager.findLastCompletelyVisibleItemPosition() : this.mStartPosition + linearLayoutManager.findFirstCompletelyVisibleItemPosition());
                    jsonObject.put("displayEnd", this.mIsReverse ? maxValue - linearLayoutManager.findFirstCompletelyVisibleItemPosition() : this.mStartPosition + linearLayoutManager.findLastCompletelyVisibleItemPosition());
                    if (this.mSupportVuiLabels != null && this.mSupportVuiLabels.size() > 0) {
                        jsonObject.put("supportVuiLabels", this.mSupportVuiLabels);
                    }
                    jsonObject.put("isReverse", this.mIsReverse);
                }
                if (getVuiAction() == null) {
                    return null;
                }
                if (getVuiAction().equals(VuiAction.SCROLLBYY.getName())) {
                    jsonObject.put(VuiConstants.PROPS_SCROLLUP, canScrollUp);
                    if (!canScrollDown && !this.isVuiCanScrollDown) {
                        z = false;
                    }
                    jsonObject.put(VuiConstants.PROPS_SCROLLDOWN, z);
                } else {
                    jsonObject.put(VuiConstants.PROPS_SCROLLLEFT, canScrollLeft);
                    if (!canScrollRight && !this.isVuiCanScrollRight) {
                        z = false;
                    }
                    jsonObject.put(VuiConstants.PROPS_SCROLLRIGHT, z);
                }
                setVuiProps(jsonObject);
                return null;
            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public boolean onVuiElementEvent(View view, VuiEvent vuiEvent) {
        return false;
    }

    public void setCanVuiScrollDown(boolean canScrollDown) {
        this.isVuiCanScrollDown = canScrollDown;
    }

    public void setCanVuiScrollRight(boolean canScrollRight) {
        this.isVuiCanScrollRight = canScrollRight;
    }

    public void setVuiCanControlScroll(boolean vuiCanControlScroll) {
        this.mIsVuiCanControlScroll = vuiCanControlScroll;
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (!this.mIsNeedLayout) {
            updateVuiScene();
        }
    }

    public void enableVuiInvisibleFeature(int startPosition) {
        logI("enableVuiInvisibleFeature:" + startPosition);
        this.mIsEnableInvisible = true;
        this.mStartPosition = startPosition;
    }

    public void enableVuiInvisibleFeature(int startPosition, List<String[]> supportVuiLabels) {
        this.mIsEnableInvisible = true;
        this.mStartPosition = startPosition;
        this.mSupportVuiLabels = supportVuiLabels;
    }

    public void enableVuiInvisibleFeature(int startPosition, int itemCount) {
        logD("enableVuiInvisibleFeature:" + startPosition);
        this.mIsEnableInvisible = true;
        this.mStartPosition = startPosition;
        this.mItemCount = itemCount;
    }

    public void setOrderState(boolean isReverse) {
        this.mIsReverse = isReverse;
    }
}
