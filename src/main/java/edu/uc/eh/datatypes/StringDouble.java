package edu.uc.eh.datatypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

/**
 * Created by chojnasm on 8/6/15.
 */
public class StringDouble implements Serializable, Comparable{

    private String string;
    private Double aDouble;
            private static final Logger log = LoggerFactory.getLogger(StringDouble.class);

    public StringDouble(String string, Double aDouble) {
        this.string = string;
        this.aDouble = aDouble;
    }



    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Double getaDouble() {
        return aDouble;
    }

    public void setaDouble(Double aDouble) {
        this.aDouble = aDouble;
    }

    @Override
    public int compareTo(Object o2) {
        StringDouble sd2 = (StringDouble)o2;
            return this.getaDouble().compareTo(sd2.getaDouble());

    }
}
