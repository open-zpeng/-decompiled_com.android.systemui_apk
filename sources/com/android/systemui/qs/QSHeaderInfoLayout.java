package com.android.systemui.qs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.badlogic.gdx.net.HttpResponseHeader;
import com.xiaopeng.speech.jarvisproto.DMEnd;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: QSHeaderInfoLayout.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u000e\u0018\u00002\u00020\u0001:\u0001!B/\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007¢\u0006\u0002\u0010\tJ\b\u0010\u0010\u001a\u00020\u0011H\u0014J0\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00072\u0006\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u0017\u001a\u00020\u00072\u0006\u0010\u0018\u001a\u00020\u0007H\u0014J\u0018\u0010\u0019\u001a\u00020\u00112\u0006\u0010\u001a\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\u0014J,\u0010\u001c\u001a\u00020\u0007*\u00020\u000b2\u0006\u0010\u001d\u001a\u00020\u00072\u0006\u0010\u001e\u001a\u00020\u00072\u0006\u0010\u001f\u001a\u00020\u00072\u0006\u0010 \u001a\u00020\u0014H\u0002R\u000e\u0010\n\u001a\u00020\u000bX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000bX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u000bX\u0082.¢\u0006\u0002\n\u0000¨\u0006\""}, d2 = {"Lcom/android/systemui/qs/QSHeaderInfoLayout;", "Landroid/widget/FrameLayout;", "context", "Landroid/content/Context;", "attrs", "Landroid/util/AttributeSet;", "defStyle", "", "defStyleRes", "(Landroid/content/Context;Landroid/util/AttributeSet;II)V", "alarmContainer", "Landroid/view/View;", "location", "Lcom/android/systemui/qs/QSHeaderInfoLayout$Location;", "ringerContainer", "statusSeparator", "onFinishInflate", "", "onLayout", "changed", "", "l", "t", "r", "b", "onMeasure", "widthMeasureSpec", "heightMeasureSpec", "layoutView", "pWidth", "pHeight", "offset", "RTL", HttpResponseHeader.Location, "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class QSHeaderInfoLayout extends FrameLayout {
    private View alarmContainer;
    private final Location location;
    private View ringerContainer;
    private View statusSeparator;

    @JvmOverloads
    public QSHeaderInfoLayout(@NotNull Context context) {
        this(context, null, 0, 0, 14, null);
    }

    @JvmOverloads
    public QSHeaderInfoLayout(@NotNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0, 0, 12, null);
    }

    @JvmOverloads
    public QSHeaderInfoLayout(@NotNull Context context, @Nullable AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0, 8, null);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    @JvmOverloads
    public QSHeaderInfoLayout(@NotNull Context context, @Nullable AttributeSet attrs, int defStyle, int defStyleRes) {
        super(context, attrs, defStyle, defStyleRes);
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.location = new Location(0, 0);
    }

    @JvmOverloads
    public /* synthetic */ QSHeaderInfoLayout(Context context, AttributeSet attributeSet, int i, int i2, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i3 & 2) != 0 ? null : attributeSet, (i3 & 4) != 0 ? 0 : i, (i3 & 8) != 0 ? 0 : i2);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        View findViewById = findViewById(R.id.alarm_container);
        Intrinsics.checkExpressionValueIsNotNull(findViewById, "findViewById(R.id.alarm_container)");
        this.alarmContainer = findViewById;
        View findViewById2 = findViewById(R.id.ringer_container);
        Intrinsics.checkExpressionValueIsNotNull(findViewById2, "findViewById(R.id.ringer_container)");
        this.ringerContainer = findViewById2;
        View findViewById3 = findViewById(R.id.status_separator);
        Intrinsics.checkExpressionValueIsNotNull(findViewById3, "findViewById(R.id.status_separator)");
        this.statusSeparator = findViewById3;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View view = this.statusSeparator;
        if (view == null) {
            Intrinsics.throwUninitializedPropertyAccessException("statusSeparator");
        }
        if (view.getVisibility() != 8) {
            boolean layoutRTL = isLayoutRtl();
            int width = r - l;
            int height = b - t;
            View view2 = this.alarmContainer;
            if (view2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("alarmContainer");
            }
            int offset = 0 + layoutView(view2, width, height, 0, layoutRTL);
            View view3 = this.statusSeparator;
            if (view3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("statusSeparator");
            }
            int offset2 = offset + layoutView(view3, width, height, offset, layoutRTL);
            View view4 = this.ringerContainer;
            if (view4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("ringerContainer");
            }
            layoutView(view4, width, height, offset2, layoutRTL);
            return;
        }
        super.onLayout(changed, l, t, r, b);
    }

    private final int layoutView(@NotNull View $receiver, int pWidth, int pHeight, int offset, boolean RTL) {
        this.location.setLocationFromOffset(pWidth, offset, $receiver.getMeasuredWidth(), RTL);
        $receiver.layout(this.location.getLeft(), 0, this.location.getRight(), pHeight);
        return $receiver.getMeasuredWidth();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), Integer.MIN_VALUE), heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        View view = this.statusSeparator;
        if (view == null) {
            Intrinsics.throwUninitializedPropertyAccessException("statusSeparator");
        }
        if (view.getVisibility() != 8) {
            View view2 = this.alarmContainer;
            if (view2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("alarmContainer");
            }
            int alarmWidth = view2.getMeasuredWidth();
            View view3 = this.statusSeparator;
            if (view3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("statusSeparator");
            }
            int separatorWidth = view3.getMeasuredWidth();
            View view4 = this.ringerContainer;
            if (view4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("ringerContainer");
            }
            int ringerWidth = view4.getMeasuredWidth();
            int availableSpace = View.MeasureSpec.getSize(width) - separatorWidth;
            if (alarmWidth < availableSpace / 2) {
                View view5 = this.ringerContainer;
                if (view5 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("ringerContainer");
                }
                measureChild(view5, View.MeasureSpec.makeMeasureSpec(Math.min(ringerWidth, availableSpace - alarmWidth), Integer.MIN_VALUE), heightMeasureSpec);
            } else if (ringerWidth < availableSpace / 2) {
                View view6 = this.alarmContainer;
                if (view6 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("alarmContainer");
                }
                measureChild(view6, View.MeasureSpec.makeMeasureSpec(Math.min(alarmWidth, availableSpace - ringerWidth), Integer.MIN_VALUE), heightMeasureSpec);
            } else {
                View view7 = this.alarmContainer;
                if (view7 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("alarmContainer");
                }
                measureChild(view7, View.MeasureSpec.makeMeasureSpec(availableSpace / 2, Integer.MIN_VALUE), heightMeasureSpec);
                View view8 = this.ringerContainer;
                if (view8 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("ringerContainer");
                }
                measureChild(view8, View.MeasureSpec.makeMeasureSpec(availableSpace / 2, Integer.MIN_VALUE), heightMeasureSpec);
            }
        }
        setMeasuredDimension(width, getMeasuredHeight());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: QSHeaderInfoLayout.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0002\u0010\u0005J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÖ\u0001J&\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00032\u0006\u0010\u0016\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\u00032\u0006\u0010\u0018\u001a\u00020\u0010J\t\u0010\u0019\u001a\u00020\u001aHÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u0007\"\u0004\b\u000b\u0010\t¨\u0006\u001b"}, d2 = {"Lcom/android/systemui/qs/QSHeaderInfoLayout$Location;", "", "left", "", "right", "(II)V", "getLeft", "()I", "setLeft", "(I)V", "getRight", "setRight", "component1", "component2", "copy", "equals", "", DMEnd.REASON_OTHER, "hashCode", "setLocationFromOffset", "", "parentWidth", "offset", "width", "RTL", "toString", "", "name"}, k = 1, mv = {1, 1, 13})
    /* loaded from: classes21.dex */
    public static final class Location {
        private int left;
        private int right;

        @NotNull
        public static /* synthetic */ Location copy$default(Location location, int i, int i2, int i3, Object obj) {
            if ((i3 & 1) != 0) {
                i = location.left;
            }
            if ((i3 & 2) != 0) {
                i2 = location.right;
            }
            return location.copy(i, i2);
        }

        public final int component1() {
            return this.left;
        }

        public final int component2() {
            return this.right;
        }

        @NotNull
        public final Location copy(int i, int i2) {
            return new Location(i, i2);
        }

        public boolean equals(@Nullable Object obj) {
            if (this != obj) {
                if (obj instanceof Location) {
                    Location location = (Location) obj;
                    if (this.left == location.left) {
                        if (this.right == location.right) {
                        }
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (Integer.hashCode(this.left) * 31) + Integer.hashCode(this.right);
        }

        @NotNull
        public String toString() {
            return "Location(left=" + this.left + ", right=" + this.right + NavigationBarInflaterView.KEY_CODE_END;
        }

        public Location(int left, int right) {
            this.left = left;
            this.right = right;
        }

        public final int getLeft() {
            return this.left;
        }

        public final int getRight() {
            return this.right;
        }

        public final void setLeft(int i) {
            this.left = i;
        }

        public final void setRight(int i) {
            this.right = i;
        }

        public final void setLocationFromOffset(int parentWidth, int offset, int width, boolean RTL) {
            if (RTL) {
                this.left = (parentWidth - offset) - width;
                this.right = parentWidth - offset;
                return;
            }
            this.left = offset;
            this.right = offset + width;
        }
    }
}
