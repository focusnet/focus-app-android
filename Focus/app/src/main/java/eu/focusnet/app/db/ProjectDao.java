package eu.focusnet.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import eu.focusnet.app.model.data.Linker;
import eu.focusnet.app.model.data.Page;
import eu.focusnet.app.model.data.Project;
import eu.focusnet.app.model.data.Widget;
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

    public Long createProject(Project project, Long fkAppContentID){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.ID, project.getGuid());
        contentValues.put(Constant.ITERATOR, project.getIterator());
        contentValues.put(Constant.TITLE, project.getTitle());
        contentValues.put(Constant.DESCRIPTION, project.getDescription());

        contentValues.put(Constant.ORDER, project.getOrder());
        contentValues.put(Constant.FK_APP_CONTENT_ID, fkAppContentID);

        return database.insert(Constant.DATABASE_TABLE_PROJECT, null, contentValues);
    }

    public Project findProject(String projectId){
        String[] params = {projectId};
        Project project = new Project();

        Cursor cursor = database.query(Constant.DATABASE_TABLE_PROJECT, columnsToRetrieve, Constant.ID+"=?", params, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            project = getProject(cursor);

            WidgetDao widgetDao = new WidgetDao(database);
            project.setWidgets(widgetDao.findWidget(projectId));

            PageDao pageDao = new PageDao(database);
            project.setPages(pageDao.findPages(projectId));

            LinkerDao linkerDao = new LinkerDao(database);
            project.setDashboards(linkerDao.findLinkers(projectId, LinkerDao.LINKER_TYPE.DASHBOARD));
            project.setTools(linkerDao.findLinkers(projectId, LinkerDao.LINKER_TYPE.TOOL));

            cursor.close();
        }

        return project;
    }


    public ArrayList<Project> findAllProjects(){

        ArrayList<Project> projects = new ArrayList<>();

        String selectAllProjectsQuery = "SELECT * FROM " + Constant.DATABASE_TABLE_PROJECT;

        Cursor cursor = database.rawQuery(selectAllProjectsQuery, null);
        if(cursor.moveToFirst()){
            do {
                Project project = getProject(cursor);
                String projectId = project.getGuid();
                WidgetDao widgetDao = new WidgetDao(database);
                project.setWidgets(widgetDao.findWidget(projectId));

                PageDao pageDao = new PageDao(database);
                project.setPages(pageDao.findPages(projectId));

                LinkerDao linkerDao = new LinkerDao(database);
                project.setDashboards(linkerDao.findLinkers(projectId, LinkerDao.LINKER_TYPE.DASHBOARD));
                project.setDashboards(linkerDao.findLinkers(projectId, LinkerDao.LINKER_TYPE.TOOL));
                projects.add(project);
            }
            while (cursor.moveToNext());

            cursor.close();
        }

        return projects;
    }

    public boolean deleteProject(Long projectId){
        return database.delete(Constant.DATABASE_TABLE_PROJECT, Constant.ID+"="+projectId, null) > 0;
    }

    //TODO update


    private Project getProject(Cursor cursor){
        Project project = new Project();
        project.setGuid(cursor.getString(cursor.getColumnIndex(Constant.ID)));
        project.setIterator(cursor.getString(cursor.getColumnIndex(Constant.ITERATOR)));
        project.setOrder(cursor.getInt(cursor.getColumnIndex(Constant.ORDER)));
        project.setTitle(cursor.getString(cursor.getColumnIndex(Constant.TITLE)));
        project.setDescription(cursor.getString(cursor.getColumnIndex(Constant.DESCRIPTION)));
        return project;
    }
}
