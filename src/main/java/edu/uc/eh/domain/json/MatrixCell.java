package edu.uc.eh.domain.json;

/**
 * Created by chojnasm on 8/12/15.
 */
public class MatrixCell {
    private int rowIndex;
    private int columnIndex;
    private int discreteValue;

    public MatrixCell(int rowIndex, int columnIndex, int discreteValue) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.discreteValue = discreteValue;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndes) {
        this.columnIndex = columnIndes;
    }

    public int getDiscreteValue() {
        return discreteValue;
    }

    public void setDiscreteValue(int discreteValue) {
        this.discreteValue = discreteValue;
    }
}
