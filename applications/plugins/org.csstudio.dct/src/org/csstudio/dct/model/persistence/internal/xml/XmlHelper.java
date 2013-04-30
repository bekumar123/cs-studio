package org.csstudio.dct.model.persistence.internal.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.google.common.base.Optional;

public class XmlHelper {

    private final Document document;

    public XmlHelper(Document document) {
        this.document = document;
    }

    public Optional<Element> getTopLevelLibraryFolder() {
        @SuppressWarnings("unchecked")
        List<Element> root = new ArrayList<Element>(document.getRootElement().getChildren());
        for (Element elem : root) {
            if (elem.getAttributeValue("name").equals("Library")) {
                return Optional.of(elem);
            }
        }
        return Optional.absent();
    }

    public Optional<Element> getTopLevelInstancesFolder() {
        @SuppressWarnings("unchecked")
        List<Element> root = new ArrayList<Element>(document.getRootElement().getChildren());
        for (Element elem : root) {
            if (elem.getAttributeValue("name").equals("Instances")) {
                return Optional.of(elem);
            }
        }
        return Optional.absent();
    }

    public Optional<Element> getTopLevelPrototypesFolder() {
        @SuppressWarnings("unchecked")
        List<Element> root = new ArrayList<Element>(document.getRootElement().getChildren());
        for (Element elem : root) {
            if (elem.getAttributeValue("name").equals("Prototypes")) {
                return Optional.of(elem);
            }
        }
        return Optional.absent();
    }
    public List<Element> flattenChildren(Element current) {
        List<Element> result = new ArrayList<Element>();
        flattenChildren(current, result);
        return result;
    }

    private void flattenChildren(Element current, List<Element> result) {
        List<?> children = current.getChildren();
        Iterator<?> iterator = children.iterator();
        while (iterator.hasNext()) {
            Element child = (Element) iterator.next();
            result.add(child);
            flattenChildren(child, result);
        }
    }
    
    public List<Element> getInstancesWithParentId(String parentId) {
        Optional<Element> instancesFolder = getTopLevelInstancesFolder();
        List<Element> result = new ArrayList<Element>();
        if (instancesFolder.isPresent()) {
            List<Element> allChilds = flattenChildren(instancesFolder.get());            
            for (Element child : allChilds) {
                String currentParentId = child.getAttributeValue("parent");
                if (currentParentId != null) {
                    if (currentParentId.equals(parentId)) {
                        result.add(child);
                    }
                }
            }
        }
        return result;
    }

    public List<Element> getPrototypesWithParentId(String parentId) {
        Optional<Element> instancesFolder = getTopLevelPrototypesFolder();
        List<Element> result = new ArrayList<Element>();
        if (instancesFolder.isPresent()) {
            List<Element> allChilds = flattenChildren(instancesFolder.get());     
            for (Element child : allChilds) {
                String currentParentId = child.getAttributeValue("parent");
                if (currentParentId != null) {
                    if (currentParentId.equals(parentId)) {
                        result.add(child);
                    }
                }
            }
        }
        return result;
    }

}
