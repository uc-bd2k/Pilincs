package edu.uc.eh.utils;

import edu.uc.eh.datatypes.AssayType;
import edu.uc.eh.domain.PeptideAnnotation;
import edu.uc.eh.domain.repository.PeptideAnnotationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by chojnasm on 7/14/15.
 */

@Service
public class ParseGCT {
    Logger logger = LoggerFactory.getLogger("edu.uc.eh.utils.ParseGCT");

    private final PeptideAnnotationRepository peptideAnnotationRepository;

    @Autowired
    public ParseGCT(PeptideAnnotationRepository peptideAnnotationRepository) {
        this.peptideAnnotationRepository = peptideAnnotationRepository;
    }


    private void savePeptidesToRepository(String url, AssayType assayType) throws IOException {

        List<String> labelsOfProbes = new ArrayList<>();

        BufferedReader reader = UtilsNetwork.downloadFile(url);

        String line;
        reader.readLine();
        line = reader.readLine();

        String[] tokens = line.split("\t", -1);
        int numberOfProbes = Integer.valueOf(tokens[0]).intValue();
        int numberOfReplicates = Integer.valueOf(tokens[1]).intValue();
        int annotationsOfProbes = Integer.valueOf(tokens[2]).intValue() + 1;
        int annotationsOfReplicates = Integer.valueOf(tokens[3]).intValue() + 1;

        line = reader.readLine();
        tokens = line.split("\t", -1);

        // first token is Id
        if (labelsOfProbes.size() == 0) {
            labelsOfProbes.add(tokens[0]);
        }

        // Labels of annotations of probes/peptides
        for (int i = 1; i < annotationsOfProbes; i++) {
            if (!labelsOfProbes.contains(tokens[i])) {
                labelsOfProbes.add(tokens[i]);
            }
        }

        // lines with meta replicates
        for (int rowId = 0; rowId < annotationsOfReplicates - 1; rowId++) {
            reader.readLine();
        }

        // lines with meta probes and values
        for (int rowId = 0; rowId < numberOfProbes; rowId++) {
            line = reader.readLine();
            tokens = line.split("\t", -1);

            // fill meta probes
            PeptideAnnotation peptideAnnotation = new PeptideAnnotation(tokens[0],assayType);

            for(int i = 0; i < labelsOfProbes.size() ; i++) {

                String annotationName = labelsOfProbes.get(i);
                String annotationValue = tokens[i+1];

                switch (annotationName) {
                    case "pr_gene_id":
                        peptideAnnotation.setPrGeneId(annotationValue);
                        break;
                    case "pr_gene_symbol":
                    case "GeneName":
                        peptideAnnotation.setPrGeneSymbol(annotationValue);
                        break;
                    case "pr_p100_cluster":
                    case "pr_gcp_cluster":
                        peptideAnnotation.setPrCluster(annotationValue);
                        break;
                    case "pr_uniprot_id":
                        peptideAnnotation.setPrUniprotId(annotationValue);
                        break;
                    case "pr_p100_base_peptide":
                    case "pr_gcp_base_peptide":
                        peptideAnnotation.setPrBasePeptide(annotationValue);
                        break;
                    case "pr_gcp_histone_mark":
                        peptideAnnotation.setPrHistoneMark(annotationValue);
                        break;
                    case "pr_gcp_modified_peptide_code":
                    case "pr_p100_modified_peptide_code":
                        peptideAnnotation.setPrModifiedPeptideCode(annotationValue);
                        break;
                    default:
                }
            }
            peptideAnnotationRepository.save(peptideAnnotation);

        }
        reader.close();
    }

    public void parseToRepository(String url,
                                  List peakValues,
                                  HashMap<String, List<AnnotationValue>> metaProbes,
                                  HashMap<String, List<AnnotationValue>> metaReplicates) throws IOException {
        logger.info("Processing: {}", url);

        List<String> labelsOfProbes = new ArrayList<>();
        List<String> labelsOfReplicates = new ArrayList<>();

        BufferedReader reader = UtilsNetwork.downloadFile(url);

        String line = reader.readLine();
        if (line == null) {
            throw new IOException("The file is empty");
        } else if (!line.equals("#1.3")) {
            throw new IOException("Only 1.3 is supported");
        } else {
            line = reader.readLine();
            if (line == null) {
                throw new IOException("Empty second line.");
            } else {
                String[] tokens = line.split("\t", -1);
                if (tokens.length != 4) {
                    throw new IOException("There should be 4 values in the second line.");
//                    #1.3
//                    96  36  9 15
//
//                    96 = number of probes/peptides (data rows)
//                    36 = number of replicates (data columns)
//                    9 = number of probe/peptide annotations / meta-data (in columns)
//                    15 = number of replicate annotations / meta-data (in rows)
                } else {
                    int numberOfProbes = Integer.valueOf(tokens[0]).intValue(); // probes are also identified by peptides
                    int numberOfReplicates = Integer.valueOf(tokens[1]).intValue();
                    int annotationsOfProbes = Integer.valueOf(tokens[2]).intValue() + 1; //also count "Id"
                    int annotationsOfReplicates = Integer.valueOf(tokens[3]).intValue() + 1; // also count "Id"
                    if (numberOfProbes > 0 && numberOfReplicates > 0 && annotationsOfProbes > 0 && annotationsOfReplicates > 0) {

                        ArrayList<String> idsOfReplicas = new ArrayList<String>();

                        line = reader.readLine();
                        if (line == null) {
                            throw new IOException("Line 3 of GCT should contain some data.");
                        } else {
                            tokens = line.split("\t", -1);
                            if (tokens.length != annotationsOfProbes + numberOfReplicates) {
                                throw new IOException("We expect number of columns to be = number of replicas + number of probe annotations");
                            } else {

                                // first token is Id
                                if(labelsOfProbes.size()==0&&labelsOfReplicates.size()==0) {
                                    labelsOfProbes.add(tokens[0]);
                                    labelsOfReplicates.add(tokens[0]);
                                }

                                // Labels of annotations of probes/peptides
                                for (int i = 1; i < annotationsOfProbes; i++) {
                                    if(!labelsOfProbes.contains(tokens[i])) {
                                        labelsOfProbes.add(tokens[i]);
                                    }
                                }

                                // Ids of replicates
                                for (int i = annotationsOfProbes; i < annotationsOfProbes + numberOfReplicates; i++) {
                                    metaReplicates.put(tokens[i], new ArrayList<AnnotationValue>());
                                    idsOfReplicas.add(tokens[i]);
                                }

                                // lines with meta replicates
                                for (int rowId = 0; rowId < annotationsOfReplicates - 1; rowId++) {
                                    line = reader.readLine();
                                    if (line == null) {
                                        throw new IOException("Line " + rowId + " in meta replicates should not be empty");
                                    }

                                    tokens = line.split("\t", -1);
                                    if (tokens.length != annotationsOfProbes + numberOfReplicates) {
                                        throw new IOException("We expect number of columns to be = number of replicas + number of probe annotations");
                                    }

                                    // add Id to labels
                                    if(!labelsOfReplicates.contains(tokens[0])){// rather unneeded condition
                                        labelsOfReplicates.add(tokens[0]);
                                    }

                                    for (int colId = annotationsOfProbes; colId < annotationsOfProbes + numberOfReplicates; colId++) {
                                        updateMetaReplicas(url, tokens[0], colId - annotationsOfProbes, tokens[colId], metaReplicates);
                                    }
                                }

                                // lines with meta probes and values
                                for (int rowId = 0; rowId < numberOfProbes; rowId++) {
                                    line = reader.readLine();
                                    if (line == null) {
                                        throw new IOException("Line " + rowId + " in meta probes + values should not be empty");
                                    }

                                    tokens = line.split("\t", -1);
                                    if (tokens.length != annotationsOfProbes + numberOfReplicates) {
                                        throw new IOException("We expect number of columns to be = number of replicas + number of probe annotations");
                                    }

                                    // fill meta probes
                                    metaProbes.put(tokens[0], updateMetaProbes(url,
                                            labelsOfProbes,
                                            Arrays.copyOfRange(tokens, 1, annotationsOfProbes)));

                                    for (int colId = annotationsOfProbes; colId < numberOfReplicates + annotationsOfProbes; colId++) {

                                        if (tokens[colId].equals("NA")) {
                                            peakValues.add(new ProbeReplicatePeak(
                                                    tokens[0], // probe id
                                                    idsOfReplicas.get(colId - annotationsOfProbes), // replica id
                                                    null)); // measurement
                                            // continue;
                                        }else {
                                            peakValues.add(new ProbeReplicatePeak(
                                                    tokens[0], // probe id
                                                    idsOfReplicas.get(colId - annotationsOfProbes), // replica id
                                                    Double.valueOf(tokens[colId]))); // measurement
                                        }
                                    }

                                }
                                reader.close();
                            }
                        }
                    } else {
                        throw new IOException("Numbers in line two should be positive.");
                    }
                }
            }
        }


    }

    private List<AnnotationValue> updateMetaProbes(String fileName, List<String> localLabelsOfProbes, String[] values) {
        List<AnnotationValue> output = new ArrayList<AnnotationValue>();
        int counter = -1;

        for(String label : localLabelsOfProbes){
            if(counter==-1){
                counter++;
                continue;
            }
            output.add(new AnnotationValue(label,values[counter]));
            counter++;
        }
        return output;
    }

    private void updateMetaReplicas(String fileName, String annotationName, int colId,
                                    String token, HashMap<String, List<AnnotationValue>> metaReplicas) {

        int counter = 0;
        for (String currentKey : metaReplicas.keySet()) {

            if (counter == colId) {
                List<AnnotationValue> metaContent = metaReplicas.get(currentKey);
                metaContent.add(new AnnotationValue(annotationName, token));
                metaReplicas.put(currentKey, metaContent);
                break;
            }

            counter++;
        }
    }

    public class AnnotationValue{
        private String annotationName;
        private String annotationValue;

        public AnnotationValue(
                String annotationName,
                String annotationValue) {
            this.annotationName = annotationName;
            this.annotationValue = annotationValue;
        }

        @Override
        public String toString() {
            return "AnnotationValue{" +
                    "annotationName='" + annotationName + '\'' +
                    ", annotationValue='" + annotationValue + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AnnotationValue)) return false;

            AnnotationValue that = (AnnotationValue) o;
            if (!getAnnotationName().equals(that.getAnnotationName())) return false;
            return getAnnotationValue().equals(that.getAnnotationValue());

        }

        @Override
        public int hashCode() {
            int result = getAnnotationName().hashCode();
            result = 31 * result + getAnnotationValue().hashCode();
            return result;
        }

        public String getAnnotationName() {
            return annotationName;
        }

        public void setAnnotationName(String annotationName) {
            this.annotationName = annotationName;
        }

        public String getAnnotationValue() {
            return annotationValue;
        }

        public void setAnnotationValue(String annotationValue) {
            this.annotationValue = annotationValue;
        }

    }

    public class ProbeReplicatePeak {
        private String probeId;
        private String replicateId;
        private Double peakArea;

        public ProbeReplicatePeak(String probeId, String replicateId, Double peakArea) {
            this.probeId = probeId;
            this.replicateId = replicateId;
            this.peakArea = peakArea;
        }

        public String getProbeId() {
            return probeId;
        }

        public void setProbeId(String probeId) {
            this.probeId = probeId;
        }

        public String getReplicateId() {
            return replicateId;
        }

        public void setReplicateId(String replicateId) {
            this.replicateId = replicateId;
        }

        public Double getPeakArea() {
            return peakArea;
        }

        public void setPeakArea(Double peakArea) {
            this.peakArea = peakArea;
        }
    }
}
