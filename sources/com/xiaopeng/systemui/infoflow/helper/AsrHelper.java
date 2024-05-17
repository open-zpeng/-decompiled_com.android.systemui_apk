package com.xiaopeng.systemui.infoflow.helper;

import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;
import com.xiaopeng.speech.protocol.bean.recommend.RecommendBean;
import com.xiaopeng.systemui.ViewFactory;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.speech.LocAsrView;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.OrientationUtil;
/* loaded from: classes24.dex */
public class AsrHelper {
    public static final int ASR_STATUS_ASR_BEGIN = 8;
    public static final int ASR_STATUS_DEFAULT = 0;
    public static final int ASR_STATUS_INVALID_SPEECH = 6;
    public static final int ASR_STATUS_LISTENING = 7;
    public static final int ASR_STATUS_NETWORK_EXCEPTION = 2;
    public static final int ASR_STATUS_NLU = 1;
    public static final int ASR_STATUS_TIMEOUT = 3;
    public static final int ASR_STATUS_TTS_END = 5;
    public static final int ASR_STATUS_TTS_START = 4;
    public static final int ASR_STATUS_VAD_BEGIN = 9;
    public static final int ASR_STATUS_VAD_END = 10;
    public static final int ASR_TEXT_STATUS_INVALID_SPEECH = 3;
    public static final int ASR_TEXT_STATUS_NORMAL = 0;
    public static final int ASR_TEXT_STATUS_SHOW_STATUS = 2;
    public static final int ASR_TEXT_STATUS_TTS = 1;
    private static final String TAG = "AsrHelper";
    private OnAvatarSceneChangeListener mOnAvatarSceneChangeListener;
    private boolean mIsListening = false;
    private boolean mListeningAnimationPlayed = false;
    private boolean mIsAvatarSceneReplaced = false;
    private boolean mIsAvatarSceneRestored = true;
    protected Context mContext = ContextUtils.getContext();
    private SparseArray<LocAsrView> mLocAsrViewSparseArray = new SparseArray<>();

    /* loaded from: classes24.dex */
    public interface OnAvatarSceneChangeListener {
        void onReplaceAvatarScene();

        void onRestoreAvatarScene();
    }

    public void showAsrView(boolean b) {
        updateShowText(b ? "" : null);
    }

    public void showAsrView(int asrLoc, boolean b) {
        updateShowText(asrLoc, b ? "" : null);
    }

    protected void checkAvatarGone() {
    }

    protected void checkAvatarShow() {
    }

    protected void startListeningAnimation() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingletonHolder {
        private static final AsrHelper sInstance;

        private SingletonHolder() {
        }

        static {
            sInstance = OrientationUtil.isLandscapeScreen(ContextUtils.getContext()) ? new LandscapeAsrHelper() : new VerticalAsrHelper();
        }
    }

    public static final AsrHelper getInstance() {
        return SingletonHolder.sInstance;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AsrHelper() {
        this.mLocAsrViewSparseArray.put(1, new LocAsrView(1, ViewFactory.getAsrView()));
    }

    public void setOnAvatarSceneChangeListener(OnAvatarSceneChangeListener listener) {
        this.mOnAvatarSceneChangeListener = listener;
    }

    public void updateAsrContainer(ViewGroup asrContainer) {
        updateAsrContainer(1, asrContainer);
    }

    public void updateAsrContainer(int asrLoc, ViewGroup asrContainer) {
        LocAsrView locAsrView = this.mLocAsrViewSparseArray.get(asrLoc);
        if (locAsrView == null) {
            Logger.d(TAG, "updateAsrContainer : asrLoc = " + asrLoc);
            locAsrView = new LocAsrView(asrLoc, ViewFactory.getAsrView());
            this.mLocAsrViewSparseArray.put(asrLoc, locAsrView);
        }
        locAsrView.setAsrLoc(asrLoc);
        locAsrView.updateAsrContainer(asrContainer);
    }

    public void updateAsrListeningStatus(boolean isListening, boolean showListeningStatus) {
        updateAsrListeningStatus(1, isListening, showListeningStatus);
    }

    public void updateAsrListeningStatus(int asrLoc, boolean isListening, boolean showListeningStatus) {
        Logger.d(TAG, "updateAsrListeningStatus : mIsListening = " + this.mIsListening);
        LocAsrView locAsrView = this.mLocAsrViewSparseArray.get(asrLoc);
        if (locAsrView != null) {
            locAsrView.updateAsrListeningStatus(isListening, showListeningStatus);
        }
    }

    public RecommendBean setRecommendData(String recommendData) {
        return setRecommendData(1, recommendData);
    }

    public RecommendBean setRecommendData(int asrLoc, String recommendData) {
        LocAsrView locAsrView = this.mLocAsrViewSparseArray.get(asrLoc);
        if (locAsrView != null) {
            return locAsrView.setRecommendData(recommendData);
        }
        return null;
    }

    public void updateAsrStatus(int status, boolean isListening) {
        updateAsrStatus(1, status, isListening);
    }

    public void updateAsrStatus(int asrLoc, int status, boolean isListening) {
        LocAsrView locAsrView = this.mLocAsrViewSparseArray.get(asrLoc);
        if (locAsrView != null) {
            locAsrView.updateAsrStatus(status, isListening);
        }
    }

    public void showVuiRecommendView(boolean hasAnimation) {
        showVuiRecommendView(1, hasAnimation);
    }

    public void showVuiRecommendView(int asrLoc, boolean hasAnimation) {
        LocAsrView locAsrView = this.mLocAsrViewSparseArray.get(asrLoc);
        if (locAsrView != null) {
            locAsrView.showVuiRecommendView(hasAnimation);
        }
    }

    public void hideVuiRecommendView() {
        LocAsrView locAsrView = this.mLocAsrViewSparseArray.get(1);
        if (locAsrView != null) {
            locAsrView.hideVuiRecommendView();
        }
    }

    public void clearAsr() {
        updateShowText("");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setAsrText(String text) {
        LocAsrView locAsrView = this.mLocAsrViewSparseArray.get(1);
        if (locAsrView != null) {
            locAsrView.setAsrText(text);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setAsrText() {
        LocAsrView locAsrView = this.mLocAsrViewSparseArray.get(1);
        if (locAsrView != null) {
            locAsrView.setAsrText(locAsrView.getAsrText());
        }
    }

    public void updateShowText(String text) {
        updateShowText(1, text);
    }

    public void updateShowText(int asrLoc, String text) {
        LocAsrView locAsrView = this.mLocAsrViewSparseArray.get(asrLoc);
        if (locAsrView != null) {
            locAsrView.updateShowText(text);
        }
    }

    public void updateShowNormalText(String text) {
        updateShowNormalText(1, text, false);
    }

    public void updateShowNormalText(int asrLoc, String text, boolean invalid) {
        LocAsrView locAsrView = this.mLocAsrViewSparseArray.get(asrLoc);
        Logger.d(TAG, "updateShowNormalText : " + asrLoc + " , " + locAsrView);
        if (locAsrView != null) {
            locAsrView.updateShowNormalText(text, invalid);
        }
    }

    public void restoreAsrStatus() {
    }

    public OnAvatarSceneChangeListener getOnAvatarSceneChangeListener() {
        return this.mOnAvatarSceneChangeListener;
    }
}
