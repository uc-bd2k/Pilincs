package edu.uc.eh.datatypes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chojnasm on 7/31/15.
 */
public class ListWrapper implements Serializable {
    private final List<StringDouble> list;

    public ListWrapper(List<StringDouble> list) {
        this.list = list;
    }

    public List<StringDouble> getList() {
        return list;
    }


}
