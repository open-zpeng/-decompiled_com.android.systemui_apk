package com.xiaopeng.systemui.infoflow.music;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.manager.MediaManager;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
/* loaded from: classes24.dex */
public class MediaCardView extends AlphaOptimizedRelativeLayout implements View.OnClickListener {
    private static final String TAG = "MediaCardView";
    private final int INVALID_POSITION;
    private ImageView btn_mode;
    private ImageView btn_next;
    private ImageView btn_play;
    private ImageView btn_playlist;
    private ImageView btn_privious;
    private ImageView btn_star;
    private ImageView mAlbum;
    private TextView mArtistName;
    private Context mContext;
    private MediaManager.OnMediaInfoChangedListener mMediaInfoChangedListener;
    private MediaManager mMediaManager;
    private MediaManager.OnPlayPositionChangedListener mPlayPositionChangedListener;
    private MediaManager.OnPlayStatusChangedListener mPlayStatusChangedListener;
    private SeekBar mSeekBar;
    SeekBar.OnSeekBarChangeListener mSeekBarChangeListener;
    private TextView mSongName;

    public MediaCardView(Context context) {
        this(context, null);
    }

    public MediaCardView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MediaCardView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.INVALID_POSITION = -1;
        this.mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() { // from class: com.xiaopeng.systemui.infoflow.music.MediaCardView.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                MediaCardView.this.seekTo(seekBar.getProgress());
            }
        };
        this.mMediaInfoChangedListener = new MediaManager.OnMediaInfoChangedListener() { // from class: com.xiaopeng.systemui.infoflow.music.MediaCardView.2
            @Override // com.xiaopeng.systemui.infoflow.manager.MediaManager.OnMediaInfoChangedListener
            public void onInfoChanged(MediaInfo mediaInfo) {
                Logger.d(MediaCardView.TAG, "onMediaInfoChanged title = " + mediaInfo.getTitle());
                MediaCardView.this.updateMediaInfo(mediaInfo);
            }
        };
        this.mPlayPositionChangedListener = new MediaManager.OnPlayPositionChangedListener() { // from class: com.xiaopeng.systemui.infoflow.music.MediaCardView.3
            @Override // com.xiaopeng.systemui.infoflow.manager.MediaManager.OnPlayPositionChangedListener
            public void onPositionChanged(long position, long duration) {
                Logger.d(MediaCardView.TAG, "onPositionChanged position " + position);
                if (position != -1 && duration != 0) {
                    MediaCardView.this.updatePosition(position, duration);
                }
            }
        };
        this.mPlayStatusChangedListener = new MediaManager.OnPlayStatusChangedListener() { // from class: com.xiaopeng.systemui.infoflow.music.MediaCardView.4
            @Override // com.xiaopeng.systemui.infoflow.manager.MediaManager.OnPlayStatusChangedListener
            public void onStatusChanged(int status) {
                Logger.d(MediaCardView.TAG, "onStatusChanged status = " + status);
                MediaCardView.this.updatePlayStatus(status);
            }
        };
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.mMediaManager = MediaManager.getInstance();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mAlbum = (ImageView) findViewById(R.id.img_ablum);
        this.mSongName = (TextView) findViewById(R.id.tv_song_name);
        this.mArtistName = (TextView) findViewById(R.id.tv_artist);
        this.btn_mode = (ImageView) findViewById(R.id.img_mode);
        this.btn_next = (ImageView) findViewById(R.id.img_next);
        this.btn_privious = (ImageView) findViewById(R.id.img_pre);
        this.btn_play = (ImageView) findViewById(R.id.img_play);
        this.btn_playlist = (ImageView) findViewById(R.id.img_playlist);
        this.btn_star = (ImageView) findViewById(R.id.img_star);
        this.mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        this.btn_mode.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.music.-$$Lambda$1581gVtS4hvy0_6dvKE70u9D6Js
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MediaCardView.this.onClick(view);
            }
        });
        this.btn_next.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.music.-$$Lambda$1581gVtS4hvy0_6dvKE70u9D6Js
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MediaCardView.this.onClick(view);
            }
        });
        this.btn_privious.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.music.-$$Lambda$1581gVtS4hvy0_6dvKE70u9D6Js
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MediaCardView.this.onClick(view);
            }
        });
        this.btn_playlist.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.music.-$$Lambda$1581gVtS4hvy0_6dvKE70u9D6Js
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MediaCardView.this.onClick(view);
            }
        });
        this.btn_star.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.music.-$$Lambda$1581gVtS4hvy0_6dvKE70u9D6Js
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MediaCardView.this.onClick(view);
            }
        });
        this.btn_play.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.music.-$$Lambda$1581gVtS4hvy0_6dvKE70u9D6Js
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MediaCardView.this.onClick(view);
            }
        });
        this.mSeekBar.setOnSeekBarChangeListener(this.mSeekBarChangeListener);
        updateMediaInfo(this.mMediaManager.getCurrentMediaInfo());
        updatePlayStatus(this.mMediaManager.getCurrentPlayStatus());
    }

    private void registerMediaCenter() {
        MediaManager mediaManager = this.mMediaManager;
        if (mediaManager != null) {
            mediaManager.addOnMediaInfoChangedListener(this.mMediaInfoChangedListener);
            this.mMediaManager.addOnPlayPositionChangedListener(this.mPlayPositionChangedListener);
            this.mMediaManager.addOnPlayStatusChangedListener(this.mPlayStatusChangedListener);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_next /* 2131362450 */:
                next();
                return;
            case R.id.img_play /* 2131362454 */:
                playOrPausePlayer();
                return;
            case R.id.img_pre /* 2131362456 */:
                previous();
                return;
            case R.id.img_star /* 2131362466 */:
                favorite();
                return;
            default:
                openMusicApp();
                return;
        }
    }

    private void openMusicApp() {
        PackageHelper.startCarMusic(this.mContext, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMediaInfo(MediaInfo mediaInfo) {
        if (mediaInfo == null) {
            return;
        }
        this.mSongName.setText(mediaInfo.getTitle());
        this.mArtistName.setText(mediaInfo.getArtist());
        Bitmap bitmap = mediaInfo.getAlbumBitmap();
        if (bitmap == null) {
            this.mAlbum.setImageResource(R.mipmap.ic_default_album);
        } else {
            this.mAlbum.setImageBitmap(bitmap);
        }
        if (mediaInfo.isXpMusic()) {
            this.btn_star.setVisibility(0);
            this.mSeekBar.setVisibility(0);
            return;
        }
        this.btn_star.setVisibility(4);
        this.mSeekBar.setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePosition(long position, long duration) {
        int progress = (int) ((100 * position) / duration);
        this.mSeekBar.setProgress(progress);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePlayStatus(int status) {
        if (status == 0) {
            this.btn_play.setImageResource(R.drawable.ic_card_play);
        } else {
            this.btn_play.setImageResource(R.drawable.ic_card_suspend);
        }
    }

    private void next() {
        this.mMediaManager.next();
    }

    private void previous() {
        this.mMediaManager.previous();
    }

    private void playOrPausePlayer() {
        this.mMediaManager.pause();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void seekTo(int position) {
        this.mMediaManager.seekTo(position);
    }

    private void favorite() {
        this.mMediaManager.favorite();
    }
}
