package com.android.systemui.screenshot;

import android.app.ActivityTaskManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.DeviceConfig;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R;
import com.android.systemui.SystemUI;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.screenshot.GlobalScreenshot;
import com.android.systemui.util.NotificationChannels;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import libcore.io.IoUtils;
/* compiled from: GlobalScreenshot.java */
/* loaded from: classes21.dex */
class SaveImageInBackgroundTask extends AsyncTask<Void, Void, Void> {
    private static final String SCREENSHOT_FILE_NAME_TEMPLATE = "Screenshot_%s.png";
    private static final String SCREENSHOT_ID_TEMPLATE = "Screenshot_%s";
    private static final String SCREENSHOT_SHARE_SUBJECT_TEMPLATE = "Screenshot (%s)";
    private static final String TAG = "SaveImageInBackgroundTask";
    private final String mImageFileName;
    private final int mImageHeight;
    private final long mImageTime;
    private final int mImageWidth;
    private final Notification.Builder mNotificationBuilder;
    private final NotificationManager mNotificationManager;
    private final Notification.BigPictureStyle mNotificationStyle;
    private final SaveImageInBackgroundData mParams;
    private final Notification.Builder mPublicNotificationBuilder;
    private final Random mRandom = new Random();
    private final String mScreenshotId;
    private final boolean mSmartActionsEnabled;
    private final ScreenshotNotificationSmartActionsProvider mSmartActionsProvider;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SaveImageInBackgroundTask(Context context, SaveImageInBackgroundData data, NotificationManager nManager) {
        Resources r = context.getResources();
        this.mParams = data;
        this.mImageTime = System.currentTimeMillis();
        String imageDate = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date(this.mImageTime));
        this.mImageFileName = String.format(SCREENSHOT_FILE_NAME_TEMPLATE, imageDate);
        this.mScreenshotId = String.format(SCREENSHOT_ID_TEMPLATE, UUID.randomUUID());
        this.mSmartActionsEnabled = DeviceConfig.getBoolean("systemui", "enable_screenshot_notification_smart_actions", false);
        if (this.mSmartActionsEnabled) {
            this.mSmartActionsProvider = SystemUIFactory.getInstance().createScreenshotNotificationSmartActionsProvider(context, THREAD_POOL_EXECUTOR, new Handler());
        } else {
            this.mSmartActionsProvider = new ScreenshotNotificationSmartActionsProvider();
        }
        this.mImageWidth = data.image.getWidth();
        this.mImageHeight = data.image.getHeight();
        int iconSize = data.iconSize;
        int previewWidth = data.previewWidth;
        int previewHeight = data.previewheight;
        Paint paint = new Paint();
        ColorMatrix desat = new ColorMatrix();
        desat.setSaturation(0.25f);
        paint.setColorFilter(new ColorMatrixColorFilter(desat));
        Matrix matrix = new Matrix();
        matrix.setTranslate((previewWidth - this.mImageWidth) / 2, (previewHeight - this.mImageHeight) / 2);
        Bitmap picture = generateAdjustedHwBitmap(data.image, previewWidth, previewHeight, matrix, paint, 1090519039);
        float scale = iconSize / Math.min(this.mImageWidth, this.mImageHeight);
        matrix.setScale(scale, scale);
        matrix.postTranslate((iconSize - (this.mImageWidth * scale)) / 2.0f, (iconSize - (this.mImageHeight * scale)) / 2.0f);
        Bitmap icon = generateAdjustedHwBitmap(data.image, iconSize, iconSize, matrix, paint, 1090519039);
        this.mNotificationManager = nManager;
        long now = System.currentTimeMillis();
        this.mNotificationStyle = new Notification.BigPictureStyle().bigPicture(picture.createAshmemBitmap());
        this.mPublicNotificationBuilder = new Notification.Builder(context, NotificationChannels.SCREENSHOTS_HEADSUP).setContentTitle(r.getString(R.string.screenshot_saving_title)).setSmallIcon(R.drawable.stat_notify_image).setCategory("progress").setWhen(now).setShowWhen(true).setColor(r.getColor(17170460));
        SystemUI.overrideNotificationAppName(context, this.mPublicNotificationBuilder, true);
        this.mNotificationBuilder = new Notification.Builder(context, NotificationChannels.SCREENSHOTS_HEADSUP).setContentTitle(r.getString(R.string.screenshot_saving_title)).setSmallIcon(R.drawable.stat_notify_image).setWhen(now).setShowWhen(true).setColor(r.getColor(17170460)).setStyle(this.mNotificationStyle).setPublicVersion(this.mPublicNotificationBuilder.build());
        this.mNotificationBuilder.setFlag(32, true);
        SystemUI.overrideNotificationAppName(context, this.mNotificationBuilder, true);
        this.mNotificationManager.notify(1, this.mNotificationBuilder.build());
        this.mNotificationBuilder.setLargeIcon(icon.createAshmemBitmap());
        this.mNotificationStyle.bigLargeIcon((Bitmap) null);
    }

    private List<Notification.Action> buildSmartActions(List<Notification.Action> actions, Context context) {
        List<Notification.Action> broadcastActions = new ArrayList<>();
        for (Notification.Action action : actions) {
            Bundle extras = action.getExtras();
            String actionType = extras.getString(ScreenshotNotificationSmartActionsProvider.ACTION_TYPE, ScreenshotNotificationSmartActionsProvider.DEFAULT_ACTION_TYPE);
            Intent intent = new Intent(context, GlobalScreenshot.SmartActionsReceiver.class).putExtra("android:screenshot_action_intent", action.actionIntent);
            addIntentExtras(this.mScreenshotId, intent, actionType, this.mSmartActionsEnabled);
            PendingIntent broadcastIntent = PendingIntent.getBroadcast(context, this.mRandom.nextInt(), intent, 268435456);
            broadcastActions.add(new Notification.Action.Builder(action.getIcon(), action.title, broadcastIntent).setContextual(true).addExtras(extras).build());
        }
        return broadcastActions;
    }

    private static void addIntentExtras(String screenshotId, Intent intent, String actionType, boolean smartActionsEnabled) {
        intent.putExtra("android:screenshot_action_type", actionType).putExtra("android:screenshot_id", screenshotId).putExtra("android:smart_actions_enabled", smartActionsEnabled);
    }

    private int getUserHandleOfForegroundApplication(Context context) {
        try {
            return ActivityTaskManager.getService().getLastResumedActivityUserId();
        } catch (RemoteException e) {
            Slog.w(TAG, "getUserHandleOfForegroundApplication: ", e);
            return context.getUserId();
        }
    }

    private boolean isManagedProfile(Context context) {
        UserManager manager = UserManager.get(context);
        UserInfo info = manager.getUserInfo(getUserHandleOfForegroundApplication(context));
        return info.isManagedProfile();
    }

    private Bitmap generateAdjustedHwBitmap(Bitmap bitmap, int width, int height, Matrix matrix, Paint paint, int color) {
        Picture picture = new Picture();
        Canvas canvas = picture.beginRecording(width, height);
        canvas.drawColor(color);
        canvas.drawBitmap(bitmap, matrix, paint);
        picture.endRecording();
        return Bitmap.createBitmap(picture);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Void doInBackground(Void... paramsUnused) {
        CompletableFuture<List<Notification.Action>> smartActionsFuture;
        Uri uri;
        MediaStore.PendingSession session;
        OutputStream out;
        if (isCancelled()) {
            return null;
        }
        Process.setThreadPriority(-2);
        Context context = this.mParams.context;
        Bitmap image = this.mParams.image;
        Resources r = context.getResources();
        try {
            smartActionsFuture = GlobalScreenshot.getSmartActionsFuture(this.mScreenshotId, image, this.mSmartActionsProvider, this.mSmartActionsEnabled, isManagedProfile(context));
            MediaStore.PendingParams params = new MediaStore.PendingParams(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, this.mImageFileName, "image/png");
            params.setPrimaryDirectory(Environment.DIRECTORY_PICTURES);
            params.setSecondaryDirectory(Environment.DIRECTORY_SCREENSHOTS);
            uri = MediaStore.createPending(context, params);
            session = MediaStore.openPending(context, uri);
            try {
                out = session.openOutputStream();
            } catch (Exception e) {
                session.abandon();
                throw e;
            }
        } catch (Exception e2) {
            Slog.e(TAG, "unable to save screenshot", e2);
            this.mParams.clearImage();
            this.mParams.errorMsgResId = R.string.screenshot_failed_to_save_text;
        }
        try {
            if (!image.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                throw new IOException("Failed to compress");
            }
            if (out != null) {
                out.close();
            }
            session.publish();
            IoUtils.closeQuietly(session);
            populateNotificationActions(context, r, uri, smartActionsFuture, this.mNotificationBuilder);
            this.mParams.imageUri = uri;
            this.mParams.image = null;
            this.mParams.errorMsgResId = 0;
            if (image != null) {
                image.recycle();
            }
            return null;
        } finally {
        }
    }

    @VisibleForTesting
    void populateNotificationActions(Context context, Resources r, Uri uri, CompletableFuture<List<Notification.Action>> smartActionsFuture, Notification.Builder notificationBuilder) {
        String subjectDate = DateFormat.getDateTimeInstance().format(new Date(this.mImageTime));
        String subject = String.format(SCREENSHOT_SHARE_SUBJECT_TEMPLATE, subjectDate);
        Intent sharingIntent = new Intent("android.intent.action.SEND");
        sharingIntent.setType("image/png");
        sharingIntent.putExtra("android.intent.extra.STREAM", uri);
        ClipData clipdata = new ClipData(new ClipDescription("content", new String[]{"text/plain"}), new ClipData.Item(uri));
        sharingIntent.setClipData(clipdata);
        sharingIntent.putExtra("android.intent.extra.SUBJECT", subject);
        sharingIntent.addFlags(1);
        PendingIntent chooserAction = PendingIntent.getBroadcast(context, 0, new Intent(context, GlobalScreenshot.TargetChosenReceiver.class), 1342177280);
        Intent sharingChooserIntent = Intent.createChooser(sharingIntent, null, chooserAction.getIntentSender()).addFlags(268468224).addFlags(1);
        PendingIntent shareAction = PendingIntent.getBroadcastAsUser(context, 0, new Intent(context, GlobalScreenshot.ActionProxyReceiver.class).putExtra("android:screenshot_action_intent", sharingChooserIntent).putExtra("android:screenshot_disallow_enter_pip", true).putExtra("android:screenshot_id", this.mScreenshotId).putExtra("android:smart_actions_enabled", this.mSmartActionsEnabled), 268435456, UserHandle.SYSTEM);
        Notification.Action.Builder shareActionBuilder = new Notification.Action.Builder(R.drawable.ic_screenshot_share, r.getString(17041022), shareAction);
        notificationBuilder.addAction(shareActionBuilder.build());
        String editorPackage = context.getString(R.string.config_screenshotEditor);
        Intent editIntent = new Intent("android.intent.action.EDIT");
        if (!TextUtils.isEmpty(editorPackage)) {
            editIntent.setComponent(ComponentName.unflattenFromString(editorPackage));
        }
        editIntent.setType("image/png");
        editIntent.setData(uri);
        editIntent.addFlags(1);
        editIntent.addFlags(2);
        PendingIntent editAction = PendingIntent.getBroadcastAsUser(context, 1, new Intent(context, GlobalScreenshot.ActionProxyReceiver.class).putExtra("android:screenshot_action_intent", editIntent).putExtra("android:screenshot_cancel_notification", editIntent.getComponent() != null).putExtra("android:screenshot_id", this.mScreenshotId).putExtra("android:smart_actions_enabled", this.mSmartActionsEnabled), 268435456, UserHandle.SYSTEM);
        Notification.Action.Builder editActionBuilder = new Notification.Action.Builder(R.drawable.ic_screenshot_edit, r.getString(17040984), editAction);
        notificationBuilder.addAction(editActionBuilder.build());
        PendingIntent deleteAction = PendingIntent.getBroadcast(context, 0, new Intent(context, GlobalScreenshot.DeleteScreenshotReceiver.class).putExtra("android:screenshot_uri_id", uri.toString()).putExtra("android:screenshot_id", this.mScreenshotId).putExtra("android:smart_actions_enabled", this.mSmartActionsEnabled), 1342177280);
        Notification.Action.Builder deleteActionBuilder = new Notification.Action.Builder(R.drawable.ic_screenshot_delete, r.getString(17039862), deleteAction);
        notificationBuilder.addAction(deleteActionBuilder.build());
        if (this.mSmartActionsEnabled) {
            int timeoutMs = DeviceConfig.getInt("systemui", "screenshot_notification_smart_actions_timeout_ms", 1000);
            List<Notification.Action> smartActions = GlobalScreenshot.getSmartActions(this.mScreenshotId, smartActionsFuture, timeoutMs, this.mSmartActionsProvider);
            for (Notification.Action action : buildSmartActions(smartActions, context)) {
                notificationBuilder.addAction(action);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Void params) {
        if (this.mParams.errorMsgResId != 0) {
            GlobalScreenshot.notifyScreenshotError(this.mParams.context, this.mNotificationManager, this.mParams.errorMsgResId);
        } else {
            Context context = this.mParams.context;
            Resources r = context.getResources();
            Intent launchIntent = new Intent("android.intent.action.VIEW");
            launchIntent.setDataAndType(this.mParams.imageUri, "image/png");
            launchIntent.setFlags(268435457);
            long now = System.currentTimeMillis();
            this.mPublicNotificationBuilder.setContentTitle(r.getString(R.string.screenshot_saved_title)).setContentText(r.getString(R.string.screenshot_saved_text)).setContentIntent(PendingIntent.getActivity(this.mParams.context, 0, launchIntent, 67108864)).setWhen(now).setAutoCancel(true).setColor(context.getColor(17170460));
            this.mNotificationBuilder.setContentTitle(r.getString(R.string.screenshot_saved_title)).setContentText(r.getString(R.string.screenshot_saved_text)).setContentIntent(PendingIntent.getActivity(this.mParams.context, 0, launchIntent, 67108864)).setWhen(now).setAutoCancel(true).setColor(context.getColor(17170460)).setPublicVersion(this.mPublicNotificationBuilder.build()).setFlag(32, false);
            this.mNotificationManager.notify(1, this.mNotificationBuilder.build());
        }
        this.mParams.finisher.accept(this.mParams.imageUri);
        this.mParams.clearContext();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onCancelled(Void params) {
        this.mParams.finisher.accept(null);
        this.mParams.clearImage();
        this.mParams.clearContext();
        this.mNotificationManager.cancel(1);
    }
}
