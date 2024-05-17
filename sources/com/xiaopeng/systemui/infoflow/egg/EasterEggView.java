package com.xiaopeng.systemui.infoflow.egg;

import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.android.systemui.R;
import com.google.gson.JsonObject;
import com.xiaopeng.lib.apirouter.ApiRouter;
import com.xiaopeng.lib.apirouter.ClientConstants;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.lib.utils.ThreadUtils;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.systemui.infoflow.egg.bean.HolidayView;
import java.io.File;
/* loaded from: classes24.dex */
public class EasterEggView extends FrameLayout {
    private static final String TAG = "EasterEggView";
    private boolean mHasOpenEasterEgg;
    private FrameLayout mLayoutContent;
    private View.OnClickListener mOnCloseBtnClickListener;

    public void setOnCloseBtnClickListener(View.OnClickListener onCloseBtnClickListener) {
        this.mOnCloseBtnClickListener = onCloseBtnClickListener;
    }

    public EasterEggView(Context context) {
        this(context, null);
    }

    public EasterEggView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasterEggView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mHasOpenEasterEgg = false;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mLayoutContent = (FrameLayout) findViewById(R.id.layout_content);
        View closeLayout = findViewById(R.id.layout_close);
        if (closeLayout != null) {
            closeLayout.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.egg.EasterEggView.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (EasterEggView.this.mOnCloseBtnClickListener != null) {
                        EasterEggView.this.mOnCloseBtnClickListener.onClick(v);
                    }
                }
            });
        }
        View closeBtn = findViewById(R.id.btn_close);
        if (closeBtn != null) {
            closeBtn.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.egg.EasterEggView.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (EasterEggView.this.mOnCloseBtnClickListener != null) {
                        EasterEggView.this.mOnCloseBtnClickListener.onClick(v);
                    }
                }
            });
        }
    }

    public void show() {
        show(false);
    }

    public void show(boolean isGetOffScene) {
        setVisibility(0);
        this.mHasOpenEasterEgg = false;
        ThreadUtils.execute(new AnonymousClass3(isGetOffScene));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.systemui.infoflow.egg.EasterEggView$3  reason: invalid class name */
    /* loaded from: classes24.dex */
    public class AnonymousClass3 implements Runnable {
        final /* synthetic */ boolean val$isGetOffScene;

        AnonymousClass3(boolean z) {
            this.val$isGetOffScene = z;
        }

        @Override // java.lang.Runnable
        public void run() {
            final HolidayView holidayView = HolidayEventManager.getHolidayView(this.val$isGetOffScene);
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.egg.EasterEggView.3.1
                @Override // java.lang.Runnable
                public void run() {
                    if (holidayView == null || EasterEggView.this.mLayoutContent == null) {
                        if (EasterEggView.this.mOnCloseBtnClickListener != null) {
                            EasterEggView.this.mOnCloseBtnClickListener.onClick(null);
                            return;
                        }
                        return;
                    }
                    EasterEggView.this.mLayoutContent.addView(holidayView.createView());
                    EasterEggView.this.notifyEasterEggStart(holidayView.getHolidayConfigItem().path);
                    holidayView.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.egg.EasterEggView.3.1.1
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            String url = (String) v.getTag(R.id.tag_url);
                            LogUtils.e(EasterEggView.TAG, "onclick tag = " + url);
                            if (!TextUtils.isEmpty(url)) {
                                JsonObject object = new JsonObject();
                                object.addProperty("cmd", "open_holiday");
                                JsonObject dataObject = new JsonObject();
                                dataObject.addProperty("holiday", holidayView.getHolidayConfigItem().path);
                                dataObject.addProperty(SpeechWidget.WIDGET_URL, url);
                                object.addProperty("data", dataObject.toString());
                                EasterEggView.this.openEasterEgg(object.toString());
                                EasterEggView.this.mHasOpenEasterEgg = true;
                                if (EasterEggView.this.mOnCloseBtnClickListener != null) {
                                    EasterEggView.this.mOnCloseBtnClickListener.onClick(v);
                                }
                            }
                        }
                    });
                    EasterEggView.this.play(holidayView);
                }
            });
        }
    }

    public void dismiss() {
        notifyEasterEggEnd(this.mHasOpenEasterEgg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openEasterEgg(final String content) {
        LogUtils.i(TAG, "openEasterEgg");
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.egg.EasterEggView.4
            @Override // java.lang.Runnable
            public void run() {
                Uri.Builder builder = new Uri.Builder();
                builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("openHoliday").appendQueryParameter("content", content);
                try {
                    ApiRouter.route(builder.build());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyEasterEggStart(final String holiday) {
        LogUtils.i(TAG, "notifyEasterEggStart");
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.egg.EasterEggView.5
            @Override // java.lang.Runnable
            public void run() {
                Uri.Builder builder = new Uri.Builder();
                builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("holidayStart").appendQueryParameter("holiday", holiday);
                try {
                    ApiRouter.route(builder.build());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void notifyEasterEggEnd(final boolean isOpen) {
        LogUtils.i(TAG, "notifyEasterEggEnd");
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.egg.EasterEggView.6
            @Override // java.lang.Runnable
            public void run() {
                Uri.Builder builder = new Uri.Builder();
                builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("holidayEnd").appendQueryParameter("isOpen", String.valueOf(isOpen));
                try {
                    ApiRouter.route(builder.build());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void play(HolidayView holidayView) {
        final String tts = holidayView.getTts();
        String ttsMusic = holidayView.getTtsMusic();
        final File ttsMusicFile = holidayView.getFile(ttsMusic);
        ThreadUtils.postBackground(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.egg.EasterEggView.7
            @Override // java.lang.Runnable
            public void run() {
                if (!TextUtils.isEmpty(tts)) {
                    Uri.Builder builder = new Uri.Builder();
                    builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("speakTTS").appendQueryParameter("tts", tts);
                    try {
                        ApiRouter.route(builder.build());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                File file = ttsMusicFile;
                if (file != null && file.exists()) {
                    Uri.Builder builder2 = new Uri.Builder();
                    builder2.authority("com.xiaopeng.aiassistant.AiassistantService").path("playMusic").appendQueryParameter(ClientConstants.ALIAS.PATH, ttsMusicFile.getAbsolutePath());
                    try {
                        ApiRouter.route(builder2.build());
                    } catch (RemoteException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });
    }
}
