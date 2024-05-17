package com.badlogic.gdx.backends.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.alibaba.fastjson.asm.Opcodes;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Pool;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes21.dex */
public class AndroidInput implements Input, View.OnKeyListener, View.OnTouchListener {
    public static final int NUM_TOUCHES = 20;
    public static final int SUPPORTED_KEYS = 260;
    private SensorEventListener accelerometerListener;
    final Application app;
    private SensorEventListener compassListener;
    private final AndroidApplicationConfiguration config;
    final Context context;
    private SensorEventListener gyroscopeListener;
    private Handler handle;
    final boolean hasMultitouch;
    boolean keyboardAvailable;
    private SensorManager manager;
    protected final Input.Orientation nativeOrientation;
    private final AndroidOnscreenKeyboard onscreenKeyboard;
    private InputProcessor processor;
    private SensorEventListener rotationVectorListener;
    private int sleepTime;
    protected final AndroidTouchHandler touchHandler;
    protected final Vibrator vibrator;
    Pool<KeyEvent> usedKeyEvents = new Pool<KeyEvent>(16, 1000) { // from class: com.badlogic.gdx.backends.android.AndroidInput.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.badlogic.gdx.utils.Pool
        public KeyEvent newObject() {
            return new KeyEvent();
        }
    };
    Pool<TouchEvent> usedTouchEvents = new Pool<TouchEvent>(16, 1000) { // from class: com.badlogic.gdx.backends.android.AndroidInput.2
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.badlogic.gdx.utils.Pool
        public TouchEvent newObject() {
            return new TouchEvent();
        }
    };
    ArrayList<View.OnKeyListener> keyListeners = new ArrayList<>();
    ArrayList<KeyEvent> keyEvents = new ArrayList<>();
    ArrayList<TouchEvent> touchEvents = new ArrayList<>();
    int[] touchX = new int[20];
    int[] touchY = new int[20];
    int[] deltaX = new int[20];
    int[] deltaY = new int[20];
    boolean[] touched = new boolean[20];
    int[] button = new int[20];
    int[] realId = new int[20];
    private int keyCount = 0;
    private boolean[] keys = new boolean[260];
    private boolean keyJustPressed = false;
    private boolean[] justPressedKeys = new boolean[260];
    public boolean accelerometerAvailable = false;
    protected final float[] accelerometerValues = new float[3];
    public boolean gyroscopeAvailable = false;
    protected final float[] gyroscopeValues = new float[3];
    private String text = null;
    private Input.TextInputListener textListener = null;
    private boolean catchBack = false;
    private boolean catchMenu = false;
    private boolean compassAvailable = false;
    private boolean rotationVectorAvailable = false;
    protected final float[] magneticFieldValues = new float[3];
    protected final float[] rotationVectorValues = new float[3];
    private float azimuth = 0.0f;
    private float pitch = 0.0f;
    private float roll = 0.0f;
    private float inclination = 0.0f;
    private boolean justTouched = false;
    private long currentEventTimeStamp = System.nanoTime();
    boolean requestFocus = true;
    final float[] R = new float[9];
    final float[] orientation = new float[3];

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public static class KeyEvent {
        static final int KEY_DOWN = 0;
        static final int KEY_TYPED = 2;
        static final int KEY_UP = 1;
        char keyChar;
        int keyCode;
        long timeStamp;
        int type;

        KeyEvent() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public static class TouchEvent {
        static final int TOUCH_DOWN = 0;
        static final int TOUCH_DRAGGED = 2;
        static final int TOUCH_MOVED = 4;
        static final int TOUCH_SCROLLED = 3;
        static final int TOUCH_UP = 1;
        int button;
        int pointer;
        int scrollAmount;
        long timeStamp;
        int type;
        int x;
        int y;

        TouchEvent() {
        }
    }

    public AndroidInput(Application activity, Context context, Object view, AndroidApplicationConfiguration config) {
        this.sleepTime = 0;
        if (view instanceof View) {
            View v = (View) view;
            v.setOnKeyListener(this);
            v.setOnTouchListener(this);
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            v.requestFocus();
        }
        this.config = config;
        this.onscreenKeyboard = new AndroidOnscreenKeyboard(context, new Handler(), this);
        int i = 0;
        while (true) {
            int[] iArr = this.realId;
            if (i >= iArr.length) {
                break;
            }
            iArr[i] = -1;
            i++;
        }
        this.handle = new Handler();
        this.app = activity;
        this.context = context;
        this.sleepTime = config.touchSleepTime;
        this.touchHandler = new AndroidMultiTouchHandler();
        this.hasMultitouch = this.touchHandler.supportsMultitouch(context);
        this.vibrator = (Vibrator) context.getSystemService("vibrator");
        int rotation = getRotation();
        Graphics.DisplayMode mode = this.app.getGraphics().getDisplayMode();
        if (((rotation == 0 || rotation == 180) && mode.width >= mode.height) || ((rotation == 90 || rotation == 270) && mode.width <= mode.height)) {
            this.nativeOrientation = Input.Orientation.Landscape;
        } else {
            this.nativeOrientation = Input.Orientation.Portrait;
        }
    }

    @Override // com.badlogic.gdx.Input
    public float getAccelerometerX() {
        return this.accelerometerValues[0];
    }

    @Override // com.badlogic.gdx.Input
    public float getAccelerometerY() {
        return this.accelerometerValues[1];
    }

    @Override // com.badlogic.gdx.Input
    public float getAccelerometerZ() {
        return this.accelerometerValues[2];
    }

    @Override // com.badlogic.gdx.Input
    public float getGyroscopeX() {
        return this.gyroscopeValues[0];
    }

    @Override // com.badlogic.gdx.Input
    public float getGyroscopeY() {
        return this.gyroscopeValues[1];
    }

    @Override // com.badlogic.gdx.Input
    public float getGyroscopeZ() {
        return this.gyroscopeValues[2];
    }

    /* renamed from: com.badlogic.gdx.backends.android.AndroidInput$3  reason: invalid class name */
    /* loaded from: classes21.dex */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ String val$hint;
        final /* synthetic */ Input.TextInputListener val$listener;
        final /* synthetic */ String val$text;
        final /* synthetic */ String val$title;

        AnonymousClass3(String str, String str2, String str3, Input.TextInputListener textInputListener) {
            this.val$title = str;
            this.val$hint = str2;
            this.val$text = str3;
            this.val$listener = textInputListener;
        }

        @Override // java.lang.Runnable
        public void run() {
            AlertDialog.Builder alert = new AlertDialog.Builder(AndroidInput.this.context);
            alert.setTitle(this.val$title);
            final EditText input = new EditText(AndroidInput.this.context);
            input.setHint(this.val$hint);
            input.setText(this.val$text);
            input.setSingleLine();
            alert.setView(input);
            alert.setPositiveButton(AndroidInput.this.context.getString(17039370), new DialogInterface.OnClickListener() { // from class: com.badlogic.gdx.backends.android.AndroidInput.3.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int whichButton) {
                    Gdx.app.postRunnable(new Runnable() { // from class: com.badlogic.gdx.backends.android.AndroidInput.3.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            AnonymousClass3.this.val$listener.input(input.getText().toString());
                        }
                    });
                }
            });
            alert.setNegativeButton(AndroidInput.this.context.getString(17039360), new DialogInterface.OnClickListener() { // from class: com.badlogic.gdx.backends.android.AndroidInput.3.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int whichButton) {
                    Gdx.app.postRunnable(new Runnable() { // from class: com.badlogic.gdx.backends.android.AndroidInput.3.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            AnonymousClass3.this.val$listener.canceled();
                        }
                    });
                }
            });
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.badlogic.gdx.backends.android.AndroidInput.3.3
                @Override // android.content.DialogInterface.OnCancelListener
                public void onCancel(DialogInterface arg0) {
                    Gdx.app.postRunnable(new Runnable() { // from class: com.badlogic.gdx.backends.android.AndroidInput.3.3.1
                        @Override // java.lang.Runnable
                        public void run() {
                            AnonymousClass3.this.val$listener.canceled();
                        }
                    });
                }
            });
            alert.show();
        }
    }

    @Override // com.badlogic.gdx.Input
    public void getTextInput(Input.TextInputListener listener, String title, String text, String hint) {
        this.handle.post(new AnonymousClass3(title, hint, text, listener));
    }

    @Override // com.badlogic.gdx.Input
    public int getX() {
        int i;
        synchronized (this) {
            i = this.touchX[0];
        }
        return i;
    }

    @Override // com.badlogic.gdx.Input
    public int getY() {
        int i;
        synchronized (this) {
            i = this.touchY[0];
        }
        return i;
    }

    @Override // com.badlogic.gdx.Input
    public int getX(int pointer) {
        int i;
        synchronized (this) {
            i = this.touchX[pointer];
        }
        return i;
    }

    @Override // com.badlogic.gdx.Input
    public int getY(int pointer) {
        int i;
        synchronized (this) {
            i = this.touchY[pointer];
        }
        return i;
    }

    @Override // com.badlogic.gdx.Input
    public boolean isTouched(int pointer) {
        boolean z;
        synchronized (this) {
            z = this.touched[pointer];
        }
        return z;
    }

    @Override // com.badlogic.gdx.Input
    public synchronized boolean isKeyPressed(int key) {
        if (key == -1) {
            return this.keyCount > 0;
        } else if (key < 0 || key >= 260) {
            return false;
        } else {
            return this.keys[key];
        }
    }

    @Override // com.badlogic.gdx.Input
    public synchronized boolean isKeyJustPressed(int key) {
        if (key == -1) {
            return this.keyJustPressed;
        } else if (key < 0 || key >= 260) {
            return false;
        } else {
            return this.justPressedKeys[key];
        }
    }

    @Override // com.badlogic.gdx.Input
    public boolean isTouched() {
        synchronized (this) {
            if (this.hasMultitouch) {
                for (int pointer = 0; pointer < 20; pointer++) {
                    if (this.touched[pointer]) {
                        return true;
                    }
                }
            }
            return this.touched[0];
        }
    }

    @Override // com.badlogic.gdx.Input
    public void setInputProcessor(InputProcessor processor) {
        synchronized (this) {
            this.processor = processor;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void processEvents() {
        synchronized (this) {
            this.justTouched = false;
            if (this.keyJustPressed) {
                this.keyJustPressed = false;
                for (int i = 0; i < this.justPressedKeys.length; i++) {
                    this.justPressedKeys[i] = false;
                }
            }
            if (this.processor != null) {
                InputProcessor processor = this.processor;
                int len = this.keyEvents.size();
                for (int i2 = 0; i2 < len; i2++) {
                    KeyEvent e = this.keyEvents.get(i2);
                    this.currentEventTimeStamp = e.timeStamp;
                    int i3 = e.type;
                    if (i3 == 0) {
                        processor.keyDown(e.keyCode);
                        this.keyJustPressed = true;
                        this.justPressedKeys[e.keyCode] = true;
                    } else if (i3 == 1) {
                        processor.keyUp(e.keyCode);
                    } else if (i3 == 2) {
                        processor.keyTyped(e.keyChar);
                    }
                    this.usedKeyEvents.free(e);
                }
                int len2 = this.touchEvents.size();
                for (int i4 = 0; i4 < len2; i4++) {
                    TouchEvent e2 = this.touchEvents.get(i4);
                    this.currentEventTimeStamp = e2.timeStamp;
                    int i5 = e2.type;
                    if (i5 == 0) {
                        processor.touchDown(e2.x, e2.y, e2.pointer, e2.button);
                        this.justTouched = true;
                    } else if (i5 == 1) {
                        processor.touchUp(e2.x, e2.y, e2.pointer, e2.button);
                    } else if (i5 == 2) {
                        processor.touchDragged(e2.x, e2.y, e2.pointer);
                    } else if (i5 == 3) {
                        processor.scrolled(e2.scrollAmount);
                    } else if (i5 == 4) {
                        processor.mouseMoved(e2.x, e2.y);
                    }
                    this.usedTouchEvents.free(e2);
                }
            } else {
                int len3 = this.touchEvents.size();
                for (int i6 = 0; i6 < len3; i6++) {
                    TouchEvent e3 = this.touchEvents.get(i6);
                    if (e3.type == 0) {
                        this.justTouched = true;
                    }
                    this.usedTouchEvents.free(e3);
                }
                int len4 = this.keyEvents.size();
                for (int i7 = 0; i7 < len4; i7++) {
                    this.usedKeyEvents.free(this.keyEvents.get(i7));
                }
            }
            if (this.touchEvents.size() == 0) {
                for (int i8 = 0; i8 < this.deltaX.length; i8++) {
                    this.deltaX[0] = 0;
                    this.deltaY[0] = 0;
                }
            }
            this.keyEvents.clear();
            this.touchEvents.clear();
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent event) {
        if (this.requestFocus && view != null) {
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            this.requestFocus = false;
        }
        this.touchHandler.onTouch(event, this);
        int i = this.sleepTime;
        if (i != 0) {
            try {
                Thread.sleep(i);
            } catch (InterruptedException e) {
            }
        }
        return true;
    }

    public void onTap(int x, int y) {
        postTap(x, y);
    }

    public void onDrop(int x, int y) {
        postTap(x, y);
    }

    protected void postTap(int x, int y) {
        synchronized (this) {
            TouchEvent event = this.usedTouchEvents.obtain();
            event.timeStamp = System.nanoTime();
            event.pointer = 0;
            event.x = x;
            event.y = y;
            event.type = 0;
            this.touchEvents.add(event);
            TouchEvent event2 = this.usedTouchEvents.obtain();
            event2.timeStamp = System.nanoTime();
            event2.pointer = 0;
            event2.x = x;
            event2.y = y;
            event2.type = 1;
            this.touchEvents.add(event2);
        }
        Gdx.app.getGraphics().requestRendering();
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View v, int keyCode, android.view.KeyEvent e) {
        int n = this.keyListeners.size();
        for (int i = 0; i < n; i++) {
            if (this.keyListeners.get(i).onKey(v, keyCode, e)) {
                return true;
            }
        }
        synchronized (this) {
            if (e.getKeyCode() == 0 && e.getAction() == 2) {
                String chars = e.getCharacters();
                for (int i2 = 0; i2 < chars.length(); i2++) {
                    KeyEvent event = this.usedKeyEvents.obtain();
                    event.timeStamp = System.nanoTime();
                    event.keyCode = 0;
                    event.keyChar = chars.charAt(i2);
                    event.type = 2;
                    this.keyEvents.add(event);
                }
                return false;
            }
            char character = (char) e.getUnicodeChar();
            if (keyCode == 67) {
                character = '\b';
            }
            if (e.getKeyCode() >= 0 && e.getKeyCode() < 260) {
                int action = e.getAction();
                if (action == 0) {
                    KeyEvent event2 = this.usedKeyEvents.obtain();
                    event2.timeStamp = System.nanoTime();
                    event2.keyChar = (char) 0;
                    event2.keyCode = e.getKeyCode();
                    event2.type = 0;
                    if (keyCode == 4 && e.isAltPressed()) {
                        keyCode = 255;
                        event2.keyCode = 255;
                    }
                    this.keyEvents.add(event2);
                    if (!this.keys[event2.keyCode]) {
                        this.keyCount++;
                        this.keys[event2.keyCode] = true;
                    }
                } else if (action == 1) {
                    long timeStamp = System.nanoTime();
                    KeyEvent event3 = this.usedKeyEvents.obtain();
                    event3.timeStamp = timeStamp;
                    event3.keyChar = (char) 0;
                    event3.keyCode = e.getKeyCode();
                    event3.type = 1;
                    if (keyCode == 4 && e.isAltPressed()) {
                        keyCode = 255;
                        event3.keyCode = 255;
                    }
                    this.keyEvents.add(event3);
                    KeyEvent event4 = this.usedKeyEvents.obtain();
                    event4.timeStamp = timeStamp;
                    event4.keyChar = character;
                    event4.keyCode = 0;
                    event4.type = 2;
                    this.keyEvents.add(event4);
                    if (keyCode == 255) {
                        if (this.keys[255]) {
                            this.keyCount--;
                            this.keys[255] = false;
                        }
                    } else if (this.keys[e.getKeyCode()]) {
                        this.keyCount--;
                        this.keys[e.getKeyCode()] = false;
                    }
                }
                this.app.getGraphics().requestRendering();
                if (keyCode == 255) {
                    return true;
                }
                if (this.catchBack && keyCode == 4) {
                    return true;
                }
                return this.catchMenu && keyCode == 82;
            }
            return false;
        }
    }

    @Override // com.badlogic.gdx.Input
    public void setOnscreenKeyboardVisible(final boolean visible) {
        this.handle.post(new Runnable() { // from class: com.badlogic.gdx.backends.android.AndroidInput.4
            @Override // java.lang.Runnable
            public void run() {
                InputMethodManager manager = (InputMethodManager) AndroidInput.this.context.getSystemService("input_method");
                if (visible) {
                    View view = ((AndroidGraphics) AndroidInput.this.app.getGraphics()).getView();
                    view.setFocusable(true);
                    view.setFocusableInTouchMode(true);
                    manager.showSoftInput(((AndroidGraphics) AndroidInput.this.app.getGraphics()).getView(), 0);
                    return;
                }
                manager.hideSoftInputFromWindow(((AndroidGraphics) AndroidInput.this.app.getGraphics()).getView().getWindowToken(), 0);
            }
        });
    }

    @Override // com.badlogic.gdx.Input
    public void setCatchBackKey(boolean catchBack) {
        this.catchBack = catchBack;
    }

    @Override // com.badlogic.gdx.Input
    public boolean isCatchBackKey() {
        return this.catchBack;
    }

    @Override // com.badlogic.gdx.Input
    public void setCatchMenuKey(boolean catchMenu) {
        this.catchMenu = catchMenu;
    }

    @Override // com.badlogic.gdx.Input
    public boolean isCatchMenuKey() {
        return this.catchMenu;
    }

    @Override // com.badlogic.gdx.Input
    public void vibrate(int milliseconds) {
        this.vibrator.vibrate(milliseconds);
    }

    @Override // com.badlogic.gdx.Input
    public void vibrate(long[] pattern, int repeat) {
        this.vibrator.vibrate(pattern, repeat);
    }

    @Override // com.badlogic.gdx.Input
    public void cancelVibrate() {
        this.vibrator.cancel();
    }

    @Override // com.badlogic.gdx.Input
    public boolean justTouched() {
        return this.justTouched;
    }

    @Override // com.badlogic.gdx.Input
    public boolean isButtonPressed(int button) {
        synchronized (this) {
            boolean z = true;
            if (this.hasMultitouch) {
                for (int pointer = 0; pointer < 20; pointer++) {
                    if (this.touched[pointer] && this.button[pointer] == button) {
                        return true;
                    }
                }
            }
            if (!this.touched[0] || this.button[0] != button) {
                z = false;
            }
            return z;
        }
    }

    private void updateOrientation() {
        if (this.rotationVectorAvailable) {
            SensorManager.getRotationMatrixFromVector(this.R, this.rotationVectorValues);
        } else if (!SensorManager.getRotationMatrix(this.R, null, this.accelerometerValues, this.magneticFieldValues)) {
            return;
        }
        SensorManager.getOrientation(this.R, this.orientation);
        this.azimuth = (float) Math.toDegrees(this.orientation[0]);
        this.pitch = (float) Math.toDegrees(this.orientation[1]);
        this.roll = (float) Math.toDegrees(this.orientation[2]);
    }

    @Override // com.badlogic.gdx.Input
    public void getRotationMatrix(float[] matrix) {
        if (this.rotationVectorAvailable) {
            SensorManager.getRotationMatrixFromVector(matrix, this.rotationVectorValues);
        } else {
            SensorManager.getRotationMatrix(matrix, null, this.accelerometerValues, this.magneticFieldValues);
        }
    }

    @Override // com.badlogic.gdx.Input
    public float getAzimuth() {
        if (this.compassAvailable || this.rotationVectorAvailable) {
            updateOrientation();
            return this.azimuth;
        }
        return 0.0f;
    }

    @Override // com.badlogic.gdx.Input
    public float getPitch() {
        if (this.compassAvailable || this.rotationVectorAvailable) {
            updateOrientation();
            return this.pitch;
        }
        return 0.0f;
    }

    @Override // com.badlogic.gdx.Input
    public float getRoll() {
        if (this.compassAvailable || this.rotationVectorAvailable) {
            updateOrientation();
            return this.roll;
        }
        return 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void registerSensorListeners() {
        if (this.config.useAccelerometer) {
            this.manager = (SensorManager) this.context.getSystemService("sensor");
            if (this.manager.getSensorList(1).size() == 0) {
                this.accelerometerAvailable = false;
            } else {
                Sensor accelerometer = this.manager.getSensorList(1).get(0);
                this.accelerometerListener = new SensorListener();
                this.accelerometerAvailable = this.manager.registerListener(this.accelerometerListener, accelerometer, this.config.sensorDelay);
            }
        } else {
            this.accelerometerAvailable = false;
        }
        if (this.config.useGyroscope) {
            this.manager = (SensorManager) this.context.getSystemService("sensor");
            if (this.manager.getSensorList(4).size() == 0) {
                this.gyroscopeAvailable = false;
            } else {
                Sensor gyroscope = this.manager.getSensorList(4).get(0);
                this.gyroscopeListener = new SensorListener();
                this.gyroscopeAvailable = this.manager.registerListener(this.gyroscopeListener, gyroscope, this.config.sensorDelay);
            }
        } else {
            this.gyroscopeAvailable = false;
        }
        this.rotationVectorAvailable = false;
        if (this.config.useRotationVectorSensor) {
            if (this.manager == null) {
                this.manager = (SensorManager) this.context.getSystemService("sensor");
            }
            List<Sensor> rotationVectorSensors = this.manager.getSensorList(11);
            if (rotationVectorSensors.size() > 0) {
                this.rotationVectorListener = new SensorListener();
                Iterator<Sensor> it = rotationVectorSensors.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Sensor sensor = it.next();
                    if (sensor.getVendor().equals("Google Inc.") && sensor.getVersion() == 3) {
                        this.rotationVectorAvailable = this.manager.registerListener(this.rotationVectorListener, sensor, this.config.sensorDelay);
                        break;
                    }
                }
                if (!this.rotationVectorAvailable) {
                    this.rotationVectorAvailable = this.manager.registerListener(this.rotationVectorListener, rotationVectorSensors.get(0), this.config.sensorDelay);
                }
            }
        }
        if (this.config.useCompass && !this.rotationVectorAvailable) {
            if (this.manager == null) {
                this.manager = (SensorManager) this.context.getSystemService("sensor");
            }
            Sensor sensor2 = this.manager.getDefaultSensor(2);
            if (sensor2 != null) {
                this.compassAvailable = this.accelerometerAvailable;
                if (this.compassAvailable) {
                    this.compassListener = new SensorListener();
                    this.compassAvailable = this.manager.registerListener(this.compassListener, sensor2, this.config.sensorDelay);
                }
            } else {
                this.compassAvailable = false;
            }
        } else {
            this.compassAvailable = false;
        }
        Gdx.app.log("AndroidInput", "sensor listener setup");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unregisterSensorListeners() {
        SensorManager sensorManager = this.manager;
        if (sensorManager != null) {
            SensorEventListener sensorEventListener = this.accelerometerListener;
            if (sensorEventListener != null) {
                sensorManager.unregisterListener(sensorEventListener);
                this.accelerometerListener = null;
            }
            SensorEventListener sensorEventListener2 = this.gyroscopeListener;
            if (sensorEventListener2 != null) {
                this.manager.unregisterListener(sensorEventListener2);
                this.gyroscopeListener = null;
            }
            SensorEventListener sensorEventListener3 = this.rotationVectorListener;
            if (sensorEventListener3 != null) {
                this.manager.unregisterListener(sensorEventListener3);
                this.rotationVectorListener = null;
            }
            SensorEventListener sensorEventListener4 = this.compassListener;
            if (sensorEventListener4 != null) {
                this.manager.unregisterListener(sensorEventListener4);
                this.compassListener = null;
            }
            this.manager = null;
        }
        Gdx.app.log("AndroidInput", "sensor listener tear down");
    }

    @Override // com.badlogic.gdx.Input
    public InputProcessor getInputProcessor() {
        return this.processor;
    }

    @Override // com.badlogic.gdx.Input
    public boolean isPeripheralAvailable(Input.Peripheral peripheral) {
        Vibrator vibrator;
        if (peripheral == Input.Peripheral.Accelerometer) {
            return this.accelerometerAvailable;
        }
        if (peripheral == Input.Peripheral.Gyroscope) {
            return this.gyroscopeAvailable;
        }
        if (peripheral == Input.Peripheral.Compass) {
            return this.compassAvailable;
        }
        if (peripheral == Input.Peripheral.HardwareKeyboard) {
            return this.keyboardAvailable;
        }
        if (peripheral == Input.Peripheral.OnscreenKeyboard) {
            return true;
        }
        if (peripheral == Input.Peripheral.Vibrator) {
            return (Build.VERSION.SDK_INT < 11 || (vibrator = this.vibrator) == null) ? this.vibrator != null : vibrator.hasVibrator();
        } else if (peripheral == Input.Peripheral.MultitouchScreen) {
            return this.hasMultitouch;
        } else {
            if (peripheral == Input.Peripheral.RotationVector) {
                return this.rotationVectorAvailable;
            }
            return false;
        }
    }

    public int getFreePointerIndex() {
        int len = this.realId.length;
        for (int i = 0; i < len; i++) {
            if (this.realId[i] == -1) {
                return i;
            }
        }
        this.realId = resize(this.realId);
        this.touchX = resize(this.touchX);
        this.touchY = resize(this.touchY);
        this.deltaX = resize(this.deltaX);
        this.deltaY = resize(this.deltaY);
        this.touched = resize(this.touched);
        this.button = resize(this.button);
        return len;
    }

    private int[] resize(int[] orig) {
        int[] tmp = new int[orig.length + 2];
        System.arraycopy(orig, 0, tmp, 0, orig.length);
        return tmp;
    }

    private boolean[] resize(boolean[] orig) {
        boolean[] tmp = new boolean[orig.length + 2];
        System.arraycopy(orig, 0, tmp, 0, orig.length);
        return tmp;
    }

    public int lookUpPointerIndex(int pointerId) {
        int len = this.realId.length;
        for (int i = 0; i < len; i++) {
            if (this.realId[i] == pointerId) {
                return i;
            }
        }
        StringBuffer buf = new StringBuffer();
        for (int i2 = 0; i2 < len; i2++) {
            buf.append(i2 + NavigationBarInflaterView.KEY_IMAGE_DELIM + this.realId[i2] + " ");
        }
        Application application = Gdx.app;
        application.log("AndroidInput", "Pointer ID lookup failed: " + pointerId + ", " + buf.toString());
        return -1;
    }

    @Override // com.badlogic.gdx.Input
    public int getRotation() {
        int orientation;
        Context context = this.context;
        if (context instanceof Activity) {
            orientation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
        } else {
            orientation = ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRotation();
        }
        if (orientation != 0) {
            if (orientation != 1) {
                if (orientation != 2) {
                    if (orientation != 3) {
                        return 0;
                    }
                    return 270;
                }
                return Opcodes.GETFIELD;
            }
            return 90;
        }
        return 0;
    }

    @Override // com.badlogic.gdx.Input
    public Input.Orientation getNativeOrientation() {
        return this.nativeOrientation;
    }

    @Override // com.badlogic.gdx.Input
    public void setCursorCatched(boolean catched) {
    }

    @Override // com.badlogic.gdx.Input
    public boolean isCursorCatched() {
        return false;
    }

    @Override // com.badlogic.gdx.Input
    public int getDeltaX() {
        return this.deltaX[0];
    }

    @Override // com.badlogic.gdx.Input
    public int getDeltaX(int pointer) {
        return this.deltaX[pointer];
    }

    @Override // com.badlogic.gdx.Input
    public int getDeltaY() {
        return this.deltaY[0];
    }

    @Override // com.badlogic.gdx.Input
    public int getDeltaY(int pointer) {
        return this.deltaY[pointer];
    }

    @Override // com.badlogic.gdx.Input
    public void setCursorPosition(int x, int y) {
    }

    @Override // com.badlogic.gdx.Input
    public long getCurrentEventTime() {
        return this.currentEventTimeStamp;
    }

    public void addKeyListener(View.OnKeyListener listener) {
        this.keyListeners.add(listener);
    }

    public void onPause() {
        unregisterSensorListeners();
        Arrays.fill(this.realId, -1);
        Arrays.fill(this.touched, false);
    }

    public void onResume() {
        registerSensorListeners();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class SensorListener implements SensorEventListener {
        public SensorListener() {
        }

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == 1) {
                if (AndroidInput.this.nativeOrientation == Input.Orientation.Portrait) {
                    System.arraycopy(event.values, 0, AndroidInput.this.accelerometerValues, 0, AndroidInput.this.accelerometerValues.length);
                } else {
                    AndroidInput.this.accelerometerValues[0] = event.values[1];
                    AndroidInput.this.accelerometerValues[1] = -event.values[0];
                    AndroidInput.this.accelerometerValues[2] = event.values[2];
                }
            }
            if (event.sensor.getType() == 2) {
                System.arraycopy(event.values, 0, AndroidInput.this.magneticFieldValues, 0, AndroidInput.this.magneticFieldValues.length);
            }
            if (event.sensor.getType() == 4) {
                if (AndroidInput.this.nativeOrientation == Input.Orientation.Portrait) {
                    System.arraycopy(event.values, 0, AndroidInput.this.gyroscopeValues, 0, AndroidInput.this.gyroscopeValues.length);
                } else {
                    AndroidInput.this.gyroscopeValues[0] = event.values[1];
                    AndroidInput.this.gyroscopeValues[1] = -event.values[0];
                    AndroidInput.this.gyroscopeValues[2] = event.values[2];
                }
            }
            if (event.sensor.getType() == 11) {
                if (AndroidInput.this.nativeOrientation == Input.Orientation.Portrait) {
                    System.arraycopy(event.values, 0, AndroidInput.this.rotationVectorValues, 0, AndroidInput.this.rotationVectorValues.length);
                    return;
                }
                AndroidInput.this.rotationVectorValues[0] = event.values[1];
                AndroidInput.this.rotationVectorValues[1] = -event.values[0];
                AndroidInput.this.rotationVectorValues[2] = event.values[2];
            }
        }
    }
}
