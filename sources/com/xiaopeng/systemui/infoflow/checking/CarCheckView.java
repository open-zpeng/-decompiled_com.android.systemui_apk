package com.xiaopeng.systemui.infoflow.checking;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.android.systemui.R;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.infoflow.LandscapeInfoFlow;
import com.xiaopeng.systemui.infoflow.checking.bean.CheckInfo;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
/* loaded from: classes24.dex */
public class CarCheckView extends AlphaOptimizedRelativeLayout {
    private static final int STATUS_CHECKED = 1;
    private static final int STATUS_CHECKING = 0;
    private static final int STATUS_WITHOUT_FAULT = 2;
    private static final String TAG = CarCheckView.class.getSimpleName();
    private ImageView mCarImageView;
    private CheckedView mCheckedView;
    private CheckingView mCheckingView;
    private ImageView mQuitImageView;

    public CarCheckView(Context context) {
        this(context, null);
    }

    public CarCheckView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CarCheckView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    public void updateData(CardEntry cardEntry) {
        String title = cardEntry.title;
        String content = cardEntry.content;
        String extraData = cardEntry.extraData;
        CheckInfo checkInfo = (CheckInfo) GsonUtil.fromJson(extraData, (Class<Object>) CheckInfo.class);
        if (checkInfo.status == 0) {
            this.mCarImageView.setImageResource(R.mipmap.ic_checking_car);
            this.mCheckedView.setVisibility(8);
            this.mCheckingView.setVisibility(0);
            this.mCheckingView.setData(title, content, checkInfo);
        } else if (checkInfo.status == 1) {
            this.mCarImageView.setImageResource(R.mipmap.ic_abnormal_car);
            this.mCheckingView.setVisibility(8);
            this.mCheckedView.setVisibility(0);
            this.mCheckedView.setData(title, content, checkInfo, cardEntry.key);
        } else if (checkInfo.status == 2) {
            stopCheck();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mCarImageView = (ImageView) findViewById(R.id.img_car);
        this.mCheckingView = (CheckingView) findViewById(R.id.view_checking);
        this.mCheckedView = (CheckedView) findViewById(R.id.view_checked);
        this.mQuitImageView = (ImageView) findViewById(R.id.img_check_quit);
        this.mQuitImageView.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.checking.CarCheckView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CarCheckView.this.stopCheck();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopCheck() {
        LandscapeInfoFlow landscapeSpeechPresenter = (LandscapeInfoFlow) PresenterCenter.getInstance().getInfoFlow();
        landscapeSpeechPresenter.exitCarCheck();
    }
}
