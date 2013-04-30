package org.csstudio.dct.model.persistence;

import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.model.persistence.internal.PersistenceService;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;

public class PersistenceServiceTest {

    private String EXAMPLE_FILE = "/Users/roger/Documents/desy-git/cs-studio/applications/"
            + "plugins/org.csstudio.dct/beispiel/unit-test.css-dct";

    private String LIBRARY_FILE = "/Users/roger/Documents/desy-git/cs-studio/applications/"
            + "plugins/org.csstudio.dct/beispiel/simple.css-dct";

    private PersistenceService persistenceService;

    @Before
    public void setUp() throws JDOMException, IOException {
        Project.IS_UNIT_TEST = true;
        persistenceService = new PersistenceService();
    }

    @Test
    public void shouldCreateWithoutLibaryFile() throws JDOMException, IOException, Exception {
        persistenceService.loadProject(getExampleDocument(), getNoLibraryDocument());
    }

    @Test
    public void shouldCreateWithLibaryFile() throws JDOMException, IOException, Exception {
        IProject project = persistenceService.loadProject(getExampleDocument(), getLibraryDocument());
        IFolder libraryFolder = project.getLibraryFolder().get();
        assertTrue(libraryFolder.getMembers().size() > 0);
    }

    private Document getExampleDocument() throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        return builder.build(new FileInputStream(new File(EXAMPLE_FILE)));
    }

    private Optional<Document> getNoLibraryDocument() {
        return Optional.absent();
    }

    private Optional<Document> getLibraryDocument() throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        return Optional.of(builder.build(new FileInputStream(new File(LIBRARY_FILE))));
    }

}
