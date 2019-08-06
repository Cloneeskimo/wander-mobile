package com.jacoboaks.wandermobile.game.gameitem;

import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.graphics.Model;
import com.jacoboaks.wandermobile.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Models text as a GameItem. The model position is as follows: the coordinates
 * represent the top-left position of the text as opposed to a normal GameItem where they would
 * normally represent the middle of the item.
 */
public class TextItem extends GameItem {

    //Data
    protected Font font; //the font_default used for the text
    protected String text; //the text
    protected float width, height, scale; //the width, height, and scale of the text

    /**
     * Constructs this TextItem.
     * @param font the font_default to be used for the text
     * @param text the text to be written
     * @param material the material to use for the text model
     * @param x the x position
     * @param y the y position
     */
    public TextItem(Font font, String text, Material material, float x, float y) {
        super(new Model(new float[] {}, new float[] {}, new int[] {}, material), x, y);
        this.font = font;
        this.scale = 1.0f;
        this.setText(text);
    }

    /**
     * Constructs this TextItem by copying another one.
     * @param other the TextItem to copy from
     */
    public TextItem(TextItem other) {
        super(other);
        this.font = other.font;
        this.text = other.text;
        this.width = other.width;
        this.height = other.height;
        this.scale = other.scale;
    }

    /**
     * Creates the appropriate model to display the given text using the given font_default.
     * @param text the text to display
     */
    private void updateModel(String text) {

        //create lists
        List<Float> modelCoords = new ArrayList<>();
        List<Float> textureCoords = new ArrayList<>();
        List<Integer> drawOrder = new ArrayList<>();

        //get character width and height
        float characterWidth = font.getCharacterWidth();

        //add each letter
        float x = 0;
        for (int i = 0; i < text.length(); i++) {

            //get character and texture coordinates
            char character = text.charAt(i);
            float[] texCoords = font.getCharacterTextureCoordinates(character, true);

            //top left vertex
            modelCoords.add(x); //x
            modelCoords.add(-Model.STD_SQUARE_SIZE * this.scale); //y
            modelCoords.add(0f); //z
            textureCoords.add(texCoords[0]); //x
            textureCoords.add(texCoords[1]); //y

            //bottom left
            modelCoords.add(x); //x
            modelCoords.add(0f); //y
            modelCoords.add(0f); //z
            textureCoords.add(texCoords[2]); //x
            textureCoords.add(texCoords[3]); //y

            //figure out model width for character
            int cutoff = font.getCharacterCutoff(character);
            float cwidth = characterWidth - (float)(cutoff * 2);
            float modelcwidth = cwidth / characterWidth * Model.STD_SQUARE_SIZE;
            x += (modelcwidth * this.scale);

            //top right
            modelCoords.add(x); //x
            modelCoords.add(-Model.STD_SQUARE_SIZE * this.scale); //y
            modelCoords.add(0f); //z
            textureCoords.add(texCoords[4]); //x
            textureCoords.add(texCoords[5]); //y

            //bottom right
            modelCoords.add(x); //x
            modelCoords.add(0f); //y
            modelCoords.add(0f); //z
            textureCoords.add(texCoords[6]); //x
            textureCoords.add(texCoords[7]); //y

            //draw order
            drawOrder.add(i * 4);
            drawOrder.add(i * 4 + 1);
            drawOrder.add(i * 4 + 2);
            drawOrder.add(i * 4 + 2);
            drawOrder.add(i * 4 + 1);
            drawOrder.add(i * 4 + 3);
        }

        //update width and height
        this.width = x;
        this.height = Model.STD_SQUARE_SIZE * this.scale;

        //convert lists to arrays
        float[] modelCoordsArr = Util.flistToArray(modelCoords);
        float[] textureCoordsArr = Util.flistToArray(textureCoords);
        int[] drawOrderArr = Util.ilistToArray(drawOrder);

        //create and set model
        this.model = new Model(modelCoordsArr, textureCoordsArr, drawOrderArr, this.model.getMaterial());
    }

    //Accessors
    public float getWidth() { return this.width; }
    public float getHeight() { return this.height; }

    //Mutators
    public void setText(String text) {
        this.text = text;
        this.updateModel(text);
    }

    /**
     * Scales the model and the item width according to a given factor.
     * @param factor the factor to scale the model by
     */
    @Override
    public void scale(float factor) {
        super.scale(factor);
        this.scale = factor;
        this.width *= factor;
        this.height *= factor;
    }
}
