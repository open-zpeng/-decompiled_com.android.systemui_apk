package com.xiaopeng.xui.widget.quicksidebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.sound.XSoundEffectManager;
import com.xiaopeng.xui.view.XView;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.view.font.XFontScaleHelper;
import com.xiaopeng.xui.widget.quicksidebar.listener.OnQuickSideBarTouchListener;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes25.dex */
public class XQuickSideBarView extends XView implements XViewDelegate.onFontScaleChangeCallback {
    private OnQuickSideBarTouchListener listener;
    private boolean mAlwaysHighlight;
    private int mChoose;
    private int mHeight;
    private boolean mIsTouchMove;
    private float mItemHeight;
    private final float mItemStartY;
    private float mItemWidth;
    private List<String> mLetters;
    private Paint mPaint;
    private int mTextColor;
    private int mTextColorChoose;
    private float mTextSize;
    private float mTextSizeChoose;
    private int mWidth;

    public XQuickSideBarView(Context context) {
        this(context, null);
    }

    public XQuickSideBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XQuickSideBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mChoose = -1;
        this.mPaint = new Paint();
        this.mItemStartY = 14.0f;
        this.mIsTouchMove = false;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mLetters = Arrays.asList(context.getResources().getStringArray(R.array.x_quick_side_bar_letters));
        initDefaultColor();
        this.mTextSize = context.getResources().getDimensionPixelSize(R.dimen.x_sidebar_textsize);
        this.mTextSizeChoose = context.getResources().getDimensionPixelSize(R.dimen.x_sidebar_choose_textsize);
        this.mItemHeight = context.getResources().getDimension(R.dimen.x_sidebar_item_height);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.XQuickSideBarView);
            this.mTextColor = a.getColor(R.styleable.XQuickSideBarView_sidebarTextColor, this.mTextColor);
            this.mTextColorChoose = a.getColor(R.styleable.XQuickSideBarView_sidebarTextColorChoose, this.mTextColorChoose);
            this.mTextSize = a.getDimension(R.styleable.XQuickSideBarView_sidebarTextSize, this.mTextSize);
            this.mTextSizeChoose = a.getDimension(R.styleable.XQuickSideBarView_sidebarTextSizeChoose, this.mTextSizeChoose);
            this.mAlwaysHighlight = a.getBoolean(R.styleable.XQuickSideBarView_sidebarHighlight, false);
            final XFontScaleHelper xFontScaleHelper = XFontScaleHelper.create(a, R.styleable.XQuickSideBarView_sidebarTextSize, R.dimen.x_sidebar_textsize);
            final XFontScaleHelper xFontScaleHelperChoose = XFontScaleHelper.create(a, R.styleable.XQuickSideBarView_sidebarTextSizeChoose, R.dimen.x_sidebar_choose_textsize);
            if (this.mXViewDelegate != null) {
                this.mXViewDelegate.setFontScaleChangeCallback(new XViewDelegate.onFontScaleChangeCallback() { // from class: com.xiaopeng.xui.widget.quicksidebar.-$$Lambda$XQuickSideBarView$1ZB34fs1FOEgdHSSURf-ptiofIs
                    @Override // com.xiaopeng.xui.view.XViewDelegate.onFontScaleChangeCallback
                    public final void onFontScaleChanged() {
                        XQuickSideBarView.this.lambda$init$0$XQuickSideBarView(xFontScaleHelper, xFontScaleHelperChoose);
                    }
                });
            }
            this.mItemHeight = a.getDimension(R.styleable.XQuickSideBarView_sidebarItemHeight, this.mItemHeight);
            a.recycle();
        }
        if (this.mXViewDelegate != null && this.mXViewDelegate.getThemeViewModel() != null) {
            this.mXViewDelegate.getThemeViewModel().setCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.widget.quicksidebar.-$$Lambda$XQuickSideBarView$wKv8A9QBMc1F3uP8mqZVN01Nvxo
                @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
                public final void onThemeChanged() {
                    XQuickSideBarView.this.lambda$init$1$XQuickSideBarView();
                }
            });
        }
        setSoundEffectsEnabled(false);
    }

    public /* synthetic */ void lambda$init$0$XQuickSideBarView(XFontScaleHelper xFontScaleHelper, XFontScaleHelper xFontScaleHelperChoose) {
        if (xFontScaleHelper != null) {
            this.mTextSize = xFontScaleHelper.getCurrentFontSize(getResources().getDisplayMetrics());
        }
        if (xFontScaleHelperChoose != null) {
            this.mTextSizeChoose = xFontScaleHelperChoose.getCurrentFontSize(getResources().getDisplayMetrics());
        }
        invalidate();
    }

    public /* synthetic */ void lambda$init$1$XQuickSideBarView() {
        initDefaultColor();
        invalidate();
    }

    @Override // com.xiaopeng.xui.view.XViewDelegate.onFontScaleChangeCallback
    public void onFontScaleChanged() {
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mHeight = getMeasuredHeight();
        this.mWidth = getMeasuredWidth();
        float defaultItemHeight = this.mHeight / this.mLetters.size();
        float f = this.mItemHeight;
        if (f > defaultItemHeight) {
            f = defaultItemHeight;
        }
        this.mItemHeight = f;
        this.mItemWidth = this.mItemHeight;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < this.mLetters.size(); i++) {
            Paint bgRect = new Paint();
            if (i == this.mChoose) {
                this.mPaint.setTextSize(this.mTextSizeChoose);
                this.mPaint.setColor(this.mTextColorChoose);
                bgRect.setColor(getColor(R.color.x_side_bar_item_choose_bg_color));
            } else {
                this.mPaint.setTextSize(this.mTextSize);
                this.mPaint.setColor(this.mTextColor);
                bgRect.setColor(getColor(R.color.x_side_bar_item_bg_color));
            }
            bgRect.setStyle(Paint.Style.FILL);
            bgRect.setAntiAlias(true);
            float f = this.mItemWidth;
            float left = (this.mWidth - f) / 2.0f;
            float f2 = this.mItemHeight;
            RectF rectF = new RectF(left, (i * f2) + 14.0f, f + left, ((i + 1) * f2) + 14.0f);
            canvas.drawRoundRect(rectF, 4.0f, 4.0f, bgRect);
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStyle(Paint.Style.FILL);
            this.mPaint.setTextAlign(Paint.Align.CENTER);
            this.mPaint.setFakeBoldText(true);
            this.mPaint.setTypeface(Typeface.create(getResources().getString(R.string.x_font_typeface_medium), 0));
            Paint.FontMetrics fontMetrics = this.mPaint.getFontMetrics();
            float distance = ((fontMetrics.bottom - fontMetrics.top) / 2.0f) - fontMetrics.bottom;
            float baseline = rectF.centerY() + distance;
            canvas.drawText(this.mLetters.get(i), rectF.centerX(), baseline, this.mPaint);
            this.mPaint.reset();
        }
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();
        int oldChoose = this.mChoose;
        int newChoose = (int) ((y - 14.0f) / this.mItemHeight);
        if (action != 0) {
            if (action != 1) {
                if (action != 2) {
                    if (action == 3) {
                        this.mIsTouchMove = false;
                        XSoundEffectManager.get().release(2);
                        OnQuickSideBarTouchListener onQuickSideBarTouchListener = this.listener;
                        if (onQuickSideBarTouchListener != null) {
                            onQuickSideBarTouchListener.onLetterTouched(false);
                        }
                        this.mChoose = -1;
                        invalidate();
                    }
                }
            } else {
                this.mIsTouchMove = false;
                this.mChoose = -1;
                OnQuickSideBarTouchListener onQuickSideBarTouchListener2 = this.listener;
                if (onQuickSideBarTouchListener2 != null) {
                    onQuickSideBarTouchListener2.onLetterTouched(false);
                }
                if (!this.mAlwaysHighlight) {
                    invalidate();
                }
            }
            return true;
        }
        this.mIsTouchMove = true;
        if (oldChoose != newChoose && newChoose >= 0 && newChoose < this.mLetters.size()) {
            this.mChoose = newChoose;
            invalidate();
            XSoundEffectManager.get().play(2);
            OnQuickSideBarTouchListener onQuickSideBarTouchListener3 = this.listener;
            if (onQuickSideBarTouchListener3 != null) {
                onQuickSideBarTouchListener3.onLetterTouched(true);
                this.listener.onLetterScrolling(this.mLetters.get(newChoose), this.mChoose);
            }
        }
        return true;
    }

    public OnQuickSideBarTouchListener getListener() {
        return this.listener;
    }

    public void setOnQuickSideBarTouchListener(OnQuickSideBarTouchListener listener) {
        this.listener = listener;
    }

    public List<String> getLetters() {
        return this.mLetters;
    }

    public void setLetters(List<String> letters) {
        this.mLetters = letters;
        invalidate();
    }

    private void initDefaultColor() {
        this.mTextColor = getColor(R.color.x_theme_text_03);
        this.mTextColorChoose = getColor(R.color.x_side_bar_text_choose_color);
    }

    private int getColor(int resourceId) {
        return getResources().getColor(resourceId, getContext().getTheme());
    }

    public void setSideBarHighLight(String letter) {
        setSideBarHighLight(letter, false);
    }

    public void setSideBarHighLight(String letter, boolean isCaseSensitive) {
        int index;
        if (!this.mAlwaysHighlight || this.mIsTouchMove) {
            return;
        }
        logD("setSideBarHighLight");
        if (!TextUtils.isEmpty(letter)) {
            if (!isCaseSensitive) {
                index = this.mLetters.indexOf(letter.toUpperCase());
            } else {
                index = this.mLetters.indexOf(letter);
            }
            if (index >= 0 && this.mChoose != index) {
                this.mChoose = index;
                invalidate();
            }
        }
    }

    private void setSideBarHighLight(int position) {
        if (!this.mAlwaysHighlight || this.mIsTouchMove) {
            return;
        }
        int sideSize = this.mLetters.size();
        if (position >= 0 && position < sideSize && this.mChoose != position) {
            this.mChoose = position;
            invalidate();
        }
    }
}
