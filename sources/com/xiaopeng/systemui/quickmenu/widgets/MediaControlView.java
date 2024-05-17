package com.xiaopeng.systemui.quickmenu.widgets;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter;
import com.xiaopeng.systemui.quickmenu.QuickMenuHolderPresenter;
import com.xiaopeng.xui.widget.XButton;
import com.xiaopeng.xui.widget.XRelativeLayout;
import com.xiaopeng.xui.widget.XSeekBar;
import com.xiaopeng.xui.widget.XTextView;
/* loaded from: classes24.dex */
public class MediaControlView extends XRelativeLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "MediaControlView";
    private IQuickMenuHolderPresenter mCallback;
    private XButton mControlBtn;
    private long mCurrentDuration;
    private long mCurrentPosition;
    private XTextView mMusicDuration;
    private XTextView mMusicPosition;
    private XButton mNextBtn;
    private int mPlaybackState;
    private XButton mPrevBtn;
    private XSeekBar mProgressBar;
    private XTextView mSubTitleTextView;
    private ThemeViewModel mThemeViewModel;
    private XTextView mTitleTextView;

    public MediaControlView(Context context) {
        this(context, null);
    }

    public MediaControlView(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.MusicPanel);
    }

    public MediaControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mCurrentDuration = -1L;
        this.mCurrentPosition = -1L;
        init(context, attrs, defStyleAttr, 0);
    }

    public MediaControlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mCurrentDuration = -1L;
        this.mCurrentPosition = -1L;
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setMediaInfoListener(IQuickMenuHolderPresenter callback) {
        this.mCallback = callback;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        LayoutInflater.from(context).inflate(R.layout.layout_media_control, (ViewGroup) this, true);
        this.mTitleTextView = (XTextView) findViewById(R.id.tv_title);
        this.mSubTitleTextView = (XTextView) findViewById(R.id.tv_sub_title);
        this.mProgressBar = (XSeekBar) findViewById(R.id.progress_bar);
        this.mProgressBar.setOnSeekBarChangeListener(this);
        this.mControlBtn = (XButton) findViewById(R.id.btn_control);
        this.mControlBtn.setOnClickListener(this);
        this.mPrevBtn = (XButton) findViewById(R.id.btn_prev);
        this.mPrevBtn.setOnClickListener(this);
        this.mNextBtn = (XButton) findViewById(R.id.btn_next);
        this.mNextBtn.setOnClickListener(this);
        this.mMusicPosition = (XTextView) findViewById(R.id.music_position);
        this.mMusicDuration = (XTextView) findViewById(R.id.music_duration);
        this.mThemeViewModel = ThemeViewModel.create(context, attrs, defStyleAttr, defStyleRes);
    }

    public void enable() {
        this.mControlBtn.setEnabled(true);
        this.mPrevBtn.setEnabled(true);
        this.mNextBtn.setEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XRelativeLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ThemeViewModel themeViewModel = this.mThemeViewModel;
        if (themeViewModel != null) {
            themeViewModel.onAttachedToWindow(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XRelativeLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ThemeViewModel themeViewModel = this.mThemeViewModel;
        if (themeViewModel != null) {
            themeViewModel.onDetachedFromWindow(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XRelativeLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ThemeViewModel themeViewModel = this.mThemeViewModel;
        if (themeViewModel != null) {
            themeViewModel.onConfigurationChanged(this, newConfig);
        }
        Log.d(TAG, "mediacontrol isThemeChanged:" + ThemeManager.isThemeChanged(newConfig));
        if (ThemeManager.isThemeChanged(newConfig)) {
            refreshTheme();
        }
    }

    public void refreshTheme() {
        refreshControlBtn(this.mPlaybackState);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_control) {
            this.mCallback.onClickMediaControl();
        } else if (id == R.id.btn_next) {
            this.mCallback.onClickMediaNext();
        } else if (id == R.id.btn_prev) {
            this.mCallback.onClickMediaPrev();
        }
    }

    public void updateMediaInfo(final String songTitle, final String artist, final String album, final int stateMusic) {
        post(new Runnable() { // from class: com.xiaopeng.systemui.quickmenu.widgets.-$$Lambda$MediaControlView$ZW-3g2AIlsdFSX0A5cB-zy5yAYc
            @Override // java.lang.Runnable
            public final void run() {
                MediaControlView.this.lambda$updateMediaInfo$0$MediaControlView(songTitle, artist, album, stateMusic);
            }
        });
    }

    public /* synthetic */ void lambda$updateMediaInfo$0$MediaControlView(String songTitle, String artist, String album, int stateMusic) {
        this.mTitleTextView.setText(songTitle);
        this.mSubTitleTextView.setText(artist);
        logi("OnMediaInfoNotify: title=" + songTitle + " artist=" + artist + " album=" + album);
        if (stateMusic == -1) {
            hideProgress();
        }
    }

    public void updateMusicProgress(long position, long duration) {
        logv("refreshMusicPosition: pos=" + position + " dur=" + duration);
        if (duration < 0) {
            duration = 0;
        }
        if (position < 0) {
            position = 0;
        }
        if (duration > 0 && duration >= position) {
            this.mProgressBar.setEnabled(true);
            enableSeekBarThumb(this.mProgressBar, true);
            showProgress();
            if (duration != this.mCurrentDuration) {
                this.mCurrentDuration = duration;
                this.mMusicDuration.setText(formatSeconds(this.mCurrentDuration));
                this.mProgressBar.setMax((int) this.mCurrentDuration);
            }
            if (position != this.mCurrentPosition) {
                this.mCurrentPosition = position;
                this.mMusicPosition.setText(formatSeconds(this.mCurrentPosition));
                this.mProgressBar.setProgress((int) this.mCurrentPosition);
                return;
            }
            return;
        }
        this.mCurrentDuration = 0L;
        this.mCurrentPosition = 0L;
        hideProgress();
        this.mProgressBar.setEnabled(false);
        enableSeekBarThumb(this.mProgressBar, false);
        this.mProgressBar.setMax(0);
        this.mProgressBar.setProgress(0);
        this.mMusicPosition.setText(formatSeconds(this.mCurrentPosition));
        this.mMusicDuration.setText(formatSeconds(this.mCurrentDuration));
    }

    public void refreshControlBtn(int playbackState) {
        logi("refreshControlBtn: state=" + playbackState + " this=" + hashCode());
        this.mPlaybackState = playbackState;
        if (playbackState == 2) {
            this.mControlBtn.setBackground(getDrawable(R.drawable.bg_music_play_btn));
        } else if (playbackState == 0) {
            this.mControlBtn.setBackground(getDrawable(R.drawable.bg_music_pause_btn));
        } else if (playbackState == 1) {
            this.mControlBtn.setBackground(getDrawable(R.drawable.bg_music_play_btn));
        }
    }

    private void hideProgress() {
        this.mProgressBar.setVisibility(8);
        this.mMusicDuration.setVisibility(8);
        this.mMusicPosition.setVisibility(8);
    }

    private void showProgress() {
        this.mProgressBar.setVisibility(0);
        this.mMusicDuration.setVisibility(0);
        this.mMusicPosition.setVisibility(0);
    }

    private static void enableSeekBarThumb(SeekBar seekBar, boolean enabled) {
        Drawable thumb;
        if (seekBar == null || (thumb = seekBar.getThumb()) == null) {
            return;
        }
        if (enabled) {
            thumb.mutate().setAlpha(255);
        } else {
            thumb.mutate().setAlpha(0);
        }
    }

    private static String getString(int id) {
        return ContextUtils.getContext().getString(id);
    }

    private static Drawable getDrawable(int id) {
        return ContextUtils.getContext().getDrawable(id);
    }

    public static String formatSeconds(long timeInSeconds) {
        long minutes;
        if (timeInSeconds > 0) {
            minutes = timeInSeconds / 60;
        } else {
            minutes = 0;
        }
        long secondsLeft = timeInSeconds - (60 * minutes);
        long seconds = secondsLeft > 0 ? secondsLeft : 0L;
        String formattedTime = "";
        if (minutes < 10) {
            formattedTime = "0";
        }
        String formattedTime2 = formattedTime + minutes + NavigationBarInflaterView.KEY_IMAGE_DELIM;
        if (seconds < 10) {
            formattedTime2 = formattedTime2 + "0";
        }
        return formattedTime2 + seconds;
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            this.mMusicPosition.setText(formatSeconds(progress));
        }
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
        this.mCallback.unRegisterMediaControlCallback();
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        int current = this.mProgressBar.getProgress();
        this.mCallback.onMediaSeekTo(current);
        this.mCallback.registerMediaControlCallback();
    }

    public void setSeekBarListener(QuickMenuHolderPresenter callback) {
        this.mCallback = callback;
        this.mCallback.registerMediaControlCallback();
    }

    private void logd(String msg) {
        Log.d(TAG, msg);
    }

    private void logi(String msg) {
        Log.i(TAG, msg);
    }

    private void logv(String msg) {
        Log.v(TAG, msg);
    }
}
