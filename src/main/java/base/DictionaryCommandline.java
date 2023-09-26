package base;

import com.almasb.fxgl.audio.Sound;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.util.ArrayList;
import java.util.Scanner;

public class DictionaryCommandline extends Dictionary{
    public static void showAllWords() {
        System.out.printf("%-5s %c %-20s %c %-20s %n", "No", '|' ,"English", '|', "Vietnamese");
        for (int i = 0; i < vocabulary.size(); i++) {
            System.out.printf("%-5d %c %-20s %c %-20s %n", i + 1,'|', vocabulary.get(i).getWord_target(), '|',vocabulary.get(i).getWord_explain());
        }
    }
    public static void dictionaryBasic(){
        DictionaryManagement.insertFromCommandline();
        showAllWords();
    }
    public static void showWordsFound(String word, int index) {
        if (index < 0) {
            System.out.println("Word not found");
            return;
        }
        ArrayList<Word> wordsFound = new ArrayList<Word>();
        int j = index;
        while (j >= 0) {
            if (order(word, vocabulary.get(j).getWord_target()) == 0) {
                j--;
            } else {
                j++;
                break;
            }
        }
        for (int i = j; i < index; i++) {
            Word temp = new Word(vocabulary.get(i).getWord_target(), vocabulary.get(i).getWord_explain());
            wordsFound.add(temp);
        }
        for (int i = index; i < vocabulary.size(); i++) {
            if (order(word, vocabulary.get(i).getWord_target()) == 0) {
                Word temp = new Word(vocabulary.get(i).getWord_target(), vocabulary.get(i).getWord_explain());
                wordsFound.add(temp);
            } else {
                break;
            }
        }
        System.out.println("Các từ tìm được: ");
        for (Word w : wordsFound) {
            System.out.printf("%-20s %c %-20s %n", w.getWord_target(), '|', w.getWord_explain());
        }
    }
    public static int order(String str1, String str2) {
        for (int i = 0; i < Math.min(str1.length(), str2.length()); i++) {
            if (str1.charAt(i) > str2.charAt(i)) {
                //behind
                return 1;
            } else if (str1.charAt(i) < str2.charAt(i)) {
                //in front
                return -1;
            }
        }
        if (str1.length() > str2.length()) {
            //behind
            return 1;
        }
        // same/starts with
        return 0;
    }
    public static int binarySearch(int start, int end, String word) {
        if (end < start) {
            return -1;
        }
        int mid = start + (end - start) / 2;
        int position = order(word, vocabulary.get(mid).getWord_target());
        if (position == -1) {
            return binarySearch(start, mid - 1, word);
        } else if (position == 1) {
            return binarySearch(mid + 1, end, word);
        } else {
            return mid;
        }
    }
    public static void dictionarySearcher() {
        Scanner scanner = new Scanner(System.in);
        String word = scanner.nextLine().toLowerCase();
        int index = binarySearch(0, vocabulary.size() - 1, word);
        if (DictionaryManagement.dictionaryLookup(word, index)) {
            System.out.println("Nghĩa của từ: " + vocabulary.get(index).getWord_explain());
        }
        else {
            showWordsFound(word, index);
        }
    }
    public static void dictionaryAdvanced() {

    }

    /** test. */
    public static void main(String[] args) {
        DictionaryManagement.insertFromFile();
        //DictionaryManagement.addWord("moon", "mặt trời");
        DictionaryManagement.modifyWord("moon", "mặt trăng");
        DictionaryManagement.dictionaryExportToFile();
        //dictionarySearcher();
    }
}
