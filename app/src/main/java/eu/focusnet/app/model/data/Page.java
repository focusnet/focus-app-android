package eu.focusnet.app.model.data;

import java.util.ArrayList;

/**
 * Created by admin on 03.08.2015.
 */
public class Page {

    private String guid,
                    title,
                    description;

    private ArrayList<WidgetLinker> widgets;

    public Page(String guid, String title, String description, ArrayList<WidgetLinker> widgets) {
        this.guid = guid;
        this.title = title;
        this.description = description;
        this.widgets = widgets;
    }

    public Page() {}

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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

    public ArrayList<WidgetLinker> getWidgets() {
        return widgets;
    }

    public void setWidgets(ArrayList<WidgetLinker> widgets) {
        this.widgets = widgets;
    }
}
