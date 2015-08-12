package edu.uc.eh.utils;

import com.google.gson.*;
import edu.uc.eh.datatypes.AnnotationNameValue;
import edu.uc.eh.datatypes.AssayType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by chojnasm on 7/17/15.
 */
public class UtilsParse {

    private static final Logger log = LoggerFactory.getLogger(UtilsParse.class);

    public static Integer parsePeptideNumber(String stepOne) {
        BufferedReader in = UtilsNetwork.downloadFile(stepOne);
        String line;
        Integer peptide = null;

        try {
            while ((line = in.readLine()) != null) {
                if(line.contains("PeptideId")){
                    try {
                        peptide = Integer.parseInt(line.split(":")[1].replace(",", "").trim());
                    }catch (Exception e){}
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return peptide;
    }


    public static int parseRunId(String url) {
        try {
            return Integer.parseInt(url.split("runId=")[1].split("&")[0]);
        }catch(Exception e){

            return 0;
        }

    }

    public static HashMap<String, Integer> parsePeptideNumbers(String stepOne, List<String> peptideIds) throws IOException {

        HashMap<String,Integer> output = new HashMap<>();
        BufferedReader in = UtilsNetwork.downloadFile(stepOne);
        String line;
        Integer peptideDB = null;
        String peptideString = null;

        try {
            while ((line = in.readLine()) != null) {
                if (line.contains("PeptideId")) {
                    try {
                        peptideDB = Integer.parseInt(line.split(":")[1].replace(",", "").trim());
                    } catch (Exception e) {
                        peptideDB = null;
                    }
                }
                if (line.contains("Value")){
                    try {
                        peptideString = line.split(":")[1].replace(",", "").replaceAll("\"","").trim();
                        if (!peptideIds.contains(peptideString))
                            peptideString = null;
                    } catch (Exception e) {
                        peptideString = null;
                    }
                }

                if(peptideDB != null && peptideString != null){
                    output.put(peptideString,peptideDB);
                    peptideDB = null;
                    peptideString = null;
                }
                }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    public static AssayType parseArrayTypeFromUrl(String url) {
         return url.contains("GCP") ? AssayType.GCP : AssayType.P100;
    }

    public static HashMap<String, List<String>> parseTags(String tags) throws ParseException {


        HashMap<String, List<String>> output = new HashMap<>();
        output.put("Pertiname",new ArrayList<String>());
        output.put("CellId",new ArrayList<String>());
        output.put("AssayTypes",new ArrayList<String>());
        output.put("PrGeneSymbol",new ArrayList<String>());

        JSONParser parser = new JSONParser();
        JSONArray array = (JSONArray)parser.parse(tags);

        Iterator<Object> iterator = array.iterator();

        while(iterator.hasNext()){
            JSONObject object = (JSONObject)iterator.next();
            if(object.get("p100")!=null){
                if(object.get("p100").toString().equals("true")) {
                    output.get("AssayTypes").add(AssayType.P100.name());
                }
            }else if(object.get("gcp")!=null){
                if(object.get("gcp").toString().equals("true")) {
                    output.get("AssayTypes").add(AssayType.GCP.name());
                }
            }else {

                String tagName = object.get("name").toString().replace("[", "").replace("]", "");
                String tagAnnotation = object.get("annotation").toString().replace("[", "").replace("]", "");
                output.get(tagAnnotation).add(tagName);
            }
        }

        return output;

    }

    public static String lastTag(String tags) throws ParseException {

        String lastTagAnnotation = null;

        JSONParser parser = new JSONParser();
        JSONArray array = (JSONArray)parser.parse(tags);

        Iterator<Object> iterator = array.iterator();

        while(iterator.hasNext()) {
            JSONObject object = (JSONObject) iterator.next();
            if (object.get("name") != null) {
                lastTagAnnotation = object.get("annotation").toString().replace("[", "").replace("]", "");
            }
        }
        return lastTagAnnotation;
    }

    public static List<String> getPeptideIdNamesFromJSON(String jsonUrl) throws Exception {
        String jsonAsString = UtilsNetwork.readUrl(jsonUrl);
        List<String> output = new ArrayList<>();

        JsonElement rootElement = new JsonParser().parse(jsonAsString);
        JsonObject jsonObject = rootElement.getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("rows");

        for(int i = 0; i < jsonArray.size(); i++){
            JsonObject jobject = jsonArray.get(i).getAsJsonObject();
            String name = jobject.get("Name").toString().replaceAll("\"","");
            String value = jobject.get("Value").toString().replaceAll("\"","");
            if(name.equals("pr_id") && !output.contains(value)){
                output.add(value);
            }
        }
        Collections.sort(output);
        return output;
    }
}
