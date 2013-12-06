
/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.archive.reader.mysql;

import org.epics.vtype.AlarmSeverity;

/** Wrapper for {@link AlarmSeverity} that adds ChannelArchiver detail
 *  @author Kay Kasemir
 */
public class SeverityImpl {

    private AlarmSeverity severity;
	private String text;
	private boolean hasValue;
	private boolean txtStat;

	public SeverityImpl(AlarmSeverity severity, String text, boolean hasValue, boolean txtStat) {
		this.severity = severity;
		this.text = text;
		this.hasValue = hasValue;
		this.txtStat = txtStat;
	}

    public AlarmSeverity getSeverity() {
		return severity;
	}

    public String getText() {
		return text;
	}

    public boolean hasValue() {
		return hasValue;
	}

	public boolean statusIsText() {
		return txtStat;
	}
}
