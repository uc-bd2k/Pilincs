package edu.uc.eh.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by chojnasm on 7/17/15.
 */
public class NetworkUtils {
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
}
