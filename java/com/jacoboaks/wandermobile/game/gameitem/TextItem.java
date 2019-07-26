package com.jacoboaks.wandermobile.game.gameitem;

import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.graphics.Model;
import com.jacoboaks.wandermobile.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @purpose is to model text as a game item. the model position is as follows: the x coordinate
 * //represents the leftmost edge of the text; the y coordinate represents the middle line of
 * the text.
 */
public class TextItem extends GameItem {

    //Data
    Font font;
    String text;
    float width, height, scale;

    //Constructor
    public TextItem(Font font, String text, Material material, float x, float y) {
        super(new Model(new float[] {}, new float[] {}, new int[] {}, material), x, y);
        this.font = font;
        this.scale = 1.0f;
        this.setText(text);
    }

    /**
     * @purpose is to create the appropriate model to display the given text using the given font
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
            modelCoords.add(-Model.STD_SQUARE_SIZE * this.scale / 2); //y
            modelCoords.add(0f); //z
            textureCoords.add(texCoords[0]); //x
            textureCoords.add(texCoords[1]); //y

            //bottom left
            modelCoords.add(x); //x
            modelCoords.add(Model.STD_SQUARE_SIZE * this.scale / 2); //y
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
            modelCoords.add(-Model.STD_SQUARE_SIZE * this.scale / 2); //y
            modelCoords.add(0f); //z
            textureCoords.add(texCoords[4]); //x
            textureCoords.add(texCoords[5]); //y

            //bottom right
            modelCoords.add(x); //x
            modelCoords.add(Model.STD_SQUARE_SIZE * this.scale / 2); //y
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
        this.updateModel(text);
        this.text = text;
    }

    /**
     * @purpose is to scale the model and the item width according to a given factor
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
