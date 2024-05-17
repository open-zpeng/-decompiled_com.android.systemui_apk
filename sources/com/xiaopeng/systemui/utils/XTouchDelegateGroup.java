package com.xiaopeng.systemui.utils;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes24.dex */
public class XTouchDelegateGroup extends TouchDelegate {
    private static final Rect IGNORED = new Rect();
    @Nullable
    private TouchDelegate currentTouchDelegate;
    private List<XTouchDelegate> touchDelegates;

    public XTouchDelegateGroup(View ancestor) {
        super(IGNORED, ancestor);
        this.touchDelegates = new ArrayList();
    }

    public void addTouchDelegate(XTouchDelegate touchDelegate) {
        if (touchDelegate == null) {
            throw new NullPointerException("Cannot add null touchDelegate");
        }
        Iterator<XTouchDelegate> it = this.touchDelegates.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            XTouchDelegate exist = it.next();
            if (exist.getDelegateViewHold().equals(touchDelegate.getDelegateViewHold())) {
                this.touchDelegates.remove(exist);
                break;
            }
        }
        this.touchDelegates.add(touchDelegate);
    }

    public void removeTouchDelegate(TouchDelegate touchDelegate) {
        this.touchDelegates.remove(touchDelegate);
        if (touchDelegate == this.currentTouchDelegate) {
            this.currentTouchDelegate = null;
        }
    }

    public void clearTouchDelegates() {
        this.touchDelegates.clear();
    }

    @VisibleForTesting
    public List<XTouchDelegate> getTouchDelegates() {
        return Collections.unmodifiableList(new ArrayList(this.touchDelegates));
    }

    @Override // android.view.TouchDelegate
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        boolean handled = false;
        if (action == 0) {
            if (event.getPointerCount() > 1) {
                return false;
            }
            for (int i = this.touchDelegates.size() - 1; i >= 0; i--) {
                XTouchDelegate touchDelegate = this.touchDelegates.get(i);
                View view = touchDelegate.getDelegateViewHold();
                if (view == null || view.getVisibility() == 0) {
                    float savedX = event.getX();
                    float savedY = event.getY();
                    boolean handled2 = touchDelegate.onTouchEvent(event);
                    event.setLocation(savedX, savedY);
                    if (handled2) {
                        this.currentTouchDelegate = touchDelegate;
                        return true;
                    }
                }
            }
            return false;
        }
        TouchDelegate touchDelegate2 = this.currentTouchDelegate;
        if (touchDelegate2 != null && touchDelegate2.onTouchEvent(event)) {
            handled = true;
        }
        if (action == 1 || action == 32) {
            this.currentTouchDelegate = null;
        }
        return handled;
    }
}
