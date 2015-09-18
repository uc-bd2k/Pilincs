package edu.uc.eh.domain;

import edu.uc.eh.datatypes.ChartSeries;
import edu.uc.eh.datatypes.JsonWrapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by chojnasm on 9/15/15.
 */

@Entity
public class MergedProfile {

    @Id
    @GeneratedValue
    private Long id;

    private String nTuple;

    @Lob
    private JsonWrapper jsonChart;

    @ManyToOne
    private ReplicateAnnotation replicateAnnotation;

    @ManyToOne
    private GctFile gctFile;

    @OneToMany(mappedBy = "mergedProfile")
    private Set<Profile> profiles = new HashSet<>();

    public MergedProfile() {
    }

    public MergedProfile(String nTuple,
                         List<ChartSeries> chartSeries,
                         ReplicateAnnotation replicateAnnotation,
                         GctFile gctFile) {
        this.nTuple = nTuple;
        this.replicateAnnotation = replicateAnnotation;
        this.gctFile = gctFile;

        JSONArray arrayOfSeries = new JSONArray();

        for (int i = 0; i < chartSeries.size(); i++) {

            JSONArray seriesVector = new JSONArray();
            JSONObject seriesObject = new JSONObject();

            ChartSeries thisSeries = chartSeries.get(i);

            seriesObject.put("assay", thisSeries.getAssayType().toString());
            seriesObject.put("dose", thisSeries.getDose());
            seriesObject.put("time", thisSeries.getTime());
            seriesObject.put("runId", thisSeries.getRunId());
            seriesObject.put("replicates", thisSeries.getTechnicalReplicates());


            for (int j = 0; j < thisSeries.getLength(); j++) {
                JSONObject jo = new JSONObject();

                jo.put("avg", thisSeries.getAvgValues()[j]);
                jo.put("min", thisSeries.getMinValues()[j]);
                jo.put("max", thisSeries.getMaxValues()[j]);

                seriesVector.add(jo);
            }

            seriesObject.put("data", seriesVector);

            arrayOfSeries.add(seriesObject);
        }

        JSONObject mainObj = new JSONObject();
        mainObj.put("series", arrayOfSeries);

        String jsonString = mainObj.toJSONString();

        JsonWrapper jsonWrapper = new JsonWrapper(jsonString);
        this.jsonChart = jsonWrapper;
    }

    public ReplicateAnnotation getReplicateAnnotation() {
        return replicateAnnotation;
    }

    public void setReplicateAnnotation(ReplicateAnnotation replicateAnnotation) {
        this.replicateAnnotation = replicateAnnotation;
    }

    public GctFile getGctFile() {
        return gctFile;
    }

    public void setGctFile(GctFile gctFile) {
        this.gctFile = gctFile;
    }

    public String getnTuple() {
        return nTuple;
    }

    public void setnTuple(String nTuple) {
        this.nTuple = nTuple;
    }

    public String getJsonChart() {
        return jsonChart.getJson();
    }

}
