
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
 */

package org.csstudio.application.xmlrpc.server.command;

import java.util.Collection;
import java.util.Vector;
import java.util.regex.Pattern;
import org.csstudio.application.xmlrpc.server.ServerCommandException;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @since 21.12.2012
 */
public class NamesCommand extends AbstractServerCommand {
    
    private static final Logger LOG = LoggerFactory.getLogger(NamesCommand.class);
    
    private IArchiveReaderFacade archiveReader;
    
    /**
     * @param name
     */
    public NamesCommand(String name, IArchiveReaderFacade reader) {
        super(name);
        archiveReader = reader;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public StringCollectionResult executeCommand(ServerCommandParams params) throws ServerCommandException {
        Collection<String> channels = null;
        String pattern = null;
        if (params.containsParameter("pattern")) {
            pattern = (String) params.getParameter("pattern");
        } else {
            // Get all channel names
            pattern = ".";
        }
        try {
            channels = archiveReader.getChannelsByNamePattern(Pattern.compile(pattern));
        } catch (ArchiveServiceException e) {
            channels = new Vector<String>();
            LOG.error("[*** ArchiveServiceException ***]: {}", e.getMessage());
        }
        return new StringCollectionResult(channels);
    }
}
