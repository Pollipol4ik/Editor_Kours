package formula.AST;

import spreadsheet.ColumnLabelConverter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CellReference implements Expression {
    public final String sheet;
    public final String reference;

    public final int startRow;
    public final int startColumn;
    public final int endRow;
    public final int endColumn;

    private static final String regex = "([A-Z]+)([1-9]\\d*)(:([A-Z]+)([1-9]\\d*))?";
    private static final Pattern pattern = Pattern.compile(regex);

    public CellReference(String sheet, String reference) {
        this.sheet = sheet;
        this.reference = reference;

        Matcher matcher = pattern.matcher(reference);
        if (matcher.find()) {
            startColumn = ColumnLabelConverter.toIndex(matcher.group(1));  // Преобразование столбца (например, "A" -> 0)
            startRow = Integer.parseInt(matcher.group(2)) ;  // Индексы строк начинаются с 0

            if (matcher.group(3) != null) {  // Если это диапазон (например, A2:A5)
                endColumn = ColumnLabelConverter.toIndex(matcher.group(4));  // Конечный столбец
                endRow = Integer.parseInt(matcher.group(5)) - 1;  // Конечная строка
            } else {  // Если это одиночная ячейка
                endColumn = startColumn;
                endRow = startRow;
            }
        } else {
            // Если строка не соответствует формату
            startColumn = -1;
            startRow = -1;
            endColumn = -1;
            endRow = -1;
        }
    }

    public boolean isRange() {
        return startRow != endRow || startColumn != endColumn;  // Проверка, что это диапазон
    }

    @Override
    public void accept(Visitor v) {
        v.visitCellReference(this);
    }
}
