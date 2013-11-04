
package org.csstudio.application.xmlrpc.server.epics;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

public class MetaDataCollection {

    private static final Logger LOG = LoggerFactory.getLogger(MetaDataCollection.class);

    private static MetaDataCollection instance = null;

	private Map<String, MetaData> content;

	private MetaDataCollection(String p) {
		content = new HashMap<String, MetaData>();
		readChannelFile(p);
	}

    public static synchronized void createInstance(String path) {
        if (instance == null) {
            instance = new MetaDataCollection(path);
        }
    }

	public static synchronized MetaDataCollection getInstance() throws NullPointerException {
	    if (instance == null) {
	        throw new NullPointerException("The instance variable is null. First call method createInstance()!");
	    }
	    return instance;
	}

    private void readChannelFile(String path) {
        content.clear();
        SAXBuilder sxbuild = new SAXBuilder();
        InputSource is = new InputSource(path);
        Document doc = null;
        try {
            doc = sxbuild.build(is);
            Element root = doc.getRootElement();
            List<Element> children = root.getChildren();
            for (Element o : children) {
                String name = o.getChild("name").getValue();
                String prec = o.getChild("prec").getValue();
                String egu = o.getChild("egu").getValue();
                MetaData md = new MetaData(name, prec, egu);
                if (md.isValid()) {
                    content.put(md.getName(), md);
                }
            }
        } catch (JDOMException e) {
            LOG.error("[*** JDOMException ***]: " + e.getMessage());
        } catch (IOException e) {
            LOG.error("[*** IOException ***]: " + e.getMessage());
        }
    }

	public synchronized void put(String k, MetaData e) {
		if (k == null) {
			return;
		}
		if (k.trim().isEmpty() || content.containsKey(k)) {
			return;
		}
		content.put(k, e);
	}

	public synchronized void putAll(Map<? extends String, ? extends MetaData> m) {
	    content.putAll(m);
	}

	public synchronized void clear() {
	    content.clear();
	}

	public MetaData get(String k) {
		return content.get(k);
	}
}
