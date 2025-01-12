package spreadsheet;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class SpreadsheetOpener {
    public static ISpreadsheet open(String fileName) throws IOException, UnsupportedFileExtensionException, ParserConfigurationException, SAXException {
        ISpreadsheet answer;
        if (fileName.toLowerCase().matches(".*\\.ods$") || fileName.toLowerCase().matches(".*\\.spreadsheet$")) {
            answer = new Spreadsheet();
            answer.open(fileName);
        } else {
            throw new UnsupportedFileExtensionException("Unsupported file extension!");
        }
        return answer;
    }

    public static ISpreadsheet open(File file) throws IOException, UnsupportedFileExtensionException, ParserConfigurationException, SAXException {
        return open(file.getName());
    }

    public static class UnsupportedFileExtensionException extends Exception {
        UnsupportedFileExtensionException(String message) {
            super(message);
        }
    }
}