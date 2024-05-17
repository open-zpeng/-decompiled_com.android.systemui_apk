package com.xiaopeng.xui.view.touch;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.annotation.Nullable;
import com.xiaopeng.xui.utils.XLogUtils;
/* loaded from: classes25.dex */
public class XTouchTargetUtils {
    private static final Rect HIT_RECT = new Rect();
    private static final String TAG = "xpui-touch";

    public static void extendViewTouchTarget(final View view, final int ancestorId, final int left, final int top, final int right, final int bottom) {
        view.post(new Runnable() { // from class: com.xiaopeng.xui.view.touch.XTouchTargetUtils.1
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
        nullableAncestor.post(new Runnable() { // from class: com.xiaopeng.xui.view.touch.XTouchTargetUtils.2
            @Override // java.lang.Runnable
            public void run() {
                if (view.isAttachedToWindow()) {
                    view.getHitRect(XTouchTargetUtils.HIT_RECT);
                    if (XTouchTargetUtils.HIT_RECT.width() == 0 || XTouchTargetUtils.HIT_RECT.height() == 0) {
                        XTouchTargetUtils.log(" width or height == 0 " + hashCode());
                        LayoutAttachStateChangeListener layoutAttachStateChangeListener = new LayoutAttachStateChangeListener(nullableAncestor, left, top, right, bottom);
                        view.addOnLayoutChangeListener(layoutAttachStateChangeListener);
                        view.addOnAttachStateChangeListener(layoutAttachStateChangeListener);
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
                    XTouchDelegate touchDelegate = new XTouchDelegate(viewHitRect, view);
                    XTouchDelegateGroup touchDelegateGroup = XTouchTargetUtils.getOrCreateTouchDelegateGroup(nullableAncestor);
                    touchDelegateGroup.addTouchDelegate(touchDelegate);
                    nullableAncestor.setTouchDelegate(touchDelegateGroup);
                    view.addOnAttachStateChangeListener(new MyStateChangeListener(touchDelegate, touchDelegateGroup));
                    return;
                }
                XTouchTargetUtils.log("not isAttachedToWindow " + hashCode());
            }
        });
    }

    /* loaded from: classes25.dex */
    private static class LayoutAttachStateChangeListener implements View.OnLayoutChangeListener, View.OnAttachStateChangeListener {
        private static int msCount;
        private View ancestor;
        int bottom;
        int left;
        int right;
        int top;

        LayoutAttachStateChangeListener(View ancestor, int left, int top, int right, int bottom) {
            this.ancestor = ancestor;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            msCount++;
        }

        protected void finalize() throws Throwable {
            super.finalize();
            msCount--;
            if (msCount == 0) {
                XLogUtils.d(XTouchTargetUtils.TAG, " LayoutAttachStateChangeListener  finalize " + msCount);
            }
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View v, int left1, int top1, int right1, int bottom1, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (v.getWidth() > 0 && v.getHeight() > 0) {
                XTouchTargetUtils.extendViewTouchTarget(v, this.ancestor, this.left, this.top, this.right, this.bottom);
                this.ancestor = null;
                v.removeOnLayoutChangeListener(this);
                v.removeOnAttachStateChangeListener(this);
                XTouchTargetUtils.log(" LayoutAttachStateChangeListener  onLayoutChange ");
            }
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            v.removeOnLayoutChangeListener(this);
            v.removeOnAttachStateChangeListener(this);
            XTouchTargetUtils.log(" LayoutAttachStateChangeListener  onViewDetachedFromWindow ");
        }
    }

    public static void extendTouchAreaAsParentSameSize(final View view, final ViewGroup parent) {
        if (view == null || parent == null) {
            return;
        }
        view.post(new Runnable() { // from class: com.xiaopeng.xui.view.touch.XTouchTargetUtils.3
            @Override // java.lang.Runnable
            public void run() {
                if (!view.isAttachedToWindow()) {
                    XTouchTargetUtils.log(" as not isAttachedToWindow " + hashCode());
                } else if (parent.getWidth() == 0 || parent.getHeight() == 0) {
                    XTouchTargetUtils.log(" as width or height == 0 " + hashCode());
                    LayoutAttachStateChangeListener2 layoutAttachStateChangeListener = new LayoutAttachStateChangeListener2(parent);
                    view.addOnLayoutChangeListener(layoutAttachStateChangeListener);
                    view.addOnAttachStateChangeListener(layoutAttachStateChangeListener);
                } else {
                    Rect rect = new Rect(0, 0, parent.getWidth(), parent.getHeight());
                    XTouchDelegate touchDelegate = new XTouchDelegate(rect, view);
                    XTouchDelegateGroup touchDelegateGroup = XTouchTargetUtils.getOrCreateTouchDelegateGroup(parent);
                    touchDelegateGroup.addTouchDelegate(touchDelegate);
                    parent.setTouchDelegate(touchDelegateGroup);
                    view.addOnAttachStateChangeListener(new MyStateChangeListener(touchDelegate, touchDelegateGroup));
                }
            }
        });
    }

    /* loaded from: classes25.dex */
    private static class LayoutAttachStateChangeListener2 implements View.OnLayoutChangeListener, View.OnAttachStateChangeListener {
        private static int msCount;
        private ViewGroup ancestor;

        LayoutAttachStateChangeListener2(ViewGroup ancestor) {
            this.ancestor = ancestor;
            msCount++;
        }

        protected void finalize() throws Throwable {
            super.finalize();
            msCount--;
            if (msCount == 0) {
                XLogUtils.d(XTouchTargetUtils.TAG, " LayoutAttachStateChangeListener2  finalize " + msCount);
            }
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (v.getWidth() > 0 && v.getHeight() > 0) {
                XTouchTargetUtils.extendTouchAreaAsParentSameSize(v, this.ancestor);
                this.ancestor = null;
                v.removeOnLayoutChangeListener(this);
                v.removeOnAttachStateChangeListener(this);
                XTouchTargetUtils.log(" LayoutAttachStateChangeListener2  onLayoutChange ");
            }
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            v.removeOnLayoutChangeListener(this);
            v.removeOnAttachStateChangeListener(this);
            XTouchTargetUtils.log(" LayoutAttachStateChangeListener2  onViewDetachedFromWindow ");
        }
    }

    /* loaded from: classes25.dex */
    private static class MyStateChangeListener implements View.OnAttachStateChangeListener {
        private static int msCount;
        private XTouchDelegate touchDelegate;
        private XTouchDelegateGroup touchDelegateGroup;

        MyStateChangeListener(XTouchDelegate touchDelegate, XTouchDelegateGroup touchDelegateGroup) {
            this.touchDelegate = touchDelegate;
            this.touchDelegateGroup = touchDelegateGroup;
            msCount++;
        }

        protected void finalize() throws Throwable {
            super.finalize();
            msCount--;
            if (msCount == 0) {
                XLogUtils.d(XTouchTargetUtils.TAG, " MyStateChangeListener finalize " + msCount);
            }
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            v.removeOnAttachStateChangeListener(this);
            this.touchDelegateGroup.removeTouchDelegate(this.touchDelegate);
            XTouchTargetUtils.log("  MyStateChangeListener onViewDetachedFromWindow " + v.hashCode());
            this.touchDelegateGroup = null;
            this.touchDelegate = null;
        }
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
    }
}
