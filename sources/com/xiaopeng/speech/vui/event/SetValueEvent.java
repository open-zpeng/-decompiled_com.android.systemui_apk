package com.xiaopeng.speech.vui.event;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.speech.vui.vuiengine.R;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.model.VuiElement;
/* loaded from: classes.dex */
public class SetValueEvent extends BaseEvent {
    private int value = -1;
    private String elementId = null;
    private VuiElement mVuiElement = null;
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() { // from class: com.xiaopeng.speech.vui.event.SetValueEvent.1
        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            View itemView;
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == 0) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int first = layoutManager.findFirstVisibleItemPosition();
                    int last = layoutManager.findLastVisibleItemPosition();
                    if (SetValueEvent.this.value >= first && SetValueEvent.this.value <= last && (itemView = layoutManager.findViewByPosition(SetValueEvent.this.value)) != null) {
                        SetValueEvent setValueEvent = SetValueEvent.this;
                        View actionView = setValueEvent.findActionView(itemView, setValueEvent.elementId);
                        if (actionView != null) {
                            if (!TextUtils.isEmpty(SetValueEvent.this.mSceneId)) {
                                VuiElement unused = SetValueEvent.this.mVuiElement;
                            }
                            SetValueEvent.this.mSceneId = null;
                            SetValueEvent.this.mVuiElement = null;
                            SetValueEvent.this.performClick(actionView);
                        }
                    }
                }
                recyclerView.removeOnScrollListener(SetValueEvent.this.mScrollListener);
            }
        }
    };
    private String mSceneId = null;

    @Override // com.xiaopeng.speech.vui.event.IVuiEvent
    public <T extends View> T run(T view, VuiElement vuiElement) {
        if (view == null) {
            return null;
        }
        if (vuiElement != null) {
            try {
                if (vuiElement.getResultActions() != null && !vuiElement.getResultActions().isEmpty() && VuiElementType.VIRTUALLIST.getType().equals(vuiElement.getType())) {
                    this.elementId = vuiElement.getId();
                    Double dValue = (Double) VuiUtils.getValueByName(vuiElement, VuiConstants.ELEMENT_VALUE);
                    if (view instanceof RecyclerView) {
                        if (((IVuiElement) view).getVuiProps() != null) {
                            boolean isReverse = false;
                            if (((IVuiElement) view).getVuiProps().has("isReverse")) {
                                isReverse = ((IVuiElement) view).getVuiProps().getBoolean("isReverse");
                            }
                            if (!isReverse) {
                                if (((IVuiElement) view).getVuiProps().has("hasHeader")) {
                                    this.value = dValue.intValue();
                                } else {
                                    this.value = dValue.intValue() - 1;
                                }
                                if (((IVuiElement) view).getVuiProps().has(VuiConstants.PROPS_MINVALUE)) {
                                    int min = ((IVuiElement) view).getVuiProps().getInt(VuiConstants.PROPS_MINVALUE);
                                    this.value = (this.value - min) + 1;
                                }
                                LogUtils.d("SetValueEvent", "value:" + this.value);
                            } else {
                                this.value = dValue.intValue();
                                if (((IVuiElement) view).getVuiProps().has(VuiConstants.PROPS_MAXVALUE)) {
                                    int max = ((IVuiElement) view).getVuiProps().getInt(VuiConstants.PROPS_MAXVALUE);
                                    this.value = max - this.value;
                                }
                                if (((IVuiElement) view).getVuiProps().has("hasHeader")) {
                                    this.value = dValue.intValue() + 1;
                                }
                                LogUtils.d("SetValueEvent", "reverse value:" + this.value);
                            }
                        }
                        RecyclerView recyclerView = (RecyclerView) view;
                        recyclerView.addOnScrollListener(this.mScrollListener);
                        recyclerView.smoothScrollToPosition(this.value);
                    }
                    this.mVuiElement = vuiElement;
                }
            } catch (Exception e) {
                LogUtils.e("SetValueEvent", e.fillInStackTrace());
            }
        }
        return view;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View findActionView(View view, String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        Object tagId = view.getTag(R.id.executeVirtualId);
        if (tagId != null && id.equals(tagId.toString())) {
            return view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (findActionView(child, id) != null) {
                    return child;
                }
            }
            return null;
        }
        return null;
    }

    public boolean performClick(View view) {
        if (view == null) {
            return false;
        }
        if (view instanceof IVuiElement) {
            ((IVuiElement) view).setPerformVuiAction(true);
        }
        boolean issucc = view.performClick();
        if (view instanceof IVuiElement) {
            ((IVuiElement) view).setPerformVuiAction(false);
        }
        LogUtils.i("ClickEvent run :" + issucc);
        if (issucc) {
            return true;
        }
        if (view.getParent() instanceof ViewRootImpl) {
            return false;
        }
        return performClick((View) view.getParent());
    }

    public void setSceneId(String sceneId) {
        this.mSceneId = sceneId;
    }
}
