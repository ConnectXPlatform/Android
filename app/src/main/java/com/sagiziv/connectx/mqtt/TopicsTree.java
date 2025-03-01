package com.sagiziv.connectx.mqtt;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sagiziv.connectx.mqtt.topics.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Stack;
import java.util.function.Consumer;

public class TopicsTree<TMessage> implements Iterable<Consumer<TMessage>> {

    private final TreeNode<String, Consumer<TMessage>> root;

    public TopicsTree() {
        root = new TreeNode<>();
    }

    public void insertTopicValue(final String topicPattern, final Consumer<TMessage> value) {
        final TreeNode<String, Consumer<TMessage>> currentNode = createNodesForTopicPattern(topicPattern);
        Consumer<TMessage> newValue = currentNode.getValue();
        if (newValue == null)
            newValue = value;
        else
            newValue = newValue.andThen(value);
        currentNode.setValue(newValue);
    }

    public void clear() {
        root.clear();
    }

    public boolean deleteTopic(final String topicPattern) {
        final TreeNode<String, Consumer<TMessage>> currentNode = getNodeForTopicPattern(topicPattern);
        if (currentNode == null)
            return false;
        final TreeNode<String, Consumer<TMessage>> parentNode = currentNode.getParent();
        parentNode.deleteChild(currentNode.getIdentifier());
        return true;
    }

    public Collection<Consumer<TMessage>> getValuesFor(final String topic) {
        final List<Consumer<TMessage>> matchingValues = new ArrayList<>(root.getChildren().size());
        findMatchingNodesForTopic(topic, node ->
        {
            // We might have reached an old parent whose children were deleted.
            // If the value of the current node is not null, add it to the list of matching values.
            if (node.getValue() != null)
                matchingValues.add(node.getValue());
        });
        return matchingValues;
    }

    public Collection<String> getMatchingSubTopicForTopic(final String topic) {
        final List<String> matchingValues = new ArrayList<>();
        final Stack<String> segments = new Stack<>();
        findMatchingNodesForTopic(topic, node ->
        {
            // We might have reached an old parent whose children were deleted.
            // If the value of the current node is not null, add it to the list of matching values.
            if (node.getValue() == null) return;
            segments.clear();
            TreeNode<String, Consumer<TMessage>> current = node;
            while (current != null &&
                    current.getIdentifier() != null &&
                    !current.getIdentifier().isEmpty()) {
                segments.add(current.getIdentifier());
                current = current.getParent();
            }

            matchingValues.add(String.join("/", segments));
        });
        return matchingValues;
    }

    private void findMatchingNodesForTopic(@NonNull final String topic, Consumer<TreeNode<String, Consumer<TMessage>>> onNodeFound) {
        final String[] segments = topic.split(Constants.Separator);
        // Find the first set of child nodes that match the first topic segment
        final Collection<Pair<TreeNode<String, Consumer<TMessage>>, Integer>> matchingChildren = findChildrenWithSubTopic(root, segments[0], 0);
        final Queue<Pair<TreeNode<String, Consumer<TMessage>>, Integer>> nodesQueue = new LinkedList<>(matchingChildren);
        while (!nodesQueue.isEmpty()) {
            final Pair<TreeNode<String, Consumer<TMessage>>, Integer> current = nodesQueue.remove();
            // If we got to the last segment
            if (current.second == segments.length - 1 ||
                    Objects.equals(current.first.getIdentifier(), "*")) {
                onNodeFound.accept(current.first);
                continue;
            }

            // Find all child nodes that match the next topic segment and add them to the queue.
            final int nextLevel = current.second + 1;
            nodesQueue.addAll(findChildrenWithSubTopic(current.first, segments[nextLevel], nextLevel));
        }
    }

    private Collection<Pair<TreeNode<String, Consumer<TMessage>>, Integer>> findChildrenWithSubTopic(
            final TreeNode<String, Consumer<TMessage>> parent, final String subTopic, final int level) {
        final List<Pair<TreeNode<String, Consumer<TMessage>>, Integer>> children = new LinkedList<>();
        for (final TreeNode<String, Consumer<TMessage>> childNode : parent.getChildren()) {
            if (Objects.equals(childNode.getIdentifier(), subTopic) ||
                    Objects.equals(childNode.getIdentifier(), "*") ||
                    Objects.equals(childNode.getIdentifier(), "+"))
                children.add(new Pair<>(childNode, level));
        }
        return children;
    }

    private TreeNode<String, Consumer<TMessage>> createNodesForTopicPattern(@NonNull final String topicPattern) {
        final String[] segments = topicPattern.split("/");
        TreeNode<String, Consumer<TMessage>> currentNode = root;
        for (final String segment : segments) {
            final Pair<Boolean, TreeNode<String, Consumer<TMessage>>> result = currentNode.tryGetChild(segment);
            TreeNode<String, Consumer<TMessage>> child = result.second;
            if (!result.first) {
                child = currentNode.addChild(segment, null);
            }

            currentNode = child;
        }

        return currentNode;
    }

    @Nullable
    private TreeNode<String, Consumer<TMessage>> getNodeForTopicPattern(@NonNull final String topicPattern) {
        final String[] segments = topicPattern.split(Constants.Separator);
        TreeNode<String, Consumer<TMessage>> currentNode = root;
        for (final String segment : segments) {
            final Pair<Boolean, TreeNode<String, Consumer<TMessage>>> result = currentNode.tryGetChild(segment);
            final TreeNode<String, Consumer<TMessage>> child = result.second;
            if (!result.first) {
                return null;
            }

            currentNode = child;
        }

        return currentNode;
    }

    @NonNull
    @Override
    public Iterator<Consumer<TMessage>> iterator() {
        return root.iterator();
    }
}
