package com.jacoboaks.wandermobile.game.gameitem;

import android.opengl.GLES20;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.game.HUD;
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
    public static final int DELETE_CHARACTER_WIDTH = 2;

    //Data
    private List<ButtonItem> buttons;

    /**
     * Constructs this Keyboard with the given character set and shift row
     * @param font the font to use for this Keyboard
     * @param characterSet the character set to be accessible on the Keyboard. Each entry should
     *                     be a new row of the Keyboard.
     * @param buttonTexture the texture to use for the buttons
     * @param selectedButtonTexture the selected texture to use for the buttons
     * @param longButtonTexture the texture to use for the longer buttons (such as shift and space bar)
     * @param selectedLongButtonTexture the texture to use for the selected longer button (such as shift and space bar)
     * @param shiftRow what row the shift key is on (if shift is set to true) - important: set to -1
     *                 if there is no shift key
     * @param x the x coordinate to place this keyboard at (top-left, aspected)
     * @param y the y coordinate to place this keyboard at (top-right, aspected)
     * @param width the width of the Keyboard in aspected coordinates
     * @param height the height of the Keyboard in aspected coordinates
     * @param padding the padding between buttons on this Keyboard
     */
    public Keyboard(Font font, String[] characterSet, Texture buttonTexture, Texture selectedButtonTexture,
                    Texture longButtonTexture, Texture selectedLongButtonTexture, int shiftRow, float x, float y,
                    float width, float height, float padding) {

        //create keyboard background and button list
        super(new Model(Model.getRectangleModelCoords(width, height), Model.STD_SQUARE_TEX_COORDS(),
                Model.STD_SQUARE_DRAW_ORDER(), new Material(Keyboard.backgroundColor)), x ,y);
        this.buttons = new ArrayList<>();

        //calculate button height
        int amountOfRows = shiftRow > -1 ? characterSet.length / 2 : characterSet.length;
        int amountOfVerticalPaddings = amountOfRows + 1;
        float totalVerticalPadding = (float)amountOfVerticalPaddings * padding;
        float buttonHeight = (height - totalVerticalPadding) / (float)amountOfRows;

        //calculate button widths
        List<Float> buttonWidths = new ArrayList<>();
        for (int i = 0; i < amountOfRows; i++) {
            int amountOfCharacters = characterSet[i].length();
            int amountOfHorizontalPaddings = amountOfCharacters + 1 + (i == shiftRow ? 1 : 0);
            int additionCharacterAccount = (characterSet[i].contains(" ") ? Keyboard.SPACE_BAR_CHARACTER_WIDTH - 1 : 0);
            if (shiftRow == i) additionCharacterAccount += SHIFT_CHARACTER_WIDTH;
            float totalHorizontalPadding = (float)amountOfHorizontalPaddings * padding;
            float buttonWidth = (width - totalHorizontalPadding) / (float)(amountOfCharacters + additionCharacterAccount);
            buttonWidths.add(buttonWidth);
        }

        //create text color
        Color textColor = new Color(0.4f, 0.4f, 0.4f, 1.0f);

        //create ButtonItems
        float yp = this.y + height / 2 - (buttonHeight / 2) - padding;
        boolean shiftAccountedFor = false;
        for (int i = 0; i < amountOfRows; i++) {

            //create buttons for row i
            float xp = this.x - (width / 2) + (buttonWidths.get(i) / 2) + padding;
            for (int j = 0; j < characterSet[i].length(); j++) {

                //create null button
                ButtonItem nextButton = null;

                //shift
                if (i == shiftRow && !shiftAccountedFor) {
                    shiftAccountedFor = true;
                    nextButton = new ButtonItem("shift", font, longButtonTexture, selectedLongButtonTexture,
                            textColor, textColor, -1, 0.02f, buttonWidths.get(i) * Keyboard.SHIFT_CHARACTER_WIDTH,
                            buttonHeight);
                    xp += nextButton.getWidth() / 2;
                    xp -= (buttonWidths.get(i) / 2);
                    j--;
                }

                //space
                else if (characterSet[i].charAt(j) == ' ') {
                    nextButton = new ButtonItem("space", font, longButtonTexture, selectedLongButtonTexture,
                            textColor, textColor, characterSet[i].charAt(j), 0.02f, buttonWidths.get(i) * Keyboard.SPACE_BAR_CHARACTER_WIDTH,
                            buttonHeight);
                    xp += nextButton.getWidth() / 2;
                    xp -= (buttonWidths.get(i) / 2);
                }

                //other characters
                else {
                    nextButton = new ButtonItem(Character.toString(characterSet[i].charAt(j)), font, buttonTexture, selectedButtonTexture,
                            textColor, textColor, characterSet[i].charAt(j), 0.02f, buttonWidths.get(i),
                            buttonHeight);
                }

                //set button position and add button
                nextButton.setX(xp);
                nextButton.setY(yp);
                this.buttons.add(nextButton);

                //increment x position
                nextButton.setX(xp);
                nextButton.setY(yp);
                xp += (nextButton.getWidth() / 2) + padding + (buttonWidths.get(i) / 2);
            }

            //increment y position
            yp -= (padding + buttonHeight);
        }
    }

    /**
     * Updates the selection of each button on the keyboard.
     * @param e the MotionEvent to respond to
     * @return -1 if nothing is selected, the action code of the selected button if one is pressed
     */
    public int updateSelections(MotionEvent e) {

        //loop through buttons
        int actionCode = -1;
        for (ButtonItem item : this.buttons) {
            if (actionCode == -1) {
                actionCode = ((ButtonTextItem)item).updateSelection(e);
            } else return actionCode;
        }

        //return the found action code
        return actionCode;
    }

    //Render Method
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
        for (ButtonItem buttonItem : this.buttons) buttonItem.render(shaderProgram);
    }

    @Override
    public void setX(float x) {
        for (ButtonItem item : this.buttons) item.moveX(x - this.x);
        super.setX(x);
    }

    @Override
    public void setY(float y) {
        for (ButtonItem item : this.buttons) item.moveY(y - this.y);
        super.setY(y);
    }
}
