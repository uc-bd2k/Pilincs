package edu.uc.eh.datatypes;

/**
 * Created by chojnasm on 9/3/15.
 */
public class Int4Tuple {
    private int int1;
    private int int2;
    private int int3;
    private int int4;

    public Int4Tuple(int int1, int int2, int int3, int int4) {
        this.int1 = int1;
        this.int2 = int2;
        this.int3 = int3;
        this.int4 = int4;
    }

    public int getInt1() {
        return int1;
    }

    public void setInt1(int int1) {
        this.int1 = int1;
    }

    public int getInt2() {
        return int2;
    }

    public void setInt2(int int2) {
        this.int2 = int2;
    }

    public int getInt3() {
        return int3;
    }

    public void setInt3(int int3) {
        this.int3 = int3;
    }

    public int getInt4() {
        return int4;
    }

    public void setInt4(int int4) {
        this.int4 = int4;
    }
}
