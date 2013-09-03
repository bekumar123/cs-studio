/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.preferences;

import org.csstudio.common.trendplotter.Messages;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/** Preference Page, registered in plugin.xml
 *  @author Kay Kasemir
 */
public class PreferenceMysqlPage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage
{
    /** Initialize */
    public PreferenceMysqlPage()
    {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE,"org.csstudio.archive.common.service.mysqlimpl"));
        setMessage(Messages.PrefPage_MySql_Title);
    }

    /** {@inheritDoc} */
    @Override
    public void init(IWorkbench workbench)
    {
        // NOP
    }

    /** {@inheritDoc} */
    @Override
    protected void createFieldEditors()
    {
        final Composite parent = getFieldEditorParent();
        // host
        final StringFieldEditor host = new StringFieldEditor(Preferences.MYSQL_SERVER,
                Messages.mySqlPrefPageHost, parent);
        addField(host);
        // hostFailover
        final StringFieldEditor hostFailover = new StringFieldEditor(Preferences.MYSQL_SERVER_FAILOVER,
                Messages.mySqlPrefPageHostFailover, parent);
        addField(hostFailover);
        
        final IntegerFieldEditor port = new IntegerFieldEditor(Preferences.MYSQL_PORT,
                                                                     Messages.mySqlPrefPagePort, parent);
        addField(port);
        // dateBase
        final StringFieldEditor dateBase = new StringFieldEditor(Preferences.MYSQL_DATEBASE,
                Messages.mySqlPrefPageDatebase, parent);
        addField(dateBase);
         // User Name
        final StringFieldEditor username = new StringFieldEditor(Preferences.MYSQL_USERNAME,
                Messages.mySqlPrefPageUsername, parent);
        addField(username);
        
        //password
        final StringFieldEditor password = new StringFieldEditor(Preferences.MYSQL_PASSWORD,
                Messages.mySqlPrefPagePassword, parent);
        addField(password);
        
        //periode
        final IntegerFieldEditor periode = new IntegerFieldEditor(Preferences.MYSQL_PERIODE,
                Messages.mySqlPrefPagePeriodInMS, parent);
        addField(periode);
        
        //maxiPacket
        final IntegerFieldEditor maxiPacket = new IntegerFieldEditor(Preferences.MYSQL_MAXIMALPACKETINKB,
                Messages.mySqlPrefPageMaxAllowedPacketInKB, parent);
        addField(maxiPacket);
          
        //terminationTime
        final IntegerFieldEditor terminationTime = new IntegerFieldEditor(Preferences.MYSQL_TERMINATIONTIME,
                Messages.mySqlPrefPageTerminationTimeInMS, parent);
        addField(terminationTime);
               
        //dataRescue
        final StringFieldEditor dataRescue = new StringFieldEditor(Preferences.MYSQL_DATARESCUE,
                Messages.mySqlPrefPageDataRescueDir, parent);
        addField(dataRescue);
        
    
   
    }
}
