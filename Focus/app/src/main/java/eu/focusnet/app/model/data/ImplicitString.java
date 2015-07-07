package eu.focusnet.app.model.data;

import eu.focusnet.app.common.FocusType;

/**
 * Created by admin on 07.07.2015.
 */
public class ImplicitString implements FocusType {

    private Type type = Type.sting;
    private String value;

    public ImplicitString(String value) {
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = (String)value;
    }
}
