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
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

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
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsFieldsAndAttributes;
import org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.INodeComponent;
import org.csstudio.utility.treemodel.ISubtreeNodeComponent;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

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
     * Removes the specified node from the LDAP server.
     * 
     * @param node
     *            {@code IControllerLeaf} node to be removed.
     * @throws InvalidNameException
     *             if a syntax violation is detected.
     */
    public static void removeNode(LdapName ldapName) throws Exception {
        Preconditions.checkNotNull(ldapName, "ldapName must not be null");

        LdapNode ldapNode = new LdapNode(ldapName);
        if (ldapNode.allowsRemovalOfChilds()) {
            LDAP_SERVICE.removeComponent(LdapEpicsControlsConfiguration.VIRTUAL_ROOT, ldapName);
        } else {
            LDAP_SERVICE.removeLeafComponent(ldapName);
        }
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
        throw new IllegalStateException("Rename operation on IControllerNode not implemented.");
    }

    /**
     * Rename a node:
     * 
     * Attention: We do not know if the LDAP server is configured to allow the
     * renaming of nodes with childs. Therefore we simulate the rename operation
     * as a combination of a move and delete operation.
     */
    public static void rename(@Nonnull final LdapName oldLdapName, @Nonnull final String newName) throws Exception {
        Preconditions.checkNotNull(oldLdapName, "oldLdapName must not be null");
        Preconditions.checkNotNull(newName, "newName must not be null");

        LdapNode ldapNode = new LdapNode(oldLdapName);

        if (ldapNode.isFacility()) {
            // rename a Facility
            Optional<LdapName> newLdapName = LdapControllerService.createNewNode(new LdapName("ou=EpicsControls"),
                    newName);
            if (!newLdapName.isPresent()) {
                throw new IllegalStateException("Can't create new node: " + newName);
            }
            LDAP_SERVICE.moveSubTrees(LdapEpicsControlsConfiguration.VIRTUAL_ROOT, oldLdapName, newLdapName.get());
            removeNode(oldLdapName);
        } else if (ldapNode.isLeaf()) {
            // rename an IOC
            LdapName newLdapName = (LdapName) oldLdapName.clone();
            newLdapName.remove(newLdapName.size() - 1);

            // create the new name by replacing the old econ value (the name)
            // with the new one.

            Attributes attrs = LDAP_SERVICE.getAttributes(oldLdapName);

            Optional<LdapName> newLdapNode = LdapControllerService.duplicateNode(newLdapName, newName, attrs);

            if (!newLdapNode.isPresent()) {
                throw new IllegalStateException("Can't create new node: " + newName);
            }

            LDAP_SERVICE.moveSubTrees(LdapEpicsControlsConfiguration.VIRTUAL_ROOT, oldLdapName, newLdapNode.get());
            removeNode(oldLdapName);

        } else {
            throw new IllegalStateException(
                    "Unsupported target for rename operation. Only Facility and IOC are supported.");
        }
    }

    /**
     * Add a new facility.
     * 
     * Create a new Facility and add the needed EPICS-IOC node.
     */
    public static void addNewFacility(@Nonnull final LdapName parent, final String newName) throws Exception {
        Preconditions.checkNotNull(parent, "parent must not be null");
        Preconditions.checkNotNull(newName, "newName must not be null");

        Optional<LdapName> newLdapName = LdapControllerService.createNewNode(parent, newName);
        if (newLdapName.isPresent()) {
            LdapControllerService.createNewNode(newLdapName.get(),
                    LdapEpicsControlsFieldsAndAttributes.ECOM_EPICS_IOC_FIELD_VALUE);
        }
    }

    /**
     * Add a new IOC node and init the node with soem default values.
     */
    public static void addNewIOC(@Nonnull final LdapName parent, final String newName) throws Exception {
        Preconditions.checkNotNull(parent, "parent must not be null");
        Preconditions.checkNotNull(newName, "newName must not be null");

        LdapControllerService.createNewNode(parent, newName);
    }

    private static Optional<LdapName> createNewNode(@Nonnull final LdapName parent, final String newName)
            throws Exception {
        Preconditions.checkNotNull(parent, "parent must not be null");
        Preconditions.checkNotNull(newName, "newName must not be null");

        LdapNode ldapNode = new LdapNode(parent);

        // We found the needed information, therefore we can create a new node.
        if (ldapNode.getChildAttribute().isPresent()) {

            LdapName newLdapName = (LdapName) parent.clone();
            newLdapName.add(new Rdn(ldapNode.getChildAttribute().get() + "=" + newName));

            // add two objectClass entries
            BasicAttribute oc1 = new BasicAttribute(LdapFieldsAndAttributes.ATTR_FIELD_OBJECT_CLASS);
            oc1.add("top"); // entry 1
            oc1.add(ldapNode.getChildAttributeValue()); // entry 2

            // now add the new attribute containing the name
            Attributes attrs = new BasicAttributes(false);
            attrs.put(oc1);
            attrs.put(ldapNode.getChildAttribute().get(), newName);

            // The selected node should contain an IOC node. Therefore provide
            // some initialization values;
            if (ldapNode.isParentOfEcon()) {
                attrs.put(new BasicAttribute(ControllerProperty.SAVE_ENABLED.getName(), ControllerProperty.SAVE_ENABLED
                        .getInitValue()));
                attrs.put(new BasicAttribute(ControllerProperty.CS_REDUNDANT.getName(), ControllerProperty.CS_REDUNDANT
                        .getInitValue()));
                attrs.put(new BasicAttribute(ControllerProperty.RESPONSIBLE_PHONE.getName(),
                        ControllerProperty.RESPONSIBLE_PHONE.getInitValue()));
                attrs.put(new BasicAttribute(ControllerProperty.RESPONSIBLE_NAME.getName(),
                        ControllerProperty.RESPONSIBLE_NAME.getInitValue()));
                attrs.put(new BasicAttribute(ControllerProperty.SERVICE_NAME.getName(), ControllerProperty.SERVICE_NAME
                        .getInitValue()));
                attrs.put(new BasicAttribute(ControllerProperty.SERVICE_PHONE.getName(),
                        ControllerProperty.SERVICE_PHONE.getInitValue()));
            }

            if (!LDAP_SERVICE.createComponent(newLdapName, attrs)) {
                throw new IllegalStateException("Can't create new node.");
            }

            return Optional.of(newLdapName);

        }
        return Optional.absent();
    }

    private static Optional<LdapName> duplicateNode(@Nonnull final LdapName parent, final String newName,
            Attributes oldAttrs) throws Exception {

        Preconditions.checkNotNull(parent, "parent must not be null");
        Preconditions.checkNotNull(newName, "newName must not be null");
        Preconditions.checkNotNull(oldAttrs, "oldAttrs must not be null");

        NamingEnumeration<? extends Attribute> all = oldAttrs.getAll();

        LdapNode ldapNode = new LdapNode(parent);

        // We found the needed information, therefore we can create a new node.
        if (ldapNode.getChildAttribute().isPresent()) {

            // z.B. ecom=EPICS-IOC,efan=Test,ou=EpicsControls
            LdapName newLdapName = (LdapName) parent.clone();
            
            // ldapNode.getChildAttribute().get() returns econ if we rename ion IOC
            // newLdapName is now econ=NewName,ecom=EPICS-IOC,efan=Test,ou=EpicsControls
            newLdapName.add(new Rdn(ldapNode.getChildAttribute().get() + "=" + newName));
           
            // add two objectClass entries
            BasicAttribute oc1 = new BasicAttribute(LdapFieldsAndAttributes.ATTR_FIELD_OBJECT_CLASS);
            oc1.add("top"); // entry 1
            oc1.add(ldapNode.getChildAttributeValue()); // entry 2 => this is epicsController if we rename an IOC

            // now add the new attribute containing the name
            Attributes attrs = new BasicAttributes(false);
            attrs.put(oc1);
            attrs.put(ldapNode.getChildAttribute().get(), newName);

            // now copy the attributes
            if (ldapNode.isParentOfEcon()) {
                while (all.hasMore()) {
                    Attribute att = all.next();
                    ControllerProperty cp = ControllerProperty.getProperty(att.getID());
                    if (cp != null) {
                        attrs.put(att.getID(), (String) att.get());
                    }
                }
            }

            if (!LDAP_SERVICE.createComponent(newLdapName, attrs)) {
                throw new IllegalStateException("Can't create new node.");
            }

            return Optional.of(newLdapName);

        }
        return Optional.absent();
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

        //@formatter:off
        // Construct the ldap query but make sure that we see nodes that have no children yet.
        ILdapSearchResult searchResult = LDAP_SERVICE.retrieveTreeContentSynchronously(
                createLdapName(
                        LdapEpicsControlsConfiguration.VIRTUAL_ROOT.getNodeTypeName(),
                        LdapEpicsControlsConfiguration.VIRTUAL_ROOT.getUnitTypeValue() 
                ), 
                any(
                      LdapEpicsControlsConfiguration.FACILITY.getNodeTypeName()
                )
        ); 
        //@formatter:on

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
