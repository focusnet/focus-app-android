package eu.focusnet.app.common;

/**
 * Created by admin on 07.07.2015.
 */
public interface FocusType {

        public enum Type {numeric, sting, array_string, array_numeric }

        public Type getType();

        public Object getValue();

        public void setValue(Object value) ;

}