package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.graphics.Bitmap;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.android.systemui.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.xiaopeng.systemui.carmanager.CarClientWrapper;
import com.xiaopeng.systemui.carmanager.controller.IIcmController;
import com.xiaopeng.systemui.controller.OsdController;
import com.xiaopeng.systemui.controller.ThemeController;
import com.xiaopeng.systemui.infoflow.IMusicCardView;
import com.xiaopeng.systemui.infoflow.dao.InfoFlowConfigDao;
import com.xiaopeng.systemui.infoflow.helper.RenderScriptHelper;
import com.xiaopeng.systemui.infoflow.message.Global;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.helper.MusicResourcesHelper;
import com.xiaopeng.systemui.infoflow.message.presenter.MusicCardPresenter;
import com.xiaopeng.systemui.infoflow.theme.AnimatedImageView;
import com.xiaopeng.systemui.infoflow.util.BlurTransformation;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
/* loaded from: classes24.dex */
public class MusicCardHolder extends BaseCardHolder implements ThemeController.OnThemeListener, IMusicCardView {
    public static final String MUSIC_CARD_KEY = "com.xiaopeng.systemui.music_card_entry_key";
    private static final String TAG = MusicCardHolder.class.getSimpleName();
    private final long DELAY_SET_PLACE_ALBUM;
    private final int MSG_BLUR_BITMAP;
    private final int MSG_SET_PLACE_ALBUM;
    private final int MSG_UPDATE_CARD_BACKGROUND;
    private boolean mAlbumBlurEnable;
    private RequestOptions mBlurRequestOptions;
    private AnimatedImageView mBtnCollect;
    private ImageView mCardBg;
    private TextView mContent;
    private Bitmap mCurrentAlbum;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private AnimatedImageView mImgAudition;
    private MediaInfo mMediaInfo;
    private ImageView mMusicIcon;
    private ImageView mNextImage;
    private Handler mNonUIHandler;
    private ImageView mPauseImage;
    private int mPlaceAlbumRes;
    private ProgressBar mPlayProgress;
    private ImageView mPreImage;
    private RequestOptions mRequestOptions;
    private TextView mTitle;
    private TextView mType;
    private ImageView mTypeImage;

    public MusicCardHolder(View itemView) {
        super(itemView);
        this.MSG_SET_PLACE_ALBUM = 67;
        this.DELAY_SET_PLACE_ALBUM = OsdController.TN.DURATION_TIMEOUT_SHORT;
        this.MSG_UPDATE_CARD_BACKGROUND = 68;
        this.MSG_BLUR_BITMAP = 1;
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.MusicCardHolder.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                int i = msg.what;
                if (i == 67) {
                    String str = MusicCardHolder.TAG;
                    Log.i(str, "handle MSG_SET_PLACE_ALBUM : " + MusicCardHolder.this.mPlaceAlbumRes);
                    MusicCardHolder.this.mMusicIcon.setImageResource(MusicCardHolder.this.mPlaceAlbumRes);
                } else if (i == 68) {
                    Bitmap blurAlbum = (Bitmap) msg.obj;
                    if (MusicCardHolder.this.mCardBg != null) {
                        MusicCardHolder.this.mCardBg.setImageBitmap(blurAlbum);
                    }
                } else {
                    super.handleMessage(msg);
                }
            }
        };
        itemView.setTag("music");
        this.mAlbumBlurEnable = InfoFlowConfigDao.getInstance().getConfig().blurAlbum;
        initGlideRequestOption();
        this.mTypeImage = (ImageView) itemView.findViewById(R.id.img_type);
        this.mType = (TextView) itemView.findViewById(R.id.tv_type);
        this.mTitle = (TextView) itemView.findViewById(R.id.tv_title);
        this.mContent = (TextView) itemView.findViewById(R.id.tv_des);
        this.mPreImage = (ImageView) itemView.findViewById(R.id.img_music_action_pre);
        this.mNextImage = (ImageView) itemView.findViewById(R.id.img_music_action_next);
        this.mPauseImage = (ImageView) itemView.findViewById(R.id.img_music_action_pause);
        this.mMusicIcon = (ImageView) itemView.findViewById(R.id.img_music_icon);
        this.mCardBg = (ImageView) itemView.findViewById(R.id.img_background);
        this.mPlayProgress = (ProgressBar) itemView.findViewById(R.id.progressBar_music);
        this.mImgAudition = (AnimatedImageView) itemView.findViewById(R.id.img_audition);
        this.mBtnCollect = (AnimatedImageView) itemView.findViewById(R.id.btn_collect);
        this.mBtnCollect.setOnClickListener(this);
        this.mCardBg.setOutlineProvider(new ViewOutlineProvider() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.MusicCardHolder.2
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 25.0f);
            }
        });
        this.mCardBg.setClipToOutline(true);
        this.mPreImage.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.-$$Lambda$0a0K483pVIHvBbcV6jQq7a1fshs
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MusicCardHolder.this.onClick(view);
            }
        });
        this.mNextImage.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.-$$Lambda$0a0K483pVIHvBbcV6jQq7a1fshs
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MusicCardHolder.this.onClick(view);
            }
        });
        this.mPreImage.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.-$$Lambda$0a0K483pVIHvBbcV6jQq7a1fshs
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MusicCardHolder.this.onClick(view);
            }
        });
        this.mPauseImage.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.-$$Lambda$0a0K483pVIHvBbcV6jQq7a1fshs
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MusicCardHolder.this.onClick(view);
            }
        });
        ThemeController.getInstance(this.mContext).registerThemeListener(this);
        this.mHandlerThread = new HandlerThread("blur-album-bitmap");
        this.mHandlerThread.start();
        this.mNonUIHandler = new Handler(this.mHandlerThread.getLooper()) { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.MusicCardHolder.3
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    MusicCardHolder.this.blurAlbumBitmap((Bitmap) msg.obj);
                }
            }
        };
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public int getCardType() {
        return 9;
    }

    private void initGlideRequestOption() {
        this.mRequestOptions = new RequestOptions().centerCrop().placeholder(R.mipmap.ic_card_music_default).error(R.mipmap.ic_card_music_default);
        new RequestOptions().placeholder(R.mipmap.bg_card_radio);
        this.mBlurRequestOptions = RequestOptions.bitmapTransform(new BlurTransformation(14, 8)).error(R.mipmap.bg_card_radio);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public void bindData(CardEntry cardEntry) {
        super.bindData(cardEntry);
        this.mTitle.setText(this.mData.title);
        this.mContent.setText(this.mData.content);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder, android.view.View.OnClickListener
    public void onClick(View view) {
        MusicCardPresenter musicCardPresenter = (MusicCardPresenter) this.mInfoflowCardPresenter;
        int id = view.getId();
        if (id != R.id.btn_collect) {
            switch (id) {
                case R.id.img_music_action_next /* 2131362444 */:
                    musicCardPresenter.onMusicCardNextClicked();
                    return;
                case R.id.img_music_action_pause /* 2131362445 */:
                    musicCardPresenter.onMusicCardPlayPauseClicked();
                    return;
                case R.id.img_music_action_pre /* 2131362446 */:
                    musicCardPresenter.onMusicCardPrevClicked();
                    return;
                default:
                    musicCardPresenter.onCardClicked();
                    return;
            }
        }
        musicCardPresenter.onMusicCardCollectClicked();
    }

    @Override // com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardMediaInfo(int displayId, MediaInfo mediaInfo) {
        IIcmController icmController;
        MediaInfo mediaInfo2;
        if (mediaInfo != null && (mediaInfo2 = this.mMediaInfo) != null) {
            String lastMediaId = mediaInfo2.getId();
            String curMediaId = mediaInfo.getId();
            if (!TextUtils.isEmpty(lastMediaId) && !lastMediaId.equals(curMediaId)) {
                this.mPlayProgress.setVisibility(8);
            }
        }
        if (mediaInfo != null && !isSameAlbum(mediaInfo, this.mMediaInfo)) {
            updateAlbumImage(mediaInfo);
        }
        this.mMediaInfo = mediaInfo;
        if (mediaInfo == null) {
            return;
        }
        String str = TAG;
        Logger.d(str, "updateMediaInfo title-" + mediaInfo.getTitle() + "&artist-" + mediaInfo.getArtist());
        checkAppOpened();
        if (Global.enjoyMode && (icmController = (IIcmController) CarClientWrapper.getInstance().getController(CarClientWrapper.XP_ICM_SERVICE)) != null) {
            icmController.setIcmInfoCardUpdate(this.mData);
        }
        if (mediaInfo.isXpMusic()) {
            this.mType.setText(MusicResourcesHelper.getTypeStringByMusicSource(mediaInfo.getSource()));
            this.mTypeImage.setImageResource(R.drawable.ic_card_music_type);
        } else {
            this.mType.setText(MusicResourcesHelper.getAppNameByPackage(mediaInfo.getPackageName()));
            this.mTypeImage.setImageDrawable(MusicResourcesHelper.getAppIconByPackage(mediaInfo.getPackageName()));
        }
        this.mTitle.setText(mediaInfo.getTitle());
        this.mContent.setText(mediaInfo.getArtist());
        checkToShowAuditionTag(mediaInfo.isAudition());
    }

    public static boolean isSameAlbum(MediaInfo mediaInfo1, MediaInfo mediaInfo2) {
        if (mediaInfo1 == null && mediaInfo2 == null) {
            return true;
        }
        if (mediaInfo1 == null || mediaInfo2 == null) {
            return false;
        }
        String mediaId1 = mediaInfo1.getId();
        String mediaId2 = mediaInfo2.getId();
        if (mediaId1 == null) {
            mediaId1 = "";
        }
        if (mediaId2 == null) {
            mediaId2 = "";
        }
        if (!mediaId1.equals(mediaId2)) {
            return false;
        }
        Bitmap album1 = mediaInfo1.getAlbumBitmap();
        Bitmap album2 = mediaInfo2.getAlbumBitmap();
        if (album1 != null && album2 != null) {
            return album1.sameAs(album2);
        }
        if (album1 != null || album2 != null) {
            return false;
        }
        String albumUri1 = mediaInfo1.getAlbumUri();
        String albumUri2 = mediaInfo2.getAlbumUri();
        if (albumUri1 == null) {
            albumUri1 = "";
        }
        if (albumUri2 == null) {
            albumUri2 = "";
        }
        return albumUri1.equals(albumUri2);
    }

    private void checkToShowAuditionTag(boolean show) {
        this.mImgAudition.setVisibility(show ? 0 : 8);
    }

    private void updateAlbumImage(MediaInfo mediaInfo) {
        this.mHandler.removeMessages(67);
        Bitmap albumBmp = mediaInfo.getAlbumBitmap();
        String logoUri = mediaInfo.getAlbumUri();
        int source = mediaInfo.getSource();
        if (albumBmp != null) {
            updateAlbumBitmap(albumBmp);
        } else if (!TextUtils.isEmpty(logoUri)) {
            updateAlbumUri(logoUri, source);
        } else {
            updateDefaultAlbum();
        }
    }

    private void updateAlbumBitmap(Bitmap albumBmp) {
        this.mMusicIcon.setImageBitmap(albumBmp);
        this.mCardBg.setImageBitmap(null);
        this.mCurrentAlbum = albumBmp;
        this.mNonUIHandler.removeMessages(1);
        Message msg = this.mNonUIHandler.obtainMessage(1);
        msg.obj = albumBmp;
        this.mNonUIHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void blurAlbumBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        Bitmap blurAlbum = RenderScriptHelper.getInstance(this.mContext).blur(bitmap, 14);
        this.mHandler.removeMessages(68);
        if (this.mCurrentAlbum == bitmap) {
            Message msg = this.mHandler.obtainMessage(68);
            msg.obj = blurAlbum;
            this.mHandler.sendMessage(msg);
        }
    }

    private void updateAlbumUri(String logoUri, int source) {
        String str = TAG;
        Log.i(str, "album uri - " + logoUri + " &source:" + source);
        int defaultAlbum = MusicResourcesHelper.getTypeAlbumByMusicSource(source);
        this.mPlaceAlbumRes = defaultAlbum;
        this.mRequestOptions.error(defaultAlbum);
        Log.i(TAG, "sendEmptyMessageDelayed MSG_SET_PLACE_ALBUM");
        this.mHandler.removeMessages(67);
        this.mHandler.sendEmptyMessageDelayed(67, OsdController.TN.DURATION_TIMEOUT_SHORT);
        Glide.with(this.mMusicIcon.getContext()).load(logoUri).apply(this.mRequestOptions).listener(new RequestListener<Drawable>() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.MusicCardHolder.4
            @Override // com.bumptech.glide.request.RequestListener
            public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                Log.i(MusicCardHolder.TAG, "onLoadFailed");
                MusicCardHolder.this.mHandler.removeMessages(67);
                return false;
            }

            @Override // com.bumptech.glide.request.RequestListener
            public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                Log.i(MusicCardHolder.TAG, "onResourceReady");
                MusicCardHolder.this.mHandler.removeMessages(67);
                return false;
            }
        }).into(this.mMusicIcon);
        if (this.mAlbumBlurEnable) {
            Glide.with(this.mCardBg.getContext()).load(logoUri).apply(this.mBlurRequestOptions).into(this.mCardBg);
        }
    }

    private void updateDefaultAlbum() {
        this.mMusicIcon.setImageResource(R.mipmap.ic_card_music_default);
        this.mCardBg.setImageResource(R.mipmap.bg_card_radio);
    }

    @Override // com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardPlayStatus(int displayId, int status) {
        IIcmController icmController;
        if (Global.enjoyMode && (icmController = (IIcmController) CarClientWrapper.getInstance().getController(CarClientWrapper.XP_ICM_SERVICE)) != null) {
            icmController.setIcmInfoCardUpdate(this.mData);
        }
        if (status == 0) {
            this.mPauseImage.setImageResource(R.drawable.ic_action_pause);
        } else {
            this.mPauseImage.setImageResource(R.drawable.ic_action_play);
        }
    }

    @Override // com.xiaopeng.systemui.controller.ThemeController.OnThemeListener
    public void onThemeChanged(boolean selfChange, Uri uri) {
        MediaInfo mediaInfo = this.mMediaInfo;
        if (mediaInfo != null) {
            checkToShowAuditionTag(mediaInfo.isAudition());
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardProgress(int displayId, int progress) {
        this.mPlayProgress.setProgress(progress);
    }

    @Override // com.xiaopeng.systemui.infoflow.IMusicCardView
    public void showMusicCardProgress(int displayId, boolean show) {
        this.mPlayProgress.setVisibility(show ? 0 : 8);
    }

    @Override // com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardPosition(int displayId, String position, String duration) {
    }
}
