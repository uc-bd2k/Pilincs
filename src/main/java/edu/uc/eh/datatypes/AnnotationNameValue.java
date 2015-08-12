package edu.uc.eh.datatypes;

/**
 * Created by chojnasm on 8/11/15.
 */
public class AnnotationNameValue {
    private String annotationName;
    private String annotationValue;

    public AnnotationNameValue(String annotationName, String annotationValue) {
        this.annotationName = annotationName;
        this.annotationValue = annotationValue;
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
