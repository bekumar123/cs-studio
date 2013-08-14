/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.httpserver;

import gov.aps.jca.Channel.ConnectionState;

import java.io.PrintWriter;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.apputil.ringbuffer.RingBuffer;
import org.csstudio.archive.common.engine.model.ArchiveChannelBuffer;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.EngineModelException;
import org.csstudio.archive.common.engine.model.SampleBuffer;
import org.csstudio.archive.common.engine.model.SampleBufferStatistics;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.epics.name.EpicsChannelName;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.epics.util.time.Timestamp;
import org.epics.util.time.TimestampFormat;

/** Provide web page with detail for one channel.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class GetChannelResponse extends AbstractChannelResponse {

    private static String URL_BASE_PAGE;
    private static String URL_GET_CHANNEL_ACTION;
    static {
        URL_GET_CHANNEL_ACTION = "GET";
        URL_BASE_PAGE = URL_CHANNEL_PAGE + "/" + URL_GET_CHANNEL_ACTION;
    }
    /** Writer */
    private PrintWriter _html;
    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    GetChannelResponse(@Nonnull final EngineModel model) {
        super(model);
        _html=null;
    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        final EpicsChannelName name = parseEpicsNameOrConfigureRedirectResponse(req, resp);
        if (name == null) {
            return;
        }
        final ArchiveChannelBuffer<?, ?> channel = getModel().getChannel(name.toString());
        if (channel == null) {
            resp.sendError(400, "Unknown channel " + name.toString());
            return;
        }

        // HTML table similar to group's list of channels
     //   final HTMLWriter html = new HTMLWriter(resp, "Archive Engine Channel");
     //  createChannelTable(channel, html);
        _html= resp.getWriter();
        createChannelTable(channel);
     //   html.close();
    }
    /** One table line.
     *  @param columns Text for each column.
     *                 Count must match the colspan of openTable
     *  @see #openTable(int, String[])
     */
    Boolean oddTableRow=false;
    protected void tableLine(@Nonnull final String[] columns) {
        text("  <tr>");
        boolean first = true;
        for (final String column : columns) {
            if (first) {
                first = false;
                if (oddTableRow) {
                    text("    <th align='left' valign='top'>" + column + "</th>");
                } else {
                    text("    <th align='left' valign='top' bgcolor='#DFDFFF'>" + column + "</th>");
                }
            } else {
                if (oddTableRow) {
                    text("    <td align='center' valign='top'>" + column + "</td>");
                } else {
                    text("    <td align='center' valign='top' bgcolor='#DFDFFF'>" + column + "</td>");
                }
            }
        }
        text("  </tr>");
        oddTableRow = !oddTableRow;
    }
    /** Add text to HTML */
    protected void text(@Nonnull final String text) {
        _html.println(text);
    }
    private String getAdel(@Nonnull final String channelName){
        final ArchiveChannelBuffer<?, ?> channel =getModel().getChannel(channelName+".ADEL");

             if(channel!=null)
             {
                 final ISystemVariable<?> mostRecentSample = channel.getMostRecentSample();
                 return  limitLength(getValueAsString(mostRecentSample), 60);
             }


        return "";
    }
    private void createChannelTable(@Nonnull final ArchiveChannelBuffer<?, ?> channel) {
        try {
        text("<table>");
        tableLine(new String[] {Messages.HTTP_CHANNEL, channel.getName()});
        tableLine(new String[] {
                           Messages.HTTP_STARTED,
                           channel.isStarted() ? Messages.HTTP_YES : HTMLWriter.makeRedText(Messages.HTTP_NO),
                       });
        final String connected = channel.isConnected()
                        ? Messages.HTTP_YES
                        : HTMLWriter.makeRedText(Messages.HTTP_NO);
        tableLine(new String[] {Messages.HTTP_CONNECTED, connected});
        final ConnectionState state=channel.getConnectState();
        final String connState = state!=null? ConnectionState.CONNECTED.equals(state)?   state.getName() : HTMLWriter.makeRedText( state.getName()):null;
        final String cajDirectconnState ;
        final String isChannelConnected ;
        if( state!=null){
        cajDirectconnState = ConnectionState.CONNECTED.equals(state) && channel.isConnected()?   state.getName() : !ConnectionState.CONNECTED.equals(state) && !channel.isConnected() ?  HTMLWriter.makeRedText(  state.getName()) : HTMLWriter.makeRedText( channel.getCAJDirectConnectState().getName());

            isChannelConnected = ConnectionState.CONNECTED.equals(state) && channel.isConnected()?   Messages.HTTP_YES  : !ConnectionState.CONNECTED.equals(state) && !channel.isConnected() ?  HTMLWriter.makeRedText(  Messages.HTTP_NO) : channel.isChannelConnected() ? Messages.HTTP_YES :
            HTMLWriter.makeRedText(Messages.HTTP_NO);

         }else{
            cajDirectconnState=HTMLWriter.makeRedText("UNKNOWN");
            isChannelConnected=HTMLWriter.makeRedText("UNKNOWN");
        }
       if( connState!=null)
     {
        tableLine(new String[] {Messages.HTTP_CONN_STATE, connState});
          //  tableLine(new String[] {"CAJ direct", cajDirectconnState});
        //    tableLine(new String[] {"DB Direct", isChannelConnected });
    }

        tableLine(new String[] {Messages.HTTP_INTERNAL_STATE, channel.getInternalState()});
        tableLine(new String[] {Messages.HTTP_CURRENT_VALUE, getValueAsString(channel.getMostRecentSample())});
        tableLine(new String[] {Messages.HTTP_DEADBAND_VALUE, getAdel(channel.getName())});

        final SampleBuffer<?, ?, ?> buffer = channel.getSampleBuffer();
        tableLine(new String[] {Messages.HTTP_QUEUELEN, Integer.toString(buffer.size())});
        final SampleBufferStatistics stats = buffer.getBufferStats();
        tableLine(new String[] {Messages.HTTP_COLUMN_QUEUEAVG, String.format("%.1f", stats.getAverageSize())});

        tableLine(new String[] {Messages.HTTP_COLUMN_QUEUEMAX, Integer.toString(stats.getMaxSize())});
        tableLine(new String[] {Messages.HTTP_SUB_TABLETITLE,""});
        final RingBuffer<IArchiveSample<?,?>> ringbuffer=channel.getRingBuffer();
        for(int i=0; i<ringbuffer.size();i++){
            final IArchiveSample<?,?> sample=ringbuffer.get(i);
            tableLine(new String[]{ new TimestampFormat("dd.MM.yyyy' 'HH:mm:ss").format(Timestamp.of( sample.getSystemVariable().getTimestamp().getSeconds(), 0)),sample.getSystemVariable().getData().toString()});
        }

        if (channel.isStarted()) {
          tableLine(new String[] {
                                         Messages.HTTP_STOP_CHANNEL,
                                         StopChannelResponse.linkTo(channel.getName()),
                                         });
        } else if (channel.isEnabled()) {
            tableLine(new String[] {
                    Messages.HTTP_ACTION,
                    StartChannelResponse.linkTo(channel.getName()),
            });
           tableLine(new String[] {
                    Messages.HTTP_ACTION,
                    PermanentDisableChannelResponse.linkTo(channel.getName()),
            });
        } else {
            tableLine(new String[] {
                    Messages.HTTP_ACTION,
                    StartChannelResponse.linkTo(channel.getName()),
            });
        }
        text("</table>");
        } catch (final EngineModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        };
        oddTableRow=false;
    }

    @Nonnull
    public static String baseUrl() {
        return URL_BASE_PAGE;
    }
    @Nonnull
    public static String linkTo(@Nonnull final String name) {
        return linkTo(name, name);
    }
    @Nonnull
    public static String linkTo(@Nonnull final String name, @Nonnull final String linkText) {
        return new Url(baseUrl()).with(PARAM_NAME, name).link(linkText);
    }
    @Nonnull
    public static String urlTo(@Nonnull final String name) {
        return new Url(baseUrl()).with(PARAM_NAME, name).url();
    }
}
