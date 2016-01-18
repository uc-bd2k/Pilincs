package edu.uc.eh.normalize;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by chojnasm on 1/11/16.
 */
public class NormalizerTest {

    @Test
    /**
     * Read documentation of QuantileNormalize class for details.
     */
    public void testQuantileNormalize() throws Exception {

        List<List<Double>> inputMatrix = new ArrayList<>();

        // input matrix
        Double[] sample1 = {5.0, null, null, 4.0};
        Double[] sample2 = {4.0, 1.0, 4.0, 2.0};
        Double[] sample3 = {3.0, 4.0, 6.0, 8.0};

        inputMatrix.add(new ArrayList<>(Arrays.asList(sample1)));
        inputMatrix.add(new ArrayList<>(Arrays.asList(sample2)));
        inputMatrix.add(new ArrayList<>(Arrays.asList(sample3)));

        List<List<Double>> outputMatrix = Normalizer.quantileAndZScoreNormalize(inputMatrix);

        // expected normalized matrix
        Double[] normalized1 = {5.666666666666666, 3.5, 3.5, 2.6666666666666665};
        Double[] normalized2 = {4.833333333333333, 2.6666666666666665, 4.833333333333333, 3.5};
        Double[] normalized3 = {2.6666666666666665, 3.5, 4.833333333333333, 5.666666666666666};

        assertArrayEquals("First column", normalized1, outputMatrix.get(0).toArray());
        assertArrayEquals("Second column", normalized2, outputMatrix.get(1).toArray());
        assertArrayEquals("Third column", normalized3, outputMatrix.get(2).toArray());
    }

}