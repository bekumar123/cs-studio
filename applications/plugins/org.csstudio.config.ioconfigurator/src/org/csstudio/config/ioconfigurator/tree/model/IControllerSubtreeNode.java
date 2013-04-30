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
 * $Id: IControllerSubtreeNode.java,v 1.1 2010/09/02 15:47:51 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.tree.model;

import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

/**
 * Emulates a tree-like node possible to hold
 * {@code IControllerNode} instances as children.
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.1 $
 * @since 22.08.2010
 */
public interface IControllerSubtreeNode extends IControllerNode {

    /**
     * Returns the child with the specified {@code name}.
     * @param name {@code String} name of the child.
     * @return {@code IController} with the specified {@code name}.
     * @throws NoSuchElementException if the child with the specified name cannot be found.
     */
    @Nonnull
    IControllerNode getChild(@Nonnull final String name) throws NoSuchElementException;

    /**
     * Returns the {@code List} of all the children of this {@code IControllerNode}.
     * @return {@code List} of all the children of this {@code IControllerSubtreeNode}.
     */
    @Nonnull
    List<IControllerNode> getChildren();

    /**
     * Adds a {@code child} to this {@code IControllerNode}.
     * @param child {@code IControllerNode} to be added.
     */
    void addChild(@Nonnull final IControllerNode child);

    /**
     * Removes the {@code child} from this {@code IControllerNode}.
     * @param child {@code IControllerNode} to be removed.
     * @throws NoSuchElementException if the child with the specified name cannot be found.
     */
    void removeChild(@Nonnull final IControllerNode child) throws NoSuchElementException;

    /**
     * Removes the {@code child} from this {@code IControllerNode}.
     * @param childName {@code IController} with the specified name to be removed.
     * @throws NoSuchElementException if the child with the specified name cannot be found.
     */
    void removeChild(@Nonnull final String childName) throws NoSuchElementException;

    /**
     * Removes all the children of this {@code IControllerNode}.
     * If children of this class have children of their own, they are
     * also removed.
     */
    void removeChildren();

    /**
     * Returns the {@code List} of {@code IControllerLeaf}s below the
     * specified child of this node.
     * @param name {@code String} name of the child.
     * @return the {@code List} of {@code IControllerLeaf}s below the
     *         specified child of this node.
     * @throws NoSuchElementException if the child with the specified name cannot be found.
     */
    @Nonnull
    List<IControllerLeaf> getLeafs(@Nonnull String childName) throws NoSuchElementException;

    /**
     * Returns the {@code List} of leafs below this {@code IControllerNode}.
     * @return the {@code List} of leafs below this {@code IControllerNode}
     */
    @Nonnull
    List<IControllerLeaf> getLeafs();
}
