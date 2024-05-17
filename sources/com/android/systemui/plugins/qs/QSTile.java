package com.android.systemui.plugins.qs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.metrics.LogMaker;
import com.android.systemui.plugins.annotations.Dependencies;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import java.util.Objects;
import java.util.function.Supplier;
@Dependencies({@DependsOn(target = QSIconView.class), @DependsOn(target = DetailAdapter.class), @DependsOn(target = Callback.class), @DependsOn(target = Icon.class), @DependsOn(target = State.class)})
@ProvidesInterface(version = 1)
/* loaded from: classes21.dex */
public interface QSTile {
    public static final int VERSION = 1;

    @ProvidesInterface(version = 1)
    /* loaded from: classes21.dex */
    public interface Callback {
        public static final int VERSION = 1;

        void onAnnouncementRequested(CharSequence charSequence);

        void onScanStateChanged(boolean z);

        void onShowDetail(boolean z);

        void onStateChanged(State state);

        void onToggleStateChanged(boolean z);
    }

    void addCallback(Callback callback);

    void click();

    QSIconView createTileView(Context context);

    void destroy();

    DetailAdapter getDetailAdapter();

    int getMetricsCategory();

    State getState();

    CharSequence getTileLabel();

    String getTileSpec();

    boolean isAvailable();

    void longClick();

    void refreshState();

    void removeCallback(Callback callback);

    void removeCallbacks();

    void secondaryClick();

    void setDetailListening(boolean z);

    void setListening(Object obj, boolean z);

    void setTileSpec(String str);

    void userSwitch(int i);

    @Deprecated
    default void clearState() {
    }

    default LogMaker populate(LogMaker logMaker) {
        return logMaker;
    }

    @ProvidesInterface(version = 1)
    /* loaded from: classes21.dex */
    public static abstract class Icon {
        public static final int VERSION = 1;

        public abstract Drawable getDrawable(Context context);

        public Drawable getInvisibleDrawable(Context context) {
            return getDrawable(context);
        }

        public int hashCode() {
            return Icon.class.hashCode();
        }

        public int getPadding() {
            return 0;
        }
    }

    @ProvidesInterface(version = 1)
    /* loaded from: classes21.dex */
    public static class State {
        public static final int VERSION = 1;
        public CharSequence contentDescription;
        public boolean disabledByPolicy;
        public CharSequence dualLabelContentDescription;
        public String expandedAccessibilityClassName;
        public Icon icon;
        public Supplier<Icon> iconSupplier;
        public CharSequence label;
        public CharSequence secondaryLabel;
        public SlashState slash;
        public int state = 2;
        public boolean dualTarget = false;
        public boolean isTransient = false;
        public boolean handlesLongClick = true;
        public boolean showRippleEffect = true;

        public boolean copyTo(State other) {
            if (other == null) {
                throw new IllegalArgumentException();
            }
            if (!other.getClass().equals(getClass())) {
                throw new IllegalArgumentException();
            }
            boolean changed = (Objects.equals(other.icon, this.icon) && Objects.equals(other.iconSupplier, this.iconSupplier) && Objects.equals(other.label, this.label) && Objects.equals(other.secondaryLabel, this.secondaryLabel) && Objects.equals(other.contentDescription, this.contentDescription) && Objects.equals(other.dualLabelContentDescription, this.dualLabelContentDescription) && Objects.equals(other.expandedAccessibilityClassName, this.expandedAccessibilityClassName) && Objects.equals(Boolean.valueOf(other.disabledByPolicy), Boolean.valueOf(this.disabledByPolicy)) && Objects.equals(Integer.valueOf(other.state), Integer.valueOf(this.state)) && Objects.equals(Boolean.valueOf(other.isTransient), Boolean.valueOf(this.isTransient)) && Objects.equals(Boolean.valueOf(other.dualTarget), Boolean.valueOf(this.dualTarget)) && Objects.equals(other.slash, this.slash) && Objects.equals(Boolean.valueOf(other.handlesLongClick), Boolean.valueOf(this.handlesLongClick)) && Objects.equals(Boolean.valueOf(other.showRippleEffect), Boolean.valueOf(this.showRippleEffect))) ? false : true;
            other.icon = this.icon;
            other.iconSupplier = this.iconSupplier;
            other.label = this.label;
            other.secondaryLabel = this.secondaryLabel;
            other.contentDescription = this.contentDescription;
            other.dualLabelContentDescription = this.dualLabelContentDescription;
            other.expandedAccessibilityClassName = this.expandedAccessibilityClassName;
            other.disabledByPolicy = this.disabledByPolicy;
            other.state = this.state;
            other.dualTarget = this.dualTarget;
            other.isTransient = this.isTransient;
            SlashState slashState = this.slash;
            other.slash = slashState != null ? slashState.copy() : null;
            other.handlesLongClick = this.handlesLongClick;
            other.showRippleEffect = this.showRippleEffect;
            return changed;
        }

        public String toString() {
            return toStringBuilder().toString();
        }

        protected StringBuilder toStringBuilder() {
            StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append('[');
            sb.append(",icon=");
            sb.append(this.icon);
            sb.append(",iconSupplier=");
            sb.append(this.iconSupplier);
            sb.append(",label=");
            sb.append(this.label);
            sb.append(",secondaryLabel=");
            sb.append(this.secondaryLabel);
            sb.append(",contentDescription=");
            sb.append(this.contentDescription);
            sb.append(",dualLabelContentDescription=");
            sb.append(this.dualLabelContentDescription);
            sb.append(",expandedAccessibilityClassName=");
            sb.append(this.expandedAccessibilityClassName);
            sb.append(",disabledByPolicy=");
            sb.append(this.disabledByPolicy);
            sb.append(",dualTarget=");
            sb.append(this.dualTarget);
            sb.append(",isTransient=");
            sb.append(this.isTransient);
            sb.append(",state=");
            sb.append(this.state);
            sb.append(",slash=\"");
            sb.append(this.slash);
            sb.append("\"");
            sb.append(']');
            return sb;
        }

        public State copy() {
            State state = new State();
            copyTo(state);
            return state;
        }
    }

    @ProvidesInterface(version = 1)
    /* loaded from: classes21.dex */
    public static class BooleanState extends State {
        public static final int VERSION = 1;
        public boolean value;

        @Override // com.android.systemui.plugins.qs.QSTile.State
        public boolean copyTo(State other) {
            BooleanState o = (BooleanState) other;
            boolean changed = super.copyTo(other) || o.value != this.value;
            o.value = this.value;
            return changed;
        }

        @Override // com.android.systemui.plugins.qs.QSTile.State
        protected StringBuilder toStringBuilder() {
            StringBuilder rt = super.toStringBuilder();
            rt.insert(rt.length() - 1, ",value=" + this.value);
            return rt;
        }

        @Override // com.android.systemui.plugins.qs.QSTile.State
        public State copy() {
            BooleanState state = new BooleanState();
            copyTo(state);
            return state;
        }
    }

    @ProvidesInterface(version = 1)
    /* loaded from: classes21.dex */
    public static final class SignalState extends BooleanState {
        public static final int VERSION = 1;
        public boolean activityIn;
        public boolean activityOut;
        public boolean isOverlayIconWide;
        public int overlayIconId;

        @Override // com.android.systemui.plugins.qs.QSTile.BooleanState, com.android.systemui.plugins.qs.QSTile.State
        public boolean copyTo(State other) {
            SignalState o = (SignalState) other;
            boolean changed = (o.activityIn == this.activityIn && o.activityOut == this.activityOut && o.isOverlayIconWide == this.isOverlayIconWide && o.overlayIconId == this.overlayIconId) ? false : true;
            o.activityIn = this.activityIn;
            o.activityOut = this.activityOut;
            o.isOverlayIconWide = this.isOverlayIconWide;
            o.overlayIconId = this.overlayIconId;
            return super.copyTo(other) || changed;
        }

        @Override // com.android.systemui.plugins.qs.QSTile.BooleanState, com.android.systemui.plugins.qs.QSTile.State
        protected StringBuilder toStringBuilder() {
            StringBuilder rt = super.toStringBuilder();
            rt.insert(rt.length() - 1, ",activityIn=" + this.activityIn);
            rt.insert(rt.length() - 1, ",activityOut=" + this.activityOut);
            return rt;
        }

        @Override // com.android.systemui.plugins.qs.QSTile.BooleanState, com.android.systemui.plugins.qs.QSTile.State
        public State copy() {
            SignalState state = new SignalState();
            copyTo(state);
            return state;
        }
    }

    @ProvidesInterface(version = 2)
    /* loaded from: classes21.dex */
    public static class SlashState {
        public static final int VERSION = 2;
        public boolean isSlashed;
        public float rotation;

        public String toString() {
            return "isSlashed=" + this.isSlashed + ",rotation=" + this.rotation;
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            try {
                if (((SlashState) o).rotation == this.rotation) {
                    return ((SlashState) o).isSlashed == this.isSlashed;
                }
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }

        public SlashState copy() {
            SlashState state = new SlashState();
            state.rotation = this.rotation;
            state.isSlashed = this.isSlashed;
            return state;
        }
    }
}
