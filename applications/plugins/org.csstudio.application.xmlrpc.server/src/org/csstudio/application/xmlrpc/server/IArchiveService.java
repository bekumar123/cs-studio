
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

package org.csstudio.application.xmlrpc.server;

import java.util.List;
import java.util.Map;


/**
 * @author mmoeller
 * @since 27.12.2012
 */
public interface IArchiveService {

    /**
     * Returns information about the server.
     *
     * @return Collection containing the result
     */
    Map<String, Object> info();

    /**
     *
     * @param key - Just for compatibility, the value will be ignored.
     * @param pattern - String containing the pattern of the channel name(s).
     *
     * @return
     */
    List<Map<String, Object>> names(Integer key, Object pattern);

    /**
     * Returns the provided archives. Just for compatibility. This method returns always the
     * same data because the MySQL archiv only provides one archive.
     *
     * @return
     */
    List<Map<String, Object>> archives();

    /**
     * Returns the values of the channels for the given time interval.
     *
     * @param key
     * @param name
     * @param startSec
     * @param startNano
     * @param endSec
     * @param endNano
     * @param count
     * @param how
     *
     * @return
     */
    List<Map<String, Object>> values(Integer key, Object[] name, Integer startSec, Integer startNano,
                                     Integer endSec, Integer endNano, Integer count, Integer how);
}
