package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.VisibleForTesting;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.DemoMode;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.StatusBarMobileView;
import com.android.systemui.statusbar.StatusBarWifiView;
import com.android.systemui.statusbar.StatusIconDisplayable;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy;
import com.android.systemui.util.Utils;
import java.util.List;
/* loaded from: classes21.dex */
public interface StatusBarIconController {
    public static final String ICON_BLACKLIST = "icon_blacklist";
    public static final int TAG_PRIMARY = 0;

    void addIconGroup(IconManager iconManager);

    void removeAllIconsForSlot(String str);

    void removeIcon(String str, int i);

    void removeIconGroup(IconManager iconManager);

    void setExternalIcon(String str);

    void setIcon(String str, int i, CharSequence charSequence);

    void setIcon(String str, StatusBarIcon statusBarIcon);

    void setIconVisibility(String str, boolean z);

    void setMobileIcons(String str, List<StatusBarSignalPolicy.MobileIconState> list);

    void setSignalIcon(String str, StatusBarSignalPolicy.WifiIconState wifiIconState);

    static ArraySet<String> getIconBlacklist(String blackListStr) {
        ArraySet<String> ret = new ArraySet<>();
        if (blackListStr == null) {
            blackListStr = "rotate,headset";
        }
        String[] blacklist = blackListStr.split(",");
        for (String slot : blacklist) {
            if (!TextUtils.isEmpty(slot)) {
                ret.add(slot);
            }
        }
        return ret;
    }

    /* loaded from: classes21.dex */
    public static class DarkIconManager extends IconManager {
        private final DarkIconDispatcher mDarkIconDispatcher;
        private int mIconHPadding;

        public DarkIconManager(LinearLayout linearLayout) {
            super(linearLayout);
            this.mIconHPadding = this.mContext.getResources().getDimensionPixelSize(R.dimen.status_bar_icon_padding);
            this.mDarkIconDispatcher = (DarkIconDispatcher) Dependency.get(DarkIconDispatcher.class);
        }

        @Override // com.android.systemui.statusbar.phone.StatusBarIconController.IconManager
        protected void onIconAdded(int index, String slot, boolean blocked, StatusBarIconHolder holder) {
            StatusIconDisplayable view = addHolder(index, slot, blocked, holder);
            this.mDarkIconDispatcher.addDarkReceiver(view);
        }

        @Override // com.android.systemui.statusbar.phone.StatusBarIconController.IconManager
        protected LinearLayout.LayoutParams onCreateLayoutParams() {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, this.mIconSize);
            int i = this.mIconHPadding;
            lp.setMargins(i, 0, i, 0);
            return lp;
        }

        @Override // com.android.systemui.statusbar.phone.StatusBarIconController.IconManager
        protected void destroy() {
            for (int i = 0; i < this.mGroup.getChildCount(); i++) {
                this.mDarkIconDispatcher.removeDarkReceiver((DarkIconDispatcher.DarkReceiver) this.mGroup.getChildAt(i));
            }
            this.mGroup.removeAllViews();
        }

        @Override // com.android.systemui.statusbar.phone.StatusBarIconController.IconManager
        protected void onRemoveIcon(int viewIndex) {
            this.mDarkIconDispatcher.removeDarkReceiver((DarkIconDispatcher.DarkReceiver) this.mGroup.getChildAt(viewIndex));
            super.onRemoveIcon(viewIndex);
        }

        @Override // com.android.systemui.statusbar.phone.StatusBarIconController.IconManager
        public void onSetIcon(int viewIndex, StatusBarIcon icon) {
            super.onSetIcon(viewIndex, icon);
            this.mDarkIconDispatcher.applyDark((DarkIconDispatcher.DarkReceiver) this.mGroup.getChildAt(viewIndex));
        }

        @Override // com.android.systemui.statusbar.phone.StatusBarIconController.IconManager
        protected DemoStatusIcons createDemoStatusIcons() {
            DemoStatusIcons icons = super.createDemoStatusIcons();
            this.mDarkIconDispatcher.addDarkReceiver(icons);
            return icons;
        }

        @Override // com.android.systemui.statusbar.phone.StatusBarIconController.IconManager
        protected void exitDemoMode() {
            this.mDarkIconDispatcher.removeDarkReceiver(this.mDemoStatusIcons);
            super.exitDemoMode();
        }
    }

    /* loaded from: classes21.dex */
    public static class TintedIconManager extends IconManager {
        private int mColor;

        public TintedIconManager(ViewGroup group) {
            super(group);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.systemui.statusbar.phone.StatusBarIconController.IconManager
        public void onIconAdded(int index, String slot, boolean blocked, StatusBarIconHolder holder) {
            StatusIconDisplayable view = addHolder(index, slot, blocked, holder);
            view.setStaticDrawableColor(this.mColor);
            view.setDecorColor(this.mColor);
        }

        public void setTint(int color) {
            this.mColor = color;
            for (int i = 0; i < this.mGroup.getChildCount(); i++) {
                View child = this.mGroup.getChildAt(i);
                if (child instanceof StatusIconDisplayable) {
                    StatusIconDisplayable icon = (StatusIconDisplayable) child;
                    icon.setStaticDrawableColor(this.mColor);
                    icon.setDecorColor(this.mColor);
                }
            }
        }

        @Override // com.android.systemui.statusbar.phone.StatusBarIconController.IconManager
        protected DemoStatusIcons createDemoStatusIcons() {
            DemoStatusIcons icons = super.createDemoStatusIcons();
            icons.setColor(this.mColor);
            return icons;
        }
    }

    /* loaded from: classes21.dex */
    public static class IconManager implements DemoMode {
        protected final Context mContext;
        protected DemoStatusIcons mDemoStatusIcons;
        protected final ViewGroup mGroup;
        protected final int mIconSize;
        private boolean mIsInDemoMode;
        protected boolean mShouldLog = false;
        protected boolean mDemoable = true;

        public IconManager(ViewGroup group) {
            this.mGroup = group;
            this.mContext = group.getContext();
            this.mIconSize = this.mContext.getResources().getDimensionPixelSize(17105441);
            Utils.DisableStateTracker tracker = new Utils.DisableStateTracker(0, 2);
            this.mGroup.addOnAttachStateChangeListener(tracker);
            if (this.mGroup.isAttachedToWindow()) {
                tracker.onViewAttachedToWindow(this.mGroup);
            }
        }

        public boolean isDemoable() {
            return this.mDemoable;
        }

        public void setIsDemoable(boolean demoable) {
            this.mDemoable = demoable;
        }

        public void setShouldLog(boolean should) {
            this.mShouldLog = should;
        }

        public boolean shouldLog() {
            return this.mShouldLog;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onIconAdded(int index, String slot, boolean blocked, StatusBarIconHolder holder) {
            addHolder(index, slot, blocked, holder);
        }

        protected StatusIconDisplayable addHolder(int index, String slot, boolean blocked, StatusBarIconHolder holder) {
            int type = holder.getType();
            if (type != 0) {
                if (type != 1) {
                    if (type == 2) {
                        return addMobileIcon(index, slot, holder.getMobileState());
                    }
                    return null;
                }
                return addSignalIcon(index, slot, holder.getWifiState());
            }
            return addIcon(index, slot, blocked, holder.getIcon());
        }

        @VisibleForTesting
        protected StatusBarIconView addIcon(int index, String slot, boolean blocked, StatusBarIcon icon) {
            StatusBarIconView view = onCreateStatusBarIconView(slot, blocked);
            view.set(icon);
            this.mGroup.addView(view, index, onCreateLayoutParams());
            return view;
        }

        @VisibleForTesting
        protected StatusBarWifiView addSignalIcon(int index, String slot, StatusBarSignalPolicy.WifiIconState state) {
            StatusBarWifiView view = onCreateStatusBarWifiView(slot);
            view.applyWifiState(state);
            this.mGroup.addView(view, index, onCreateLayoutParams());
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.addDemoWifiView(state);
            }
            return view;
        }

        @VisibleForTesting
        protected StatusBarMobileView addMobileIcon(int index, String slot, StatusBarSignalPolicy.MobileIconState state) {
            StatusBarMobileView view = onCreateStatusBarMobileView(slot);
            view.applyMobileState(state);
            this.mGroup.addView(view, index, onCreateLayoutParams());
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.addMobileView(state);
            }
            return view;
        }

        private StatusBarIconView onCreateStatusBarIconView(String slot, boolean blocked) {
            return new StatusBarIconView(this.mContext, slot, null, blocked);
        }

        private StatusBarWifiView onCreateStatusBarWifiView(String slot) {
            StatusBarWifiView view = StatusBarWifiView.fromContext(this.mContext, slot);
            return view;
        }

        private StatusBarMobileView onCreateStatusBarMobileView(String slot) {
            StatusBarMobileView view = StatusBarMobileView.fromContext(this.mContext, slot);
            return view;
        }

        protected LinearLayout.LayoutParams onCreateLayoutParams() {
            return new LinearLayout.LayoutParams(-2, this.mIconSize);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void destroy() {
            this.mGroup.removeAllViews();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onIconExternal(int viewIndex, int height) {
            ImageView imageView = (ImageView) this.mGroup.getChildAt(viewIndex);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setAdjustViewBounds(true);
            setHeightAndCenter(imageView, height);
        }

        protected void onDensityOrFontScaleChanged() {
            for (int i = 0; i < this.mGroup.getChildCount(); i++) {
                View child = this.mGroup.getChildAt(i);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, this.mIconSize);
                child.setLayoutParams(lp);
            }
        }

        private void setHeightAndCenter(ImageView imageView, int height) {
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.height = height;
            if (params instanceof LinearLayout.LayoutParams) {
                ((LinearLayout.LayoutParams) params).gravity = 16;
            }
            imageView.setLayoutParams(params);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onRemoveIcon(int viewIndex) {
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.onRemoveIcon((StatusIconDisplayable) this.mGroup.getChildAt(viewIndex));
            }
            this.mGroup.removeViewAt(viewIndex);
        }

        public void onSetIcon(int viewIndex, StatusBarIcon icon) {
            StatusBarIconView view = (StatusBarIconView) this.mGroup.getChildAt(viewIndex);
            view.set(icon);
        }

        public void onSetIconHolder(int viewIndex, StatusBarIconHolder holder) {
            int type = holder.getType();
            if (type == 0) {
                onSetIcon(viewIndex, holder.getIcon());
            } else if (type == 1) {
                onSetSignalIcon(viewIndex, holder.getWifiState());
            } else if (type == 2) {
                onSetMobileIcon(viewIndex, holder.getMobileState());
            }
        }

        public void onSetSignalIcon(int viewIndex, StatusBarSignalPolicy.WifiIconState state) {
            StatusBarWifiView wifiView = (StatusBarWifiView) this.mGroup.getChildAt(viewIndex);
            if (wifiView != null) {
                wifiView.applyWifiState(state);
            }
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.updateWifiState(state);
            }
        }

        public void onSetMobileIcon(int viewIndex, StatusBarSignalPolicy.MobileIconState state) {
            StatusBarMobileView view = (StatusBarMobileView) this.mGroup.getChildAt(viewIndex);
            if (view != null) {
                view.applyMobileState(state);
            }
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.updateMobileState(state);
            }
        }

        @Override // com.android.systemui.DemoMode
        public void dispatchDemoCommand(String command, Bundle args) {
            if (!this.mDemoable) {
                return;
            }
            if (command.equals(DemoMode.COMMAND_EXIT)) {
                DemoStatusIcons demoStatusIcons = this.mDemoStatusIcons;
                if (demoStatusIcons != null) {
                    demoStatusIcons.dispatchDemoCommand(command, args);
                    exitDemoMode();
                }
                this.mIsInDemoMode = false;
                return;
            }
            if (this.mDemoStatusIcons == null) {
                this.mIsInDemoMode = true;
                this.mDemoStatusIcons = createDemoStatusIcons();
            }
            this.mDemoStatusIcons.dispatchDemoCommand(command, args);
        }

        protected void exitDemoMode() {
            this.mDemoStatusIcons.remove();
            this.mDemoStatusIcons = null;
        }

        protected DemoStatusIcons createDemoStatusIcons() {
            return new DemoStatusIcons((LinearLayout) this.mGroup, this.mIconSize);
        }
    }
}
