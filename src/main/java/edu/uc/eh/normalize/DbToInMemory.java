package edu.uc.eh.normalize;

/**
 * Created by chojnasm on 1/12/16.
 *
 * Double check with:
 *
 *
 SELECT G.ASSAY_TYPE, P.PEPTIDE_ANNOTATION_ID, P.REPLICATE_ANNOTATION_ID, COUNT(*) as C
 FROM
 PEAK_AREA as P join GCT_FILE as G on P.GCT_FILE_ID=G.ID
 WHERE G.ASSAY_TYPE='GCP'
 GROUP BY G.ASSAY_TYPE, P.PEPTIDE_ANNOTATION_ID, P.REPLICATE_ANNOTATION_ID
 ORDER BY C desc
 *
 *
 */
public class DbToInMemory {
}
