package com.xiaopeng.systemui.qs;

import android.content.Context;
import android.view.ViewGroup;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.qs.views.ButtonView;
import com.xiaopeng.systemui.qs.views.SliderView;
import com.xiaopeng.systemui.qs.views.TileView;
import com.xiaopeng.systemui.qs.views.TrunkButtonView;
/* loaded from: classes24.dex */
public class TileViewFactory {
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static TileView createTileView(Context context, TileState tileState, ViewGroup viewGroup) {
        char c;
        String str = tileState.interactType;
        switch (str.hashCode()) {
            case -1377687758:
                if (str.equals(ThemeManager.AttributeSet.BUTTON)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1088861651:
                if (str.equals("largebutton")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -899647263:
                if (str.equals("slider")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -103980975:
                if (str.equals("truckbutton")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0 || c == 1) {
            TileView tileView = new ButtonView(context, tileState);
            return tileView;
        } else if (c == 2) {
            TileView tileView2 = new SliderView(context, tileState);
            return tileView2;
        } else if (c == 3) {
            TileView tileView3 = new TrunkButtonView(context, tileState, viewGroup);
            return tileView3;
        } else {
            throw new IllegalArgumentException("Unexpected value: " + tileState.interactType);
        }
    }
}
