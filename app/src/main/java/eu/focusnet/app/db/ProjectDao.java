package eu.focusnet.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import eu.focusnet.app.model.focus.Linker;
import eu.focusnet.app.model.focus.PageTemplate;
import eu.focusnet.app.model.focus.ProjectTemplate;
import eu.focusnet.app.model.focus.WidgetTemplate;
import eu.focusnet.app.util.Constant;

/**
 * Created by admin on 04.08.2015.
 */
public class ProjectDao {

    private String[] columnsToRetrieve = {Constant.ID, Constant.ITERATOR, Constant.TITLE, Constant.DESCRIPTION, Constant.ORDER, Constant.FK_APP_CONTENT_ID};

    private SQLiteDatabase database;

    public ProjectDao(SQLiteDatabase database){
        this.database = database;
    }

    public Long createProject(ProjectTemplate project, Long fkAppContentID){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.ID, project.getGuid());
        contentValues.put(Constant.ITERATOR, project.getIterator());
        contentValues.put(Constant.TITLE, project.getTitle());
        contentValues.put(Constant.DESCRIPTION, project.getDescription());
        contentValues.put(Constant.ORDER, project.getOrder());
        contentValues.put(Constant.FK_APP_CONTENT_ID, fkAppContentID);

        Long rawId = database.insert(Constant.DATABASE_TABLE_PROJECT, null, contentValues);

        if(rawId != -1) {
            String projectId = project.getGuid();

            ArrayList<WidgetTemplate> widgets = project.getWidgets();
            if (widgets != null) {
                WidgetDao widgetDao = new WidgetDao(database);
                for (WidgetTemplate w : widgets)
                    widgetDao.createWidget(w, projectId);
            }

            ArrayList<PageTemplate> pages = project.getPages();
            if (pages != null) {
                PageDao pageDao = new PageDao(database);
                for (PageTemplate p : pages) {
                    pageDao.createPage(p, projectId);
                }
            }

            LinkerDao linkerDao = new LinkerDao(database);
            ArrayList<Linker> dashboards = project.getDashboards();
            if (dashboards != null) {
                for (Linker l : dashboards)
                    linkerDao.createLinker(l, projectId, LinkerDao.LINKER_TYPE.DASHBOARD);
            }

            ArrayList<Linker> tools = project.getTools();
            if (tools != null) {
                for (Linker l : tools)
                    linkerDao.createLinker(l, projectId, LinkerDao.LINKER_TYPE.TOOL);
            }
        }

        return rawId;
    }

    public ProjectTemplate findProject(String projectId){
        String[] params = {projectId};
        ProjectTemplate project = null;

        Cursor cursor = database.query(Constant.DATABASE_TABLE_PROJECT, columnsToRetrieve, Constant.ID+"=?", params, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            project = getProject(cursor);

            WidgetDao widgetDao = new WidgetDao(database);
            project.setWidgets(widgetDao.findWidgetByProjectId(projectId));

            PageDao pageDao = new PageDao(database);
            project.setPages(pageDao.findPages(projectId));

            LinkerDao linkerDao = new LinkerDao(database);
            project.setDashboards(linkerDao.findLinkers(projectId, LinkerDao.LINKER_TYPE.DASHBOARD));
            project.setTools(linkerDao.findLinkers(projectId, LinkerDao.LINKER_TYPE.TOOL));

            //TODO NotificationDao
            //  project.setNotifications(notificationDao.findNotification(projectId)) ;

            cursor.close();
        }

        return project;
    }

    public ArrayList<ProjectTemplate> findAllProjects(String appContentId){
        String[] params = {appContentId};
        ArrayList<ProjectTemplate> projects = new ArrayList<>();
        Cursor cursor = database.query(Constant.DATABASE_TABLE_PROJECT, columnsToRetrieve, Constant.FK_APP_CONTENT_ID + "=?", params, null, null, null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ProjectTemplate project = getProject(cursor);
                    String projectId = project.getGuid();

                    WidgetDao widgetDao = new WidgetDao(database);
                    project.setWidgets(widgetDao.findWidgetByProjectId(projectId));

                    PageDao pageDao = new PageDao(database);
                    project.setPages(pageDao.findPages(projectId));

                    LinkerDao linkerDao = new LinkerDao(database);
                    project.setDashboards(linkerDao.findLinkers(projectId, LinkerDao.LINKER_TYPE.DASHBOARD));
                    project.setTools(linkerDao.findLinkers(projectId, LinkerDao.LINKER_TYPE.TOOL));
                    projects.add(project);
                    //TODO NotificationDao
                    //  project.setNotifications(notificationDao.findNotification(projectId)) ;
                } while (cursor.moveToNext());

                cursor.close();
            }
        }

        return projects;
    }


    public ArrayList<ProjectTemplate> findAllProjects(){
        ArrayList<ProjectTemplate> projects = new ArrayList<>();
        String selectAllProjectsQuery = "SELECT * FROM " + Constant.DATABASE_TABLE_PROJECT;
        Cursor cursor = database.rawQuery(selectAllProjectsQuery, null);
        if(cursor.moveToFirst()){
            do {
                ProjectTemplate project = getProject(cursor);
                String projectId = project.getGuid();
                WidgetDao widgetDao = new WidgetDao(database);
                project.setWidgets(widgetDao.findWidgetByProjectId(projectId));

                PageDao pageDao = new PageDao(database);
                project.setPages(pageDao.findPages(projectId));

                LinkerDao linkerDao = new LinkerDao(database);
                project.setDashboards(linkerDao.findLinkers(projectId, LinkerDao.LINKER_TYPE.DASHBOARD));
                project.setTools(linkerDao.findLinkers(projectId, LinkerDao.LINKER_TYPE.TOOL));

                //TODO NotificationDao
                //  project.setNotifications(notificationDao.findNotification(projectId)) ;
                projects.add(project);
            }
            while (cursor.moveToNext());
            cursor.close();
        }

        return projects;
    }


    public boolean deleteProject(String projectId){

        LinkerDao linkerDao = new LinkerDao(database);
        linkerDao.deleteLinker(projectId, LinkerDao.LINKER_TYPE.DASHBOARD);
        linkerDao.deleteLinker(projectId, LinkerDao.LINKER_TYPE.TOOL);

        PageDao pageDao = new PageDao(database);
        for(PageTemplate page : pageDao.findPages(projectId)) {
            pageDao.deletePage(page.getGuid());
        }

        WidgetDao widgetDao = new WidgetDao(database);
        for(WidgetTemplate widget : widgetDao.findWidgetByProjectId(projectId))
            widgetDao.deleteWidget(widget.getGuid());

        //TODO NotificationDao
        //  project.setNotifications(notificationDao.findNotification(projectId)) ;

        String[] params = {projectId};
        return database.delete(Constant.DATABASE_TABLE_PROJECT, Constant.ID+"=?", params) > 0;
    }


    //TODO update


    private ProjectTemplate getProject(Cursor cursor){
        ProjectTemplate project = new ProjectTemplate();
        project.setGuid(cursor.getString(cursor.getColumnIndex(Constant.ID)));
        project.setIterator(cursor.getString(cursor.getColumnIndex(Constant.ITERATOR)));
        project.setOrder(cursor.getInt(cursor.getColumnIndex(Constant.ORDER)));
        project.setTitle(cursor.getString(cursor.getColumnIndex(Constant.TITLE)));
        project.setDescription(cursor.getString(cursor.getColumnIndex(Constant.DESCRIPTION)));
        return project;
    }
}
