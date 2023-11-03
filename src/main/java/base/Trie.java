package base;

import java.sql.*;
import java.util.List;

public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public boolean isEmpty() {
        return root == null;
    }

    public TrieNode getRoot() {
        return root;
    }


    public void insert(Word word) {
        TrieNode temp = root;

        for (char c : word.getWord_target().toCharArray()) {
            temp = temp.getChildren().computeIfAbsent(c, x -> new TrieNode());
        }
        temp.setEndOfWord(true);
    }

    public void insert(String word) {
        TrieNode temp = root;

        for (char c : word.toCharArray()) {
            temp = temp.getChildren().computeIfAbsent(c, x -> new TrieNode());
        }
        temp.setEndOfWord(true);
    }

    public void delete(String word) {
        delete(root, word, 0);
    }

    private boolean delete(TrieNode current, String word, int index) {
        if (index == word.length()) {
            if (!current.isEndOfWord()) {
                return false;
            }
            current.setEndOfWord(false);
            return current.getChildren().isEmpty();
        }

        char ch = word.charAt(index);
        TrieNode childNode = current.getChildren().get(ch);
        if (childNode == null) {
            return false;
        }

        boolean shouldDeleteChild = delete(childNode, word, index + 1);
        if (shouldDeleteChild) {
            current.getChildren().remove(ch);
            return current.getChildren().isEmpty();
        }

        return false;
    }

}
