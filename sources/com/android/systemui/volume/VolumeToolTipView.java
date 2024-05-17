package com.android.systemui.volume;

import android.content.Context;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import com.android.systemui.R;
import com.android.systemui.recents.TriangleShape;
/* loaded from: classes21.dex */
public class VolumeToolTipView extends LinearLayout {
    public VolumeToolTipView(Context context) {
        super(context);
    }

    public VolumeToolTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VolumeToolTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public VolumeToolTipView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        drawArrow();
    }

    private void drawArrow() {
        View arrowView = findViewById(R.id.arrow);
        ViewGroup.LayoutParams arrowLp = arrowView.getLayoutParams();
        ShapeDrawable arrowDrawable = new ShapeDrawable(TriangleShape.createHorizontal(arrowLp.width, arrowLp.height, false));
        Paint arrowPaint = arrowDrawable.getPaint();
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(16843829, typedValue, true);
        arrowPaint.setColor(ContextCompat.getColor(getContext(), typedValue.resourceId));
        arrowPaint.setPathEffect(new CornerPathEffect(getResources().getDimension(R.dimen.volume_tool_tip_arrow_corner_radius)));
        arrowView.setBackground(arrowDrawable);
    }
}
