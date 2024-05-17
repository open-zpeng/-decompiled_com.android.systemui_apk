package com.xiaopeng.xui.widget.prompt;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import com.xiaopeng.xpui.R;
@Deprecated
/* loaded from: classes25.dex */
public class XPrompt extends Prompt {
    private ViewGroup mHostView;

    public XPrompt(@NonNull Context context) {
        super(context);
    }

    @UiThread
    public static XPrompt makePrompt(@NonNull Activity activity) {
        return makePrompt(activity, findHostViewFromActivity(activity));
    }

    @UiThread
    public static XPrompt makePrompt(@NonNull Activity activity, @NonNull CharSequence text) {
        return makePrompt(activity, text, 0);
    }

    @UiThread
    public static XPrompt makePrompt(@NonNull Activity activity, @NonNull CharSequence text, int duration) {
        return makePrompt(activity, findHostViewFromActivity(activity), text, duration);
    }

    @UiThread
    public static XPrompt makePrompt(@NonNull Context context, @NonNull ViewGroup hostView, @NonNull CharSequence text, int duration) {
        XPrompt xPrompt = makePrompt(context, hostView);
        xPrompt.addMessage(new XPromptMessage(duration, text));
        return xPrompt;
    }

    private static ViewGroup findHostViewFromActivity(@NonNull Activity activity) {
        return (ViewGroup) activity.findViewById(16908290);
    }

    public static XPrompt makePrompt(@NonNull Context context, @NonNull ViewGroup hostView) {
        XPromptView xPromptView = (XPromptView) hostView.findViewById(R.id.x_prompt);
        XPrompt xPrompt = null;
        if (xPromptView != null) {
            xPrompt = (XPrompt) xPromptView.getPrompt();
        }
        if (xPrompt == null) {
            xPrompt = new XPrompt(context);
        }
        xPrompt.setHostView(hostView);
        return xPrompt;
    }

    @UiThread
    public XPrompt setHostView(@NonNull Activity activity) {
        setHostView(findHostViewFromActivity(activity));
        return this;
    }

    @UiThread
    public XPrompt setHostView(@NonNull ViewGroup hostView) {
        ViewGroup viewGroup = this.mHostView;
        if (viewGroup != null && !viewGroup.equals(hostView)) {
            this.mHostView.removeView(this.mXPromptView);
        }
        this.mHostView = hostView;
        return this;
    }

    @Override // com.xiaopeng.xui.widget.prompt.Prompt
    protected boolean addView() {
        ViewGroup viewGroup = this.mHostView;
        if (viewGroup != null) {
            viewGroup.addView(this.mXPromptView);
            return true;
        }
        return false;
    }

    @Override // com.xiaopeng.xui.widget.prompt.Prompt
    protected void removeView() {
        if (this.mXPromptView.getParent() instanceof ViewGroup) {
            ((ViewGroup) this.mXPromptView.getParent()).removeView(this.mXPromptView);
        }
    }
}
