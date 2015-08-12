package edu.uc.eh.utils;

import edu.uc.eh.datatypes.AssayType;
import edu.uc.eh.datatypes.StringDouble;
import edu.uc.eh.domain.Profile;
import edu.uc.eh.domain.json.HeatMapResponse;
import edu.uc.eh.domain.json.MatrixCell;
import edu.uc.eh.domain.json.ProfileRecord;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

/**
 * Created by chojnasm on 8/6/15.
 */
public class UtilsTransform {

    public static String truncate(String text, int number){
        if(text.length() > number){
            return text.substring(0,number)+"..";
        }else{
            return text;
        }
    }
    public static String SortedSetToHTML(SortedSet<StringDouble> peptides, boolean orderAsc) {
        DecimalFormat df = new DecimalFormat("0.0000");
        StringBuilder sb = null;

        if(orderAsc){
            sb = new StringBuilder("<div class=\"col-md-4\">");
            for(StringDouble sd : peptides){
                sb.append(df.format(sd.getaDouble())).append("<span class=\"tab-space\">").append(truncate(sd.getString(),37)).append("</span><br/>");
            }
            sb.append("</div>");
        }else{
            sb = new StringBuilder("<br/><div class=\"col-md-4\">");
            Object[] array = peptides.toArray();

            for(int i = array.length-1;i >= 0; i--){
                sb.append(df.format(((StringDouble)array[i]).getaDouble())).append("<span class=\"tab-space\">").append(truncate(((StringDouble) array[i]).getString(), 37)).append("</span><br/>");
            }
            sb.append("</div>");
        }

        return sb.toString();
    }

    public static HeatMapResponse profilesToHeatMap(List<Profile> profiles, List<String> referenceProfile) {

        List<String> peptideNames = referenceProfile;
        List<String> profileNames = new ArrayList<>();
        List<MatrixCell> cells = new ArrayList<>();

        int rowIndex = 0;

        for( Profile profile : profiles){
            profileNames.add(profile.getReplicateAnnotation().getReplicateId());

            for(int columnIndex = 0; columnIndex < profile.getVector().length; columnIndex++){
                cells.add(new MatrixCell(rowIndex,columnIndex,profile.getVector()[columnIndex]));
            }

            rowIndex++;
        }
        return new HeatMapResponse(peptideNames, profileNames, cells);
    }
}
