package com.jacoboaks.wandermobile.game.gameitem;

import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.graphics.Model;
import com.jacoboaks.wandermobile.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Models text as a GameItem.
 */
public class TextItem extends GameItem {

    //Static Data
    private static final String DEFAULT_TEXT = "N/A";

    //Instance Data
    protected Font font; //the font_default used for the text
    private String text; //the text
    protected float scale; //the width, height, and scale of the text

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
        this.setText(text.equals("") ? TextItem.DEFAULT_TEXT : text);
    }

    /**
     * Constructs this TextItem by copying another one.
     * @param other the TextItem to copy from
     */
    public TextItem(TextItem other) {
        super(other);
        this.font = other.font;
        this.scale = other.scale;
        this.setText(other.text);
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

        //move to middle x
        float[] modelCoordsArr = new float[modelCoords.size()];
        for (int i = 0; i < modelCoords.size(); i++) {
            if (i % 3 == 0) modelCoordsArr[i] = modelCoords.get(i) - x / 2;
            else if (i % 3 == 1) modelCoordsArr[i] = modelCoords.get(i) + (Model.STD_SQUARE_SIZE * this.scale) / 2;
            else modelCoordsArr[i] = 0;
        }

        //convert lists to arrays
        float[] textureCoordsArr = Util.flistToArray(textureCoords);
        int[] drawOrderArr = Util.ilistToArray(drawOrder);

        //create and set model
        this.model = new Model(modelCoordsArr, textureCoordsArr, drawOrderArr, this.model.getMaterial());
    }

    //Mutators
    public void setText(String text) {
        this.text = text;
        this.updateModel(text);
    }

    /**
     * Appends the given text onto the end of this TextItem.
     * @param text the text to append
     */
    public void appendText(String text) {
        this.text = this.text + text;
        this.updateModel(this.text);
    }

    /**
     * Removes the last character of the text of this TextItem.
     */
    public void removeLastChar() {
        if (this.text.length() > 0) {
            this.text = this.text.substring(0, this.text.length() - 1);
            this.updateModel(this.text);
        }
    }

    //Accessor
    public String getText() { return this.text; }

    /**
     * Scales the model and the item width according to a given factor.
     * @param factor the factor to scale the model by
     */
    @Override
    public void scale(float factor) {
        super.scale(factor);
        this.scale *= factor;
    }
}
