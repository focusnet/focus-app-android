package eu.focusnet.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import eu.focusnet.app.model.data.Page;
import eu.focusnet.app.model.data.WidgetLinker;
import eu.focusnet.app.util.Constant;

/**
 * Created by admin on 04.08.2015.
 */
public class PageDao {

    private String[] columnsToRetrieve = {Constant.ID, Constant.TITLE, Constant.DESCRIPTION, Constant.FK_PROJECT_ID};

    private SQLiteDatabase database;

    public PageDao(SQLiteDatabase database){
        this.database = database;
    }

    public Long createPage(Page page, String fkProjectId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.ID, page.getGuid());
        contentValues.put(Constant.TITLE, page.getTitle());
        contentValues.put(Constant.DESCRIPTION, page.getDescription());
        contentValues.put(Constant.FK_PROJECT_ID, fkProjectId);
        Long rawId = database.insert(Constant.DATABASE_TABLE_PAGE, null, contentValues);

        if(rawId !=-1) {
            WidgetLinkerDao widgetLinkerDao = new WidgetLinkerDao(database);
            ArrayList<WidgetLinker> widgetLinkers = page.getWidgets();
            if (widgetLinkers != null) {
                for (WidgetLinker wl : widgetLinkers)
                    widgetLinkerDao.createWidgetLinker(wl, page.getGuid());
            }
        }

        return rawId;
    }

    public Page findPage(String pageId){
        String[] params = {String.valueOf(pageId)};
        Page page = null;

        Cursor cursor = database.query(Constant.DATABASE_TABLE_PAGE, columnsToRetrieve, Constant.ID + "=?", params, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
            page = getPage(cursor);
            WidgetLinkerDao widgetLinkerDao = new WidgetLinkerDao(database);
            page.setWidgets(widgetLinkerDao.findWidgetLinker(page.getGuid()));
            cursor.close();
        }

        return page;
    }

    public ArrayList<Page> findPages(String fkProjectId){
        ArrayList<Page> pages = new ArrayList<>();
        String[] params = {fkProjectId};
        Cursor cursor = database.query(Constant.DATABASE_TABLE_PAGE, columnsToRetrieve, Constant.FK_PROJECT_ID+"=?", params, null, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    Page page = getPage(cursor);
                    WidgetLinkerDao widgetLinkerDao = new WidgetLinkerDao(database);
                    page.setWidgets(widgetLinkerDao.findWidgetLinker(page.getGuid()));
                    pages.add(page);

                }while (cursor.moveToNext());
            }
            cursor.close();
        }

        return pages;
    }

    public boolean deletePage(String pageId){
        String[] params = {pageId};
        WidgetLinkerDao widgetLinkerDao = new WidgetLinkerDao(database);
        widgetLinkerDao.deleteWidgetLinker(pageId);
        return database.delete(Constant.DATABASE_TABLE_PAGE, Constant.ID+"=?", params) > 0;
    }

    //TODO update

    private Page getPage(Cursor cursor){
        Page page = new Page();
        page.setGuid(cursor.getString(cursor.getColumnIndex(Constant.ID)));
        page.setTitle(cursor.getString(cursor.getColumnIndex(Constant.TITLE)));
        page.setDescription(cursor.getString(cursor.getColumnIndex(Constant.DESCRIPTION)));
        return page;
    }
}
