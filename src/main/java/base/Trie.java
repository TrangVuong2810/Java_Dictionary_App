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

//    public boolean find(String word) {
//        TrieNode temp = root;
//
//        for (int i = 0; i < word.length(); ++i) {
//            TrieNode nextNode;
//            if ((nextNode = temp.getChildren().get(word.charAt(i))) == null) {
//                return false;
//            }
//            temp = nextNode;
//        }
//        return temp.isEndOfWord();
//    }



    public void insert(String word) {
        TrieNode temp = root;

        for (char c : word.toCharArray()) {
            temp = temp.getChildren().computeIfAbsent(c, x -> new TrieNode());
        }
        temp.setEndOfWord(true);
    }

//    public void seacher(String word) {
//        TrieNode temp = root;
//
//        for (int i = 0; i < word.length(); ++i) {
//            TrieNode nextNode;
//            if ((nextNode = temp.getChildren().get(word.charAt(i))) == null) {
//                return;
//            }
//            temp = nextNode;
//        }
//        printAllWordWithPrefix(temp, word);
//    }

//    public void lookup(String word) {
//        TrieNode temp = root;
//
//        for (int i = 0; i < word.length(); ++i) {
//            TrieNode nextNode;
//            if ((nextNode = temp.getChildren().get(word.charAt(i))) == null) {
//                System.out.println("Word not found in dictionary");
//                return;
//            }
//            temp = nextNode;
//        }
//        if (temp.isEndOfWord()) {
//            System.out.println(temp.getMeaning());
//        } else {
//            System.out.println("Word not found in dictionary");
//        }
//    }

//    public boolean delete(TrieNode root, String word, int depth) {
//        if (root == null) {
//            return false;
//        }
//
//        if (depth == word.length()) {
//            if (root.isEndOfWord()) {
//                root.setEndOfWord(false);
//            }
//            return root.getChildren().isEmpty();
//        }
//
//        TrieNode nextNode = root.getChildren().get(word.charAt(depth));
//
//        if (nextNode == null) {
//            return false;
//        }
//        if (delete(nextNode, word, depth + 1) && !nextNode.isEndOfWord()) {
//            root.getChildren().remove(word.charAt(depth));
//            return root.getChildren().isEmpty();
//        }
//        return false;
//    }

//    public void edit(String word, String value) {
//        TrieNode temp = root;
//
//        for (int i = 0; i < word.length(); ++i) {
//            TrieNode nextNode;
//            if ((nextNode = temp.getChildren().get(word.charAt(i))) == null) {
//                System.out.println("Word not found in dictionary");
//                return;
//            }
//            temp = nextNode;
//        }
//        if (temp.isEndOfWord()) {
//            temp.setMeaning(value);
//        }
//    }
}
