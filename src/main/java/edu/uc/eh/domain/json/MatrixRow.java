package edu.uc.eh.domain.json;

/**
 * Created by chojnasm on 8/14/15.
 */
public class MatrixRow {

    private int rowIndex;
    private int clusterOrder;
    private int[] colors;

    public MatrixRow(int rowIndex, int clusterOrder, int[] colors) {
        this.rowIndex = rowIndex;
        this.clusterOrder = clusterOrder;
        this.colors = colors;

    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getClusterOrder() {
        return clusterOrder;
    }

    public void setClusterOrder(int clusterOrder) {
        this.clusterOrder = clusterOrder;
    }

    public int[] getColors() {
        return colors;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }
}
