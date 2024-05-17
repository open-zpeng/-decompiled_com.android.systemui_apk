package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.infoflow.speech.ui.ICaracPresenter;
import com.xiaopeng.systemui.infoflow.speech.ui.ICaracView;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.seekbar.ArcSeekBar;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
/* loaded from: classes24.dex */
public class CaracView extends AlphaOptimizedRelativeLayout implements ICaracView {
    private static final String TAG = CaracView.class.getSimpleName();
    public static final int TYPE_CARAC = 1;
    private ArcSeekBar mArcSeekBar;
    private TextView mContent;
    private ImageView mIcon;
    private ICaracPresenter mPresenter;
    private ArcSeekBar.OnProgressChangeListener mProgressChangeListener;
    private ImageView mTip;
    private TextView mTitle;
    private int mType;

    public CaracView(Context context) {
        super(context);
        this.mType = 1;
        this.mProgressChangeListener = new ArcSeekBar.OnProgressChangeListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.CaracView.1
            @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.seekbar.ArcSeekBar.OnProgressChangeListener
            public void onProgressChanged(ArcSeekBar seekBar, int progress, boolean isUser) {
                CaracView.this.mContent.setText(CaracView.this.getContext().getResources().getString(R.string.hvac_temperature_symbol, Integer.valueOf(seekBar.getProgress())));
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.seekbar.ArcSeekBar.OnProgressChangeListener
            public void onStartTrackingTouch(ArcSeekBar seekBar) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.seekbar.ArcSeekBar.OnProgressChangeListener
            public void onStopTrackingTouch(ArcSeekBar seekBar) {
                CaracView.this.mPresenter.setCaracTemp(seekBar.getProgress());
            }
        };
    }

    public CaracView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mType = 1;
        this.mProgressChangeListener = new ArcSeekBar.OnProgressChangeListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.CaracView.1
            @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.seekbar.ArcSeekBar.OnProgressChangeListener
            public void onProgressChanged(ArcSeekBar seekBar, int progress, boolean isUser) {
                CaracView.this.mContent.setText(CaracView.this.getContext().getResources().getString(R.string.hvac_temperature_symbol, Integer.valueOf(seekBar.getProgress())));
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.seekbar.ArcSeekBar.OnProgressChangeListener
            public void onStartTrackingTouch(ArcSeekBar seekBar) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.seekbar.ArcSeekBar.OnProgressChangeListener
            public void onStopTrackingTouch(ArcSeekBar seekBar) {
                CaracView.this.mPresenter.setCaracTemp(seekBar.getProgress());
            }
        };
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.ICaracView
    public void setPresenter(ICaracPresenter presenter) {
        this.mPresenter = presenter;
    }

    public void setType(int type) {
        this.mType = type;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mIcon = (ImageView) findViewById(R.id.img_icon);
        this.mTitle = (TextView) findViewById(R.id.tv_title);
        this.mContent = (TextView) findViewById(R.id.tv_content);
        this.mTip = (ImageView) findViewById(R.id.img_tip);
        if (!CarModelsManager.getConfig().isAllWheelKeyToICMSupport()) {
            this.mTip.setVisibility(0);
        }
        this.mArcSeekBar = (ArcSeekBar) findViewById(R.id.arcSeekbar);
        this.mArcSeekBar.setOnProgressChangeListener(this.mProgressChangeListener);
        showInfo();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mPresenter.getCurrentValue();
    }

    private void showInfo() {
        if (this.mType == 1) {
            setBackgroundResource(R.mipmap.bg_speech_view);
            this.mIcon.setImageResource(R.mipmap.ic_temp);
            this.mTitle.setText(R.string.carac_title);
            this.mTip.setImageResource(R.mipmap.ic_carac_tip);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.ICaracView
    public void onTempSet(int value) {
        this.mContent.setText(getContext().getResources().getString(R.string.hvac_temperature_symbol, Integer.valueOf(value)));
        this.mArcSeekBar.setProgress(value);
    }
}
