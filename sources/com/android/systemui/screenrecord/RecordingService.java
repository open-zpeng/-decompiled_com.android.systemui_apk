package com.android.systemui.screenrecord;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Icon;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.widget.Toast;
import com.android.systemui.R;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
/* loaded from: classes21.dex */
public class RecordingService extends Service {
    private static final String ACTION_CANCEL = "com.android.systemui.screenrecord.CANCEL";
    private static final String ACTION_DELETE = "com.android.systemui.screenrecord.DELETE";
    private static final String ACTION_PAUSE = "com.android.systemui.screenrecord.PAUSE";
    private static final String ACTION_RESUME = "com.android.systemui.screenrecord.RESUME";
    private static final String ACTION_SHARE = "com.android.systemui.screenrecord.SHARE";
    private static final String ACTION_START = "com.android.systemui.screenrecord.START";
    private static final String ACTION_STOP = "com.android.systemui.screenrecord.STOP";
    private static final int AUDIO_BIT_RATE = 16;
    private static final int AUDIO_SAMPLE_RATE = 44100;
    private static final String CHANNEL_ID = "screen_record";
    private static final String EXTRA_DATA = "extra_data";
    private static final String EXTRA_PATH = "extra_path";
    private static final String EXTRA_RESULT_CODE = "extra_resultCode";
    private static final String EXTRA_SHOW_TAPS = "extra_showTaps";
    private static final String EXTRA_USE_AUDIO = "extra_useAudio";
    private static final int NOTIFICATION_ID = 1;
    private static final int REQUEST_CODE = 2;
    private static final String TAG = "RecordingService";
    private static final int TOTAL_NUM_TRACKS = 1;
    private static final int VIDEO_BIT_RATE = 6000000;
    private static final int VIDEO_FRAME_RATE = 30;
    private Surface mInputSurface;
    private MediaProjection mMediaProjection;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaRecorder mMediaRecorder;
    private Notification.Builder mRecordingNotificationBuilder;
    private boolean mShowTaps;
    private File mTempFile;
    private boolean mUseAudio;
    private VirtualDisplay mVirtualDisplay;

    public static Intent getStartIntent(Context context, int resultCode, Intent data, boolean useAudio, boolean showTaps) {
        return new Intent(context, RecordingService.class).setAction(ACTION_START).putExtra(EXTRA_RESULT_CODE, resultCode).putExtra(EXTRA_DATA, data).putExtra(EXTRA_USE_AUDIO, useAudio).putExtra(EXTRA_SHOW_TAPS, showTaps);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0038, code lost:
        if (r1.equals(com.android.systemui.screenrecord.RecordingService.ACTION_STOP) != false) goto L9;
     */
    @Override // android.app.Service
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int onStartCommand(android.content.Intent r9, int r10, int r11) {
        /*
            Method dump skipped, instructions count: 412
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.screenrecord.RecordingService.onStartCommand(android.content.Intent, int, int):int");
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mMediaProjectionManager = (MediaProjectionManager) getSystemService("media_projection");
    }

    private void startRecording() {
        try {
            this.mTempFile = File.createTempFile("temp", ".mp4");
            Log.d(TAG, "Writing video output to: " + this.mTempFile.getAbsolutePath());
            setTapsVisible(this.mShowTaps);
            this.mMediaRecorder = new MediaRecorder();
            if (this.mUseAudio) {
                this.mMediaRecorder.setAudioSource(1);
            }
            this.mMediaRecorder.setVideoSource(2);
            this.mMediaRecorder.setOutputFormat(2);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenWidth = metrics.widthPixels;
            int screenHeight = metrics.heightPixels;
            this.mMediaRecorder.setVideoEncoder(2);
            this.mMediaRecorder.setVideoSize(screenWidth, screenHeight);
            this.mMediaRecorder.setVideoFrameRate(30);
            this.mMediaRecorder.setVideoEncodingBitRate(VIDEO_BIT_RATE);
            if (this.mUseAudio) {
                this.mMediaRecorder.setAudioEncoder(1);
                this.mMediaRecorder.setAudioChannels(1);
                this.mMediaRecorder.setAudioEncodingBitRate(16);
                this.mMediaRecorder.setAudioSamplingRate(AUDIO_SAMPLE_RATE);
            }
            this.mMediaRecorder.setOutputFile(this.mTempFile);
            this.mMediaRecorder.prepare();
            this.mInputSurface = this.mMediaRecorder.getSurface();
            this.mVirtualDisplay = this.mMediaProjection.createVirtualDisplay("Recording Display", screenWidth, screenHeight, metrics.densityDpi, 16, this.mInputSurface, null, null);
            this.mMediaRecorder.start();
            createRecordingNotification();
        } catch (IOException e) {
            Log.e(TAG, "Error starting screen recording: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void createRecordingNotification() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.screenrecord_name), 4);
        channel.setDescription(getString(R.string.screenrecord_channel_description));
        channel.enableVibration(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        notificationManager.createNotificationChannel(channel);
        this.mRecordingNotificationBuilder = new Notification.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.ic_android).setContentTitle(getResources().getString(R.string.screenrecord_name)).setUsesChronometer(true).setOngoing(true);
        setNotificationActions(false, notificationManager);
        Notification notification = this.mRecordingNotificationBuilder.build();
        startForeground(1, notification);
    }

    private void setNotificationActions(boolean isPaused, NotificationManager notificationManager) {
        String pauseString = getResources().getString(isPaused ? R.string.screenrecord_resume_label : R.string.screenrecord_pause_label);
        Intent pauseIntent = isPaused ? getResumeIntent(this) : getPauseIntent(this);
        this.mRecordingNotificationBuilder.setActions(new Notification.Action.Builder(Icon.createWithResource(this, R.drawable.ic_android), getResources().getString(R.string.screenrecord_stop_label), PendingIntent.getService(this, 2, getStopIntent(this), 134217728)).build(), new Notification.Action.Builder(Icon.createWithResource(this, R.drawable.ic_android), pauseString, PendingIntent.getService(this, 2, pauseIntent, 134217728)).build(), new Notification.Action.Builder(Icon.createWithResource(this, R.drawable.ic_android), getResources().getString(R.string.screenrecord_cancel_label), PendingIntent.getService(this, 2, getCancelIntent(this), 134217728)).build());
        notificationManager.notify(1, this.mRecordingNotificationBuilder.build());
    }

    private Notification createSaveNotification(Uri uri) {
        Intent viewIntent = new Intent("android.intent.action.VIEW").setFlags(268435457).setDataAndType(uri, "video/mp4");
        Notification.Action shareAction = new Notification.Action.Builder(Icon.createWithResource(this, R.drawable.ic_android), getResources().getString(R.string.screenrecord_share_label), PendingIntent.getService(this, 2, getShareIntent(this, uri.toString()), 134217728)).build();
        Notification.Action deleteAction = new Notification.Action.Builder(Icon.createWithResource(this, R.drawable.ic_android), getResources().getString(R.string.screenrecord_delete_label), PendingIntent.getService(this, 2, getDeleteIntent(this, uri.toString()), 134217728)).build();
        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.ic_android).setContentTitle(getResources().getString(R.string.screenrecord_name)).setContentText(getResources().getString(R.string.screenrecord_save_message)).setContentIntent(PendingIntent.getActivity(this, 2, viewIntent, 67108864)).addAction(shareAction).addAction(deleteAction).setAutoCancel(true);
        Bitmap thumbnailBitmap = null;
        try {
            ContentResolver resolver = getContentResolver();
            Size size = Point.convert(MediaStore.ThumbnailConstants.MINI_SIZE);
            thumbnailBitmap = resolver.loadThumbnail(uri, size, null);
        } catch (IOException e) {
            Log.e(TAG, "Error creating thumbnail: " + e.getMessage());
            e.printStackTrace();
        }
        if (thumbnailBitmap != null) {
            Notification.BigPictureStyle pictureStyle = new Notification.BigPictureStyle().bigPicture(thumbnailBitmap).bigLargeIcon((Bitmap) null);
            builder.setLargeIcon(thumbnailBitmap).setStyle(pictureStyle);
        }
        return builder.build();
    }

    private void stopRecording() {
        setTapsVisible(false);
        this.mMediaRecorder.stop();
        this.mMediaRecorder.release();
        this.mMediaRecorder = null;
        this.mMediaProjection.stop();
        this.mMediaProjection = null;
        this.mInputSurface.release();
        this.mVirtualDisplay.release();
        stopSelf();
    }

    private void saveRecording(NotificationManager notificationManager) {
        String fileName = new SimpleDateFormat("'screen-'yyyyMMdd-HHmmss'.mp4'").format(new Date());
        ContentValues values = new ContentValues();
        values.put("_display_name", fileName);
        values.put("mime_type", "video/mp4");
        values.put("date_added", Long.valueOf(System.currentTimeMillis()));
        values.put("datetaken", Long.valueOf(System.currentTimeMillis()));
        ContentResolver resolver = getContentResolver();
        Uri collectionUri = MediaStore.Video.Media.getContentUri("external_primary");
        Uri itemUri = resolver.insert(collectionUri, values);
        try {
            OutputStream os = resolver.openOutputStream(itemUri, "w");
            Files.copy(this.mTempFile.toPath(), os);
            os.close();
            Notification notification = createSaveNotification(itemUri);
            notificationManager.notify(1, notification);
            this.mTempFile.delete();
        } catch (IOException e) {
            Log.e(TAG, "Error saving screen recording: " + e.getMessage());
            Toast.makeText(this, R.string.screenrecord_delete_error, 1).show();
        }
    }

    private void setTapsVisible(boolean turnOn) {
        Settings.System.putInt(getApplicationContext().getContentResolver(), "show_touches", turnOn ? 1 : 0);
    }

    private static Intent getStopIntent(Context context) {
        return new Intent(context, RecordingService.class).setAction(ACTION_STOP);
    }

    private static Intent getPauseIntent(Context context) {
        return new Intent(context, RecordingService.class).setAction(ACTION_PAUSE);
    }

    private static Intent getResumeIntent(Context context) {
        return new Intent(context, RecordingService.class).setAction(ACTION_RESUME);
    }

    private static Intent getCancelIntent(Context context) {
        return new Intent(context, RecordingService.class).setAction(ACTION_CANCEL);
    }

    private static Intent getShareIntent(Context context, String path) {
        return new Intent(context, RecordingService.class).setAction(ACTION_SHARE).putExtra(EXTRA_PATH, path);
    }

    private static Intent getDeleteIntent(Context context, String path) {
        return new Intent(context, RecordingService.class).setAction(ACTION_DELETE).putExtra(EXTRA_PATH, path);
    }
}
