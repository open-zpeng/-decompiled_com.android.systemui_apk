package com.xiaopeng.speech.vui.event;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.speech.vui.constants.Foo;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.VuiAction;
import com.xiaopeng.vui.commons.model.VuiElement;
/* loaded from: classes.dex */
public class ScrollByYEvent extends BaseEvent {
    private EndSmoothScroller mScroller = null;

    @Override // com.xiaopeng.speech.vui.event.IVuiEvent
    public <T extends View> T run(T view, VuiElement vuiElement) {
        if (view != null && vuiElement != null && vuiElement.getResultActions() != null && !vuiElement.getResultActions().isEmpty() && VuiAction.SCROLLBYY.getName().equals(vuiElement.getResultActions().get(0))) {
            String direction = (String) VuiUtils.getValueByName(vuiElement, VuiConstants.EVENT_VALUE_DIRECTION);
            Double offset = (Double) VuiUtils.getValueByName(vuiElement, "offset");
            if (direction == null || offset == null) {
                return view;
            }
            if (view instanceof IVuiElement) {
                ((IVuiElement) view).setPerformVuiAction(true);
            }
            int value = VuiConstants.EVENT_VALUE_DIRECTION_UP.equals(direction) ? -offset.intValue() : offset.intValue();
            LogUtils.i("ScrollByYEvent", "ScrollByYEvent run value:" + value + ",view:" + view);
            if (view instanceof RecyclerView) {
                int value2 = value != 100 ? (int) (value * 0.01d * view.getHeight()) : 100;
                if (value2 == 0) {
                    ((RecyclerView) view).smoothScrollToPosition(0);
                } else if (value2 == 100) {
                    RecyclerView recyclerView = (RecyclerView) view;
                    if (recyclerView.getAdapter() == null) {
                        return view;
                    }
                    smoothMoveToPosition(recyclerView, recyclerView.getAdapter().getItemCount() - 1);
                } else {
                    ((RecyclerView) view).smoothScrollBy(0, value2);
                }
            } else if (view instanceof ScrollView) {
                int value3 = value != 100 ? (int) (value * 0.01d * view.getHeight()) : 100;
                if (value3 == 0) {
                    ((ScrollView) view).fullScroll(33);
                } else if (value3 == 100) {
                    ((ScrollView) view).fullScroll(130);
                } else {
                    ((ScrollView) view).smoothScrollBy(0, value3);
                }
            } else if (view instanceof ListView) {
                int value4 = value != 100 ? (int) (value * 0.01d * view.getHeight()) : 100;
                ListView listView = (ListView) view;
                if (value4 == 0) {
                    listView.setSelection(listView.getTop());
                } else if (value4 == 100) {
                    listView.setSelection(listView.getBottom());
                } else {
                    listView.smoothScrollBy(0, value4);
                }
            } else {
                Rect rect = new Rect();
                view.getGlobalVisibleRect(rect);
                if (value == 0) {
                    view.scrollTo(0, 0);
                } else if (value == 100) {
                    view.scrollTo(0, view.getHeight() - rect.height());
                } else {
                    int value5 = (int) (value * 0.01d * rect.height());
                    if (value5 > 0) {
                        if (view.getScrollY() + value5 + rect.height() >= view.getHeight()) {
                            view.scrollTo(0, view.getHeight() - rect.height());
                            return view;
                        }
                    } else if ((view.getScrollY() + value5) - rect.height() < 0) {
                        view.scrollTo(0, 0);
                        return view;
                    }
                    view.scrollBy(0, value5);
                }
            }
            if (view instanceof IVuiElement) {
                ((IVuiElement) view).setPerformVuiAction(false);
            }
        }
        return view;
    }

    public void smoothMoveToPosition(RecyclerView recyclerView, int position) {
        if (recyclerView.getLayoutManager() != null) {
            Log.d("ScrollByYEvent", "smoothMoveToPosition: ===== " + position);
            if (this.mScroller == null) {
                this.mScroller = new EndSmoothScroller(Foo.getContext());
            }
            this.mScroller.setTargetPosition(position);
            recyclerView.getLayoutManager().startSmoothScroll(this.mScroller);
        }
    }
}
