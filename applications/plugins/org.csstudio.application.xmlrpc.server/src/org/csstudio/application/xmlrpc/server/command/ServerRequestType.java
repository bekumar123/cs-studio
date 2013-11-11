
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

package org.csstudio.application.xmlrpc.server.command;

/**
 * @author mmoeller
 * @since 22.03.2013
 */
public enum ServerRequestType {

    RAW(0),
    AVERAGE(1, "OPTIMIZED"),
    TAIL_RAW(2);

    private int requestTypeNumber;

    private String alternateName;

    private ServerRequestType(int nr, String altName) {
        requestTypeNumber = nr;
        if (altName != null) {
            alternateName = altName;
        } else {
            alternateName = this.toString();
        }
    }

    private ServerRequestType(int nr) {
        this(nr, null);
    }

    public int getRequestTypeNumber() {
        return requestTypeNumber;
    }

    public String getAlternateName() {
        return alternateName;
    }

    public static ServerRequestType getRequestTypeByName(String name) {
        ServerRequestType result = ServerRequestType.RAW;
        for (ServerRequestType o : ServerRequestType.values()) {
           if (o.toString().compareToIgnoreCase(name) == 0
               || o.getAlternateName().compareToIgnoreCase(name) == 0) {
               result = o;
           }
        }
        return result;
    }
}
