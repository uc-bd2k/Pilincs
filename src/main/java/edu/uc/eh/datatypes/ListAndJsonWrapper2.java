package edu.uc.eh.datatypes;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chojnasm on 7/31/15.
 */
public class ListAndJsonWrapper2 implements Serializable {
    private final double[] list;
    private final String json;

    public ListAndJsonWrapper2(double[] vector, boolean[] imputes, List<String> referenceProfile) {

        this.list = vector;
//        List<String> referenceProfile = databaseLoader.getReferenceProfile(assayType);

        JSONArray ja = new JSONArray();
        for (int i = 0; i < vector.length; i++) {
            JSONObject jo = new JSONObject();
            jo.put("name", referenceProfile.get(i));
            jo.put("value", vector[i]);
            jo.put("imputed", imputes[i]);

            ja.add(jo);
        }
        JSONObject mainObj = new JSONObject();
        mainObj.put("data", ja);
        this.json = mainObj.toJSONString();

        boolean x = false;
    }

    public double[] getList() {
        return list;
    }

    public double[] getDoubles() {
        return list;
    }

    public String getJSON() {
        return json;
    }
}
