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
 * $Id$
 */
package org.csstudio.alarm.service.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.InvalidNameException;
import javax.naming.directory.SearchControls;

import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.utility.ldap.service.ILdapContentModelBuilder;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.LdapServiceException;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.ContentModelExporter;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.ExportContentModelException;
import org.csstudio.utility.treemodel.builder.XmlFileContentModelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.csstudio.utility.ldap.service.util.LdapUtils.*;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.*;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.*;

/**
 * Alarm configuration service implementation
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 11.05.2010
 */
public class AlarmConfigurationServiceImpl implements IAlarmConfigurationService {

    private static final Logger LOG = LoggerFactory.getLogger(AlarmConfigurationServiceImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ContentModel<LdapEpicsAlarmcfgConfiguration> retrieveInitialContentModel(@Nonnull final List<String> facilityNames) throws CreateContentModelException, LdapServiceException {

        ContentModel<LdapEpicsAlarmcfgConfiguration> model;
        model = new ContentModel<LdapEpicsAlarmcfgConfiguration>(VIRTUAL_ROOT);

        final ILdapService ldapService = ServiceLocator.getService(ILdapService.class);
        if (ldapService == null) {
            throw new CreateContentModelException("LDAP service is unavailable.", null);
        }
        final ILdapContentModelBuilder<LdapEpicsAlarmcfgConfiguration> builder = 
            ldapService.getLdapContentModelBuilder(model);

        for (final String facility : facilityNames) {
            LOG.trace("retrieve from ldap for facility {}", facility);
            final ILdapSearchResult result =
                ldapService.retrieveSearchResultSynchronously(createLdapName(FACILITY.getNodeTypeName(), facility,
                                                                             UNIT.getNodeTypeName(), UNIT.getUnitTypeValue()),
                                                                             any(ATTR_FIELD_OBJECT_CLASS),
                                                                             SearchControls.SUBTREE_SCOPE);
            if (result != null) {
                builder.setSearchResult(result);
                LOG.trace("build model for facility {}", facility);
                builder.build();
                LOG.trace("build finished for facility {}", facility);
            }
        }
        final ContentModel<LdapEpicsAlarmcfgConfiguration> enrichedModel = builder.getModel();
        return enrichedModel != null ? enrichedModel : model;
    }

    /**
     * {@inheritDoc}
     * @throws CreateContentModelException occurs on file not found, io error, or parsing error.
     * @throws IOException 
     * @throws MalformedURLException 
     * @throws InvalidNameException
     */
    @Override
    @CheckForNull
    public ContentModel<LdapEpicsAlarmcfgConfiguration> retrieveInitialContentModelFromFile(@Nonnull final String filePath)
        throws CreateContentModelException {

    	try {
        final XmlFileContentModelBuilder<LdapEpicsAlarmcfgConfiguration> builder =
            new XmlFileContentModelBuilder<LdapEpicsAlarmcfgConfiguration>(VIRTUAL_ROOT, new URL(filePath).openStream());
        builder.build();
        return builder.getModel();
    	} catch (IOException e) {
    		throw new RuntimeException(e);
    	}
    }





    /**
     * {@inheritDoc}
     */
    @Override
    public void exportContentModelToXmlFile(@Nonnull final String filePath,
                                            @Nonnull final ContentModel<LdapEpicsAlarmcfgConfiguration> model,
                                            @Nullable final String dtdFilePath) throws ExportContentModelException {
        ContentModelExporter.exportContentModelToXmlFile(filePath, model, dtdFilePath);
    }

}
