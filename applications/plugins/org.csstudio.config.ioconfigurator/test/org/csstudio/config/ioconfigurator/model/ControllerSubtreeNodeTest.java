/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id: ControllerSubtreeNodeTest.java,v 1.1 2010/09/02 15:47:52 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.NoSuchElementException;

import org.csstudio.config.ioconfigurator.tree.model.IControllerLeaf;
import org.csstudio.config.ioconfigurator.tree.model.IControllerSubtreeNode;
import org.csstudio.config.ioconfigurator.tree.model.impl.ControllerLeaf;
import org.csstudio.config.ioconfigurator.tree.model.impl.ControllerSubtreeNode;
import org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * TODO (tslamic) :
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.1 $
 * @since 27.08.2010
 */
public class ControllerSubtreeNodeTest {

    /*
     * Testing subjects. Loaded in setUp method.
     */
    private IControllerSubtreeNode nodeA;
    private IControllerSubtreeNode nodeB;
    private IControllerSubtreeNode nodeC;

    /*
     * Testing leafs.
     * Instantiated when the method setLeafs is invoked.
     * The leaf nodes are named "leafA", "leafB", "leafC", respectively.
     */
    private static final int NUMBER_OF_LEAFS = 3;
    private IControllerLeaf leafA;
    private IControllerLeaf leafB;
    private IControllerLeaf leafC;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        nodeA = new ControllerSubtreeNode("nodeA",
                                          null,
                                          LdapEpicsControlsConfiguration.ROOT);
        nodeB = new ControllerSubtreeNode("nodeB",
                                          nodeA,
                                          LdapEpicsControlsConfiguration.FACILITY);
        nodeC = new ControllerSubtreeNode("nodeC",
                                          nodeA,
                                          LdapEpicsControlsConfiguration.COMPONENT);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        nodeA = null;
        nodeB = null;
        nodeC = null;

        leafA = null;
        leafB = null;
        leafC = null;
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfigurator.tree.model.impl.ControllerSubtreeNode#hashCode()}.
     */
    @Test
    public final void testHashCode() {
        assertEquals(nodeA.hashCode(), nodeA.hashCode());

        assertEquals(nodeB.hashCode(), nodeB.hashCode());
        assertEquals(nodeB.hashCode(), new ControllerSubtreeNode("nodeB", nodeA, LdapEpicsControlsConfiguration.FACILITY));
        assertEquals(nodeC.hashCode(), nodeC.hashCode());
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfigurator.tree.model.impl.ControllerSubtreeNode#hasChildren()}.
     */
    @Test
    public final void testHasChildren() {
        assertTrue(nodeA.hasChildren());
        assertFalse(nodeB.hasChildren());
        assertFalse(nodeC.hasChildren());
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfigurator.tree.model.impl.ControllerSubtreeNode#equals(java.lang.Object)}.
     */
    @Test
    public final void testEqualsObject() {
//        assertEquals(nodeA, nodeA);
//        assertEquals(nodeA, getEqualInstance(nodeA));
//        assertEquals(getEqualInstance(nodeA), nodeA);
//
//        assertEquals(nodeB, nodeB);
//        assertEquals(nodeB, getEqualInstance(nodeB));
//        assertEquals(getEqualInstance(nodeB), nodeB);
//
//        assertEquals(nodeC, nodeC);
//        assertEquals(nodeC, getEqualInstance(nodeC));
//        assertEquals(getEqualInstance(nodeC), nodeC);
//
//        assertFalse(nodeA.equals(nodeB));
//        assertFalse(nodeB.equals(nodeC));
//        assertFalse(nodeC.equals(nodeA));
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfigurator.tree.model.impl.ControllerSubtreeNode#removeChild(org.csstudio.config.ioconfigurator.tree.model.IControllerNode)}.
     */
    @Test
    public final void testRemoveChild() {
        setLeafs(nodeA);

        assertTrue(nodeA.hasChildren());
        assertEquals(NUMBER_OF_LEAFS, nodeA.getChildren().size());
        assertEquals(leafA, nodeA.getChild("leafA"));
        assertEquals(leafB, nodeA.getChild("leafB"));
        assertEquals(leafC, nodeA.getChild("leafC"));

        nodeA.removeChild(leafA);
        NoSuchElementException e = null;
        try {
            nodeA.getChild("leafA");
        } catch (NoSuchElementException exc) {
            e = exc;
        }
        assertNotNull(e);
        assertTrue(nodeA.hasChildren());
        assertEquals(2, nodeA.getChildren().size());

        nodeA.removeChild(leafB);
        e = null;
        try {
            nodeA.getChild("leafB");
        } catch (NoSuchElementException exc) {
            e = exc;
        }
        assertNotNull(e);
        assertTrue(nodeA.hasChildren());
        assertEquals(1, nodeA.getChildren().size());

        nodeA.removeChild(leafC);
        e = null;
        try {
            nodeA.getChild("leafC");
        } catch (NoSuchElementException exc) {
            e = exc;
        }
        assertNotNull(e);
        assertFalse(nodeA.hasChildren());
        assertEquals(0, nodeA.getChildren().size());
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfigurator.tree.model.impl.ControllerSubtreeNode#removeChildren()}.
     */
    @Test
    public final void testRemoveChildren() {
        nodeB.addChild(nodeC);
        setLeafs(nodeC);

        assertTrue(nodeB.hasChildren());
        assertEquals(1, nodeB.getChildren().size());

        assertTrue(nodeC.hasChildren());
        assertEquals(leafA, nodeC.getChild("leafA"));
        assertEquals(leafB, nodeC.getChild("leafB"));
        assertEquals(leafC, nodeC.getChild("leafC"));

        nodeB.removeChildren();
        assertFalse(nodeB.hasChildren());
        assertEquals(0, nodeB.getChildren().size());

        assertFalse(nodeC.hasChildren());
        assertEquals(0, nodeC.getChildren().size());
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfigurator.tree.model.impl.ControllerSubtreeNode#addChild(org.csstudio.config.ioconfigurator.tree.model.IControllerNode)}.
     */
    @Test
    public final void testAddChild() {
        IControllerLeaf test = new ControllerLeaf("test",
                                                  null,
                                                  LdapEpicsControlsConfiguration.IOC);
        assertNotNull(test);

        assertFalse(nodeB.hasChildren());
        nodeB.addChild(test);
        assertTrue(nodeB.hasChildren());
        assertEquals(test, nodeB.getChild("test"));
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfigurator.tree.model.impl.ControllerSubtreeNode#getLeafs(java.lang.String)}.
     */
    @Test
    public final void testGetLeafsString() {
        nodeA.addChild(nodeB);
        nodeB.addChild(nodeC);
        setLeafs(nodeC);
        List<IControllerLeaf> list = nodeA.getLeafs("nodeB");
        assertEquals(NUMBER_OF_LEAFS, list.size());
        // the getLeafs() does not sort elements
        // TODO: try to fix this
        List<IControllerLeaf> compareList = Lists.newArrayList(leafB,
                                                               leafC,
                                                               leafA);
        assertEquals(list, compareList);
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfigurator.tree.model.impl.ControllerSubtreeNode#getLeafs()}.
     */
    @Test
    public final void testGetLeafs() {
        nodeA.addChild(nodeB);
        setLeafs(nodeB);
        List<IControllerLeaf> list = nodeA.getLeafs();
        assertEquals(NUMBER_OF_LEAFS, list.size());
        // the getLeafs() does not sort elements
        // TODO: try to fix this
        List<IControllerLeaf> compareList = Lists.newArrayList(leafB,
                                                               leafC,
                                                               leafA);
        assertEquals(list, compareList);
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfigurator.tree.model.impl.ControllerSubtreeNode#getChild(java.lang.String)}.
     */
    @Test
    public final void testGetChild() {
        nodeB.addChild(nodeA);
        setLeafs(nodeA);
        assertEquals(leafA, nodeA.getChild("leafA"));
        assertEquals(leafB, nodeA.getChild("leafB"));
        assertEquals(leafC, nodeA.getChild("leafC"));

        IControllerSubtreeNode nde = (IControllerSubtreeNode) nodeB
                .getChild("nodeA");
        assertEquals(leafA, nde.getChild("leafA"));
        assertEquals(leafB, nde.getChild("leafB"));
        assertEquals(leafC, nde.getChild("leafC"));
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfigurator.tree.model.impl.ControllerSubtreeNode#getChildren()}.
     */
    @Test
    public final void testGetChildren() {
        setLeafs(nodeA);
        List<IControllerLeaf> list = Lists.newArrayList(leafB, leafC, leafA);
        assertEquals(nodeA.getChildren(), list);
    }

    // HELPER NON-TEST METHODS BELOW

    private final IControllerSubtreeNode getRoot(final String name,
                                                 final IControllerSubtreeNode parent) {
        IControllerSubtreeNode node = new ControllerSubtreeNode(name,
                                                                parent,
                                                                LdapEpicsControlsConfiguration.ROOT);
        return node;
    }

    private final void setLeafs(final IControllerSubtreeNode node) {
        leafA = new ControllerLeaf("leafA",
                                   node,
                                   LdapEpicsControlsConfiguration.IOC);
        leafB = new ControllerLeaf("leafB",
                                   node,
                                   LdapEpicsControlsConfiguration.IOC);
        leafC = new ControllerLeaf("leafC",
                                   node,
                                   LdapEpicsControlsConfiguration.IOC);
    }
}
