package edu.uc.eh.utils;


import edu.uc.eh.datatypes.AnnotationNameValue;
import edu.uc.eh.domain.*;
import edu.uc.eh.datatypes.AssayType;
import org.json.simple.JSONObject;
import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.query.SelectRowsCommand;
import org.labkey.remoteapi.query.SelectRowsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by chojnasm on 7/13/15.
 */

@Service
public class ConnectPanorama {

    private static final Logger log = LoggerFactory.getLogger(ConnectPanorama.class);

    @Value("${panorama.folders}")
    private String panoramaFolders;// = "LINCS/P100,LINCS/GCP";

    @Value("${panorama.runIdUrl}")
    private String runIdUrl;//="https://panoramaweb.org/labkey/targetedms/LINCS/%s/showPrecursorList.view?id=%d";

    @Value("${panorama.gctDownloadUrl}")
    private String gctDownloadUrl;

    @Value("${panorama.connectionUrl}")
    private String panoramaConnectionUrl;

    @Value("${panorama.peptideInternalIdUrl}")
    private String peptideInternalIdsUrl;

    @Value("${panorama.chromatogramsUrl}")
    private String chromatogramsUrl;

    @Value("${panorama.peptideAnnotationsUrl}")
    private String peptideAnnotationsUrl;

    public List<String> gctDownloadUrls(boolean ifProcessed) throws IOException, CommandException {

        List<String> output = new ArrayList<>();
        String[] folderNames = panoramaFolders.split(",");
        String gcpOrP100;

        for(String folderName:folderNames) {
            for (Integer runId : getRunIdsFromDatabase(folderName)) {
                gcpOrP100 = folderName.contains("GCP") ? "GCP" : "P100";

                output.add(String.format(gctDownloadUrl,gcpOrP100,runId,gcpOrP100,ifProcessed));
            }
        }
        return output;
    }



    private List<Integer> getRunIdsFromDatabase(String folderName) throws IOException, CommandException {
        ArrayList<Integer> runIds = new ArrayList<Integer>();
        Connection cn = new Connection(panoramaConnectionUrl);

        SelectRowsCommand cmd = new SelectRowsCommand("targetedms", "runs");
        cmd.getColumns().addAll(Arrays.asList("Id", "Description"));

        SelectRowsResponse response = cmd.execute(cn, folderName);
        List<Map<String, Object>> rows = response.getRows();

        for (Map<String, Object> row : rows) {
            if(row.get("Description").toString().contains("QC"))continue; // skip Quality Control files
            runIds.add((Integer) row.get("Id"));
        }
        return runIds;
    }

//    public String getChromatogramsUrl(PeakArea peakArea) {
//
//        AssayType assayType = peakArea.getGctFile().getAssayType();
//        int runId = peakArea.getGctFile().getRunId();
//        String escapedPeptideId = peakArea.getPeptideAnnotation().escapedPeptideId();
//        String replicateId = peakArea.getReplicateAnnotation().getReplicateId();
//
//        String stepOne = String.format(intermediateLink, assayType, escapedPeptideId, runId);
//        log.warn(stepOne);
//
//        Integer peptide = UtilsParse.parsePeptideNumber(stepOne);
//
//        if(peptide!=null){
//            return String.format(detailedLink,assayType,peptide,replicateId);
//        }
//            return null;
//    }

    public HashMap<String, Integer> getPeptideIdsFromJSON(List<String> peptideIds, AssayType assayType, int runId) throws IOException {

        HashMap<String,Integer> output;

        StringBuilder sb = new StringBuilder();

        for(String peptideId : peptideIds){
            String escapedPeptideId = peptideId.replaceAll("\\+", "%2B");
            if(sb.length()==0){
                sb.append(escapedPeptideId);
            }else{
                sb.append(";").append(escapedPeptideId);
            }
        }

        String stepOne = String.format(peptideInternalIdsUrl, assayType, sb.toString(), runId);

        output= UtilsParse.parsePeptideNumbers(stepOne, peptideIds);

        return output;
    }

    public String getRunIdLink(GctFile gctFile){

        AssayType assayType = gctFile.getAssayType();
        int runId = gctFile.getRunId();

        return String.format(runIdUrl,assayType,runId);
    }

    public String getChromatogramsUrl(AssayType assayType, Integer peptide, String replicateId) {
        return String.format(chromatogramsUrl,assayType,peptide,replicateId);
    }

    public List<String> getPeptideReferenceIdNames(AssayType assayType) throws Exception {
        String jsonUrl = String.format(peptideAnnotationsUrl, assayType);

        return UtilsParse.getPeptideIdNamesFromJSON(jsonUrl);
    }
}
