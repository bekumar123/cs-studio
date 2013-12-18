package org.csstudio.common.trendplotter;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveRepository;
import org.csstudio.common.trendplotter.model.ArchiveDataSource;
import org.csstudio.common.trendplotter.preferences.Preferences;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * Check if given pv is in archives.
 * 
 *  @author jhatje
 */
public class CheckPvInArchivesJob extends Job
{
    private String _pvName;
    private StringBuffer _infoText;

    public CheckPvInArchivesJob(String name, String pvName) {
        super(name);
        _pvName = pvName;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        _infoText = new StringBuffer();
        _infoText.append("\nRecord is in archive(s): ");
        
        ArchiveDataSource[] archiveDataSources = Preferences.getArchives();
        for (ArchiveDataSource archiveDataSource : archiveDataSources) {
            ArchiveReader reader;
            try {
                reader = ArchiveRepository.getInstance().getArchiveReader(archiveDataSource.getUrl());
                String[] names = reader.getNamesByPattern(archiveDataSource.getKey(), _pvName);
                if (names != null && names.length > 0) {
                    _infoText.append("\n" + archiveDataSource.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
        MessageDialog md = new MessageDialog(null, "PV Archive Info", null, _pvName + "\n" + CheckPvInArchivesJob.this._infoText.toString(), MessageDialog.INFORMATION, new String[] { "OK" }, 0);
        md.open();
            }
        });
        return Status.OK_STATUS;
    }
}
