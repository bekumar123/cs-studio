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
 * $Id: IControllerNode.java,v 1.1 2010/09/02 15:47:51 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.tree.model;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration;

/**
 * This interface provides the methods common to {@code IControllerSubtreeNode}
 * and {@code IControllerLeaf} nodes.
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.1 $
 * @since 22.08.2010
 */
public interface IControllerNode {

    /**
     * Returns the name of this {@code IControllerNode}.
     * @return the name of this {@code IControllerNode}.
     */
    @Nonnull
    String getName();

    /**
     * Sets the name of this {@code IControllerNode}.
     * @param newName {@code String} name to be set.
     */
    void setName(@Nonnull final String newName);

    /**
     * Returns this {@code IControllerNode} {@code LdapName}.
     * @return this {@code IControllerNode} {@code LdapName}.
     */
    @Nonnull
    LdapName getLdapName() throws InvalidNameException;

    /**
     * Returns the parent of this {@code IControllerNode}.
     * @return parent of this {@code IControllerNode}.
     */
    @CheckForNull
    IControllerSubtreeNode getParent();

    /**
     * Sets the parent of this {@code IControllerNode}.
     * @param parent {@code IControllerNode} to be set as a parent to this {@code IControllerNode}.
     */
    void setParent(@Nonnull IControllerSubtreeNode parent);

    /**
     * Determines whether this {@code IControllerNode} has children.
     * @return {@code true} if this {@code IControllerNode} has children, {@code false} otherwise.
     */
    boolean hasChildren();

    /**
     * Returns the tree configuration type of this node in the directory. If this node does
     * not correspond to an entry in the directory, returns <code>null</code>.
     * @return the tree node configuration type of this node in the directory.
     */
    @CheckForNull
    LdapEpicsControlsConfiguration getConfiguration();

    /**
     * Determines if this {@code IControllerNode} is a leaf.
     * @return {@code true} if this {@code IControllerNode} is a leaf, {@code false} otherwise.
     */
    boolean isLeaf();
}
