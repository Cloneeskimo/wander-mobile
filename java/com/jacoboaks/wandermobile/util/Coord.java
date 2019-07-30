package com.jacoboaks.wandermobile.util;

/**
 * @purpose is to represent a 2 component coordinate - essentially this class is a vector2f
 */
public class Coord {

    //Data
    public float x;
    public float y;

    /**
     * @purpose is to construct this coordinate with the given x and y values
     * @param x the x value
     * @param y the y value
     */
    public Coord(float x, float y) {
        this.x = x;
        this.y = y;
    }

    //Copy Constructor
    public Coord(Coord other) {
        this(other.x, other.y);
    }

    /**
     * @purpose is to construct this coordinate at 0, 0
     */
    public Coord() {
        this(0, 0);
    }
}
