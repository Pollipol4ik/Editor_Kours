package spreadsheet;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

public class Sheet implements ISheet {
    private final Map<Integer, Column> columnMap = new HashMap<>();
    private String name = "Sheet1";
    private final Spreadsheet spreadsheet;

    public Sheet(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;
    }

    public Sheet(Spreadsheet spreadsheet, String name) {
        this(spreadsheet);
        this.name = name;
    }

    @Override
    public void remove() {

    }

    @Override
    public ICell getCellAt(int row, int col) {
        Column column = getColumnAt(col);
        return column.getCellAt(row);
    }

    @Override
    public Column getColumnAt(int col) {
        if (columnMap.get(col) == null) {
            columnMap.put(col, new Column(this, col));
        }
        return columnMap.get(col);
    }

    @Override
    public Object getValueAt(int row, int col) {
        ICell cell = getCellAt(row, col);
        return cell == null ? null : cell.getValue();
    }

    @Override
    public void removeColumn(int index, boolean keepWidth) {
        columnMap.remove(index);
        if (!keepWidth) {
            columnMap.keySet().stream().filter(i -> i > index).sorted()
                    .forEach(i -> {
                        columnMap.get(i).setColNumber(i - 1);
                        columnMap.put(i - 1, columnMap.get(i));
                        columnMap.remove(i);
                    });
        }
    }

    @Override
    public void removeColumns(int first, int last, boolean keepWidth) {
        for (int i = first; i < last; ++i) {
            removeColumn(i, true);
        }
        removeColumn(last, keepWidth);
    }

    @Override
    public void removeRow(int index, boolean keepLength) {

    }

    @Override
    public void removeRows(int first, int last, boolean keepLength) {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Spreadsheet getSpreadsheet() {
        return spreadsheet;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        Column column = getColumnAt(col);
        column.setValueAt(value, row);
    }

    public void save(Element sheetElement) {
        sheetElement.setAttribute("name", name);
        columnMap.forEach((colNum, column) -> {
            Element columnElement = sheetElement.getOwnerDocument().createElement("column");
            columnElement.setAttribute("index", colNum.toString());
            column.save(columnElement);
            if (columnElement.getChildNodes().getLength() != 0) {
                sheetElement.appendChild(columnElement);
            }
        });
    }

    public static void fillSheetFromXMLElement(Element sheetElement, Sheet targetSheet) {
        targetSheet.setName(sheetElement.getAttribute("name"));
        NodeList childNodeList = sheetElement.getChildNodes();
        for (int i = 0; i < childNodeList.getLength(); ++i) {
            Node node = childNodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element columnElement = (Element)node;
            targetSheet.columnMap.put(Integer.parseInt(columnElement.getAttribute("index")),
                    Column.columnFromXMLElement(targetSheet, columnElement));
        }
    }
}
