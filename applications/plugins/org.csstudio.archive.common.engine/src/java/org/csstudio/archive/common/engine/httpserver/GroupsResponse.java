/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.httpserver;

import gov.aps.jca.Channel.ConnectionState;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.model.ArchiveChannelBuffer;
import org.csstudio.archive.common.engine.model.ArchiveGroup;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.SampleBufferStatistics;

/** Provide web page with basic info for all the groups.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class GroupsResponse extends AbstractResponse {

    private static final String URL_BASE_PAGE = "/groups";
    private static final String URL_BASE_DESC = Messages.HTTP_GROUPS;

    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    GroupsResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp) throws Exception {
        final HTMLWriter html = new HTMLWriter(resp, "Archive Engine Groups");

        createGroupsTable(html);

        html.close();
    }

    private void openTableWithHeader(@Nonnull final HTMLWriter html) {
        html.openTable(1, new String[] { Messages.HTTP_COLUMN_GROUP, numOf(Messages.HTTP_COLUMN_CHANNELS),
                                        numOf(Messages.HTTP_START_CHANNEL), numOf(Messages.HTTP_CONNECTED),
                                        numOf(Messages.HTTP_CONNECTED_CHANNEL_STATE),
                                        numOf(Messages.HTTP_DISCONNECTED_CHANNEL),
                                        numOf(Messages.HTTP_NEVERCONNECTED_CHANNEL), numOf(Messages.HTTP_CLOSED_CHANNEL),
                                        numOf(Messages.HTTP_UNKNOWN_CHANNEL), numOf(Messages.HTTP_COLUMN_RECEIVEDVALUES),
                                        Messages.HTTP_COLUMN_QUEUEAVG, Messages.HTTP_COLUMN_QUEUEMAX, });
    }

    private void createGroupsTable(@Nonnull final HTMLWriter html) {

        int totalNumOfChannels = 0;
        int totalNumOfConnectedChannels = 0;
        int totalNumOfConnectedStateChannels = 0;
        int totalNumOfStartedChannels = 0;
        int totalNumOfDisconnectedStateChannels = 0;
        int totalNumOfNeverConnectedStateChannels = 0;
        int totalNumOfColsedStateChannels = 0;
        int totalNumOfUnknownStateChannels = 0;
        long totalNumOfReceivedSamples = 0;
        boolean hasNumOfConnectedStateChannels = false;
        boolean hasNumOfDisconnectedStateChannels = false;
        boolean hasNumOfNeverConnectedStateChannels = false;
        boolean hasNumOfClosedStateChannels = false;
        boolean hasNumOfUnknownStateChannels = false;
        for (final ArchiveGroup group : getModel().getGroups()) {
            final Collection<ArchiveChannelBuffer> channels = group.getChannels();
            for (@SuppressWarnings("rawtypes")
            final ArchiveChannelBuffer channel : channels) {
                if(channel!=null) {
                    if(channel.getConnectState()!=null) {
                        switch (channel.getConnectState().getValue()) {
                            case 2:
                                hasNumOfConnectedStateChannels = true;
                                break;
                            case 1:
                                hasNumOfDisconnectedStateChannels = true;
                                break;
                            case 0:
                                hasNumOfNeverConnectedStateChannels = true;
                                break;
                            case 3:
                                hasNumOfClosedStateChannels = true;
                                break;
                            default:
                                hasNumOfUnknownStateChannels = true;
                                break;
                        }
                    }else{
                        hasNumOfUnknownStateChannels = true;
                    }
                }
            }
        }
        html.openTable(1,
                       new String[] {
                                     Messages.HTTP_COLUMN_GROUP,
                                     numOf(Messages.HTTP_COLUMN_CHANNELS),
                                     numOf(Messages.HTTP_START_CHANNEL),
                                     numOf(Messages.HTTP_CONNECTED),
                                     hasNumOfConnectedStateChannels ? numOf(Messages.HTTP_CONNECTED_CHANNEL_STATE) : null,
                                     hasNumOfDisconnectedStateChannels ? numOf(Messages.HTTP_DISCONNECTED_CHANNEL) : null,
                                     hasNumOfNeverConnectedStateChannels ? numOf(Messages.HTTP_NEVERCONNECTED_CHANNEL)
                                                                        : null,
                                     hasNumOfClosedStateChannels ? numOf(Messages.HTTP_CLOSED_CHANNEL) : null,
                                     hasNumOfUnknownStateChannels ? numOf(Messages.HTTP_UNKNOWN_CHANNEL) : null,
                                     numOf(Messages.HTTP_COLUMN_RECEIVEDVALUES), Messages.HTTP_COLUMN_QUEUEAVG,
                                     Messages.HTTP_COLUMN_QUEUEMAX, });
        for (final ArchiveGroup group : getModel().getGroups()) {

            int numOfConnectedChannels = 0;
            int numOfStartedChannels = 0;
            int numOfConnectedStateChannels = 0;
            int numOfDisconnectedStateChannels = 0;
            int numOfNeverConnectedStateChannels = 0;
            int numOfClosedStateChannels = 0;
            int numOfUnknownStateChannels = 0;
            double avgQueueLength = 0;
            int maxQueueLength = 0;
            long numOfReceivedSamples = 0;

            @SuppressWarnings("rawtypes")
            final Collection<ArchiveChannelBuffer> channels = group.getChannels();
            for (@SuppressWarnings("rawtypes")
            final ArchiveChannelBuffer channel : channels) {
                if (channel.isConnected()) {
                    ++numOfConnectedChannels;
                }
                numOfStartedChannels += channel.isStarted() ? 1 : 0;
                numOfConnectedStateChannels += ConnectionState.CONNECTED.equals(channel.getConnectState()) ? 1 : 0;
                numOfDisconnectedStateChannels += ConnectionState.DISCONNECTED.equals(channel.getConnectState()) ? 1 : 0;
                numOfNeverConnectedStateChannels +=
                                                    ConnectionState.NEVER_CONNECTED.equals(channel.getConnectState()) ? 1
                                                                                                                     : 0;
                numOfClosedStateChannels += ConnectionState.CLOSED.equals(channel.getConnectState()) ? 1 : 0;
                numOfUnknownStateChannels += channel.getConnectState() == null ? 1 : 0;
                numOfReceivedSamples += channel.getReceivedValues();
                final SampleBufferStatistics stats = channel.getSampleBuffer().getBufferStats();
                avgQueueLength += stats.getAverageSize();
                maxQueueLength = Math.max(maxQueueLength, stats.getMaxSize());
            }
            final int numOfChannels = channels.size();
            if (numOfChannels > 0) {
                avgQueueLength /= numOfChannels;
            }
            totalNumOfChannels += numOfChannels;
            totalNumOfStartedChannels += numOfStartedChannels;
            totalNumOfConnectedChannels += numOfConnectedChannels;
            totalNumOfReceivedSamples += numOfReceivedSamples;
            totalNumOfConnectedStateChannels += numOfConnectedStateChannels;
            totalNumOfDisconnectedStateChannels += numOfDisconnectedStateChannels;
            totalNumOfNeverConnectedStateChannels += numOfNeverConnectedStateChannels;
            totalNumOfColsedStateChannels += numOfClosedStateChannels;
            totalNumOfUnknownStateChannels += numOfUnknownStateChannels;
            html.tableLine(new String[] {
                                         ShowGroupResponse.linkTo(group.getName()),
                                         Integer.toString(numOfChannels),
                                         createChannelConnectedTableEntry(numOfStartedChannels, numOfChannels),
                                         createChannelConnectedTableEntry(numOfConnectedChannels, numOfChannels),
                                         hasNumOfConnectedStateChannels
                                                                       ? createChannelConnectedTableEntry(numOfConnectedStateChannels,
                                                                                                          numOfChannels)
                                                                       : null,
                                         hasNumOfDisconnectedStateChannels
                                                                          ? createChannelConnectedTableEntry(numOfDisconnectedStateChannels,
                                                                                                             0) : null,
                                         hasNumOfNeverConnectedStateChannels
                                                                            ? createChannelConnectedTableEntry(numOfNeverConnectedStateChannels,
                                                                                                               0) : null,
                                         hasNumOfClosedStateChannels
                                                                    ? createChannelConnectedTableEntry(numOfClosedStateChannels,
                                                                                                       0) : null,
                                         hasNumOfUnknownStateChannels
                                                                     ? createChannelConnectedTableEntry(numOfUnknownStateChannels,
                                                                                                        0) : null,
                                         Long.toString(numOfReceivedSamples), String.format("%.1f", avgQueueLength),
                                         Integer.toString(maxQueueLength), });
        }
        html.tableLine(new String[] {
                                     Messages.HTTP_ROW_TOTAL,
                                     Integer.toString(totalNumOfChannels),
                                     createChannelConnectedTableEntry(totalNumOfStartedChannels, totalNumOfChannels),
                                     createChannelConnectedTableEntry(totalNumOfConnectedChannels, totalNumOfChannels),
                                     hasNumOfConnectedStateChannels
                                                                   ? createChannelConnectedTableEntry(totalNumOfConnectedStateChannels,
                                                                                                      totalNumOfChannels)
                                                                   : null,
                                     hasNumOfDisconnectedStateChannels
                                                                      ? createChannelConnectedTableEntry(totalNumOfDisconnectedStateChannels,
                                                                                                         0) : null,
                                     hasNumOfNeverConnectedStateChannels
                                                                        ? createChannelConnectedTableEntry(totalNumOfNeverConnectedStateChannels,
                                                                                                           0) : null,
                                     hasNumOfClosedStateChannels
                                                                ? createChannelConnectedTableEntry(totalNumOfColsedStateChannels,
                                                                                                   0) : null,
                                     hasNumOfUnknownStateChannels
                                                                 ? createChannelConnectedTableEntry(totalNumOfUnknownStateChannels,
                                                                                                    0) : null,
                                     Long.toString(totalNumOfReceivedSamples), "", "", });
        html.closeTable();
        /*    closeTableWithSummaryRow(html,
                                     totalNumOfChannels,
                                     totalNumOfStartedChannels,
                                     totalNumOfConnectedChannels,
                                     totalNumOfConnectedStateChannels,
                                     totalNumOfDisconnectedStateChannels,
                                     totalNumOfNeverConnectedStateChannels,
                                     totalNumOfColsedStateChannels,
                                     totalNumOfUnknownStateChannels,
                                     totalNumOfReceivedSamples);*/
    }

    @Nonnull
    private String createChannelConnectedTableEntry(final int numOfConnectedChannels, final int numOfChannels) {
        final String connected =
                                 numOfChannels == numOfConnectedChannels ? Integer.toString(numOfConnectedChannels)
                                                                        : HTMLWriter.makeRedText(Integer
                                                                                .toString(numOfConnectedChannels));
        return connected;
    }

    private void closeTableWithSummaryRow(@Nonnull final HTMLWriter html,
                                          final int totalNumOfChannels,
                                          final int totalNumOfStartedChannels,
                                          final int totalNumOfConnectedChannels,
                                          final int totalNumOfConnectedStateChannels,
                                          final int totalNumOfDisconnectedStateChannels,
                                          final int totalNumOfNeverConnectedStateChannels,
                                          final int totalNumOfColsedStateChannels,
                                          final int totalNumOfUnknownStateChannels,
                                          final long totalNumOfReceivedSamples) {
        html.tableLine(new String[] { Messages.HTTP_ROW_TOTAL, Integer.toString(totalNumOfChannels),
                                     createChannelConnectedTableEntry(totalNumOfStartedChannels, totalNumOfChannels),
                                     createChannelConnectedTableEntry(totalNumOfConnectedChannels, totalNumOfChannels),
                                     createChannelConnectedTableEntry(totalNumOfConnectedStateChannels, totalNumOfChannels),
                                     createChannelConnectedTableEntry(totalNumOfDisconnectedStateChannels, 0),
                                     createChannelConnectedTableEntry(totalNumOfNeverConnectedStateChannels, 0),
                                     createChannelConnectedTableEntry(totalNumOfColsedStateChannels, 0),
                                     createChannelConnectedTableEntry(totalNumOfUnknownStateChannels, 0),
                                     Long.toString(totalNumOfReceivedSamples), "", "", });
        html.closeTable();
    }

    @Nonnull
    public static String baseUrl() {
        return URL_BASE_PAGE;
    }

    @Nonnull
    public static String linkTo(@Nonnull final String linkText) {
        return new Url(baseUrl()).link(linkText);
    }

    @Nonnull
    public static String linkTo() {
        return new Url(baseUrl()).link(URL_BASE_DESC);
    }
}
