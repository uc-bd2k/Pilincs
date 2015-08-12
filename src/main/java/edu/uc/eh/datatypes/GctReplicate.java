package edu.uc.eh.datatypes;

import edu.uc.eh.domain.GctFile;
import edu.uc.eh.domain.ReplicateAnnotation;

/**
 * Created by chojnasm on 8/10/15.
 */
public class GctReplicate{
    private GctFile gctFile;
    private ReplicateAnnotation replicateAnnotation;

    public GctReplicate(GctFile gctFile, ReplicateAnnotation replicateAnnotation) {
        this.gctFile = gctFile;
        this.replicateAnnotation = replicateAnnotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GctReplicate)) return false;

        GctReplicate that = (GctReplicate) o;

        if (!getGctFile().equals(that.getGctFile())) return false;
        return getReplicateAnnotation().equals(that.getReplicateAnnotation());

    }

    @Override
    public int hashCode() {
        int result = getGctFile().hashCode();
        result = 31 * result + getReplicateAnnotation().hashCode();
        return result;
    }

    public GctFile getGctFile() {
        return gctFile;
    }

    public ReplicateAnnotation getReplicateAnnotation() {
        return replicateAnnotation;
    }

}
