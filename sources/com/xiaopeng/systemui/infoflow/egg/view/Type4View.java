package com.xiaopeng.systemui.infoflow.egg.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.infoflow.egg.bean.HolidayLayout;
/* loaded from: classes24.dex */
public class Type4View extends BaseHolidayView {
    @Override // com.xiaopeng.systemui.infoflow.egg.view.BaseHolidayView
    public View createView(final HolidayLayout layout) {
        LayoutInflater layoutInflater = LayoutInflater.from(SystemUIApplication.getContext());
        View view = layoutInflater.inflate(R.layout.view_egg_type4, (ViewGroup) null);
        final ImageView iv = (ImageView) view.findViewById(R.id.iv_pic);
        iv.setImageDrawable(layout.getBackground());
        iv.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.egg.view.Type4View.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (Type4View.this.mOnClickListener != null) {
                    iv.setTag(R.id.tag_url, layout.getUrl());
                    Type4View.this.mOnClickListener.onClick(iv);
                }
            }
        });
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
        if (layout.getBtns() != null && layout.getBtns().size() >= 2) {
            int[] ids = {R.id.btn_1, R.id.btn_2};
            for (int i = 0; i < ids.length; i++) {
                TextView btn = (TextView) view.findViewById(ids[i]);
                btn.setBackground(layout.getBtnBackground(i));
                final int index = i;
                btn.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.egg.view.Type4View.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        if (Type4View.this.mOnClickListener != null) {
                            v.setTag(R.id.tag_url, layout.getBtns().get(index).url);
                            Type4View.this.mOnClickListener.onClick(v);
                        }
                    }
                });
            }
        }
        return view;
    }
}
