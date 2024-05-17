package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.utils.Selection;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Array;
/* loaded from: classes21.dex */
public class Tree<N extends Node, V> extends WidgetGroup {
    private static final Vector2 tmp = new Vector2();
    private ClickListener clickListener;
    private N foundNode;
    float iconSpacingLeft;
    float iconSpacingRight;
    float indentSpacing;
    private N overNode;
    float paddingLeft;
    float paddingRight;
    private float prefHeight;
    private float prefWidth;
    N rangeStart;
    final Array<N> rootNodes;
    final Selection<N> selection;
    private boolean sizeInvalid;
    TreeStyle style;
    float ySpacing;

    public Tree(Skin skin) {
        this((TreeStyle) skin.get(TreeStyle.class));
    }

    public Tree(Skin skin, String styleName) {
        this((TreeStyle) skin.get(styleName, TreeStyle.class));
    }

    public Tree(TreeStyle style) {
        this.rootNodes = new Array<>();
        this.ySpacing = 4.0f;
        this.iconSpacingLeft = 2.0f;
        this.iconSpacingRight = 2.0f;
        this.sizeInvalid = true;
        this.selection = (Selection<N>) new Selection<N>() { // from class: com.badlogic.gdx.scenes.scene2d.ui.Tree.1
            @Override // com.badlogic.gdx.scenes.scene2d.utils.Selection
            protected void changed() {
                int size = size();
                if (size == 0) {
                    Tree.this.rangeStart = null;
                } else if (size == 1) {
                    Tree.this.rangeStart = (N) first();
                }
            }
        };
        this.selection.setActor(this);
        this.selection.setMultiple(true);
        setStyle(style);
        initialize();
    }

    private void initialize() {
        ClickListener clickListener = new ClickListener() { // from class: com.badlogic.gdx.scenes.scene2d.ui.Tree.2
            @Override // com.badlogic.gdx.scenes.scene2d.utils.ClickListener
            public void clicked(InputEvent event, float x, float y) {
                N node = (N) Tree.this.getNodeAt(y);
                if (node != null && node == Tree.this.getNodeAt(getTouchDownY())) {
                    if (Tree.this.selection.getMultiple() && Tree.this.selection.notEmpty() && UIUtils.shift()) {
                        if (Tree.this.rangeStart == null) {
                            Tree.this.rangeStart = node;
                        }
                        N rangeStart = Tree.this.rangeStart;
                        if (!UIUtils.ctrl()) {
                            Tree.this.selection.clear();
                        }
                        float start = rangeStart.actor.getY();
                        float end = node.actor.getY();
                        if (start > end) {
                            Tree tree = Tree.this;
                            tree.selectNodes(tree.rootNodes, end, start);
                        } else {
                            Tree tree2 = Tree.this;
                            tree2.selectNodes(tree2.rootNodes, start, end);
                            Tree.this.selection.items().orderedItems().reverse();
                        }
                        Tree.this.selection.fireChangeEvent();
                        Tree.this.rangeStart = rangeStart;
                        return;
                    }
                    if (node.children.size > 0 && (!Tree.this.selection.getMultiple() || !UIUtils.ctrl())) {
                        float rowX = node.actor.getX();
                        if (node.icon != null) {
                            rowX -= Tree.this.iconSpacingRight + node.icon.getMinWidth();
                        }
                        if (x < rowX) {
                            node.setExpanded(!node.expanded);
                            return;
                        }
                    }
                    if (node.isSelectable()) {
                        Tree.this.selection.choose(node);
                        if (!Tree.this.selection.isEmpty()) {
                            Tree.this.rangeStart = node;
                        }
                    }
                }
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.badlogic.gdx.scenes.scene2d.InputListener
            public boolean mouseMoved(InputEvent event, float x, float y) {
                Tree tree = Tree.this;
                tree.setOverNode(tree.getNodeAt(y));
                return false;
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.badlogic.gdx.scenes.scene2d.utils.ClickListener, com.badlogic.gdx.scenes.scene2d.InputListener
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                Tree tree = Tree.this;
                tree.setOverNode(tree.getNodeAt(y));
            }

            @Override // com.badlogic.gdx.scenes.scene2d.utils.ClickListener, com.badlogic.gdx.scenes.scene2d.InputListener
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (toActor == null || !toActor.isDescendantOf(Tree.this)) {
                    Tree.this.setOverNode(null);
                }
            }
        };
        this.clickListener = clickListener;
        addListener(clickListener);
    }

    public void setStyle(TreeStyle style) {
        this.style = style;
        if (this.indentSpacing == 0.0f) {
            this.indentSpacing = plusMinusWidth();
        }
    }

    public void add(N node) {
        insert(this.rootNodes.size, node);
    }

    public void insert(int index, N node) {
        int existingIndex = this.rootNodes.indexOf(node, true);
        if (existingIndex != -1 && existingIndex < index) {
            index--;
        }
        remove(node);
        node.parent = null;
        this.rootNodes.insert(index, node);
        node.addToTree(this);
        invalidateHierarchy();
    }

    public void remove(N node) {
        if (node.parent != null) {
            node.parent.remove(node);
            return;
        }
        this.rootNodes.removeValue(node, true);
        node.removeFromTree(this);
        invalidateHierarchy();
    }

    @Override // com.badlogic.gdx.scenes.scene2d.Group
    public void clearChildren() {
        super.clearChildren();
        setOverNode(null);
        this.rootNodes.clear();
        this.selection.clear();
    }

    public Array<N> getNodes() {
        return this.rootNodes;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public void invalidate() {
        super.invalidate();
        this.sizeInvalid = true;
    }

    private float plusMinusWidth() {
        float width = Math.max(this.style.plus.getMinWidth(), this.style.minus.getMinWidth());
        if (this.style.plusOver != null) {
            width = Math.max(width, this.style.plusOver.getMinWidth());
        }
        return this.style.minusOver != null ? Math.max(width, this.style.minusOver.getMinWidth()) : width;
    }

    private void computeSize() {
        this.sizeInvalid = false;
        this.prefWidth = plusMinusWidth();
        this.prefHeight = 0.0f;
        computeSize(this.rootNodes, 0.0f, this.prefWidth);
        this.prefWidth += this.paddingLeft + this.paddingRight;
    }

    private void computeSize(Array<N> nodes, float indent, float plusMinusWidth) {
        float rowWidth;
        float ySpacing = this.ySpacing;
        float spacing = this.iconSpacingLeft + this.iconSpacingRight;
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            N node = nodes.get(i);
            float rowWidth2 = indent + plusMinusWidth;
            Actor actor = node.actor;
            if (actor instanceof Layout) {
                Layout layout = (Layout) actor;
                rowWidth = rowWidth2 + layout.getPrefWidth();
                node.height = layout.getPrefHeight();
            } else {
                rowWidth = rowWidth2 + actor.getWidth();
                node.height = actor.getHeight();
            }
            if (node.icon != null) {
                rowWidth += node.icon.getMinWidth() + spacing;
                node.height = Math.max(node.height, node.icon.getMinHeight());
            }
            this.prefWidth = Math.max(this.prefWidth, rowWidth);
            this.prefHeight += node.height + ySpacing;
            if (node.expanded) {
                computeSize(node.children, this.indentSpacing + indent, plusMinusWidth);
            }
        }
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public void layout() {
        if (this.sizeInvalid) {
            computeSize();
        }
        layout(this.rootNodes, this.paddingLeft, getHeight() - (this.ySpacing / 2.0f), plusMinusWidth());
    }

    private float layout(Array<N> nodes, float indent, float y, float plusMinusWidth) {
        float x;
        float ySpacing = this.ySpacing;
        float iconSpacingLeft = this.iconSpacingLeft;
        float spacing = this.iconSpacingRight + iconSpacingLeft;
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            N node = nodes.get(i);
            float x2 = indent + plusMinusWidth;
            if (node.icon != null) {
                x = x2 + node.icon.getMinWidth() + spacing;
            } else {
                x = x2 + iconSpacingLeft;
            }
            if (node.actor instanceof Layout) {
                ((Layout) node.actor).pack();
            }
            float y2 = y - node.getHeight();
            node.actor.setPosition(x, y2);
            y = y2 - ySpacing;
            if (node.expanded) {
                y = layout(node.children, this.indentSpacing + indent, y, plusMinusWidth);
            }
        }
        return y;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.Group, com.badlogic.gdx.scenes.scene2d.Actor
    public void draw(Batch batch, float parentAlpha) {
        drawBackground(batch, parentAlpha);
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        draw(batch, this.rootNodes, this.paddingLeft, plusMinusWidth());
        super.draw(batch, parentAlpha);
    }

    protected void drawBackground(Batch batch, float parentAlpha) {
        if (this.style.background != null) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            this.style.background.draw(batch, getX(), getY(), getWidth(), getHeight());
        }
    }

    private void draw(Batch batch, Array<N> nodes, float indent, float plusMinusWidth) {
        float cullBottom;
        float cullTop;
        float height;
        Actor actor;
        N node;
        int i;
        int n;
        float iconX;
        float iconX2;
        Array<N> array = nodes;
        Rectangle cullingArea = getCullingArea();
        if (cullingArea == null) {
            cullBottom = 0.0f;
            cullTop = 0.0f;
        } else {
            float cullBottom2 = cullingArea.y;
            float cullTop2 = cullBottom2 + cullingArea.height;
            cullBottom = cullBottom2;
            cullTop = cullTop2;
        }
        TreeStyle style = this.style;
        float x = getX();
        float y = getY();
        float expandX = x + indent;
        float iconX3 = expandX + plusMinusWidth + this.iconSpacingLeft;
        int i2 = 0;
        for (int n2 = array.size; i2 < n2; n2 = n) {
            N node2 = array.get(i2);
            Actor actor2 = node2.actor;
            float actorY = actor2.getY();
            float height2 = node2.height;
            if (cullingArea == null || (actorY + height2 >= cullBottom && actorY <= cullTop)) {
                if (this.selection.contains(node2) && style.selection != null) {
                    float iconX4 = getWidth();
                    height = height2;
                    actor = actor2;
                    node = node2;
                    i = i2;
                    n = n2;
                    iconX = iconX3;
                    drawSelection(node2, style.selection, batch, x, (y + actorY) - (this.ySpacing / 2.0f), iconX4, height2 + this.ySpacing);
                } else {
                    height = height2;
                    actor = actor2;
                    node = node2;
                    i = i2;
                    n = n2;
                    iconX = iconX3;
                    if (node == this.overNode && style.over != null) {
                        drawOver(node, style.over, batch, x, (y + actorY) - (this.ySpacing / 2.0f), getWidth(), height + this.ySpacing);
                    }
                }
                if (node.icon != null) {
                    float iconY = y + actorY + Math.round((height - node.icon.getMinHeight()) / 2.0f);
                    batch.setColor(actor.getColor());
                    drawIcon(node, node.icon, batch, iconX, iconY);
                    batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                }
                if (node.children.size <= 0) {
                    iconX2 = iconX;
                } else {
                    iconX2 = iconX;
                    Drawable expandIcon = getExpandIcon(node, iconX2);
                    float iconY2 = y + actorY + Math.round((height - expandIcon.getMinHeight()) / 2.0f);
                    drawExpandIcon(node, expandIcon, batch, expandX, iconY2);
                }
            } else if (actorY < cullBottom) {
                return;
            } else {
                node = node2;
                i = i2;
                n = n2;
                iconX2 = iconX3;
            }
            if (node.expanded && node.children.size > 0) {
                draw(batch, node.children, indent + this.indentSpacing, plusMinusWidth);
            }
            i2 = i + 1;
            array = nodes;
            iconX3 = iconX2;
        }
    }

    protected void drawSelection(N node, Drawable selection, Batch batch, float x, float y, float width, float height) {
        selection.draw(batch, x, y, width, height);
    }

    protected void drawOver(N node, Drawable over, Batch batch, float x, float y, float width, float height) {
        over.draw(batch, x, y, width, height);
    }

    protected void drawExpandIcon(N node, Drawable expandIcon, Batch batch, float x, float y) {
        expandIcon.draw(batch, x, y, expandIcon.getMinWidth(), expandIcon.getMinHeight());
    }

    protected void drawIcon(N node, Drawable icon, Batch batch, float x, float y) {
        icon.draw(batch, x, y, icon.getMinWidth(), icon.getMinHeight());
    }

    protected Drawable getExpandIcon(N node, float iconX) {
        boolean over = false;
        if (node == this.overNode && Gdx.app.getType() == Application.ApplicationType.Desktop && (!this.selection.getMultiple() || (!UIUtils.ctrl() && !UIUtils.shift()))) {
            float mouseX = screenToLocalCoordinates(tmp.set(Gdx.input.getX(), 0.0f)).x;
            if (mouseX >= 0.0f && mouseX < iconX) {
                over = true;
            }
        }
        if (over) {
            Drawable icon = node.expanded ? this.style.minusOver : this.style.plusOver;
            if (icon != null) {
                return icon;
            }
        }
        return node.expanded ? this.style.minus : this.style.plus;
    }

    public N getNodeAt(float y) {
        this.foundNode = null;
        getNodeAt(this.rootNodes, y, getHeight());
        return this.foundNode;
    }

    private float getNodeAt(Array<N> nodes, float y, float rowY) {
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            N node = nodes.get(i);
            float height = node.height;
            float rowY2 = rowY - (node.getHeight() - height);
            if (y >= (rowY2 - height) - this.ySpacing && y < rowY2) {
                this.foundNode = node;
                return -1.0f;
            }
            rowY = rowY2 - (this.ySpacing + height);
            if (node.expanded) {
                rowY = getNodeAt(node.children, y, rowY);
                if (rowY == -1.0f) {
                    return -1.0f;
                }
            }
        }
        return rowY;
    }

    void selectNodes(Array<N> nodes, float low, float high) {
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            N node = nodes.get(i);
            if (node.actor.getY() >= low) {
                if (node.isSelectable()) {
                    if (node.actor.getY() <= high) {
                        this.selection.add(node);
                    }
                    if (node.expanded) {
                        selectNodes(node.children, low, high);
                    }
                }
            } else {
                return;
            }
        }
    }

    public Selection<N> getSelection() {
        return this.selection;
    }

    public N getSelectedNode() {
        return this.selection.first();
    }

    public V getSelectedValue() {
        N node = this.selection.first();
        if (node == null) {
            return null;
        }
        return (V) node.getValue();
    }

    public TreeStyle getStyle() {
        return this.style;
    }

    public Array<N> getRootNodes() {
        return this.rootNodes;
    }

    public void updateRootNodes() {
        for (int i = this.rootNodes.size - 1; i >= 0; i--) {
            this.rootNodes.get(i).removeFromTree(this);
        }
        int n = this.rootNodes.size;
        for (int i2 = 0; i2 < n; i2++) {
            this.rootNodes.get(i2).addToTree(this);
        }
    }

    public N getOverNode() {
        return this.overNode;
    }

    public V getOverValue() {
        N n = this.overNode;
        if (n == null) {
            return null;
        }
        return (V) n.getValue();
    }

    public void setOverNode(N overNode) {
        this.overNode = overNode;
    }

    public void setPadding(float padding) {
        this.paddingLeft = padding;
        this.paddingRight = padding;
    }

    public void setPadding(float left, float right) {
        this.paddingLeft = left;
        this.paddingRight = right;
    }

    public void setIndentSpacing(float indentSpacing) {
        this.indentSpacing = indentSpacing;
    }

    public float getIndentSpacing() {
        return this.indentSpacing;
    }

    public void setYSpacing(float ySpacing) {
        this.ySpacing = ySpacing;
    }

    public float getYSpacing() {
        return this.ySpacing;
    }

    public void setIconSpacing(float left, float right) {
        this.iconSpacingLeft = left;
        this.iconSpacingRight = right;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public float getPrefWidth() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.prefWidth;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public float getPrefHeight() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.prefHeight;
    }

    public void findExpandedValues(Array<V> values) {
        findExpandedValues(this.rootNodes, values);
    }

    public void restoreExpandedValues(Array<V> values) {
        int n = values.size;
        for (int i = 0; i < n; i++) {
            N node = findNode(values.get(i));
            if (node != null) {
                node.setExpanded(true);
                node.expandTo();
            }
        }
    }

    static boolean findExpandedValues(Array<? extends Node> nodes, Array values) {
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            Node node = nodes.get(i);
            if (node.expanded && !findExpandedValues(node.children, values)) {
                values.add(node.value);
            }
        }
        return false;
    }

    public N findNode(V value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null.");
        }
        return (N) findNode(this.rootNodes, value);
    }

    static Node findNode(Array<? extends Node> nodes, Object value) {
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            Node node = nodes.get(i);
            if (value.equals(node.value)) {
                return node;
            }
        }
        int n2 = nodes.size;
        for (int i2 = 0; i2 < n2; i2++) {
            Node found = findNode(nodes.get(i2).children, value);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public void collapseAll() {
        collapseAll(this.rootNodes);
    }

    static void collapseAll(Array<? extends Node> nodes) {
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            Node node = nodes.get(i);
            node.setExpanded(false);
            collapseAll(node.children);
        }
    }

    public void expandAll() {
        expandAll(this.rootNodes);
    }

    static void expandAll(Array<? extends Node> nodes) {
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            nodes.get(i).expandAll();
        }
    }

    public ClickListener getClickListener() {
        return this.clickListener;
    }

    /* loaded from: classes21.dex */
    public static abstract class Node<N extends Node, V, A extends Actor> {
        A actor;
        boolean expanded;
        float height;
        Drawable icon;
        N parent;
        V value;
        final Array<N> children = new Array<>(0);
        boolean selectable = true;

        public Node(A actor) {
            if (actor == null) {
                throw new IllegalArgumentException("actor cannot be null.");
            }
            this.actor = actor;
        }

        public Node() {
        }

        public void setExpanded(boolean expanded) {
            Tree tree;
            if (expanded == this.expanded) {
                return;
            }
            this.expanded = expanded;
            if (this.children.size == 0 || (tree = getTree()) == null) {
                return;
            }
            if (expanded) {
                int n = this.children.size;
                for (int i = 0; i < n; i++) {
                    this.children.get(i).addToTree(tree);
                }
            } else {
                for (int i2 = this.children.size - 1; i2 >= 0; i2--) {
                    this.children.get(i2).removeFromTree(tree);
                }
            }
            tree.invalidateHierarchy();
        }

        protected void addToTree(Tree<N, V> tree) {
            tree.addActor(this.actor);
            if (this.expanded) {
                Object[] children = this.children.items;
                for (int i = this.children.size - 1; i >= 0; i--) {
                    ((Node) children[i]).addToTree(tree);
                }
            }
        }

        protected void removeFromTree(Tree<N, V> tree) {
            tree.removeActor(this.actor);
            if (this.expanded) {
                Object[] children = this.children.items;
                for (int i = this.children.size - 1; i >= 0; i--) {
                    ((Node) children[i]).removeFromTree(tree);
                }
            }
        }

        public void add(N node) {
            insert(this.children.size, node);
        }

        public void addAll(Array<N> nodes) {
            int n = nodes.size;
            for (int i = 0; i < n; i++) {
                insert(this.children.size, nodes.get(i));
            }
        }

        public void insert(int index, N node) {
            node.parent = this;
            this.children.insert(index, node);
            updateChildren();
        }

        public void remove() {
            Tree tree = getTree();
            if (tree != null) {
                tree.remove(this);
                return;
            }
            N n = this.parent;
            if (n != null) {
                n.remove(this);
            }
        }

        public void remove(N node) {
            Tree tree;
            this.children.removeValue(node, true);
            if (this.expanded && (tree = getTree()) != null) {
                node.removeFromTree(tree);
            }
        }

        public void removeAll() {
            Tree tree = getTree();
            if (tree != null) {
                Object[] children = this.children.items;
                for (int i = this.children.size - 1; i >= 0; i--) {
                    ((Node) children[i]).removeFromTree(tree);
                }
            }
            this.children.clear();
        }

        public Tree<N, V> getTree() {
            Group parent = this.actor.getParent();
            if (parent instanceof Tree) {
                return (Tree) parent;
            }
            return null;
        }

        public void setActor(A newActor) {
            Tree<N, V> tree;
            if (this.actor != null && (tree = getTree()) != null) {
                this.actor.remove();
                tree.addActor(newActor);
            }
            this.actor = newActor;
        }

        public A getActor() {
            return this.actor;
        }

        public boolean isExpanded() {
            return this.expanded;
        }

        public Array<N> getChildren() {
            return this.children;
        }

        public boolean hasChildren() {
            return this.children.size > 0;
        }

        public void updateChildren() {
            Tree tree;
            if (this.expanded && (tree = getTree()) != null) {
                for (int i = this.children.size - 1; i >= 0; i--) {
                    this.children.get(i).removeFromTree(tree);
                }
                int n = this.children.size;
                for (int i2 = 0; i2 < n; i2++) {
                    this.children.get(i2).addToTree(tree);
                }
            }
        }

        public N getParent() {
            return this.parent;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public V getValue() {
            return this.value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public Drawable getIcon() {
            return this.icon;
        }

        public int getLevel() {
            int level = 0;
            Node<N, V, A> node = this;
            do {
                level++;
                node = node.getParent();
            } while (node != null);
            return level;
        }

        public N findNode(V value) {
            if (value != null) {
                return value.equals(this.value) ? this : (N) Tree.findNode(this.children, value);
            }
            throw new IllegalArgumentException("value cannot be null.");
        }

        public void collapseAll() {
            setExpanded(false);
            Tree.collapseAll(this.children);
        }

        public void expandAll() {
            setExpanded(true);
            if (this.children.size > 0) {
                Tree.expandAll(this.children);
            }
        }

        public void expandTo() {
            for (Node node = this.parent; node != null; node = node.parent) {
                node.setExpanded(true);
            }
        }

        public boolean isSelectable() {
            return this.selectable;
        }

        public void setSelectable(boolean selectable) {
            this.selectable = selectable;
        }

        public void findExpandedValues(Array<V> values) {
            if (!this.expanded || Tree.findExpandedValues(this.children, values)) {
                return;
            }
            values.add(this.value);
        }

        public void restoreExpandedValues(Array<V> values) {
            int n = values.size;
            for (int i = 0; i < n; i++) {
                N node = findNode(values.get(i));
                if (node != null) {
                    node.setExpanded(true);
                    node.expandTo();
                }
            }
        }

        public float getHeight() {
            return this.height;
        }

        public boolean isAscendantOf(N node) {
            if (node == null) {
                throw new IllegalArgumentException("node cannot be null.");
            }
            Node current = node;
            while (current != this) {
                current = current.parent;
                if (current == null) {
                    return false;
                }
            }
            return true;
        }

        public boolean isDescendantOf(N node) {
            if (node == null) {
                throw new IllegalArgumentException("node cannot be null.");
            }
            Node parent = this;
            while (parent != node) {
                parent = parent.parent;
                if (parent == null) {
                    return false;
                }
            }
            return true;
        }
    }

    /* loaded from: classes21.dex */
    public static class TreeStyle {
        public Drawable background;
        public Drawable minus;
        public Drawable minusOver;
        public Drawable over;
        public Drawable plus;
        public Drawable plusOver;
        public Drawable selection;

        public TreeStyle() {
        }

        public TreeStyle(Drawable plus, Drawable minus, Drawable selection) {
            this.plus = plus;
            this.minus = minus;
            this.selection = selection;
        }

        public TreeStyle(TreeStyle style) {
            this.plus = style.plus;
            this.minus = style.minus;
            this.plusOver = style.plusOver;
            this.minusOver = style.minusOver;
            this.over = style.over;
            this.selection = style.selection;
            this.background = style.background;
        }
    }
}
