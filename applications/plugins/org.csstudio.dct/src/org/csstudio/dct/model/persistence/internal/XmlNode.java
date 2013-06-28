package org.csstudio.dct.model.persistence.internal;

import org.jdom.Element;

public class XmlNode {

    private static final String LIBRARY = "Library";

    private static final String RECORD = "record";

    private static final String INSTANCE = "instance";

    private static int MAX_RECURSION_DEPTH = 7;

    private final Element element;

    public XmlNode(final Element element) {
        this.element = element;
    }

    public boolean isFromLibrary() {

        if (element.getName().equals(RECORD)) {
            Element parent = element.getParentElement();
            if (parent.getName().equals(INSTANCE)) {
                if (LIBRARY.equals(parent.getAttributeValue(XmlAttributes.PROTOTYPE_FOLDER))) {
                    return true;
                }
            }
        }

        if (element.getName().equals(INSTANCE)) {
            if (LIBRARY.equals(element.getAttributeValue(XmlAttributes.PROTOTYPE_FOLDER))) {
                return true;
            }
            return hasParentFromLibrary(element);

        }
        return false;

    }

    public boolean hasParentFromLibrary(Element element) {
        Element parent = element.getParentElement();
        int safetyCounter = 0;
        while ((parent != null) && (safetyCounter < MAX_RECURSION_DEPTH)) {
            if (LIBRARY.equals(parent.getAttributeValue(XmlAttributes.PROTOTYPE_FOLDER))) {
                return true;
            }
            parent = parent.getParentElement();
            safetyCounter++;
        }
        return false;
    }

}
