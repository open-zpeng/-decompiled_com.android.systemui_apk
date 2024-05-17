package com.xiaopeng.systemui.infoflow.egg.view;

import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.infoflow.egg.bean.HolidayLayout;
/* loaded from: classes24.dex */
public class Type3View extends BaseHolidayView {
    @Override // com.xiaopeng.systemui.infoflow.egg.view.BaseHolidayView
    public View createView(final HolidayLayout layout) {
        LayoutInflater layoutInflater = LayoutInflater.from(SystemUIApplication.getContext());
        View view = layoutInflater.inflate(R.layout.view_egg_type3, (ViewGroup) null);
        final ImageView iv = (ImageView) view.findViewById(R.id.iv_pic);
        iv.setImageDrawable(layout.getBackground());
        iv.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.egg.view.Type3View.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (Type3View.this.mOnClickListener != null) {
                    iv.setTag(R.id.tag_url, layout.getUrl());
                    Type3View.this.mOnClickListener.onClick(iv);
                }
            }
        });
        if (layout.getTag() != null) {
            TextView tag = (TextView) view.findViewById(R.id.tv_tag);
            tag.setText(layout.getTagText());
            tag.setTextColor(layout.getTagColor());
            tag.setBackground(getTagBack(layout.getTagBackgroundColor()));
        }
        if (layout.getTitle() != null) {
            TextView title = (TextView) view.findViewById(R.id.tv_title);
            title.setText(layout.getTitleText());
            title.setTextColor(layout.getTitleColor());
        }
        if (layout.getSubTitle() != null) {
            TextView title2 = (TextView) view.findViewById(R.id.tv_subtitle);
            title2.setText(layout.getSubTitleText());
            title2.setTextColor(layout.getSubTitleColor());
        }
        if (layout.getBtns() != null && layout.getBtns().size() > 0) {
            TextView btn = (TextView) view.findViewById(R.id.btn_submit);
            btn.setText(layout.getBtnText(0));
            btn.setTextColor(layout.getBtnTextColor(0));
            btn.setBackground(getBtnBack(layout.getBtnColor(0)));
            btn.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.egg.view.Type3View.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (Type3View.this.mOnClickListener != null) {
                        v.setTag(R.id.tag_url, layout.getBtns().get(0).url);
                        Type3View.this.mOnClickListener.onClick(v);
                    }
                }
            });
        }
        return view;
    }

    private GradientDrawable getTagBack(int color) {
        GradientDrawable drawable = null;
        try {
            float[] radii = {5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f};
            drawable = new GradientDrawable();
            drawable.setShape(0);
            drawable.setColor(color);
            drawable.setCornerRadii(radii);
            return drawable;
        } catch (Throwable e) {
            Log.e("Round", e.getLocalizedMessage());
            return drawable;
        }
    }

    private GradientDrawable getBtnBack(int color) {
        GradientDrawable drawable = null;
        try {
            float[] radii = {35.0f, 35.0f, 35.0f, 35.0f, 35.0f, 35.0f, 35.0f, 35.0f};
            drawable = new GradientDrawable();
            drawable.setShape(0);
            drawable.setColor(color);
            drawable.setCornerRadii(radii);
            return drawable;
        } catch (Throwable e) {
            Log.e("Round", e.getLocalizedMessage());
            return drawable;
        }
    }
}
