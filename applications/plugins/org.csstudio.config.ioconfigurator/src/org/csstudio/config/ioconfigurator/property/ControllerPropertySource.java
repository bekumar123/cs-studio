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
 * $Id: ControllerPropertySource.java,v 1.2 2010/09/03 11:52:26 tslamic Exp $
 */
package org.csstudio.config.ioconfigurator.property;


import javax.naming.NamingException;

import org.csstudio.config.ioconfigurator.annotation.Nonnull;
import org.csstudio.config.ioconfigurator.annotation.Nullable;
import org.csstudio.config.ioconfigurator.ldap.LdapControllerService;
import org.csstudio.config.ioconfigurator.property.ioc.ControllerProperty;
import org.csstudio.config.ioconfigurator.tree.model.IControllerLeaf;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * This class represents a property source for {@code IControllerLeaf} nodes.
 * 
 * Its properties can be viewed in a Property View by the methods provided in
 * this class.
 * 
 * FIXME:(tslamic) This class needs to be double checked, since I have a hunch
 * some subtle errors exist. Also, when the MessageDialog is invoked, it always
 * appears twice.
 * 
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.2 $
 * @since 31.08.2010
 */
class ControllerPropertySource implements IPropertySource2 {

    private boolean validationAssigned = false;
    
    /*
     * The IPropertyDescriptor array is populated with the values from the
     * ControllerProperty.
     * 
     * Every instance of this class has the same DESCRIPTORS.
     */
    private static final IPropertyDescriptor[] DESCRIPTORS;
    static {
        ControllerProperty[] values = ControllerProperty.values();
        DESCRIPTORS = new IPropertyDescriptor[values.length];
        int count = 0;
        for (final ControllerProperty i : values) {
            DESCRIPTORS[count] = new TextPropertyDescriptor(i, i.getName());
            PropertyDescriptor propertyDescriptor = (PropertyDescriptor)DESCRIPTORS[count];
            propertyDescriptor.setValidator(new ICellEditorValidator() {                
                @Override
                public String isValid(Object value) {
                    return i.getValidator().isValid((String)value);
                }
            });
            ++count;
        }
    }

    /*
     * The following field instantiates the shell contained in the plug-in. It
     * is used for displaying messages in this class.
     */
    private static final Shell SHELL = PlatformUI.getWorkbench().getDisplay().getActiveShell();

    private final IControllerLeaf _node;

    /**
     * 
     * Private constructor. Instantiation through static method
     * {@link ControllerPropertySource#getInstance(IControllerLeaf)}.
     * 
     * @param node
     *            {@code IControllerLeaf} to be used as a property source.
     */
    private ControllerPropertySource(@Nonnull final IControllerLeaf node) {
        _node = node;
        loadAttributes(_node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getEditableValue() {
        /*
         * Since this property source is not appearing in the property sheet as
         * the value of a property of some other IPropertySource, it returns
         * null.
         */
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        if (!validationAssigned) {
            ControllerProperty[] values = ControllerProperty.values();
            int count = 0;
            for (final ControllerProperty i : values) {
                PropertyDescriptor propertyDescriptor = (PropertyDescriptor)DESCRIPTORS[count];
                propertyDescriptor.setValidator(new ICellEditorValidator() {                
                    @Override
                    public String isValid(Object value) {
                        
                        final String stringValue = ((String)value).trim();        
                        
                        final ControllerProperty property = (ControllerProperty) i;                        
                        final String oldLdapValue = _node.getValue(property);
                        
                        loadAttributes(_node);
                        String newLdapValue = _node.getValue(property);

                        if (!oldLdapValue.equals(newLdapValue)) {
                            return "The value has been modified by an outside source.";
                        }
                        
                        if (newLdapValue.equals(stringValue)) {
                            return null;
                        }
                        
                        return i.getValidator().isValid((String)value);
                    }
                });
                ++count;
            }
        }
        return DESCRIPTORS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getPropertyValue(final Object id) {
        if (id instanceof ControllerProperty) {
            return _node.getValue((ControllerProperty) id);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetPropertyValue(final Object id) {
        if (id instanceof ControllerProperty) {
            ControllerProperty property = (ControllerProperty) id;
            String value = property.getDefaultValue();
            replaceAttributeValueInLdap(_node, property, value);
            _node.setValue(property, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyValue(final Object id, final Object value) {
        if (id instanceof ControllerProperty) {

            final String stringValue = ((String)value).trim();            
            final ControllerProperty property = (ControllerProperty) id;
                             
            replaceAttributeValueInLdap(_node, property, stringValue);

            _node.setValue(property, stringValue);
            
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPropertyResettable(final Object id) {
        return id instanceof ControllerProperty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPropertySet(final Object id) {
        if (id instanceof ControllerProperty) {
            ControllerProperty property = (ControllerProperty) id;
            return !_node.getValue(property).equals(property.getDefaultValue());
        }
        return false;
    }

    /**
     * Returns a new {@code ControllerPropertySource} instance.
     * 
     * @param node
     *            {@code IControllerLeaf} to be used as a property source.
     * @return a new {@code ControllerPropertySource} instance.
     */
    public static ControllerPropertySource getInstance(final IControllerLeaf node) {
        return new ControllerPropertySource(node);
    }

    /*
     * Replaces the value in the LDAP.
     */
    private static final void replaceAttributeValueInLdap(@Nonnull final IControllerLeaf node,
            @Nonnull final ControllerProperty property, @Nullable final Object value) {
        try {
            LdapControllerService.setValue(node, property, (String) value);
        } catch (final NamingException e) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    MessageDialog.openError(SHELL, "Input error", e.getMessage());
                }
            });
        }
    }

    /*
     * Loads the Attribute from the LDAP server and populates the properties map
     * in the node.
     */
    private static final void loadAttributes(@Nonnull final IControllerLeaf node) {
        try {
            node.loadProperties(LdapControllerService.getAttributes(node));
        } catch (final NamingException e) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    MessageDialog.openError(SHELL, "Input error", e.getMessage());
                }
            });
            // TODO: what to do after the error?
        }
    }

    /*
     * Validates the user input.
     */
    private static final String validate(@Nonnull final ControllerProperty property, @Nonnull final Object value) {
        return property.getValidator().isValid((String) value);
    }
}
