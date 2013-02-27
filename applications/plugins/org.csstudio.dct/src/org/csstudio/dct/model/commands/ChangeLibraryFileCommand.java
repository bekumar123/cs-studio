package org.csstudio.dct.model.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.internal.Project;
import org.eclipse.gef.commands.Command;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.google.common.base.Optional;

/**
 * Undoable command that changes the database definition (dbd) reference of a
 * {@link IProject}.
 * 
 * @author Sven Wende
 * 
 */
public final class ChangeLibraryFileCommand extends Command {
    private IProject project;
    private String currentPath;
    private String oldPath;
    

    /**
     * Constructor.
     * @param project the project
     * @param path the path to the dbd file
     */
    public ChangeLibraryFileCommand(IProject project,  String path) {
        this.project = project;
        this.currentPath = path;
        this.oldPath = project.getDbdPath();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void execute() {
        setPath(currentPath);
        Optional<Document> noLib = Optional.absent();
        try {
            Project libraryProject =  DctActivator.getDefault().getPersistenceService().loadProject(getLibDoc(), noLib);
            project.addPrototypesToLibrary(libraryProject);
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    
    private Document getLibDoc() throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        return builder
                .build(new FileInputStream(
                        new File(
                                "/Users/roger/Documents/desy-git/cs-studio/applications/plugins/org.csstudio.dct/beispiel/simple.css-dct")));
    }
//    project = DctActivator.getDefault().getPersistenceService()
  //          .loadProject(doc, getLibraryDocument());
    
    /**
     *{@inheritDoc}
     */
    @Override
    public void undo() {
        setPath(oldPath);
    }

    private void setPath(String path) {
        project.setLibraryPath(path);
    }

}
