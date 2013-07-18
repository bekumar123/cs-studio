
/*
 * Copyright (c) 2013 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.websuite.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mmoeller
 * @since 18.07.2013
 */
public class HowToLink implements IUtilityLink {

    private Pattern pattern;

    private String url;

    public HowToLink(String address, int port) {
        pattern = Pattern.compile("HOWTO:[ ]?\\d+", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        url = "http://" + address + ":" + port + "/HowToViewer?value=";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String replace(String text) {
        if (text == null) {
            return text;
        }
        if (text.trim().isEmpty()) {
            return text;
        }
        String temp = text;
        Matcher matcher = pattern.matcher(temp);
        while (matcher.find()) {
            String utilId = matcher.group();
            int value = getLinkValue(utilId);
            temp = temp.replace(utilId, "<a href=\"" + url + value + "\">" + utilId + "</a>");
        }
        return temp.toString();
    }

    private int getLinkValue(String id) {
        if (id == null) {
            return -1;
        }
        int value = -1;
        String[] parts = id.split(":");
        if (parts.length == 2) {
            try {
                value = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                value = -1;
            }
        }
        return value;
    }
}
