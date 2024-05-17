package com.xiaopeng.systemui.infoflow.montecarlo.view.sapa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.theme.AnimatedImageView;
import java.util.ArrayList;
import java.util.List;
@SuppressLint({"AppCompatCustomView"})
/* loaded from: classes24.dex */
public class SapaDetailView extends AnimatedImageView {
    private static final int ICON_MARGIN = 8;
    private static final int ICON_SIZE = 32;
    private final long INVAIL_SAPA_DETAIL;
    private long mSapaDetail;
    private static final String TAG = SapaDetailView.class.getSimpleName();
    public static final int[] SAPA_DETAIL_ICON_ARRAY = {R.mipmap.ic_small_chargingstation, R.mipmap.ic_small_food, R.mipmap.ic_small_toilet, R.mipmap.ic_small_repair, R.mipmap.ic_small_shoping, R.mipmap.ic_small_lodging};

    public SapaDetailView(Context context) {
        super(context);
        this.INVAIL_SAPA_DETAIL = -1L;
        this.mSapaDetail = -1L;
    }

    public SapaDetailView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.INVAIL_SAPA_DETAIL = -1L;
        this.mSapaDetail = -1L;
    }

    public void setSapaDetail(long sapaDetail) {
        this.mSapaDetail = sapaDetail;
        invalidate();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long j = this.mSapaDetail;
        if (j == -1) {
            return;
        }
        List<Integer> iconList = getIconList(j);
        for (int j2 = 0; j2 < iconList.size(); j2++) {
            Drawable drawable = getResources().getDrawable(iconList.get(j2).intValue(), getContext().getTheme());
            int left = j2 * 40;
            int right = left + 32;
            drawable.setBounds(left, 0, right, 32);
            drawable.draw(canvas);
        }
    }

    private List<Integer> getIconList(long sapaDetail) {
        List<Integer> iconList = new ArrayList<>();
        for (int i = 0; i < SAPA_DETAIL_ICON_ARRAY.length; i++) {
            if (((1 << i) & sapaDetail) != 0) {
                iconList.add(getSAPAIcon(i));
            }
        }
        return iconList;
    }

    private Integer getSAPAIcon(int i) {
        return Integer.valueOf(SAPA_DETAIL_ICON_ARRAY[i]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AnimatedImageView, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        invalidate();
    }
}
