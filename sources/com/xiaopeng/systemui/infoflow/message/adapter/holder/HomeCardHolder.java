package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
/* loaded from: classes24.dex */
public class HomeCardHolder extends BaseCardHolder {
    private static final String TAG = HomeCardHolder.class.getSimpleName();
    private ImageView mAppListImg;

    public HomeCardHolder(View itemView) {
        super(itemView);
        itemView.setClickable(false);
        this.mAppListImg = (ImageView) itemView.findViewById(R.id.img_app_list);
        this.mAppListImg.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.HomeCardHolder.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Logger.d(HomeCardHolder.TAG, "mAppListImg be clicked");
                HomeCardHolder.this.mInfoflowCardPresenter.onCardClicked();
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public int getCardType() {
        return 22;
    }
}
