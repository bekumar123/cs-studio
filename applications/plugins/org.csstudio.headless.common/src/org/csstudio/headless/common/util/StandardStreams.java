
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
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author mmoeller
 * @since 19.04.2013
 */
public class StandardStreams {

    private SimpleDateFormat dateFormat;

    private boolean stdOutRedirected;

    private boolean stdErrRedirected;

    public StandardStreams() {
        dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
    }

    public void redirectStreams() {
        File stdOut = new File("./stdout.txt");
        Date currentTime = new Date(System.currentTimeMillis());
        if (stdOut.exists()) {
            stdOut.renameTo(new File("./stdout-" + dateFormat.format(currentTime) + ".txt"));
        }
        try {
            System.setOut(new PrintStream(new File("./stdout.txt")));
            stdOutRedirected = true;
        } catch (FileNotFoundException e) {
            stdOutRedirected = false;
        }
        File stdErr = new File("./stderr.txt");
        if (stdErr.exists()) {
            stdErr.renameTo(new File("./stderr-" + dateFormat.format(currentTime) + ".txt"));
        }
        try {
            System.setErr(new PrintStream(new File("./stderr.txt")));
            stdErrRedirected = true;
        } catch (FileNotFoundException e) {
            stdErrRedirected = false;
        }
    }

    public boolean isStdOutRedirected() {
        return stdOutRedirected;
    }

    public boolean isStdErrRedirected() {
        return stdErrRedirected;
    }
}
