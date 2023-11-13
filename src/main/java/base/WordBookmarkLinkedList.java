package base;

import javafx.stage.Stage;

public class WordBookmarkLinkedList<String> extends WordLinkedList<String>{
    public WordBookmarkLinkedList(String tableName) {
        super(tableName);
    }

    @Override
    public boolean add(String word) {
        boolean add;
        if (!super.checkDuplicateValue(word)) {
            add = super.delete((java.lang.String) word);
        }
        else {
            add = super.add(word);
        }
        return add;
    }
}
