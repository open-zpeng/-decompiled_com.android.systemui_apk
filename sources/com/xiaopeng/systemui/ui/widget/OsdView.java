package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.AudioController;
import com.xiaopeng.systemui.controller.OsdController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class OsdView extends AlphaOptimizedLinearLayout {
    private static final String TAG = "OsdView";
    private InteractiveOsdView mOsdInteractive;
    private AlphaOptimizedRelativeLayout mOsdNormal;
    private int mScreenId;
    private ThemeViewModel mThemeViewModel;
    private int mType;
    private AnimatedTextView mViewContent;
    private AnimatedImageView mViewIcon;
    private OsdController.OsdParams mViewParams;
    private AnimatedProgressBar mViewProgress;
    private AnimatedTextView mViewTitle;

    public OsdView(Context context) {
        super(context);
        this.mType = 0;
        this.mScreenId = -1;
        init(context, null, 0, 0);
    }

    public OsdView(Context context, OsdController.OsdParams params) {
        super(context);
        this.mType = 0;
        this.mScreenId = -1;
        this.mViewParams = params;
        init(context, null, 0, 0);
    }

    public OsdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mType = 0;
        this.mScreenId = -1;
        init(context, attrs, 0, 0);
    }

    public OsdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mType = 0;
        this.mScreenId = -1;
        init(context, attrs, defStyleAttr, 0);
    }

    public OsdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mType = 0;
        this.mScreenId = -1;
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        LayoutInflater.from(context).inflate(R.layout.view_osd, this);
        this.mThemeViewModel = ThemeViewModel.create(context, attrs, defStyleAttr, defStyleRes);
        this.mViewIcon = (AnimatedImageView) findViewById(R.id.icon);
        this.mViewTitle = (AnimatedTextView) findViewById(R.id.title);
        this.mViewContent = (AnimatedTextView) findViewById(R.id.content);
        this.mViewProgress = (AnimatedProgressBar) findViewById(R.id.progress);
        this.mViewProgress.setTouchable(false);
        this.mOsdNormal = (AlphaOptimizedRelativeLayout) findViewById(R.id.osd_normal);
        this.mOsdInteractive = (InteractiveOsdView) findViewById(R.id.osd_interactive);
        apply(this.mViewParams);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        Logger.d(TAG, "onFinishInflate");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.ui.widget.AlphaOptimizedLinearLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            apply(this.mViewParams);
        }
    }

    public void setTitle(CharSequence title) {
        this.mViewTitle.setText(title);
    }

    public void setTitleColor(int titleColor) {
        this.mViewTitle.setTextColor(this.mContext.getColor(titleColor));
    }

    public void setTitleIcon(Drawable left, Drawable right) {
        this.mViewTitle.setCompoundDrawablesWithIntrinsicBounds(left, (Drawable) null, right, (Drawable) null);
    }

    public void setIcon(Drawable icon) {
        this.mViewIcon.setImageDrawable(icon);
    }

    public void setContent(CharSequence content) {
        if (!TextUtils.isEmpty(content)) {
            this.mViewContent.setVisibility(0);
            this.mViewProgress.setVisibility(8);
        } else {
            this.mViewContent.setVisibility(8);
            this.mViewProgress.setVisibility(0);
        }
        this.mViewContent.setText(content);
    }

    public void setProgress(int progress, int min, int max) {
        this.mViewProgress.setMin(min);
        this.mViewProgress.setMax(max);
        this.mViewProgress.setProgress(progress);
    }

    public void apply(OsdController.OsdParams params) {
        Logger.d(TAG, "apply params=" + params);
        if (params != null) {
            this.mType = params.mType;
            this.mScreenId = params.mScreenId;
            if (params.mType == 1) {
                if (!this.mOsdInteractive.isVisibleToUser()) {
                    this.mOsdInteractive.reset();
                }
                this.mOsdInteractive.setVisibility(0);
                this.mOsdNormal.setVisibility(8);
                this.mOsdInteractive.setCurrentStreamType(params.mStreamType);
                this.mOsdInteractive.setProgress(params.mStreamType, params.mProgress, params.mProgressMin, params.mProgressMax);
                setOtherStreamVolumeProgress(params);
                return;
            }
            Drawable icon = params.mIcon != null ? params.mIcon.loadDrawable(getContext()) : null;
            Drawable left = params.mTitleLeft != null ? params.mTitleLeft.loadDrawable(getContext()) : null;
            Drawable right = params.mTitleRight != null ? params.mTitleRight.loadDrawable(getContext()) : null;
            setTitle(params.mTitle);
            setTitleColor(params.mTitleColor);
            setTitleIcon(left, right);
            setIcon(icon);
            setContent(params.mContent);
            setProgress(params.mProgress, params.mProgressMin, params.mProgressMax);
            this.mOsdInteractive.setVisibility(8);
            this.mOsdNormal.setVisibility(0);
        }
    }

    public int getOsdType() {
        return this.mType;
    }

    public int getScreenId() {
        return this.mScreenId;
    }

    private void setOtherStreamVolumeProgress(OsdController.OsdParams params) {
        List<Integer> otherStreamTypes = new ArrayList<>();
        if (params.mStreamType != 3) {
            otherStreamTypes.add(3);
        }
        if (params.mStreamType != 9) {
            otherStreamTypes.add(9);
        }
        if (params.mStreamType != 10) {
            otherStreamTypes.add(10);
        }
        if (params.mStreamType != 11) {
            otherStreamTypes.add(11);
        }
        for (Integer num : otherStreamTypes) {
            int streamType = num.intValue();
            AudioController audioController = AudioController.getInstance(this.mContext);
            int progress = audioController.getStreamVolume(streamType);
            int progressMax = audioController.getStreamMaxVolume(streamType);
            this.mOsdInteractive.setProgress(streamType, progress, 0, progressMax);
        }
    }

    /* loaded from: classes24.dex */
    public static final class Builder {
        private Context mContext;
        private OsdController.OsdParams mParams = new OsdController.OsdParams();

        public Builder(Context context, String title) {
            this.mContext = context;
            this.mParams.mTitle = title;
        }

        public Builder(Context context, String title, Icon left, Icon right) {
            this.mContext = context;
            OsdController.OsdParams osdParams = this.mParams;
            osdParams.mTitle = title;
            osdParams.mTitleLeft = left;
            osdParams.mTitleRight = right;
        }

        public Builder setIcon(Icon icon) {
            return this;
        }

        public Builder setContent(String content) {
            this.mParams.mContent = content;
            return this;
        }

        public Builder setProgress(int progress, int min, int max) {
            OsdController.OsdParams osdParams = this.mParams;
            osdParams.mProgress = progress;
            osdParams.mProgressMin = min;
            osdParams.mProgressMax = max;
            return this;
        }

        public OsdView create() {
            return new OsdView(this.mContext, this.mParams);
        }
    }
}
