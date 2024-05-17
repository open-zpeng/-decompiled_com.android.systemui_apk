package com.badlogic.gdx.backends.android;

import android.view.MotionEvent;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidInput;
/* loaded from: classes21.dex */
public class AndroidMouseHandler {
    private int deltaX = 0;
    private int deltaY = 0;

    public boolean onGenericMotion(MotionEvent event, AndroidInput input) {
        if ((event.getSource() & 2) == 0) {
            return false;
        }
        int action = event.getAction() & 255;
        long timeStamp = System.nanoTime();
        synchronized (input) {
            if (action == 7) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (x != this.deltaX || y != this.deltaY) {
                    postTouchEvent(input, 4, x, y, 0, timeStamp);
                    this.deltaX = x;
                    this.deltaY = y;
                }
            } else if (action == 8) {
                int scrollAmount = (int) (-Math.signum(event.getAxisValue(9)));
                postTouchEvent(input, 3, 0, 0, scrollAmount, timeStamp);
            }
        }
        Gdx.app.getGraphics().requestRendering();
        return true;
    }

    private void logAction(int action) {
        String actionStr;
        if (action == 9) {
            actionStr = "HOVER_ENTER";
        } else if (action == 7) {
            actionStr = "HOVER_MOVE";
        } else if (action == 10) {
            actionStr = "HOVER_EXIT";
        } else if (action == 8) {
            actionStr = "SCROLL";
        } else {
            actionStr = "UNKNOWN (" + action + NavigationBarInflaterView.KEY_CODE_END;
        }
        Gdx.app.log("AndroidMouseHandler", "action " + actionStr);
    }

    private void postTouchEvent(AndroidInput input, int type, int x, int y, int scrollAmount, long timeStamp) {
        AndroidInput.TouchEvent event = input.usedTouchEvents.obtain();
        event.timeStamp = timeStamp;
        event.x = x;
        event.y = y;
        event.type = type;
        event.scrollAmount = scrollAmount;
        input.touchEvents.add(event);
    }
}
