package edu.uc.eh.datatypes;

/**
 * Created by chojnasm on 9/15/15.
 */
public class ChartSeries {

    private AssayType assayType;

    private int runId;

    private double[] minValues;
    private double[] maxValues;
    private double[] avgValues;

    private String dose;
    private String time;

    private int technicalReplicates;

    public ChartSeries(AssayType assayTypes,
                       int runId,
                       double[] minValues, double[] maxValues, double[] avgValues,
                       String dose, String time,
                       int technicalReplicates) {
        this.assayType = assayTypes;
        this.runId = runId;
        this.minValues = minValues;
        this.maxValues = maxValues;
        this.avgValues = avgValues;
        this.dose = dose;
        this.time = time;
        this.technicalReplicates = technicalReplicates;
    }

    public AssayType getAssayType() {
        return assayType;
    }

    public void setAssayType(AssayType assayType) {
        this.assayType = assayType;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public double[] getMinValues() {
        return minValues;
    }

    public void setMinValues(double[] minValues) {
        this.minValues = minValues;
    }

    public int getLength() {
        return minValues.length;
    }

    public double[] getMaxValues() {
        return maxValues;
    }

    public void setMaxValues(double[] maxValues) {
        this.maxValues = maxValues;
    }

    public double[] getAvgValues() {
        return avgValues;
    }

    public void setAvgValues(double[] avgValues) {
        this.avgValues = avgValues;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTechnicalReplicates() {
        return technicalReplicates;
    }

    public void setTechnicalReplicates(int technicalReplicates) {
        this.technicalReplicates = technicalReplicates;
    }
}
