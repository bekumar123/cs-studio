package org.csstudio.dct.model.persistence.internal.xml;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class XmlDomUpdater {

    private static String PROTOYPES = "Prototypes";

    private static final Logger LOG = LoggerFactory.getLogger(XmlDomUpdater.class);

    private final Document destDocument;
    private final Optional<Document> libraryDocument;

    public XmlDomUpdater(final Document destDocument, final Optional<Document> libraryDocument) {
        this.destDocument = destDocument;
        this.libraryDocument = libraryDocument;
    }

    public void addPrototypesFromLibrary() {
        if (!libraryDocument.isPresent()) {
            LOG.info("Library document not present");
            return;
        }

        LOG.info("Using Library: " + libraryDocument.get().toString());

        Optional<Element> libraryElement = getLibraryElement();

        if (!libraryElement.isPresent()) {
            LOG.error("No Prototypes node in Library");
            return;
        }

        @SuppressWarnings("unchecked")
        List<Element> srcRoot = new ArrayList<Element>(libraryElement.get().getChildren());

        if (srcRoot.isEmpty()) {
            LOG.error("No elements in Library");
            return;
        }

        for (Element element : srcRoot) {
            element.detach();
        }

        XmlHelper xmlHelperDestination = new XmlHelper(destDocument);

        Optional<Element> libraryFolder = xmlHelperDestination.getTopLevelLibraryFolder();
        if (libraryFolder.isPresent()) {
            LOG.info("Adding content to Library");
            libraryFolder.get().addContent(srcRoot);
        } else {
            LOG.info("Library folder not present");
        }

    }

    private Optional<Element> getLibraryElement() {
        @SuppressWarnings("unchecked")
        List<Element> elements = libraryDocument.get().getRootElement().getChildren();
        for (Element element : elements) {
            if (isPrototype(element)) {
                return Optional.of(element);
            }
        }
        return Optional.absent();
    }

    public boolean isPrototype(Element element) {
        return element.getAttribute("name").getValue().equals(PROTOYPES);
    }
}
