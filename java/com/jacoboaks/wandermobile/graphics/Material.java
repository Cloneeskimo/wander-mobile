package com.jacoboaks.wandermobile.graphics;

import com.jacoboaks.wandermobile.util.Color;

/**
 * @purpose is to represent a type of material to be used for a model. Can either be a color, a
 * texture, or a texture whose color is overriden by the provided colord
 */
public class Material {

    //Data
    Texture texture;
    Color color;
    Boolean colorOverride;

    /**
     * @purpose is to construct this Material
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
     * @purpose is to construct this Material with color overriding disabled
     * @param texture the texture to use for this material
     */
    public Material(Texture texture) {
        this(texture, new Color(), false);
    }

    /**
     * @purpose is to construct this Material using a solid color
     * @param color the color to use for this material
     */
    public Material(Color color) {
        this(null, color, false);
    }

    //Accessors
    public boolean isTextured() { return this.texture != null; }
    public boolean isColorOverrided() { return this.colorOverride; }
    public Texture getTexture() { return this.texture; }
    public Color getColor() { return this.color; }
}
