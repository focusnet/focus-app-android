package eu.focusnet.app.model.data;

import eu.focusnet.app.common.FocusType;

/**
 * Created by admin on 07.07.2015.
 */
public class ArrayString implements FocusType {

    private Type type = Type.array_string;
    private String[] value;

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String[] getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (String[])value;
    }
}
