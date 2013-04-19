
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

package org.csstudio.headless.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Properties;
import org.csstudio.headless.common.time.StartTime;

/**
 * @author mmoeller
 * @since 18.04.2013
 */
public class ApplicationInfo {

    private final String NOT_AVAILABLE = "Not available";

    private StartTime startTime;

    private String version;

    private String description;

    public ApplicationInfo(String desc) {
        startTime = new StartTime();
        readVersionFile();
        if (desc != null) {
            description = desc.trim();
            if (description.trim().isEmpty()) {
                description = NOT_AVAILABLE;
            }
        } else {
            description = NOT_AVAILABLE;
        }
    }

    private void readVersionFile() {
        File file = new File(".eclipseproduct");
        if (file.exists()) {
            URI uri = file.toURI();
            String path;
            try {
                path = uri.toURL().getPath();
                if (path != null) {
                    final Properties prop = new Properties();
                    try {
                        prop.load(new FileInputStream(path));
                        version = prop.getProperty("version", NOT_AVAILABLE);
                    } catch (Exception fnfe) {
                        version = NOT_AVAILABLE;
                    }
                }
            } catch (MalformedURLException e) {
                version = NOT_AVAILABLE;
            }
        } else {
            version = NOT_AVAILABLE;
        }
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("Jms2Oracle Version: " + version);
        str.append("\n\nDescription: " + description);
        str.append("\n\nStarting time\n  " + startTime.getStartingTimeAsString());
        str.append("\n\nUptime\n  " + startTime.getRunningTimeAsString());
        return str.toString();
    }
}
