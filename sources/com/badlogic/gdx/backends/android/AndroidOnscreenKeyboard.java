package com.badlogic.gdx.backends.android;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.badlogic.gdx.Input;
/* loaded from: classes21.dex */
class AndroidOnscreenKeyboard implements View.OnKeyListener, View.OnTouchListener {
    final Context context;
    Dialog dialog;
    final Handler handler;
    final AndroidInput input;
    TextView textView;

    public AndroidOnscreenKeyboard(Context context, Handler handler, AndroidInput input) {
        this.context = context;
        this.handler = handler;
        this.input = input;
    }

    Dialog createDialog() {
        this.textView = createView(this.context);
        this.textView.setOnKeyListener(this);
        FrameLayout.LayoutParams textBoxLayoutParams = new FrameLayout.LayoutParams(-1, -2, 80);
        this.textView.setLayoutParams(textBoxLayoutParams);
        this.textView.setFocusable(true);
        this.textView.setFocusableInTouchMode(true);
        TextView textView = this.textView;
        textView.setImeOptions(textView.getImeOptions() | 268435456);
        FrameLayout layout = new FrameLayout(this.context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, 0);
        layout.setLayoutParams(layoutParams);
        layout.addView(this.textView);
        layout.setOnTouchListener(this);
        this.dialog = new Dialog(this.context, 16973841);
        this.dialog.setContentView(layout);
        return this.dialog;
    }

    public static TextView createView(Context context) {
        TextView view = new TextView(context) { // from class: com.badlogic.gdx.backends.android.AndroidOnscreenKeyboard.1
            Editable editable = new PassThroughEditable();

            @Override // android.widget.TextView
            protected boolean getDefaultEditable() {
                return true;
            }

            @Override // android.widget.TextView
            public Editable getEditableText() {
                return this.editable;
            }

            @Override // android.widget.TextView
            protected MovementMethod getDefaultMovementMethod() {
                return ArrowKeyMovementMethod.getInstance();
            }

            @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                Log.d("Test", "down keycode: " + event.getKeyCode());
                return super.onKeyDown(keyCode, event);
            }

            @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
            public boolean onKeyUp(int keyCode, KeyEvent event) {
                Log.d("Test", "up keycode: " + event.getKeyCode());
                return super.onKeyUp(keyCode, event);
            }
        };
        return view;
    }

    public void setVisible(boolean visible) {
        Dialog dialog;
        Dialog dialog2;
        if (visible && (dialog2 = this.dialog) != null) {
            dialog2.dismiss();
            this.dialog = null;
        }
        if (visible && this.dialog == null && !this.input.isPeripheralAvailable(Input.Peripheral.HardwareKeyboard)) {
            this.handler.post(new Runnable() { // from class: com.badlogic.gdx.backends.android.AndroidOnscreenKeyboard.2
                @Override // java.lang.Runnable
                public void run() {
                    AndroidOnscreenKeyboard androidOnscreenKeyboard = AndroidOnscreenKeyboard.this;
                    androidOnscreenKeyboard.dialog = androidOnscreenKeyboard.createDialog();
                    AndroidOnscreenKeyboard.this.dialog.show();
                    AndroidOnscreenKeyboard.this.handler.post(new Runnable() { // from class: com.badlogic.gdx.backends.android.AndroidOnscreenKeyboard.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            AndroidOnscreenKeyboard.this.dialog.getWindow().setSoftInputMode(32);
                            InputMethodManager input = (InputMethodManager) AndroidOnscreenKeyboard.this.context.getSystemService("input_method");
                            if (input != null) {
                                input.showSoftInput(AndroidOnscreenKeyboard.this.textView, 2);
                            }
                        }
                    });
                    final View content = AndroidOnscreenKeyboard.this.dialog.getWindow().findViewById(16908290);
                    content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: com.badlogic.gdx.backends.android.AndroidOnscreenKeyboard.2.2
                        private int keyboardHeight;
                        private boolean keyboardShowing;
                        int[] screenloc = new int[2];

                        @Override // android.view.ViewTreeObserver.OnPreDrawListener
                        public boolean onPreDraw() {
                            content.getLocationOnScreen(this.screenloc);
                            this.keyboardHeight = Math.abs(this.screenloc[1]);
                            if (this.keyboardHeight > 0) {
                                this.keyboardShowing = true;
                            }
                            if (this.keyboardHeight == 0 && this.keyboardShowing) {
                                AndroidOnscreenKeyboard.this.dialog.dismiss();
                                AndroidOnscreenKeyboard.this.dialog = null;
                            }
                            return true;
                        }
                    });
                }
            });
        } else if (!visible && (dialog = this.dialog) != null) {
            dialog.dismiss();
        }
    }

    /* loaded from: classes21.dex */
    public static class PassThroughEditable implements Editable {
        @Override // java.lang.CharSequence
        public char charAt(int index) {
            Log.d("Editable", "charAt");
            return (char) 0;
        }

        @Override // java.lang.CharSequence
        public int length() {
            Log.d("Editable", "length");
            return 0;
        }

        @Override // java.lang.CharSequence
        public CharSequence subSequence(int start, int end) {
            Log.d("Editable", "subSequence");
            return null;
        }

        @Override // android.text.GetChars
        public void getChars(int start, int end, char[] dest, int destoff) {
            Log.d("Editable", "getChars");
        }

        @Override // android.text.Spannable
        public void removeSpan(Object what) {
            Log.d("Editable", "removeSpan");
        }

        @Override // android.text.Spannable
        public void setSpan(Object what, int start, int end, int flags) {
            Log.d("Editable", "setSpan");
        }

        @Override // android.text.Spanned
        public int getSpanEnd(Object tag) {
            Log.d("Editable", "getSpanEnd");
            return 0;
        }

        @Override // android.text.Spanned
        public int getSpanFlags(Object tag) {
            Log.d("Editable", "getSpanFlags");
            return 0;
        }

        @Override // android.text.Spanned
        public int getSpanStart(Object tag) {
            Log.d("Editable", "getSpanStart");
            return 0;
        }

        @Override // android.text.Spanned
        public <T> T[] getSpans(int arg0, int arg1, Class<T> arg2) {
            Log.d("Editable", "getSpans");
            return null;
        }

        @Override // android.text.Spanned
        public int nextSpanTransition(int start, int limit, Class type) {
            Log.d("Editable", "nextSpanTransition");
            return 0;
        }

        @Override // android.text.Editable, java.lang.Appendable
        public Editable append(CharSequence text) {
            Log.d("Editable", "append: " + ((Object) text));
            return this;
        }

        @Override // android.text.Editable, java.lang.Appendable
        public Editable append(char text) {
            Log.d("Editable", "append: " + text);
            return this;
        }

        @Override // android.text.Editable, java.lang.Appendable
        public Editable append(CharSequence text, int start, int end) {
            Log.d("Editable", "append: " + ((Object) text));
            return this;
        }

        @Override // android.text.Editable
        public void clear() {
            Log.d("Editable", "clear");
        }

        @Override // android.text.Editable
        public void clearSpans() {
            Log.d("Editable", "clearSpanes");
        }

        @Override // android.text.Editable
        public Editable delete(int st, int en) {
            Log.d("Editable", "delete, " + st + ", " + en);
            return this;
        }

        @Override // android.text.Editable
        public InputFilter[] getFilters() {
            Log.d("Editable", "getFilters");
            return new InputFilter[0];
        }

        @Override // android.text.Editable
        public Editable insert(int where, CharSequence text) {
            Log.d("Editable", "insert: " + ((Object) text));
            return this;
        }

        @Override // android.text.Editable
        public Editable insert(int where, CharSequence text, int start, int end) {
            Log.d("Editable", "insert: " + ((Object) text));
            return this;
        }

        @Override // android.text.Editable
        public Editable replace(int st, int en, CharSequence text) {
            Log.d("Editable", "replace: " + ((Object) text));
            return this;
        }

        @Override // android.text.Editable
        public Editable replace(int st, int en, CharSequence source, int start, int end) {
            Log.d("Editable", "replace: " + ((Object) source));
            return this;
        }

        @Override // android.text.Editable
        public void setFilters(InputFilter[] filters) {
            Log.d("Editable", "setFilters");
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent e) {
        return false;
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View view, int keycode, KeyEvent e) {
        return false;
    }
}
