package com.jacoboaks.wandermobile.util;

/**
 * @purpose is to represent a 4-component color
 */
public class Color {

    //Data
    private float r;
    private float g;
    private float b;
    private float a;

    //Default Constructor
    public Color() {
        this(1.0f, 1.0f, 1.0f, 1.0f);
    }

    //Full Constructor
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

    //Delta Mutators
    public void dR(float dR) { this.r += dR; }
    public void dG(float dG) { this.g += dG; }
    public void dB(float dB) { this.b += dB; }
    public void dA(float dA) { this.a += dA; }
}
