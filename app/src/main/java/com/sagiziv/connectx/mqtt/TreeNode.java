package com.sagiziv.connectx.mqtt;

import android.util.Pair;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

public class TreeNode<TIdentifier, TValue> implements Iterable<TValue> {

    private TIdentifier identifier;
    private TreeNode<TIdentifier, TValue> parent;
    private TValue value;
    private final Map<TIdentifier, TreeNode<TIdentifier, TValue>> children;

    public TreeNode() {
        children = new HashMap<>();
    }

    public TreeNode(final TIdentifier identifier, final TValue value) {
        this();
        this.identifier = identifier;
        this.value = value;
    }

    public Collection<TreeNode<TIdentifier, TValue>> getChildren() {
        return children.values();
    }

    public TreeNode<TIdentifier, TValue> addChild(final TIdentifier id, final TValue val) {
        final TreeNode<TIdentifier, TValue> child = new TreeNode<>(id, val);
        child.parent = this;
        children.put(id, child);
        return child;
    }

    public Pair<Boolean, TreeNode<TIdentifier, TValue>> tryGetChild(final TIdentifier id) {
        final TreeNode<TIdentifier, TValue> child = children.getOrDefault(id, null);
        return child == null
                ? new Pair<>(false, null)
                : new Pair<>(true, child);
    }

    public void deleteChild(final TIdentifier id) {
        final TreeNode<TIdentifier, TValue> child = children.remove(id);
        if (child != null)
            child.parent = null; // Remove the reference to the parent to avoid memory leaks.
    }

    public void clear() {
        for (final TreeNode<TIdentifier, TValue> child : getChildren()) {
            // Make sure to break the strong-reference, so the parent can be garbage collected.
            child.parent = null;
            child.clear();
        }

        children.clear();
    }

    public TIdentifier getIdentifier() {
        return identifier;
    }

    public TreeNode<TIdentifier, TValue> setIdentifier(final TIdentifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public TreeNode<TIdentifier, TValue> getParent() {
        return parent;
    }

    public TreeNode<TIdentifier, TValue> setParent(final TreeNode<TIdentifier, TValue> parent) {
        this.parent = parent;
        return this;
    }

    public TValue getValue() {
        return value;
    }

    public TreeNode<TIdentifier, TValue> setValue(final TValue value) {
        this.value = value;
        return this;
    }

    @NonNull
    @Override
    public Iterator<TValue> iterator() {
        return new TreeNodeIterator();
    }

    private class TreeNodeIterator implements Iterator<TValue> {
        private final Queue<TreeNode<TIdentifier, TValue>> toIterate;

        public TreeNodeIterator() {
            toIterate = getChildren()
                    .stream()
                    .filter(child -> child.value != null)
                    .collect(Collectors.toCollection(LinkedList::new));
        }

        @Override
        public boolean hasNext() {
            return !toIterate.isEmpty();
        }

        @Override
        public TValue next() {
            final TreeNode<TIdentifier, TValue> child = toIterate.remove();
            child.getChildren()
                    .stream()
                    .filter(subChild -> subChild.value != null)
                    .forEach(toIterate::add);
            return child.value;
        }
    }
}
