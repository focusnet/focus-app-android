package eu.focusnet.app.model.data;

/**
 * Created by admin on 03.08.2015.
 */
public class WidgetLinker {

    private String widgetid;
    private int order;
    private String layout; //TODO

    public WidgetLinker(String widgetid, int order, String layout) {
        this.widgetid = widgetid;
        this.order = order;
        this.layout = layout;
    }

    public WidgetLinker() {}

    public String getWidgetid() {
        return widgetid;
    }

    public void setWidgetid(String widgetid) {
        this.widgetid = widgetid;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }
}
