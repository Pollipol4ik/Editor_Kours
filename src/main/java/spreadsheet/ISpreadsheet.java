package spreadsheet;


import formula.evaluator.CyclicDependencyException;
import formula.evaluator.DependencyGraph;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * This interface represents a spreadsheet.
 */
public interface ISpreadsheet extends Iterable<ISheet> {
    /**
     * Reads spreadsheet from file
     *
     * @param fileName path to file
     */
    void open(String fileName) throws IOException, ParserConfigurationException, SAXException;

    /**
     * Saves spreadsheet to file
     *
     * @param fileName path to file
     */
    void save(String fileName) throws IOException, TransformerConfigurationException, ParserConfigurationException;

    /**
     * Saves spreadsheet to the same file
     */
    void save() throws IOException, ParserConfigurationException, TransformerConfigurationException;

    /**
     * Creates an empty spreadsheet with one sheet
     */
    void empty();

    /**
     * Get the number of sheets
     *
     * @return number of sheets in the spreadsheet
     */
    int getSheetCount();

    /**
     * Create a new sheet
     *
     * @return new sheet
     */
    ISheet addSheet();

    /**
     * Removes a sheet at given index
     *
     * @param index index of the sheet
     */
    default void removeSheet(int index) {
        getSheet(index).remove();
    }

    /**
     * Get list of sheets
     *
     * @return new sheet
     */
    List<? extends ISheet> getSheetList();

    /**
     * Get a sheet at given index
     *
     * @param index index of the sheet
     */
    ISheet getSheet(int index);

    /**
     * Get a sheet by given name
     *
     * @param name name of the sheet
     */
    ISheet getSheet(String name);

    DependencyGraph getDependencyGraph();

    /**
     * Check whether the sheet has been modified
     *
     * @return whether the sheet was modified
     */
    boolean isModified();

    /**
     * Get the name of the file
     *
     * @return the name of the file, if exists, otherwise null
     */
    String getFileName();

    /**
     * Evaluate formulae
     */
    void calculate() throws CyclicDependencyException;

    @Override
    default Iterator<ISheet> iterator() {
        return new Iterator<>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < getSheetCount();
            }

            @Override
            public ISheet next() {
                return getSheet(index++);
            }
        };
    }
}
