package eu.focusnet.app.model.data;

/**
 * Created by yandypiedra on 27.10.15.
 */
public class FocusSample {

    public enum Type {numeric, string, array_string, array_numeric }

    private String property;
    private Type type;
    public Object value;

    public FocusSample() {}

    public FocusSample(String property, Type type, Object value) {
        this.property = property;
        this.type = type;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
