package edu.uc.eh.utils;

import edu.uc.eh.DatabaseLoader;
import edu.uc.eh.datatypes.*;
import edu.uc.eh.domain.*;
import edu.uc.eh.domain.json.ExploreResponse;
import edu.uc.eh.domain.json.HeatMapResponse;
import edu.uc.eh.domain.json.MatrixRow;
import edu.uc.eh.domain.repository.PeptideAnnotationRepository;
import edu.uc.eh.normalize.Normalizer;

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

        List<String> assayNames = new ArrayList<>();
        List<String> cellNames = new ArrayList<>();
        List<String> pertNames = new ArrayList<>();
        List<String> doseNames = new ArrayList<>();
        List<String> timeNames = new ArrayList<>();
        List<Int5Tuple> rows = new ArrayList<>();


        for (Profile profile : profiles) {

            String assayName = profile.getAssayType().toString();
            String cellName = profile.getReplicateAnnotation().getCellId();
            String pertName = profile.getReplicateAnnotation().getPertiname();
            String doseName = profile.getReplicateAnnotation().getPertDose();
            String timeName = profile.getReplicateAnnotation().getPertTime();

            if (!assayNames.contains(assayName)) {
                assayNames.add(assayName);
            }

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

            int assayNameId = assayNames.indexOf(assayName);
            int cellNameId = cellNames.indexOf(cellName);
            int pertNameId = pertNames.indexOf(pertName);
            int doseNameId = doseNames.indexOf(doseName);
            int timeNameId = timeNames.indexOf(timeName);

            rows.add(new Int5Tuple(assayNameId, cellNameId, pertNameId, doseNameId, timeNameId));
        }
        return new ExploreResponse(assayNames, cellNames, pertNames, doseNames, timeNames, rows);
    }

    public static MergedProfile mergeProfiles(List<Profile> bunchOfProfiles, int p100Length, int gcpLength) {

        List<ChartSeries> chartSeries = new ArrayList<>();
        String nTuple = null;
        HashMap<String, List<Profile>> seriesMap = new HashMap<>();
        ReplicateAnnotation replicateAnnotation = null;
        GctFile gctFile = null;

        for (Profile profile : bunchOfProfiles) {
            if (nTuple == null || replicateAnnotation == null || gctFile == null) {
                nTuple = profile.getReplicateAnnotation().getCellId() + " - "
                        + profile.getReplicateAnnotation().getPertiname();
                replicateAnnotation = profile.getReplicateAnnotation();
                gctFile = profile.getGctFile();
            }

            String seriesKey = profile.getAssayType().toString()
                    + profile.getRunId()
                    + profile.getReplicateAnnotation().getPertDose()
                    + profile.getReplicateAnnotation().getPertTime();

            if (!seriesMap.containsKey(seriesKey)) {
                seriesMap.put(seriesKey, new ArrayList<>());
            }

            List<Profile> updatedSeries = seriesMap.get(seriesKey);
            updatedSeries.add(profile);
            seriesMap.put(seriesKey, updatedSeries);
        }

        for (String seriesKey : seriesMap.keySet()) {
            List<Profile> profiles = seriesMap.get(seriesKey);

            Profile firstProfile = profiles.get(0);
            AssayType assayType = firstProfile.getAssayType();
            String dose = firstProfile.getReplicateAnnotation().getPertDose();
            String time = firstProfile.getReplicateAnnotation().getPertTime();
            int technicalReplicates = 0;
            int runId = firstProfile.getRunId();

            int profileLength = assayType == AssayType.P100 ? p100Length : gcpLength;

            double[] minValues = new double[profileLength];
            double[] maxValues = new double[profileLength];
            double[] sumValues = new double[profileLength];

            for (int i = 0; i < profileLength; i++) {
                minValues[i] = Double.MAX_VALUE;
                maxValues[i] = Double.MIN_VALUE;
                sumValues[i] = 0;
            }

            for (Profile profile : profiles) {
                technicalReplicates++;

                double[] vector = profile.getVector();

                for (int j = 0; j < vector.length; j++) {
                    minValues[j] = minValues[j] < vector[j] ? minValues[j] : vector[j];
                    maxValues[j] = maxValues[j] > vector[j] ? maxValues[j] : vector[j];
                    sumValues[j] += vector[j];
                }
            }

            for (int k = 0; k < profileLength; k++) {
                sumValues[k] /= technicalReplicates;
            }

            ChartSeries chartSeriesLocal = new ChartSeries(
                    assayType, runId, minValues, maxValues, sumValues, dose, time, technicalReplicates);

            chartSeries.add(chartSeriesLocal);
        }

        return new MergedProfile(nTuple, chartSeries, replicateAnnotation, gctFile);
    }

    /**
     * Parse matrix from GCT file, feed it into Normalizer and decorate normalized matrix with GCT annotations
     */
    public static String gctNormalize(String gct) {

        StringBuilder sb = new StringBuilder("");

        List<String> peptideAnnotationsCache = new ArrayList<>();

        List<List<Double>> matrixOfValues = new ArrayList<>();

        int numberOfPeptides = 0;
        int numberOfReplicates = 0;
        int numberOfPeptideAnnotations = 0;
        int numberOfReplicateAnnotations = 0;


        String[] rows = gct.split("\n");

        for (int rowId = 0; rowId < rows.length; rowId++) {

            if (rowId == 0) {
                sb.append(rows[rowId]).append("\n");

            } else if (rowId == 1) {
                sb.append(rows[rowId]).append("\n");

                String[] dimensionsString = rows[rowId].split("\t");
                numberOfPeptides = Integer.valueOf(dimensionsString[0]);
                numberOfReplicates = Integer.valueOf(dimensionsString[1]);
                numberOfPeptideAnnotations = Integer.valueOf(dimensionsString[2]);
                numberOfReplicateAnnotations = Integer.valueOf(dimensionsString[3]);

                // init matrix of values
                for (int i = 0; i < numberOfReplicates; i++) {
                    matrixOfValues.add(new ArrayList<>());
                }

            } else if (rowId < numberOfReplicateAnnotations + 3) {

                sb.append(rows[rowId]).append("\n");

            } else {

                String[] cols = rows[rowId].split("\t");
                StringBuilder replicateAnnotationsInRow = new StringBuilder();

                for (int colId = 0; colId < cols.length; colId++) {
                    // add annotations to cache
                    if (colId < numberOfPeptideAnnotations + 1) {
                        replicateAnnotationsInRow.append(cols[colId]).append("\t");
                    }
                    // add values to matrix
                    else {
                        matrixOfValues.get(colId - (numberOfPeptideAnnotations + 1)).add(Double.parseDouble(cols[colId]));
                    }
                }
                peptideAnnotationsCache.add(replicateAnnotationsInRow.toString());
            }
        }

        List<List<Double>> normalizedMatrix = Normalizer.quantileAndZScoreNormalize(matrixOfValues);

        for (int i = 0; i < numberOfPeptides; i++) {
            // first append annotations
            sb.append(peptideAnnotationsCache.get(i));

            // secondly append normalized values
            for (int j = 0; j < numberOfReplicates; j++) {
                sb.append(normalizedMatrix.get(j).get(i));
                if (j < numberOfReplicates - 1) {
                    sb.append("\t");
                } else {
                    if (i < numberOfPeptides - 1)
                    sb.append("\n");
                }
            }
        }

        return sb.toString();
    }
}
