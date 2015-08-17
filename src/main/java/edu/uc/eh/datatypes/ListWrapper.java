package edu.uc.eh.datatypes;

import java.io.Serializable;

/**
 * Created by chojnasm on 8/14/15.
 */
public class ListWrapper implements Serializable {

    private int[] list;

    public ListWrapper(int[] list) {
        this.list = list;
    }

    public int[] getList() {
        return list;
    }

    public void setList(int[] list) {
        this.list = list;
    }
}
