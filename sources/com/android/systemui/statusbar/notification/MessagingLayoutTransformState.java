package com.android.systemui.statusbar.notification;

import android.content.res.Resources;
import android.util.Pools;
import android.view.View;
import com.android.internal.widget.MessagingGroup;
import com.android.internal.widget.MessagingImageMessage;
import com.android.internal.widget.MessagingLayout;
import com.android.internal.widget.MessagingLinearLayout;
import com.android.internal.widget.MessagingMessage;
import com.android.internal.widget.MessagingPropertyAnimator;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.notification.TransformState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes21.dex */
public class MessagingLayoutTransformState extends TransformState {
    private static Pools.SimplePool<MessagingLayoutTransformState> sInstancePool = new Pools.SimplePool<>(40);
    private HashMap<MessagingGroup, MessagingGroup> mGroupMap = new HashMap<>();
    private MessagingLinearLayout mMessageContainer;
    private MessagingLayout mMessagingLayout;
    private float mRelativeTranslationOffset;

    public static MessagingLayoutTransformState obtain() {
        MessagingLayoutTransformState instance = (MessagingLayoutTransformState) sInstancePool.acquire();
        if (instance != null) {
            return instance;
        }
        return new MessagingLayoutTransformState();
    }

    @Override // com.android.systemui.statusbar.notification.TransformState
    public void initFrom(View view, TransformState.TransformInfo transformInfo) {
        super.initFrom(view, transformInfo);
        if (this.mTransformedView instanceof MessagingLinearLayout) {
            this.mMessageContainer = this.mTransformedView;
            this.mMessagingLayout = this.mMessageContainer.getMessagingLayout();
            Resources resources = view.getContext().getResources();
            this.mRelativeTranslationOffset = resources.getDisplayMetrics().density * 8.0f;
        }
    }

    @Override // com.android.systemui.statusbar.notification.TransformState
    public boolean transformViewTo(TransformState otherState, float transformationAmount) {
        if (otherState instanceof MessagingLayoutTransformState) {
            transformViewInternal((MessagingLayoutTransformState) otherState, transformationAmount, true);
            return true;
        }
        return super.transformViewTo(otherState, transformationAmount);
    }

    @Override // com.android.systemui.statusbar.notification.TransformState
    public void transformViewFrom(TransformState otherState, float transformationAmount) {
        if (otherState instanceof MessagingLayoutTransformState) {
            transformViewInternal((MessagingLayoutTransformState) otherState, transformationAmount, false);
        } else {
            super.transformViewFrom(otherState, transformationAmount);
        }
    }

    private void transformViewInternal(MessagingLayoutTransformState mlt, float transformationAmount, boolean to) {
        float groupTransformationAmount;
        float f = transformationAmount;
        ensureVisible();
        ArrayList<MessagingGroup> ownGroups = filterHiddenGroups(this.mMessagingLayout.getMessagingGroups());
        ArrayList<MessagingGroup> otherGroups = filterHiddenGroups(mlt.mMessagingLayout.getMessagingGroups());
        HashMap<MessagingGroup, MessagingGroup> pairs = findPairs(ownGroups, otherGroups);
        MessagingGroup lastPairedGroup = null;
        float currentTranslation = 0.0f;
        int i = ownGroups.size() - 1;
        while (i >= 0) {
            MessagingGroup ownGroup = ownGroups.get(i);
            MessagingGroup matchingGroup = pairs.get(ownGroup);
            if (!isGone(ownGroup)) {
                if (matchingGroup != null) {
                    transformGroups(ownGroup, matchingGroup, f, to);
                    if (lastPairedGroup == null) {
                        lastPairedGroup = ownGroup;
                        if (to) {
                            float totalTranslation = ownGroup.getTop() - matchingGroup.getTop();
                            float currentTranslation2 = matchingGroup.getAvatar().getTranslationY() - totalTranslation;
                            currentTranslation = currentTranslation2;
                        } else {
                            currentTranslation = ownGroup.getAvatar().getTranslationY();
                        }
                    }
                } else {
                    float groupTransformationAmount2 = transformationAmount;
                    if (lastPairedGroup != null) {
                        adaptGroupAppear(ownGroup, f, currentTranslation, to);
                        float newPosition = ownGroup.getTop() + currentTranslation;
                        if (!this.mTransformInfo.isAnimating()) {
                            float fadeStart = (-ownGroup.getHeight()) * 0.5f;
                            groupTransformationAmount = (newPosition - fadeStart) / Math.abs(fadeStart);
                        } else {
                            float fadeStart2 = (-ownGroup.getHeight()) * 0.75f;
                            groupTransformationAmount = (newPosition - fadeStart2) / (Math.abs(fadeStart2) + ownGroup.getTop());
                        }
                        float groupTransformationAmount3 = Math.max(0.0f, Math.min(1.0f, groupTransformationAmount));
                        if (!to) {
                            groupTransformationAmount2 = groupTransformationAmount3;
                        } else {
                            groupTransformationAmount2 = 1.0f - groupTransformationAmount3;
                        }
                    }
                    if (to) {
                        disappear(ownGroup, groupTransformationAmount2);
                    } else {
                        appear(ownGroup, groupTransformationAmount2);
                    }
                }
            }
            i--;
            f = transformationAmount;
        }
    }

    private void appear(MessagingGroup ownGroup, float transformationAmount) {
        MessagingLinearLayout ownMessages = ownGroup.getMessageContainer();
        for (int j = 0; j < ownMessages.getChildCount(); j++) {
            View child = ownMessages.getChildAt(j);
            if (!isGone(child)) {
                appear(child, transformationAmount);
                setClippingDeactivated(child, true);
            }
        }
        appear(ownGroup.getAvatar(), transformationAmount);
        appear(ownGroup.getSenderView(), transformationAmount);
        appear((View) ownGroup.getIsolatedMessage(), transformationAmount);
        setClippingDeactivated(ownGroup.getSenderView(), true);
        setClippingDeactivated(ownGroup.getAvatar(), true);
    }

    private void adaptGroupAppear(MessagingGroup ownGroup, float transformationAmount, float overallTranslation, boolean to) {
        float relativeOffset;
        if (to) {
            relativeOffset = this.mRelativeTranslationOffset * transformationAmount;
        } else {
            relativeOffset = (1.0f - transformationAmount) * this.mRelativeTranslationOffset;
        }
        if (ownGroup.getSenderView().getVisibility() != 8) {
            relativeOffset *= 0.5f;
        }
        ownGroup.getMessageContainer().setTranslationY(relativeOffset);
        ownGroup.getSenderView().setTranslationY(relativeOffset);
        ownGroup.setTranslationY(0.9f * overallTranslation);
    }

    private void disappear(MessagingGroup ownGroup, float transformationAmount) {
        MessagingLinearLayout ownMessages = ownGroup.getMessageContainer();
        for (int j = 0; j < ownMessages.getChildCount(); j++) {
            View child = ownMessages.getChildAt(j);
            if (!isGone(child)) {
                disappear(child, transformationAmount);
                setClippingDeactivated(child, true);
            }
        }
        disappear(ownGroup.getAvatar(), transformationAmount);
        disappear(ownGroup.getSenderView(), transformationAmount);
        disappear((View) ownGroup.getIsolatedMessage(), transformationAmount);
        setClippingDeactivated(ownGroup.getSenderView(), true);
        setClippingDeactivated(ownGroup.getAvatar(), true);
    }

    private void appear(View child, float transformationAmount) {
        if (child == null || child.getVisibility() == 8) {
            return;
        }
        TransformState ownState = TransformState.createFrom(child, this.mTransformInfo);
        ownState.appear(transformationAmount, null);
        ownState.recycle();
    }

    private void disappear(View child, float transformationAmount) {
        if (child == null || child.getVisibility() == 8) {
            return;
        }
        TransformState ownState = TransformState.createFrom(child, this.mTransformInfo);
        ownState.disappear(transformationAmount, null);
        ownState.recycle();
    }

    private ArrayList<MessagingGroup> filterHiddenGroups(ArrayList<MessagingGroup> groups) {
        ArrayList<MessagingGroup> result = new ArrayList<>(groups);
        int i = 0;
        while (i < result.size()) {
            MessagingGroup messagingGroup = result.get(i);
            if (isGone(messagingGroup)) {
                result.remove(i);
                i--;
            }
            i++;
        }
        return result;
    }

    private void transformGroups(MessagingGroup ownGroup, MessagingGroup otherGroup, float transformationAmount, boolean to) {
        View otherChild;
        boolean useLinearTransformation = otherGroup.getIsolatedMessage() == null && !this.mTransformInfo.isAnimating();
        transformView(transformationAmount, to, ownGroup.getSenderView(), otherGroup.getSenderView(), true, useLinearTransformation);
        transformView(transformationAmount, to, ownGroup.getAvatar(), otherGroup.getAvatar(), true, useLinearTransformation);
        List<MessagingMessage> ownMessages = ownGroup.getMessages();
        List<MessagingMessage> otherMessages = otherGroup.getMessages();
        float previousTranslation = 0.0f;
        float previousTranslation2 = transformationAmount;
        for (int i = 0; i < ownMessages.size(); i++) {
            MessagingImageMessage view = ownMessages.get((ownMessages.size() - 1) - i).getView();
            if (!isGone(view)) {
                int otherIndex = (otherMessages.size() - 1) - i;
                if (otherIndex < 0) {
                    otherChild = null;
                } else {
                    View otherChild2 = otherMessages.get(otherIndex).getView();
                    if (!isGone(otherChild2)) {
                        otherChild = otherChild2;
                    } else {
                        otherChild = null;
                    }
                }
                if (otherChild == null && previousTranslation < 0.0f) {
                    float distanceToTop = view.getTop() + view.getHeight() + previousTranslation;
                    previousTranslation2 = Math.max(0.0f, Math.min(1.0f, distanceToTop / view.getHeight()));
                    if (to) {
                        previousTranslation2 = 1.0f - previousTranslation2;
                    }
                }
                View otherChild3 = otherChild;
                transformView(previousTranslation2, to, view, otherChild, false, useLinearTransformation);
                boolean otherIsIsolated = otherGroup.getIsolatedMessage() == otherChild3;
                if (previousTranslation2 == 0.0f && otherIsIsolated) {
                    ownGroup.setTransformingImages(true);
                }
                if (otherChild3 == null) {
                    view.setTranslationY(previousTranslation);
                    setClippingDeactivated(view, true);
                } else if (ownGroup.getIsolatedMessage() != view && !otherIsIsolated) {
                    if (to) {
                        float totalTranslation = ((view.getTop() + ownGroup.getTop()) - otherChild3.getTop()) - otherChild3.getTop();
                        float previousTranslation3 = otherChild3.getTranslationY() - totalTranslation;
                        previousTranslation = previousTranslation3;
                    } else {
                        previousTranslation = view.getTranslationY();
                    }
                }
            }
        }
        ownGroup.updateClipRect();
    }

    private void transformView(float transformationAmount, boolean to, View ownView, View otherView, boolean sameAsAny, boolean useLinearTransformation) {
        TransformState ownState = TransformState.createFrom(ownView, this.mTransformInfo);
        if (useLinearTransformation) {
            ownState.setDefaultInterpolator(Interpolators.LINEAR);
        }
        ownState.setIsSameAsAnyView(sameAsAny);
        if (to) {
            if (otherView != null) {
                TransformState otherState = TransformState.createFrom(otherView, this.mTransformInfo);
                ownState.transformViewTo(otherState, transformationAmount);
                otherState.recycle();
            } else {
                ownState.disappear(transformationAmount, null);
            }
        } else if (otherView != null) {
            TransformState otherState2 = TransformState.createFrom(otherView, this.mTransformInfo);
            ownState.transformViewFrom(otherState2, transformationAmount);
            otherState2.recycle();
        } else {
            ownState.appear(transformationAmount, null);
        }
        ownState.recycle();
    }

    private HashMap<MessagingGroup, MessagingGroup> findPairs(ArrayList<MessagingGroup> ownGroups, ArrayList<MessagingGroup> otherGroups) {
        this.mGroupMap.clear();
        int lastMatch = Integer.MAX_VALUE;
        for (int i = ownGroups.size() - 1; i >= 0; i--) {
            MessagingGroup ownGroup = ownGroups.get(i);
            MessagingGroup bestMatch = null;
            int bestCompatibility = 0;
            for (int j = Math.min(otherGroups.size(), lastMatch) - 1; j >= 0; j--) {
                MessagingGroup otherGroup = otherGroups.get(j);
                int compatibility = ownGroup.calculateGroupCompatibility(otherGroup);
                if (compatibility > bestCompatibility) {
                    bestCompatibility = compatibility;
                    bestMatch = otherGroup;
                    lastMatch = j;
                }
            }
            if (bestMatch != null) {
                this.mGroupMap.put(ownGroup, bestMatch);
            }
        }
        return this.mGroupMap;
    }

    private boolean isGone(View view) {
        if (view.getVisibility() == 8) {
            return true;
        }
        MessagingLinearLayout.LayoutParams layoutParams = view.getLayoutParams();
        return (layoutParams instanceof MessagingLinearLayout.LayoutParams) && layoutParams.hide;
    }

    @Override // com.android.systemui.statusbar.notification.TransformState
    public void setVisible(boolean visible, boolean force) {
        super.setVisible(visible, force);
        resetTransformedView();
        ArrayList<MessagingGroup> ownGroups = this.mMessagingLayout.getMessagingGroups();
        for (int i = 0; i < ownGroups.size(); i++) {
            MessagingGroup ownGroup = ownGroups.get(i);
            if (!isGone(ownGroup)) {
                MessagingLinearLayout ownMessages = ownGroup.getMessageContainer();
                for (int j = 0; j < ownMessages.getChildCount(); j++) {
                    View child = ownMessages.getChildAt(j);
                    setVisible(child, visible, force);
                }
                setVisible(ownGroup.getAvatar(), visible, force);
                setVisible(ownGroup.getSenderView(), visible, force);
                MessagingImageMessage isolatedMessage = ownGroup.getIsolatedMessage();
                if (isolatedMessage != null) {
                    setVisible(isolatedMessage, visible, force);
                }
            }
        }
    }

    private void setVisible(View child, boolean visible, boolean force) {
        if (isGone(child) || MessagingPropertyAnimator.isAnimatingAlpha(child)) {
            return;
        }
        TransformState ownState = TransformState.createFrom(child, this.mTransformInfo);
        ownState.setVisible(visible, force);
        ownState.recycle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.TransformState
    public void resetTransformedView() {
        super.resetTransformedView();
        ArrayList<MessagingGroup> ownGroups = this.mMessagingLayout.getMessagingGroups();
        for (int i = 0; i < ownGroups.size(); i++) {
            MessagingGroup ownGroup = ownGroups.get(i);
            if (!isGone(ownGroup)) {
                MessagingLinearLayout ownMessages = ownGroup.getMessageContainer();
                for (int j = 0; j < ownMessages.getChildCount(); j++) {
                    View child = ownMessages.getChildAt(j);
                    if (!isGone(child)) {
                        resetTransformedView(child);
                        setClippingDeactivated(child, false);
                    }
                }
                resetTransformedView(ownGroup.getAvatar());
                resetTransformedView(ownGroup.getSenderView());
                MessagingImageMessage isolatedMessage = ownGroup.getIsolatedMessage();
                if (isolatedMessage != null) {
                    resetTransformedView(isolatedMessage);
                }
                setClippingDeactivated(ownGroup.getAvatar(), false);
                setClippingDeactivated(ownGroup.getSenderView(), false);
                ownGroup.setTranslationY(0.0f);
                ownGroup.getMessageContainer().setTranslationY(0.0f);
                ownGroup.getSenderView().setTranslationY(0.0f);
            }
            ownGroup.setTransformingImages(false);
            ownGroup.updateClipRect();
        }
    }

    @Override // com.android.systemui.statusbar.notification.TransformState
    public void prepareFadeIn() {
        super.prepareFadeIn();
        setVisible(true, false);
    }

    private void resetTransformedView(View child) {
        TransformState ownState = TransformState.createFrom(child, this.mTransformInfo);
        ownState.resetTransformedView();
        ownState.recycle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.TransformState
    public void reset() {
        super.reset();
        this.mMessageContainer = null;
        this.mMessagingLayout = null;
    }

    @Override // com.android.systemui.statusbar.notification.TransformState
    public void recycle() {
        super.recycle();
        this.mGroupMap.clear();
        sInstancePool.release(this);
    }
}
