package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.BinaryHeap.Node;
/* loaded from: classes21.dex */
public class BinaryHeap<T extends Node> {
    private final boolean isMaxHeap;
    private Node[] nodes;
    public int size;

    public BinaryHeap() {
        this(16, false);
    }

    public BinaryHeap(int capacity, boolean isMaxHeap) {
        this.isMaxHeap = isMaxHeap;
        this.nodes = new Node[capacity];
    }

    public T add(T node) {
        int i = this.size;
        Node[] nodeArr = this.nodes;
        if (i == nodeArr.length) {
            Node[] newNodes = new Node[i << 1];
            System.arraycopy(nodeArr, 0, newNodes, 0, i);
            this.nodes = newNodes;
        }
        int i2 = this.size;
        node.index = i2;
        this.nodes[i2] = node;
        this.size = i2 + 1;
        up(i2);
        return node;
    }

    public T add(T node, float value) {
        node.value = value;
        return add(node);
    }

    public boolean contains(T node, boolean identity) {
        Node[] nodeArr;
        Node[] nodeArr2;
        if (node == null) {
            throw new IllegalArgumentException("node cannot be null.");
        }
        if (identity) {
            for (Node n : this.nodes) {
                if (n == node) {
                    return true;
                }
            }
        } else {
            for (Node other : this.nodes) {
                if (other.equals(node)) {
                    return true;
                }
            }
        }
        return false;
    }

    public T peek() {
        if (this.size == 0) {
            throw new IllegalStateException("The heap is empty.");
        }
        return (T) this.nodes[0];
    }

    public T pop() {
        return remove(0);
    }

    public T remove(T node) {
        return remove(node.index);
    }

    private T remove(int index) {
        Node[] nodes = this.nodes;
        T t = (T) nodes[index];
        int i = this.size - 1;
        this.size = i;
        nodes[index] = nodes[i];
        int i2 = this.size;
        nodes[i2] = null;
        if (i2 > 0 && index < i2) {
            down(index);
        }
        return t;
    }

    public boolean notEmpty() {
        return this.size > 0;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void clear() {
        Node[] nodes = this.nodes;
        int n = this.size;
        for (int i = 0; i < n; i++) {
            nodes[i] = null;
        }
        this.size = 0;
    }

    public void setValue(T node, float value) {
        float oldValue = node.value;
        node.value = value;
        if ((value < oldValue) ^ this.isMaxHeap) {
            up(node.index);
        } else {
            down(node.index);
        }
    }

    private void up(int index) {
        Node[] nodes = this.nodes;
        Node node = nodes[index];
        float value = node.value;
        while (index > 0) {
            int parentIndex = (index - 1) >> 1;
            Node parent = nodes[parentIndex];
            if (!((value < parent.value) ^ this.isMaxHeap)) {
                break;
            }
            nodes[index] = parent;
            parent.index = index;
            index = parentIndex;
        }
        nodes[index] = node;
        node.index = index;
    }

    private void down(int index) {
        Node rightNode;
        float rightValue;
        Node[] nodes = this.nodes;
        int size = this.size;
        Node node = nodes[index];
        float value = node.value;
        while (true) {
            int leftIndex = (index << 1) + 1;
            if (leftIndex >= size) {
                break;
            }
            int rightIndex = leftIndex + 1;
            Node leftNode = nodes[leftIndex];
            float leftValue = leftNode.value;
            if (rightIndex >= size) {
                rightNode = null;
                rightValue = this.isMaxHeap ? -3.4028235E38f : Float.MAX_VALUE;
            } else {
                rightNode = nodes[rightIndex];
                rightValue = rightNode.value;
            }
            if ((leftValue < rightValue) ^ this.isMaxHeap) {
                if (leftValue == value) {
                    break;
                }
                if ((leftValue > value) ^ this.isMaxHeap) {
                    break;
                }
                nodes[index] = leftNode;
                leftNode.index = index;
                index = leftIndex;
            } else if (rightValue == value) {
                break;
            } else {
                if ((rightValue > value) ^ this.isMaxHeap) {
                    break;
                }
                nodes[index] = rightNode;
                rightNode.index = index;
                index = rightIndex;
            }
        }
        nodes[index] = node;
        node.index = index;
    }

    public boolean equals(Object obj) {
        if (obj instanceof BinaryHeap) {
            BinaryHeap other = (BinaryHeap) obj;
            if (other.size != this.size) {
                return false;
            }
            Node[] nodes1 = this.nodes;
            Node[] nodes2 = other.nodes;
            int n = this.size;
            for (int i = 0; i < n; i++) {
                if (nodes1[i].value != nodes2[i].value) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        int h = 1;
        int n = this.size;
        for (int i = 0; i < n; i++) {
            h = (h * 31) + Float.floatToIntBits(this.nodes[i].value);
        }
        return h;
    }

    public String toString() {
        if (this.size == 0) {
            return "[]";
        }
        Node[] nodes = this.nodes;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        buffer.append(nodes[0].value);
        for (int i = 1; i < this.size; i++) {
            buffer.append(", ");
            buffer.append(nodes[i].value);
        }
        buffer.append(']');
        return buffer.toString();
    }

    /* loaded from: classes21.dex */
    public static class Node {
        int index;
        float value;

        public Node(float value) {
            this.value = value;
        }

        public float getValue() {
            return this.value;
        }

        public String toString() {
            return Float.toString(this.value);
        }
    }
}
