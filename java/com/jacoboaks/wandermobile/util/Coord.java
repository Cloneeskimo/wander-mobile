package com.jacoboaks.wandermobile.util;

/**
 * Represents a 2 component coordinate - this class is essentially a vector2f.
 */
public class Coord {

    //Data
    public float x, y; //x, y

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

    //Node Constructor
    public Coord(Node node) {
        this.x = Float.parseFloat(node.getChild("x").getValue());
        this.y = Float.parseFloat(node.getChild("y").getValue());
    }

    /**
     * Constructs this coordinate at (0, 0).
     */
    public Coord() {
        this(0, 0);
    }

    //String Converter
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    //Node Converter
    public Node toNode() {
        Node node = new Node("Coord");
        node.addChild("x", Float.toString(this.x));
        node.addChild("y", Float.toString(this.y));
        return node;
    }
}
