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
 * $Id: ControllerSubtreeNode.java,v 1.1 2010/09/02 15:47:51 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.tree.model.impl;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfigurator.tree.model.IControllerLeaf;
import org.csstudio.config.ioconfigurator.tree.model.IControllerNode;
import org.csstudio.config.ioconfigurator.tree.model.IControllerSubtreeNode;
import org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Basic implementation of the {@code IControllerSubtreeNode} interface.
 *
 * TODO (tslamic) : There is a fundamental design error in this class.
 * If the setParent() method is invoked, it does not 'move' the child
 * and its children to the appropriate parent, it only changes the
 * reference of the parent. For completeness, this functionality has
 * to be implemented ASAP.
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.1 $
 * @since 22.08.2010
 */
public class ControllerSubtreeNode extends AbstractControllerNode implements
        IControllerSubtreeNode {

    /*
     * Providing space for this class Map instance in order
     * to avoid the rehash() method calls.
     *
     * TODO: Determine the optimal capacity.
     */
    private static final int INITIAL_MAP_CAPACITY = 30;

    private final Map<String, IControllerNode> _children;

    /**
     * Constructor.
     *
     * @param nodeName {@code String} name of this node.
     * @param parent {@code IControllerSubtreeNode} parent of this node.
     * @param configurationType {@code LdapEpicsControlsConfiguration} type of this node.
     */
    public ControllerSubtreeNode(@Nonnull final String nodeName,
                                 @Nullable final IControllerSubtreeNode parent,
                                 @Nonnull final LdapEpicsControlsConfiguration configurationType) {
        super(nodeName, parent, configurationType);
        _children = Maps.newHashMapWithExpectedSize(INITIAL_MAP_CAPACITY);

        // Add this node as a child to the parent, if not null
        if (parent != null) {
            parent.addChild(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IControllerNode getChild(@Nonnull final String name) throws NoSuchElementException {
        hasChild(name);
        return _children.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IControllerNode> getChildren() {
        return Lists.newArrayList(_children.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild(@Nonnull final IControllerNode child) {
        assert child != null;
        _children.put(child.getName(), child);
        child.setParent(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeChild(@Nonnull final IControllerNode child) throws NoSuchElementException {
        hasChild(child.getName());
        if (child.isLeaf()) {
            _children.remove(child.getName());
        } else {
            ControllerSubtreeNode node = (ControllerSubtreeNode) child;
            if (node.hasChildren()) {
                node.removeChildren();
            }
            _children.remove(node.getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeChild(final String childName) throws NoSuchElementException {
        hasChild(childName);
        removeChild(_children.get(childName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeChildren() {
        /*
         *  TODO: maybe this method can be improved by the fact that
         *  there are either IControllerSubtreeNodes either IControllerLeafs
         *  but not a mixture of both in the _children map.
         */
        for (IControllerNode node : _children.values()) {
            if (!node.isLeaf()) {
                ((IControllerSubtreeNode) node).removeChildren();
            }
        }
        _children.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IControllerLeaf> getLeafs(@Nonnull final String name) {
        hasChild(name);
        IControllerNode node = _children.get(name);
        /*
         * TODO: Does the functionality described below makes sense,
         *       or would it be better to just throw an exception,
         *       if the 'name' would return an instance of a leaf?
         *
         * If the node is an instance of IControllerLeaf, this method
         * returns all other leafs from the directory, besides the node leaf by
         * referring to their parent (this class).
         *
         * Otherwise the node is an instance of IControllerSubtreeNode and
         * appropriate method is invoked.
         */
        if (node instanceof IControllerLeaf) {
            return getLeafs();
        }
        return ((IControllerSubtreeNode) node).getLeafs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IControllerLeaf> getLeafs() {
        List<IControllerLeaf> list = Lists.newArrayList();
        for (IControllerNode i : _children.values()) {
            if (i.isLeaf()) {
                list.add((IControllerLeaf) i);
            } else {
                list.addAll( ((IControllerSubtreeNode) i).getLeafs());
            }
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildren() {
        return !_children.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ControllerSubtreeNode) {
            ControllerSubtreeNode node = (ControllerSubtreeNode) o;
            return super.equals(node)
                    && getChildren().equals(node.getChildren());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 419;
        result = 31 * result + super.hashCode();
        result = 31 * result + _children.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     * This helper method checks whether the _children map holds
     * the child with a given name.
     * @throws NoSuchElementException if no such name exists.
     */
    private void hasChild(@Nonnull final String name) {
        if (!_children.containsKey(name)) {
            throw new NoSuchElementException("No such child: " + name);
        }
    }
}