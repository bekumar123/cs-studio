
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
 */

package org.csstudio.archive.sdds.server.internal;

/**
 * @author Markus Moeller
 *
 */
public final class ServerPreferenceKey {

    public static final String P_USE_JMX = "useJmx";
    public static final String P_XMPP_SERVER = "xmppServer";
    public static final String P_XMPP_USER = "xmppUser";
    public static final String P_XMPP_PASSWORD = "xmppPassword";
    public static final String P_DESCRIPTION = "description";
    public static final String P_SERVER_PORT = "serverPort";
    public static final String P_SDDS_LITTLE_ENDIAN = "sddsLittleEndian";
    public static final String P_VALID_RECORD_BEFORE = "validRecordBefore";
    public static final String P_MAX_SAMPLES_PER_REQUEST = "maxSamplesPerRequest";
    public static final String P_IGNORE_BIG_FILES = "ignoreBigFiles";
    public static final String P_MAX_FILE_SIZE = "maxFileSize";
    public static final String P_USE_COMPRESSED_FILES = "useCompressedFiles";

    private ServerPreferenceKey() {
        // Avoid instantiation
    }
}
