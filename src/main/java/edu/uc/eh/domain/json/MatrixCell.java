package edu.uc.eh.domain.json;

/**
 * Created by chojnasm on 8/12/15.
 */
public class MatrixCell {
    private int rowIndex;
    private int columnIndes;
    private Double discreteValue;

    public MatrixCell(int rowIndex, int columnIndes, Double discreteValue) {
        this.rowIndex = rowIndex;
        this.columnIndes = columnIndes;
        this.discreteValue = discreteValue;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumnIndes() {
        return columnIndes;
    }

    public void setColumnIndes(int columnIndes) {
        this.columnIndes = columnIndes;
    }

    public Double getDiscreteValue() {
        return discreteValue;
    }

    public void setDiscreteValue(Double discreteValue) {
        this.discreteValue = discreteValue;
    }
}
