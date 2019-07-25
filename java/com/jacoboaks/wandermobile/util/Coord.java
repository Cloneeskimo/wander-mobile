package com.jacoboaks.wandermobile.util;

public class Coord {

    //Data
    public float x;
    public float y;

    //Full Constructor
    public Coord(float x, float y) {
        this.x = x;
        this.y = y;
    }

    //Copy Constructor
    public Coord(Coord other) {
        this(other.x, other.y);
    }

    //Default Constructor
    public Coord() {
        this(0, 0);
    }
}
