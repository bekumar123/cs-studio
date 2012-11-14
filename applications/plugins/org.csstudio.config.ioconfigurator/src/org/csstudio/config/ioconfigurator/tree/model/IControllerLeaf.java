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
 * $Id: IControllerLeaf.java,v 1.1 2010/09/02 15:47:51 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.tree.model;

import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.csstudio.config.ioconfigurator.property.ioc.ControllerProperty;

/**
 * Emulates a tree-like node representing a leaf.
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.1 $
 * @since 22.08.2010
 */
public interface IControllerLeaf extends IControllerNode {

    /**
     * Returns the value of the specified property.
     * @param propertyName {@code String} name of the property.
     * @return the value of the specified property.
     * @throws NoSuchElementException if the child with the specified name cannot be found.
     */
    @Nonnull
    String getValue(@Nonnull String propertyName) throws NoSuchElementException;

    /**
     * Returns the value of the specified property.
     * @param property {@code ControllerProperty} property.
     * @return the value of the specified property.
     */
    @Nonnull
    String getValue(@Nonnull ControllerProperty property);

    /**
     * Sets the value of the specified property.
     * @param propertyName {@code String} name of the property.
     * @param value {@code String} new value of the property.
     * @throws NoSuchElementException if the child with the specified name cannot be found.
     */
    void setValue(@Nonnull String propertyName, @Nonnull String value) throws NoSuchElementException;

    /**
     * Sets the value of the specified property.
     * @param property {@code ControllerProperty} property.
     * @param value {@code String} new value of the property.
     * @throws IllegalArgumentException if the {@code value} does not comply with the property input validator.
     */
    void setValue(@Nonnull ControllerProperty property, @Nonnull String value) throws IllegalArgumentException;

    /**
     * Returns an unmodifiable Map holding the properties and its values.
     * @return an unmodifiable Map holding the properties and its values.
     */
    Map<ControllerProperty, String> getProperties();

    /**
     * Loads the properties for this {@code IControllerLeaf}.
     * @param ldapAttributes {@code Attributes} from the LDAP server for this leaf.
     */
    void loadProperties(Attributes ldapAttributes) throws NamingException;
}
