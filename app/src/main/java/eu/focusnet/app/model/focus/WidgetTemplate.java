package eu.focusnet.app.model.focus;

import java.util.Map;

/**
 * Created by admin on 03.08.2015.
 */
public class WidgetTemplate
{

    private String guid,
                   type;
    private Map<String, String> params;

    public WidgetTemplate(String guid, String type, Map<String, String> params) {
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

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
