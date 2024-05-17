package com.android.systemui.pip.tv;

import android.app.PendingIntent;
import android.app.RemoteAction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.systemui.R;
import com.android.systemui.pip.tv.PipManager;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes21.dex */
public class PipControlsView extends LinearLayout {
    private static final float DISABLED_ACTION_ALPHA = 0.54f;
    private static final String TAG = PipControlsView.class.getSimpleName();
    private PipControlButtonView mCloseButtonView;
    private List<RemoteAction> mCustomActions;
    private ArrayList<PipControlButtonView> mCustomButtonViews;
    private final View.OnFocusChangeListener mFocusChangeListener;
    private PipControlButtonView mFocusedChild;
    private PipControlButtonView mFullButtonView;
    private final Handler mHandler;
    private final LayoutInflater mLayoutInflater;
    private Listener mListener;
    private MediaController mMediaController;
    private MediaController.Callback mMediaControllerCallback;
    private final PipManager mPipManager;
    private final PipManager.MediaListener mPipMediaListener;
    private PipControlButtonView mPlayPauseButtonView;

    /* loaded from: classes21.dex */
    public interface Listener {
        void onClosed();
    }

    public PipControlsView(Context context) {
        this(context, null, 0, 0);
    }

    public PipControlsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public PipControlsView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PipControlsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mPipManager = PipManager.getInstance();
        this.mCustomButtonViews = new ArrayList<>();
        this.mCustomActions = new ArrayList();
        this.mMediaControllerCallback = new MediaController.Callback() { // from class: com.android.systemui.pip.tv.PipControlsView.1
            @Override // android.media.session.MediaController.Callback
            public void onPlaybackStateChanged(PlaybackState state) {
                PipControlsView.this.updateUserActions();
            }
        };
        this.mPipMediaListener = new PipManager.MediaListener() { // from class: com.android.systemui.pip.tv.PipControlsView.2
            @Override // com.android.systemui.pip.tv.PipManager.MediaListener
            public void onMediaControllerChanged() {
                PipControlsView.this.updateMediaController();
            }
        };
        this.mFocusChangeListener = new View.OnFocusChangeListener() { // from class: com.android.systemui.pip.tv.PipControlsView.3
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    PipControlsView.this.mFocusedChild = (PipControlButtonView) view;
                } else if (PipControlsView.this.mFocusedChild == view) {
                    PipControlsView.this.mFocusedChild = null;
                }
            }
        };
        this.mLayoutInflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
        this.mLayoutInflater.inflate(R.layout.tv_pip_controls, this);
        this.mHandler = new Handler();
        setOrientation(0);
        setGravity(49);
    }

    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mFullButtonView = (PipControlButtonView) findViewById(R.id.full_button);
        this.mFullButtonView.setOnFocusChangeListener(this.mFocusChangeListener);
        this.mFullButtonView.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.pip.tv.PipControlsView.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                PipControlsView.this.mPipManager.movePipToFullscreen();
            }
        });
        this.mCloseButtonView = (PipControlButtonView) findViewById(R.id.close_button);
        this.mCloseButtonView.setOnFocusChangeListener(this.mFocusChangeListener);
        this.mCloseButtonView.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.pip.tv.PipControlsView.5
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                PipControlsView.this.mPipManager.closePip();
                if (PipControlsView.this.mListener != null) {
                    PipControlsView.this.mListener.onClosed();
                }
            }
        });
        this.mPlayPauseButtonView = (PipControlButtonView) findViewById(R.id.play_pause_button);
        this.mPlayPauseButtonView.setOnFocusChangeListener(this.mFocusChangeListener);
        this.mPlayPauseButtonView.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.pip.tv.PipControlsView.6
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (PipControlsView.this.mMediaController != null && PipControlsView.this.mMediaController.getPlaybackState() != null) {
                    PipControlsView.this.mMediaController.getPlaybackState().getActions();
                    PipControlsView.this.mMediaController.getPlaybackState().getState();
                    if (PipControlsView.this.mPipManager.getPlaybackState() == 1) {
                        PipControlsView.this.mMediaController.getTransportControls().play();
                    } else if (PipControlsView.this.mPipManager.getPlaybackState() == 0) {
                        PipControlsView.this.mMediaController.getTransportControls().pause();
                    }
                }
            }
        });
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateMediaController();
        this.mPipManager.addMediaListener(this.mPipMediaListener);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mPipManager.removeMediaListener(this.mPipMediaListener);
        MediaController mediaController = this.mMediaController;
        if (mediaController != null) {
            mediaController.unregisterCallback(this.mMediaControllerCallback);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMediaController() {
        MediaController newController = this.mPipManager.getMediaController();
        MediaController mediaController = this.mMediaController;
        if (mediaController == newController) {
            return;
        }
        if (mediaController != null) {
            mediaController.unregisterCallback(this.mMediaControllerCallback);
        }
        this.mMediaController = newController;
        MediaController mediaController2 = this.mMediaController;
        if (mediaController2 != null) {
            mediaController2.registerCallback(this.mMediaControllerCallback);
        }
        updateUserActions();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUserActions() {
        int i;
        if (!this.mCustomActions.isEmpty()) {
            while (this.mCustomButtonViews.size() < this.mCustomActions.size()) {
                PipControlButtonView buttonView = (PipControlButtonView) this.mLayoutInflater.inflate(R.layout.tv_pip_custom_control, (ViewGroup) this, false);
                addView(buttonView);
                this.mCustomButtonViews.add(buttonView);
            }
            for (int i2 = 0; i2 < this.mCustomButtonViews.size(); i2++) {
                PipControlButtonView pipControlButtonView = this.mCustomButtonViews.get(i2);
                if (i2 < this.mCustomActions.size()) {
                    i = 0;
                } else {
                    i = 8;
                }
                pipControlButtonView.setVisibility(i);
            }
            for (int i3 = 0; i3 < this.mCustomActions.size(); i3++) {
                final RemoteAction action = this.mCustomActions.get(i3);
                final PipControlButtonView actionView = this.mCustomButtonViews.get(i3);
                action.getIcon().loadDrawableAsync(getContext(), new Icon.OnDrawableLoadedListener() { // from class: com.android.systemui.pip.tv.-$$Lambda$PipControlsView$ZwQyQkGsN0bsRufZ6MVGwaQtJA8
                    @Override // android.graphics.drawable.Icon.OnDrawableLoadedListener
                    public final void onDrawableLoaded(Drawable drawable) {
                        PipControlsView.lambda$updateUserActions$0(PipControlButtonView.this, drawable);
                    }
                }, this.mHandler);
                actionView.setText(action.getContentDescription());
                if (action.isEnabled()) {
                    actionView.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.pip.tv.-$$Lambda$PipControlsView$HMvSX-xIxW1kpM7rGrVPgysk-xY
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            PipControlsView.lambda$updateUserActions$1(action, view);
                        }
                    });
                }
                actionView.setEnabled(action.isEnabled());
                actionView.setAlpha(action.isEnabled() ? 1.0f : DISABLED_ACTION_ALPHA);
            }
            this.mPlayPauseButtonView.setVisibility(8);
            return;
        }
        int state = this.mPipManager.getPlaybackState();
        if (state == 2) {
            this.mPlayPauseButtonView.setVisibility(8);
        } else {
            this.mPlayPauseButtonView.setVisibility(0);
            if (state == 0) {
                this.mPlayPauseButtonView.setImageResource(R.drawable.ic_pause_white);
                this.mPlayPauseButtonView.setText(R.string.pip_pause);
            } else {
                this.mPlayPauseButtonView.setImageResource(R.drawable.ic_play_arrow_white);
                this.mPlayPauseButtonView.setText(R.string.pip_play);
            }
        }
        for (int i4 = 0; i4 < this.mCustomButtonViews.size(); i4++) {
            this.mCustomButtonViews.get(i4).setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$updateUserActions$0(PipControlButtonView actionView, Drawable d) {
        d.setTint(-1);
        actionView.setImageDrawable(d);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$updateUserActions$1(RemoteAction action, View v) {
        try {
            action.getActionIntent().send();
        } catch (PendingIntent.CanceledException e) {
            Log.w(TAG, "Failed to send action", e);
        }
    }

    public void reset() {
        this.mFullButtonView.reset();
        this.mCloseButtonView.reset();
        this.mPlayPauseButtonView.reset();
        this.mFullButtonView.requestFocus();
        for (int i = 0; i < this.mCustomButtonViews.size(); i++) {
            this.mCustomButtonViews.get(i).reset();
        }
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void setActions(List<RemoteAction> actions) {
        this.mCustomActions.clear();
        this.mCustomActions.addAll(actions);
        updateUserActions();
    }

    PipControlButtonView getFocusedButton() {
        return this.mFocusedChild;
    }
}
