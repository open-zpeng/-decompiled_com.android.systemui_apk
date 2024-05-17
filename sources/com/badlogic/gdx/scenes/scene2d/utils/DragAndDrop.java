package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
/* loaded from: classes21.dex */
public class DragAndDrop {
    static final Vector2 tmpVector = new Vector2();
    private int button;
    Actor dragActor;
    Source dragSource;
    long dragValidTime;
    boolean isValidTarget;
    Payload payload;
    Target target;
    float touchOffsetX;
    float touchOffsetY;
    final Array<Target> targets = new Array<>();
    final ObjectMap<Source, DragListener> sourceListeners = new ObjectMap<>();
    private float tapSquareSize = 8.0f;
    float dragActorX = 0.0f;
    float dragActorY = 0.0f;
    int dragTime = 250;
    int activePointer = -1;
    boolean cancelTouchFocus = true;
    boolean keepWithinStage = true;

    public void addSource(final Source source) {
        DragListener listener = new DragListener() { // from class: com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.1
            @Override // com.badlogic.gdx.scenes.scene2d.utils.DragListener
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                Stage stage;
                if (DragAndDrop.this.activePointer != -1) {
                    event.stop();
                    return;
                }
                DragAndDrop dragAndDrop = DragAndDrop.this;
                dragAndDrop.activePointer = pointer;
                dragAndDrop.dragValidTime = System.currentTimeMillis() + DragAndDrop.this.dragTime;
                DragAndDrop dragAndDrop2 = DragAndDrop.this;
                Source source2 = source;
                dragAndDrop2.dragSource = source2;
                dragAndDrop2.payload = source2.dragStart(event, getTouchDownX(), getTouchDownY(), pointer);
                event.stop();
                if (!DragAndDrop.this.cancelTouchFocus || DragAndDrop.this.payload == null || (stage = source.getActor().getStage()) == null) {
                    return;
                }
                stage.cancelTouchFocusExcept(this, source.getActor());
            }

            @Override // com.badlogic.gdx.scenes.scene2d.utils.DragListener
            public void drag(InputEvent event, float x, float y, int pointer) {
                Target newTarget;
                if (DragAndDrop.this.payload != null && pointer == DragAndDrop.this.activePointer) {
                    source.drag(event, x, y, pointer);
                    Stage stage = event.getStage();
                    if (DragAndDrop.this.dragActor != null) {
                        DragAndDrop.this.dragActor.remove();
                        DragAndDrop.this.dragActor = null;
                    }
                    DragAndDrop.this.isValidTarget = false;
                    float stageX = event.getStageX() + DragAndDrop.this.touchOffsetX;
                    float stageY = event.getStageY() + DragAndDrop.this.touchOffsetY;
                    Actor hit = event.getStage().hit(stageX, stageY, true);
                    if (hit == null) {
                        hit = event.getStage().hit(stageX, stageY, false);
                    }
                    Actor hit2 = hit;
                    if (hit2 != null) {
                        int n = DragAndDrop.this.targets.size;
                        for (int i = 0; i < n; i++) {
                            Target target = DragAndDrop.this.targets.get(i);
                            if (target.actor.isAscendantOf(hit2)) {
                                target.actor.stageToLocalCoordinates(DragAndDrop.tmpVector.set(stageX, stageY));
                                newTarget = target;
                                break;
                            }
                        }
                    }
                    newTarget = null;
                    if (newTarget != DragAndDrop.this.target) {
                        if (DragAndDrop.this.target != null) {
                            DragAndDrop.this.target.reset(source, DragAndDrop.this.payload);
                        }
                        DragAndDrop.this.target = newTarget;
                    }
                    if (newTarget != null) {
                        DragAndDrop dragAndDrop = DragAndDrop.this;
                        dragAndDrop.isValidTarget = newTarget.drag(source, dragAndDrop.payload, DragAndDrop.tmpVector.x, DragAndDrop.tmpVector.y, pointer);
                    }
                    Actor actor = null;
                    if (DragAndDrop.this.target != null) {
                        actor = DragAndDrop.this.isValidTarget ? DragAndDrop.this.payload.validDragActor : DragAndDrop.this.payload.invalidDragActor;
                    }
                    if (actor == null) {
                        actor = DragAndDrop.this.payload.dragActor;
                    }
                    DragAndDrop.this.dragActor = actor;
                    if (actor == null) {
                        return;
                    }
                    stage.addActor(actor);
                    float actorX = (event.getStageX() - actor.getWidth()) + DragAndDrop.this.dragActorX;
                    float actorY = event.getStageY() + DragAndDrop.this.dragActorY;
                    if (DragAndDrop.this.keepWithinStage) {
                        if (actorX < 0.0f) {
                            actorX = 0.0f;
                        }
                        if (actorY < 0.0f) {
                            actorY = 0.0f;
                        }
                        if (actor.getWidth() + actorX > stage.getWidth()) {
                            actorX = stage.getWidth() - actor.getWidth();
                        }
                        if (actor.getHeight() + actorY > stage.getHeight()) {
                            actorY = stage.getHeight() - actor.getHeight();
                        }
                    }
                    actor.setPosition(actorX, actorY);
                }
            }

            @Override // com.badlogic.gdx.scenes.scene2d.utils.DragListener
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                if (pointer != DragAndDrop.this.activePointer) {
                    return;
                }
                DragAndDrop dragAndDrop = DragAndDrop.this;
                dragAndDrop.activePointer = -1;
                if (dragAndDrop.payload == null) {
                    return;
                }
                if (System.currentTimeMillis() < DragAndDrop.this.dragValidTime) {
                    DragAndDrop.this.isValidTarget = false;
                }
                if (DragAndDrop.this.dragActor != null) {
                    DragAndDrop.this.dragActor.remove();
                }
                if (DragAndDrop.this.isValidTarget) {
                    float stageX = event.getStageX() + DragAndDrop.this.touchOffsetX;
                    float stageY = event.getStageY() + DragAndDrop.this.touchOffsetY;
                    DragAndDrop.this.target.actor.stageToLocalCoordinates(DragAndDrop.tmpVector.set(stageX, stageY));
                    DragAndDrop.this.target.drop(source, DragAndDrop.this.payload, DragAndDrop.tmpVector.x, DragAndDrop.tmpVector.y, pointer);
                }
                source.dragStop(event, x, y, pointer, DragAndDrop.this.payload, DragAndDrop.this.isValidTarget ? DragAndDrop.this.target : null);
                if (DragAndDrop.this.target != null) {
                    DragAndDrop.this.target.reset(source, DragAndDrop.this.payload);
                }
                DragAndDrop dragAndDrop2 = DragAndDrop.this;
                dragAndDrop2.dragSource = null;
                dragAndDrop2.payload = null;
                dragAndDrop2.target = null;
                dragAndDrop2.isValidTarget = false;
                dragAndDrop2.dragActor = null;
            }
        };
        listener.setTapSquareSize(this.tapSquareSize);
        listener.setButton(this.button);
        source.actor.addCaptureListener(listener);
        this.sourceListeners.put(source, listener);
    }

    public void removeSource(Source source) {
        DragListener dragListener = this.sourceListeners.remove(source);
        source.actor.removeCaptureListener(dragListener);
    }

    public void addTarget(Target target) {
        this.targets.add(target);
    }

    public void removeTarget(Target target) {
        this.targets.removeValue(target, true);
    }

    public void clear() {
        this.targets.clear();
        ObjectMap.Entries<Source, DragListener> it = this.sourceListeners.entries().iterator();
        while (it.hasNext()) {
            ObjectMap.Entry entry = it.next();
            ((Source) entry.key).actor.removeCaptureListener((EventListener) entry.value);
        }
        this.sourceListeners.clear();
    }

    public void cancelTouchFocusExcept(Source except) {
        Stage stage;
        DragListener listener = this.sourceListeners.get(except);
        if (listener != null && (stage = except.getActor().getStage()) != null) {
            stage.cancelTouchFocusExcept(listener, except.getActor());
        }
    }

    public void setTapSquareSize(float halfTapSquareSize) {
        this.tapSquareSize = halfTapSquareSize;
    }

    public void setButton(int button) {
        this.button = button;
    }

    public void setDragActorPosition(float dragActorX, float dragActorY) {
        this.dragActorX = dragActorX;
        this.dragActorY = dragActorY;
    }

    public void setTouchOffset(float touchOffsetX, float touchOffsetY) {
        this.touchOffsetX = touchOffsetX;
        this.touchOffsetY = touchOffsetY;
    }

    public boolean isDragging() {
        return this.payload != null;
    }

    public Actor getDragActor() {
        return this.dragActor;
    }

    public Payload getDragPayload() {
        return this.payload;
    }

    public Source getDragSource() {
        return this.dragSource;
    }

    public void setDragTime(int dragMillis) {
        this.dragTime = dragMillis;
    }

    public int getDragTime() {
        return this.dragTime;
    }

    public boolean isDragValid() {
        return this.payload != null && System.currentTimeMillis() >= this.dragValidTime;
    }

    public void setCancelTouchFocus(boolean cancelTouchFocus) {
        this.cancelTouchFocus = cancelTouchFocus;
    }

    public void setKeepWithinStage(boolean keepWithinStage) {
        this.keepWithinStage = keepWithinStage;
    }

    /* loaded from: classes21.dex */
    public static abstract class Source {
        final Actor actor;

        public abstract Payload dragStart(InputEvent inputEvent, float f, float f2, int i);

        public Source(Actor actor) {
            if (actor == null) {
                throw new IllegalArgumentException("actor cannot be null.");
            }
            this.actor = actor;
        }

        public void drag(InputEvent event, float x, float y, int pointer) {
        }

        public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
        }

        public Actor getActor() {
            return this.actor;
        }
    }

    /* loaded from: classes21.dex */
    public static abstract class Target {
        final Actor actor;

        public abstract boolean drag(Source source, Payload payload, float f, float f2, int i);

        public abstract void drop(Source source, Payload payload, float f, float f2, int i);

        public Target(Actor actor) {
            if (actor == null) {
                throw new IllegalArgumentException("actor cannot be null.");
            }
            this.actor = actor;
            Stage stage = actor.getStage();
            if (stage != null && actor == stage.getRoot()) {
                throw new IllegalArgumentException("The stage root cannot be a drag and drop target.");
            }
        }

        public void reset(Source source, Payload payload) {
        }

        public Actor getActor() {
            return this.actor;
        }
    }

    /* loaded from: classes21.dex */
    public static class Payload {
        Actor dragActor;
        Actor invalidDragActor;
        Object object;
        Actor validDragActor;

        public void setDragActor(Actor dragActor) {
            this.dragActor = dragActor;
        }

        public Actor getDragActor() {
            return this.dragActor;
        }

        public void setValidDragActor(Actor validDragActor) {
            this.validDragActor = validDragActor;
        }

        public Actor getValidDragActor() {
            return this.validDragActor;
        }

        public void setInvalidDragActor(Actor invalidDragActor) {
            this.invalidDragActor = invalidDragActor;
        }

        public Actor getInvalidDragActor() {
            return this.invalidDragActor;
        }

        public Object getObject() {
            return this.object;
        }

        public void setObject(Object object) {
            this.object = object;
        }
    }
}
