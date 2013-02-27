package org.csstudio.dct.util;

import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.model.persistence.internal.PersistenceService;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;

public class CompareUtilRootFolderTest {

    private String EXAMPLE_FILE = "/Users/roger/Documents/desy-git/cs-studio/applications/"
            + "plugins/org.csstudio.dct/beispiel/unit-test.css-dct";

    private PersistenceService persistenceService;
    private Project project;
    
    @Before
    public void setUp() throws Exception {
        Project.IS_UNIT_TEST = true;
        persistenceService = new PersistenceService();
        project = persistenceService.loadProject(getExampleDocument(), getNoLibraryDocument());

    }

    @Test
    public void shouldRecognizeInstancesFolder() throws JDOMException, IOException, Exception {
        assertTrue(CompareUtil.containsInstancesFolder(getAsList(project.getInstancesFolder().get())));
    }

    @Test
    public void shouldRecognizePrototypesFolder() throws JDOMException, IOException, Exception {
        assertTrue(CompareUtil.containsPrototypesFolder(getAsList(project.getPrototypesFolder().get())));
    }

    @Test
    public void shouldRecognizeLibraryFolder() throws JDOMException, IOException, Exception {
        assertTrue(CompareUtil.containsLibraryFolder(getAsList(project.getLibraryFolder().get())));
    }

    private List<IElement> getAsList(IElement element) {
        List<IElement> list = new ArrayList<IElement>();
        list.add(element);
        return list;
    }
    
    private Document getExampleDocument() throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        return builder.build(new FileInputStream(new File(EXAMPLE_FILE)));
    }

    private Optional<Document> getNoLibraryDocument() {
        return Optional.absent();
    }

}
