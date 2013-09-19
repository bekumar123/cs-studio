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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.model.ArchiveChannelBuffer;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.EngineModelException;
import org.csstudio.domain.desy.epics.name.EpicsChannelName;

import com.google.common.base.Strings;

/**
 * Simple http request/response for removing a channel from the archiver configuration.
 *
 * @author bknerr
 * @since 22.09.2011
 */
public class AddChannelResponse extends AbstractChannelResponse {

    static final String PARAM_CHANNEL_GROUP = "group";
    static final String PARAM_DATATYPE = "datatype";
    //    private static final String PARAM_CONTROLSYSTEM = "controlsystem";
    //    private static final String PARAM_DESCRIPTION = "desc";
    static final String PARAM_LOPR = "lopr";
    static final String PARAM_HOPR = "hopr";

    private static String URL_ADD_CHANNEL_ACTION;
    private static String URL_ADD_CHANNEL_PAGE;
    static {
        URL_ADD_CHANNEL_ACTION = "add";
        URL_ADD_CHANNEL_PAGE = URL_CHANNEL_PAGE + "/" + URL_ADD_CHANNEL_ACTION;
    }

    private static final long serialVersionUID = 1L;
    private  String error_msg="";

    /**
     * Constructor.
     */
    AddChannelResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp) throws Exception {
        final String names = req.getParameter(PARAM_NAME);
        if (names == null) {
            return;
        }
        error_msg="<br>";
        final String group = req.getParameter(PARAM_CHANNEL_GROUP);
        if (Strings.isNullOrEmpty(group)) {
            redirectToErrorPage(resp, "The required parameter '" + PARAM_CHANNEL_GROUP + "' is either null or empty!");
            return;
        }
        final List<EpicsChannelName> channelList = createEpicsNames(resp, group, names);

        if (channelList.size() > 1) {
            ImportResultResponse.setResult(channelList,error_msg);
            //redirectToErrorPage(resp,error_msg);

            resp.sendRedirect(new Url(ImportResultResponse.baseUrl()).url());//ShowChannelResponse.urlTo(name.toString()));
        } else {
            resp.sendRedirect(ShowChannelResponse.urlTo(channelList.get(0).toString()));
        }
        /*
        final EpicsChannelName name = parseEpicsNameOrConfigureRedirectResponse(req, resp);
         if (Strings.isNullOrEmpty(group)) {
            redirectToErrorPage(resp, "The required parameter '" + PARAM_CHANNEL_GROUP + "' is either null or empty!");
            return;
        }
        final String type = req.getParameter(PARAM_DATATYPE);
        final String lopr = req.getParameter(PARAM_LOPR);
        final String hopr = req.getParameter(PARAM_HOPR);

        try {
            getModel().configureNewChannel(name, group, type, lopr, hopr);
            resp.sendRedirect(ShowChannelResponse.urlTo(name.toString()));
        } catch (final EngineModelException e) {
            redirectToErrorPage(resp, "Channel could not be configured:\n" + e.getMessage());
        }
        */
    }

    @Nonnull
    public static String baseUrl() {
        return URL_ADD_CHANNEL_PAGE;
    }

    @Nonnull
    public static String linkTo(@Nonnull final String name) {
        return new Url(baseUrl()).with(PARAM_NAME, name).link(name);
    }

    @Nonnull
    public static String urlTo(@Nonnull final String name) {
        return new Url(baseUrl()).with(PARAM_NAME, name).url();
    }

    private List<EpicsChannelName> createEpicsNames(@Nonnull final HttpServletResponse resp,
                                                    @Nonnull final String groupName,
                                                    @Nonnull final String names) throws Exception {
        final List<EpicsChannelName> channelList = new ArrayList<EpicsChannelName>();
        try {
            if (Strings.isNullOrEmpty(names)) {
                redirectToErrorPage(resp, "Required parameter '" + PARAM_NAME + "' is either null or empty!");
                return null;
            }

            final String[] splits = names.split(" ");
            for (final String channelName : splits) {
                if(channelName.length()<5) {
                    continue;
                }
                final EpicsChannelName ename = new EpicsChannelName(channelName);
                 try {
                    channelList.add(ename);
                    getModel().configureNewChannel(ename, groupName, null, null, null);
                    startChannel(ename);
                } catch (final EngineModelException e) {
                    // TODO Auto-generated catch block
                    error_msg +=e.getMessage()+"<br>";
                    continue;
                }
            }

        } catch (final IllegalArgumentException e) {

            redirectToErrorPage(resp, "Channel name is not EPICS compatible:\n" + e.getMessage());

        } catch (final EngineModelException e) {
            error_msg+=e.getMessage()+"<br>";

        }

        return channelList;
    }

    private void startChannel(final EpicsChannelName channelName) throws EngineModelException {
        final ArchiveChannelBuffer<?, ?> buffer = getModel().getChannel(channelName.toString());
        if (buffer == null) {
            return;
        }
        if (buffer.isStarted()) {
            return;
        }

        buffer.start("START FILE IMPORT");

    }

    private void addChannel(final EpicsChannelName channelName, final String groupName) throws EngineModelException {
        getModel().configureNewChannel(channelName, groupName, null, null, null);
    }
}
