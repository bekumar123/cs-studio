
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

package org.csstudio.alarm.jms2ora;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @since 13.12.2012
 */
public class ThreadExceptionHandler implements UncaughtExceptionHandler {

    private static ThreadExceptionHandler instance;

    private static final Logger LOG = LoggerFactory.getLogger(ThreadExceptionHandler.class);

    private Stoppable stoppable;

    /** Avoid instanciation */
    private ThreadExceptionHandler() {
        stoppable = null;
    }

    public synchronized static ThreadExceptionHandler getInstance() {
        if (instance == null) {
            instance = new ThreadExceptionHandler();
        }
        return instance;
    }

    public synchronized static void initialize(Stoppable object) {
        if (instance == null) {
            instance = new ThreadExceptionHandler();
        }
        instance.setStoppable(object);
    }

    private synchronized void setStoppable(Stoppable object) {
        stoppable = object;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void uncaughtException(Thread t, Throwable e) {
        LOG.error("-----------------------------------------------");
        LOG.error("-----------   THREAD EXCEPTION   --------------");
        LOG.error("    Thread: {}", t.getName());
        LOG.error(" Exception: ", e);
        LOG.error("-----------------------------------------------");
        if (stoppable != null) {
            LOG.info("Try to stop the process.");
            stoppable.sendStopNotification();
            stoppable.stopWorking(false);
        } else {
            LOG.info("Cannot stop the process because no object reference is available.");
        }
    }
}
