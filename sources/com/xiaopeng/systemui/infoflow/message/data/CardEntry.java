package com.xiaopeng.systemui.infoflow.message.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import com.xiaopeng.systemui.infoflow.message.define.CardExtra;
/* loaded from: classes24.dex */
public class CardEntry implements Cloneable {
    public static final int SPEECH_CARD_INVALID = -1;
    public static final int SPEECH_CARD_KNOWLEDGE = 2;
    public static final int SPEECH_CARD_WEATHER = 1;
    public String action;
    public Bitmap bigIcon;
    public String key;
    public String pkgName;
    public int position;
    public boolean priority;
    public int showIndex;
    public int soundEffect;
    public int status;
    public String summary;
    public int type;
    public long when;
    public int speechCardType = -1;
    public String title = "";
    public String content = "";
    public int importance = 3;
    public String extraData = "";

    public CardEntry() {
    }

    public CardEntry(StatusBarNotification sbn) {
        this.key = sbn.getKey();
        this.pkgName = sbn.getPackageName();
        init(sbn);
    }

    private void init(StatusBarNotification sbn) {
        Bundle data = sbn.getNotification().extras;
        this.title = data.getString("android.title", "");
        this.content = data.getString("android.text", "");
        this.type = data.getInt(CardExtra.KEY_CARD_TYPE, 1);
        this.action = data.getString(CardExtra.KEY_CARD_JUMP_ACTION, "");
        this.bigIcon = createBigIcon(data.getByteArray(CardExtra.KEY_CARD_IMAGE));
        this.status = data.getInt("status", -1);
        this.extraData = data.getString(CardExtra.KEY_CARD_EXTRA_DATA, "");
        this.when = System.currentTimeMillis();
        this.soundEffect = 0;
    }

    private Bitmap createBigIcon(byte[] image) {
        if (image != null && image.length > 0) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }
        return null;
    }

    public String toString() {
        return String.format("CardEntry(key=%s summary=%s title=%s content=%s status=%d type=%d  action=%s when = %s vul = %b importance = %d)", this.key, this.summary, this.title, this.content, Integer.valueOf(this.status), Integer.valueOf(this.type), this.action, String.valueOf(this.when), Boolean.valueOf(this.priority), Integer.valueOf(this.importance));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: clone */
    public CardEntry m44clone() throws CloneNotSupportedException {
        return (CardEntry) super.clone();
    }
}
