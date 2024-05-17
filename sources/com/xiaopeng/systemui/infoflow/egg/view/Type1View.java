package com.xiaopeng.systemui.infoflow.egg.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.infoflow.egg.bean.HolidayLayout;
/* loaded from: classes24.dex */
public class Type1View extends BaseHolidayView {
    @Override // com.xiaopeng.systemui.infoflow.egg.view.BaseHolidayView
    public View createView(final HolidayLayout layout) {
        LayoutInflater layoutInflater = LayoutInflater.from(SystemUIApplication.getContext());
        View view = layoutInflater.inflate(R.layout.view_egg_type1, (ViewGroup) null);
        final ImageView iv = (ImageView) view.findViewById(R.id.iv_pic);
        iv.setImageDrawable(layout.getBackground());
        iv.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.egg.view.Type1View.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (Type1View.this.mOnClickListener != null) {
                    iv.setTag(R.id.tag_url, layout.getUrl());
                    Type1View.this.mOnClickListener.onClick(iv);
                }
            }
        });
        return view;
    }
}
