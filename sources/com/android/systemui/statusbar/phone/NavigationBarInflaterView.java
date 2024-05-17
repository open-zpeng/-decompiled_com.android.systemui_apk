package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Space;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.ReverseLinearLayout;
import com.android.systemui.statusbar.policy.KeyButtonView;
import java.util.Objects;
/* loaded from: classes21.dex */
public class NavigationBarInflaterView extends FrameLayout implements NavigationModeController.ModeChangedListener {
    private static final String ABSOLUTE_SUFFIX = "A";
    private static final String ABSOLUTE_VERTICAL_CENTERED_SUFFIX = "C";
    public static final String BACK = "back";
    public static final String BUTTON_SEPARATOR = ",";
    public static final String CLIPBOARD = "clipboard";
    public static final String CONTEXTUAL = "contextual";
    public static final String GRAVITY_SEPARATOR = ";";
    public static final String HOME = "home";
    public static final String HOME_HANDLE = "home_handle";
    public static final String IME_SWITCHER = "ime_switcher";
    public static final String KEY = "key";
    public static final String KEY_CODE_END = ")";
    public static final String KEY_CODE_START = "(";
    public static final String KEY_IMAGE_DELIM = ":";
    public static final String LEFT = "left";
    public static final String MENU_IME_ROTATE = "menu_ime";
    public static final String NAVSPACE = "space";
    public static final String NAV_BAR_LEFT = "sysui_nav_bar_left";
    public static final String NAV_BAR_RIGHT = "sysui_nav_bar_right";
    public static final String NAV_BAR_VIEWS = "sysui_nav_bar";
    public static final String RECENT = "recent";
    public static final String RIGHT = "right";
    public static final String SIZE_MOD_END = "]";
    public static final String SIZE_MOD_START = "[";
    private static final String TAG = "NavBarInflater";
    private static final String WEIGHT_CENTERED_SUFFIX = "WC";
    private static final String WEIGHT_SUFFIX = "W";
    private boolean mAlternativeOrder;
    @VisibleForTesting
    SparseArray<ButtonDispatcher> mButtonDispatchers;
    private String mCurrentLayout;
    protected FrameLayout mHorizontal;
    private boolean mIsVertical;
    protected LayoutInflater mLandscapeInflater;
    private View mLastLandscape;
    private View mLastPortrait;
    protected LayoutInflater mLayoutInflater;
    private int mNavBarMode;
    private OverviewProxyService mOverviewProxyService;
    private boolean mUsingCustomLayout;
    protected FrameLayout mVertical;

    public NavigationBarInflaterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mNavBarMode = 0;
        createInflaters();
        this.mOverviewProxyService = (OverviewProxyService) Dependency.get(OverviewProxyService.class);
        this.mNavBarMode = ((NavigationModeController) Dependency.get(NavigationModeController.class)).addListener(this);
    }

    @VisibleForTesting
    void createInflaters() {
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
        Configuration landscape = new Configuration();
        landscape.setTo(this.mContext.getResources().getConfiguration());
        landscape.orientation = 2;
        this.mLandscapeInflater = LayoutInflater.from(this.mContext.createConfigurationContext(landscape));
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflateChildren();
        clearViews();
        inflateLayout(getDefaultLayout());
    }

    private void inflateChildren() {
        removeAllViews();
        this.mHorizontal = (FrameLayout) this.mLayoutInflater.inflate(R.layout.navigation_layout, (ViewGroup) this, false);
        addView(this.mHorizontal);
        this.mVertical = (FrameLayout) this.mLayoutInflater.inflate(R.layout.navigation_layout_vertical, (ViewGroup) this, false);
        addView(this.mVertical);
        updateAlternativeOrder();
    }

    protected String getDefaultLayout() {
        int defaultResource;
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            defaultResource = R.string.config_navBarLayoutHandle;
        } else if (this.mOverviewProxyService.shouldShowSwipeUpUI()) {
            defaultResource = R.string.config_navBarLayoutQuickstep;
        } else {
            defaultResource = R.string.config_navBarLayout;
        }
        return getContext().getString(defaultResource);
    }

    @Override // com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener
    public void onNavigationModeChanged(int mode) {
        this.mNavBarMode = mode;
        onLikelyDefaultLayoutChange();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        ((NavigationModeController) Dependency.get(NavigationModeController.class)).removeListener(this);
        super.onDetachedFromWindow();
    }

    public void setNavigationBarLayout(String layoutValue) {
        if (!Objects.equals(this.mCurrentLayout, layoutValue)) {
            this.mUsingCustomLayout = layoutValue != null;
            clearViews();
            inflateLayout(layoutValue);
        }
    }

    public void onLikelyDefaultLayoutChange() {
        if (this.mUsingCustomLayout) {
            return;
        }
        String newValue = getDefaultLayout();
        if (!Objects.equals(this.mCurrentLayout, newValue)) {
            clearViews();
            inflateLayout(newValue);
        }
    }

    public void setButtonDispatchers(SparseArray<ButtonDispatcher> buttonDispatchers) {
        this.mButtonDispatchers = buttonDispatchers;
        for (int i = 0; i < buttonDispatchers.size(); i++) {
            initiallyFill(buttonDispatchers.valueAt(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateButtonDispatchersCurrentView() {
        if (this.mButtonDispatchers != null) {
            View view = this.mIsVertical ? this.mVertical : this.mHorizontal;
            for (int i = 0; i < this.mButtonDispatchers.size(); i++) {
                ButtonDispatcher dispatcher = this.mButtonDispatchers.valueAt(i);
                dispatcher.setCurrentView(view);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setVertical(boolean vertical) {
        if (vertical != this.mIsVertical) {
            this.mIsVertical = vertical;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAlternativeOrder(boolean alternativeOrder) {
        if (alternativeOrder != this.mAlternativeOrder) {
            this.mAlternativeOrder = alternativeOrder;
            updateAlternativeOrder();
        }
    }

    private void updateAlternativeOrder() {
        updateAlternativeOrder(this.mHorizontal.findViewById(R.id.ends_group));
        updateAlternativeOrder(this.mHorizontal.findViewById(R.id.center_group));
        updateAlternativeOrder(this.mVertical.findViewById(R.id.ends_group));
        updateAlternativeOrder(this.mVertical.findViewById(R.id.center_group));
    }

    private void updateAlternativeOrder(View v) {
        if (v instanceof ReverseLinearLayout) {
            ((ReverseLinearLayout) v).setAlternativeOrder(this.mAlternativeOrder);
        }
    }

    private void initiallyFill(ButtonDispatcher buttonDispatcher) {
        addAll(buttonDispatcher, (ViewGroup) this.mHorizontal.findViewById(R.id.ends_group));
        addAll(buttonDispatcher, (ViewGroup) this.mHorizontal.findViewById(R.id.center_group));
        addAll(buttonDispatcher, (ViewGroup) this.mVertical.findViewById(R.id.ends_group));
        addAll(buttonDispatcher, (ViewGroup) this.mVertical.findViewById(R.id.center_group));
    }

    private void addAll(ButtonDispatcher buttonDispatcher, ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (parent.getChildAt(i).getId() == buttonDispatcher.getId()) {
                buttonDispatcher.addView(parent.getChildAt(i));
            }
            if (parent.getChildAt(i) instanceof ViewGroup) {
                addAll(buttonDispatcher, (ViewGroup) parent.getChildAt(i));
            }
        }
    }

    protected void inflateLayout(String newLayout) {
        this.mCurrentLayout = newLayout;
        if (newLayout == null) {
            newLayout = getDefaultLayout();
        }
        String[] sets = newLayout.split(GRAVITY_SEPARATOR, 3);
        if (sets.length != 3) {
            Log.d(TAG, "Invalid layout.");
            sets = getDefaultLayout().split(GRAVITY_SEPARATOR, 3);
        }
        String[] start = sets[0].split(",");
        String[] center = sets[1].split(",");
        String[] end = sets[2].split(",");
        inflateButtons(start, (ViewGroup) this.mHorizontal.findViewById(R.id.ends_group), false, true);
        inflateButtons(start, (ViewGroup) this.mVertical.findViewById(R.id.ends_group), true, true);
        inflateButtons(center, (ViewGroup) this.mHorizontal.findViewById(R.id.center_group), false, false);
        inflateButtons(center, (ViewGroup) this.mVertical.findViewById(R.id.center_group), true, false);
        addGravitySpacer((LinearLayout) this.mHorizontal.findViewById(R.id.ends_group));
        addGravitySpacer((LinearLayout) this.mVertical.findViewById(R.id.ends_group));
        inflateButtons(end, (ViewGroup) this.mHorizontal.findViewById(R.id.ends_group), false, false);
        inflateButtons(end, (ViewGroup) this.mVertical.findViewById(R.id.ends_group), true, false);
        updateButtonDispatchersCurrentView();
    }

    private void addGravitySpacer(LinearLayout layout) {
        layout.addView(new Space(this.mContext), new LinearLayout.LayoutParams(0, 0, 1.0f));
    }

    private void inflateButtons(String[] buttons, ViewGroup parent, boolean landscape, boolean start) {
        for (String str : buttons) {
            inflateButton(str, parent, landscape, start);
        }
    }

    private ViewGroup.LayoutParams copy(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LinearLayout.LayoutParams) {
            return new LinearLayout.LayoutParams(layoutParams.width, layoutParams.height, ((LinearLayout.LayoutParams) layoutParams).weight);
        }
        return new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
    }

    protected View inflateButton(String buttonSpec, ViewGroup parent, boolean landscape, boolean start) {
        LayoutInflater inflater = landscape ? this.mLandscapeInflater : this.mLayoutInflater;
        View v = createView(buttonSpec, parent, inflater);
        if (v == null) {
            return null;
        }
        View v2 = applySize(v, buttonSpec, landscape, start);
        parent.addView(v2);
        addToDispatchers(v2);
        View lastView = landscape ? this.mLastLandscape : this.mLastPortrait;
        View accessibilityView = v2;
        if (v2 instanceof ReverseLinearLayout.ReverseRelativeLayout) {
            accessibilityView = ((ReverseLinearLayout.ReverseRelativeLayout) v2).getChildAt(0);
        }
        if (lastView != null) {
            accessibilityView.setAccessibilityTraversalAfter(lastView.getId());
        }
        if (landscape) {
            this.mLastLandscape = accessibilityView;
        } else {
            this.mLastPortrait = accessibilityView;
        }
        return v2;
    }

    private View applySize(View v, String buttonSpec, boolean landscape, boolean start) {
        int gravity;
        String sizeStr = extractSize(buttonSpec);
        if (sizeStr == null) {
            return v;
        }
        if (sizeStr.contains(WEIGHT_SUFFIX) || sizeStr.contains(ABSOLUTE_SUFFIX)) {
            ReverseLinearLayout.ReverseRelativeLayout frame = new ReverseLinearLayout.ReverseRelativeLayout(this.mContext);
            FrameLayout.LayoutParams childParams = new FrameLayout.LayoutParams(v.getLayoutParams());
            if (landscape) {
                gravity = start ? 48 : 80;
            } else {
                gravity = start ? 8388611 : 8388613;
            }
            if (sizeStr.endsWith(WEIGHT_CENTERED_SUFFIX)) {
                gravity = 17;
            } else if (sizeStr.endsWith(ABSOLUTE_VERTICAL_CENTERED_SUFFIX)) {
                gravity = 16;
            }
            frame.setDefaultGravity(gravity);
            frame.setGravity(gravity);
            frame.addView(v, childParams);
            if (sizeStr.contains(WEIGHT_SUFFIX)) {
                float weight = Float.parseFloat(sizeStr.substring(0, sizeStr.indexOf(WEIGHT_SUFFIX)));
                frame.setLayoutParams(new LinearLayout.LayoutParams(0, -1, weight));
            } else {
                int width = (int) convertDpToPx(this.mContext, Float.parseFloat(sizeStr.substring(0, sizeStr.indexOf(ABSOLUTE_SUFFIX))));
                frame.setLayoutParams(new LinearLayout.LayoutParams(width, -1));
            }
            frame.setClipChildren(false);
            frame.setClipToPadding(false);
            return frame;
        }
        float size = Float.parseFloat(sizeStr);
        ViewGroup.LayoutParams params = v.getLayoutParams();
        params.width = (int) (params.width * size);
        return v;
    }

    private View createView(String buttonSpec, ViewGroup parent, LayoutInflater inflater) {
        String button = extractButton(buttonSpec);
        if ("left".equals(button)) {
            button = extractButton(NAVSPACE);
        } else if ("right".equals(button)) {
            button = extractButton(MENU_IME_ROTATE);
        }
        if ("home".equals(button)) {
            return inflater.inflate(R.layout.home, parent, false);
        }
        if (BACK.equals(button)) {
            return inflater.inflate(R.layout.back, parent, false);
        }
        if (RECENT.equals(button)) {
            return inflater.inflate(R.layout.recent_apps, parent, false);
        }
        if (MENU_IME_ROTATE.equals(button)) {
            return inflater.inflate(R.layout.menu_ime, parent, false);
        }
        if (NAVSPACE.equals(button)) {
            return inflater.inflate(R.layout.nav_key_space, parent, false);
        }
        if (CLIPBOARD.equals(button)) {
            return inflater.inflate(R.layout.clipboard, parent, false);
        }
        if (CONTEXTUAL.equals(button)) {
            return inflater.inflate(R.layout.contextual, parent, false);
        }
        if (HOME_HANDLE.equals(button)) {
            return inflater.inflate(R.layout.home_handle, parent, false);
        }
        if (IME_SWITCHER.equals(button)) {
            return inflater.inflate(R.layout.ime_switcher, parent, false);
        }
        if (!button.startsWith("key")) {
            return null;
        }
        String uri = extractImage(button);
        int code = extractKeycode(button);
        View v = inflater.inflate(R.layout.custom_key, parent, false);
        ((KeyButtonView) v).setCode(code);
        if (uri != null) {
            if (uri.contains(KEY_IMAGE_DELIM)) {
                ((KeyButtonView) v).loadAsync(Icon.createWithContentUri(uri));
                return v;
            } else if (uri.contains("/")) {
                int index = uri.indexOf(47);
                String pkg = uri.substring(0, index);
                int id = Integer.parseInt(uri.substring(index + 1));
                ((KeyButtonView) v).loadAsync(Icon.createWithResource(pkg, id));
                return v;
            } else {
                return v;
            }
        }
        return v;
    }

    public static String extractImage(String buttonSpec) {
        if (!buttonSpec.contains(KEY_IMAGE_DELIM)) {
            return null;
        }
        int start = buttonSpec.indexOf(KEY_IMAGE_DELIM);
        String subStr = buttonSpec.substring(start + 1, buttonSpec.indexOf(KEY_CODE_END));
        return subStr;
    }

    public static int extractKeycode(String buttonSpec) {
        if (!buttonSpec.contains(KEY_CODE_START)) {
            return 1;
        }
        int start = buttonSpec.indexOf(KEY_CODE_START);
        String subStr = buttonSpec.substring(start + 1, buttonSpec.indexOf(KEY_IMAGE_DELIM));
        return Integer.parseInt(subStr);
    }

    public static String extractSize(String buttonSpec) {
        if (!buttonSpec.contains(SIZE_MOD_START)) {
            return null;
        }
        int sizeStart = buttonSpec.indexOf(SIZE_MOD_START);
        return buttonSpec.substring(sizeStart + 1, buttonSpec.indexOf(SIZE_MOD_END));
    }

    public static String extractButton(String buttonSpec) {
        if (!buttonSpec.contains(SIZE_MOD_START)) {
            return buttonSpec;
        }
        return buttonSpec.substring(0, buttonSpec.indexOf(SIZE_MOD_START));
    }

    private void addToDispatchers(View v) {
        SparseArray<ButtonDispatcher> sparseArray = this.mButtonDispatchers;
        if (sparseArray != null) {
            int indexOfKey = sparseArray.indexOfKey(v.getId());
            if (indexOfKey >= 0) {
                this.mButtonDispatchers.valueAt(indexOfKey).addView(v);
            }
            if (v instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) v;
                int N = viewGroup.getChildCount();
                for (int i = 0; i < N; i++) {
                    addToDispatchers(viewGroup.getChildAt(i));
                }
            }
        }
    }

    private void clearViews() {
        if (this.mButtonDispatchers != null) {
            for (int i = 0; i < this.mButtonDispatchers.size(); i++) {
                this.mButtonDispatchers.valueAt(i).clear();
            }
        }
        clearAllChildren((ViewGroup) this.mHorizontal.findViewById(R.id.nav_buttons));
        clearAllChildren((ViewGroup) this.mVertical.findViewById(R.id.nav_buttons));
    }

    private void clearAllChildren(ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            ((ViewGroup) group.getChildAt(i)).removeAllViews();
        }
    }

    private static float convertDpToPx(Context context, float dp) {
        return context.getResources().getDisplayMetrics().density * dp;
    }
}
