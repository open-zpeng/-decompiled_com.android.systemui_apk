package com.xiaopeng.xui.widget.dialogview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.vui.commons.VuiMode;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.utils.XTouchAreaUtils;
import com.xiaopeng.xui.vui.VuiView;
import com.xiaopeng.xui.widget.XButton;
import com.xiaopeng.xui.widget.XImageButton;
import com.xiaopeng.xui.widget.XLinearLayout;
import com.xiaopeng.xui.widget.XScrollView;
import com.xiaopeng.xui.widget.XTextView;
import com.xiaopeng.xui.widget.dialogview.XCountDown;
import com.xiaopeng.xui.widget.dialogview.XDialogViewInterface;
import org.json.JSONException;
import org.json.JSONObject;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes25.dex */
public class XDialogViewDelegateImpl extends XDialogViewDelegate {
    private View mCloseView;
    private ViewGroup mContentView;
    private XCountDown mCountDownHandler;
    private XDialogViewInterface.OnClickListener mItemListener;
    private XButton mNegativeButton;
    private boolean mNegativeInterceptDismiss;
    private XDialogViewInterface.OnClickListener mNegativeListener;
    private View.OnClickListener mOnClickListener;
    private XDialogViewInterface.OnCloseListener mOnCloseListener;
    private XDialogViewInterface.OnCountDownListener mOnCountDownListener;
    private XDialogViewInterface.OnDismissListener mOnDismissListener;
    private XButton mPositiveButton;
    private boolean mPositiveInterceptDismiss;
    private XDialogViewInterface.OnClickListener mPositiveListener;
    private XDialogContentView mRootView;
    private TextView mTextTitle;
    private ViewGroup mTextTitleLayout;
    private View mViewHasButtons;
    private XDialogList mXDialogList;
    private XDialogMessage mXDialogMessage;
    private XScrollView mXDialogScroll;

    /* JADX INFO: Access modifiers changed from: package-private */
    public XDialogViewDelegateImpl(XDialogView xDialogView, @NonNull Context context, int style) {
        super(xDialogView, context, style);
        this.mOnClickListener = new View.OnClickListener() { // from class: com.xiaopeng.xui.widget.dialogview.XDialogViewDelegateImpl.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int id = v.getId();
                boolean positiveInterceptDismiss = XDialogViewDelegateImpl.this.mPositiveInterceptDismiss;
                boolean negativeInterceptDismiss = XDialogViewDelegateImpl.this.mNegativeInterceptDismiss;
                if (id == R.id.x_dialog_button1) {
                    if (XDialogViewDelegateImpl.this.mPositiveListener == null) {
                        XDialogViewDelegateImpl.this.logs("onClick mPositiveListener is null");
                    } else {
                        XDialogViewDelegateImpl xDialogViewDelegateImpl = XDialogViewDelegateImpl.this;
                        xDialogViewDelegateImpl.logs("onClick  positiveIntercept " + positiveInterceptDismiss + " , mPositiveListener " + XDialogViewDelegateImpl.this.mPositiveListener);
                        XDialogViewDelegateImpl.this.mPositiveListener.onClick(XDialogViewDelegateImpl.this.mXDelegator, -1);
                        if (positiveInterceptDismiss) {
                            return;
                        }
                    }
                } else if (id == R.id.x_dialog_button2) {
                    if (XDialogViewDelegateImpl.this.mNegativeListener == null) {
                        XDialogViewDelegateImpl.this.logs("onClick mNegativeListener is null");
                    } else {
                        XDialogViewDelegateImpl xDialogViewDelegateImpl2 = XDialogViewDelegateImpl.this;
                        xDialogViewDelegateImpl2.logs("onClick negativeIntercept " + negativeInterceptDismiss + " , mNegativeListener " + XDialogViewDelegateImpl.this.mNegativeListener);
                        XDialogViewDelegateImpl.this.mNegativeListener.onClick(XDialogViewDelegateImpl.this.mXDelegator, -2);
                        if (negativeInterceptDismiss) {
                            return;
                        }
                    }
                } else if (id == R.id.x_dialog_close) {
                    XDialogViewDelegateImpl.this.logs("onClick close");
                    if (XDialogViewDelegateImpl.this.mOnCloseListener != null && XDialogViewDelegateImpl.this.mOnCloseListener.onClose(XDialogViewDelegateImpl.this.mXDelegator)) {
                        XDialogViewDelegateImpl.this.logs("onClick close intercept dismiss ");
                        return;
                    }
                } else {
                    XDialogViewDelegateImpl xDialogViewDelegateImpl3 = XDialogViewDelegateImpl.this;
                    xDialogViewDelegateImpl3.logs("onClick invalid id " + id);
                }
                XDialogViewDelegateImpl.this.dismiss();
            }
        };
        init();
    }

    private void init() {
        TypedArray a = this.mContext.obtainStyledAttributes((AttributeSet) null, R.styleable.XDialog);
        int maxHeight = (int) a.getDimension(R.styleable.XDialog_dialog_max_height, 0.0f);
        int w = a.getLayoutDimension(R.styleable.XDialog_dialog_width, 0);
        int h = a.getLayoutDimension(R.styleable.XDialog_dialog_height, 0);
        a.recycle();
        this.mRootView = new XDialogContentView(this, this.mContext);
        this.mRootView.setMaxHeight(maxHeight);
        this.mRootView.setMinimumHeight(this.mContext.getResources().getDimensionPixelSize(R.dimen.x_dialog_min_height));
        this.mRootView.setWidth(w);
        this.mRootView.setHeight(h);
        logs("maxHeight:" + maxHeight + ",w:" + w + ", h " + h);
        LayoutInflater.from(this.mContext).inflate(R.layout.x_dialog, this.mRootView);
        initView(this.mRootView);
    }

    private void initView(View v) {
        this.mTextTitleLayout = (ViewGroup) v.findViewById(R.id.x_dialog_title_layout);
        this.mCloseView = v.findViewById(R.id.x_dialog_close);
        this.mTextTitle = (TextView) v.findViewById(R.id.x_dialog_title);
        this.mContentView = (ViewGroup) v.findViewById(R.id.x_dialog_content);
        this.mPositiveButton = (XButton) v.findViewById(R.id.x_dialog_button1);
        this.mNegativeButton = (XButton) v.findViewById(R.id.x_dialog_button2);
        this.mViewHasButtons = v.findViewById(R.id.x_dialog_has_buttons);
        this.mCloseView.setOnClickListener(this.mOnClickListener);
        this.mPositiveButton.setOnClickListener(this.mOnClickListener);
        this.mNegativeButton.setOnClickListener(this.mOnClickListener);
        this.mRootView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.xiaopeng.xui.widget.dialogview.XDialogViewDelegateImpl.1
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View v2) {
                XDialogViewDelegateImpl.this.extendTouchArea();
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View v2) {
                if (XDialogViewDelegateImpl.this.mCountDownHandler != null) {
                    XDialogViewDelegateImpl.this.mCountDownHandler.stop();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismiss() {
        XDialogViewInterface.OnDismissListener onDismissListener = this.mOnDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss(this.mXDelegator);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Context getContext() {
        return this.mContext;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setTitle(@Nullable CharSequence text) {
        this.mTextTitle.setText(text);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setTitle(@StringRes int resId) {
        this.mTextTitle.setText(resId);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setIcon(@DrawableRes int iconId) {
        prepareMessage();
        this.mXDialogMessage.setIcon(iconId);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setIcon(@Nullable Drawable icon) {
        prepareMessage();
        this.mXDialogMessage.setIcon(icon);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setMessage(@Nullable CharSequence text) {
        prepareMessage();
        this.mXDialogMessage.setMessage(text);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setMessage(@StringRes int resId) {
        prepareMessage();
        this.mXDialogMessage.setMessage(resId);
    }

    private void prepareMessage() {
        if (this.mXDialogMessage == null) {
            this.mXDialogMessage = new XDialogMessage(this.mContext);
        }
        this.mContentView.removeAllViews();
        this.mContentView.addView(this.mXDialogMessage);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setCustomView(@NonNull View view) {
        setCustomView(view, true);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setCustomView(@NonNull View view, boolean useScroll) {
        this.mContentView.removeAllViews();
        if (useScroll) {
            if (this.mXDialogScroll == null) {
                this.mXDialogScroll = (XScrollView) LayoutInflater.from(this.mContext).inflate(R.layout.x_dialog_scroll, this.mContentView, false);
            }
            this.mXDialogScroll.removeAllViews();
            this.mContentView.addView(this.mXDialogScroll);
            this.mXDialogScroll.addView(view);
            return;
        }
        this.mContentView.addView(view);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setCustomView(@LayoutRes int resId) {
        setCustomView(resId, true);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setCustomView(@LayoutRes int resId, boolean useScroll) {
        View view = LayoutInflater.from(getContext()).inflate(resId, this.mContentView, false);
        setCustomView(view, useScroll);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    @Deprecated
    public void setSingleChoiceItems(CharSequence[] items, int checkedItem, XDialogViewInterface.OnClickListener listener) {
        if (this.mXDialogList == null) {
            this.mXDialogList = new XDialogList(getContext());
        }
        this.mContentView.removeAllViews();
        this.mContentView.addView(this.mXDialogList);
        this.mXDialogList.setSingleChoiceItems(items, checkedItem, new AdapterView.OnItemClickListener() { // from class: com.xiaopeng.xui.widget.dialogview.-$$Lambda$XDialogViewDelegateImpl$rRQqXOMO6vKrH5nQXK80WFX9l94
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                XDialogViewDelegateImpl.this.lambda$setSingleChoiceItems$0$XDialogViewDelegateImpl(adapterView, view, i, j);
            }
        });
        this.mItemListener = listener;
    }

    public /* synthetic */ void lambda$setSingleChoiceItems$0$XDialogViewDelegateImpl(AdapterView parent, View view, int position, long id) {
        XDialogViewInterface.OnClickListener onClickListener = this.mItemListener;
        if (onClickListener != null) {
            onClickListener.onClick(this.mXDelegator, position);
        }
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setCloseVisibility(final boolean show) {
        this.mCloseView.setVisibility(show ? 0 : 4);
        if (this.mCloseView.getWidth() > 0) {
            resetTitlePadding(show);
        } else {
            this.mCloseView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: com.xiaopeng.xui.widget.dialogview.XDialogViewDelegateImpl.3
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    XDialogViewDelegateImpl.this.mCloseView.getViewTreeObserver().removeOnPreDrawListener(this);
                    XDialogViewDelegateImpl.this.resetTitlePadding(show);
                    return true;
                }
            });
        }
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public boolean isCloseShowing() {
        return this.mCloseView.getVisibility() == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetTitlePadding(boolean show) {
        if (!show) {
            this.mTextTitle.setPadding(0, 0, 0, 0);
            return;
        }
        int closeWidth = this.mCloseView.getWidth();
        int closeMargin = (int) this.mContext.getResources().getDimension(R.dimen.x_dialog_close_padding);
        int textMargin = 0;
        ViewGroup.LayoutParams lp = this.mTextTitle.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            textMargin = ((ViewGroup.MarginLayoutParams) lp).leftMargin;
        }
        int padding = ((closeMargin * 2) + closeWidth) - textMargin;
        this.mTextTitle.setPadding(padding, 0, padding, 0);
        logs(String.format("resetTitlePadding closeWidth: %s ,closeMargin: %s ,textMargin: %s ,padding: %s", Integer.valueOf(closeWidth), Integer.valueOf(closeMargin), Integer.valueOf(textMargin), Integer.valueOf(padding)));
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    @Deprecated
    public void setTitleVisibility(boolean show) {
        setTitleBarVisibility(show);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setTitleBarVisibility(boolean show) {
        this.mTextTitleLayout.setVisibility(show ? 0 : 8);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setPositiveButton(@StringRes int resId) {
        if (resId == 0) {
            setPositiveButton((CharSequence) null);
        } else {
            setPositiveButton(this.mContext.getText(resId));
        }
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setPositiveButton(@Nullable CharSequence text) {
        this.mPositiveButton.setVisibility(TextUtils.isEmpty(text) ? 8 : 0);
        this.mPositiveButton.setText(text);
        resetButton();
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setPositiveButtonListener(XDialogViewInterface.OnClickListener listener) {
        this.mPositiveListener = listener;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setPositiveButton(@StringRes int text, XDialogViewInterface.OnClickListener listener) {
        setPositiveButton(text);
        setPositiveButtonListener(listener);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setPositiveButton(@Nullable CharSequence text, XDialogViewInterface.OnClickListener listener) {
        setPositiveButton(text);
        setPositiveButtonListener(listener);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setNegativeButton(@StringRes int resId) {
        if (resId == 0) {
            setNegativeButton((CharSequence) null);
        } else {
            setNegativeButton(this.mContext.getText(resId));
        }
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setNegativeButton(@Nullable CharSequence text) {
        this.mNegativeButton.setVisibility(TextUtils.isEmpty(text) ? 8 : 0);
        this.mNegativeButton.setText(text);
        resetButton();
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setNegativeButtonListener(XDialogViewInterface.OnClickListener listener) {
        this.mNegativeListener = listener;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setNegativeButton(@StringRes int text, XDialogViewInterface.OnClickListener listener) {
        setNegativeButton(text);
        setNegativeButtonListener(listener);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setNegativeButton(@Nullable CharSequence text, XDialogViewInterface.OnClickListener listener) {
        setNegativeButton(text);
        setNegativeButtonListener(listener);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setPositiveButtonInterceptDismiss(boolean interceptDismiss) {
        this.mPositiveInterceptDismiss = interceptDismiss;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setNegativeButtonInterceptDismiss(boolean interceptDismiss) {
        this.mNegativeInterceptDismiss = interceptDismiss;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setPositiveButtonEnable(boolean enable) {
        this.mPositiveButton.setEnabled(enable);
        updateVui(this.mPositiveButton, enable);
    }

    private void updateVui(XButton button, boolean enable) {
        boolean needUpdate = false;
        try {
            JSONObject props = button.getVuiProps();
            if (enable) {
                if (props != null && props.has(VuiConstants.PROPS_FEEDBACK)) {
                    props.remove(VuiConstants.PROPS_FEEDBACK);
                    needUpdate = true;
                }
            } else {
                if (props == null) {
                    props = new JSONObject();
                }
                if (!props.has(VuiConstants.PROPS_FEEDBACK)) {
                    props.put(VuiConstants.PROPS_FEEDBACK, true);
                    needUpdate = true;
                }
            }
            if (needUpdate) {
                button.setVuiProps(props);
                button.updateVui(button);
            }
        } catch (JSONException e) {
        }
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setNegativeButtonEnable(boolean enable) {
        this.mNegativeButton.setEnabled(enable);
        updateVui(this.mNegativeButton, enable);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public boolean isPositiveButtonEnable() {
        XButton xButton = this.mPositiveButton;
        return xButton != null && xButton.isEnabled();
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public boolean isNegativeButtonEnable() {
        XButton xButton = this.mNegativeButton;
        return xButton != null && xButton.isEnabled();
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public boolean isPositiveButtonShowing() {
        XButton xButton = this.mPositiveButton;
        return xButton != null && xButton.getVisibility() == 0;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public boolean isNegativeButtonShowing() {
        XButton xButton = this.mNegativeButton;
        return xButton != null && xButton.getVisibility() == 0;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void startPositiveButtonCountDown(int delayDismiss) {
        XCountDown xCountDown = this.mCountDownHandler;
        if (xCountDown != null) {
            xCountDown.stop();
        }
        if (this.mPositiveButton.getVisibility() == 0 && delayDismiss > 0) {
            this.mCountDownHandler = new XCountDown(new XDialogCountDownHandlerCallBack(this.mPositiveButton, -1));
            this.mCountDownHandler.start(this.mPositiveButton.getText().toString(), delayDismiss);
        }
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void startNegativeButtonCountDown(int delayDismiss) {
        XCountDown xCountDown = this.mCountDownHandler;
        if (xCountDown != null) {
            xCountDown.stop();
        }
        if (this.mNegativeButton.getVisibility() == 0 && delayDismiss > 0) {
            this.mCountDownHandler = new XCountDown(new XDialogCountDownHandlerCallBack(this.mNegativeButton, -2));
            this.mCountDownHandler.start(this.mNegativeButton.getText().toString(), delayDismiss);
        }
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setOnCloseListener(XDialogViewInterface.OnCloseListener listener) {
        this.mOnCloseListener = listener;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setOnCountDownListener(XDialogViewInterface.OnCountDownListener listener) {
        this.mOnCountDownListener = listener;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setOnDismissListener(XDialogViewInterface.OnDismissListener listener) {
        this.mOnDismissListener = listener;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setThemeCallback(ThemeViewModel.OnCallback onCallback) {
        this.mRootView.setThemeCallback(onCallback);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public boolean onKey(int keyCode, KeyEvent event) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void extendTouchArea() {
        int[] padding = {0, 20, 0, 0};
        XTouchAreaUtils.extendTouchArea(this.mPositiveButton, this.mRootView, padding);
        XTouchAreaUtils.extendTouchArea(this.mNegativeButton, this.mRootView, padding);
        int margin = (int) this.mCloseView.getContext().getResources().getDimension(R.dimen.x_dialog_close_padding);
        int[] paddingClose = {margin, margin, margin, margin};
        XTouchAreaUtils.extendTouchArea(this.mCloseView, this.mRootView, paddingClose);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public ViewGroup getContentView() {
        return this.mRootView;
    }

    private void resetButton() {
        if (this.mPositiveButton.getVisibility() == 0 || this.mNegativeButton.getVisibility() == 0) {
            this.mViewHasButtons.setVisibility(0);
        } else {
            this.mViewHasButtons.setVisibility(8);
        }
    }

    /* loaded from: classes25.dex */
    private class XDialogCountDownHandlerCallBack implements XCountDown.CallBack {
        private int mBtnId;
        private TextView mButton;
        private String mString;

        XDialogCountDownHandlerCallBack(TextView button, int btnId) {
            this.mButton = button;
            this.mBtnId = btnId;
            this.mString = XDialogViewDelegateImpl.this.getContext().getString(R.string.x_countdown_tips);
        }

        @Override // com.xiaopeng.xui.widget.dialogview.XCountDown.CallBack
        public void onCountDown(String tag, int count, int index) {
            this.mButton.setText(String.format(this.mString, tag, Integer.valueOf(index)));
            TextView textView = this.mButton;
            if (textView instanceof VuiView) {
                ((VuiView) textView).setVuiLabel(tag);
            }
        }

        @Override // com.xiaopeng.xui.widget.dialogview.XCountDown.CallBack
        public void onCountDownOver(String tag) {
            this.mButton.setText(tag);
            if (XDialogViewDelegateImpl.this.mOnCountDownListener == null || !XDialogViewDelegateImpl.this.mOnCountDownListener.onCountDown(XDialogViewDelegateImpl.this.mXDelegator, this.mBtnId)) {
                XDialogViewDelegateImpl.this.mOnClickListener.onClick(this.mButton);
            } else {
                XDialogViewDelegateImpl.this.logs("onCountDown intercept onClick ");
            }
        }

        @Override // com.xiaopeng.xui.widget.dialogview.XCountDown.CallBack
        public void onCountDownStop(String tag) {
            this.mButton.setText(tag);
        }
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void onBuildScenePrepare() {
        try {
            XTextView view = (XTextView) this.mRootView.findViewById(R.id.x_dialog_title);
            CharSequence title = view.getText();
            if (title != null) {
                String titleStr = title.toString();
                if (!TextUtils.isEmpty(titleStr)) {
                    String label = this.mRootView.getVuiLabel();
                    if (TextUtils.isEmpty(label)) {
                        this.mRootView.setVuiLabel(titleStr);
                    } else if (!label.contains(titleStr)) {
                        this.mRootView.setVuiLabel(String.format("%s|%s", titleStr, label));
                    }
                }
            }
            XImageButton close = (XImageButton) this.mRootView.findViewById(R.id.x_dialog_close);
            if (close != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(VuiConstants.PROPS_GENERALACT, VuiConstants.GENERALACT_CLOSE);
                jsonObject.put(VuiConstants.PROPS_VOICECONTROL, true);
                close.setVuiProps(jsonObject);
            }
            XButton positiveBtn = (XButton) this.mRootView.findViewById(R.id.x_dialog_button1);
            if (positiveBtn.getVisibility() == 0) {
                if (this.mRootView.getContext().getString(R.string.vui_label_confirm).equals(positiveBtn.getText().toString())) {
                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put(VuiConstants.PROPS_GENERALACT, "System.Confirm");
                    positiveBtn.setVuiProps(jsonObject2);
                }
            } else {
                positiveBtn.setVuiMode(VuiMode.DISABLED);
            }
            XButton negativeBtn = (XButton) this.mRootView.findViewById(R.id.x_dialog_button2);
            if (negativeBtn.getVisibility() == 0) {
                if (this.mRootView.getContext().getString(R.string.vui_label_cancel).equals(negativeBtn.getText().toString())) {
                    JSONObject jsonObject3 = new JSONObject();
                    jsonObject3.put(VuiConstants.PROPS_GENERALACT, VuiConstants.GENERALACT_CANCEL);
                    negativeBtn.setVuiProps(jsonObject3);
                    return;
                }
                return;
            }
            negativeBtn.setVuiMode(VuiMode.DISABLED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes25.dex */
    public class XDialogContentView extends XLinearLayout {
        private int mH;
        private int mMaxHeight;
        private int mW;

        public XDialogContentView(@NonNull XDialogViewDelegateImpl xDialogViewDelegateImpl, Context context) {
            this(context, null);
        }

        public XDialogContentView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            setOrientation(1);
            setVuiLabel(getResources().getString(R.string.vui_label_dialog));
        }

        public void setMaxHeight(int maxHeight) {
            this.mMaxHeight = maxHeight;
        }

        public void setWidth(int w) {
            this.mW = w;
        }

        public void setHeight(int h) {
            this.mH = h;
        }

        @Override // android.view.View
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
            XDialogViewDelegateImpl xDialogViewDelegateImpl = XDialogViewDelegateImpl.this;
            xDialogViewDelegateImpl.logs("hasWindowFocus " + hasWindowFocus);
            if (this.mXViewDelegate != null) {
                this.mXViewDelegate.onWindowFocusChanged(hasWindowFocus);
            }
        }

        @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (changed) {
                XDialogViewDelegateImpl xDialogViewDelegateImpl = XDialogViewDelegateImpl.this;
                xDialogViewDelegateImpl.logs("onLayout-w:" + getWidth() + ",h:" + getHeight() + ", mMaxHeight " + this.mMaxHeight);
            }
        }

        @Override // android.widget.LinearLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int wSpec = View.MeasureSpec.makeMeasureSpec(this.mW, 1073741824);
            int i = this.mH;
            if (i <= 0) {
                int i2 = this.mMaxHeight;
                if (i2 > 0) {
                    int hSpec = View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE);
                    super.onMeasure(wSpec, hSpec);
                    return;
                }
                super.onMeasure(wSpec, heightMeasureSpec);
                return;
            }
            int hSpec2 = View.MeasureSpec.makeMeasureSpec(i, 1073741824);
            super.onMeasure(wSpec, hSpec2);
        }

        public void setThemeCallback(ThemeViewModel.OnCallback onCallback) {
            if (this.mXViewDelegate != null && this.mXViewDelegate.getThemeViewModel() != null) {
                this.mXViewDelegate.getThemeViewModel().setCallback(onCallback);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logs(String msg) {
        XLogUtils.i("xpui-XDialogView", msg + "--hashcode " + this.mXDelegator.hashCode());
    }
}
