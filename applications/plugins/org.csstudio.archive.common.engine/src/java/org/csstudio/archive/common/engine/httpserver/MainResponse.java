/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.httpserver;

import gov.aps.jca.Channel.ConnectionState;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.model.ArchiveChannelBuffer;
import org.csstudio.archive.common.engine.model.ArchiveGroup;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.eclipse.core.runtime.Platform;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Provide web page with engine overview.
 *  @author Kay Kasemir
 */
class MainResponse extends AbstractResponse {

    private static final String URL_BASE_DESC = Messages.HTTP_MAIN;

    private static final Logger LOG = LoggerFactory.getLogger(MainResponse.class);

    private static final String URL_BASE_PAGE = "/main";


    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    /** Bytes in a MegaByte */
    private static final double MB = 1024.0*1024.0;


    private final String _host;
    private final String _version;

    MainResponse(@Nonnull final EngineModel model,
                 @Nonnull final String version) {
        super(model);
        _version = version;
        _host = findHostName();
    }

    @Nonnull
    private String findHostName() {
        String host = null;
        try {
            host = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (final UnknownHostException ex) {
            LOG.warn("Host name could not be resolved, fall back to 'localhost'.");
            host = "localhost";
        }
        return host;
    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        final HTMLWriter html = new HTMLWriter(resp, Messages.HTTP_MAIN_TITLE);
        html.openTable(2, new String[] {Messages.HTTP_SUMMARY});

        createTableRows(req, html);

        html.closeTable();
        html.close();
    }

    private void createTableRows(@Nonnull final HttpServletRequest req,
                                 @Nonnull final HTMLWriter html) {
        createProgramInfoRows(req, html);

        createChannelStatsRows(html);

        createWriteStatsRows(html);

        createMemoryStatsRow(html);
    }

    private void createProgramInfoRows(@Nonnull final HttpServletRequest req,
                                       @Nonnull final HTMLWriter html) {
        final String s= Platform.getInstanceLocation().getURL().getFile().toString();
        try{
            html.tableLine(new String[] {Messages.HTTP_VERSION, _version+"-"+ Long.parseLong(s)});
        }catch(final NumberFormatException e){
            html.tableLine(new String[] {Messages.HTTP_VERSION, _version+"-"+ 0});
        }

        html.tableLine(new String[] {Messages.HTTP_DESCRIPTION, getModel().getName()});
        html.tableLine(new String[] {Messages.HTTP_HOST, _host + ":" + req.getLocalPort()});
        html.tableLine(new String[] {Messages.HTTP_STATE, getModel().getState().name()});

        final TimeInstant start = getModel().getStartTime();
        if (start != null) {
            html.tableLine(new String[] {Messages.HTTP_STARTTIME, start.formatted()});
            final Duration dur = new Duration(start.getInstant(),
                                              TimeInstantBuilder.fromNow().getInstant());
            html.tableLine(new String[] {Messages.HTTP_UPTIME,
                                         TimeInstant.STD_DURATION_FMT.print(dur.toPeriod()),
                                         });
        }

        html.tableLine(new String[] {Messages.HTTP_WORKSPACE,
                                     Platform.getInstanceLocation().getURL().getFile().toString(),
                                     });
    }

    private void createChannelStatsRows(@Nonnull final HTMLWriter html) {
        int numOfChannels = 0;
        int numOfConnectedChannels = 0;
        int numOfStartedChannels = 0;
        int numOfConnectedStateChannels = 0;
        int numOfDisconnectedStateChannels = 0;
        int numOfNeverConnectedStateChannels = 0;
        int numOfClosedStateChannels = 0;
        int numOfUnknownStateChannels = 0;
        for (final ArchiveGroup group : getModel().getGroups()) {
            numOfChannels += group.getChannels().size();

            for (final ArchiveChannelBuffer<?, ?> channel : group.getChannels()) {
                numOfConnectedChannels += channel.isConnected() ? 1 : 0;
                numOfStartedChannels += channel.isStarted()?1:0;
                numOfConnectedStateChannels += ConnectionState.CONNECTED.equals(channel.getConnectState()) ? 1:0;
                numOfDisconnectedStateChannels += ConnectionState.DISCONNECTED.equals(channel.getConnectState())? 1:0;
                numOfNeverConnectedStateChannels += ConnectionState.NEVER_CONNECTED.equals(channel.getConnectState())? 1:0;
                numOfClosedStateChannels += ConnectionState.CLOSED.equals(channel.getConnectState())? 1:0;
                numOfUnknownStateChannels += channel.getConnectState()==null ? 1:0;

            }
        }
        html.tableLine(new String[] {numOf(Messages.HTTP_COLUMN_GROUPCOUNT),
                                     String.valueOf(getModel().getGroups().size()),
                                     });
        html.tableLine(new String[] {numOf(Messages.HTTP_COLUMN_CHANNELS),
                                     String.valueOf(numOfChannels),
                                     });
        if (numOfStartedChannels > 0) {
            html.tableLine(new String[] {numOf(Messages.HTTP_START_CHANNEL),
                    numOfStartedChannels==numOfChannels?  String.valueOf(numOfStartedChannels): HTMLWriter.makeRedText(String.valueOf(numOfStartedChannels)),
                                         });
        }
        final int numOfDisconnectedChannels = numOfChannels - numOfConnectedChannels;
        if (numOfDisconnectedChannels > 0) {
            html.tableLine(new String[] {numOf(Messages.HTTP_NOT_CONNECTED),
                                         HTMLWriter.makeRedText(String.valueOf(numOfDisconnectedChannels)),
                                         });
        }
        if (numOfConnectedStateChannels > 0) {
            html.tableLine(new String[] {numOf(Messages.HTTP_CONNECTED_CHANNEL_STATE),
                                         HTMLWriter.makeRedText(String.valueOf(numOfConnectedStateChannels)),
                                         });
        }
        if (numOfDisconnectedStateChannels > 0) {
            html.tableLine(new String[] {numOf(Messages.HTTP_DISCONNECTED_CHANNEL),
                                         HTMLWriter.makeRedText(String.valueOf(numOfDisconnectedStateChannels)),
                                         });
        }
        if (numOfNeverConnectedStateChannels > 0) {
            html.tableLine(new String[] {numOf(Messages.HTTP_NEVERCONNECTED_CHANNEL),
                                         HTMLWriter.makeRedText(String.valueOf(numOfNeverConnectedStateChannels)),
                                         });
        }
        if (numOfClosedStateChannels> 0) {
            html.tableLine(new String[] {numOf(Messages.HTTP_CLOSED_CHANNEL),
                                         HTMLWriter.makeRedText(String.valueOf(numOfClosedStateChannels)),
                                         });
        }

        if (numOfUnknownStateChannels > 0) {
            html.tableLine(new String[] {numOf(Messages.HTTP_UNKNOWN_CHANNEL),
                                         HTMLWriter.makeRedText(String.valueOf(numOfUnknownStateChannels)),
                                         });
        }

    }

    private void createWriteStatsRows(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {Messages.HTTP_WRITE_PERIOD,
                                     getModel().getWritePeriodInMS() + " ms",
                                     });

        final TimeInstant lastWriteTime = getModel().getLastWriteTime();
        html.tableLine(new String[] {Messages.HTTP_LAST_WRITETIME,
                                     lastWriteTime == null ? Messages.HTTP_NEVER :
                                                              lastWriteTime.formatted(),
                                                              });

        final Double avgWriteCount = getModel().getAvgWriteCount();
        html.tableLine(new String[] {numOf(Messages.HTTP_AVG_WRITE),
                                     (avgWriteCount != null ? String.format("%.1f", avgWriteCount):
                                                              Messages.HTTP_NO) + " samples",
                                                              });
        final Duration avgWriteDuration = getModel().getAvgWriteDuration();
        String printDur = "NONE";
        if (avgWriteDuration != null) {
            printDur =
                TimeInstant.STD_DURATION_WITH_MILLIS_FMT.print(avgWriteDuration.toPeriod());
            if (Strings.isNullOrEmpty(printDur)) {
                printDur = "<1";
            }
            printDur += "ms";
        }
        html.tableLine(new String[] {Messages.HTTP_WRITE_DURATION, printDur});
    }

    private void createMemoryStatsRow(@Nonnull final HTMLWriter html) {
        final Runtime runtime = Runtime.getRuntime();
        final double maxMem = runtime.maxMemory() / MB;
        final double totalMem = runtime.totalMemory() / MB;
        final double freeMem = runtime.freeMemory() / MB;
        final double percMem = maxMem > 0 ? totalMem / maxMem * 100.0 : 0.0;
        html.tableLine(new String[] {"Memory (used|total|max)",
                                     String.format("%.1f MB | %.1f MB | %.1f MB (%.1f %%)",
                                                   totalMem-freeMem, totalMem, maxMem, percMem),
                                                   });
    }

    @Nonnull
    public static final String baseUrl() {
        return URL_BASE_PAGE;
    }
    @Nonnull
    public static String linkTo(@CheckForNull final String linkText) {
        return new Url(baseUrl()).link(linkText);
    }
    @Nonnull
    public static String linkTo() {
        return new Url(baseUrl()).link(URL_BASE_DESC);
    }
}
