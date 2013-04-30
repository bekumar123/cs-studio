package org.csstudio.dct.model.persistence.xml;

import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.csstudio.dct.model.persistence.internal.xml.XmlHelper;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

public class XmlHelperTest {
    private String EXAMPLE_FILE = "/Users/roger/Documents/desy-git/cs-studio/applications/"
            + "plugins/org.csstudio.dct/beispiel/unit-test.css-dct";

    private XmlHelper xmlHelper;

    @Before
    public void setUp() throws JDOMException, IOException {
        xmlHelper = new XmlHelper(getExampleDocument());
    }

    @Test
    public void shouldGetInstancesFolder() {
        assertTrue(xmlHelper.getTopLevelInstancesFolder().isPresent());
    }

    @Test
    public void shouldFlattenChildren() {
        List<Element> elements = xmlHelper.flattenChildren(xmlHelper.getTopLevelInstancesFolder().get());
        assertTrue(elements.size() == 40);
    }

    @Test
    public void shouldFindInstancesWithId() {
        List<Element> elements = xmlHelper.getInstancesWithParentId("e61dcb81-0179-428a-98bb-c8656b5e4710");
        assertTrue(elements.size() == 2);
    }

    private Document getExampleDocument() throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        return builder.build(new FileInputStream(new File(EXAMPLE_FILE)));
    }
}
