package com.xiaopeng.speech.vui.event;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewRootImpl;
import android.widget.ListView;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.model.VuiElement;
/* loaded from: classes.dex */
public class ListItemClickEvent extends BaseEvent {
    private VuiElement params;

    @Override // com.xiaopeng.speech.vui.event.IVuiEvent
    public <T extends View> T run(T view, VuiElement vuiElement) {
        this.params = this.params;
        boolean issucc = performClick(view);
        if (!issucc) {
            View listView = getListView(view);
            boolean z = listView instanceof ListView;
        }
        return view;
    }

    public boolean performClick(View view) {
        if (view == null) {
            return false;
        }
        if (view instanceof IVuiElement) {
            ((IVuiElement) view).setPerformVuiAction(true);
        }
        boolean issucc = view.performClick();
        LogUtils.i("ClickEvent run :" + issucc);
        if (view instanceof IVuiElement) {
            ((IVuiElement) view).setPerformVuiAction(false);
        }
        if (issucc) {
            return true;
        }
        if (view instanceof IVuiElement) {
            ((IVuiElement) view).setPerformVuiAction(false);
        }
        if (view.getParent() instanceof ViewRootImpl) {
            return false;
        }
        return performClick((View) view.getParent());
    }

    private View getListView(View view) {
        if ((view instanceof ListView) || (view instanceof RecyclerView)) {
            return view;
        }
        if (view.getParent() instanceof ViewRootImpl) {
            return view;
        }
        return getListView((View) view.getParent());
    }

    public String getPositionByViewId(int vId, VuiElement vuiElement) {
        if (vuiElement != null && !TextUtils.isEmpty(vuiElement.getId())) {
            String[] ids = vuiElement.getId().split("_");
            if (ids.length != 2 || ids[1] == null) {
                return "0";
            }
            return ids[1];
        }
        return "0";
    }
}
