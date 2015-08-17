package edu.uc.eh.utils;

import edu.uc.eh.datatypes.PeptideOrder;
import edu.uc.eh.datatypes.StringDouble;
import edu.uc.eh.domain.Profile;
import edu.uc.eh.domain.json.HeatMapResponse;
import edu.uc.eh.domain.json.MatrixRow;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by chojnasm on 8/6/15.
 */
public class UtilsTransform {

    public static String truncate(String text, int number){
        if(text.length() > number){
            return text.substring(0,number)+"..";
        }else{
            return text;
        }
    }
    public static String SortedSetToHTML(SortedSet<StringDouble> peptides, boolean orderAsc) {
        DecimalFormat df = new DecimalFormat("0.0000");
        StringBuilder sb = null;

        if(orderAsc){
            sb = new StringBuilder("<div class=\"col-md-4\">");
            for(StringDouble sd : peptides){
                sb.append(df.format(sd.getaDouble())).append("<span class=\"tab-space\">").append(truncate(sd.getString(),37)).append("</span><br/>");
            }
            sb.append("</div>");
        }else{
            sb = new StringBuilder("<br/><div class=\"col-md-4\">");
            Object[] array = peptides.toArray();

            for(int i = array.length-1;i >= 0; i--){
                sb.append(df.format(((StringDouble)array[i]).getaDouble())).append("<span class=\"tab-space\">").append(truncate(((StringDouble) array[i]).getString(), 37)).append("</span><br/>");
            }
            sb.append("</div>");
        }

        return sb.toString();
    }

    public static HeatMapResponse profilesToHeatMap(List<Profile> profiles, List<String> profileNames) {

        List<PeptideOrder> peptideOrders = new ArrayList<>();
        List<MatrixRow> rows = new ArrayList<>();

        int rowIndex = 0;


        List<Integer> ordersToRerank = new ArrayList<>();
        for (Profile profile : profiles) {
            ordersToRerank.add(profile.getClusteringOrder());
        }

        HashMap<Integer, Integer> reranking = rerank(ordersToRerank);

        for( Profile profile : profiles){

            String replicateName = profile.getReplicateAnnotation().getReplicateId();
            Integer newOrder = reranking.get(profile.getClusteringOrder());

            peptideOrders.add(
                    new PeptideOrder(replicateName, newOrder));
            rows.add(new MatrixRow(rowIndex, newOrder, profile.getColors()));
            rowIndex++;
        }
        return new HeatMapResponse(peptideOrders, profileNames, rows);
    }

    public static HashMap<Integer, Integer> rerank(List<Integer> inputOrder) {

        HashMap<Integer, Integer> output = new HashMap<>();

        Collections.sort(inputOrder);
        for (int i = 0; i < inputOrder.size(); i++) {
            output.put(inputOrder.get(i), i);
        }
        return output;
    }

    public static double[] intArrayToDouble(int[] input) {
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }
}
