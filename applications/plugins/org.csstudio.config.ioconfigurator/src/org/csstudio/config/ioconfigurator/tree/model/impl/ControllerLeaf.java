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
 * $Id: ControllerLeaf.java,v 1.1 2010/09/02 15:47:51 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.tree.model.impl;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.csstudio.config.ioconfigurator.property.ioc.ControllerProperty;
import org.csstudio.config.ioconfigurator.tree.model.IControllerLeaf;
import org.csstudio.config.ioconfigurator.tree.model.IControllerSubtreeNode;
import org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration;

import com.google.common.collect.Maps;

/**
 * Basic implementation of the {@code IControllerLeaf} interface.
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.1 $
 * @since 22.08.2010
 */
public class ControllerLeaf extends AbstractControllerNode implements
        IControllerLeaf {

    /*
     * Instantiated as an EnumMap
     */
    private final Map<ControllerProperty, String> _properties;

    /**
     * Constructor.
     * @param nodeName {@code String} name of this node.
     * @param parent {@code IControllerSubtreeNode} parent of this node.
     * @param configurationType {@code LdapEpicsControlsConfiguration} type of this node.
     */
    public ControllerLeaf(@Nonnull final String nodeName,
                          @Nullable final IControllerSubtreeNode parent,
                          @Nonnull final LdapEpicsControlsConfiguration configurationType) {
        super(nodeName, parent, configurationType);
        _properties = Maps.newEnumMap(ControllerProperty.class);

        /*
         * Add this node as a child to the parent, if not null.
         * However, since this is a leaf representing an IOC, this
         * should not be the case.
         */
        assert parent != null;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue(final String propertyName) throws NoSuchElementException {
        hasProperty(propertyName);
        return _properties.get(ControllerProperty.getProperty(propertyName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue(final ControllerProperty property) {
        return _properties.get(property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(final String propertyName, final String value) throws NoSuchElementException {
        /*
         * No validation is performed here as this method should be only used
         * from the property source class, where the validation is performed
         * before trying to set the value.
         */
        hasProperty(propertyName);
        _properties.put(ControllerProperty.getProperty(propertyName), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(final ControllerProperty property, final String value) throws IllegalArgumentException {
        /*
         * No validation is performed here as this method should be only used
         * from the property source class, where the validation is performed
         * before trying to set the value.
         */
        _properties.put(property, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<ControllerProperty, String> getProperties() {
        return Collections.unmodifiableMap(_properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildren() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadProperties(@Nonnull final Attributes ldapAttributes) throws NamingException {
        // If the properties are already loaded, remove them.
        if (!_properties.isEmpty()) {
            _properties.clear();
        }
        for (ControllerProperty i : ControllerProperty.values()) {
            Attribute att = ldapAttributes.get(i.getName());
            if (att == null) {
                _properties.put(i, i.getDefaultValue());
            } else {
                StringBuilder value = new StringBuilder();
                for (NamingEnumeration<?> j = att.getAll(); j.hasMore();) {
                    value.append(j.next() + " ");
                }
                _properties.put(i, value.toString());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ControllerLeaf) {
            ControllerLeaf node = (ControllerLeaf) o;
            return super.equals(node)
                    && getProperties().equals(node.getProperties());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1087;
        result = 31 * result + super.hashCode();
        result = 31 * result + _properties.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     * This helper method checks whether the _properties map holds
     * the child with a given name.
     * @throws NoSuchElementException if no such name exists.
     */
    private void hasProperty(@Nonnull final String name) {
        if (!_properties.containsKey(ControllerProperty.getProperty(name))) {
            throw new NoSuchElementException("No such property: " + name);
        }
    }
}
