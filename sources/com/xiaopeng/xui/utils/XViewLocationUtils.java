package com.xiaopeng.xui.utils;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.annotation.NonNull;
/* loaded from: classes25.dex */
public class XViewLocationUtils {
    private static final int FADING_EDGE_LENGTH = 60;

    /* loaded from: classes25.dex */
    public interface OnCorrectionLocationListener {
        void onCorrectionLocationEnd(View view);
    }

    public static void scrollByLocation(@NonNull View view, @NonNull OnCorrectionLocationListener listener) {
        ViewGroup parentView = isInScrollContainer(view);
        if (parentView != null) {
            scrollByLocation(parentView, view, listener);
        } else {
            listener.onCorrectionLocationEnd(view);
        }
    }

    public static void scrollByLocation(@NonNull final ViewGroup parentView, @NonNull final View view, @NonNull final OnCorrectionLocationListener listener) {
        log("scrollByLocation start ");
        view.post(new Runnable() { // from class: com.xiaopeng.xui.utils.-$$Lambda$XViewLocationUtils$ASPj6RLccAz9kzfwNAyScYQXOVU
            @Override // java.lang.Runnable
            public final void run() {
                XViewLocationUtils.lambda$scrollByLocation$0(view, parentView, listener);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$scrollByLocation$0(View view, ViewGroup parentView, OnCorrectionLocationListener listener) {
        Rect rect = new Rect();
        boolean localVisible = view.getLocalVisibleRect(rect);
        int h = view.getHeight();
        String type = "";
        if (rect.top < 0) {
            parentView.scrollBy(0, rect.top - 60);
            type = "top all ";
        } else if (rect.top > 0) {
            if (rect.height() < h) {
                parentView.scrollBy(0, (-rect.top) - 60);
                type = "top a part ";
            } else {
                parentView.scrollBy(0, (rect.bottom - parentView.getHeight()) + 60);
                type = "bottom all ";
            }
        } else if (rect.height() < h) {
            parentView.scrollBy(0, (h - rect.bottom) + 60);
            type = "bottom a part ";
        }
        log("scrollByLocation end localVisible : " + localVisible + ", top " + rect.top + ", bottom " + rect.bottom + ", rect h:" + rect.height() + ",h:" + h + ", type: " + type);
        listener.onCorrectionLocationEnd(view);
    }

    private static ViewGroup isInScrollContainer(View view) {
        for (ViewParent p = view.getParent(); p instanceof ViewGroup; p = p.getParent()) {
            if (((ViewGroup) p).shouldDelayChildPressedState()) {
                return (ViewGroup) p;
            }
        }
        return null;
    }

    private static void log(String msg) {
        XLogUtils.d("xui-ViewLocation", msg);
    }
}
