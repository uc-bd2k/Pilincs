package edu.uc.eh.normalize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by chojnasm on 1/11/16.
 * <p>
 * Normalize according to the procedure in: https://en.wikipedia.org/wiki/Quantile_normalization
 * <p>
 * Missing values represented here as NA:
 * - are replaced with an average value in a column just for the purpose of ranking.
 *
 * Example:

 Input matrix is: List{ List{5, null, null, 4}, {4, 1, 4, 2}, {3, 4, 6, 8}}

 A    5    4    3
 B    NA   1    4
 C    NA   4    6
 D    4    2    8


 After NA replacement:

 A    5    4    3
 B    4.5  1    4
 C    4.5  4    6
 D    4    2    8

Ranks are:

 A    iv   iii   i
 B    ii   i     ii
 C    ii   iii   iii
 D    i    ii    iv

 After ordering we have a temporary matrix,

 A    4     1   3
 B    4.5   2   4
 C    4.5   4   6
 D    5     4   8

 from which we can calculate row-wide averages, see how duplicates are dealt with.

 A (4 1 3)/3 = 2.66 = rank i
 B (4.5 2 4)/3 = 3.5 = rank ii
 C (4.5 4 6)/3 = 4.83 = rank iii
 D (5 4 8)/3 = 5.66 = rank iv

 Quantile normalized matrix is:

 A    5.66      4.83    2.66
 B    3.5       2.66    3.5
 C    3.5       4.83    4.83
 D    2.66      3.5     5.66

 Now w z-score normalize in rows i.e. subtract mean and divide by standard deviation

 A (5.66 + 4.83 + 2.66)/3  (x - <x>)/stdev(x), x- vector

 */

public class Normalizer {

    /**
     *
     * @param inputMatrix is a list of columns, each column is kept as a list of doubles
     * @return
     */
    public static List<List<Double>> quantileAndZScoreNormalize(List<List<Double>> inputMatrix){
        List<List<Double>> outputMatrix = new ArrayList<>();

        // temporary matrix with sorted non-null columns
        List<List<Double>> replacedNulls = new ArrayList<>();

        // average values for each column
        List<Double> averageForColumns = new ArrayList<>();

        // replace null values with means
        for(List<Double> column : inputMatrix){

            Double average = column
                    .stream()
                    .filter(d -> d != null)
                    .mapToDouble(Double::valueOf)
                    .average()
                    .getAsDouble();

            averageForColumns.add(average);

            // add column with imputed null values to replacedNulls matrix
            replacedNulls
                    .add(column.stream()
                            .map(d -> d == null ? average : d)
                            .collect(Collectors.toList()));
        }

        // list of row-wide averages
        List<Double> rankToAverageMapping = null;

        // Sort temporary matrix and computer row-wide averages
        for(List<Double> column : replacedNulls){

            // init rankToAverageMapping
            if(rankToAverageMapping == null) rankToAverageMapping = new ArrayList<>(Collections.nCopies(column.size(), 0.0));
            Collections.sort(column);

            for(int j = 0; j < column.size(); j++){

                rankToAverageMapping.set(j, rankToAverageMapping.get(j) + column.get(j) / replacedNulls.size());
            }
        }

        // map values to ranks, now replacedNulls are sorted
        for(int i = 0; i < inputMatrix.size(); i++){

            List<Double> oryginalColumn = inputMatrix.get(i);
            List<Double> sortedAndImputedColumns = replacedNulls.get(i);
            List<Double> outputColumn = new ArrayList<>();

            for(int j = 0; j < oryginalColumn.size(); j++){
                Double value = oryginalColumn.get(j);
                if(value == null) value = averageForColumns.get(i);

                int rank = sortedAndImputedColumns.indexOf(value);
                Double normalizedValue = rankToAverageMapping.get(rank);

                outputColumn.add(normalizedValue);
            }

            outputMatrix.add(outputColumn);
        }

        return outputMatrix;
    }
}
