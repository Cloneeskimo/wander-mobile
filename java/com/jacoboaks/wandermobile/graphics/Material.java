package com.jacoboaks.wandermobile.graphics;

import com.jacoboaks.wandermobile.util.Color;

/**
 * Represents a type of material to be used for a Model. Can either be a color, a
 * texture, or a texture whose color is overriden by the provided color.
 */
public class Material {

    //Data
    private Texture texture;
    private Color color;
    private boolean colorOverride;

    /**
     * Constructs this Material with a Texture, a Color, and a color override value.
     * @param texture the texture to use for this material
     * @param color the color to use for this material
     * @param colorOverride whether or not to override this material's texture with the given color
     */
    public Material(Texture texture, Color color, boolean colorOverride) {
        this.texture = texture;
        this.color = color;
        this.colorOverride = colorOverride;
    }

    /**
     * Constructs this Material with a Texture and without color overriding.
     * @param texture the texture to use for this material
     */
    public Material(Texture texture) {
        this(texture, new Color(), false);
    }

    /**
     * Constructs this Material with a Color.
     * @param color the color to use for this material
     */
    public Material(Color color) {
        this(null, color, false);
    }

    //Mutator
    public void setColor(Color color) { this.color = color; }

    //Accessors
    public boolean isTextured() { return this.texture != null; }
    public boolean isColorOverrided() { return this.colorOverride; }
    public Texture getTexture() { return this.texture; }
    public Color getColor() { return this.color; }
}
