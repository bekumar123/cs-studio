package org.csstudio.dct.model.persistence.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.csstudio.dct.model.persistence.internal.xml.XmlDomUpdater;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

import com.google.common.base.Optional;

public class XmlUpdaterTest {

    private String EXAMPLE_FILE = "/Users/roger/Documents/desy-git/cs-studio/applications/"
            + "plugins/org.csstudio.dct/beispiel/unit-test.css-dct";

    private String LIBRARY_FILE = "/Users/roger/Documents/desy-git/cs-studio/applications/"
            + "plugins/org.csstudio.dct/beispiel/simple.css-dct";

    @Test
    public void shouldAddPrototypesFromLibaryFile() throws JDOMException, IOException, Exception {
        Document destDoc = getExampleDocument();
        Optional<Document> libraryDocument = getLibraryDocument();
        XmlDomUpdater xmlHelper = new XmlDomUpdater(destDoc, libraryDocument);
        xmlHelper.addPrototypesFromLibrary();
    }

    private Document getExampleDocument() throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        return builder.build(new FileInputStream(new File(EXAMPLE_FILE)));
    }

    private Optional<Document> getLibraryDocument() throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        return Optional.of(builder.build(new FileInputStream(new File(LIBRARY_FILE))));
    }

}
