package com.jacoboaks.wandermobile.graphics;

import com.jacoboaks.wandermobile.util.Color;

public class Material {

    //Data
    Texture texture;
    Color color;
    Boolean colorOverride;

    //Full Constructor
    public Material(Texture texture, Color color, boolean colorOverride) {
        this.texture = texture;
        this.color = color;
        this.colorOverride = colorOverride;
    }

    //Texture Constructor
    public Material(Texture texture) {
        this(texture, new Color(), false);
    }

    //Color Constructor
    public Material(Color color) {
        this(null, color, false);
    }

    //Accessors
    public boolean isTextured() { return this.texture != null; }
    public Texture getTexture() { return this.texture; }
    public Color getColor() { return this.color; }
    public boolean isColorOverrided() { return this.colorOverride; }
}
