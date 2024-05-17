package com.badlogic.gdx.backends.android;

import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.text.ClipboardManager;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.badlogic.gdx.utils.Clipboard;
/* loaded from: classes21.dex */
public class AndroidClipboard implements Clipboard {
    private ClipboardManager clipboard;
    private android.content.ClipboardManager honeycombClipboard;

    public AndroidClipboard(Context context) {
        if (Build.VERSION.SDK_INT < 11) {
            this.clipboard = (ClipboardManager) context.getSystemService(NavigationBarInflaterView.CLIPBOARD);
        } else {
            this.honeycombClipboard = (android.content.ClipboardManager) context.getSystemService(NavigationBarInflaterView.CLIPBOARD);
        }
    }

    @Override // com.badlogic.gdx.utils.Clipboard
    public String getContents() {
        CharSequence text;
        if (Build.VERSION.SDK_INT < 11) {
            if (this.clipboard.getText() == null) {
                return null;
            }
            return this.clipboard.getText().toString();
        }
        ClipData clip = this.honeycombClipboard.getPrimaryClip();
        if (clip == null || (text = clip.getItemAt(0).getText()) == null) {
            return null;
        }
        return text.toString();
    }

    @Override // com.badlogic.gdx.utils.Clipboard
    public void setContents(String contents) {
        if (Build.VERSION.SDK_INT < 11) {
            this.clipboard.setText(contents);
            return;
        }
        ClipData data = ClipData.newPlainText(contents, contents);
        this.honeycombClipboard.setPrimaryClip(data);
    }
}
