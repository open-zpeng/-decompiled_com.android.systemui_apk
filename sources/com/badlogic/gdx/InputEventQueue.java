package com.badlogic.gdx;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.TimeUtils;
/* loaded from: classes21.dex */
public class InputEventQueue implements InputProcessor {
    private static final int KEY_DOWN = 0;
    private static final int KEY_TYPED = 2;
    private static final int KEY_UP = 1;
    private static final int MOUSE_MOVED = 6;
    private static final int SCROLLED = 7;
    private static final int SKIP = -1;
    private static final int TOUCH_DOWN = 3;
    private static final int TOUCH_DRAGGED = 5;
    private static final int TOUCH_UP = 4;
    private long currentEventTime;
    private InputProcessor processor;
    private final IntArray queue = new IntArray();
    private final IntArray processingQueue = new IntArray();

    public InputEventQueue() {
    }

    public InputEventQueue(InputProcessor processor) {
        this.processor = processor;
    }

    public void setProcessor(InputProcessor processor) {
        this.processor = processor;
    }

    public InputProcessor getProcessor() {
        return this.processor;
    }

    public void drain() {
        synchronized (this) {
            if (this.processor == null) {
                this.queue.clear();
                return;
            }
            this.processingQueue.addAll(this.queue);
            this.queue.clear();
            int[] q = this.processingQueue.items;
            InputProcessor localProcessor = this.processor;
            int type = 0;
            int n = this.processingQueue.size;
            while (type < n) {
                int i = type + 1;
                int type2 = q[type];
                int i2 = i + 1;
                int i3 = i2 + 1;
                this.currentEventTime = (q[i] << 32) | (q[i2] & 4294967295L);
                switch (type2) {
                    case -1:
                        type = i3 + q[i3];
                        break;
                    case 0:
                        localProcessor.keyDown(q[i3]);
                        type = i3 + 1;
                        break;
                    case 1:
                        localProcessor.keyUp(q[i3]);
                        type = i3 + 1;
                        break;
                    case 2:
                        localProcessor.keyTyped((char) q[i3]);
                        type = i3 + 1;
                        break;
                    case 3:
                        int i4 = i3 + 1;
                        int i5 = i4 + 1;
                        int i6 = i5 + 1;
                        localProcessor.touchDown(q[i3], q[i4], q[i5], q[i6]);
                        type = i6 + 1;
                        break;
                    case 4:
                        int i7 = i3 + 1;
                        int i8 = i7 + 1;
                        int i9 = i8 + 1;
                        localProcessor.touchUp(q[i3], q[i7], q[i8], q[i9]);
                        type = i9 + 1;
                        break;
                    case 5:
                        int i10 = i3 + 1;
                        int i11 = i10 + 1;
                        localProcessor.touchDragged(q[i3], q[i10], q[i11]);
                        type = i11 + 1;
                        break;
                    case 6:
                        int i12 = i3 + 1;
                        localProcessor.mouseMoved(q[i3], q[i12]);
                        type = i12 + 1;
                        break;
                    case 7:
                        localProcessor.scrolled(q[i3]);
                        type = i3 + 1;
                        break;
                    default:
                        throw new RuntimeException();
                }
            }
            this.processingQueue.clear();
        }
    }

    private synchronized int next(int nextType, int i) {
        int[] q = this.queue.items;
        int n = this.queue.size;
        while (i < n) {
            int type = q[i];
            if (type == nextType) {
                return i;
            }
            int i2 = i + 3;
            switch (type) {
                case -1:
                    i = i2 + q[i2];
                    break;
                case 0:
                    i = i2 + 1;
                    break;
                case 1:
                    i = i2 + 1;
                    break;
                case 2:
                    i = i2 + 1;
                    break;
                case 3:
                    i = i2 + 4;
                    break;
                case 4:
                    i = i2 + 4;
                    break;
                case 5:
                    i = i2 + 3;
                    break;
                case 6:
                    i = i2 + 2;
                    break;
                case 7:
                    i = i2 + 1;
                    break;
                default:
                    throw new RuntimeException();
            }
        }
        return -1;
    }

    private void queueTime() {
        long time = TimeUtils.nanoTime();
        this.queue.add((int) (time >> 32));
        this.queue.add((int) time);
    }

    @Override // com.badlogic.gdx.InputProcessor
    public synchronized boolean keyDown(int keycode) {
        this.queue.add(0);
        queueTime();
        this.queue.add(keycode);
        return false;
    }

    @Override // com.badlogic.gdx.InputProcessor
    public synchronized boolean keyUp(int keycode) {
        this.queue.add(1);
        queueTime();
        this.queue.add(keycode);
        return false;
    }

    @Override // com.badlogic.gdx.InputProcessor
    public synchronized boolean keyTyped(char character) {
        this.queue.add(2);
        queueTime();
        this.queue.add(character);
        return false;
    }

    @Override // com.badlogic.gdx.InputProcessor
    public synchronized boolean touchDown(int screenX, int screenY, int pointer, int button) {
        this.queue.add(3);
        queueTime();
        this.queue.add(screenX);
        this.queue.add(screenY);
        this.queue.add(pointer);
        this.queue.add(button);
        return false;
    }

    @Override // com.badlogic.gdx.InputProcessor
    public synchronized boolean touchUp(int screenX, int screenY, int pointer, int button) {
        this.queue.add(4);
        queueTime();
        this.queue.add(screenX);
        this.queue.add(screenY);
        this.queue.add(pointer);
        this.queue.add(button);
        return false;
    }

    @Override // com.badlogic.gdx.InputProcessor
    public synchronized boolean touchDragged(int screenX, int screenY, int pointer) {
        int i = next(5, 0);
        while (i >= 0) {
            if (this.queue.get(i + 5) == pointer) {
                this.queue.set(i, -1);
                this.queue.set(i + 3, 3);
            }
            i = next(5, i + 6);
        }
        this.queue.add(5);
        queueTime();
        this.queue.add(screenX);
        this.queue.add(screenY);
        this.queue.add(pointer);
        return false;
    }

    @Override // com.badlogic.gdx.InputProcessor
    public synchronized boolean mouseMoved(int screenX, int screenY) {
        int i = next(6, 0);
        while (i >= 0) {
            this.queue.set(i, -1);
            this.queue.set(i + 3, 2);
            i = next(6, i + 5);
        }
        this.queue.add(6);
        queueTime();
        this.queue.add(screenX);
        this.queue.add(screenY);
        return false;
    }

    @Override // com.badlogic.gdx.InputProcessor
    public synchronized boolean scrolled(int amount) {
        this.queue.add(7);
        queueTime();
        this.queue.add(amount);
        return false;
    }

    public long getCurrentEventTime() {
        return this.currentEventTime;
    }
}
