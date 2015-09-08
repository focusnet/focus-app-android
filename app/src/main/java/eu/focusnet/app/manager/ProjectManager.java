package eu.focusnet.app.manager;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import eu.focusnet.app.db.ProjectDao;
import eu.focusnet.app.model.data.Project;

/**
 * Created by admin on 08.09.2015.
 */
public class ProjectManager {

    private ProjectDao projectDao;

    public ProjectManager(SQLiteDatabase database){
        projectDao = new ProjectDao(database);
    }

    public ArrayList<Project> getAllProjects(){
        return projectDao.findAllProjects();
    }

    public Long createProject(Project project, Long fkAppContentID){
        return projectDao.createProject(project, fkAppContentID);
    }
}
