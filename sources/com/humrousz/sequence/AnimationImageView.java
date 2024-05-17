package com.humrousz.sequence;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.support.rastermill.FrescoSequence;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.humrousz.sequence.AnimationSequenceDrawable;
import com.sequence.BaseSequenceFactory;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.module.aiavatar.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes21.dex */
public class AnimationImageView extends AppCompatImageView {
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";
    private static final List<String> SUPPORTED_RESOURCE_TYPE_NAMES = Arrays.asList("raw", "drawable", "mipmap");
    private AnimationSequenceDrawable mAnimatedBgDrawable;
    private AnimationSequenceDrawable mAnimatedSrcDrawable;
    private AnimationSequenceDrawable.OnFinishedListener mDrawableFinishedListener;
    private OnFinishedListener mFinishedListener;
    private int mLoopBehavior;
    private int mLoopCount;
    private BaseSequenceFactory mSequenceFactory;

    /* loaded from: classes21.dex */
    public interface OnFinishedListener {
        void onFinished();
    }

    public AnimationImageView(Context context) {
        super(context);
        this.mLoopCount = 1;
        this.mLoopBehavior = 3;
    }

    public AnimationImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLoopCount = 1;
        this.mLoopBehavior = 3;
        init(context, attrs);
    }

    public AnimationImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLoopCount = 1;
        this.mLoopBehavior = 3;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mDrawableFinishedListener = new AnimationSequenceDrawable.OnFinishedListener() { // from class: com.humrousz.sequence.AnimationImageView.1
            @Override // com.humrousz.sequence.AnimationSequenceDrawable.OnFinishedListener
            public void onFinished(AnimationSequenceDrawable drawable) {
                if (AnimationImageView.this.mFinishedListener != null) {
                    AnimationImageView.this.mFinishedListener.onFinished();
                }
            }
        };
        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AnimationImageView);
            this.mLoopCount = attributes.getInt(R.styleable.AnimationImageView_loopCount, -1);
            if (this.mLoopCount != -1) {
                setLoopFinite();
            } else {
                this.mLoopBehavior = attributes.getInt(R.styleable.AnimationImageView_loopBehavior, 3);
            }
            attributes.getInt(R.styleable.AnimationImageView_srcType, 1);
            this.mSequenceFactory = FrescoSequence.getSequenceFactory();
            attributes.recycle();
            int srcId = attrs.getAttributeResourceValue(ANDROID_NS, ThemeManager.AttributeSet.SRC, 0);
            if (srcId > 0) {
                String srcTypeName = context.getResources().getResourceTypeName(srcId);
                if (SUPPORTED_RESOURCE_TYPE_NAMES.contains(srcTypeName) && !setAnimatedResource(true, srcId)) {
                    super.setImageResource(srcId);
                }
            }
            int bgId = attrs.getAttributeResourceValue(ANDROID_NS, ThemeManager.AttributeSet.BACKGROUND, 0);
            if (bgId > 0) {
                String bgTypeName = context.getResources().getResourceTypeName(bgId);
                if (SUPPORTED_RESOURCE_TYPE_NAMES.contains(bgTypeName) && !setAnimatedResource(false, bgId)) {
                    super.setBackgroundResource(bgId);
                }
            }
        }
    }

    private boolean setAnimatedResource(boolean isSrc, int resId) {
        Resources res = getResources();
        if (res != null) {
            try {
                InputStream inputStream = getInputStreamByResource(res, resId);
                AnimationSequenceDrawable drawable = createDrawable(inputStream);
                if (isSrc) {
                    setImageDrawable(drawable);
                    if (this.mAnimatedSrcDrawable != null) {
                        this.mAnimatedSrcDrawable.destroy();
                    }
                    this.mAnimatedSrcDrawable = drawable;
                    return true;
                } else if (Build.VERSION.SDK_INT >= 16) {
                    setBackground(drawable);
                    if (this.mAnimatedBgDrawable != null) {
                        this.mAnimatedBgDrawable.destroy();
                    }
                    this.mAnimatedBgDrawable = drawable;
                    return true;
                } else {
                    setBackgroundDrawable(drawable);
                    if (this.mAnimatedBgDrawable != null) {
                        this.mAnimatedBgDrawable.destroy();
                    }
                    this.mAnimatedBgDrawable = drawable;
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override // android.support.v7.widget.AppCompatImageView, android.widget.ImageView
    public void setImageURI(Uri uri) {
        if (!setAnimatedImageUri(this, uri)) {
            super.setImageURI(uri);
        }
    }

    @Override // android.support.v7.widget.AppCompatImageView, android.widget.ImageView
    public void setImageResource(int resId) {
        if (!setAnimatedResource(true, resId)) {
            super.setImageResource(resId);
        }
    }

    @Override // android.support.v7.widget.AppCompatImageView, android.view.View
    public void setBackgroundResource(int resId) {
        if (!setAnimatedResource(false, resId)) {
            super.setBackgroundResource(resId);
        }
    }

    public boolean setImageResourceFromAssets(String path) {
        AssetManager am = getContext().getResources().getAssets();
        try {
            InputStream inputStream = am.open(path);
            AnimationSequenceDrawable drawable = createDrawable(inputStream);
            setImageDrawable(drawable);
            if (this.mAnimatedSrcDrawable != null) {
                this.mAnimatedSrcDrawable.destroy();
            }
            this.mAnimatedSrcDrawable = drawable;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean setAnimatedImageUri(ImageView imageView, Uri uri) {
        if (uri != null) {
            InputStream inputStream = null;
            try {
                inputStream = getInputStreamByUri(imageView.getContext(), uri);
                AnimationSequenceDrawable frameSequenceDrawable = createDrawable(inputStream);
                imageView.setImageDrawable(frameSequenceDrawable);
                if (this.mAnimatedSrcDrawable != null) {
                    this.mAnimatedSrcDrawable.destroy();
                }
                this.mAnimatedSrcDrawable = frameSequenceDrawable;
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            } catch (Exception e2) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                        return false;
                    } catch (IOException e3) {
                        e3.printStackTrace();
                        return false;
                    }
                }
                return false;
            } catch (Throwable th) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                    }
                }
                throw th;
            }
        }
        return false;
    }

    private AnimationSequenceDrawable createDrawable(InputStream inputStream) {
        AnimationSequenceDrawable frameSequenceDrawable = new AnimationSequenceDrawable(this.mSequenceFactory.createSequence(inputStream));
        frameSequenceDrawable.setLoopCount(this.mLoopCount);
        frameSequenceDrawable.setLoopBehavior(this.mLoopBehavior);
        frameSequenceDrawable.setOnFinishedListener(this.mDrawableFinishedListener);
        return frameSequenceDrawable;
    }

    private InputStream getInputStreamByResource(Resources resources, int resId) {
        return resources.openRawResource(resId);
    }

    private InputStream getInputStreamByUri(Context context, Uri uri) throws IOException {
        if ("file".equals(uri.getScheme())) {
            return new FileInputStream(new File(uri.getPath()));
        }
        return context.getResources().getAssets().open(uri.getPath());
    }

    public void setLoopCount(int count) {
        this.mLoopCount = count;
        setLoopFinite();
        AnimationSequenceDrawable animationSequenceDrawable = this.mAnimatedBgDrawable;
        if (animationSequenceDrawable != null) {
            animationSequenceDrawable.setLoopCount(this.mLoopCount);
        }
        AnimationSequenceDrawable animationSequenceDrawable2 = this.mAnimatedSrcDrawable;
        if (animationSequenceDrawable2 != null) {
            animationSequenceDrawable2.setLoopCount(this.mLoopCount);
        }
    }

    public void setLoopDefault() {
        this.mLoopBehavior = 3;
    }

    public void setLoopFinite() {
        this.mLoopBehavior = 1;
    }

    public void setLoopInf() {
        this.mLoopBehavior = 2;
    }

    public void stopAnimation() {
        AnimationSequenceDrawable animationSequenceDrawable = this.mAnimatedSrcDrawable;
        if (animationSequenceDrawable != null) {
            animationSequenceDrawable.stop();
        }
    }

    public void setOnFinishedListener(OnFinishedListener listener) {
        this.mFinishedListener = listener;
    }

    public void setSequenceFactory(BaseSequenceFactory factory) {
        if (factory != null) {
            this.mSequenceFactory = factory;
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AnimationSequenceDrawable animationSequenceDrawable = this.mAnimatedBgDrawable;
        if (animationSequenceDrawable != null) {
            animationSequenceDrawable.destroy();
        }
        AnimationSequenceDrawable animationSequenceDrawable2 = this.mAnimatedSrcDrawable;
        if (animationSequenceDrawable2 != null) {
            animationSequenceDrawable2.destroy();
        }
    }
}
