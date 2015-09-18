package edu.uc.eh.utils;

import edu.uc.eh.datatypes.StringDouble;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by chojnasm on 8/6/15.
 */
public class UtilsStatistics {


    private static final Logger log = LoggerFactory.getLogger(UtilsStatistics.class);

    public static SortedSet<StringDouble> influentialPeptides(
            double[] vectorA,
            double[] vectorB,
            List<String> referenceProfile,
            boolean ifPositiveCorrelation) {


        DescriptiveStatistics statisticsA = new DescriptiveStatistics(vectorA);
        DescriptiveStatistics statisticsB = new DescriptiveStatistics(vectorB);

        double meanA = statisticsA.getMean();
        double meanB = statisticsB.getMean();

        double standardDevA = statisticsA.getStandardDeviation();
        double standardDevB = statisticsB.getStandardDeviation();

        SortedSet<StringDouble> output = new TreeSet<>();
        double sumImacts = 0.0;

        int n = referenceProfile.size();

        for(int i = 0; i < n; i++){
            double impact = ((vectorA[i] - meanA) * (vectorB[i] - meanB)) / ((n - 1) * standardDevA * standardDevB);
//            log.warn(""+impact);
            sumImacts+=impact;
            String peptideName = referenceProfile.get(i);

            if(output.size() < 7){
                output.add(new StringDouble(peptideName,impact));
            }else{
                if(ifPositiveCorrelation){
                    if(impact > output.first().getaDouble()){
                        output.remove(output.first());
                        output.add(new StringDouble(peptideName,impact));
                    }
                }else{
                    if(impact < output.last().getaDouble()){
                        output.remove(output.last());
                        output.add(new StringDouble(peptideName,impact));
                    }
                }
            }
        }
//        log.warn("S "+sumImacts)sumImacts;
        return output;
    }

    public static void imputeProfileVector(Double[] profileVector, boolean[] imputeVector) {
        Double sum = 0.0;
        int nonEmpty = 0;

        for (int i = 0; i < profileVector.length; i++) {
            if (profileVector[i] != null) {
                sum += profileVector[i];
                nonEmpty++;
            }
        }

        if (nonEmpty == 0) {
            log.warn("All coordinates of a profile are zeros");
        }

        for (int i = 0; i < profileVector.length; i++) {
            if (profileVector[i] == null) {
                profileVector[i] = sum / nonEmpty;
                imputeVector[i] = true;
            }else{
                imputeVector[i] = false;
            }
        }

    }

}
