package edu.uc.eh.utils;

import edu.uc.eh.datatypes.AssayType;
import edu.uc.eh.datatypes.Int4Tuple;
import edu.uc.eh.datatypes.PeptideOrder;
import edu.uc.eh.datatypes.StringDouble;
import edu.uc.eh.domain.PeptideAnnotation;
import edu.uc.eh.domain.Profile;
import edu.uc.eh.domain.ReplicateAnnotation;
import edu.uc.eh.domain.json.ExploreResponse;
import edu.uc.eh.domain.json.HeatMapResponse;
import edu.uc.eh.domain.json.MatrixRow;
import edu.uc.eh.domain.repository.PeptideAnnotationRepository;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by chojnasm on 8/6/15.
 */
public class UtilsTransform {

    public static String truncate(String text, int number) {
        if (text.length() > number) {
            return text.substring(0, number) + "..";
        } else {
            return text;
        }
    }

    public static String SortedSetToHTML(SortedSet<StringDouble> peptides, boolean orderAsc) {
        DecimalFormat df = new DecimalFormat("0.0000");
        StringBuilder sb = null;

        if (orderAsc) {
            sb = new StringBuilder("<div class=\"col-md-4\">");
            for (StringDouble sd : peptides) {
                sb.append(df.format(sd.getaDouble())).append("<span class=\"tab-space\">").append(truncate(sd.getString(), 37)).append("</span><br/>");
            }
            sb.append("</div>");
        } else {
            sb = new StringBuilder("<br/><div class=\"col-md-4\">");
            Object[] array = peptides.toArray();

            for (int i = array.length - 1; i >= 0; i--) {
                sb.append(df.format(((StringDouble) array[i]).getaDouble())).append("<span class=\"tab-space\">").append(truncate(((StringDouble) array[i]).getString(), 37)).append("</span><br/>");
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

        for (Profile profile : profiles) {

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


    /**
     * Returned GCT file contains replicates in rows and peptides in columns. It is a transposition compared to
     * GCT files in Panorama.
     *
     * @param assayType
     * @param profiles
     * @param databaseLoader
     * @param peptideAnnotationRepository
     * @param ifTransponse
     * @return
     */
    public static String profilesToGct(AssayType assayType,
                                       List<Profile> profiles,
                                       DatabaseLoader databaseLoader,
                                       PeptideAnnotationRepository peptideAnnotationRepository, boolean ifTransponse) {


        List<String> peptideIds = databaseLoader.getReferenceProfile(assayType);
        List<String> replicateLabels = ReplicateAnnotation.getAnnotationLabels();
        List<String> peptideLabels = PeptideAnnotation.getAnnotationLabels(assayType);
        List<List<String>> peptideAnnotations = new ArrayList<>(); // order as in peptideIds

        for (String peptideId : peptideIds) {
            PeptideAnnotation peptideAnnotation = peptideAnnotationRepository.findFirstByPeptideId(peptideId);
            peptideAnnotations.add(peptideAnnotation.getAnnotationsForGct(assayType));
        }


        StringBuilder stringBuilder = new StringBuilder("#1.3\n");

        int numReplicates = profiles.size();
        int numPeptides = peptideIds.size();
        int numReplicateAnnotations = replicateLabels.size();
        int numPeptideAnnotations = peptideLabels.size();

        // Counts
        stringBuilder.append(numReplicates).append("\t");
        stringBuilder.append(numPeptides).append("\t");
        stringBuilder.append(numReplicateAnnotations).append("\t");
        stringBuilder.append(numPeptideAnnotations).append("\n");

        // Replicate Labels
        stringBuilder.append("id").append("\t");

        for (String label : replicateLabels) {
            stringBuilder.append(label).append("\t");
        }

        // Peptide Ids
        int counter = 0;
        for (String peptideId : peptideIds) {
            stringBuilder.append(peptideId);
            if (counter++ == peptideIds.size() - 1) {
                stringBuilder.append("\n");
            } else {
                stringBuilder.append("\t");
            }
        }

        // Peptide Labels + nulls + peptide annotations
        for (int i = 0; i < peptideLabels.size(); i++) {
            // Peptide label
            stringBuilder.append(peptideLabels.get(i)).append("\t");

            // nulls for replicate annotations
            for (int j = 0; j < replicateLabels.size(); j++) {
                stringBuilder.append("\t");
            }

            // Peptide annotations
            for (int j = 0; j < peptideAnnotations.size(); j++) {

                stringBuilder.append(peptideAnnotations.get(j).get(i));

                if (j == peptideAnnotations.size() - 1) {
                    stringBuilder.append("\n");
                } else {
                    stringBuilder.append("\t");
                }
            }
        }


        for (Profile profile : profiles) {

            // Replicate annotations
            counter = 0;
            for (String annotation : profile.getReplicateAnnotation().getAnnotationsForGct()) {

                if (profile.getRunId() == 2107 && counter == 0) annotation += "_PRM";
                if (annotation == null) annotation = "NA";

                stringBuilder.append(annotation).append("\t");

                counter++;
            }

            // Values
            counter = 0;
            for (double value : profile.getListWrapper().getDoubles()) {
                stringBuilder.append(value);
                if (counter++ == profile.getListWrapper().getDoubles().length - 1) {
                    stringBuilder.append("\n");
                } else {
                    stringBuilder.append("\t");
                }
            }
        }

        if (ifTransponse) {
            return transponseGct(stringBuilder.toString());
        } else {
            return stringBuilder.toString();
        }

    }

    private static String transponseGct(String gct) {

        String[][] matrix = null; //rows by columns
        int[] dims = new int[4];

        String[] rows = gct.split("\n");

        for (int rowId = 0; rowId < rows.length; rowId++) {

            if (rowId == 0) {
                continue;

            } else if (rowId == 1) {

                String[] dimensionsString = rows[rowId].split("\t");
                dims[0] = Integer.valueOf(dimensionsString[0]);
                dims[1] = Integer.valueOf(dimensionsString[1]);
                dims[2] = Integer.valueOf(dimensionsString[2]);
                dims[3] = Integer.valueOf(dimensionsString[3]);

                matrix = new String[dims[0] + dims[3] + 1][dims[1] + dims[2] + 1];
                continue;
            }

            String[] cols = rows[rowId].split("\t");

            for (int colId = 0; colId < cols.length; colId++) {
                matrix[rowId - 2][colId] = cols[colId];
            }
        }

        StringBuilder sb = new StringBuilder("#1.3\n");

        sb.append(dims[1]).append("\t");
        sb.append(dims[0]).append("\t");
        sb.append(dims[3]).append("\t");
        sb.append(dims[2]).append("\n");

        for (int i = 0; i < dims[1] + dims[2] + 1; i++) {
            for (int j = 0; j < dims[0] + dims[3] + 1; j++) {
                sb.append(matrix[j][i]);

                if (j == dims[0] + dims[3]) {
                    sb.append("\n");
                } else {
                    sb.append("\t");
                }
            }
        }

        return sb.toString();
    }

    public static ExploreResponse profilesToExplore(List<Profile> profiles) {

        List<String> cellNames = new ArrayList<>();
        List<String> pertNames = new ArrayList<>();
        List<String> doseNames = new ArrayList<>();
        List<String> timeNames = new ArrayList<>();
        List<Int4Tuple> rows = new ArrayList<>();


        for (Profile profile : profiles) {

            String cellName = profile.getReplicateAnnotation().getCellId();
            String pertName = profile.getReplicateAnnotation().getPertiname();
            String doseName = profile.getReplicateAnnotation().getPertDose();
            String timeName = profile.getReplicateAnnotation().getPertTime();

            if (!cellNames.contains(cellName)) {
                cellNames.add(cellName);
            }

            if (!pertNames.contains(pertName)) {
                pertNames.add(pertName);
            }

            if (!doseNames.contains(doseName)) {
                doseNames.add(doseName);
            }

            if (!timeNames.contains(timeName)) {
                timeNames.add(timeName);
            }

            int cellNameId = cellNames.indexOf(cellName);
            int pertNameId = pertNames.indexOf(pertName);
            int doseNameId = doseNames.indexOf(doseName);
            int timeNameId = timeNames.indexOf(timeName);

            rows.add(new Int4Tuple(cellNameId, pertNameId, doseNameId, timeNameId));
        }
        return new ExploreResponse(cellNames, pertNames, doseNames, timeNames, rows);
    }
}
