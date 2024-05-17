package com.xiaopeng.systemui.infoflow.montecarlo.view.sapa;

import android.content.Context;
import android.util.AttributeSet;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedLinearLayout;
import com.xiaopeng.xuimanager.contextinfo.Sapa;
import com.xiaopeng.xuimanager.contextinfo.SapaInfo;
import java.util.List;
/* loaded from: classes24.dex */
public class SapaInfoView extends AlphaOptimizedLinearLayout {
    private static final String TAG = SapaInfoView.class.getSimpleName();
    private final int VIEW_SIZE;
    private Sapa mSapa;
    private SapaItemView[] mSapaItemViews;

    public SapaInfoView(Context context) {
        super(context);
        this.VIEW_SIZE = 3;
        this.mSapaItemViews = new SapaItemView[3];
    }

    public SapaInfoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.VIEW_SIZE = 3;
        this.mSapaItemViews = new SapaItemView[3];
    }

    public void setData(Sapa sapa) {
        if (sapa == null || sapa.getSapaInfo() == null) {
            return;
        }
        this.mSapa = sapa;
        List<SapaInfo> sapaInfos = sapa.getSapaInfo();
        int size = sapaInfos.size();
        for (int i = 0; i < size; i++) {
            this.mSapaItemViews[i].setVisibility(0);
            this.mSapaItemViews[i].setData(sapaInfos.get(i));
            this.mSapaItemViews[i].setElevation((size - i) * 5);
        }
        for (int j = size; j < 3; j++) {
            this.mSapaItemViews[j].setVisibility(8);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < 3; i++) {
            this.mSapaItemViews[i] = (SapaItemView) getChildAt(i);
        }
    }
}
