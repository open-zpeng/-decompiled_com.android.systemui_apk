package com.xiaopeng.systemui.utils;

import android.graphics.Rect;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.annotation.Nullable;
/* loaded from: classes24.dex */
public class XTouchTargetUtils {
    private static final Rect HIT_RECT = new Rect();

    public static void extendViewTouchTarget(final View view, final int ancestorId, final int left, final int top, final int right, final int bottom) {
        view.post(new Runnable() { // from class: com.xiaopeng.systemui.utils.XTouchTargetUtils.1
            @Override // java.lang.Runnable
            public void run() {
                View ancestor = XTouchTargetUtils.findViewAncestor(view, ancestorId);
                XTouchTargetUtils.extendViewTouchTarget(view, ancestor, left, top, right, bottom);
            }
        });
    }

    public static void extendViewTouchTarget(final View view, @Nullable final View nullableAncestor, final int left, final int top, final int right, final int bottom) {
        if (view == null || nullableAncestor == null) {
            return;
        }
        nullableAncestor.post(new Runnable() { // from class: com.xiaopeng.systemui.utils.XTouchTargetUtils.2
            @Override // java.lang.Runnable
            public void run() {
                if (view.isAttachedToWindow()) {
                    view.getHitRect(XTouchTargetUtils.HIT_RECT);
                    if (XTouchTargetUtils.HIT_RECT.width() == 0 || XTouchTargetUtils.HIT_RECT.height() == 0) {
                        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.xiaopeng.systemui.utils.XTouchTargetUtils.2.1
                            @Override // android.view.View.OnLayoutChangeListener
                            public void onLayoutChange(View v, int left1, int top1, int right1, int bottom1, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                if (v.getWidth() > 0 && v.getHeight() > 0) {
                                    XTouchTargetUtils.extendViewTouchTarget(v, nullableAncestor, left, top, right, bottom);
                                    v.removeOnLayoutChangeListener(this);
                                }
                            }
                        });
                        return;
                    }
                    Rect viewHitRect = new Rect();
                    viewHitRect.set(XTouchTargetUtils.HIT_RECT);
                    ViewParent parent = view.getParent();
                    while (parent != nullableAncestor) {
                        if (parent instanceof View) {
                            View parentView = (View) parent;
                            parentView.getHitRect(XTouchTargetUtils.HIT_RECT);
                            viewHitRect.offset(XTouchTargetUtils.HIT_RECT.left, XTouchTargetUtils.HIT_RECT.top);
                            parent = parentView.getParent();
                        } else {
                            return;
                        }
                    }
                    viewHitRect.left -= left;
                    viewHitRect.top -= top;
                    viewHitRect.right += right;
                    viewHitRect.bottom += bottom;
                    final XTouchDelegate touchDelegate = new XTouchDelegate(viewHitRect, view);
                    final XTouchDelegateGroup touchDelegateGroup = XTouchTargetUtils.getOrCreateTouchDelegateGroup(nullableAncestor);
                    touchDelegateGroup.addTouchDelegate(touchDelegate);
                    nullableAncestor.setTouchDelegate(touchDelegateGroup);
                    XTouchTargetUtils.log("view : " + view.hashCode());
                    XTouchTargetUtils.log("size : " + touchDelegateGroup.getTouchDelegates().size());
                    view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.xiaopeng.systemui.utils.XTouchTargetUtils.2.2
                        @Override // android.view.View.OnAttachStateChangeListener
                        public void onViewAttachedToWindow(View view2) {
                        }

                        @Override // android.view.View.OnAttachStateChangeListener
                        public void onViewDetachedFromWindow(View view2) {
                            XTouchTargetUtils.log("onViewDetachedFromWindow " + view2.hashCode());
                            touchDelegateGroup.removeTouchDelegate(touchDelegate);
                            view2.removeOnAttachStateChangeListener(this);
                        }
                    });
                    return;
                }
                XTouchTargetUtils.log("not isAttachedToWindow " + hashCode());
            }
        });
    }

    public static void extendTouchAreaAsParentSameSize(final View view, final ViewGroup parent) {
        if (view == null || parent == null) {
            return;
        }
        view.post(new Runnable() { // from class: com.xiaopeng.systemui.utils.XTouchTargetUtils.3
            @Override // java.lang.Runnable
            public void run() {
                if (!view.isAttachedToWindow()) {
                    XTouchTargetUtils.log("not isAttachedToWindow " + hashCode());
                } else if (parent.getWidth() == 0 || parent.getHeight() == 0) {
                    view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.xiaopeng.systemui.utils.XTouchTargetUtils.3.1
                        @Override // android.view.View.OnLayoutChangeListener
                        public void onLayoutChange(View v, int left1, int top1, int right1, int bottom1, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            if (v.getWidth() > 0 && v.getHeight() > 0) {
                                XTouchTargetUtils.extendTouchAreaAsParentSameSize(v, parent);
                                v.removeOnLayoutChangeListener(this);
                            }
                        }
                    });
                } else {
                    Rect rect = new Rect(0, 0, parent.getWidth(), parent.getHeight());
                    final XTouchDelegate touchDelegate = new XTouchDelegate(rect, view);
                    final XTouchDelegateGroup touchDelegateGroup = XTouchTargetUtils.getOrCreateTouchDelegateGroup(parent);
                    touchDelegateGroup.addTouchDelegate(touchDelegate);
                    parent.setTouchDelegate(touchDelegateGroup);
                    XTouchTargetUtils.log("view : " + view.hashCode() + "size : " + touchDelegateGroup.getTouchDelegates().size());
                    view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.xiaopeng.systemui.utils.XTouchTargetUtils.3.2
                        @Override // android.view.View.OnAttachStateChangeListener
                        public void onViewAttachedToWindow(View view2) {
                        }

                        @Override // android.view.View.OnAttachStateChangeListener
                        public void onViewDetachedFromWindow(View view2) {
                            XTouchTargetUtils.log("onViewDetachedFromWindow " + view2.hashCode());
                            touchDelegateGroup.removeTouchDelegate(touchDelegate);
                            view2.removeOnAttachStateChangeListener(this);
                        }
                    });
                }
            }
        });
    }

    public static XTouchDelegateGroup getOrCreateTouchDelegateGroup(View ancestor) {
        TouchDelegate existingTouchDelegate = ancestor.getTouchDelegate();
        if (existingTouchDelegate != null) {
            if (existingTouchDelegate instanceof XTouchDelegateGroup) {
                return (XTouchDelegateGroup) existingTouchDelegate;
            }
            XTouchDelegateGroup touchDelegateGroup = new XTouchDelegateGroup(ancestor);
            if (existingTouchDelegate instanceof XTouchDelegate) {
                touchDelegateGroup.addTouchDelegate((XTouchDelegate) existingTouchDelegate);
                return touchDelegateGroup;
            }
            return touchDelegateGroup;
        }
        return new XTouchDelegateGroup(ancestor);
    }

    @Nullable
    public static View findViewAncestor(View view, int ancestorId) {
        View parent = view;
        while (parent != null && parent.getId() != ancestorId) {
            if (!(parent.getParent() instanceof View)) {
                return null;
            }
            parent = (View) parent.getParent();
        }
        return parent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void log(String msg) {
        Log.d("byron-touch", msg);
    }
}
