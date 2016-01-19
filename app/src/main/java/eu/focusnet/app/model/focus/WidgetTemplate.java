package eu.focusnet.app.model.focus;

import java.util.Map;

/**
 * Created by admin on 03.08.2015.
 */
public class WidgetTemplate
{

    private String guid,
                   type;
    private FocusSampleDataMap params;

    public WidgetTemplate(String guid, String type, FocusSampleDataMap params) {
        this.guid = guid;
        this.type = type;
        this.params = params;
    }

    public WidgetTemplate() {}

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FocusSampleDataMap getParams() {
        return params;
    }

    public void setParams(FocusSampleDataMap params) {
        this.params = params;
    }
}
