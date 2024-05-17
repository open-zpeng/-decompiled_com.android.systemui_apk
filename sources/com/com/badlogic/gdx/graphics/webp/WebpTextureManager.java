package com.com.badlogic.gdx.graphics.webp;

import android.content.Context;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.support.rastermill.FrescoSequence;
import android.util.Log;
import com.android.internal.app.IntentForwarderActivity;
import com.badlogic.gdx.backends.android.RemoteContext;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.com.badlogic.gdx.graphics.webp.WebpAnimationLruCache;
import com.com.badlogic.gdx.graphics.webp.WebpAnimationTexture;
import com.google.gson.Gson;
import com.sequence.BaseAnimationSequence;
import com.sequence.BaseSequenceFactory;
import com.xiaopeng.lib.apirouter.ApiRouter;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.module.aiavatar.mvp.avatar.bean.AvatarPlayStatus;
import com.xiaopeng.module.aiavatar.player.Avatar3dPlayer;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
/* loaded from: classes21.dex */
public class WebpTextureManager {
    public static final int TYPE_GLASSES = 1;
    public static final int TYPE_LEFT = 3;
    public static final int TYPE_LEFT_TOP = 2;
    public static final int TYPE_RIGHT = 4;
    private static final Object mLock = new Object();
    private String mBizName;
    private boolean mCallbackOnWebpEnd;
    private Context mContext;
    private WebpAnimationTexture mDayRunningTexture;
    private Handler mDecodeThreadHandler;
    private TextureLruCache<String, WebpAnimationTexture.WebpFrame> mFrameLruCache;
    private boolean mIsActive;
    private WebpAnimationTexture mNightRunningTexture;
    private int mType;
    private WebpAnimationLruCache<String, WebpAnimationTexture> mWebpLruCache;
    private long mDayStartTime = -1;
    private long mNightStartTime = -1;

    public WebpTextureManager(int type, Context mContext, String bizName, int webpCacheNum, int frameCacheNum) {
        this.mType = type;
        this.mBizName = bizName;
        HandlerThread decodeThread = new HandlerThread("AnimSequence decoding thread:" + this.mBizName, 0);
        decodeThread.start();
        this.mDecodeThreadHandler = new Handler(decodeThread.getLooper());
        this.mWebpLruCache = new WebpAnimationLruCache<>(webpCacheNum);
        this.mFrameLruCache = new TextureLruCache<>(frameCacheNum);
        this.mContext = mContext;
    }

    public void decodeWebp(String assetsPath, int loopCount) {
        long mDuration;
        Context remoteContext = RemoteContext.getRemoteContext(this.mContext);
        BaseAnimationSequence sequence = null;
        BaseSequenceFactory sequenceFactory = FrescoSequence.getSequenceFactory();
        InputStream inputStream = null;
        try {
            try {
                inputStream = remoteContext.getAssets().open(assetsPath);
                sequence = sequenceFactory.createSequence(inputStream);
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
            }
            BaseAnimationSequence sequence2 = sequence;
            if (sequence2 != null) {
                ArrayList<WebpAnimationTexture.WebpFrame> frameList = new ArrayList<>();
                try {
                    int totalCount = sequence2.getFrameCount();
                    mDuration = 0;
                    for (int i = 0; i < totalCount; i++) {
                        try {
                            WebpAnimationTexture.WebpFrame webpFrame = new WebpAnimationTexture.WebpFrame();
                            webpFrame.mDuration = sequence2.getDefaultFrameDuration();
                            webpFrame.mStartTime = mDuration;
                            mDuration += webpFrame.mDuration;
                            webpFrame.mEndTime = mDuration;
                            frameList.add(webpFrame);
                        } catch (Exception e4) {
                            e = e4;
                            Log.e("laishuai", "decode error", e);
                        }
                    }
                } catch (Exception e5) {
                    e = e5;
                }
                try {
                    WebpAnimationTexture animationTexture = new WebpAnimationTexture(frameList, sequence2, mDuration, this.mFrameLruCache, assetsPath, loopCount, this);
                    this.mWebpLruCache.put(assetsPath, animationTexture);
                    String webpType = ".";
                    int i2 = this.mType;
                    if (i2 == 1) {
                        webpType = ".";
                    } else if (i2 == 2) {
                        webpType = Avatar3dPlayer.WEBP_NIGHT_LEFT_TOP;
                    } else if (i2 == 3) {
                        webpType = ".";
                    } else if (i2 == 4) {
                        webpType = ".";
                    }
                    if (webpType.equals(".")) {
                        updateNightRunningTexture(animationTexture);
                        updateDayRunningTexture(animationTexture);
                    } else {
                        if (assetsPath.endsWith(webpType + Avatar3dPlayer.FILE_TYPE_WEBP)) {
                            updateNightRunningTexture(animationTexture);
                        } else {
                            updateDayRunningTexture(animationTexture);
                        }
                    }
                } catch (Exception e6) {
                    e = e6;
                    Log.e("laishuai", "decode error", e);
                }
            }
        } catch (Throwable e7) {
            InputStream inputStream2 = inputStream;
            if (inputStream2 != null) {
                try {
                    inputStream2.close();
                } catch (IOException e8) {
                    e8.printStackTrace();
                }
            }
            throw e7;
        }
    }

    public void playAnimation(final String dayAssetsPath, final String nightAssetsPath, final int loopCount) {
        this.mDecodeThreadHandler.post(new Runnable() { // from class: com.com.badlogic.gdx.graphics.webp.WebpTextureManager.1
            @Override // java.lang.Runnable
            public void run() {
                WebpAnimationTexture dayTexture = (WebpAnimationTexture) WebpTextureManager.this.mWebpLruCache.get(dayAssetsPath);
                WebpAnimationTexture nightTexture = (WebpAnimationTexture) WebpTextureManager.this.mWebpLruCache.get(nightAssetsPath);
                if (dayTexture == null) {
                    WebpTextureManager.this.decodeWebp(dayAssetsPath, loopCount);
                } else {
                    dayTexture.setLoopCount(loopCount);
                    WebpTextureManager.this.updateDayRunningTexture(dayTexture);
                }
                if (nightTexture == null) {
                    WebpTextureManager.this.decodeWebp(nightAssetsPath, loopCount);
                    return;
                }
                nightTexture.setLoopCount(loopCount);
                WebpTextureManager.this.updateNightRunningTexture(nightTexture);
            }
        });
    }

    public void updateDayRunningTexture(WebpAnimationTexture texture) {
        synchronized (mLock) {
            this.mDayRunningTexture = texture;
            this.mDayStartTime = System.currentTimeMillis();
        }
    }

    public void updateNightRunningTexture(WebpAnimationTexture texture) {
        synchronized (mLock) {
            this.mNightRunningTexture = texture;
            this.mNightStartTime = System.currentTimeMillis();
        }
    }

    public TextureAttribute getCurrentFrame() {
        WebpAnimationTexture runningTexture;
        long startTime;
        synchronized (mLock) {
            if (ThemeManager.isNightMode(this.mContext)) {
                runningTexture = this.mNightRunningTexture;
                startTime = this.mNightStartTime;
            } else {
                runningTexture = this.mDayRunningTexture;
                startTime = this.mDayStartTime;
            }
            if (runningTexture == null) {
                return null;
            }
            return runningTexture.getCurrentFrame(startTime);
        }
    }

    public boolean canRun() {
        synchronized (mLock) {
            if (this.mIsActive) {
                if (ThemeManager.isNightMode(this.mContext)) {
                    return this.mNightRunningTexture != null;
                }
                return this.mDayRunningTexture != null;
            }
            return false;
        }
    }

    public void setGlSurfaceView(GLSurfaceView glSurfaceView) {
        this.mFrameLruCache.setGlSurfaceView(glSurfaceView);
    }

    public void initDefaultWebpAnimation() {
        playAnimation("avatar/idle/default.webp", "avatar/idle/default_night.webp", 1);
    }

    public void setCallbackOnWebpEnd(boolean callbackOnWebpEnd) {
        this.mCallbackOnWebpEnd = callbackOnWebpEnd;
    }

    public void onWebpEnd(String assetsPath) {
        if (!this.mCallbackOnWebpEnd) {
            return;
        }
        AvatarPlayStatus playStatus = new AvatarPlayStatus();
        playStatus.setStatus(2);
        Uri.Builder builder = new Uri.Builder();
        builder.authority("com.xiaopeng.aiavatarservice.APIRouterHelper").path("onWebpStateChange").appendQueryParameter("param", new Gson().toJson(playStatus));
        try {
            ApiRouter.route(builder.build());
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(IntentForwarderActivity.TAG, "onWebpEnd error for ApiRouter Exception.");
        }
    }

    public boolean isWebpLoaded(String resPath) {
        WebpAnimationTexture texture = this.mWebpLruCache.get(resPath);
        return texture != null;
    }

    public void setActive(boolean active) {
        this.mIsActive = active;
    }

    public void releseTexture() {
        WebpAnimationTexture webpAnimationTexture = this.mNightRunningTexture;
        if (webpAnimationTexture != null) {
            webpAnimationTexture.release();
        }
        WebpAnimationTexture webpAnimationTexture2 = this.mDayRunningTexture;
        if (webpAnimationTexture2 != null) {
            webpAnimationTexture2.release();
        }
    }

    public void setEntryRemovedListener(WebpAnimationLruCache.OnEntryRemovedListener listener) {
        WebpAnimationLruCache<String, WebpAnimationTexture> webpAnimationLruCache = this.mWebpLruCache;
        if (webpAnimationLruCache != null) {
            webpAnimationLruCache.setEntryRemovedListener(listener);
        }
    }
}
