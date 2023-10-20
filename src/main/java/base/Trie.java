package base;

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


    public void insert(Word word, String ipa, String wordType) {
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

}
