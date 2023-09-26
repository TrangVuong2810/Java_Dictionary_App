package base;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

public class DictionaryManagement extends Dictionary{
    private static final String VOCAB_PATH_IN = "src/main/resource/vocabulary_in.txt";
    private static final String VOCAB_PATH_OUT = "src/main/resource/vocabulary_out.txt";
    public static void insertFromCommandline() {
        Scanner scanner = new Scanner(System.in);
        int wordNum = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < wordNum; i++) {
            String word_target = scanner.nextLine().toLowerCase();
            String word_explain = scanner.nextLine().toLowerCase();
            Word newWord = new Word(word_target, word_explain);
            vocabulary.add(newWord);
        }
    }
    public static void insertFromFile() {
        try {
            File file = new File(VOCAB_PATH_IN);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] wordsLine = line.split("\\\\t");
                Word temp = new Word(wordsLine[0].toLowerCase(), wordsLine[1].toLowerCase());
                vocabulary.add(temp);
            }
            bufferedReader.close();
            Collections.sort(vocabulary);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void dictionaryExportToFile() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(VOCAB_PATH_OUT));
            for (Word word : vocabulary) {
                bufferedWriter.write(String.format("%-20s %c %-20s %n", word.getWord_target(), '|', word.getWord_explain()));
            }
            //bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean dictionaryLookup(String word, int index) {
        if (index < 0) {
            return false;
        }
        return vocabulary.get(index).getWord_target().equals(word);
    }
    public static int binaryCheck(int start, int end, String word) {
        if (end < start) {
            return -1;
        }
        int mid = start + (end - start) / 2;
        int compareNext = word.compareTo(vocabulary.get(mid).getWord_target());
        if (mid == 0) {
            if (compareNext < 0) {
                return 0;
            } else if (compareNext > 0) {
                return binaryCheck(mid + 1, end, word);
            } else {
                return -1;
            }
        } else {
            int comparePrevious = word.compareTo(vocabulary.get(mid - 1).getWord_target());
            if (comparePrevious > 0 && compareNext < 0) {
                return mid;
            } else if (comparePrevious < 0) {
                return binaryCheck(start, mid - 1, word);
            } else if (compareNext > 0) {
                if (mid == vocabulary.size() - 1) {
                    return vocabulary.size();
                }
                return binaryCheck(mid + 1, end, word);
            } else {
                return -1;
            }
        }
    }
    public static void updateWordFile() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(VOCAB_PATH_IN));
            for (Word word : vocabulary) {
                bufferedWriter.write(word.getWord_target() + "\\t" + word.getWord_explain() + "\n");
            }
            //bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void addWord(String word_target, String word_explain) {
        word_target = word_target.toLowerCase();
        word_explain = word_explain.toLowerCase();
        int pos = binaryCheck(0, vocabulary.size() - 1, word_target);
        if (pos == -1) {
            System.out.println("WORD IS ALREADY EXISTED");
            return;
        }
        vocabulary.add(pos, new Word(word_target, word_explain));
        updateWordFile();
    }
    public static void modifyWord(String word_target, String word_explain) {
        word_target = word_target.toLowerCase();
        word_explain = word_explain.toLowerCase();
        int pos = -1;
        pos = DictionaryCommandline.binarySearch(0, vocabulary.size() - 1, word_target);
        if (pos >= 0) {
            if (vocabulary.get(pos).getWord_explain().equals(word_explain)) {
                System.out.println("WORD_EXPLAIN NOT CHANGE");
                return;
            }
            vocabulary.get(pos).setWord_explain(word_explain);
        } else {
            System.out.println("WORD NEEDS TO BE MODIFIED NOT FOUND");
        }
        updateWordFile();
    }
    public static void removeWord(String word_target) {
        word_target = word_target.toLowerCase();
        int pos = DictionaryCommandline.binarySearch(0, vocabulary.size() - 1, word_target);
        if (pos >= 0) {
            vocabulary.remove(vocabulary.get(pos));
        } else {
            System.out.println("WORD NEEDS TO BE REMOVED NOT FOUND");
        }
        updateWordFile();
    }
}
