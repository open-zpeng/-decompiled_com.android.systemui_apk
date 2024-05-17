package com.xiaopeng.xui.drawable.progress;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.xiaopeng.xpui.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes25.dex */
public class XCircularProgressBgDrawable extends Drawable {
    private static final float STROKE_WIDTH_DEFAULT = 0.0f;
    private Drawable mIndicatorPause;
    private Drawable mIndicatorPlay;
    private Drawable mIndicatorStart;
    private Drawable mIndicatorStop;
    private float mInset;
    private int mProgressBgColor;
    private float mStrokeWidth;
    private Rect mTmpIndicatorBounds = new Rect();
    private Paint mPaint = new Paint();
    private Rect mOutBounds = getBounds();
    private RectF mInnerBounds = new RectF(getBounds());
    private boolean mShowIndicator = true;
    private boolean mPlayingStop = false;
    private boolean mPlayingPause = false;
    private boolean mPaused = false;
    private boolean mStartDownload = false;

    public XCircularProgressBgDrawable() {
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setColor(this.mProgressBgColor);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs, @Nullable Resources.Theme theme) throws IOException, XmlPullParserException {
        super.inflate(r, parser, attrs, theme);
        updateAttr(r, attrs, theme);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs) throws IOException, XmlPullParserException {
        super.inflate(r, parser, attrs);
        updateAttr(r, attrs, null);
    }

    private void updateAttr(Resources resources, AttributeSet attrs, Resources.Theme theme) {
        TypedArray ta;
        if (theme != null) {
            ta = theme.obtainStyledAttributes(attrs, R.styleable.XCircularProgressBgDrawable, 0, 0);
        } else {
            ta = resources.obtainAttributes(attrs, R.styleable.XCircularProgressBgDrawable);
        }
        this.mProgressBgColor = ta.getColor(R.styleable.XCircularProgressBgDrawable_progress_backgroundColor, resources.getColor(R.color.x_circular_progress_circle_bg_color, theme));
        this.mPaint.setColor(this.mProgressBgColor);
        this.mStrokeWidth = ta.getDimension(R.styleable.XCircularProgressBgDrawable_progress_background_strokeWidth, 0.0f);
        this.mInset = ta.getDimensionPixelSize(R.styleable.XCircularProgressBgDrawable_progress_background_inset, 0);
        this.mIndicatorPlay = ta.getDrawable(R.styleable.XCircularProgressBgDrawable_progress_bg_indicator_play);
        this.mIndicatorPause = ta.getDrawable(R.styleable.XCircularProgressBgDrawable_progress_bg_indicator_pause);
        this.mIndicatorStop = ta.getDrawable(R.styleable.XCircularProgressBgDrawable_progress_bg_indicator_stop);
        this.mIndicatorStart = ta.getDrawable(R.styleable.XCircularProgressBgDrawable_progress_bg_indicator_start);
        ta.recycle();
    }

    public void setStrokeWidth(float strokeWidth) {
        this.mStrokeWidth = strokeWidth;
    }

    public void setInset(float inset) {
        this.mInset = inset;
    }

    public void setIndicatorPlay(Drawable indicatorPlay) {
        this.mIndicatorPlay = indicatorPlay;
    }

    public void setIndicatorStop(Drawable indicatorStop) {
        this.mIndicatorStop = indicatorStop;
    }

    public void setIndicatorPause(Drawable indicatorPause) {
        this.mIndicatorPause = indicatorPause;
    }

    public void setIndicatorStart(Drawable indicatorStart) {
        this.mIndicatorStart = indicatorStart;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(@NonNull Canvas canvas) {
        Drawable drawable;
        int count = canvas.save();
        canvas.drawArc(this.mInnerBounds, 0.0f, 360.0f, false, this.mPaint);
        canvas.restoreToCount(count);
        if (this.mShowIndicator) {
            if (this.mPlayingStop) {
                Drawable drawable2 = this.mIndicatorStop;
                if (drawable2 != null) {
                    drawable2.draw(canvas);
                }
            } else if (this.mPlayingPause) {
                Drawable drawable3 = this.mIndicatorPause;
                if (drawable3 != null) {
                    drawable3.draw(canvas);
                }
            } else if (this.mPaused) {
                Drawable drawable4 = this.mIndicatorPlay;
                if (drawable4 != null) {
                    drawable4.draw(canvas);
                }
            } else if (this.mStartDownload && (drawable = this.mIndicatorStart) != null) {
                drawable.draw(canvas);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(@NonNull Rect bounds) {
        super.onBoundsChange(bounds);
        applyIndicatorBounds(this.mIndicatorPlay, this.mTmpIndicatorBounds);
        applyIndicatorBounds(this.mIndicatorPause, this.mTmpIndicatorBounds);
        applyIndicatorBounds(this.mIndicatorStop, this.mTmpIndicatorBounds);
        applyIndicatorBounds(this.mIndicatorStart, this.mTmpIndicatorBounds);
        if (this.mStrokeWidth == 0.0f) {
            this.mStrokeWidth = (this.mOutBounds.width() * 1.0f) / 10.0f;
        }
        float borderInset = this.mStrokeWidth / 2.0f;
        this.mInnerBounds.set(bounds);
        RectF rectF = this.mInnerBounds;
        float f = this.mInset;
        rectF.inset(f + borderInset, f + borderInset);
        this.mPaint.setStrokeWidth(this.mStrokeWidth);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        Drawable drawable = this.mIndicatorPause;
        if (drawable != null) {
            drawable.setState(state);
        }
        Drawable drawable2 = this.mIndicatorPlay;
        if (drawable2 != null) {
            drawable2.setState(state);
        }
        Drawable drawable3 = this.mIndicatorStart;
        if (drawable3 != null) {
            drawable3.setState(state);
        }
        Drawable drawable4 = this.mIndicatorStop;
        if (drawable4 != null) {
            drawable4.setState(state);
        }
        boolean playingStop = false;
        boolean playingPause = false;
        boolean paused = false;
        int length = state.length;
        boolean z = false;
        boolean startDownload = false;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            int s = state[i];
            if (s == R.attr.progress_state_playing_stop) {
                playingStop = true;
                break;
            } else if (s == R.attr.progress_state_playing_pause) {
                playingPause = true;
                break;
            } else if (s == R.attr.progress_state_paused) {
                paused = true;
                break;
            } else {
                if (s == R.attr.progress_state_start_download) {
                    startDownload = true;
                }
                i++;
            }
        }
        boolean changed = false;
        if (playingStop != this.mPlayingStop) {
            this.mPlayingStop = playingStop;
            changed = true;
        }
        if (playingPause != this.mPlayingPause) {
            this.mPlayingPause = playingPause;
            changed = true;
        }
        if (paused != this.mPaused) {
            this.mPaused = paused;
            changed = true;
        }
        if (startDownload != this.mStartDownload) {
            this.mStartDownload = startDownload;
            changed = true;
        }
        if (changed) {
            if (this.mPlayingStop || this.mPlayingPause || this.mPaused || this.mStartDownload) {
                z = true;
            }
            this.mShowIndicator = z;
            return true;
        }
        return super.onStateChange(state);
    }

    private void applyIndicatorBounds(Drawable drawable, Rect outBounds) {
        if (drawable == null || outBounds == null) {
            return;
        }
        Rect bounds = getBounds();
        float totalWidth = bounds.width();
        float totalHeight = bounds.height();
        float width = drawable.getIntrinsicWidth();
        float height = drawable.getIntrinsicHeight();
        if (width >= totalWidth || height >= totalHeight) {
            outBounds.set(bounds);
            drawable.setBounds(outBounds);
            return;
        }
        int left = Math.round(bounds.left + ((totalWidth - width) / 2.0f));
        int top = Math.round(bounds.top + ((totalHeight - height) / 2.0f));
        int right = Math.round(width) + left;
        int bottom = Math.round(height) + top;
        outBounds.set(left, top, right, bottom);
        drawable.setBounds(outBounds);
    }
}
