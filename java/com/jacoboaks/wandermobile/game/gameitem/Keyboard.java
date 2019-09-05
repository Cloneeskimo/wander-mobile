package com.jacoboaks.wandermobile.game.gameitem;

import android.opengl.GLES20;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.graphics.Model;
import com.jacoboaks.wandermobile.graphics.ShaderProgram;
import com.jacoboaks.wandermobile.graphics.Texture;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Coord;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a configurable Keyboard for user input.
 */
public class Keyboard extends GameItem {

    //Standard Character Sets
    public static final String[] LETTER_ONLY_CHARACTER_SET = {
            "qwertyuiop", "asdfghjkl", "zxcvbnm", " ", //non-shift
            "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM", " " //shift
    };

    //Static Keyboard Data
    private static Color backgroundColor = new Color(0.4f, 0.4f, 0.4f, 0.8f);
    private static final int DELETE_WIDTH = 2;
    private static final int SHIFT_WIDTH = 2;
    private static final int SPACE_BAR_WIDTH = 3;
    private static final char NON_CHARACTER_SHIFT_VALUE = (char)0;

    //Action Codes
    public static final int DELETE_ACTION_CODE = 2;
    private static final int SHIFT_ACTION_CODE = 1;

    //Instance Data
    private List<KeyboardButton> buttons;
    private boolean shifted;

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
                                  Texture longButtonTexture, Texture selectedLongButtonTexture, int shiftRow, int deleteRow,
                                  float x, float y, float width, float height, float padding) {

        //create keyboard background and button list
        super(new Model(Model.getRectangleModelCoords(width, height), Model.STD_SQUARE_TEX_COORDS(),
                Model.STD_SQUARE_DRAW_ORDER(), new Material(Keyboard.backgroundColor)), x ,y);
        this.buttons = new ArrayList<>();
        this.shifted = false;

        //calculate button height
        int amountOfRows = shiftRow > -1 ? characterSet.length / 2 : characterSet.length;
        int amountOfVerticalPaddings = amountOfRows + 1;
        float totalVerticalPadding = (float)amountOfVerticalPaddings * padding;
        float buttonHeight = (height - totalVerticalPadding) / (float)amountOfRows;

        //calculate button widths
        List<Float> buttonWidths = new ArrayList<>();
        int shiftAdd = (shiftRow > -1 ? amountOfRows : 0);
        for (int i = 0; i < amountOfRows; i++) {
            int amountOfCharacters = characterSet[i].length();
            int amountOfHorizontalPaddings = amountOfCharacters + 1 + (i == shiftRow ? 1 : 0) + (i == deleteRow ? 1 : 0);
            int additionCharacterAccount = (characterSet[i].contains(" ") ? Keyboard.SPACE_BAR_WIDTH - 1 : 0);
            if (shiftRow == i) additionCharacterAccount += SHIFT_WIDTH;
            if (deleteRow == i) additionCharacterAccount += DELETE_WIDTH;
            float totalHorizontalPadding = (float)amountOfHorizontalPaddings * padding;
            float buttonWidth = (width - totalHorizontalPadding) / (float)(amountOfCharacters + additionCharacterAccount);
            buttonWidths.add(buttonWidth);
        }

        //create text color
        Color textColor = new Color(0.4f, 0.4f, 0.4f, 1.0f);

        //create ButtonItems
        float yp = this.y + height / 2 - (buttonHeight / 2) - padding;
        boolean shiftAccountedFor = false, deleteAccountedFor = false;
        for (int i = 0; i < amountOfRows; i++) {

            //create buttons for row i
            float xp = this.x - (width / 2) + (buttonWidths.get(i) / 2) + padding;
            for (int j = 0; j < characterSet[i].length(); j++) {

                //create button reference
                ButtonItem nextButton = null;

                //create delete button if appropriate
                if (i == deleteRow && !deleteAccountedFor) {
                    deleteAccountedFor = true;
                    ButtonItem deleteButton = new ButtonItem("del", font, longButtonTexture, selectedLongButtonTexture,
                            textColor, textColor, Keyboard.DELETE_ACTION_CODE, 0.02f, buttonWidths.get(i) * Keyboard.DELETE_WIDTH,
                            buttonHeight);
                    deleteButton.setX(this.x + (width / 2) - padding - (deleteButton.getWidth() / 2));
                    deleteButton.setY(yp);
                    this.buttons.add(new KeyboardButton(deleteButton, NON_CHARACTER_SHIFT_VALUE, NON_CHARACTER_SHIFT_VALUE));
                }

                //create shift button if appropriate
                boolean shift = false;
                if (i == shiftRow && !shiftAccountedFor) {
                    shiftAccountedFor = true;
                    nextButton = new ButtonItem("shift", font, longButtonTexture, selectedLongButtonTexture,
                            textColor, textColor, Keyboard.SHIFT_ACTION_CODE, 0.02f, buttonWidths.get(i) * Keyboard.SHIFT_WIDTH,
                            buttonHeight);
                    xp += nextButton.getWidth() / 2;
                    xp -= (buttonWidths.get(i) / 2);
                    shift = true;
                    j--;
                }

                //create space button if appropriate
                else if (characterSet[i].charAt(j) == ' ') {
                    nextButton = new ButtonItem(" ", font, longButtonTexture, selectedLongButtonTexture,
                            textColor, textColor, characterSet[i].charAt(j), 0.02f, buttonWidths.get(i) * Keyboard.SPACE_BAR_WIDTH,
                            buttonHeight);
                    xp += nextButton.getWidth() / 2;
                    xp -= (buttonWidths.get(i) / 2);
                }

                //create any other type of button
                else {
                    nextButton = new ButtonItem(Character.toString(characterSet[i].charAt(j)), font, buttonTexture, selectedButtonTexture,
                            textColor, textColor, characterSet[i].charAt(j), 0.02f, buttonWidths.get(i),
                            buttonHeight);
                }

                //set button position and add button
                nextButton.setX(xp);
                nextButton.setY(yp);
                this.buttons.add(new KeyboardButton(nextButton, shift ? NON_CHARACTER_SHIFT_VALUE : characterSet[i].charAt(j),
                        shift ? NON_CHARACTER_SHIFT_VALUE : characterSet[i + shiftAdd].charAt(j)));

                //increment x position for next button
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
     * @param touchPos the position of the touch in aspected space.
     * @return -1 if nothing is selected, the action code of the selected button if one is pressed
     */
    public int updateSelections(MotionEvent e, Coord touchPos) {

        //loop through buttons
        int actionCode = -1;
        for (KeyboardButton item : this.buttons) {
            if (actionCode == -1) { //did not press current button in loop
                actionCode = item.button.updateSelection(e, touchPos);
            } else if (actionCode == Keyboard.SHIFT_ACTION_CODE) { //pressed shift
                this.toggleShift();
                return -1;
            } else return actionCode; //found pressed button
        }

        //return the determined action code
        return actionCode;
    }

    /**
     * Toggles the shift variation of this Keyboard.
     */
    private void toggleShift() {
        this.shifted = !this.shifted;
        for (KeyboardButton button : this.buttons) button.toggleShift(this.shifted);
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

        //draw model and each button
        this.model.render(shaderProgram);
        for (KeyboardButton item : this.buttons) item.button.render(shaderProgram);
    }

    /**
     * Sets the x value of this Keyboard in aspected/world coordinates.
     * @param x the x value to set this Keyboard to
     */
    @Override
    public void setX(float x) {
        for (KeyboardButton item : this.buttons) item.button.moveX(x - this.x);
        super.setX(x);
    }

    /**
     * Sets the y value of this Keyboard in aspected/world coordinates.
     * @param y the y value to set this Keyboard to
     */
    @Override
    public void setY(float y) {
        for (KeyboardButton item : this.buttons) item.button.moveY(y - this.y);
        super.setY(y);
    }

    /**
     * Represents a button on a Keyboard. Contains a ButtonItem as well as shift data for the
     * button.
     */
    private static class KeyboardButton {

        //Data
        ButtonItem button;
        char unshifted, shifted;

        /**
         * Constructs this KeyboardButton with the given information.
         * @param button the ButtonItem to use for this KeyboardButton
         * @param unshifted the character that appears on this KeyboardButton when not shifted
         * @param shifted the character that appears on this KeyboardButton when shifted
         */
        public KeyboardButton(ButtonItem button, char unshifted, char shifted) {
            this.button = button;
            this.unshifted = unshifted;
            this.shifted = shifted;
        }

        /**
         * Toggles shift for this KeyboardButton.
         * @param shift whether shift is pressed or not
         */
        public void toggleShift(boolean shift) {
            char toChangeTo = shift ? this.shifted : this.unshifted;
            if (toChangeTo != NON_CHARACTER_SHIFT_VALUE) {
                this.button.setText(Character.toString(toChangeTo));
                this.button.setActionCode(toChangeTo);
            }
        }
    }
}
