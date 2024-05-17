package com.badlogic.gdx.backends.android;

import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidInput;
/* loaded from: classes21.dex */
public class AndroidMultiTouchHandler implements AndroidTouchHandler {
    /* JADX WARN: Not initialized variable reg: 18, insn: 0x01eb: MOVE  (r5 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r18 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('button' int)]), block:B:88:0x01eb */
    /* JADX WARN: Not initialized variable reg: 19, insn: 0x01ed: MOVE  (r3 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r19 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('y' int)]), block:B:88:0x01eb */
    /* JADX WARN: Not initialized variable reg: 20, insn: 0x01ef: MOVE  (r4 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r20 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('realPointerIndex' int)]), block:B:88:0x01eb */
    /* JADX WARN: Not initialized variable reg: 21, insn: 0x01f1: MOVE  (r2 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r21 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('x' int)]), block:B:88:0x01eb */
    @Override // com.badlogic.gdx.backends.android.AndroidTouchHandler
    public void onTouch(MotionEvent event, AndroidInput input) {
        int x;
        int y;
        int button;
        int button2;
        int y2;
        int realPointerIndex;
        int x2;
        int realPointerIndex2;
        int button3;
        int y3;
        int realPointerIndex3;
        int x3;
        int realPointerIndex4;
        int y4;
        int realPointerIndex5;
        int button4;
        int x4;
        int pointerIndex;
        int i;
        int pointerCount;
        int action = event.getAction() & 255;
        int pointerIndex2 = (event.getAction() & 65280) >> 8;
        int pointerId = event.getPointerId(pointerIndex2);
        long timeStamp = System.nanoTime();
        synchronized (input) {
            int i2 = 20;
            int realPointerIndex6 = -1;
            try {
                try {
                    try {
                        try {
                            try {
                                switch (action) {
                                    case 0:
                                    case 5:
                                        int realPointerIndex7 = input.getFreePointerIndex();
                                        if (realPointerIndex7 >= 20) {
                                            Gdx.app.getGraphics().requestRendering();
                                            return;
                                        }
                                        input.realId[realPointerIndex7] = pointerId;
                                        x = (int) event.getX(pointerIndex2);
                                        y = (int) event.getY(pointerIndex2);
                                        if (Build.VERSION.SDK_INT >= 14) {
                                            try {
                                                try {
                                                    button = toGdxButton(event.getButtonState());
                                                } catch (Throwable th) {
                                                    th = th;
                                                    throw th;
                                                }
                                            } catch (Throwable th2) {
                                                th = th2;
                                            }
                                        } else {
                                            button = 0;
                                        }
                                        if (button != -1) {
                                            button2 = button;
                                            y2 = y;
                                            y = button2;
                                            realPointerIndex = realPointerIndex7;
                                            x2 = x;
                                            long j = timeStamp;
                                            postTouchEvent(input, 0, x, y, realPointerIndex7, y, j);
                                            realPointerIndex2 = j;
                                        } else {
                                            button2 = button;
                                            y2 = y;
                                            realPointerIndex = realPointerIndex7;
                                            x2 = x;
                                            realPointerIndex2 = realPointerIndex7;
                                        }
                                        try {
                                            input.touchX[realPointerIndex] = x2;
                                            input.touchY[realPointerIndex] = y2;
                                            input.deltaX[realPointerIndex] = 0;
                                            input.deltaY[realPointerIndex] = 0;
                                            int button5 = button2;
                                            try {
                                                input.touched[realPointerIndex] = button5 != -1;
                                                input.button[realPointerIndex] = button5;
                                                realPointerIndex6 = realPointerIndex2;
                                                Gdx.app.getGraphics().requestRendering();
                                                return;
                                            } catch (Throwable th3) {
                                                th = th3;
                                                throw th;
                                            }
                                        } catch (Throwable th4) {
                                            th = th4;
                                        }
                                    case 1:
                                    case 3:
                                    case 4:
                                    case 6:
                                        int realPointerIndex8 = input.lookUpPointerIndex(pointerId);
                                        if (realPointerIndex8 == -1 || realPointerIndex8 >= 20) {
                                            Gdx.app.getGraphics().requestRendering();
                                            return;
                                        }
                                        input.realId[realPointerIndex8] = -1;
                                        x = (int) event.getX(pointerIndex2);
                                        y = (int) event.getY(pointerIndex2);
                                        int button6 = input.button[realPointerIndex8];
                                        if (button6 != -1) {
                                            button3 = button6;
                                            y3 = y;
                                            y = button3;
                                            realPointerIndex3 = realPointerIndex8;
                                            x3 = x;
                                            long j2 = timeStamp;
                                            postTouchEvent(input, 1, x, y, realPointerIndex8, y, j2);
                                            realPointerIndex4 = j2;
                                        } else {
                                            button3 = button6;
                                            y3 = y;
                                            realPointerIndex3 = realPointerIndex8;
                                            x3 = x;
                                            realPointerIndex4 = realPointerIndex8;
                                        }
                                        input.touchX[realPointerIndex3] = x3;
                                        input.touchY[realPointerIndex3] = y3;
                                        input.deltaX[realPointerIndex3] = 0;
                                        input.deltaY[realPointerIndex3] = 0;
                                        input.touched[realPointerIndex3] = false;
                                        input.button[realPointerIndex3] = 0;
                                        realPointerIndex6 = realPointerIndex4;
                                        Gdx.app.getGraphics().requestRendering();
                                        return;
                                    case 2:
                                        int pointerCount2 = event.getPointerCount();
                                        int y5 = 0;
                                        int x5 = pointerIndex2;
                                        int pointerIndex3 = 0;
                                        int button7 = 0;
                                        int button8 = 0;
                                        int realPointerIndex9 = 0;
                                        while (pointerIndex3 < pointerCount2) {
                                            int pointerIndex4 = pointerIndex3;
                                            try {
                                                int pointerId2 = event.getPointerId(pointerIndex4);
                                                int x6 = (int) event.getX(pointerIndex4);
                                                try {
                                                    int y6 = (int) event.getY(pointerIndex4);
                                                    try {
                                                        int realPointerIndex10 = input.lookUpPointerIndex(pointerId2);
                                                        if (realPointerIndex10 == realPointerIndex6) {
                                                            y4 = y6;
                                                            realPointerIndex5 = realPointerIndex10;
                                                            x4 = x6;
                                                            pointerIndex = pointerIndex4;
                                                            i = realPointerIndex6;
                                                            pointerCount = pointerCount2;
                                                        } else if (realPointerIndex10 >= i2) {
                                                            Gdx.app.getGraphics().requestRendering();
                                                            return;
                                                        } else {
                                                            try {
                                                                int button9 = input.button[realPointerIndex10];
                                                                if (button9 != realPointerIndex6) {
                                                                    y4 = y6;
                                                                    realPointerIndex5 = realPointerIndex10;
                                                                    button4 = button9;
                                                                    x4 = x6;
                                                                    pointerIndex = pointerIndex4;
                                                                    i = realPointerIndex6;
                                                                    pointerCount = pointerCount2;
                                                                    try {
                                                                        postTouchEvent(input, 2, x6, y4, realPointerIndex5, button4, timeStamp);
                                                                    } catch (Throwable th5) {
                                                                        th = th5;
                                                                        throw th;
                                                                    }
                                                                } else {
                                                                    y4 = y6;
                                                                    realPointerIndex5 = realPointerIndex10;
                                                                    button4 = button9;
                                                                    x4 = x6;
                                                                    pointerIndex = pointerIndex4;
                                                                    i = realPointerIndex6;
                                                                    pointerCount = pointerCount2;
                                                                    postTouchEvent(input, 4, x4, y4, realPointerIndex5, 0, timeStamp);
                                                                }
                                                                input.deltaX[realPointerIndex5] = x4 - input.touchX[realPointerIndex5];
                                                                input.deltaY[realPointerIndex5] = y4 - input.touchY[realPointerIndex5];
                                                                input.touchX[realPointerIndex5] = x4;
                                                                input.touchY[realPointerIndex5] = y4;
                                                                button7 = button4;
                                                            } catch (Throwable th6) {
                                                                th = th6;
                                                            }
                                                        }
                                                        pointerIndex3++;
                                                        realPointerIndex6 = i;
                                                        button8 = realPointerIndex5;
                                                        realPointerIndex9 = y4;
                                                        y5 = x4;
                                                        x5 = pointerIndex;
                                                        pointerCount2 = pointerCount;
                                                        i2 = 20;
                                                    } catch (Throwable th7) {
                                                        th = th7;
                                                    }
                                                } catch (Throwable th8) {
                                                    th = th8;
                                                }
                                            } catch (Throwable th9) {
                                                th = th9;
                                            }
                                        }
                                        Gdx.app.getGraphics().requestRendering();
                                        return;
                                    default:
                                        Gdx.app.getGraphics().requestRendering();
                                        return;
                                }
                            } catch (Throwable th10) {
                                th = th10;
                            }
                        } catch (Throwable th11) {
                            th = th11;
                        }
                    } catch (Throwable th12) {
                        th = th12;
                    }
                } catch (Throwable th13) {
                    th = th13;
                }
            } catch (Throwable th14) {
                th = th14;
            }
        }
    }

    private void logAction(int action, int pointer) {
        String actionStr;
        if (action == 0) {
            actionStr = "DOWN";
        } else if (action == 5) {
            actionStr = "POINTER DOWN";
        } else if (action == 1) {
            actionStr = "UP";
        } else if (action == 6) {
            actionStr = "POINTER UP";
        } else if (action == 4) {
            actionStr = "OUTSIDE";
        } else if (action == 3) {
            actionStr = "CANCEL";
        } else if (action == 2) {
            actionStr = "MOVE";
        } else {
            actionStr = "UNKNOWN (" + action + NavigationBarInflaterView.KEY_CODE_END;
        }
        Gdx.app.log("AndroidMultiTouchHandler", "action " + actionStr + ", Android pointer id: " + pointer);
    }

    private int toGdxButton(int button) {
        if (button == 0 || button == 1) {
            return 0;
        }
        if (button == 2) {
            return 1;
        }
        if (button == 4) {
            return 2;
        }
        if (button == 8) {
            return 3;
        }
        if (button == 16) {
            return 4;
        }
        return -1;
    }

    private void postTouchEvent(AndroidInput input, int type, int x, int y, int pointer, int button, long timeStamp) {
        AndroidInput.TouchEvent event = input.usedTouchEvents.obtain();
        event.timeStamp = timeStamp;
        event.pointer = pointer;
        event.x = x;
        event.y = y;
        event.type = type;
        event.button = button;
        input.touchEvents.add(event);
    }

    @Override // com.badlogic.gdx.backends.android.AndroidTouchHandler
    public boolean supportsMultitouch(Context activity) {
        return activity.getPackageManager().hasSystemFeature("android.hardware.touchscreen.multitouch");
    }
}
