package eu.focusnet.app.common;

import java.io.Serializable;

/**
 * Created by admin on 07.07.2015.
 */
public interface FocusType extends Serializable{

        public enum Type {numeric, string, array_string, array_numeric }

        public Type getType();

        public Object getValue();

        public void setValue(Object value) ;

}