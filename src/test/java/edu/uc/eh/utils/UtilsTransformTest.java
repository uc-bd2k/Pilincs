package edu.uc.eh.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by chojnasm on 11/13/15.
 */
public class UtilsTransformTest {

    @Test
    public void testGctNormalize() throws Exception {

        String oryginalGct = "#1.3\t\t\t\t\t\t\t\t\t\t\n" +
                "4\t3\t7\t14\t\t\t\t\t\t\t\n" +
                "id\tprGeneId\tprGeneSymbol\tprBasePeptide\tprModifiedPeptideCode\tprUniprotId\tprHistoneMark\tprBiNumber\tGA5-30E17-025C01\tGP3-3F877-052E04\tGM7-33653-093H09\n" +
                "cellId\t\t\t\t\t\t\t\tA375\tPC3\tMCF7\n" +
                "detPlate\t\t\t\t\t\t\t\tG-0017\tG-0018\tG-0023\n" +
                "detWell\t\t\t\t\t\t\t\tC1\tE4\tH9\n" +
                "isomericSmiles\t\t\t\t\t\t\t\tCCNC(=O)C[C@H]1C2=NN=C(N2C3=C(C=C(C=C3)OC)C(=N1)C4=CC=C(C=C4)Cl)C\tCN1CCCN(CC1)C2=NC3=CC(=C(C=C3C(=N2)NC4CCN(CC4)C)OC)OCCOCCN(C)C\tC[C@@H]1CC[C@]2(CCCCO2)O[C@@H]1[C@@H](C)C[C@@H]([C@@H]3C(=C)[C@H]([C@H]4[C@H](O3)CC[C@]5(O4)CC[C@@H](O5)C=C[C@@H](C)[C@@H]6CC(=C[C@@]7(O6)[C@@H](CC[C@H](O7)C[C@](C)(C(=O)O)O)O)C)O)O\n" +
                "pertDose\t\t\t\t\t\t\t\t1\t10\t1\n" +
                "pertDoseUnit\t\t\t\t\t\t\t\tuM\tuM\tuM\n" +
                "pertId\t\t\t\t\t\t\t\tBRD-K08109215\tBRD-K74236984\tBRD-A13254067\n" +
                "pertiname\t\t\t\t\t\t\t\tGSK525762A\tUNC-0321\tOkadaic Acid\n" +
                "pertTime\t\t\t\t\t\t\t\t24\t24\t24\n" +
                "pertTimeUnit\t\t\t\t\t\t\t\th\th\th\n" +
                "pertType\t\t\t\t\t\t\t\ttrt_cp\ttrt_cp\ttrt_cp\n" +
                "pertVehicle\t\t\t\t\t\t\t\tDMSO\tDMSO\tDMSO\n" +
                "pubchemCid\t\t\t\t\t\t\t\t46943432\t46901937\t57059112\n" +
                "lsmId\t\t\t\t\t\t\t\tLSM-6334\tLSM-4942\tLSM-42759\n" +
                "BI10003\t8350\tHIST1H3A\tTKQTAR\tT[+56]K[+56]QTAR\tP68431\tH3K4me0\tBI10003\t5\t4\t3\n" +
                "BI10004\t8350\tHIST1H3A\tTKQTAR\tT[+56]K[+70]QTAR\tP68431\tH3K4me1\tBI10004\t4.5\t1\t4\n" +
                "BI10005\t8350\tHIST1H3A\tTKQTAR\tT[+56]K[+28]QTAR\tP68431\tH3K4me2\tBI10005\t4.5\t4\t6\n" +
                "BI10006\t8350\tHIST1H3A\tTKQTAR\tT[+56]K[+42]QTAR\tP68431\tH3K4me3\tBI10006\t4\t2\t8";

        String normalizedGct = UtilsTransform.gctNormalize(oryginalGct);

        String expectedOutput = "#1.3\t\t\t\t\t\t\t\t\t\t\n" +
                "4\t3\t7\t14\t\t\t\t\t\t\t\n" +
                "id\tprGeneId\tprGeneSymbol\tprBasePeptide\tprModifiedPeptideCode\tprUniprotId\tprHistoneMark\tprBiNumber\tGA5-30E17-025C01\tGP3-3F877-052E04\tGM7-33653-093H09\n" +
                "cellId\t\t\t\t\t\t\t\tA375\tPC3\tMCF7\n" +
                "detPlate\t\t\t\t\t\t\t\tG-0017\tG-0018\tG-0023\n" +
                "detWell\t\t\t\t\t\t\t\tC1\tE4\tH9\n" +
                "isomericSmiles\t\t\t\t\t\t\t\tCCNC(=O)C[C@H]1C2=NN=C(N2C3=C(C=C(C=C3)OC)C(=N1)C4=CC=C(C=C4)Cl)C\tCN1CCCN(CC1)C2=NC3=CC(=C(C=C3C(=N2)NC4CCN(CC4)C)OC)OCCOCCN(C)C\tC[C@@H]1CC[C@]2(CCCCO2)O[C@@H]1[C@@H](C)C[C@@H]([C@@H]3C(=C)[C@H]([C@H]4[C@H](O3)CC[C@]5(O4)CC[C@@H](O5)C=C[C@@H](C)[C@@H]6CC(=C[C@@]7(O6)[C@@H](CC[C@H](O7)C[C@](C)(C(=O)O)O)O)C)O)O\n" +
                "pertDose\t\t\t\t\t\t\t\t1\t10\t1\n" +
                "pertDoseUnit\t\t\t\t\t\t\t\tuM\tuM\tuM\n" +
                "pertId\t\t\t\t\t\t\t\tBRD-K08109215\tBRD-K74236984\tBRD-A13254067\n" +
                "pertiname\t\t\t\t\t\t\t\tGSK525762A\tUNC-0321\tOkadaic Acid\n" +
                "pertTime\t\t\t\t\t\t\t\t24\t24\t24\n" +
                "pertTimeUnit\t\t\t\t\t\t\t\th\th\th\n" +
                "pertType\t\t\t\t\t\t\t\ttrt_cp\ttrt_cp\ttrt_cp\n" +
                "pertVehicle\t\t\t\t\t\t\t\tDMSO\tDMSO\tDMSO\n" +
                "pubchemCid\t\t\t\t\t\t\t\t46943432\t46901937\t57059112\n" +
                "lsmId\t\t\t\t\t\t\t\tLSM-6334\tLSM-4942\tLSM-42759\n" +
                "BI10003\t8350\tHIST1H3A\tTKQTAR\tT[+56]K[+56]QTAR\tP68431\tH3K4me0\tBI10003\t5.666666666666666\t4.833333333333333\t2.6666666666666665\n" +
                "BI10004\t8350\tHIST1H3A\tTKQTAR\tT[+56]K[+70]QTAR\tP68431\tH3K4me1\tBI10004\t3.5\t2.6666666666666665\t3.5\n" +
                "BI10005\t8350\tHIST1H3A\tTKQTAR\tT[+56]K[+28]QTAR\tP68431\tH3K4me2\tBI10005\t3.5\t4.833333333333333\t4.833333333333333\n" +
                "BI10006\t8350\tHIST1H3A\tTKQTAR\tT[+56]K[+42]QTAR\tP68431\tH3K4me3\tBI10006\t2.6666666666666665\t3.5\t5.666666666666666";

        assertEquals("Normalized GCT is incorrect", normalizedGct, expectedOutput);

    }
}