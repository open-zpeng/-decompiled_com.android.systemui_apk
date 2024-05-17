package com.xiaopeng.systemui.infoflow.egg.view;
/* loaded from: classes24.dex */
public class HolidayViewFactory {
    private static final int TYPE_1 = 1;
    private static final int TYPE_2 = 2;
    private static final int TYPE_3 = 3;
    private static final int TYPE_4 = 4;
    private static final int TYPE_5 = 5;
    private static final int TYPE_6 = 6;

    public static BaseHolidayView getHolidayView(int type) {
        if (type == 1) {
            return new Type1View();
        }
        if (type == 2) {
            return new Type2View();
        }
        if (type == 3) {
            return new Type3View();
        }
        if (type == 4) {
            return new Type4View();
        }
        if (type == 5) {
            return new Type5View();
        }
        if (type == 6) {
            return new Type6View();
        }
        return null;
    }
}
