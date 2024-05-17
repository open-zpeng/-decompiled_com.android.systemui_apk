package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.R;
import com.xiaopeng.speech.vui.VuiEngine;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.infoflow.util.CommonUtils;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.VuiAction;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.VuiFeedbackType;
import com.xiaopeng.vui.commons.VuiMode;
import com.xiaopeng.vui.commons.VuiPriority;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.vui.commons.model.VuiEvent;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class XRecyclerView extends RecyclerView implements IVuiElement, IVuiElementListener {
    private static final String TAG = "XRecyclerView";
    private Context context;
    private final RecyclerView.AdapterDataObserver mAdapterDataObserver;
    private boolean mIsDataChange;
    private int mMaxHeight;
    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private Runnable mRun;
    private String mSceneId;
    private boolean performVuiAction;
    private String vuiAction;
    private boolean vuiDynamic;
    private String vuiElementId;
    private VuiElementType vuiElementType;
    private boolean vuiEnabled;
    private String vuiFatherElementId;
    private String vuiFatherLabel;
    private VuiFeedbackType vuiFeedbackType;
    private String vuiLabel;
    private int vuiPosition;
    private VuiPriority vuiPriority;
    private JSONObject vuiProps;

    public XRecyclerView(Context context) {
        super(context);
        this.mIsDataChange = false;
        this.mMaxHeight = -1;
        this.mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.xiaopeng.systemui.ui.widget.XRecyclerView.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                if (XRecyclerView.this.mIsDataChange) {
                    XRecyclerView.this.updateVuiScene(500);
                    XRecyclerView.this.mIsDataChange = false;
                }
            }
        };
        this.mOnScrollListener = new RecyclerView.OnScrollListener() { // from class: com.xiaopeng.systemui.ui.widget.XRecyclerView.2
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    XRecyclerView.this.updateVuiScene(200);
                }
            }
        };
        this.mRun = new Runnable() { // from class: com.xiaopeng.systemui.ui.widget.XRecyclerView.3
            @Override // java.lang.Runnable
            public void run() {
                VuiEngine.getInstance(XRecyclerView.this.getContext()).updateScene(XRecyclerView.this.mSceneId, XRecyclerView.this);
            }
        };
        this.mAdapterDataObserver = new RecyclerView.AdapterDataObserver() { // from class: com.xiaopeng.systemui.ui.widget.XRecyclerView.4
            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                XRecyclerView.this.mIsDataChange = true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int positionStart, int itemCount) {
                XRecyclerView.this.mIsDataChange = true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                XRecyclerView.this.mIsDataChange = true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                XRecyclerView.this.mIsDataChange = true;
            }
        };
        this.mSceneId = null;
    }

    public XRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsDataChange = false;
        this.mMaxHeight = -1;
        this.mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.xiaopeng.systemui.ui.widget.XRecyclerView.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                if (XRecyclerView.this.mIsDataChange) {
                    XRecyclerView.this.updateVuiScene(500);
                    XRecyclerView.this.mIsDataChange = false;
                }
            }
        };
        this.mOnScrollListener = new RecyclerView.OnScrollListener() { // from class: com.xiaopeng.systemui.ui.widget.XRecyclerView.2
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    XRecyclerView.this.updateVuiScene(200);
                }
            }
        };
        this.mRun = new Runnable() { // from class: com.xiaopeng.systemui.ui.widget.XRecyclerView.3
            @Override // java.lang.Runnable
            public void run() {
                VuiEngine.getInstance(XRecyclerView.this.getContext()).updateScene(XRecyclerView.this.mSceneId, XRecyclerView.this);
            }
        };
        this.mAdapterDataObserver = new RecyclerView.AdapterDataObserver() { // from class: com.xiaopeng.systemui.ui.widget.XRecyclerView.4
            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                XRecyclerView.this.mIsDataChange = true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int positionStart, int itemCount) {
                XRecyclerView.this.mIsDataChange = true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                XRecyclerView.this.mIsDataChange = true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                XRecyclerView.this.mIsDataChange = true;
            }
        };
        this.mSceneId = null;
        this.context = context;
        init(attrs);
    }

    public XRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIsDataChange = false;
        this.mMaxHeight = -1;
        this.mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.xiaopeng.systemui.ui.widget.XRecyclerView.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                if (XRecyclerView.this.mIsDataChange) {
                    XRecyclerView.this.updateVuiScene(500);
                    XRecyclerView.this.mIsDataChange = false;
                }
            }
        };
        this.mOnScrollListener = new RecyclerView.OnScrollListener() { // from class: com.xiaopeng.systemui.ui.widget.XRecyclerView.2
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    XRecyclerView.this.updateVuiScene(200);
                }
            }
        };
        this.mRun = new Runnable() { // from class: com.xiaopeng.systemui.ui.widget.XRecyclerView.3
            @Override // java.lang.Runnable
            public void run() {
                VuiEngine.getInstance(XRecyclerView.this.getContext()).updateScene(XRecyclerView.this.mSceneId, XRecyclerView.this);
            }
        };
        this.mAdapterDataObserver = new RecyclerView.AdapterDataObserver() { // from class: com.xiaopeng.systemui.ui.widget.XRecyclerView.4
            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                XRecyclerView.this.mIsDataChange = true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int positionStart, int itemCount) {
                XRecyclerView.this.mIsDataChange = true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                XRecyclerView.this.mIsDataChange = true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                XRecyclerView.this.mIsDataChange = true;
            }
        };
        this.mSceneId = null;
        this.context = context;
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        TypedArray ta = this.context.obtainStyledAttributes(attrs, R.styleable.xui);
        this.vuiAction = ta.getString(1);
        this.vuiElementType = CommonUtils.getElementType(ta.getInteger(4, -1));
        if (this.vuiElementType.equals(VuiElementType.UNKNOWN)) {
            this.vuiElementType = VuiElementType.RECYCLEVIEW;
        }
        this.vuiFatherElementId = ta.getString(6);
        this.vuiLabel = ta.getString(9);
        this.vuiFatherLabel = ta.getString(7);
        this.vuiElementId = ta.getString(3);
        this.vuiPosition = ta.getInteger(10, -1);
        this.vuiDynamic = ta.getBoolean(2, false);
        this.vuiEnabled = ta.getBoolean(5, true);
        int priority = ta.getInt(11, 2);
        this.vuiPriority = CommonUtils.getViewLeveByPriority(priority);
        this.vuiFeedbackType = CommonUtils.getFeedbackType(ta.getInteger(8, 1));
        if (ta.hasValue(0)) {
            this.mMaxHeight = ta.getDimensionPixelOffset(0, -1);
        }
        ta.recycle();
        setVuiLayoutLoadable(true);
        addOnScrollListener(this.mOnScrollListener);
        getViewTreeObserver().addOnGlobalLayoutListener(this.mOnGlobalLayoutListener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this.mOnGlobalLayoutListener);
        removeOnScrollListener(this.mOnScrollListener);
    }

    public void updateVuiScene(int time) {
        if (!TextUtils.isEmpty(this.mSceneId)) {
            removeCallbacks(this.mRun);
            postDelayed(this.mRun, time);
            return;
        }
        Logger.i("VuiRecycleView", "updateVuiScene sceneid is empty");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        boolean needLimit = false;
        if (this.mMaxHeight >= 0) {
            needLimit = true;
        }
        if (needLimit) {
            int limitHeight = getMeasuredHeight();
            int limitWith = getMeasuredWidth();
            if (getMeasuredHeight() > this.mMaxHeight) {
                limitHeight = this.mMaxHeight;
            }
            setMeasuredDimension(limitWith, limitHeight);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public String getVuiFatherLabel() {
        return this.vuiFatherLabel;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiFatherLabel(String vuiFatherLabel) {
        this.vuiFatherLabel = vuiFatherLabel;
    }

    public boolean isVuiDynamic() {
        return this.vuiDynamic;
    }

    public void setVuiDynamic(boolean vuiDynamic) {
        this.vuiDynamic = vuiDynamic;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public VuiMode getVuiMode() {
        return VuiMode.NORMAL;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiMode(VuiMode vuiMode) {
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public boolean isVuiLayoutLoadable() {
        return true;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiLayoutLoadable(boolean b) {
    }

    public boolean isVuiEnabled() {
        return this.vuiEnabled;
    }

    public void setVuiEnabled(boolean vuiEnabled) {
        this.vuiEnabled = vuiEnabled;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public VuiPriority getVuiPriority() {
        return this.vuiPriority;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiPriority(VuiPriority vuiPriority) {
        this.vuiPriority = vuiPriority;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public String getVuiAction() {
        return this.vuiAction;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiAction(String vuiAction) {
        this.vuiAction = vuiAction;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public VuiElementType getVuiElementType() {
        return this.vuiElementType;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiElementType(VuiElementType vuiElementType) {
        this.vuiElementType = vuiElementType;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public String getVuiFatherElementId() {
        return this.vuiFatherElementId;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiFatherElementId(String vuiFatherElementId) {
        this.vuiFatherElementId = vuiFatherElementId;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public String getVuiLabel() {
        return this.vuiLabel;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiLabel(String vuiLabel) {
        this.vuiLabel = vuiLabel;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public String getVuiElementId() {
        return this.vuiElementId;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiElementId(String vuiElementId) {
        this.vuiElementId = vuiElementId;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiPosition(int vuiPosition) {
        this.vuiPosition = vuiPosition;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public int getVuiPosition() {
        return this.vuiPosition;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public VuiFeedbackType getVuiFeedbackType() {
        return this.vuiFeedbackType;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiFeedbackType(VuiFeedbackType vuiFeedbackType) {
        this.vuiFeedbackType = vuiFeedbackType;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public boolean isPerformVuiAction() {
        return this.performVuiAction;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setPerformVuiAction(boolean performVuiAction) {
        this.performVuiAction = performVuiAction;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiProps(JSONObject vuiProps) {
        this.vuiProps = vuiProps;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public JSONObject getVuiProps() {
        return this.vuiProps;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public void setVuiBizId(String s) {
    }

    @Override // com.xiaopeng.vui.commons.IVuiElement
    public String getVuiBizId() {
        return null;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public VuiElement onBuildVuiElement(String s, IVuiElementBuilder iVuiElementBuilder) {
        boolean canScrollUp = canScrollVertically(-1);
        boolean canScrollDown = canScrollVertically(1);
        if (canScrollUp || canScrollDown) {
            setVuiAction(VuiAction.SCROLLBYY.getName());
        }
        if (getVuiAction() == null) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            if (getVuiAction().equals(VuiAction.SCROLLBYY.getName())) {
                jsonObject.put(VuiConstants.PROPS_SCROLLUP, canScrollUp);
                jsonObject.put(VuiConstants.PROPS_SCROLLDOWN, canScrollDown);
            }
            jsonObject.put("firstPriority", true);
            setVuiProps(jsonObject);
        } catch (JSONException e) {
        }
        return null;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public boolean onVuiElementEvent(View view, VuiEvent vuiEvent) {
        return false;
    }

    public void setSceneId(String sceneId) {
        this.mSceneId = sceneId;
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void setAdapter(RecyclerView.Adapter adapter) {
        RecyclerView.Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(this.mAdapterDataObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.mAdapterDataObserver);
        }
        this.mIsDataChange = true;
    }
}
