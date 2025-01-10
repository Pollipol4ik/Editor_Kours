package spreadsheet;

public class ColumnLabelConverter {
    static public String toLabel(int index) {
        String result = "";
        //index++;
        do {
            result = (char)('A' + index % 26) + result;
            index /= 26;
        } while (index > 0);
        return result;
    }

    static public int toIndex(String label) {
        int result = 0;
        for (char c : label.toCharArray()) {
            result = result * 26 + c - 'A' + 1;
        }
        return result - 1;
    }
}
