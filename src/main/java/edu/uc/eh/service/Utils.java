package edu.uc.eh.service;

import edu.uc.eh.domain.AssayType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by chojnasm on 7/17/15.
 */
public class Utils {
    public static Integer parsePeptideNumber(String stepOne) {
        BufferedReader in = downloadFile(stepOne);
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


    public static BufferedReader downloadFile(String link){
        URL url;
        BufferedReader in = null;
        try {
            url = new URL(link);
            in = new BufferedReader(new InputStreamReader(url.openStream()));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
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
        BufferedReader in = downloadFile(stepOne);
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

    public static Collection<String> parseTags(String tags) throws ParseException {


        Collection<String> output = new ArrayList<>();
        if(tags == null || tags.length() == 0) return output;

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(tags);
        JSONArray array = (JSONArray)obj;
        Iterator<Object> iter = array.iterator();
        while(iter.hasNext()){
            JSONObject object = (JSONObject)iter.next();
            output.add(object.get("name").toString().replace("[","").replace("]",""));
        }

        return output;

    }
}
