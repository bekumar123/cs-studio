/**
 * 
 */
package org.csstudio.dct.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IVisitor;
import org.csstudio.dct.model.internal.AbstractElement;
import org.csstudio.dct.model.internal.Record;
import org.junit.Test;

/**
 * @author Sven Wende
 * 
 */
public class CompareUtilTest {

    /**
     * Test method for
     * {@link org.csstudio.dct.util.CompareUtil#equals(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public final void testEquals() {
        assertTrue(CompareUtil.equals(null, null));
        assertTrue(CompareUtil.equals("", ""));
        assertTrue(CompareUtil.equals("a", "a"));
        assertFalse(CompareUtil.equals(null, ""));
        assertFalse(CompareUtil.equals("", null));
        assertFalse(CompareUtil.equals("a", null));
        assertFalse(CompareUtil.equals("a", "b"));
        assertFalse(CompareUtil.equals("b", "a"));
        assertFalse(CompareUtil.equals("", "a"));

    }

    /**
     * Test method for
     * {@link org.csstudio.dct.util.CompareUtil#equals(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public final void testIdsEquals() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        IElement element1 = new AbstractElement("e1", id1) {
            private static final long serialVersionUID = 1L;

            public void accept(IVisitor visitor) {
            }

            public boolean isInherited() {
                return false;
            }
        };

        IElement element2 = new AbstractElement("e2", id2) {
            private static final long serialVersionUID = 1L;

            public void accept(IVisitor visitor) {
            }

            public boolean isInherited() {
                return false;
            }
        };

        assertTrue(CompareUtil.idsEqual(null, null));
        assertTrue(CompareUtil.idsEqual(element1, element1));
        assertTrue(CompareUtil.idsEqual(element2, element2));
        assertFalse(CompareUtil.idsEqual(null, element1));
        assertFalse(CompareUtil.idsEqual(element1, null));
        assertFalse(CompareUtil.idsEqual(element1, element2));
    }

    @Test
    public final void testContainsOnly() {
        List<Object> list = new ArrayList<Object>();
        list.add("a");
        list.add("b");

        assertTrue(CompareUtil.containsOnly(String.class, list));
        assertTrue(CompareUtil.containsOnly(Object.class, list));
        assertFalse(CompareUtil.containsOnly(IRecord.class, list));

        list.add(new Record());

        assertFalse(CompareUtil.containsOnly(String.class, list));
        assertTrue(CompareUtil.containsOnly(Object.class, list));
        assertFalse(CompareUtil.containsOnly(IRecord.class, list));

        list.remove(0);
        list.remove(0);

        assertFalse(CompareUtil.containsOnly(String.class, list));
        assertTrue(CompareUtil.containsOnly(Object.class, list));
        assertTrue(CompareUtil.containsOnly(IRecord.class, list));
    }

    @Test
    public void testIsChilOfLibraryFolderHandlesEmptyList() {
        List<IElement> elements = new ArrayList<IElement>();
        assertFalse(CompareUtil.childOfInstancesFolder(elements));
    }

    @Test
    public void testIsLibraryFolderHandlesNullValues() {
        assertFalse(new CompareUtil.IsLibraryFolder().isFolder(null));
    }

    @Test
    public void testIsProtypesFolderHandlesNullValues() {
        assertFalse(new CompareUtil.IsPrototypesFolder().isFolder(null));
    }

    @Test
    public void testIsInstancesFolderHandlesNullValues() {
        assertFalse(new CompareUtil.IsInstancesFolder().isFolder(null));
    }

}
