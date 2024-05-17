package com.android.systemui.statusbar.notification.stack;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.stack.StackScrollAlgorithm;
import com.android.systemui.statusbar.policy.ConfigurationController;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public class NotificationSectionsManager implements StackScrollAlgorithm.SectionProvider {
    private final ActivityStarter mActivityStarter;
    private final ConfigurationController mConfigurationController;
    private ExpandableNotificationRow mFirstGentleNotif;
    private SectionHeaderView mGentleHeader;
    private View.OnClickListener mOnClearGentleNotifsClickListener;
    private final NotificationStackScrollLayout mParent;
    private final StatusBarStateController mStatusBarStateController;
    private final boolean mUseMultipleSections;
    private boolean mInitialized = false;
    private boolean mGentleHeaderVisible = false;
    private final ConfigurationController.ConfigurationListener mConfigurationListener = new ConfigurationController.ConfigurationListener() { // from class: com.android.systemui.statusbar.notification.stack.NotificationSectionsManager.1
        @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
        public void onLocaleListChanged() {
            NotificationSectionsManager.this.mGentleHeader.reinflateContents();
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public NotificationSectionsManager(NotificationStackScrollLayout parent, ActivityStarter activityStarter, StatusBarStateController statusBarStateController, ConfigurationController configurationController, boolean useMultipleSections) {
        this.mParent = parent;
        this.mActivityStarter = activityStarter;
        this.mStatusBarStateController = statusBarStateController;
        this.mConfigurationController = configurationController;
        this.mUseMultipleSections = useMultipleSections;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initialize(LayoutInflater layoutInflater) {
        if (this.mInitialized) {
            throw new IllegalStateException("NotificationSectionsManager already initialized");
        }
        this.mInitialized = true;
        reinflateViews(layoutInflater);
        this.mConfigurationController.addCallback(this.mConfigurationListener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reinflateViews(LayoutInflater layoutInflater) {
        int oldPos = -1;
        SectionHeaderView sectionHeaderView = this.mGentleHeader;
        if (sectionHeaderView != null) {
            if (sectionHeaderView.getTransientContainer() != null) {
                this.mGentleHeader.getTransientContainer().removeView(this.mGentleHeader);
            } else if (this.mGentleHeader.getParent() != null) {
                oldPos = this.mParent.indexOfChild(this.mGentleHeader);
                this.mParent.removeView(this.mGentleHeader);
            }
        }
        this.mGentleHeader = (SectionHeaderView) layoutInflater.inflate(R.layout.status_bar_notification_section_header, (ViewGroup) this.mParent, false);
        this.mGentleHeader.setOnHeaderClickListener(new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationSectionsManager$Lm4LNd4tUWZPNzSmZnkDovE-xCU
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NotificationSectionsManager.this.onGentleHeaderClick(view);
            }
        });
        this.mGentleHeader.setOnClearAllClickListener(new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationSectionsManager$BXFcLGpgdZnd7PRimoedNDlJa8o
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NotificationSectionsManager.this.onClearGentleNotifsClick(view);
            }
        });
        if (oldPos != -1) {
            this.mParent.addView(this.mGentleHeader, oldPos);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnClearGentleNotifsClickListener(View.OnClickListener listener) {
        this.mOnClearGentleNotifsClickListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onUiModeChanged() {
        this.mGentleHeader.onUiModeChanged();
    }

    @Override // com.android.systemui.statusbar.notification.stack.StackScrollAlgorithm.SectionProvider
    public boolean beginsSection(View view) {
        return view == getFirstLowPriorityChild();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSectionBoundaries() {
        if (!this.mUseMultipleSections) {
            return;
        }
        this.mFirstGentleNotif = null;
        int firstGentleNotifIndex = -1;
        int n = this.mParent.getChildCount();
        int i = 0;
        while (true) {
            if (i >= n) {
                break;
            }
            View child = this.mParent.getChildAt(i);
            if ((child instanceof ExpandableNotificationRow) && child.getVisibility() != 8) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) child;
                if (!row.getEntry().isTopBucket()) {
                    firstGentleNotifIndex = i;
                    this.mFirstGentleNotif = row;
                    break;
                }
            }
            i++;
        }
        adjustGentleHeaderVisibilityAndPosition(firstGentleNotifIndex);
        this.mGentleHeader.setAreThereDismissableGentleNotifs(this.mParent.hasActiveClearableNotifications(2));
    }

    private void adjustGentleHeaderVisibilityAndPosition(int firstGentleNotifIndex) {
        boolean showGentleHeader = (firstGentleNotifIndex == -1 || this.mStatusBarStateController.getState() == 1) ? false : true;
        int currentHeaderIndex = this.mParent.indexOfChild(this.mGentleHeader);
        if (!showGentleHeader) {
            if (this.mGentleHeaderVisible) {
                this.mGentleHeaderVisible = false;
                this.mParent.removeView(this.mGentleHeader);
            }
        } else if (!this.mGentleHeaderVisible) {
            this.mGentleHeaderVisible = true;
            if (this.mGentleHeader.getTransientContainer() != null) {
                this.mGentleHeader.getTransientContainer().removeTransientView(this.mGentleHeader);
                this.mGentleHeader.setTransientContainer(null);
            }
            this.mParent.addView(this.mGentleHeader, firstGentleNotifIndex);
        } else if (currentHeaderIndex != firstGentleNotifIndex - 1) {
            int targetIndex = firstGentleNotifIndex;
            if (currentHeaderIndex < firstGentleNotifIndex) {
                targetIndex--;
            }
            this.mParent.changeViewPosition(this.mGentleHeader, targetIndex);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean updateFirstAndLastViewsInSections(NotificationSection highPrioritySection, NotificationSection lowPrioritySection, ActivatableNotificationView firstChild, ActivatableNotificationView lastChild) {
        if (this.mUseMultipleSections) {
            ActivatableNotificationView previousLastHighPriorityChild = highPrioritySection.getLastVisibleChild();
            ActivatableNotificationView previousFirstLowPriorityChild = lowPrioritySection.getFirstVisibleChild();
            ActivatableNotificationView lastHighPriorityChild = getLastHighPriorityChild();
            ActivatableNotificationView firstLowPriorityChild = getFirstLowPriorityChild();
            if (lastHighPriorityChild != null && firstLowPriorityChild != null) {
                highPrioritySection.setFirstVisibleChild(firstChild);
                highPrioritySection.setLastVisibleChild(lastHighPriorityChild);
                lowPrioritySection.setFirstVisibleChild(firstLowPriorityChild);
                lowPrioritySection.setLastVisibleChild(lastChild);
            } else if (lastHighPriorityChild != null) {
                highPrioritySection.setFirstVisibleChild(firstChild);
                highPrioritySection.setLastVisibleChild(lastChild);
                lowPrioritySection.setFirstVisibleChild(null);
                lowPrioritySection.setLastVisibleChild(null);
            } else {
                highPrioritySection.setFirstVisibleChild(null);
                highPrioritySection.setLastVisibleChild(null);
                lowPrioritySection.setFirstVisibleChild(firstChild);
                lowPrioritySection.setLastVisibleChild(lastChild);
            }
            return (lastHighPriorityChild == previousLastHighPriorityChild && firstLowPriorityChild == previousFirstLowPriorityChild) ? false : true;
        }
        highPrioritySection.setFirstVisibleChild(firstChild);
        highPrioritySection.setLastVisibleChild(lastChild);
        return false;
    }

    @VisibleForTesting
    SectionHeaderView getGentleHeaderView() {
        return this.mGentleHeader;
    }

    private ActivatableNotificationView getFirstLowPriorityChild() {
        if (this.mGentleHeaderVisible) {
            return this.mGentleHeader;
        }
        return this.mFirstGentleNotif;
    }

    private ActivatableNotificationView getLastHighPriorityChild() {
        ActivatableNotificationView lastChildBeforeGap = null;
        int childCount = this.mParent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = this.mParent.getChildAt(i);
            if (child.getVisibility() != 8 && (child instanceof ExpandableNotificationRow)) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) child;
                if (!row.getEntry().isTopBucket()) {
                    break;
                }
                lastChildBeforeGap = row;
            }
        }
        return lastChildBeforeGap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onGentleHeaderClick(View v) {
        Intent intent = new Intent("android.settings.NOTIFICATION_SETTINGS");
        this.mActivityStarter.startActivity(intent, true, true, 536870912);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClearGentleNotifsClick(View v) {
        View.OnClickListener onClickListener = this.mOnClearGentleNotifsClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(v);
        }
    }
}
