/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.common.engine.httpserver;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.fileconfigure.FileArchiveConfigure;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.domain.desy.epics.name.EpicsChannelName;

/**
 *
 * @author wenhua xu
 * @since 30.09.2013
 */
public class ImportFileResponse extends AbstractResponse {

    private static final String URL_IMPORT_ACTION = "/importfile";

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public ImportFileResponse(@Nonnull final EngineModel model,
                                 @Nonnull final String adminParamKey,
                                 @Nonnull final String admingParamValue) {
        super(model, adminParamKey, admingParamValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
            final FileArchiveConfigure config = new FileArchiveConfigure(getModel());

             final String s ="";
            //Here request is the reference of HttpServletRequest.
            final List<EpicsChannelName> channelList =config.configureChannelsFromFile(req.getInputStream());
           // channelList.addAll(config.configureChannelsFromFile(req.getInputStream()));
            ImportResultResponse.setResult(channelList,"");
            resp.sendRedirect(new Url(ImportResultResponse.baseUrl()).url());//ShowChannelResponse.urlTo(name.toString()));
    }

    /** {@inheritDoc} */
    @Override
    protected void doPost(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp) throws ServletException,
                                                                                                         IOException {
        try {
               fillResponse(req, resp);
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    @Nonnull
    public static String baseUrl() {
        return URL_IMPORT_ACTION;
    }
}
