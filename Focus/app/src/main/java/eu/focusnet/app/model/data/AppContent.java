package eu.focusnet.app.model.data;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by admin on 03.08.2015.
 */
public class AppContent extends FocusObject{

    private Long id;
    private ArrayList<Project> projects;

    public AppContent(String type, String url, String owner, String editor, int version, Date creationDateTime, Date editionDateTime, boolean active, ArrayList<Project> projects) {
        super(type, url, owner, editor, version, creationDateTime, editionDateTime, active);
        this.projects = projects;
    }

    public AppContent(Long id, ArrayList<Project> projects) {
        this.id = id;
        this.projects = projects;
    }

    public AppContent(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArrayList<Project> getProjects() {
        return projects;
    }

    public void setProjects(ArrayList<Project> projects) {
        this.projects = projects;
    }
}
