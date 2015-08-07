package edu.uc.eh.utils;

import edu.uc.eh.datatypes.StringDouble;
import edu.uc.eh.domain.Profile;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by chojnasm on 8/6/15.
 */
public class CalculateUtils {

    private static final Logger log = LoggerFactory.getLogger(CalculateUtils.class);
    public static SortedSet<StringDouble> influentialPeptides(Profile profileA, Profile profileB, boolean ifPositiveCorrelation) {

        List<StringDouble> list = profileA.getVector();

        double[] doublesA = profileA.getVectorDoubles();
        double[] doublesB = profileB.getVectorDoubles();

        PearsonsCorrelation pearson = new PearsonsCorrelation();

//        log.warn("P "+pearson.correlation(doublesA,doublesB));

        DescriptiveStatistics statisticsA = new DescriptiveStatistics(doublesA);
        DescriptiveStatistics statisticsB = new DescriptiveStatistics(doublesB);

        double meanA = statisticsA.getMean();
        double meanB = statisticsB.getMean();

        double standardDevA = statisticsA.getStandardDeviation();
        double standardDevB = statisticsB.getStandardDeviation();

        SortedSet<StringDouble> output = new TreeSet<>();
        double sumImacts = 0.0;

        int n = list.size();

        for(int i = 0; i < n; i++){
            double impact = ((doublesA[i] - meanA) * (doublesB[i] - meanB)) / ((n - 1) * standardDevA * standardDevB);
//            log.warn(""+impact);
            sumImacts+=impact;
            String peptideName = profileA.getVector().get(i).getString();

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
}
