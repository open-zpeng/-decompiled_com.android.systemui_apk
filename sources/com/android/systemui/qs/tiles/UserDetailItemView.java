package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.util.ArrayUtils;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.UserAvatarView;
/* loaded from: classes21.dex */
public class UserDetailItemView extends LinearLayout {
    protected static int layoutResId = R.layout.qs_user_detail_item;
    private Typeface mActivatedTypeface;
    private UserAvatarView mAvatar;
    private TextView mName;
    private Typeface mRegularTypeface;
    private View mRestrictedPadlock;

    public UserDetailItemView(Context context) {
        this(context, null);
    }

    public UserDetailItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserDetailItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public UserDetailItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UserDetailItemView, defStyleAttr, defStyleRes);
        int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.UserDetailItemView_regularFontFamily) {
                this.mRegularTypeface = Typeface.create(a.getString(attr), 0);
            } else if (attr == R.styleable.UserDetailItemView_activatedFontFamily) {
                this.mActivatedTypeface = Typeface.create(a.getString(attr), 0);
            }
        }
        a.recycle();
    }

    public static UserDetailItemView convertOrInflate(Context context, View convertView, ViewGroup root) {
        if (!(convertView instanceof UserDetailItemView)) {
            convertView = LayoutInflater.from(context).inflate(layoutResId, root, false);
        }
        return (UserDetailItemView) convertView;
    }

    public void bind(String name, Bitmap picture, int userId) {
        this.mName.setText(name);
        this.mAvatar.setAvatarWithBadge(picture, userId);
    }

    public void bind(String name, Drawable picture, int userId) {
        this.mName.setText(name);
        this.mAvatar.setDrawableWithBadge(picture, userId);
    }

    public void setAvatarEnabled(boolean enabled) {
        this.mAvatar.setEnabled(enabled);
    }

    public void setDisabledByAdmin(boolean disabled) {
        this.mRestrictedPadlock.setVisibility(disabled ? 0 : 8);
        this.mName.setEnabled(!disabled);
        this.mAvatar.setEnabled(!disabled);
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        this.mName.setEnabled(enabled);
        this.mAvatar.setEnabled(enabled);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        this.mAvatar = (UserAvatarView) findViewById(R.id.user_picture);
        this.mName = (TextView) findViewById(R.id.user_name);
        if (this.mRegularTypeface == null) {
            this.mRegularTypeface = this.mName.getTypeface();
        }
        if (this.mActivatedTypeface == null) {
            this.mActivatedTypeface = this.mName.getTypeface();
        }
        updateTypeface();
        this.mRestrictedPadlock = findViewById(R.id.restricted_padlock);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FontSizeUtils.updateFontSize(this.mName, R.dimen.qs_detail_item_secondary_text_size);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateTypeface();
    }

    private void updateTypeface() {
        boolean activated = ArrayUtils.contains(getDrawableState(), 16843518);
        this.mName.setTypeface(activated ? this.mActivatedTypeface : this.mRegularTypeface);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }
}
