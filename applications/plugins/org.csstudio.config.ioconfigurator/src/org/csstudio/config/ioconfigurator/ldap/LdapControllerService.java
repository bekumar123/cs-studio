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
 * $Id: LdapControllerService.java,v 1.2 2010/09/03 11:52:26 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.ldap;

import static org.csstudio.utility.ldap.service.util.LdapUtils.any;
import static org.csstudio.utility.ldap.service.util.LdapUtils.createLdapName;

import java.util.Collection;

import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

import org.csstudio.config.ioconfigurator.activator.Activator;
import org.csstudio.config.ioconfigurator.annotation.Nonnull;
import org.csstudio.config.ioconfigurator.annotation.Nullable;
import org.csstudio.config.ioconfigurator.property.ioc.ControllerProperty;
import org.csstudio.config.ioconfigurator.tree.model.IControllerLeaf;
import org.csstudio.config.ioconfigurator.tree.model.IControllerNode;
import org.csstudio.config.ioconfigurator.tree.model.IControllerSubtreeNode;
import org.csstudio.config.ioconfigurator.tree.model.impl.ControllerLeaf;
import org.csstudio.config.ioconfigurator.tree.model.impl.ControllerSubtreeNode;
import org.csstudio.utility.ldap.model.builder.LdapContentModelBuilder;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.LdapServiceException;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.INodeComponent;
import org.csstudio.utility.treemodel.ISubtreeNodeComponent;

/**
 * This utility class provides methods to ease the LDAP server modifications.
 * 
 * TODO: is Logger needed?
 * 
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.2 $
 * @since 16.08.2010
 */
public final class LdapControllerService {

    /*
     * LDAP service.
     * 
     * By default, this LDAP service is equal to the LDAP service from the
     * Activator, but can be changed by invoking the setLdapService method.
     */
    private static ILdapService LDAP_SERVICE = Activator.getDefault().getLdapService();

    /** Constructor. Do not instantiate. */
    private LdapControllerService() {
    }

    /**
     * Sets the {@code ILdapService} this class can operate on.
     * 
     * @param service
     *            {@code ILdapService} service to be set.
     */
    public static void setLdapService(@Nonnull final ILdapService service) {
        LDAP_SERVICE = service;
    }

    /**
     * Adds a new IOC to the LDAP server.
     * 
     * @param node
     *            {@code IControllerLeaf} representing the IOC.
     * @throws InvalidNameException
     *             if a syntax violation is detected.
     */
    public static void addController(@Nonnull final IControllerLeaf node) throws InvalidNameException {
        Attributes atts = new BasicAttributes();
        for (ControllerProperty i : ControllerProperty.values()) {
            atts.put(i.getName(), node.getValue(i));
        }
        LDAP_SERVICE.createComponent(node.getLdapName(), atts);
    }

    /**
     * Removes the specified node from the LDAP server.
     * 
     * @param node
     *            {@code IControllerLeaf} node to be removed.
     * @throws InvalidNameException
     *             if a syntax violation is detected.
     */
    public static void removeController(@Nonnull final IControllerLeaf node) throws InvalidNameException {
        LDAP_SERVICE.removeLeafComponent(node.getLdapName());
    }

    /**
     * Sets the value of the {@code node} property, described by the
     * {@code propertyName}. If the value is {@code null} or empty
     * {@code String}, then the default value is set.
     * 
     * @param node
     *            {@code IControllerLeafNode} holding the property.
     * @param propertyName
     *            {@code String} property to modify.
     * @param value
     *            {@code String} value to be set.
     * @return {@code true} if the new value has been set, {@code false}
     *         otherwise.
     */
    @Nonnull
    public static void setValue(@Nonnull final IControllerLeaf node, @Nonnull final ControllerProperty property,
            @Nullable final String value) throws NamingException {

        String val = (value == null || value == "") ? property.getDefaultValue() : value;

        ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(
                property.getName(), val));
        
        LDAP_SERVICE.modifyAttributes(node.getLdapName(), new ModificationItem[] { item });
    }

    /**
     * Returns the value of the {@code node} property in the specified node.
     * 
     * @param node
     *            {@code IControllerLeafNode} to get the property from.
     * @param property
     *            {@code ControllerProperty} to retrieve value from.
     * @return the value of the {@code node} property in the specified node.
     */
    @Nonnull
    public static String getValue(@Nonnull final IControllerLeaf node, @Nonnull final ControllerProperty property)
            throws NamingException {
        Attribute att = LDAP_SERVICE.getAttributes(node.getLdapName()).get(property.getName());
        if (att == null) {
            return "";
        }
        StringBuilder value = new StringBuilder();
        for (NamingEnumeration<?> i = att.getAll(); i.hasMore();) {
            value.append(i.next() + " ");
        }
        return value.toString();
    }

    /**
     * Returns the node attributes.
     * 
     * @param node
     *            {@code IControllerLeaf} to retrieve attributes from.
     * @return the node attributes.
     * @throws NamingException
     *             if a syntax violation is detected.
     */
    public static Attributes getAttributes(@Nonnull final IControllerLeaf node) throws NamingException {
        return LDAP_SERVICE.getAttributes(node.getLdapName());
    }

    public static void rename(@Nonnull final IControllerNode node, @Nonnull final String newName) {
        // TODO: implement this
    }

    /**
     * Loads the LDAP tree structure to the specified {@code root} node.
     * 
     * @param root
     *            {@code IControllerNode} root of the data model
     * @throws CreateContentModelException
     *             if an exception occurred while creating the LDAP tree
     */
    public static void loadContent(@Nonnull final IControllerSubtreeNode root) throws CreateContentModelException {
        // TODO: first make sure root is empty or cleared
        ContentModel<LdapEpicsControlsConfiguration> model = retrieveContentModel();
        for (final INodeComponent<LdapEpicsControlsConfiguration> node : model.getVirtualRoot().getDirectChildren()) {
            populate(root, node);
        }
    }

    /*
     * (non-Javadoc) This method helps the loadContent() to populate the model
     * which represents the LDAP tree.
     */
    private static void populate(@Nonnull final IControllerSubtreeNode root,
            @Nonnull final INodeComponent<LdapEpicsControlsConfiguration> modelNode) {
        String nodeName = modelNode.getName();

        LdapEpicsControlsConfiguration configuration = modelNode.getType();

        if (configuration == LdapEpicsControlsConfiguration.IOC) {
            // This creates a new IOC leaf
            new ControllerLeaf(nodeName, root, configuration);
        } else {
            // This creates a new ControllerNode along with its children
            ControllerSubtreeNode node = new ControllerSubtreeNode(nodeName, root, configuration);
            if (modelNode instanceof ISubtreeNodeComponent) {
                Collection<INodeComponent<LdapEpicsControlsConfiguration>> children = ((ISubtreeNodeComponent<LdapEpicsControlsConfiguration>) modelNode)
                        .getDirectChildren();
                for (INodeComponent<LdapEpicsControlsConfiguration> child : children) {
                    populate(node, child);
                }
            }
        }
    }

    /*
     * Returns the {@code ContentModel} from the LDAP.
     * 
     * @return the {@code ContentModel} from the LDAP
     * 
     * @throws CreateContentModelException if an error occurred while retrieving
     * results
     */
    @Nonnull
    private static ContentModel<LdapEpicsControlsConfiguration> retrieveContentModel()
            throws CreateContentModelException {
        ILdapSearchResult searchResult = LDAP_SERVICE.retrieveSearchResultSynchronously(
                createLdapName(LdapEpicsControlsConfiguration.VIRTUAL_ROOT.getNodeTypeName(),
                        LdapEpicsControlsConfiguration.VIRTUAL_ROOT.getUnitTypeValue() // LdapEpicsControlsConfiguration.getRootTypeValue()
                ), any(LdapEpicsControlsConfiguration.IOC.getNodeTypeName()), SearchControls.SUBTREE_SCOPE);
        ContentModel<LdapEpicsControlsConfiguration> model;

        LdapContentModelBuilder<LdapEpicsControlsConfiguration> builder;
        try {
            builder = new LdapContentModelBuilder<LdapEpicsControlsConfiguration>(
                    LdapEpicsControlsConfiguration.VIRTUAL_ROOT, searchResult, LDAP_SERVICE.getLdapNameParser());
            builder.build();
            model = builder.getModel();
            return model;
        } catch (LdapServiceException e) {
            throw new CreateContentModelException(e.getLocalizedMessage(), e);
        }
    }
}
