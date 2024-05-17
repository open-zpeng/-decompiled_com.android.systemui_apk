package com.android.systemui.statusbar.policy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.Editable;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper;
import com.android.systemui.statusbar.phone.LightBarController;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class RemoteInputView extends LinearLayout implements View.OnClickListener, TextWatcher {
    private static final String TAG = "RemoteInput";
    public static final Object VIEW_TAG = new Object();
    private RemoteInputController mController;
    private RemoteEditText mEditText;
    private NotificationEntry mEntry;
    private Consumer<Boolean> mOnVisibilityChangedListener;
    private PendingIntent mPendingIntent;
    private ProgressBar mProgressBar;
    private RemoteInput mRemoteInput;
    private RemoteInputQuickSettingsDisabler mRemoteInputQuickSettingsDisabler;
    private RemoteInput[] mRemoteInputs;
    private boolean mRemoved;
    private boolean mResetting;
    private int mRevealCx;
    private int mRevealCy;
    private int mRevealR;
    private ImageButton mSendButton;
    public final Object mToken;
    private NotificationViewWrapper mWrapper;

    public RemoteInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mToken = new Object();
        this.mRemoteInputQuickSettingsDisabler = (RemoteInputQuickSettingsDisabler) Dependency.get(RemoteInputQuickSettingsDisabler.class);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mProgressBar = (ProgressBar) findViewById(R.id.remote_input_progress);
        this.mSendButton = (ImageButton) findViewById(R.id.remote_input_send);
        this.mSendButton.setOnClickListener(this);
        this.mEditText = (RemoteEditText) getChildAt(0);
        this.mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.android.systemui.statusbar.policy.RemoteInputView.1
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean isSoftImeEvent = event == null && (actionId == 6 || actionId == 5 || actionId == 4);
                boolean isKeyboardEnterKey = event != null && KeyEvent.isConfirmKey(event.getKeyCode()) && event.getAction() == 0;
                if (isSoftImeEvent || isKeyboardEnterKey) {
                    if (RemoteInputView.this.mEditText.length() > 0) {
                        RemoteInputView.this.sendRemoteInput();
                    }
                    return true;
                }
                return false;
            }
        });
        this.mEditText.addTextChangedListener(this);
        this.mEditText.setInnerFocusable(false);
        this.mEditText.mRemoteInputView = this;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendRemoteInput() {
        Bundle results = new Bundle();
        results.putString(this.mRemoteInput.getResultKey(), this.mEditText.getText().toString());
        Intent fillInIntent = new Intent().addFlags(268435456);
        RemoteInput.addResultsToIntent(this.mRemoteInputs, fillInIntent, results);
        if (this.mEntry.editedSuggestionInfo == null) {
            RemoteInput.setResultsSource(fillInIntent, 0);
        } else {
            RemoteInput.setResultsSource(fillInIntent, 1);
        }
        this.mEditText.setEnabled(false);
        this.mSendButton.setVisibility(4);
        this.mProgressBar.setVisibility(0);
        this.mEntry.remoteInputText = this.mEditText.getText();
        this.mEntry.lastRemoteInputSent = SystemClock.elapsedRealtime();
        this.mController.addSpinning(this.mEntry.key, this.mToken);
        this.mController.removeRemoteInput(this.mEntry, this.mToken);
        this.mEditText.mShowImeOnInputConnection = false;
        this.mController.remoteInputSent(this.mEntry);
        this.mEntry.setHasSentReply();
        ((ShortcutManager) getContext().getSystemService(ShortcutManager.class)).onApplicationActive(this.mEntry.notification.getPackageName(), this.mEntry.notification.getUser().getIdentifier());
        MetricsLogger.action(this.mContext, 398, this.mEntry.notification.getPackageName());
        try {
            this.mPendingIntent.send(this.mContext, 0, fillInIntent);
        } catch (PendingIntent.CanceledException e) {
            Log.i(TAG, "Unable to send remote input result", e);
            MetricsLogger.action(this.mContext, 399, this.mEntry.notification.getPackageName());
        }
    }

    public CharSequence getText() {
        return this.mEditText.getText();
    }

    public static RemoteInputView inflate(Context context, ViewGroup root, NotificationEntry entry, RemoteInputController controller) {
        RemoteInputView v = (RemoteInputView) LayoutInflater.from(context).inflate(R.layout.remote_input, root, false);
        v.mController = controller;
        v.mEntry = entry;
        v.mEditText.setTextOperationUser(computeTextOperationUser(entry.notification.getUser()));
        v.setTag(VIEW_TAG);
        return v;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v == this.mSendButton) {
            sendRemoteInput();
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDefocus(boolean animate) {
        int i;
        this.mController.removeRemoteInput(this.mEntry, this.mToken);
        this.mEntry.remoteInputText = this.mEditText.getText();
        if (!this.mRemoved) {
            if (animate && (i = this.mRevealR) > 0) {
                Animator reveal = ViewAnimationUtils.createCircularReveal(this, this.mRevealCx, this.mRevealCy, i, 0.0f);
                reveal.setInterpolator(Interpolators.FAST_OUT_LINEAR_IN);
                reveal.setDuration(150L);
                reveal.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.policy.RemoteInputView.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        RemoteInputView.this.setVisibility(4);
                        if (RemoteInputView.this.mWrapper != null) {
                            RemoteInputView.this.mWrapper.setRemoteInputVisible(false);
                        }
                    }
                });
                reveal.start();
            } else {
                setVisibility(4);
                NotificationViewWrapper notificationViewWrapper = this.mWrapper;
                if (notificationViewWrapper != null) {
                    notificationViewWrapper.setRemoteInputVisible(false);
                }
            }
        }
        this.mRemoteInputQuickSettingsDisabler.setRemoteInputActive(false);
        MetricsLogger.action(this.mContext, 400, this.mEntry.notification.getPackageName());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mEntry.getRow().isChangingPosition() && getVisibility() == 0 && this.mEditText.isFocusable()) {
            this.mEditText.requestFocus();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mEntry.getRow().isChangingPosition() || isTemporarilyDetached()) {
            return;
        }
        this.mController.removeRemoteInput(this.mEntry, this.mToken);
        this.mController.removeSpinning(this.mEntry.key, this.mToken);
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.mPendingIntent = pendingIntent;
    }

    public void setRemoteInput(RemoteInput[] remoteInputs, RemoteInput remoteInput, NotificationEntry.EditedSuggestionInfo editedSuggestionInfo) {
        this.mRemoteInputs = remoteInputs;
        this.mRemoteInput = remoteInput;
        this.mEditText.setHint(this.mRemoteInput.getLabel());
        NotificationEntry notificationEntry = this.mEntry;
        notificationEntry.editedSuggestionInfo = editedSuggestionInfo;
        if (editedSuggestionInfo != null) {
            notificationEntry.remoteInputText = editedSuggestionInfo.originalText;
        }
    }

    public void focusAnimated() {
        if (getVisibility() != 0) {
            Animator animator = ViewAnimationUtils.createCircularReveal(this, this.mRevealCx, this.mRevealCy, 0.0f, this.mRevealR);
            animator.setDuration(360L);
            animator.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
            animator.start();
        }
        focus();
    }

    private static UserHandle computeTextOperationUser(UserHandle notificationUser) {
        return UserHandle.ALL.equals(notificationUser) ? UserHandle.of(ActivityManager.getCurrentUser()) : notificationUser;
    }

    public void focus() {
        MetricsLogger.action(this.mContext, 397, this.mEntry.notification.getPackageName());
        setVisibility(0);
        NotificationViewWrapper notificationViewWrapper = this.mWrapper;
        if (notificationViewWrapper != null) {
            notificationViewWrapper.setRemoteInputVisible(true);
        }
        this.mEditText.setInnerFocusable(true);
        RemoteEditText remoteEditText = this.mEditText;
        remoteEditText.mShowImeOnInputConnection = true;
        remoteEditText.setText(this.mEntry.remoteInputText);
        RemoteEditText remoteEditText2 = this.mEditText;
        remoteEditText2.setSelection(remoteEditText2.getText().length());
        this.mEditText.requestFocus();
        this.mController.addRemoteInput(this.mEntry, this.mToken);
        this.mRemoteInputQuickSettingsDisabler.setRemoteInputActive(true);
        updateSendButton();
    }

    public void onNotificationUpdateOrReset() {
        NotificationViewWrapper notificationViewWrapper;
        boolean sending = this.mProgressBar.getVisibility() == 0;
        if (sending) {
            reset();
        }
        if (isActive() && (notificationViewWrapper = this.mWrapper) != null) {
            notificationViewWrapper.setRemoteInputVisible(true);
        }
    }

    private void reset() {
        this.mResetting = true;
        this.mEntry.remoteInputTextWhenReset = SpannedString.valueOf(this.mEditText.getText());
        this.mEditText.getText().clear();
        this.mEditText.setEnabled(true);
        this.mSendButton.setVisibility(0);
        this.mProgressBar.setVisibility(4);
        this.mController.removeSpinning(this.mEntry.key, this.mToken);
        updateSendButton();
        onDefocus(false);
        this.mResetting = false;
    }

    @Override // android.view.ViewGroup
    public boolean onRequestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        if (this.mResetting && child == this.mEditText) {
            return false;
        }
        return super.onRequestSendAccessibilityEvent(child, event);
    }

    private void updateSendButton() {
        this.mSendButton.setEnabled(this.mEditText.getText().length() != 0);
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable s) {
        updateSendButton();
    }

    public void close() {
        this.mEditText.defocusIfNeeded(false);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            this.mController.requestDisallowLongPressAndDismiss();
        }
        return super.onInterceptTouchEvent(ev);
    }

    public boolean requestScrollTo() {
        this.mController.lockScrollTo(this.mEntry);
        return true;
    }

    public boolean isActive() {
        return this.mEditText.isFocused() && this.mEditText.isEnabled();
    }

    public void stealFocusFrom(RemoteInputView other) {
        other.close();
        setPendingIntent(other.mPendingIntent);
        setRemoteInput(other.mRemoteInputs, other.mRemoteInput, this.mEntry.editedSuggestionInfo);
        setRevealParameters(other.mRevealCx, other.mRevealCy, other.mRevealR);
        focus();
    }

    public boolean updatePendingIntentFromActions(Notification.Action[] actions) {
        Intent current;
        PendingIntent pendingIntent = this.mPendingIntent;
        if (pendingIntent == null || actions == null || (current = pendingIntent.getIntent()) == null) {
            return false;
        }
        for (Notification.Action a : actions) {
            RemoteInput[] inputs = a.getRemoteInputs();
            if (a.actionIntent != null && inputs != null) {
                Intent candidate = a.actionIntent.getIntent();
                if (current.filterEquals(candidate)) {
                    RemoteInput input = null;
                    for (RemoteInput i : inputs) {
                        if (i.getAllowFreeFormInput()) {
                            input = i;
                        }
                    }
                    if (input != null) {
                        setPendingIntent(a.actionIntent);
                        setRemoteInput(inputs, input, null);
                        return true;
                    }
                } else {
                    continue;
                }
            }
        }
        return false;
    }

    public PendingIntent getPendingIntent() {
        return this.mPendingIntent;
    }

    public void setRemoved() {
        this.mRemoved = true;
    }

    public void setRevealParameters(int cx, int cy, int r) {
        this.mRevealCx = cx;
        this.mRevealCy = cy;
        this.mRevealR = r;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchStartTemporaryDetach() {
        super.dispatchStartTemporaryDetach();
        detachViewFromParent(this.mEditText);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchFinishTemporaryDetach() {
        if (isAttachedToWindow()) {
            RemoteEditText remoteEditText = this.mEditText;
            attachViewToParent(remoteEditText, 0, remoteEditText.getLayoutParams());
        } else {
            removeDetachedView(this.mEditText, false);
        }
        super.dispatchFinishTemporaryDetach();
    }

    public void setWrapper(NotificationViewWrapper wrapper) {
        this.mWrapper = wrapper;
    }

    public void setOnVisibilityChangedListener(Consumer<Boolean> visibilityChangedListener) {
        this.mOnVisibilityChangedListener = visibilityChangedListener;
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View changedView, int visibility) {
        Consumer<Boolean> consumer;
        super.onVisibilityChanged(changedView, visibility);
        if (changedView != this || (consumer = this.mOnVisibilityChangedListener) == null) {
            return;
        }
        consumer.accept(Boolean.valueOf(visibility == 0));
    }

    public boolean isSending() {
        return getVisibility() == 0 && this.mController.isSpinning(this.mEntry.key, this.mToken);
    }

    /* loaded from: classes21.dex */
    public static class RemoteEditText extends EditText {
        private final Drawable mBackground;
        private LightBarController mLightBarController;
        private RemoteInputView mRemoteInputView;
        boolean mShowImeOnInputConnection;

        public RemoteEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.mBackground = getBackground();
            this.mLightBarController = (LightBarController) Dependency.get(LightBarController.class);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void defocusIfNeeded(boolean animate) {
            RemoteInputView remoteInputView;
            RemoteInputView remoteInputView2 = this.mRemoteInputView;
            if ((remoteInputView2 != null && remoteInputView2.mEntry.getRow().isChangingPosition()) || isTemporarilyDetached()) {
                if (isTemporarilyDetached() && (remoteInputView = this.mRemoteInputView) != null) {
                    remoteInputView.mEntry.remoteInputText = getText();
                }
            } else if (isFocusable() && isEnabled()) {
                setInnerFocusable(false);
                RemoteInputView remoteInputView3 = this.mRemoteInputView;
                if (remoteInputView3 != null) {
                    remoteInputView3.onDefocus(animate);
                }
                this.mShowImeOnInputConnection = false;
            }
        }

        @Override // android.widget.TextView, android.view.View
        protected void onVisibilityChanged(View changedView, int visibility) {
            super.onVisibilityChanged(changedView, visibility);
            if (!isShown()) {
                defocusIfNeeded(false);
            }
        }

        @Override // android.widget.TextView, android.view.View
        protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
            if (!focused) {
                defocusIfNeeded(true);
            }
            if (!this.mRemoteInputView.mRemoved) {
                this.mLightBarController.setDirectReplying(focused);
            }
        }

        @Override // android.widget.TextView, android.view.View
        public void getFocusedRect(Rect r) {
            super.getFocusedRect(r);
            r.top = this.mScrollY;
            r.bottom = this.mScrollY + (this.mBottom - this.mTop);
        }

        @Override // android.view.View
        public boolean requestRectangleOnScreen(Rect rectangle) {
            return this.mRemoteInputView.requestScrollTo();
        }

        @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == 4) {
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }

        @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
        public boolean onKeyUp(int keyCode, KeyEvent event) {
            if (keyCode == 4) {
                defocusIfNeeded(true);
                return true;
            }
            return super.onKeyUp(keyCode, event);
        }

        @Override // android.widget.TextView, android.view.View
        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            if (event.getKeyCode() == 4 && event.getAction() == 1) {
                defocusIfNeeded(true);
            }
            return super.onKeyPreIme(keyCode, event);
        }

        @Override // android.widget.TextView, android.view.View
        public boolean onCheckIsTextEditor() {
            RemoteInputView remoteInputView = this.mRemoteInputView;
            boolean flyingOut = remoteInputView != null && remoteInputView.mRemoved;
            return !flyingOut && super.onCheckIsTextEditor();
        }

        @Override // android.widget.TextView, android.view.View
        public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
            final InputMethodManager imm;
            InputConnection inputConnection = super.onCreateInputConnection(outAttrs);
            if (this.mShowImeOnInputConnection && inputConnection != null && (imm = (InputMethodManager) getContext().getSystemService(InputMethodManager.class)) != null) {
                post(new Runnable() { // from class: com.android.systemui.statusbar.policy.RemoteInputView.RemoteEditText.1
                    @Override // java.lang.Runnable
                    public void run() {
                        imm.viewClicked(RemoteEditText.this);
                        imm.showSoftInput(RemoteEditText.this, 0);
                    }
                });
            }
            return inputConnection;
        }

        @Override // android.widget.TextView
        public void onCommitCompletion(CompletionInfo text) {
            clearComposingText();
            setText(text.getText());
            setSelection(getText().length());
        }

        void setInnerFocusable(boolean focusable) {
            setFocusableInTouchMode(focusable);
            setFocusable(focusable);
            setCursorVisible(focusable);
            if (focusable) {
                requestFocus();
                setBackground(this.mBackground);
                return;
            }
            setBackground(null);
        }
    }
}
