package com.xiaopeng.systemui.qs;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.xiaopeng.systemui.qs.views.TileView;
import java.util.List;
/* loaded from: classes24.dex */
public class QsDynamicLayout extends FrameLayout {
    private final Context mContext;

    public QsDynamicLayout(Context context) {
        super(context);
        this.mContext = context;
    }

    public void initQsLayout(List<TileState> tileStateList) {
        int layoutWidth;
        int sumArea = 0;
        for (TileState tileState : tileStateList) {
            sumArea += tileState.width;
        }
        if (sumArea > 12) {
            layoutWidth = ((QsPanelSetting.ATOM_WIDTH + QsPanelSetting.H_GAP) * 6) - QsPanelSetting.H_GAP;
        } else {
            int layoutWidth2 = QsPanelSetting.ATOM_WIDTH;
            layoutWidth = ((layoutWidth2 + QsPanelSetting.H_GAP) * 4) - QsPanelSetting.H_GAP;
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(layoutWidth, -1);
        setLayoutParams(layoutParams);
        for (TileState tileState2 : tileStateList) {
            int beginX = tileState2.x;
            int beginY = tileState2.y;
            int marginLeft = (QsPanelSetting.ATOM_WIDTH + QsPanelSetting.H_GAP) * beginX;
            int marginTop = (QsPanelSetting.ATOM_WIDTH + QsPanelSetting.V_GAP) * beginY;
            TileView tileView = TileViewFactory.createTileView(this.mContext, tileState2, this);
            if (tileView.getView().getLayoutParams().getClass().equals(FrameLayout.LayoutParams.class)) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) tileView.getView().getLayoutParams();
                lp.leftMargin = marginLeft;
                lp.topMargin = marginTop;
                addView(tileView.getView(), lp);
            } else {
                RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) tileView.getView().getLayoutParams();
                lp2.leftMargin = marginLeft;
                lp2.topMargin = marginTop;
                addView(tileView.getView(), lp2);
            }
        }
    }
}
