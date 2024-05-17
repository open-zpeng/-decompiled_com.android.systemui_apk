package com.badlogic.gdx.backends.android;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import com.badlogic.gdx.Application;
import java.util.ArrayList;
/* loaded from: classes21.dex */
public class AndroidInputThreePlus extends AndroidInput implements View.OnGenericMotionListener {
    ArrayList<View.OnGenericMotionListener> genericMotionListeners;
    private final AndroidMouseHandler mouseHandler;

    public AndroidInputThreePlus(Application activity, Context context, Object view, AndroidApplicationConfiguration config) {
        super(activity, context, view, config);
        this.genericMotionListeners = new ArrayList<>();
        if (view instanceof View) {
            View v = (View) view;
            v.setOnGenericMotionListener(this);
        }
        this.mouseHandler = new AndroidMouseHandler();
    }

    @Override // android.view.View.OnGenericMotionListener
    public boolean onGenericMotion(View view, MotionEvent event) {
        if (this.mouseHandler.onGenericMotion(event, this)) {
            return true;
        }
        int n = this.genericMotionListeners.size();
        for (int i = 0; i < n; i++) {
            if (this.genericMotionListeners.get(i).onGenericMotion(view, event)) {
                return true;
            }
        }
        return false;
    }

    public void addGenericMotionListener(View.OnGenericMotionListener listener) {
        this.genericMotionListeners.add(listener);
    }
}
