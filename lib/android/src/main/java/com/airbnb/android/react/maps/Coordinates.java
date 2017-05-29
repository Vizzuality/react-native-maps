package com.airbnb.android.react.maps;

/**
 * Created by joseangel on 25/05/2017.
 */

public class Coordinates {
    private int[] tile;
    private double[] precision;

    public Coordinates(int[] tile, double[] precision) {
        this.tile = tile;
        this.precision = precision;
    }

    public int[] getTile() {
        return tile;
    }
    public double[] getPrecision() {
        return precision;
    }
}
