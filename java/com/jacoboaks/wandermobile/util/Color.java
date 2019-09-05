package com.jacoboaks.wandermobile.util;

/**
 * Represents a 4-component color (rgba).
 */
public class Color {

    //Data
    private float r, g, b, a; //rgba

    /**
     * Constructs this color as the default color (white).
     */
    public Color() {
        this(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Constructs this color with the given rgba values.
     * @param r the red value
     * @param g the green value
     * @param b the blue value
     * @param a the alpha value
     */
    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    /**
     * Constructs this Color using a Node.
     * @param data the Node to use when constructing this Color
     */
    public Color(Node data) {
        this(Float.parseFloat(data.getChild("r").getValue()), Float.parseFloat(data.getChild("g").getValue()),
                Float.parseFloat(data.getChild("b").getValue()), Float.parseFloat(data.getChild("a").getValue()));
    }

    //Accessors
    public float getR() { return this.r; }
    public float getG() { return this.g; }
    public float getB() { return this.b; }
    public float getA() { return this.a; }
    public float[] getAsArr() { return new float[] { this.r, this.g, this.b, this.a }; }

    //Mutators
    public void setR(float r) { this.r = r; }
    public void setG(float g) { this.g = g; }
    public void setB(float b) { this.b = b; }
    public void setA(float a) { this.a = a; }

    //Node Converter
    public Node toNode() {
        Node node = new Node("color");
        node.addChild(new Node("r", Float.toString(this.r)));
        node.addChild(new Node("g", Float.toString(this.g)));
        node.addChild(new Node("b", Float.toString(this.b)));
        node.addChild(new Node("a", Float.toString(this.a)));
        return node;
    }
}
