package com.jacoboaks.wandermobile.util;

/**
 * Represents a 2 component coordinate - this class is essentially a vector2f.
 */
public class Coord {

    //Data
    public float x;
    public float y;

    /**
     * Constructs this Coord with the given x and y values.
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
     * Constructs this coordinate at (0, 0).
     */
    public Coord() {
        this(0, 0);
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
