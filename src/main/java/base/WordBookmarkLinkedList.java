package base;

public class WordBookmarkLinkedList extends WordLinkedList<String>{
    public WordBookmarkLinkedList(String tableName) {
        super(tableName);
    }

    @Override
    public boolean add(String word) {
        boolean add;
        if (!super.checkDuplicateValue(word)) {
            add = super.delete(word);
        }
        else {
            add = super.add(word);
        }
        return add;
    }

    @Override
    public boolean delete(String word) {
        return false;
    }
}
