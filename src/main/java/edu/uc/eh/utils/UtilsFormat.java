package edu.uc.eh.utils;

import edu.uc.eh.datatypes.AssayType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.ServiceMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by chojnasm on 8/10/15.
 */


@Service
public class UtilsFormat {


    private final DatabaseLoader databaseLoader;

    @Autowired
    public UtilsFormat(DatabaseLoader databaseLoader) {
        this.databaseLoader = databaseLoader;
    }



//    public static String mapToJSON(HashMap<String, Double> vector) {
//
//        JSONArray ja = new JSONArray();
//
//        Iterator it = vector.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            JSONObject jo = new JSONObject();
//            jo.put("name",pair.getKey());
//            jo.put("value",pair.getValue());
//            ja.add(jo);
//        }
//        JSONObject mainObj = new JSONObject();
//        mainObj.put("data", ja);
//
//        return mainObj.toJSONString();
////        JSONObject json = new JSONObject();
////        json.putAll( vector );
////        return json.toString();
//    }

    public String buildProfileVector(double[] vector, boolean[] imputeVector, AssayType assayType) {
        List<String> referenceProfile = databaseLoader.getReferenceProfile(assayType);

        JSONArray ja = new JSONArray();
        for(int i = 0; i < vector.length ; i++){
            JSONObject jo = new JSONObject();
            jo.put("name",referenceProfile.get(i));
            jo.put("value",vector[i]);
            jo.put("imputed", imputeVector[i]);

            ja.add(jo);
        }
        JSONObject mainObj = new JSONObject();
        mainObj.put("data", ja);
        return mainObj.toJSONString();
    }
}
