package eu.focusnet.app.model.focus;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 03.08.2015.
 */
public class ProjectTemplate implements Serializable {

    private String guid;
    private String iterator; //TODO define this object
    private String title;
    private String description;

    private int order;

    private ArrayList<Widget> widgets;
    private ArrayList<PageTemplate> pages;

    private ArrayList<Linker> dashboards;
    private ArrayList<Linker> tools;

    private ArrayList<Notification> notifications;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getIterator() {
        return iterator;
    }

    public void setIterator(String iterator) {
        this.iterator = iterator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public ArrayList<Widget> getWidgets() {
        return widgets;
    }

    public void setWidgets(ArrayList<Widget> widgets) {
        this.widgets = widgets;
    }

    public ArrayList<PageTemplate> getPages() {
        return pages;
    }

    public void setPages(ArrayList<PageTemplate> pages) {
        this.pages = pages;
    }

    public ArrayList<Linker> getDashboards() {
        return dashboards;
    }

    public void setDashboards(ArrayList<Linker> dashboards) {
        this.dashboards = dashboards;
    }

    public ArrayList<Linker> getTools() {
        return tools;
    }

    public void setTools(ArrayList<Linker> tools) {
        this.tools = tools;
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }
}
