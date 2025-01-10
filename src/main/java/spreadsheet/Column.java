package spreadsheet;

import formula.evaluator.DependencyGraph;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class Column implements IColumn {
    Map<Integer, Cell> cellMap = new HashMap<Integer, Cell>();
    private int colNumber;
    private final Sheet sheet;


    public Column(Sheet sheet, int colNumber) {
        this.sheet = sheet;
        this.colNumber = colNumber;
    }

    @Override
    public int getColNumber() {
        return colNumber;
    }

    @Override
    public void setColNumber(int colNumber) {
        this.colNumber = colNumber;
    }

    public ICell getCellAt(int row) {
        if (cellMap.get(row) == null) {
            cellMap.put(row, new Cell(this, row, null));
        }
        return cellMap.get(row);
    }

    @Override
    public ISheet getSheet() {
        return sheet;
    }

    @Override
    public void setValueAt(Object value, int row) {
        ICell cell = getCellAt(row);
        cell.setValue(value);
    }

    public void save(Element columnElement) {
        cellMap.forEach((rowNum, cell) -> {
            if (cell.getValue() != null) {
                Element cellElement = columnElement.getOwnerDocument().createElement("cell");
                columnElement.appendChild(cellElement);
                cellElement.setAttribute("index", rowNum.toString());
                cell.save(cellElement);
            }
        });
    }

    public static Column columnFromXMLElement(Sheet sheet, Element columnElement) {
        Column result = new Column(sheet, Integer.parseInt(columnElement.getAttribute("index")));
        NodeList childNodeList = columnElement.getChildNodes();
        for (int i = 0; i < childNodeList.getLength(); ++i) {
            Node node = childNodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element cellElement = (Element)node;
            result.cellMap.put(Integer.parseInt(cellElement.getAttribute("index")), Cell.cellFromXMLElement(cellElement, result));
        }
        return result;
    }

    public void addDependencies(DependencyGraph dependencyGraph){
        cellMap.values().forEach(cell -> cell.addDependencies(dependencyGraph));
    }
}
