package com.com.badlogic.gdx.graphics.webp;

import android.graphics.Bitmap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.sequence.BaseAnimationSequence;
import com.xiaopeng.module.aiavatar.helper.TextureUtil;
import java.util.List;
/* loaded from: classes21.dex */
public class WebpAnimationTexture {
    private String assetsPath;
    private Bitmap bitmap;
    private TextureLruCache<String, WebpFrame> frameLruCache;
    private int loopCount;
    private long mDuration;
    private BaseAnimationSequence sequence;
    private List<WebpFrame> webpFrameList;
    private WebpTextureManager webpTextureManager;

    /* loaded from: classes21.dex */
    public static class WebpFrame {
        long mDuration;
        long mEndTime;
        long mStartTime;
        Texture mTexture;
        TextureAttribute mTextureAttribute;
    }

    public WebpAnimationTexture(List<WebpFrame> webpFrameList, BaseAnimationSequence sequence, long mDuration, TextureLruCache<String, WebpFrame> textureLruCache, String assetsPath, int loopCount, WebpTextureManager webpTextureManager) {
        this.webpFrameList = webpFrameList;
        this.mDuration = mDuration;
        this.sequence = sequence;
        this.bitmap = Bitmap.createBitmap(sequence.getWidth(), sequence.getHeight(), Bitmap.Config.ARGB_8888);
        this.frameLruCache = textureLruCache;
        this.assetsPath = assetsPath;
        this.loopCount = loopCount;
        this.webpTextureManager = webpTextureManager;
    }

    public TextureAttribute getCurrentFrame(long mStartTime) {
        int i;
        int i2 = this.loopCount;
        if (i2 > 0 || i2 == -1) {
            long currentTime = (System.currentTimeMillis() - mStartTime) % this.mDuration;
            List<WebpFrame> list = this.webpFrameList;
            if (list != null && list.size() > 0) {
                int size = this.webpFrameList.size();
                long lastFrameEndTime = 0;
                long duration = 0;
                for (int i3 = 0; i3 < size; i3++) {
                    WebpFrame frame = this.webpFrameList.get(i3);
                    if (currentTime < frame.mEndTime) {
                        if (frame.mTextureAttribute == null || !TextureUtil.isTexture(frame.mTexture)) {
                            if (frame.mTexture != null) {
                                frame.mTexture.dispose();
                                frame.mTexture = null;
                                frame.mTextureAttribute = null;
                            }
                            initTexture(i3, frame, lastFrameEndTime);
                        }
                        if (i3 == size - 1 && (i = this.loopCount) > 0) {
                            this.loopCount = i - 1;
                            if (this.loopCount == 0) {
                                this.webpTextureManager.onWebpEnd(this.assetsPath);
                            }
                        }
                        return frame.mTextureAttribute;
                    } else if (i3 == size - 1) {
                        if (frame.mTextureAttribute == null) {
                            initTexture(i3, frame, lastFrameEndTime);
                        }
                        int i4 = this.loopCount;
                        if (i4 > 0) {
                            this.loopCount = i4 - 1;
                            if (this.loopCount == 0) {
                                this.webpTextureManager.onWebpEnd(this.assetsPath);
                            }
                        }
                        return frame.mTextureAttribute;
                    } else {
                        long j = frame.mEndTime;
                        lastFrameEndTime = j;
                        duration = j;
                    }
                }
                this.mDuration = duration;
            }
        }
        List<WebpFrame> list2 = this.webpFrameList;
        if (list2 == null || list2.size() <= 0) {
            return null;
        }
        int index = this.webpFrameList.size() - 1;
        WebpFrame frame2 = this.webpFrameList.get(index);
        if (frame2.mTextureAttribute == null || !TextureUtil.isTexture(frame2.mTexture)) {
            if (frame2.mTexture != null) {
                frame2.mTexture.dispose();
                frame2.mTexture = null;
                frame2.mTextureAttribute = null;
            }
            initTexture(index, frame2, index * frame2.mDuration);
        }
        return frame2.mTextureAttribute;
    }

    private void initTexture(int index, WebpFrame webpFrame, long lastFrameEndTime) {
        webpFrame.mDuration = this.sequence.getFrame(index, this.bitmap, index - 1);
        webpFrame.mStartTime = lastFrameEndTime;
        webpFrame.mEndTime = webpFrame.mStartTime + webpFrame.mDuration;
        webpFrame.mTexture = new Texture(this.bitmap.getWidth(), this.bitmap.getHeight(), Pixmap.Format.RGBA8888);
        webpFrame.mTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        webpFrame.mTextureAttribute = TextureAttribute.createDiffuse(webpFrame.mTexture);
        TextureUtil.bindTexture(webpFrame.mTexture, this.bitmap);
        String key = this.assetsPath + "-" + index;
        WebpFrame frame = this.frameLruCache.get(key);
        if (frame == null) {
            this.frameLruCache.put(key, webpFrame);
        }
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public BaseAnimationSequence getSequence() {
        return this.sequence;
    }

    public void release() {
        List<WebpFrame> list = this.webpFrameList;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < this.webpFrameList.size(); i++) {
                WebpFrame webpFrame = this.webpFrameList.get(i);
                if (webpFrame.mTexture != null) {
                    webpFrame.mTexture.dispose();
                    webpFrame.mTextureAttribute = null;
                    webpFrame.mTexture = null;
                }
            }
        }
    }
}
