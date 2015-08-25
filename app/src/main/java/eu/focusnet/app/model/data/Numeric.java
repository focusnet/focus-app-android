package eu.focusnet.app.model.data;


import eu.focusnet.app.common.FocusType;

/**
 * Created by admin on 07.07.2015.
 */
public class Numeric implements FocusType {

    private Type type = Type.numeric;
    private Long value;

    public Numeric(Long value) {
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = (Long) value;
    }
}
