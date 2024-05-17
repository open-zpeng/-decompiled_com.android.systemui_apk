package com.android.systemui.tuner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class ClipboardView extends ImageView implements ClipboardManager.OnPrimaryClipChangedListener {
    private static final int TARGET_COLOR = 1308622847;
    private final ClipboardManager mClipboardManager;
    private ClipData mCurrentClip;

    public ClipboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mClipboardManager = (ClipboardManager) context.getSystemService(ClipboardManager.class);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startListening();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopListening();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == 0 && this.mCurrentClip != null) {
            startPocketDrag();
        }
        return super.onTouchEvent(ev);
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x000f, code lost:
        if (r0 != 6) goto L9;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onDragEvent(android.view.DragEvent r4) {
        /*
            r3 = this;
            int r0 = r4.getAction()
            r1 = 3
            r2 = 1
            if (r0 == r1) goto L16
            r1 = 4
            if (r0 == r1) goto L1f
            r1 = 5
            if (r0 == r1) goto L12
            r1 = 6
            if (r0 == r1) goto L1f
            goto L23
        L12:
            r3.setBackgroundDragTarget(r2)
            goto L23
        L16:
            android.content.ClipboardManager r0 = r3.mClipboardManager
            android.content.ClipData r1 = r4.getClipData()
            r0.setPrimaryClip(r1)
        L1f:
            r0 = 0
            r3.setBackgroundDragTarget(r0)
        L23:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.tuner.ClipboardView.onDragEvent(android.view.DragEvent):boolean");
    }

    private void setBackgroundDragTarget(boolean isTarget) {
        setBackgroundColor(isTarget ? TARGET_COLOR : 0);
    }

    public void startPocketDrag() {
        startDragAndDrop(this.mCurrentClip, new View.DragShadowBuilder(this), null, 256);
    }

    public void startListening() {
        this.mClipboardManager.addPrimaryClipChangedListener(this);
        onPrimaryClipChanged();
    }

    public void stopListening() {
        this.mClipboardManager.removePrimaryClipChangedListener(this);
    }

    @Override // android.content.ClipboardManager.OnPrimaryClipChangedListener
    public void onPrimaryClipChanged() {
        this.mCurrentClip = this.mClipboardManager.getPrimaryClip();
        setImageResource(this.mCurrentClip != null ? R.drawable.clipboard_full : R.drawable.clipboard_empty);
    }
}
