/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.httpserver;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.model.ArchiveGroup;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide web page with engine overview.
 *  @author Kay Kasemir
 */
class ManageResponse extends AbstractResponse {

    private static final String URL_BASE_DESC = Messages.HTTP_MANAGE;

    private static final Logger LOG = LoggerFactory.getLogger(ManageResponse.class);

    private static final String URL_BASE_PAGE = "/manage";
    static final String PARAM_CHANNEL_GROUP = "group";
    protected static final String URL_CHANNEL_PAGE = "/channel";
    private static String URL_ADD_CHANNEL_ACTION;
    private static String URL_ADD_CHANNEL_PAGE;
    private static String URL_STOP_ENGINE_ACTION;
    private static String URL_STOP_ENGINE_PAGE;
    int size = 0;
    static {
        URL_ADD_CHANNEL_ACTION = "add";
        URL_STOP_ENGINE_ACTION="/shutdown";
        URL_ADD_CHANNEL_PAGE = URL_CHANNEL_PAGE + "/" + URL_ADD_CHANNEL_ACTION;
        URL_STOP_ENGINE_PAGE=URL_STOP_ENGINE_ACTION;
    }

    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    ManageResponse(@Nonnull final EngineModel model) {
        super(model);

    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp) throws Exception {
        final HTMLWriter html = new HTMLWriter(resp, Messages.HTTP_MANAGE) {
            /** One table line.
             *  @param columns Text for each column.
             *                 Count must match the colspan of openTable
             *  @see #openTable(int, String[])
             */
            @Override
            protected void tableLine(@Nonnull final String[] columns) {
                text("  <tr>");
                boolean first = true;
                for (final String column : columns) {
                    if (column != null) {
                        if (first) {
                            first = false;
                            text("    <th align='left' valign='top'  style='padding-right: 20'>" + column + "</th>");
                        } else {
                            text("    <td align='left' valign='top'  style='padding-right: 20'>" + column + "</td>");
                        }
                    }
                }
                text("  </tr>");

            }
            /** Start a table.
             *  <p>
             *  The intial column header might span more than one column.
             *  In fact, it might be the only columns header.
             *  Otherwise, the remaining column headers each span one column.
             *
             *  @param initialColSpan Number of columns for the first header.
             *  @param header Headers for all the columns
             *  @see #tableLine(String[])
             *  @see #closeTable()
             */
            @Override
            protected void openTable(final int initialColSpan,
                                     @Nonnull final String[] headers) {
                text("<table border='0'>");
                text("<thead>");
                text("  <tr bgcolor='#FFCC66'>");
                text("    <th align='center' colspan='" + initialColSpan + "'>" +
                                headers[0] + "</th>");
                for (int i=1; i<headers.length; ++i) {
                    if( headers[i] !=null) {
                        text("    <th align='center'>" + headers[i] + "</th>");
                    }
                }
                text("  </tr>");
                text("</thead>");
                text("<tbody>");

            }

        };
        stopEngineForm(req, html);
        createAddChannelForm(req, html);
    //    importChannelForm(req, html);

        html.close();
    }

    private void createAddChannelForm(@Nonnull final HttpServletRequest req, @Nonnull final HTMLWriter html) {

        String form =
                      "<form action=\"" + AddChannelResponse.urlTo(URL_ADD_CHANNEL_PAGE)
                              + "\" method=\"GET\" name=\"name\">";
        html.text(form);
        html.openTable(2, new String[] { Messages.HTTP_MANAGE_ADD_CHANNEL });
        String groupName = "<select name=\"group\">";

        for (final ArchiveGroup group : getModel().getGroups()) {
            if (size < group.getName().length()) {
                size = group.getName().length();
            }
            groupName += "<option value=\"" + group.getName() + "\">" + group.getName() + "</option>";
        }
        groupName += "</select>";
        html.tableLine(new String[] { Messages.HTTP_GROUP, groupName });
         String st = "<input type=\"text\" name=\"name\" size=\"" + size*1.5 + "\">";
        html.tableLine(new String[] { Messages.HTTP_CHANNEL, st });
        final String [] types={"Integer", "Double","Byte","Short","Long","Float","EpicsEnum"};
        st="<select name=\"datatype\">" + "<option value=\"\">"+"</option>";
        for (final String type :types) {
           st += "<option value=\"" +type+"\">" +type+"</option>";
           st += "<option value=\"ArrayList&lt;" +type+"&gt;\">ArrayList&lt;" +type+"&gt;</option>";
        }
        st += "</select>";
       // html.tableLine(new String[] { Messages.HTTP_CHANNEL_DATATYPE, st });
        st = "<input type=\"text\" name=\"lopr\" size=\"" + size*1.5 + "\">";
     //   html.tableLine(new String[] { Messages.HTTP_CHANNEL_DISPLAYLOW, st });
        st = "<input type=\"text\" name=\"hopr\" size=\"" + size*1.5 + "\">";
      //  html.tableLine(new String[] { Messages.HTTP_CHANNEL_DISPLAYHIGH, st });
        final String button = "<input type=\"submit\" value=\""+ Messages.HTTP_MANAGE_ADD_CHANNEL+"\">";

        html.tableLine(new String[] { "", button });
        html.closeTable();
        form = "</form>";
        html.text(form);

    }


    private void stopEngineForm(@Nonnull final HttpServletRequest req, @Nonnull final HTMLWriter html) {

        String form =
                      "<form action=\"" + ShutdownResponse.baseUrl()
                              + "\" method=\"GET\" name=\"name\" >";
        html.text(form);
        html.openTable(2, new String[] { Messages.HTTP_MANAGE_STOP_ENGINE });
        final String passwordName = "<input type=\"password\" name=\"httpAdmin\" size=\"" + size*1.5 + "\">";
        html.tableLine(new String[] { Messages.PASSWORD, passwordName });
        final String button = "<input type=\"submit\" value=\""+Messages.HTTP_MANAGE_STOP_ENGINE+"\">";

        html.tableLine(new String[] { "", button });
        html.closeTable();
        form = "</form>";
        html.text(form);

    }


    private void importChannelForm(@Nonnull final HttpServletRequest req, @Nonnull final HTMLWriter html) {

        String form =
                      "<form action=\"" + ImportResponse.baseUrl()
                              + "\" method=\"POST\" name=\"name\" enctype=\"multipart/form-data\">";
        html.text(form);
        html.openTable(2, new String[] { "Channels Import" });
        final String passwordName = "<input type=\"password\" name=\"httpAdmin\" size=\"" + size*1.5 + "\">";
        html.tableLine(new String[] { Messages.PASSWORD, passwordName });
        final String datei = "<input type=\"file\" name=\"datei\" size=\"50\" maxlength=\"100000\" accept=\"text/config\">";
        html.tableLine(new String[] { "W&auml;hlen Sie eine Importatei von Ihrem Rechner aus:"});
        html.tableLine(new String[] { "", datei });

        final String button = "<input type=\"submit\" value=\"Import\">";

        html.tableLine(new String[] { "", button });
        html.closeTable();
        form = "</form>";
        html.text(form);

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
