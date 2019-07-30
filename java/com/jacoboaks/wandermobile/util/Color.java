package com.jacoboaks.wandermobile.util;

/**
 * @purpose is to represent a 4-component color (rgba)
 */
public class Color {

    //Data
    private float r;
    private float g;
    private float b;
    private float a;

    /**
     * @purpose is to construct this color as the default color: white
     */
    public Color() {
        this(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * @purpose is to construct this color with the given rgba values
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
}
