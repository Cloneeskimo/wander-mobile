package com.jacoboaks.wandermobile.game.gameitem;

import android.opengl.GLES20;

import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.graphics.Model;
import com.jacoboaks.wandermobile.graphics.ShaderProgram;
import com.jacoboaks.wandermobile.graphics.Texture;
import com.jacoboaks.wandermobile.util.Color;

import java.util.ArrayList;
import java.util.List;

public class Keyboard extends GameItem {

    //Static Data
    public static final String[] LETTER_ONLY_CHARACTER_SET = {
            "qwertyuiop", "asdfghjkl", "zxcvbnm", " ", //non-shift
            "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM", " ", //shift
    };
    public static final String[] STD_CHARACTER_SET = {
            "1234567890", "qwertyuiop", "asdfghjkl", "zxcvbnm`-=", " ,./", //non-shift
            "!@#$%^&*()", "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM~_+", " <>?" //shift
    };
    public static Color backgroundColor = new Color(0.4f, 0.4f, 0.4f, 0.8f);
    public static final int SPACE_BAR_CHARACTER_WIDTH = 3;
    public static final int SHIFT_CHARACTER_WIDTH = 2;

    //Data
    private boolean shift;
    private int shiftRow;
    private float width, height;
    private float padding;
    private String[] characterSet;
    private Texture buttonTexture;
    private Texture spaceButtonTexture;
    private List<Float> buttonWidths;
    private List<GameItem> buttons;
    private List<ButtonTextItem> buttonTextItems;
    private Font font;

    /**
     * Constructs this Keyboard with the given character set and shift row
     * @param font the font to use for this Keyboard
     * @param characterSet the character set to be accessible on the Keyboard. Each entry should
     *                     be a new row of the Keyboard.
     * @param button the Texture to use for the normal buttons
     * @param space the Texture to use for the space bar
     * @param shift whether a alternate set of character is available through a shift key
     * @param shiftRow what row the shift key is on (if shift is set to true) - important: set to -1
     *                 if there is no shift key
     * @param x the x coordinate to place this keyboard at (top-left, aspected)
     * @param y the y coordinate to place this keyboard at (top-right, aspected)
     * @param width the width of the Keyboard in aspected coordinates
     * @param height the height of the Keyboard in aspected coordinates
     * @param padding the padding between buttons on this Keyboard
     */
    public Keyboard(Font font, String[] characterSet, Texture button, Texture space, boolean shift, int shiftRow, float x, float y, float width, float height, float padding) {
        super(new Model(new float[] { 0f, 0f, 0f, 0f, height, 0f, width, 0f, 0f, width, height, 0f },
                Model.STD_SQUARE_TEX_COORDS(), Model.STD_SQUARE_DRAW_ORDER(), new Material(Keyboard.backgroundColor)), x ,y);

        //set data
        this.characterSet = characterSet;
        this.buttonTexture = button;
        this.spaceButtonTexture = space;
        this.shift = shift;
        this.shiftRow = shiftRow;
        this.width = width;
        this.height = height;
        this.padding = padding;
        this.font = font;

        //calculate button height
        int amountOfRows = shift ? characterSet.length / 2 : characterSet.length;
        int amountOfVerticalPaddings = amountOfRows + 1;
        float totalVerticalPadding = (float)amountOfVerticalPaddings * padding;
        float buttonHeight = (height - totalVerticalPadding) / (float)amountOfRows;

        //calculate button widths
        this.buttonWidths = new ArrayList<>();
        for (int i = 0; i < amountOfRows; i++) {
            int amountOfCharacters = characterSet[i].length();
            int amountOfHorizontalPaddings = amountOfCharacters + 1 + (i == shiftRow ? 1 : 0);
            int additionCharacterAccount = (characterSet[i].contains(" ") ? Keyboard.SPACE_BAR_CHARACTER_WIDTH - 1 : 0);
            if (shiftRow == i) additionCharacterAccount += SHIFT_CHARACTER_WIDTH;
            float totalHorizontalPadding = (float)amountOfHorizontalPaddings * padding;
            float buttonWidth = (width - totalHorizontalPadding) / (float)(amountOfCharacters + additionCharacterAccount);
            this.buttonWidths.add(buttonWidth);
        }

        //create button models for each row
        List<Model> buttonModels = new ArrayList<>();
        Model spaceModel = null, shiftModel = null;
        for (int i = 0; i < amountOfRows; i++) {
            buttonModels.add(new Model(new float[] { 0f, 0f, 0f, 0f, buttonHeight, 0f,
                    buttonWidths.get(i), 0f, 0f, buttonWidths.get(i), buttonHeight, 0f },
                    Model.STD_SQUARE_TEX_COORDS(), Model.STD_SQUARE_DRAW_ORDER(), new Material(button)));
            if (i == shiftRow) {
                shiftModel = new Model(new float[] { 0f, 0f, 0f, 0f, buttonHeight, 0f,
                        Keyboard.SHIFT_CHARACTER_WIDTH * buttonWidths.get(i), 0f, 0f, Keyboard.SHIFT_CHARACTER_WIDTH *
                        buttonWidths.get(i), buttonHeight, 0f }, Model.STD_SQUARE_TEX_COORDS(),
                        Model.STD_SQUARE_DRAW_ORDER(), new Material(space));
            }
            if (characterSet[i].contains(" ")) {
                spaceModel = new Model(new float[] { 0f, 0f, 0f, 0f, buttonHeight, 0f,
                        Keyboard.SPACE_BAR_CHARACTER_WIDTH * buttonWidths.get(i), 0f, 0f, Keyboard.SPACE_BAR_CHARACTER_WIDTH *
                        buttonWidths.get(i), buttonHeight, 0f }, Model.STD_SQUARE_TEX_COORDS(),
                        Model.STD_SQUARE_DRAW_ORDER(), new Material(space));
            }
        }

        //create GameItems
        float yp = -padding - buttonHeight + height / 2;
        boolean shiftAccountedFor = false;
        this.buttons = new ArrayList<>();
        this.buttonTextItems = new ArrayList<>();
        for (int i = 0; i < amountOfRows; i++) {

            float xp = padding - (width / 2);
            for (int j = 0; j < characterSet[i].length(); j++) {

                GameItem nextButton = null;
                if (i == shiftRow && !shiftAccountedFor) {
                    shiftAccountedFor = true;
                    nextButton = new GameItem(shiftModel, xp, yp);
                    xp += nextButton.getWidth() + padding;
                    this.buttons.add(nextButton);
                }

                if (characterSet[i].charAt(j) == ' ') {
                    nextButton = new GameItem(spaceModel, xp, yp);
                } else {
                    nextButton = new GameItem(buttonModels.get(i), xp, yp);
                }
                this.buttons.add(nextButton);

                ButtonTextItem bti = new ButtonTextItem(font, Character.toString(characterSet[i].charAt(j)),
                        new Color(0.0f, 0.0f, 0.0f, 1.0f), new Color(0.2f, 0.2f, 0.2f, 1.0f),
                        characterSet[i].charAt(j));
                bti.scale(0.2f);
                bti.setX(xp + nextButton.getWidth() / 2);
                bti.setY(yp + nextButton.getHeight() / 2);
                this.buttonTextItems.add(bti);


                xp += nextButton.getWidth() + padding;
            }

            yp -= (padding + buttonHeight);
        }
    }

    @Override
    public void render(ShaderProgram shaderProgram) {

        //return if invisible
        if (!this.visible) return;

        //set x and y uniforms
        GLES20.glUniform1fv(shaderProgram.getUniformIndex("x"), 1,
                new float[] { x }, 0);
        GLES20.glUniform1fv(shaderProgram.getUniformIndex("y"), 1,
                new float[] { y }, 0);

        //draw model
        this.model.render(shaderProgram);

        //draw each button
        for (GameItem button : this.buttons) button.render(shaderProgram);
        for (ButtonTextItem buttonTextItem : this.buttonTextItems) buttonTextItem.render(shaderProgram);
    }

}
