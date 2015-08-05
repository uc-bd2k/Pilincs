package edu.uc.eh.utils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chojnasm on 7/31/15.
 */
public class ListWrapper implements Serializable {
    private final List<Tuples.Tuple2<String,Double>> list;

    public ListWrapper(List<Tuples.Tuple2<String,Double>> list) {
        this.list = list;
    }

    public List<Tuples.Tuple2<String,Double>> getList() {
        return list;
    }


}
