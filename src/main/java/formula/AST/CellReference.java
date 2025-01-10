package formula.AST;

import spreadsheet.ColumnLabelConverter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CellReference implements Expression {
    public final String sheet;
    public final String reference;

    public final int row;
    public final int column;

    private static final String regex = "([A-Z]+)([1-9]\\d*)";
    private static final Pattern pattern = Pattern.compile(regex);

    public CellReference(String sheet, String reference){
        this.sheet = sheet;
        this.reference = reference;
        Matcher matcher = pattern.matcher(reference);
        if (matcher.find()) {
            column = ColumnLabelConverter.toIndex(matcher.group(1));
            row = Integer.parseInt(matcher.group(2));
        } else {
            column = -1;
            row = -1;
        }
    }

    @Override
    public void accept(Visitor v) {
        v.visitCellReference(this);
    }
}