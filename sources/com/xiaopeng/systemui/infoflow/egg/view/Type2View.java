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
public class Type2View extends BaseHolidayView {
    @Override // com.xiaopeng.systemui.infoflow.egg.view.BaseHolidayView
    public View createView(final HolidayLayout layout) {
        LayoutInflater layoutInflater = LayoutInflater.from(SystemUIApplication.getContext());
        View view = layoutInflater.inflate(R.layout.view_egg_type2, (ViewGroup) null);
        final ImageView iv = (ImageView) view.findViewById(R.id.iv_pic);
        iv.setImageDrawable(layout.getBackground());
        iv.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.egg.view.Type2View.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (Type2View.this.mOnClickListener != null) {
                    iv.setTag(R.id.tag_url, layout.getUrl());
                    Type2View.this.mOnClickListener.onClick(iv);
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
        if (layout.getDesc() != null) {
            TextView title3 = (TextView) view.findViewById(R.id.tv_desc);
            title3.setText(layout.getDescText());
            title3.setTextColor(layout.getDescColor());
        }
        return view;
    }
}
