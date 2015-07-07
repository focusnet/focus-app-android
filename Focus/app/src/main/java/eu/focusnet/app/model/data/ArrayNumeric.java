package eu.focusnet.app.model.data;

import eu.focusnet.app.common.FocusType;

/**
 * Created by admin on 07.07.2015.
 */
public class ArrayNumeric implements FocusType {

    private Type type = Type.array_numeric;
    private Long[] value;

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Long[] getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (Long[])value;
    }
}
