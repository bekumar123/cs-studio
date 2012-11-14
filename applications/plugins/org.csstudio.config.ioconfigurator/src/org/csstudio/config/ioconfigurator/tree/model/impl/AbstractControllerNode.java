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
 * $Id: AbstractControllerNode.java,v 1.1 2010/09/02 15:47:51 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.tree.model.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.config.ioconfigurator.tree.model.IControllerNode;
import org.csstudio.config.ioconfigurator.tree.model.IControllerSubtreeNode;
import org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration;

import com.google.common.collect.Lists;

/**
 * This abstract class servers as a parent class for the {@code ControllerSubtreeNode}
 * and {@code ControllerLeaf}.
 *
 * It provides the basic implementation of methods equal to both classes.
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.1 $
 * @since 22.08.2010
 */
abstract class AbstractControllerNode implements IControllerNode {

    private String _nodeName;
    private IControllerSubtreeNode _parent;
    /*
     * The following field is determined by LDAP server.
     * It cannot be modified, thus declared final.
     */
    private final LdapEpicsControlsConfiguration _configurationType;

    /**
     * Constructor.
     *
     * @param nodeName {@code String} name of this node.
     * @param parent {@code IControllerSubtreeNode} parent of this node.
     * @param configurationType {@code LdapEpicsControlsConfiguration} type of this node.
     * @throws IllegalArgumentException
     *         if the parent is {@code null} but the controller is not a root.
     */
    AbstractControllerNode(@Nonnull final String nodeName,
                           @Nullable final IControllerSubtreeNode parent,
                           @Nonnull final LdapEpicsControlsConfiguration configurationType) {
//        if (parent == null
//                && configurationType != LdapEpicsControlsConfiguration.ROOT) {
//            throw new IllegalArgumentException("Only root is allowed to have a null parent");
//        }

        _nodeName = nodeName;
        _parent = parent;
        _configurationType = configurationType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return _nodeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(@Nonnull final String newName) {
        _nodeName = newName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LdapName getLdapName() throws InvalidNameException {
        Rdn ldapRdn = new Rdn(_configurationType.getNodeTypeName(), _nodeName);
        LdapName name = new LdapName(Lists.newArrayList(ldapRdn));
        if (_parent != null) {
            name.addAll(0, _parent.getLdapName());
        }
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IControllerSubtreeNode getParent() {
        return _parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LdapEpicsControlsConfiguration getConfiguration() {
        return _configurationType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLeaf() {
        return _configurationType == LdapEpicsControlsConfiguration.IOC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParent(@Nonnull final IControllerSubtreeNode parent) {
        _parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof AbstractControllerNode) {
            AbstractControllerNode node = (AbstractControllerNode) o;
            return getName().equals(node.getName())
                    && getConfiguration() == node.getConfiguration();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 3571;
        result = 31 * result + _nodeName.hashCode();
        result = 31 * result + _configurationType.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return _nodeName;
    }
}
