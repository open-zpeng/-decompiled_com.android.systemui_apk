package com.xiaopeng.systemui.infoflow.message.manager;

import android.view.KeyEvent;
import com.xiaopeng.systemui.infoflow.message.view.IXFocusView;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class FocusManager {
    private static final String TAG = FocusManager.class.getSimpleName();
    private static volatile FocusManager mInstance;
    private List<IXFocusView> mIXFocusViews = new ArrayList();

    public static FocusManager instance() {
        if (mInstance == null) {
            synchronized (FocusManager.class) {
                if (mInstance == null) {
                    mInstance = new FocusManager();
                }
            }
        }
        return mInstance;
    }

    private FocusManager() {
    }

    public synchronized void addFocusView(IXFocusView focusView) {
        if (!this.mIXFocusViews.contains(focusView)) {
            this.mIXFocusViews.add(focusView);
        }
    }

    public synchronized void removeFocusView(IXFocusView focusView) {
        if (this.mIXFocusViews.contains(focusView)) {
            this.mIXFocusViews.remove(focusView);
        }
    }

    public synchronized void dispatchFocusNavigationEvent(KeyEvent keyEvent) {
        for (int i = 0; i < this.mIXFocusViews.size(); i++) {
            IXFocusView focusView = this.mIXFocusViews.get(i);
            if (focusView.isXShown()) {
                focusView.performFocusNavigation(keyEvent);
                return;
            }
        }
    }
}
