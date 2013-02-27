package org.csstudio.dct.model.persistence.internal.xml;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.google.common.base.Optional;

public class XmlDomUpdater {

    private final Document destDocument;
    private final Optional<Document> libraryDocument;

    public XmlDomUpdater(final Document destDocument, final Optional<Document> libraryDocument) {
        this.destDocument = destDocument;
        this.libraryDocument = libraryDocument;
    }

    public void addPrototypesFromLibrary() {
        if (!libraryDocument.isPresent()) {
            return;
        }

        @SuppressWarnings("unchecked")
        List<Element> srcRoot = new ArrayList<Element>(libraryDocument.get().getRootElement().getChild("folder")
                .getChildren());
        for (Element libraryElement : srcRoot) {
            libraryElement.detach();
        }

        XmlHelper xmlHelperDestination = new XmlHelper(destDocument);

        Optional<Element> libraryFolder = xmlHelperDestination.getTopLevelLibraryFolder();
        if (libraryFolder.isPresent()) {
            libraryFolder.get().addContent(srcRoot);
        }

    }

}
