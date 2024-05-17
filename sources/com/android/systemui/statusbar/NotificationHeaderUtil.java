package com.android.systemui.statusbar;

import android.app.Notification;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Icon;
import android.text.TextUtils;
import android.view.NotificationHeaderView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.util.ContrastColorUtil;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.NotificationContentView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
/* loaded from: classes21.dex */
public class NotificationHeaderUtil {
    private final ArrayList<HeaderProcessor> mComparators = new ArrayList<>();
    private final HashSet<Integer> mDividers = new HashSet<>();
    private final ExpandableNotificationRow mRow;
    private static final TextViewComparator sTextViewComparator = new TextViewComparator();
    private static final VisibilityApplicator sVisibilityApplicator = new VisibilityApplicator();
    private static final DataExtractor sIconExtractor = new DataExtractor() { // from class: com.android.systemui.statusbar.NotificationHeaderUtil.1
        @Override // com.android.systemui.statusbar.NotificationHeaderUtil.DataExtractor
        public Object extractData(ExpandableNotificationRow row) {
            return row.getStatusBarNotification().getNotification();
        }
    };
    private static final IconComparator sIconVisibilityComparator = new IconComparator() { // from class: com.android.systemui.statusbar.NotificationHeaderUtil.2
        @Override // com.android.systemui.statusbar.NotificationHeaderUtil.IconComparator, com.android.systemui.statusbar.NotificationHeaderUtil.ViewComparator
        public boolean compare(View parent, View child, Object parentData, Object childData) {
            return hasSameIcon(parentData, childData) && hasSameColor(parentData, childData);
        }
    };
    private static final IconComparator sGreyComparator = new IconComparator() { // from class: com.android.systemui.statusbar.NotificationHeaderUtil.3
        @Override // com.android.systemui.statusbar.NotificationHeaderUtil.IconComparator, com.android.systemui.statusbar.NotificationHeaderUtil.ViewComparator
        public boolean compare(View parent, View child, Object parentData, Object childData) {
            return !hasSameIcon(parentData, childData) || hasSameColor(parentData, childData);
        }
    };
    private static final ResultApplicator mGreyApplicator = new ResultApplicator() { // from class: com.android.systemui.statusbar.NotificationHeaderUtil.4
        @Override // com.android.systemui.statusbar.NotificationHeaderUtil.ResultApplicator
        public void apply(View view, boolean apply) {
            NotificationHeaderView header = (NotificationHeaderView) view;
            ImageView icon = (ImageView) view.findViewById(16908294);
            ImageView expand = (ImageView) view.findViewById(16908998);
            applyToChild(icon, apply, header.getOriginalIconColor());
            applyToChild(expand, apply, header.getOriginalNotificationColor());
        }

        private void applyToChild(View view, boolean shouldApply, int originalColor) {
            if (originalColor != 1) {
                ImageView imageView = (ImageView) view;
                imageView.getDrawable().mutate();
                if (shouldApply) {
                    Configuration config = view.getContext().getResources().getConfiguration();
                    boolean inNightMode = (config.uiMode & 48) == 32;
                    int grey = ContrastColorUtil.resolveColor(view.getContext(), 0, inNightMode);
                    imageView.getDrawable().setColorFilter(grey, PorterDuff.Mode.SRC_ATOP);
                    return;
                }
                imageView.getDrawable().setColorFilter(originalColor, PorterDuff.Mode.SRC_ATOP);
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public interface DataExtractor {
        Object extractData(ExpandableNotificationRow expandableNotificationRow);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public interface ResultApplicator {
        void apply(View view, boolean z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public interface ViewComparator {
        boolean compare(View view, View view2, Object obj, Object obj2);

        boolean isEmpty(View view);
    }

    public NotificationHeaderUtil(ExpandableNotificationRow row) {
        this.mRow = row;
        this.mComparators.add(new HeaderProcessor(this.mRow, 16908294, sIconExtractor, sIconVisibilityComparator, sVisibilityApplicator));
        this.mComparators.add(new HeaderProcessor(this.mRow, 16909265, sIconExtractor, sGreyComparator, mGreyApplicator));
        this.mComparators.add(new HeaderProcessor(this.mRow, 16909358, null, new ViewComparator() { // from class: com.android.systemui.statusbar.NotificationHeaderUtil.5
            @Override // com.android.systemui.statusbar.NotificationHeaderUtil.ViewComparator
            public boolean compare(View parent, View child, Object parentData, Object childData) {
                return parent.getVisibility() != 8;
            }

            @Override // com.android.systemui.statusbar.NotificationHeaderUtil.ViewComparator
            public boolean isEmpty(View view) {
                return (view instanceof ImageView) && ((ImageView) view).getDrawable() == null;
            }
        }, sVisibilityApplicator));
        this.mComparators.add(HeaderProcessor.forTextView(this.mRow, 16908830));
        this.mComparators.add(HeaderProcessor.forTextView(this.mRow, 16909076));
        this.mDividers.add(16909077);
        this.mDividers.add(16909079);
        this.mDividers.add(16909565);
    }

    public void updateChildrenHeaderAppearance() {
        List<ExpandableNotificationRow> notificationChildren = this.mRow.getNotificationChildren();
        if (notificationChildren == null) {
            return;
        }
        for (int compI = 0; compI < this.mComparators.size(); compI++) {
            this.mComparators.get(compI).init();
        }
        for (int i = 0; i < notificationChildren.size(); i++) {
            ExpandableNotificationRow row = notificationChildren.get(i);
            for (int compI2 = 0; compI2 < this.mComparators.size(); compI2++) {
                this.mComparators.get(compI2).compareToHeader(row);
            }
        }
        for (int i2 = 0; i2 < notificationChildren.size(); i2++) {
            ExpandableNotificationRow row2 = notificationChildren.get(i2);
            for (int compI3 = 0; compI3 < this.mComparators.size(); compI3++) {
                this.mComparators.get(compI3).apply(row2);
            }
            sanitizeHeaderViews(row2);
        }
    }

    private void sanitizeHeaderViews(ExpandableNotificationRow row) {
        if (row.isSummaryWithChildren()) {
            sanitizeHeader(row.getNotificationHeader());
            return;
        }
        NotificationContentView layout = row.getPrivateLayout();
        sanitizeChild(layout.getContractedChild());
        sanitizeChild(layout.getHeadsUpChild());
        sanitizeChild(layout.getExpandedChild());
    }

    private void sanitizeChild(View child) {
        if (child != null) {
            NotificationHeaderView header = (NotificationHeaderView) child.findViewById(16909265);
            sanitizeHeader(header);
        }
    }

    private void sanitizeHeader(NotificationHeaderView rowHeader) {
        int timeVisibility;
        if (rowHeader == null) {
            return;
        }
        int childCount = rowHeader.getChildCount();
        View time = rowHeader.findViewById(16909561);
        boolean hasVisibleText = false;
        int i = 1;
        while (true) {
            if (i >= childCount - 1) {
                break;
            }
            View child = rowHeader.getChildAt(i);
            if (!(child instanceof TextView) || child.getVisibility() == 8 || this.mDividers.contains(Integer.valueOf(child.getId())) || child == time) {
                i++;
            } else {
                hasVisibleText = true;
                break;
            }
        }
        if (!hasVisibleText || this.mRow.getStatusBarNotification().getNotification().showsTime()) {
            timeVisibility = 0;
        } else {
            timeVisibility = 8;
        }
        time.setVisibility(timeVisibility);
        View left = null;
        int i2 = 1;
        while (i2 < childCount - 1) {
            View child2 = rowHeader.getChildAt(i2);
            if (this.mDividers.contains(Integer.valueOf(child2.getId()))) {
                boolean visible = false;
                while (true) {
                    i2++;
                    if (i2 >= childCount - 1) {
                        break;
                    }
                    View right = rowHeader.getChildAt(i2);
                    if (this.mDividers.contains(Integer.valueOf(right.getId()))) {
                        i2--;
                        break;
                    } else if (right.getVisibility() != 8 && (right instanceof TextView)) {
                        visible = left != null;
                        left = right;
                    }
                }
                child2.setVisibility(visible ? 0 : 8);
            } else if (child2.getVisibility() != 8 && (child2 instanceof TextView)) {
                left = child2;
            }
            i2++;
        }
    }

    public void restoreNotificationHeader(ExpandableNotificationRow row) {
        for (int compI = 0; compI < this.mComparators.size(); compI++) {
            this.mComparators.get(compI).apply(row, true);
        }
        sanitizeHeaderViews(row);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class HeaderProcessor {
        private final ResultApplicator mApplicator;
        private boolean mApply;
        private ViewComparator mComparator;
        private final DataExtractor mExtractor;
        private final int mId;
        private Object mParentData;
        private final ExpandableNotificationRow mParentRow;
        private View mParentView;

        public static HeaderProcessor forTextView(ExpandableNotificationRow row, int id) {
            return new HeaderProcessor(row, id, null, NotificationHeaderUtil.sTextViewComparator, NotificationHeaderUtil.sVisibilityApplicator);
        }

        HeaderProcessor(ExpandableNotificationRow row, int id, DataExtractor extractor, ViewComparator comparator, ResultApplicator applicator) {
            this.mId = id;
            this.mExtractor = extractor;
            this.mApplicator = applicator;
            this.mComparator = comparator;
            this.mParentRow = row;
        }

        public void init() {
            this.mParentView = this.mParentRow.getNotificationHeader().findViewById(this.mId);
            DataExtractor dataExtractor = this.mExtractor;
            this.mParentData = dataExtractor == null ? null : dataExtractor.extractData(this.mParentRow);
            this.mApply = !this.mComparator.isEmpty(this.mParentView);
        }

        public void compareToHeader(ExpandableNotificationRow row) {
            NotificationHeaderView header;
            if (!this.mApply || (header = row.getContractedNotificationHeader()) == null) {
                return;
            }
            DataExtractor dataExtractor = this.mExtractor;
            Object childData = dataExtractor == null ? null : dataExtractor.extractData(row);
            this.mApply = this.mComparator.compare(this.mParentView, header.findViewById(this.mId), this.mParentData, childData);
        }

        public void apply(ExpandableNotificationRow row) {
            apply(row, false);
        }

        public void apply(ExpandableNotificationRow row, boolean reset) {
            boolean apply = this.mApply && !reset;
            if (row.isSummaryWithChildren()) {
                applyToView(apply, row.getNotificationHeader());
                return;
            }
            applyToView(apply, row.getPrivateLayout().getContractedChild());
            applyToView(apply, row.getPrivateLayout().getHeadsUpChild());
            applyToView(apply, row.getPrivateLayout().getExpandedChild());
        }

        private void applyToView(boolean apply, View parent) {
            View view;
            if (parent != null && (view = parent.findViewById(this.mId)) != null && !this.mComparator.isEmpty(view)) {
                this.mApplicator.apply(view, apply);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class TextViewComparator implements ViewComparator {
        private TextViewComparator() {
        }

        @Override // com.android.systemui.statusbar.NotificationHeaderUtil.ViewComparator
        public boolean compare(View parent, View child, Object parentData, Object childData) {
            TextView parentView = (TextView) parent;
            TextView childView = (TextView) child;
            return parentView.getText().equals(childView.getText());
        }

        @Override // com.android.systemui.statusbar.NotificationHeaderUtil.ViewComparator
        public boolean isEmpty(View view) {
            return TextUtils.isEmpty(((TextView) view).getText());
        }
    }

    /* loaded from: classes21.dex */
    private static abstract class IconComparator implements ViewComparator {
        private IconComparator() {
        }

        @Override // com.android.systemui.statusbar.NotificationHeaderUtil.ViewComparator
        public boolean compare(View parent, View child, Object parentData, Object childData) {
            return false;
        }

        protected boolean hasSameIcon(Object parentData, Object childData) {
            Icon parentIcon = ((Notification) parentData).getSmallIcon();
            Icon childIcon = ((Notification) childData).getSmallIcon();
            return parentIcon.sameAs(childIcon);
        }

        protected boolean hasSameColor(Object parentData, Object childData) {
            int parentColor = ((Notification) parentData).color;
            int childColor = ((Notification) childData).color;
            return parentColor == childColor;
        }

        @Override // com.android.systemui.statusbar.NotificationHeaderUtil.ViewComparator
        public boolean isEmpty(View view) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class VisibilityApplicator implements ResultApplicator {
        private VisibilityApplicator() {
        }

        @Override // com.android.systemui.statusbar.NotificationHeaderUtil.ResultApplicator
        public void apply(View view, boolean apply) {
            view.setVisibility(apply ? 8 : 0);
        }
    }
}
