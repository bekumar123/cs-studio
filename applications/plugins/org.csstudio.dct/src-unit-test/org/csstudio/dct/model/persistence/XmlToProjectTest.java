package org.csstudio.dct.model.persistence;

import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.model.persistence.internal.XmlToProject;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

public class XmlToProjectTest {

    private String EXAMPLE_FILE = "/Users/roger/Documents/desy-git/cs-studio/applications/"
            + "plugins/org.csstudio.dct/beispiel/unit-test.css-dct";

    private XmlToProject xmlToProject;

    @Before
    public void setUp() throws JDOMException, IOException {
        Project.IS_UNIT_TEST = true;
        xmlToProject = new XmlToProject(getExampleDocument());
    }

    @Test
    public void shouldGetLibraryFolder() {
        assertTrue(xmlToProject.createProject().getLibraryFolder().isPresent());
    }

    @Test
    public void shouldGetPrototypesFolder() {
        assertTrue(xmlToProject.createProject().getPrototypesFolder().isPresent());
    }

    private Document getExampleDocument() throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        return builder.build(new FileInputStream(new File(EXAMPLE_FILE)));
    }

}
